package com.softsquare.midimapper.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.softsquare.midimapper.database.daos.BindingsPresetDao;
import com.softsquare.midimapper.database.daos.KeyBindingDao;
import com.softsquare.midimapper.database.daos.MIDIControllerDeviceDao;
import com.softsquare.midimapper.database.daos.SettingsDao;
import com.softsquare.midimapper.database.entities.BindingsPresetEntity;
import com.softsquare.midimapper.database.entities.KeyBindingEntity;
import com.softsquare.midimapper.database.entities.MIDIControllerDeviceEntity;
import com.softsquare.midimapper.database.entities.SettingsEntity;
import com.softsquare.midimapper.model.Settings;

import java.util.concurrent.Executors;

@Database(entities = {
        KeyBindingEntity.class,
        BindingsPresetEntity.class,
        MIDIControllerDeviceEntity.class,
        SettingsEntity.class}, version = 1, exportSchema = false)
public abstract class SettingsDatabase extends RoomDatabase {
    private static volatile SettingsDatabase instance;

    public abstract KeyBindingDao keyBindingDao();
    public abstract BindingsPresetDao bindingsPresetDao();
    public abstract MIDIControllerDeviceDao deviceDao();
    public abstract SettingsDao settingsDao();

    public static SettingsDatabase getDatabase(final Context context) {
        if (instance == null) {
            synchronized (SettingsDatabase.class) {
                instance = Room.databaseBuilder(context.getApplicationContext(), SettingsDatabase.class, "midi-mapper").addCallback(new Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        Executors.newSingleThreadExecutor().execute(() -> {
                            Settings settings = new Settings();
                            SettingsEntity settingsEntity = new SettingsEntity(settings);
                            SettingsDao settingsDao = getDatabase(context).settingsDao();
                            settingsDao.insert(settingsEntity);
                        });
                    }
                }).build();
            }
        }
        return instance;
    }
}
