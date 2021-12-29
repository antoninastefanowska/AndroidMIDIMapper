package com.softsquare.midimapper.database.entities;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.softsquare.midimapper.model.BindingsPreset;
import com.softsquare.midimapper.model.Device;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DeviceRelation {
    @Embedded public DeviceEntity device;

    @Relation(
            parentColumn = "id",
            entityColumn = "device_id",
            entity = BindingsPresetEntity.class
    )
    public List<BindingsPresetRelation> presets;

    public DeviceRelation() { }

    public DeviceRelation(Device device) {
        Map<String, BindingsPreset> dictionary = device.getPresets();
        this.device = new DeviceEntity(device);

        presets = new ArrayList<>();
        for (Map.Entry<String, BindingsPreset> entry : dictionary.entrySet())
            presets.add(new BindingsPresetRelation(entry.getValue()));
    }
}
