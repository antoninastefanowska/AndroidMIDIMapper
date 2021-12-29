package com.softsquare.midimapper.database.repositories;

import android.content.Context;

import com.softsquare.midimapper.database.AppDatabase;
import com.softsquare.midimapper.database.daos.BindingsPresetDao;
import com.softsquare.midimapper.database.entities.BindingsPresetEntity;
import com.softsquare.midimapper.model.BindingsPreset;

import java.util.concurrent.Executors;

public class BindingsPresetRepository {
    private static BindingsPresetRepository instance;
    private final BindingsPresetDao dao;

    private BindingsPresetRepository(Context context) {
        AppDatabase db = AppDatabase.getDatabase(context);
        dao = db.bindingsPresetDao();
    }

    public static BindingsPresetRepository getInstance(Context context) {
        if (instance == null)
            instance = new BindingsPresetRepository(context);
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
