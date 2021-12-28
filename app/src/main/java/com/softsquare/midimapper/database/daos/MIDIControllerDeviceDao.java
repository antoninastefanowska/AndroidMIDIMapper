package com.softsquare.midimapper.database.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Update;

import com.softsquare.midimapper.database.entities.MIDIControllerDeviceEntity;

@Dao
public interface MIDIControllerDeviceDao {
    @Insert
    long insert(MIDIControllerDeviceEntity device);

    @Delete
    void delete(MIDIControllerDeviceEntity device);

    @Update
    void update(MIDIControllerDeviceEntity device);
}
