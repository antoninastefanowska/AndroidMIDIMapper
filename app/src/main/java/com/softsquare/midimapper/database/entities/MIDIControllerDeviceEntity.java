package com.softsquare.midimapper.database.entities;

import static androidx.room.ForeignKey.CASCADE;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.softsquare.midimapper.model.MIDIControllerDevice;

@Entity(
        tableName = "midi_controller_device",
        indices = {
                @Index(value = {"serial_number"}, unique = true),
                @Index(value = {"settings_id"})},
        foreignKeys = {@ForeignKey(
                onDelete = CASCADE,
                entity = SettingsEntity.class,
                parentColumns = "id",
                childColumns = "settings_id"
        )})
public class MIDIControllerDeviceEntity {
    @PrimaryKey(autoGenerate = true)
    public Long id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "manufacturer")
    public String manufacturer;

    @ColumnInfo(name = "serial_number")
    public String serialNumber;

    @ColumnInfo(name = "current_preset_name")
    public String currentPresetName;

    @ColumnInfo(name = "settings_id")
    public long settingsId;

    public MIDIControllerDeviceEntity() { }

    public MIDIControllerDeviceEntity(MIDIControllerDevice device) {
        id = device.getId();
        settingsId = device.getParentId();
        name = device.getName();
        manufacturer = device.getManufacturer();
        serialNumber = device.getSerialNumber();
        currentPresetName = device.getCurrentPresetName();
    }
}
