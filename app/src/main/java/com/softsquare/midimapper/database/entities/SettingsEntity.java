package com.softsquare.midimapper.database.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.softsquare.midimapper.model.Settings;

@Entity(tableName = "settings")
public class SettingsEntity {
    @PrimaryKey(autoGenerate = true)
    public Long id;

    @ColumnInfo(name = "current_device_serial_number")
    public String currentDeviceSerialNumber;

    @ColumnInfo(name = "hidden")
    public boolean hidden;

    public SettingsEntity() { }

    public SettingsEntity(Settings settings) {
        id = settings.getId();
        currentDeviceSerialNumber = settings.getCurrentDeviceSerialNumber();
        hidden = settings.isHidden();
    }
}
