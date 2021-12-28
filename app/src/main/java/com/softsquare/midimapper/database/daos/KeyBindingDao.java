package com.softsquare.midimapper.database.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Update;

import com.softsquare.midimapper.database.entities.KeyBindingEntity;

@Dao
public interface KeyBindingDao {
    @Insert
    long insert(KeyBindingEntity keyBinding);

    @Delete
    void delete(KeyBindingEntity keyBinding);

    @Update
    void update(KeyBindingEntity keyBinding);
}
