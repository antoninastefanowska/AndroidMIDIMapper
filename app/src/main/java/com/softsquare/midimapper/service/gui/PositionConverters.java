package com.softsquare.midimapper.service.gui;

import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.Display;
import android.view.Surface;
import android.widget.FrameLayout;

public final class PositionConverters {
    private PositionConverters() { }

    private static Pair<Integer, Integer> getScreenSize(Display defaultDisplay) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        defaultDisplay.getRealMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        return new Pair<>(width, height);
    }

    public static Pair<Float, Float> getLayoutPosition(FrameLayout containerLayout) {
        int[] position = new int[2];
        containerLayout.getLocationOnScreen(position);
        float x = position[0];
        float y = position[1];
        return new Pair<>(x, y);
    }

    public static Pair<Float, Float> markerToBindingPosition(int markerSize, FrameLayout containerLayout, Display defaultDisplay, float x, float y) {
        Pair<Float, Float> screenPosition = markerToScreenPosition(containerLayout, x, y);
        float newX = screenPosition.first + markerSize / 2.0f;
        float newY = screenPosition.second + markerSize / 2.0f;
        return screenToBindingPosition(defaultDisplay, newX, newY);
    }

    public static Pair<Float, Float> bindingToMarkerPosition(int markerSize, FrameLayout containerLayout, Display defaultDisplay, float x, float y) {
        Pair<Float, Float> screenPosition = bindingToScreenPosition(defaultDisplay, x, y);
        float newX = screenPosition.first - markerSize / 2.0f;
        float newY = screenPosition.second - markerSize / 2.0f;
        return screenToMarkerPosition(containerLayout, newX, newY);
    }

    public static Pair<Float, Float> markerToScreenPosition(FrameLayout containerLayout, float x, float y) {
        Pair<Float, Float> layoutPosition = getLayoutPosition(containerLayout);
        return new Pair<>(x + layoutPosition.first, y + layoutPosition.second);
    }

    public static Pair<Float, Float> screenToMarkerPosition(FrameLayout containerLayout, float x, float y) {
        Pair<Float, Float> layoutPosition = getLayoutPosition(containerLayout);
        return new Pair<>(x - layoutPosition.first, y - layoutPosition.second);
    }

    @SuppressWarnings("all")
    public static Pair<Float, Float> bindingToScreenPosition(Display defaultDisplay, float x, float y) {
        int rotation = defaultDisplay.getRotation();
        Pair<Integer, Integer> screenSize = getScreenSize(defaultDisplay);
        float width = screenSize.first;
        float height = screenSize.second;

        float newX = 0, newY = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                newX = x;
                newY = y;
                break;
            case Surface.ROTATION_90:
                newX = y;
                newY = height - x + 1;
                break;
            case Surface.ROTATION_180:
                newX = width - x + 1;
                newY = height - y + 1;
                break;
            case Surface.ROTATION_270:
                newX = width - y + 1;
                newY = x;
                break;
        }
        return new Pair<>(newX, newY);
    }

    @SuppressWarnings("all")
    public static Pair<Float, Float> screenToBindingPosition(Display defaultDisplay, float x, float y) {
        int rotation = defaultDisplay.getRotation();
        Pair<Integer, Integer> screenSize = getScreenSize(defaultDisplay);
        float width = screenSize.first;
        float height = screenSize.second;

        float newX = 0, newY = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                newX = x;
                newY = y;
                break;
            case Surface.ROTATION_90:
                newX = height - y + 1;
                newY = x;
                break;
            case Surface.ROTATION_180:
                newX = width - x + 1;
                newY = height - y + 1;
                break;
            case Surface.ROTATION_270:
                newX = y;
                newY = width - x + 1;
                break;
        }
        return new Pair<>(newX, newY);
    }
}
