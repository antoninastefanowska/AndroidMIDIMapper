package com.softsquare.midimapper.database.entities;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.softsquare.midimapper.model.MIDIControllerDevice;
import com.softsquare.midimapper.model.Settings;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SettingsRelation {
    @Embedded public SettingsEntity settings;

    @Relation(
            parentColumn = "id",
            entityColumn = "settings_id",
            entity = MIDIControllerDeviceEntity.class
    )
    public List<MIDIControllerDeviceRelation> devices;

    public SettingsRelation() { }

    public SettingsRelation(Settings settings) {
        Map<String, MIDIControllerDevice> dictionary = settings.getDevices();
        this.settings = new SettingsEntity(settings);

        devices = new ArrayList<>();
        for (Map.Entry<String, MIDIControllerDevice> entry : dictionary.entrySet())
            devices.add(new MIDIControllerDeviceRelation(entry.getValue()));
    }
}
