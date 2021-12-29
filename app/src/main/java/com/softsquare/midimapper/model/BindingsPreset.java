package com.softsquare.midimapper.model;

import com.softsquare.midimapper.database.DatabaseEntity;
import com.softsquare.midimapper.database.entities.BindingsPresetRelation;
import com.softsquare.midimapper.database.entities.KeyBindingEntity;

import java.util.HashMap;
import java.util.Map;

public class BindingsPreset extends DatabaseEntity {
    private final Map<Integer, KeyBinding> bindings = new HashMap<>();
    private String name;

    public BindingsPreset(String name) {
        this.name = name;
    }

    public BindingsPreset(BindingsPresetRelation databaseEntity) {
        setId(databaseEntity.bindingsPreset.id);
        setParentId(databaseEntity.bindingsPreset.deviceId);
        name = databaseEntity.bindingsPreset.name;
        for (KeyBindingEntity keyBindingEntity : databaseEntity.keyBindings)
            bindings.put(keyBindingEntity.keyCode, new KeyBinding(keyBindingEntity));
    }

    public void addBinding(KeyBinding binding) {
        binding.setParentId(getId());
        bindings.put(binding.getKeyCode(), binding);
    }

    public void removeBinding(int keyCode) {
        bindings.remove(keyCode);
    }

    public KeyBinding getBinding(int keyCode) {
        return bindings.get(keyCode);
    }

    public Map<Integer, KeyBinding> getBindings() {
        return bindings;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
