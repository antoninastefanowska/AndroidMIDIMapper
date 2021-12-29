package com.softsquare.midimapper.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.softsquare.midimapper.database.daos.AppStateDao;
import com.softsquare.midimapper.database.daos.BindingsPresetDao;
import com.softsquare.midimapper.database.daos.KeyBindingDao;
import com.softsquare.midimapper.database.daos.DeviceDao;
import com.softsquare.midimapper.database.entities.AppStateEntity;
import com.softsquare.midimapper.database.entities.BindingsPresetEntity;
import com.softsquare.midimapper.database.entities.KeyBindingEntity;
import com.softsquare.midimapper.database.entities.DeviceEntity;
import com.softsquare.midimapper.model.AppState;

import java.util.concurrent.Executors;

@Database(entities = {
        KeyBindingEntity.class,
        BindingsPresetEntity.class,
        DeviceEntity.class,
        AppStateEntity.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase instance;

    public abstract KeyBindingDao keyBindingDao();
    public abstract BindingsPresetDao bindingsPresetDao();
    public abstract DeviceDao deviceDao();
    public abstract AppStateDao settingsDao();

    public static AppDatabase getDatabase(final Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                instance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "midi-mapper").addCallback(new Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        Executors.newSingleThreadExecutor().execute(() -> {
                            AppState appState = new AppState();
                            AppStateEntity appStateEntity = new AppStateEntity(appState);
                            AppStateDao appStateDao = getDatabase(context).settingsDao();
                            appStateDao.insert(appStateEntity);
                        });
                    }
                }).build();
            }
        }
        return instance;
    }
}
