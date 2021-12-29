package com.softsquare.midimapper.service;

import android.content.Context;
import android.content.pm.PackageManager;
import android.media.midi.MidiDeviceInfo;
import android.media.midi.MidiManager;
import android.media.midi.MidiOutputPort;
import android.media.midi.MidiReceiver;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.softsquare.midimapper.R;
import com.softsquare.midimapper.communication.AppActionPerformer;
import com.softsquare.midimapper.model.AppState;
import com.softsquare.midimapper.model.Device;

public class MIDIDeviceConnector {
    private final AppActionPerformer actionPerformer;
    private final AppState appState;
    private MidiManager midiManager;

    public MIDIDeviceConnector(Context context, AppState appState) {
        this.appState = appState;
        actionPerformer = AppActionPerformer.getInstance(context);
        initializeMIDIManager(context);
    }

    private void initializeMIDIManager(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_MIDI)) {
            midiManager = (MidiManager)context.getSystemService(Context.MIDI_SERVICE);
            MidiDeviceInfo[] connectedDevices = midiManager.getDevices();

            if (connectedDevices.length == 0) {
                actionPerformer.showError(context.getString(R.string.device_not_detected));
                actionPerformer.changeCurrentDevice(null, null);
            }

            for (MidiDeviceInfo deviceInfo : connectedDevices)
                connectDevice(context, deviceInfo);

            midiManager.registerDeviceCallback(new MidiManager.DeviceCallback() {
                public void onDeviceAdded(MidiDeviceInfo deviceInfo) {
                    connectDevice(context, deviceInfo);
                }

                public void onDeviceRemoved(MidiDeviceInfo deviceInfo) {
                    Device device = appState.getDevice(getSerialNumber(deviceInfo));
                    actionPerformer.disconnectDevice(device);
                    if (device.getSerialNumber().equals(appState.getCurrentDeviceSerialNumber()))
                        actionPerformer.changeCurrentDevice(null,null);
                }
            }, new Handler(Looper.getMainLooper()));
        } else
            actionPerformer.showError(context.getString(R.string.feature_not_supported));
    }

    private void connectDevice(Context context, MidiDeviceInfo deviceInfo) {
        String serialNumber = getSerialNumber(deviceInfo);
        Device device = appState.getDevice(serialNumber);
        if (device == null)
            device = actionPerformer.createDevice(deviceInfo);

        if (device.isConnected())
            return;

        Device finalDevice = device;

        int numOutputs = deviceInfo.getOutputPortCount();
        if (numOutputs > 0) {
            midiManager.openDevice(deviceInfo, midiDevice -> {
                if (midiDevice != null) {
                    MidiOutputPort outputPort = midiDevice.openOutputPort(0);
                    outputPort.connect(new MidiReceiver() {
                        @Override
                        public void onSend(byte[] msg, int offset, int count, long timestamp) {
                            boolean pressed = ((msg[1] >> 4) & 1) == 1;
                            if (pressed)
                                actionPerformer.pressKey(finalDevice, msg[2]);
                        }
                    });
                    if (finalDevice.getPresets().size() == 0)
                        actionPerformer.createPreset(finalDevice);

                    actionPerformer.connectDevice(finalDevice);
                } else
                    actionPerformer.showError(context.getString(R.string.device_connection_failure));
            }, new Handler(Looper.getMainLooper()));
        }
    }

    private String getSerialNumber(MidiDeviceInfo deviceInfo) {
        Bundle properties = deviceInfo.getProperties();
        return properties.getString(MidiDeviceInfo.PROPERTY_SERIAL_NUMBER);
    }
}
