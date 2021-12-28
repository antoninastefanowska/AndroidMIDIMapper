package com.softsquare.midimapper.model;

import android.graphics.Color;

public enum MusicSymbol {
    C(0, "C", "#fc9392"),
    C_SHARP(1, "C#", "#fbbf91"),
    D(2, "D", "#fbea8f"),
    D_SHARP(3, "D#", "#e1fb8e"),
    E(4, "E", "#b4fc8f"),
    F(5, "F", "#90fc99"),
    F_SHARP(6, "F#", "#90fbc6"),
    G(7, "G", "#90faf2"),
    G_SHARP(8, "G#", "#91d8fc"),
    A(9, "A", "#93acfd"),
    A_SHARP(10, "A#", "#a591fd"),
    B(11, "B", "#d191fd");

    private final int code;
    private final String symbol;
    private final int color;

    MusicSymbol(int code, String symbol, String color) {
        this.code = code;
        this.symbol = symbol;
        this.color = Color.parseColor(color);
    }

    public int getCode() {
        return code;
    }

    public String getSymbol() {
        return symbol;
    }

    public int getColor() { return color; }

    public static String getSymbol(int keyCode) {
        int code = keyCode % 12;
        for (MusicSymbol value : MusicSymbol.values())
            if (value.getCode() == code)
                return value.getSymbol();
        return null;
    }

    public static int getColor(int keyCode) {
        int code = keyCode % 12;
        for (MusicSymbol value : MusicSymbol.values())
            if (value.getCode() == code)
                return value.getColor();
        return -1;
    }

    public static int getOctave(int keyCode) {
        return keyCode / 12 - 1;
    }
}
