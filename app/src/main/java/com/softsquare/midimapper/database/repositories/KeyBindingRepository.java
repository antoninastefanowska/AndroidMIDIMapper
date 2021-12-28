package com.softsquare.midimapper.database.repositories;

import android.content.Context;

import com.softsquare.midimapper.database.SettingsDatabase;
import com.softsquare.midimapper.database.daos.KeyBindingDao;
import com.softsquare.midimapper.database.entities.KeyBindingEntity;
import com.softsquare.midimapper.model.KeyBinding;

import java.util.concurrent.Executors;

public class KeyBindingRepository {
    private static KeyBindingRepository instance;
    private final KeyBindingDao dao;

    private KeyBindingRepository(Context context) {
        SettingsDatabase db = SettingsDatabase.getDatabase(context);
        dao = db.keyBindingDao();
    }

    public static void createInstance(Context context) {
        if (instance == null)
            instance = new KeyBindingRepository(context);
    }

    public static KeyBindingRepository getInstance() {
        return instance;
    }

    public void insert(KeyBinding keyBinding) {
        KeyBindingEntity entity = new KeyBindingEntity(keyBinding);
        Executors.newSingleThreadExecutor().execute(() -> keyBinding.setId(dao.insert(entity)));
    }

    public void delete(KeyBinding keyBinding) {
        KeyBindingEntity entity = new KeyBindingEntity(keyBinding);
        Executors.newSingleThreadExecutor().execute(() -> dao.delete(entity));
    }

    public void update(KeyBinding keyBinding) {
        KeyBindingEntity entity = new KeyBindingEntity(keyBinding);
        Executors.newSingleThreadExecutor().execute(() -> dao.update(entity));
    }
}
