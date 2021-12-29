package com.softsquare.midimapper.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.accessibilityservice.GestureDescription;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.graphics.Path;

import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;

import com.softsquare.midimapper.communication.AppActionPerformer;

import java.util.List;

public class MIDIMapperAccessibilityService extends AccessibilityService {
    private AppActionPerformer actionPerformer;

    @Override
    protected void onServiceConnected() {
        actionPerformer = AppActionPerformer.getInstance(this);
        actionPerformer.startService(this);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        actionPerformer.stopService(this);
        return super.onUnbind(intent);
    }

    public void dispatchTapAt(float x, float y) {
        Path tapPath = new Path();
        tapPath.moveTo(x, y);
        GestureDescription.Builder gestureBuilder = new GestureDescription.Builder();
        GestureDescription.StrokeDescription tapStroke = new GestureDescription.StrokeDescription(tapPath, 0, 1);
        gestureBuilder.addStroke(tapStroke);
        dispatchGesture(gestureBuilder.build(), null, null);
    }

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

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) { }

    @Override
    public void onInterrupt() { }
}
