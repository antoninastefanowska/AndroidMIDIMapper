package com.softsquare.midimapper.database.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Update;

import com.softsquare.midimapper.database.entities.DeviceEntity;

@Dao
public interface DeviceDao {
    @Insert
    long insert(DeviceEntity device);

    @Delete
    void delete(DeviceEntity device);

    @Update
    void update(DeviceEntity device);
}
