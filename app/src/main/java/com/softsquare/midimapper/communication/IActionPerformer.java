package com.softsquare.midimapper.communication;

import android.content.Context;

import com.softsquare.midimapper.activity.MIDIMapperActivity;
import com.softsquare.midimapper.model.BindingsPreset;
import com.softsquare.midimapper.model.Device;
import com.softsquare.midimapper.model.KeyBinding;
import com.softsquare.midimapper.model.AppState;
import com.softsquare.midimapper.service.MIDIMapperAccessibilityService;

public interface IActionPerformer {
    void loadSettings(AppState appState, Context context);

    void addBinding(BindingsPreset preset, KeyBinding binding);
    void removeBinding(BindingsPreset preset, KeyBinding binding);
    void changeBindingPosition(BindingsPreset preset, KeyBinding binding, float x, float y);

    void addPreset(Device device, BindingsPreset preset);
    void removePreset(Device device, BindingsPreset preset);
    void renamePreset(Device device, BindingsPreset preset, String newName);
    void changeCurrentPreset(Device device, BindingsPreset oldPreset, BindingsPreset newPreset);

    void addDevice(Device device);
    void removeDevice(Device device);
    void connectDevice(Device device);
    void disconnectDevice(Device device);
    void changeCurrentDevice(Device oldDevice, Device newDevice);

    void pressKey(Device device, int keyCode);

    void showError(String error);
    void hideMenu();
    void showMenu();
    void listenForKey();
    void stopListeningForKey();
    void hideServiceGUI();
    void showServiceGUI();

    void startService(MIDIMapperAccessibilityService service);
    void stopService(MIDIMapperAccessibilityService service);

    void startActivity(MIDIMapperActivity activity);
    void stopActivity(MIDIMapperActivity activity);
    void resumeActivity(MIDIMapperActivity activity);
}
