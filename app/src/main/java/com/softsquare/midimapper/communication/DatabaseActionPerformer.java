package com.softsquare.midimapper.communication;

import android.content.Context;

import com.softsquare.midimapper.database.repositories.BindingsPresetRepository;
import com.softsquare.midimapper.database.repositories.KeyBindingRepository;
import com.softsquare.midimapper.database.repositories.DeviceRepository;
import com.softsquare.midimapper.database.repositories.AppStateRepository;
import com.softsquare.midimapper.model.AppState;
import com.softsquare.midimapper.model.BindingsPreset;
import com.softsquare.midimapper.model.Device;
import com.softsquare.midimapper.model.KeyBinding;

public class DatabaseActionPerformer implements
        Actions.ILoadSettingsAction,
        Actions.IAddBindingAction,
        Actions.IRemoveBindingAction,
        Actions.IChangeBindingPositionAction,
        Actions.IAddPresetAction,
        Actions.IRemovePresetAction,
        Actions.IRenamePresetAction,
        Actions.IChangeCurrentPresetAction,
        Actions.IAddDeviceAction,
        Actions.IRemoveDeviceAction,
        Actions.IChangeCurrentDeviceAction {

    private static DatabaseActionPerformer instance;

    private AppState appState;

    private final KeyBindingRepository bindingRepository;
    private final BindingsPresetRepository presetRepository;
    private final DeviceRepository deviceRepository;
    private final AppStateRepository appStateRepository;

    private DatabaseActionPerformer(Context context) {
        bindingRepository = KeyBindingRepository.getInstance(context);
        presetRepository = BindingsPresetRepository.getInstance(context);
        deviceRepository = DeviceRepository.getInstance(context);
        appStateRepository = AppStateRepository.getInstance(context);
    }

    public static DatabaseActionPerformer getInstance(Context context) {
        if (instance == null)
            instance = new DatabaseActionPerformer(context);
        return instance;
    }

    @Override
    public void loadSettings(AppState appState, Context context) {
        this.appState = appState;
    }

    @Override
    public void addBinding(BindingsPreset preset, KeyBinding binding) {
        bindingRepository.insert(binding);
    }

    @Override
    public void removeBinding(BindingsPreset preset, KeyBinding binding) {
        bindingRepository.delete(binding);
    }

    @Override
    public void changeBindingPosition(BindingsPreset preset, KeyBinding binding, float x, float y) {
        bindingRepository.update(binding);
    }

    @Override
    public void addPreset(Device device, BindingsPreset preset) {
        presetRepository.insert(preset);
    }

    @Override
    public void removePreset(Device device, BindingsPreset preset) {
        presetRepository.delete(preset);
    }

    @Override
    public void renamePreset(Device device, BindingsPreset preset, String newName) {
        presetRepository.update(preset);
    }

    @Override
    public void changeCurrentPreset(Device device, BindingsPreset oldPreset, BindingsPreset newPreset) {
        deviceRepository.update(device);
    }

    @Override
    public void addDevice(Device device) {
        deviceRepository.insert(device);
    }

    @Override
    public void removeDevice(Device device) {
        deviceRepository.delete(device);
    }

    @Override
    public void changeCurrentDevice(Device oldDevice, Device newDevice) {
        appStateRepository.update(appState);
    }
}
