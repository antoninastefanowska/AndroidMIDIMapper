package com.softsquare.midimapper.activity;

import android.view.View;
import android.view.ViewGroup;

public class ViewUtilities {
    public static void setEnabledForGroup(boolean enabled, ViewGroup viewGroup) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            child.setEnabled(enabled);
            if (child instanceof ViewGroup)
                setEnabledForGroup(enabled, (ViewGroup)child);
        }
    }
}
