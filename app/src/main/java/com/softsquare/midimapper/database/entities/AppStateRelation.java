package com.softsquare.midimapper.database.entities;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.softsquare.midimapper.model.AppState;
import com.softsquare.midimapper.model.Device;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AppStateRelation {
    @Embedded public AppStateEntity appState;

    @Relation(
            parentColumn = "id",
            entityColumn = "settings_id",
            entity = DeviceEntity.class
    )
    public List<DeviceRelation> devices;

    public AppStateRelation() { }

    public AppStateRelation(AppState appState) {
        Map<String, Device> dictionary = appState.getDevices();
        this.appState = new AppStateEntity(appState);

        devices = new ArrayList<>();
        for (Map.Entry<String, Device> entry : dictionary.entrySet())
            devices.add(new DeviceRelation(entry.getValue()));
    }
}
