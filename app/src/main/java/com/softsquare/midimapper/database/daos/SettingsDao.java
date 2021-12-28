package com.softsquare.midimapper.database.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.softsquare.midimapper.database.entities.SettingsEntity;
import com.softsquare.midimapper.database.entities.SettingsRelation;

import java.util.List;

@Dao
public interface SettingsDao {
    @Transaction
    @Query("SELECT * FROM settings")
    List<SettingsRelation> getSettings();

    @Insert
    long insert(SettingsEntity settings);

    @Update
    void update(SettingsEntity settings);
}
