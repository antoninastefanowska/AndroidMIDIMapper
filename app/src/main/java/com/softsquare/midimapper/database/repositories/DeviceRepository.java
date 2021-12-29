package com.softsquare.midimapper.database.repositories;

import android.content.Context;

import com.softsquare.midimapper.database.AppDatabase;
import com.softsquare.midimapper.database.daos.DeviceDao;
import com.softsquare.midimapper.database.entities.DeviceEntity;
import com.softsquare.midimapper.model.Device;

import java.util.concurrent.Executors;

public class DeviceRepository {
    public static DeviceRepository instance;
    private final DeviceDao dao;

    private DeviceRepository(Context context) {
        AppDatabase db = AppDatabase.getDatabase(context);
        dao = db.deviceDao();
    }

    public static DeviceRepository getInstance(Context context) {
        if (instance == null)
            instance = new DeviceRepository(context);
        return instance;
    }

    public void insert(Device device) {
        DeviceEntity entity = new DeviceEntity(device);
        Executors.newSingleThreadExecutor().execute(() -> device.setId(dao.insert(entity)));
    }

    public void delete(Device device) {
        DeviceEntity entity = new DeviceEntity(device);
        Executors.newSingleThreadExecutor().execute(() -> dao.delete(entity));
    }

    public void update(Device device) {
        DeviceEntity entity = new DeviceEntity(device);
        Executors.newSingleThreadExecutor().execute(() -> dao.update(entity));
    }
}
