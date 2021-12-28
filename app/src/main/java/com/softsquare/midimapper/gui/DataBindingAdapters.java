package com.softsquare.midimapper.gui;

import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

public class DataBindingAdapters {
    @BindingAdapter("tint")
    public static void setTint(ImageView view, int color) {
        view.setColorFilter(color);
    }
}
