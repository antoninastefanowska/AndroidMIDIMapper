package com.softsquare.midimapper.model;

import android.media.midi.MidiDeviceInfo;
import android.media.midi.MidiManager;
import android.media.midi.MidiOutputPort;
import android.media.midi.MidiReceiver;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.softsquare.midimapper.database.DatabaseEntity;
import com.softsquare.midimapper.database.entities.BindingsPresetRelation;
import com.softsquare.midimapper.database.entities.MIDIControllerDeviceRelation;
import com.softsquare.midimapper.database.repositories.BindingsPresetRepository;
import com.softsquare.midimapper.database.repositories.MIDIControllerDeviceRepository;

import java.util.HashMap;
import java.util.Map;

public class MIDIControllerDevice extends DatabaseEntity {
    private final String name;
    private final String manufacturer;
    private final String serialNumber;

    private String currentPresetName;
    private final Map<String, BindingsPreset> presets = new HashMap<>();

    private boolean connected = false;
    private OnKeyPressedListener onKeyPressedListener;

    public MIDIControllerDevice(MidiDeviceInfo deviceInfo) {
        Bundle properties = deviceInfo.getProperties();
        name = properties.getString(MidiDeviceInfo.PROPERTY_NAME);
        manufacturer = properties.getString(MidiDeviceInfo.PROPERTY_MANUFACTURER);
        serialNumber = properties.getString(MidiDeviceInfo.PROPERTY_SERIAL_NUMBER);
    }

    public MIDIControllerDevice(MIDIControllerDeviceRelation databaseEntity) {
        setId(databaseEntity.device.id);
        setParentId(databaseEntity.device.settingsId);
        name = databaseEntity.device.name;
        manufacturer = databaseEntity.device.manufacturer;
        serialNumber = databaseEntity.device.serialNumber;
        currentPresetName = databaseEntity.device.currentPresetName;
        for (BindingsPresetRelation presetEntity : databaseEntity.presets)
            presets.put(presetEntity.bindingsPreset.name, new BindingsPreset(presetEntity));
    }

    public void connect(MidiManager midiManager, MidiDeviceInfo deviceInfo, OnInitializedListener onInitializedListener) {
        int numOutputs = deviceInfo.getOutputPortCount();
        if (numOutputs > 0) {
            midiManager.openDevice(deviceInfo, device -> {
                if (device != null) {
                    MidiOutputPort outputPort = device.openOutputPort(0);
                    outputPort.connect(new MidiReceiver() {
                        @Override
                        public void onSend(byte[] msg, int offset, int count, long timestamp) {
                            boolean pressed = ((msg[1] >> 4) & 1) == 1;
                            if (pressed)
                                handleKey(msg[2]);
                        }
                    });
                    if (presets.size() == 0)
                        createNewPreset();
                    connected = true;
                    onInitializedListener.onSuccess(this);
                } else
                    onInitializedListener.onFailure(this);
            }, new Handler(Looper.getMainLooper()));
        }
    }

    public void disconnect() {
        connected = false;
    }

    private void handleKey(int keyCode) {
        if (onKeyPressedListener != null) {
            KeyBinding keyBinding = getCurrentPreset().getBinding(keyCode);
            onKeyPressedListener.onPressed(keyBinding, keyCode);
        }
    }

    public void setOnKeyPressedListener(OnKeyPressedListener onKeyPressedListener) {
        this.onKeyPressedListener = onKeyPressedListener;
    }

    public BindingsPreset createNewPreset() {
        String baseName = "preset", name;
        int suffix = 0;
        do {
            name = baseName + suffix;
            suffix++;
        } while (presets.containsKey(name));

        BindingsPreset preset = new BindingsPreset(name);
        preset.setParentId(getId());
        presets.put(name, preset);
        if (currentPresetName == null)
            setCurrentPresetName(name);
        BindingsPresetRepository.getInstance().insert(preset);

        return preset;
    }

    public void changePresetName(BindingsPreset preset, String newName) {
        if (!presets.containsKey(newName)) {
            if (currentPresetName.equals(preset.getName()))
                setCurrentPresetName(newName);
            presets.remove(preset.getName());
            preset.setName(newName);
            presets.put(newName, preset);
        }
    }

    public boolean presetNameExists(String name) {
        return presets.containsKey(name);
    }

    public Map<String, BindingsPreset> getPresets() {
        return presets;
    }

    public BindingsPreset getCurrentPreset() {
        return presets.get(currentPresetName);
    }

    public void removePreset(String name) {
        BindingsPreset preset = presets.remove(name);
        if (currentPresetName.equals(name))
            setCurrentPresetName(null);
        BindingsPresetRepository.getInstance().delete(preset);
    }

    public void setCurrentPresetName(String name) {
        currentPresetName = name;
        MIDIControllerDeviceRepository.getInstance().update(this);
    }

    public String getCurrentPresetName() {
        return currentPresetName;
    }

    public String getName() {
        return name;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public boolean isConnected() {
        return connected;
    }

    public interface OnInitializedListener {
        void onSuccess(MIDIControllerDevice device);
        void onFailure(MIDIControllerDevice device);
    }

    public interface OnKeyPressedListener {
        void onPressed(KeyBinding keyBinding, int keyCode);
    }
}
