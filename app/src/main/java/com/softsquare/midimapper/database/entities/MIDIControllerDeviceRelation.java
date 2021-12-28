package com.softsquare.midimapper.database.entities;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.softsquare.midimapper.model.BindingsPreset;
import com.softsquare.midimapper.model.MIDIControllerDevice;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MIDIControllerDeviceRelation {
    @Embedded public MIDIControllerDeviceEntity device;

    @Relation(
            parentColumn = "id",
            entityColumn = "device_id",
            entity = BindingsPresetEntity.class
    )
    public List<BindingsPresetRelation> presets;

    public MIDIControllerDeviceRelation() { }

    public MIDIControllerDeviceRelation(MIDIControllerDevice device) {
        Map<String, BindingsPreset> dictionary = device.getPresets();
        this.device = new MIDIControllerDeviceEntity(device);

        presets = new ArrayList<>();
        for (Map.Entry<String, BindingsPreset> entry : dictionary.entrySet())
            presets.add(new BindingsPresetRelation(entry.getValue()));
    }
}
