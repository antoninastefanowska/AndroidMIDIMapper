package com.softsquare.midimapper;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Path;
import android.media.midi.MidiDeviceInfo;
import android.media.midi.MidiManager;
import android.os.Bundle;


import android.os.Debug;
import android.os.Handler;
import android.os.Looper;
import android.util.Pair;
import android.view.accessibility.AccessibilityEvent;

import com.softsquare.midimapper.database.repositories.BindingsPresetRepository;
import com.softsquare.midimapper.database.repositories.KeyBindingRepository;
import com.softsquare.midimapper.database.repositories.MIDIControllerDeviceRepository;
import com.softsquare.midimapper.database.repositories.SettingsRepository;
import com.softsquare.midimapper.gui.ConfigurationGUI;
import com.softsquare.midimapper.gui.SettingsActivity;
import com.softsquare.midimapper.model.KeyBinding;
import com.softsquare.midimapper.model.MIDIControllerDevice;
import com.softsquare.midimapper.model.Settings;

public class MIDIMapperAccessibilityService extends AccessibilityService {
    private ConfigurationGUI configurationGUI;
    private MidiManager midiManager;
    private Settings settings;
    private static MIDIMapperAccessibilityService instance;

    @Override
    protected void onServiceConnected() {
        instance = this;

        KeyBindingRepository.createInstance(this);
        BindingsPresetRepository.createInstance(this);
        MIDIControllerDeviceRepository.createInstance(this);
        SettingsRepository.createInstance(this);

        SettingsRepository.getInstance().getSettingsAsync(settings -> {
            this.settings = settings;
            new Handler(getMainLooper()).post(() -> {
                configurationGUI = new ConfigurationGUI(this);
                initializeMIDIManager();
            });
        });
    }

    @Override
    public boolean onUnbind(Intent intent) {
        instance = null;
        return super.onUnbind(intent);
    }

    public static MIDIMapperAccessibilityService getInstance() {
        return instance;
    }

    private void initializeMIDIManager() {
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_MIDI)) {
            midiManager = (MidiManager)getSystemService(Context.MIDI_SERVICE);
            MidiDeviceInfo[] connectedDevices = midiManager.getDevices();

            if (connectedDevices.length == 0) {
                configurationGUI.showError(getString(R.string.device_not_detected));
                settings.setCurrentDeviceSerialNumber(null);
            }

            for (MidiDeviceInfo deviceInfo : connectedDevices)
                connectDevice(deviceInfo);

            midiManager.registerDeviceCallback(new MidiManager.DeviceCallback() {
                public void onDeviceAdded(MidiDeviceInfo deviceInfo) {
                    connectDevice(deviceInfo);
                }

                public void onDeviceRemoved(MidiDeviceInfo deviceInfo) {
                    MIDIControllerDevice device = settings.getDevice(getSerialNumber(deviceInfo));
                    device.disconnect();
                    if (device.getSerialNumber().equals(settings.getCurrentDeviceSerialNumber())) {
                        settings.setCurrentDeviceSerialNumber(null);
                        configurationGUI.updateViews();
                        SettingsActivity activity = SettingsActivity.getInstance();
                        if (activity != null)
                            activity.deviceConnectionChanged(device);
                    }
                }
            }, new Handler(Looper.getMainLooper()));
        } else
            configurationGUI.showError(getString(R.string.feature_not_supported));
    }

    private void connectDevice(final MidiDeviceInfo deviceInfo) {
        MIDIControllerDevice device = settings.getDevice(getSerialNumber(deviceInfo));
        if (device == null) {
            device = new MIDIControllerDevice(deviceInfo);
            settings.addDevice(device);
        }
        if (device.isConnected())
            return;

        device.connect(midiManager, deviceInfo, new MIDIControllerDevice.OnInitializedListener() {
            @Override
            public void onSuccess(MIDIControllerDevice device) {
                if (settings.getCurrentDeviceSerialNumber() == null) {
                    settings.setCurrentDeviceSerialNumber(device.getSerialNumber());
                    SettingsActivity activity = SettingsActivity.getInstance();
                    if (activity != null)
                        activity.deviceConnectionChanged(device);
                }
                configurationGUI.updateViews();

                device.setOnKeyPressedListener((keyBinding, keyCode) -> {
                    if ((!configurationGUI.isOpen() || settings.isHidden()) && keyBinding != null)
                        dispatchTapAt(keyBinding.getX(), keyBinding.getY());
                    else if (configurationGUI.isListeningForKey() && keyBinding == null) {
                        KeyBinding newBinding = settings.getCurrentDevice().getCurrentPreset().createNewBinding(
                                keyCode,
                                configurationGUI.getInitialX(),
                                configurationGUI.getInitialY());
                        new Handler(Looper.getMainLooper()).post(() -> configurationGUI.createNewMarker(newBinding));
                    }
                    if (configurationGUI.isOpen())
                        new Handler(Looper.getMainLooper()).post(() -> configurationGUI.makeRipple(keyCode));
                });
            }

            @Override
            public void onFailure(MIDIControllerDevice device) {
                configurationGUI.showError(getString(R.string.device_connection_failure));
            }
        });
    }

    public Settings getSettings() {
        return settings;
    }

    private String getSerialNumber(MidiDeviceInfo deviceInfo) {
        Bundle properties = deviceInfo.getProperties();
        return properties.getString(MidiDeviceInfo.PROPERTY_SERIAL_NUMBER);
    }

    private void dispatchTapAt(float x, float y) {
        Pair<Float, Float> layoutPosition = configurationGUI.getLayoutPosition();
        x += layoutPosition.first;
        y += layoutPosition.second;

        Path tapPath = new Path();
        tapPath.moveTo(x, y);
        GestureDescription.Builder gestureBuilder = new GestureDescription.Builder();
        GestureDescription.StrokeDescription tapStroke = new GestureDescription.StrokeDescription(tapPath, 0, 1);
        gestureBuilder.addStroke(tapStroke);
        dispatchGesture(gestureBuilder.build(), null, null);
    }

    public void hideMenuWidget() {
        configurationGUI.hideMenuWidget();
    }

    public void showMenuWidget() {
        configurationGUI.showMenuWidget();
    }

    public void updateConfigurationView() {
        configurationGUI.updateViews();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) { }

    @Override
    public void onInterrupt() { }
}
