package com.softsquare.midimapper.database.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Update;

import com.softsquare.midimapper.database.entities.BindingsPresetEntity;

@Dao
public interface BindingsPresetDao {
    @Insert
    long insert(BindingsPresetEntity preset);

    @Delete
    void delete(BindingsPresetEntity preset);

    @Update
    void update(BindingsPresetEntity preset);
}
