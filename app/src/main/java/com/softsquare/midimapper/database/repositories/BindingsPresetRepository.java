package com.softsquare.midimapper.database.repositories;

import android.content.Context;

import com.softsquare.midimapper.database.SettingsDatabase;
import com.softsquare.midimapper.database.daos.BindingsPresetDao;
import com.softsquare.midimapper.database.entities.BindingsPresetEntity;
import com.softsquare.midimapper.model.BindingsPreset;

import java.util.concurrent.Executors;

public class BindingsPresetRepository {
    private static BindingsPresetRepository instance;
    private final BindingsPresetDao dao;

    private BindingsPresetRepository(Context context) {
        SettingsDatabase db = SettingsDatabase.getDatabase(context);
        dao = db.bindingsPresetDao();
    }

    public static void createInstance(Context context) {
        if (instance == null)
            instance = new BindingsPresetRepository(context);
    }

    public static BindingsPresetRepository getInstance() {
        return instance;
    }

    public void insert(BindingsPreset bindingsPreset) {
        BindingsPresetEntity entity = new BindingsPresetEntity(bindingsPreset);
        Executors.newSingleThreadExecutor().execute(() -> bindingsPreset.setId(dao.insert(entity)));
    }

    public void delete(BindingsPreset bindingsPreset) {
        BindingsPresetEntity entity = new BindingsPresetEntity(bindingsPreset);
        Executors.newSingleThreadExecutor().execute(() -> dao.delete(entity));
    }

    public void update(BindingsPreset bindingsPreset) {
        BindingsPresetEntity entity = new BindingsPresetEntity(bindingsPreset);
        Executors.newSingleThreadExecutor().execute(() -> dao.update(entity));
    }
}
