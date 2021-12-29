package com.softsquare.midimapper.database.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.softsquare.midimapper.database.entities.AppStateRelation;
import com.softsquare.midimapper.database.entities.AppStateEntity;

import java.util.List;

@Dao
public interface AppStateDao {
    @Transaction
    @Query("SELECT * FROM app_state")
    List<AppStateRelation> getSettings();

    @Insert
    long insert(AppStateEntity settings);

    @Update
    void update(AppStateEntity settings);
}
