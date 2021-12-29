package com.softsquare.midimapper.communication;

import android.content.Context;

import com.softsquare.midimapper.activity.MIDIMapperActivity;
import com.softsquare.midimapper.model.BindingsPreset;
import com.softsquare.midimapper.model.Device;
import com.softsquare.midimapper.model.KeyBinding;
import com.softsquare.midimapper.model.AppState;
import com.softsquare.midimapper.service.MIDIMapperAccessibilityService;

public class ModelActionPerformer implements IActionPerformer {
    private static ModelActionPerformer instance;

    private AppState appState;

    private ModelActionPerformer() { }

    public static ModelActionPerformer getInstance() {
        if (instance == null)
            instance = new ModelActionPerformer();
        return instance;
    }

    @Override
    public void loadSettings(AppState appState, Context context) {
        this.appState = appState;
    }

    @Override
    public void addBinding(BindingsPreset preset, KeyBinding binding) {
        preset.addBinding(binding);
    }

    @Override
    public void removeBinding(BindingsPreset preset, KeyBinding binding) {
        preset.removeBinding(binding.getKeyCode());
    }

    @Override
    public void changeBindingPosition(BindingsPreset preset, KeyBinding binding, float x, float y) {
        binding.changePosition(x, y);
    }

    @Override
    public void addPreset(Device device, BindingsPreset preset) {
        device.addPreset(preset);
    }

    @Override
    public void removePreset(Device device, BindingsPreset preset) {
        device.removePreset(preset.getName());
    }

    @Override
    public void renamePreset(Device device, BindingsPreset preset, String newName) {
        device.changePresetName(preset, newName);
        preset.setName(newName);
    }

    @Override
    public void changeCurrentPreset(Device device, BindingsPreset oldPreset, BindingsPreset newPreset) {
        if (newPreset != null)
            device.setCurrentPresetName(newPreset.getName());
        else
            device.setCurrentPresetName(null);
    }

    @Override
    public void addDevice(Device device) {
        appState.addDevice(device);
    }

    @Override
    public void removeDevice(Device device) {
        appState.removeDevice(device.getSerialNumber());
    }

    @Override
    public void connectDevice(Device device) {
        device.setConnected(true);
    }

    @Override
    public void disconnectDevice(Device device) {
        device.setConnected(false);
    }

    @Override
    public void changeCurrentDevice(Device oldDevice, Device newDevice) {
        if (newDevice != null)
            appState.setCurrentDeviceSerialNumber(newDevice.getSerialNumber());
        else
            appState.setCurrentDeviceSerialNumber(null);
    }

    @Override
    public void pressKey(Device device, int keyCode) { }

    @Override
    public void showError(String error) { }

    @Override
    public void hideMenu() {
        appState.setMenuHidden(true);
    }

    @Override
    public void showMenu() {
        appState.setMenuHidden(false);
    }

    @Override
    public void listenForKey() {
        appState.setListeningForKey(true);
    }

    @Override
    public void stopListeningForKey() {
        appState.setListeningForKey(false);
    }

    @Override
    public void hideServiceGUI() {
        appState.setServiceGUIOpened(false);
    }

    @Override
    public void showServiceGUI() {
        appState.setServiceGUIOpened(true);
    }

    @Override
    public void startService(MIDIMapperAccessibilityService service) { }

    @Override
    public void stopService(MIDIMapperAccessibilityService service) { }

    @Override
    public void startActivity(MIDIMapperActivity activity) { }

    @Override
    public void stopActivity(MIDIMapperActivity activity) { }

    @Override
    public void resumeActivity(MIDIMapperActivity activity) { }
}
