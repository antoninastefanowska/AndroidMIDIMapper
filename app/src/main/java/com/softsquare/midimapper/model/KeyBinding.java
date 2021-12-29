package com.softsquare.midimapper.model;

import com.softsquare.midimapper.database.DatabaseEntity;
import com.softsquare.midimapper.database.entities.KeyBindingEntity;

public class KeyBinding extends DatabaseEntity {
    private final int keyCode;
    private final String label;
    private final int octave;
    private final int color;
    private float x;
    private float y;

    public KeyBinding(int keyCode, float x, float y) {
        this.keyCode = keyCode;
        this.x = x;
        this.y = y;
        label = MusicSymbol.getSymbol(keyCode);
        octave = MusicSymbol.getOctave(keyCode);
        color = MusicSymbol.getColor(keyCode);
    }

    public KeyBinding(KeyBindingEntity databaseEntity) {
        setId(databaseEntity.id);
        setParentId(databaseEntity.presetId);
        keyCode = databaseEntity.keyCode;
        x = databaseEntity.x;
        y = databaseEntity.y;
        label = MusicSymbol.getSymbol(keyCode);
        octave = MusicSymbol.getOctave(keyCode);
        color = MusicSymbol.getColor(keyCode);
    }

    public int getKeyCode() {
        return keyCode;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public String getLabel() {
        return label;
    }

    public String getOctave() {
        return String.valueOf(octave);
    }

    public int getColor() {
        return color;
    }

    public void changePosition(float x, float y) {
        this.x = x;
        this.y = y;
    }
}
