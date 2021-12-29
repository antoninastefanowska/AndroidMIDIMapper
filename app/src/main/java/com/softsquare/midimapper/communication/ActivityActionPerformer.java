package com.softsquare.midimapper.communication;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.softsquare.midimapper.activity.MIDIMapperActivity;
import com.softsquare.midimapper.activity.adapters.BindingAdapter;
import com.softsquare.midimapper.activity.adapters.DeviceAdapter;
import com.softsquare.midimapper.activity.adapters.PresetAdapter;
import com.softsquare.midimapper.model.AppState;
import com.softsquare.midimapper.model.BindingsPreset;
import com.softsquare.midimapper.model.Device;
import com.softsquare.midimapper.model.KeyBinding;
import com.softsquare.midimapper.service.MIDIMapperAccessibilityService;

public class ActivityActionPerformer implements IActionPerformer {
    private MIDIMapperActivity activity;
    private AppState appState;
    private DeviceAdapter deviceAdapter;

    private static ActivityActionPerformer instance;

    private ActivityActionPerformer() { }

    public static ActivityActionPerformer getInstance() {
        if (instance == null)
            instance = new ActivityActionPerformer();
        return instance;
    }

    @Override
    public void loadSettings(AppState appState, Context context) {
        this.appState = appState;
        AppActionPerformer actionPerformer = AppActionPerformer.getInstance(context);
        if (activity != null) {
            new Handler(Looper.getMainLooper()).post(() -> {
                activity.initializeAdapters(appState, actionPerformer);
                activity.updateViews(appState);
                deviceAdapter = activity.getDeviceAdapter();
            });
        }
    }

    @Override
    public void addBinding(BindingsPreset preset, KeyBinding binding) {
        if (activity != null && deviceAdapter != null) {
            Device parentDevice = null;
            for (Device device : appState.getDevices().values()) {
                if (device.getPresets().containsKey(preset.getName())) {
                    parentDevice = device;
                    break;
                }
            }
            PresetAdapter presetAdapter = deviceAdapter.findPresetAdapter(parentDevice);
            if (presetAdapter != null) {
                BindingAdapter bindingAdapter = presetAdapter.findBindingAdapter(preset);
                if (bindingAdapter != null)
                    new Handler(Looper.getMainLooper()).post(() -> bindingAdapter.addItem(binding));
            }
        }
    }

    @Override
    public void removeBinding(BindingsPreset preset, KeyBinding binding) {
        if (activity != null && deviceAdapter != null) {
            Device parentDevice = null;
            for (Device device : appState.getDevices().values()) {
                if (device.getPresets().containsKey(preset.getName())) {
                    parentDevice = device;
                    break;
                }
            }
            PresetAdapter presetAdapter = deviceAdapter.findPresetAdapter(parentDevice);
            if (presetAdapter != null) {
                BindingAdapter bindingAdapter = presetAdapter.findBindingAdapter(preset);
                if (bindingAdapter != null)
                    new Handler(Looper.getMainLooper()).post(() -> bindingAdapter.removeItem(binding));
            }
        }
    }

    @Override
    public void changeBindingPosition(BindingsPreset preset, KeyBinding binding, float x, float y) {
        if (activity != null && deviceAdapter != null) {
            Device parentDevice = null;
            for (Device device : appState.getDevices().values()) {
                if (device.getPresets().containsKey(preset.getName())) {
                    parentDevice = device;
                    break;
                }
            }
            PresetAdapter presetAdapter = deviceAdapter.findPresetAdapter(parentDevice);
            if (presetAdapter != null) {
                BindingAdapter bindingAdapter = presetAdapter.findBindingAdapter(preset);
                if (bindingAdapter != null)
                    new Handler(Looper.getMainLooper()).post(() -> bindingAdapter.updateItem(binding));
            }
        }
    }

