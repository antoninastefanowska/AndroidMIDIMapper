package com.softsquare.midimapper.model;

import com.softsquare.midimapper.database.DatabaseEntity;
import com.softsquare.midimapper.database.entities.DeviceRelation;
import com.softsquare.midimapper.database.entities.AppStateRelation;

import java.util.HashMap;
import java.util.Map;

public class AppState extends DatabaseEntity {
    private final Map<String, Device> devices = new HashMap<>();
    private String currentDeviceSerialNumber;

    private boolean menuHidden = false;
    private boolean listeningForKey = false;
    private boolean serviceGUIOpened = false;

    public AppState(AppStateRelation databaseEntity) {
        setId(databaseEntity.appState.id);
        currentDeviceSerialNumber = databaseEntity.appState.currentDeviceSerialNumber;
        for (DeviceRelation deviceEntity : databaseEntity.devices)
            devices.put(deviceEntity.device.serialNumber, new Device(deviceEntity));
    }

    public AppState() { }

    public Device getDevice(String serialNumber) {
        return devices.get(serialNumber);
    }

    public String getCurrentDeviceSerialNumber() {
        return currentDeviceSerialNumber;
    }

    public Device getCurrentDevice() {
        return devices.get(currentDeviceSerialNumber);
    }

    public boolean isMenuHidden() {
        return menuHidden;
    }

    public boolean isListeningForKey() {
        return listeningForKey;
    }

    public boolean isServiceGUIOpened() {
        return serviceGUIOpened;
    }

    public Map<String, Device> getDevices() {
        return devices;
    }

    public void removeDevice(String serialNumber) {
        devices.remove(serialNumber);
    }

    public void setCurrentDeviceSerialNumber(String serialNumber) {
        this.currentDeviceSerialNumber = serialNumber;
    }

    public void setMenuHidden(boolean menuHidden) {
        this.menuHidden = menuHidden;
    }

    public void setListeningForKey(boolean listeningForKey) {
        this.listeningForKey = listeningForKey;
    }

    public void setServiceGUIOpened(boolean serviceGUIOpened) {
        this.serviceGUIOpened = serviceGUIOpened;
    }

    public void addDevice(Device device) {
        device.setParentId(getId());
        devices.put(device.getSerialNumber(), device);
    }
}
