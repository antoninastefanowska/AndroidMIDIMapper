package com.softsquare.midimapper.model;

import com.softsquare.midimapper.database.DatabaseEntity;
import com.softsquare.midimapper.database.entities.BindingsPresetRelation;
import com.softsquare.midimapper.database.entities.KeyBindingEntity;
import com.softsquare.midimapper.database.repositories.BindingsPresetRepository;
import com.softsquare.midimapper.database.repositories.KeyBindingRepository;

import java.util.HashMap;
import java.util.Map;

public class BindingsPreset extends DatabaseEntity {
    private Map<Integer, KeyBinding> bindings = new HashMap<>();
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

    public KeyBinding createNewBinding(int keyCode, float x, float y) {
        KeyBinding keyBinding = new KeyBinding(keyCode, x, y);
        keyBinding.setParentId(getId());
        bindings.put(keyCode, keyBinding);
        KeyBindingRepository.getInstance().insert(keyBinding);
        return keyBinding;
    }

    public void removeBinding(int keyCode) {
        KeyBinding keyBinding = bindings.remove(keyCode);
        KeyBindingRepository.getInstance().delete(keyBinding);
    }

    public KeyBinding getBinding(int keyCode) {
        return bindings.get(keyCode);
    }

    public Map<Integer, KeyBinding> getBindings() {
        return bindings;
    }

    public void setName(String name) {
        this.name = name;
        BindingsPresetRepository.getInstance().update(this);
    }

    public String getName() {
        return name;
    }
}
