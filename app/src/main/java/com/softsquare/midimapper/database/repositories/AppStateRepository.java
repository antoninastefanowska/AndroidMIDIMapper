package com.softsquare.midimapper.database.repositories;

import android.content.Context;

import com.softsquare.midimapper.database.AppDatabase;
import com.softsquare.midimapper.database.daos.AppStateDao;
import com.softsquare.midimapper.database.entities.AppStateEntity;
import com.softsquare.midimapper.model.AppState;

import java.util.concurrent.Executors;

public class AppStateRepository {
    private static AppStateRepository instance;
    private final AppStateDao dao;
    private AppState appState;

    private AppStateRepository(Context context) {
        AppDatabase db = AppDatabase.getDatabase(context);
        dao = db.settingsDao();
    }

    public static AppStateRepository getInstance(Context context) {
        if (instance == null)
            instance = new AppStateRepository(context);
        return instance;
    }

    public AppState getSettings() {
        return appState;
    }

    public void getSettingsAsync(OnAppStateLoadedListener listener) {
        Executors.newSingleThreadExecutor().execute(() -> {
            if (appState == null)
                appState = new AppState(dao.getSettings().get(0));
            listener.onLoaded(appState);
        });
    }

    public void update(AppState appState) {
        AppStateEntity entity = new AppStateEntity(appState);
        Executors.newSingleThreadExecutor().execute(() -> dao.update(entity));
    }

    public interface OnAppStateLoadedListener {
        void onLoaded(AppState appState);
    }
}
