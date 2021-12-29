package com.softsquare.midimapper.activity;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.pm.ServiceInfo;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;

import java.util.List;

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
