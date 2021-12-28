package com.softsquare.midimapper.database.repositories;

import android.content.Context;

import com.softsquare.midimapper.database.SettingsDatabase;
import com.softsquare.midimapper.database.daos.SettingsDao;
import com.softsquare.midimapper.database.entities.SettingsEntity;
import com.softsquare.midimapper.database.entities.SettingsRelation;
import com.softsquare.midimapper.model.Settings;

import java.util.concurrent.Executors;

public class SettingsRepository {
    private static SettingsRepository instance;
    private final SettingsDao dao;
    private Settings settings;

    private SettingsRepository(Context context) {
        SettingsDatabase db = SettingsDatabase.getDatabase(context);
        dao = db.settingsDao();
    }

    public static void createInstance(Context context) {
        if (instance == null)
            instance = new SettingsRepository(context);
    }

    public static SettingsRepository getInstance() {
        return instance;
    }

    public Settings getSettings() {
        return settings;
    }

    public void getSettingsAsync(OnSettingsLoadedListener listener) {
        Executors.newSingleThreadExecutor().execute(() -> {
            if (settings == null)
                settings = new Settings(dao.getSettings().get(0));
            listener.onLoaded(settings);
        });
    }

    public void update() {
        SettingsEntity entity = new SettingsEntity(settings);
        Executors.newSingleThreadExecutor().execute(() -> dao.update(entity));
    }

    public static interface OnSettingsLoadedListener {
        void onLoaded(Settings settings);
    }
}
