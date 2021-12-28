package com.softsquare.midimapper.database.entities;

import static androidx.room.ForeignKey.CASCADE;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.softsquare.midimapper.model.BindingsPreset;

@Entity(
        tableName = "bindings_preset",
        indices = {
                @Index(value = {"name"}, unique = true),
                @Index(value = {"device_id"})},
        foreignKeys = {@ForeignKey(
                onDelete = CASCADE,
                entity = MIDIControllerDeviceEntity.class,
                parentColumns = "id",
                childColumns = "device_id"
        )})
public class BindingsPresetEntity {
    @PrimaryKey(autoGenerate = true)
    public Long id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "device_id")
    public long deviceId;

    public BindingsPresetEntity() { }

    public BindingsPresetEntity(BindingsPreset bindingsPreset) {
        id = bindingsPreset.getId();
        deviceId = bindingsPreset.getParentId();
        name = bindingsPreset.getName();
    }
}
