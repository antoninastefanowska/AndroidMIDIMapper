package com.softsquare.midimapper.model;

import android.media.midi.MidiDeviceInfo;
import android.os.Bundle;

import com.softsquare.midimapper.database.DatabaseEntity;
import com.softsquare.midimapper.database.entities.BindingsPresetRelation;
import com.softsquare.midimapper.database.entities.DeviceRelation;

import java.util.HashMap;
import java.util.Map;

public class Device extends DatabaseEntity {
    private final String name;
    private final String manufacturer;
    private final String serialNumber;

    private String currentPresetName;
    private final Map<String, BindingsPreset> presets = new HashMap<>();

    private boolean connected = false;

    public Device(MidiDeviceInfo deviceInfo) {
        Bundle properties = deviceInfo.getProperties();
        name = properties.getString(MidiDeviceInfo.PROPERTY_NAME);
        manufacturer = properties.getString(MidiDeviceInfo.PROPERTY_MANUFACTURER);
        serialNumber = properties.getString(MidiDeviceInfo.PROPERTY_SERIAL_NUMBER);
    }

    public Device(DeviceRelation databaseEntity) {
        setId(databaseEntity.device.id);
        setParentId(databaseEntity.device.settingsId);
        name = databaseEntity.device.name;
        manufacturer = databaseEntity.device.manufacturer;
        serialNumber = databaseEntity.device.serialNumber;
        currentPresetName = databaseEntity.device.currentPresetName;
        for (BindingsPresetRelation presetEntity : databaseEntity.presets)
            presets.put(presetEntity.bindingsPreset.name, new BindingsPreset(presetEntity));
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public void addPreset(BindingsPreset preset) {
        preset.setParentId(getId());
        presets.put(preset.getName(), preset);
    }

    public void removePreset(String name) {
        presets.remove(name);
    }

    public void changePresetName(BindingsPreset preset, String newName) {
        if (!presets.containsKey(newName)) {
            presets.remove(preset.getName());
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

    public void setCurrentPresetName(String name) {
        currentPresetName = name;
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
}
