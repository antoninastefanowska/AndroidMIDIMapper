package com.softsquare.midimapper.model;

import com.softsquare.midimapper.database.DatabaseEntity;
import com.softsquare.midimapper.database.entities.MIDIControllerDeviceRelation;
import com.softsquare.midimapper.database.entities.SettingsRelation;
import com.softsquare.midimapper.database.repositories.MIDIControllerDeviceRepository;
import com.softsquare.midimapper.database.repositories.SettingsRepository;

import java.util.HashMap;
import java.util.Map;

public class Settings extends DatabaseEntity {
    private final Map<String, MIDIControllerDevice> devices = new HashMap<>();
    private String currentDeviceSerialNumber;
    private boolean hidden = false;

    public Settings(SettingsRelation databaseEntity) {
        setId(databaseEntity.settings.id);
        currentDeviceSerialNumber = databaseEntity.settings.currentDeviceSerialNumber;
        //hidden = databaseEntity.settings.hidden;
        for (MIDIControllerDeviceRelation deviceEntity : databaseEntity.devices)
            devices.put(deviceEntity.device.serialNumber, new MIDIControllerDevice(deviceEntity));
    }

    public Settings() { }

    public String getCurrentDeviceSerialNumber() {
        return currentDeviceSerialNumber;
    }

    public void setCurrentDeviceSerialNumber(String serialNumber) {
        this.currentDeviceSerialNumber = serialNumber;
        SettingsRepository.getInstance().update();
    }

    public Map<String, MIDIControllerDevice> getDevices() {
        return devices;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
        //SettingsRepository.getInstance().update();
    }

    public boolean isHidden() {
        return hidden;
    }

    public void removeDevice(String serialNumber) {
        MIDIControllerDevice device = devices.remove(serialNumber);
        if (currentDeviceSerialNumber.equals(serialNumber))
            setCurrentDeviceSerialNumber(null);
        MIDIControllerDeviceRepository.getInstance().delete(device);
    }

    public MIDIControllerDevice getDevice(String serialNumber) {
        return devices.get(serialNumber);
    }

    public void addDevice(MIDIControllerDevice device) {
        device.setParentId(getId());
        devices.put(device.getSerialNumber(), device);
        MIDIControllerDeviceRepository.getInstance().insert(device);
    }

    public MIDIControllerDevice getCurrentDevice() {
        return devices.get(currentDeviceSerialNumber);
    }
}
