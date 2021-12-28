package com.softsquare.midimapper.database.entities;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.softsquare.midimapper.model.BindingsPreset;
import com.softsquare.midimapper.model.KeyBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BindingsPresetRelation {
    @Embedded public BindingsPresetEntity bindingsPreset;
    @Relation(
            parentColumn = "id",
            entityColumn = "preset_id"
    )
    public List<KeyBindingEntity> keyBindings;

    public BindingsPresetRelation() { }

    public BindingsPresetRelation(BindingsPreset bindingsPreset) {
        Map<Integer, KeyBinding> dictionary = bindingsPreset.getBindings();
        this.bindingsPreset = new BindingsPresetEntity(bindingsPreset);

        keyBindings = new ArrayList<>();
        for (Map.Entry<Integer, KeyBinding> entry : dictionary.entrySet())
            keyBindings.add(new KeyBindingEntity(entry.getValue()));
    }
}