    @Override
    public void addPreset(Device device, BindingsPreset preset) {
        if (activity != null && deviceAdapter != null) {
            PresetAdapter presetAdapter = deviceAdapter.findPresetAdapter(device);
            if (presetAdapter != null)
                new Handler(Looper.getMainLooper()).post(() -> presetAdapter.addItem(preset));
        }
    }

    @Override
    public void removePreset(Device device, BindingsPreset preset) {
        if (activity != null && deviceAdapter != null) {
            PresetAdapter presetAdapter = deviceAdapter.findPresetAdapter(device);
            if (presetAdapter != null)
                new Handler(Looper.getMainLooper()).post(() -> presetAdapter.removeItem(preset));
        }
    }

    @Override
    public void renamePreset(Device device, BindingsPreset preset, String newName) {
        if (activity != null && deviceAdapter != null) {
            PresetAdapter presetAdapter = deviceAdapter.findPresetAdapter(device);
            if (presetAdapter != null)
                new Handler(Looper.getMainLooper()).post(() -> presetAdapter.updateItem(preset));
        }
    }

    @Override
    public void changeCurrentPreset(Device device, BindingsPreset oldPreset, BindingsPreset newPreset) {
        if (activity != null && deviceAdapter != null) {
            PresetAdapter presetAdapter = deviceAdapter.findPresetAdapter(device);
            if (presetAdapter != null) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (oldPreset != newPreset)
                        presetAdapter.updateItem(oldPreset);
                    presetAdapter.updateItem(newPreset);
                });
            }
        }
    }

    @Override
    public void addDevice(Device device) {
        if (activity != null && deviceAdapter != null)
            new Handler(Looper.getMainLooper()).post(() -> deviceAdapter.addItem(device));
    }

    @Override
    public void removeDevice(Device device) {
        if (activity != null && deviceAdapter != null)
            new Handler(Looper.getMainLooper()).post(() -> deviceAdapter.removeItem(device));
    }

    @Override
    public void connectDevice(Device device) {
        if (activity != null && deviceAdapter != null)
            new Handler(Looper.getMainLooper()).post(() -> deviceAdapter.updateItem(device));
    }

    @Override
    public void disconnectDevice(Device device) {
        if (activity != null && deviceAdapter != null)
            new Handler(Looper.getMainLooper()).post(() -> deviceAdapter.updateItem(device));
    }

    @Override
    public void changeCurrentDevice(Device oldDevice, Device newDevice) {
        if (activity != null && deviceAdapter != null) {
            new Handler(Looper.getMainLooper()).post(() -> {
                if (oldDevice != newDevice)
                    deviceAdapter.updateItem(oldDevice);
                deviceAdapter.updateItem(newDevice);
            });
        }
    }

    @Override
    public void pressKey(Device device, int keyCode) { }

    @Override
    public void showError(String error) { }

    @Override
    public void hideMenu() {
        if (activity != null)
            new Handler(Looper.getMainLooper()).post(() -> activity.onMenuHidden());
    }

    @Override
    public void showMenu() {
        if (activity != null)
            new Handler(Looper.getMainLooper()).post(() -> activity.onMenuShown());
    }

    @Override
    public void listenForKey() { }

    @Override
    public void stopListeningForKey() { }

    @Override
    public void hideServiceGUI() { }

    @Override
    public void showServiceGUI() { }

    @Override
    public void startService(MIDIMapperAccessibilityService service) {
        if (activity != null && appState != null)
            activity.updateViews(appState);
    }

    @Override
    public void stopService(MIDIMapperAccessibilityService service) {
        if (activity != null && appState != null)
            activity.updateViews(appState);
    }

    @Override
    public void startActivity(MIDIMapperActivity activity) {
        if (this.activity != null && this.activity != activity)
            this.activity.finish();
        this.activity = activity;
        if (appState != null)
            this.activity.updateViews(appState);
    }

    @Override
    public void stopActivity(MIDIMapperActivity activity) {
        this.activity = null;
    }

    @Override
    public void resumeActivity(MIDIMapperActivity activity) {
        this.activity = activity;
        if (appState != null)
            this.activity.updateViews(appState);
    }
}
