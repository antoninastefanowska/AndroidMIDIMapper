package com.softsquare.midimapper.database.repositories;

import android.content.Context;

import com.softsquare.midimapper.database.SettingsDatabase;
import com.softsquare.midimapper.database.daos.MIDIControllerDeviceDao;
import com.softsquare.midimapper.database.entities.MIDIControllerDeviceEntity;
import com.softsquare.midimapper.database.entities.MIDIControllerDeviceRelation;
import com.softsquare.midimapper.model.MIDIControllerDevice;

import java.util.concurrent.Executors;

public class MIDIControllerDeviceRepository {
    public static MIDIControllerDeviceRepository instance;
    private final MIDIControllerDeviceDao dao;

    private MIDIControllerDeviceRepository(Context context) {
        SettingsDatabase db = SettingsDatabase.getDatabase(context);
        dao = db.deviceDao();
    }

    public static void createInstance(Context context) {
        if (instance == null)
            instance = new MIDIControllerDeviceRepository(context);
    }

    public static MIDIControllerDeviceRepository getInstance() {
        return instance;
    }

    public void insert(MIDIControllerDevice device) {
        MIDIControllerDeviceEntity entity = new MIDIControllerDeviceEntity(device);
        Executors.newSingleThreadExecutor().execute(() -> device.setId(dao.insert(entity)));
    }

    public void delete(MIDIControllerDevice device) {
        MIDIControllerDeviceEntity entity = new MIDIControllerDeviceEntity(device);
        Executors.newSingleThreadExecutor().execute(() -> dao.delete(entity));
    }

    public void update(MIDIControllerDevice device) {
        MIDIControllerDeviceEntity entity = new MIDIControllerDeviceEntity(device);
        Executors.newSingleThreadExecutor().execute(() -> dao.update(entity));
    }
}
