package com.softsquare.midimapper.database.entities;

import static androidx.room.ForeignKey.CASCADE;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.softsquare.midimapper.model.KeyBinding;

@Entity(
        tableName = "key_binding",
        indices = {
                @Index(value = {"key_code"}, unique = true),
                @Index(value = {"preset_id"})},
        foreignKeys = {@ForeignKey(
                onDelete = CASCADE,
                entity = BindingsPresetEntity.class,
                parentColumns = "id",
                childColumns = "preset_id"
        )})
public class KeyBindingEntity {
    @PrimaryKey(autoGenerate = true)
    public Long id;

    @ColumnInfo(name = "key_code")
    public int keyCode;

    @ColumnInfo(name = "x")
    public float x;

    @ColumnInfo(name = "y")
    public float y;

    @ColumnInfo(name = "preset_id")
    public long presetId;

    public KeyBindingEntity () { }

    public KeyBindingEntity(KeyBinding keyBinding) {
        id = keyBinding.getId();
        presetId = keyBinding.getParentId();
        keyCode = keyBinding.getKeyCode();
        x = keyBinding.getX();
        y = keyBinding.getY();
    }
}
