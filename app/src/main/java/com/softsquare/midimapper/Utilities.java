package com.softsquare.midimapper;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.pm.ServiceInfo;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;

import java.util.List;

public class Utilities {
    public static boolean isServiceEnabled(Context context) {
        AccessibilityManager manager = (AccessibilityManager)context.getSystemService(Context.ACCESSIBILITY_SERVICE);
        List<AccessibilityServiceInfo> services = manager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK);
        for (AccessibilityServiceInfo service : services) {
            ServiceInfo serviceInfo = service.getResolveInfo().serviceInfo;
            if (serviceInfo.packageName.equals(context.getPackageName()) && serviceInfo.name.equals(MIDIMapperAccessibilityService.class.getName()))
                return true;
        }
        return false;
    }

    public static void setEnabledForGroup(boolean enabled, ViewGroup viewGroup) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            child.setEnabled(enabled);
            if (child instanceof ViewGroup)
                setEnabledForGroup(enabled, (ViewGroup)child);
        }
    }
}
