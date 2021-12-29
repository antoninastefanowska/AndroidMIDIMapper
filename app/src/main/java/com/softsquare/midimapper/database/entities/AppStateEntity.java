package com.softsquare.midimapper.database.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.softsquare.midimapper.model.AppState;

@Entity(tableName = "app_state")
public class AppStateEntity {
    @PrimaryKey(autoGenerate = true)
    public Long id;

    @ColumnInfo(name = "current_device_serial_number")
    public String currentDeviceSerialNumber;

    public AppStateEntity() { }

    public AppStateEntity(AppState appState) {
        id = appState.getId();
        currentDeviceSerialNumber = appState.getCurrentDeviceSerialNumber();
    }
}
