package com.softsquare.midimapper.communication;

import android.content.Context;
import android.media.midi.MidiDeviceInfo;

import com.softsquare.midimapper.activity.MIDIMapperActivity;
import com.softsquare.midimapper.database.repositories.AppStateRepository;
import com.softsquare.midimapper.model.BindingsPreset;
import com.softsquare.midimapper.model.Device;
import com.softsquare.midimapper.model.KeyBinding;
import com.softsquare.midimapper.model.AppState;
import com.softsquare.midimapper.service.MIDIMapperAccessibilityService;

import java.util.ArrayList;
import java.util.List;

public class AppActionPerformer implements
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
        Actions.IConnectDeviceAction,
        Actions.IDisconnectDeviceAction,
        Actions.IChangeCurrentDeviceAction,
        Actions.IPressKeyAction,
        Actions.IShowErrorAction,
        Actions.IHideMenuAction,
        Actions.IShowMenuAction,
        Actions.IListenForKeyAction,
        Actions.IStopListeningForKeyAction,
        Actions.IHideServiceGUIAction,
        Actions.IShowServiceGUIAction,
        Actions.IStartServiceAction,
        Actions.IStopServiceAction,
        Actions.IStartActivityAction,
        Actions.IStopActivityAction,
        Actions.IResumeActivityAction
{
    private static AppActionPerformer instance;
    private final List<Actions.IAction> listeners = new ArrayList<>();
    private AppState appState;

    private AppActionPerformer(Context context) {
        listeners.add(ModelActionPerformer.getInstance());
        listeners.add(DatabaseActionPerformer.getInstance(context));
        listeners.add(ServiceActionPerformer.getInstance());
        listeners.add(ActivityActionPerformer.getInstance());
    }

    public static AppActionPerformer getInstance(Context context) {
        if (instance == null)
            instance = new AppActionPerformer(context);
        return instance;
    }

    public void createBinding(BindingsPreset preset, int keyCode) {
        KeyBinding binding = new KeyBinding(keyCode, 0.0f, 0.0f);
        addBinding(preset, binding);
    }

    public void createPreset(Device device) {
        String baseName = "preset", name;
        int suffix = 0;
        do {
            name = baseName + suffix;
            suffix++;
        } while (device.getPresets().containsKey(name));

        BindingsPreset preset = new BindingsPreset(name);
        addPreset(device, preset);
    }

    public Device createDevice(MidiDeviceInfo deviceInfo) {
        Device device = new Device(deviceInfo);
        addDevice(device);
        createPreset(device);
        return device;
    }

    @Override
    public void loadSettings(AppState appState, Context context) {
        this.appState = appState;
        for (Actions.IAction listener : listeners)
            if (listener instanceof Actions.ILoadSettingsAction)
                ((Actions.ILoadSettingsAction) listener).loadSettings(appState, context);

        showMenu();
    }

    @Override
    public void addBinding(BindingsPreset preset, KeyBinding binding) {
        for (Actions.IAction listener : listeners)
            if (listener instanceof Actions.IAddBindingAction)
                ((Actions.IAddBindingAction) listener).addBinding(preset, binding);
    }

    @Override
    public void removeBinding(BindingsPreset preset, KeyBinding binding) {
        for (Actions.IAction listener : listeners)
            if (listener instanceof Actions.IRemoveBindingAction)
                ((Actions.IRemoveBindingAction) listener).removeBinding(preset, binding);
    }

    @Override
    public void changeBindingPosition(BindingsPreset preset, KeyBinding binding, float x, float y) {
        for (Actions.IAction listener : listeners)
            if (listener instanceof Actions.IChangeBindingPositionAction)
                ((Actions.IChangeBindingPositionAction) listener).changeBindingPosition(preset, binding, x, y);
    }

    @Override
    public void addPreset(Device device, BindingsPreset preset) {
        for (Actions.IAction listener : listeners)
            if (listener instanceof Actions.IAddPresetAction)
                ((Actions.IAddPresetAction) listener).addPreset(device, preset);

        if (device != null) {
            BindingsPreset currentPreset = device.getCurrentPreset();
            if (currentPreset == null)
                changeCurrentPreset(device, null, preset);
        }
    }

    @Override
    public void removePreset(Device device, BindingsPreset preset) {
        for (Actions.IAction listener : listeners)
            if (listener instanceof Actions.IRemovePresetAction)
                ((Actions.IRemovePresetAction) listener).removePreset(device, preset);

        if (device != null) {
            BindingsPreset currentPreset = device.getCurrentPreset();
            if (currentPreset == null)
                changeCurrentPreset(device, preset, null);
        }
    }

    @Override
    public void renamePreset(Device device, BindingsPreset preset, String newName) {
        for (Actions.IAction listener : listeners)
            if (listener instanceof Actions.IRenamePresetAction)
                ((Actions.IRenamePresetAction) listener).renamePreset(device, preset, newName);

        Device currentDevice = appState.getCurrentDevice();
        if (currentDevice != null) {
            BindingsPreset currentPreset = currentDevice.getCurrentPreset();
            if (currentPreset == null)
                changeCurrentPreset(device, null, preset);
        }
    }

    @Override
    public void changeCurrentPreset(Device device, BindingsPreset oldPreset, BindingsPreset newPreset) {
        for (Actions.IAction listener : listeners)
            if (listener instanceof Actions.IChangeCurrentPresetAction)
                ((Actions.IChangeCurrentPresetAction) listener).changeCurrentPreset(device, oldPreset, newPreset);
    }

    @Override
    public void addDevice(Device device) {
        for (Actions.IAction listener : listeners)
            if (listener instanceof Actions.IAddDeviceAction)
                ((Actions.IAddDeviceAction) listener).addDevice(device);
    }

    @Override
    public void removeDevice(Device device) {
        for (Actions.IAction listener : listeners)
            if (listener instanceof Actions.IRemoveDeviceAction)
                ((Actions.IRemoveDeviceAction) listener).removeDevice(device);
    }

    @Override
    public void connectDevice(Device device) {
        for (Actions.IAction listener : listeners)
            if (listener instanceof Actions.IConnectDeviceAction)
                ((Actions.IConnectDeviceAction) listener).connectDevice(device);

        if (appState.getCurrentDeviceSerialNumber() == null)
            changeCurrentDevice(null, device);
    }

    @Override
    public void disconnectDevice(Device device) {
        for (Actions.IAction listener : listeners)
            if (listener instanceof Actions.IDisconnectDeviceAction)
                ((Actions.IDisconnectDeviceAction) listener).disconnectDevice(device);

        Device currentDevice = appState.getCurrentDevice();
        if (device == currentDevice)
            changeCurrentDevice(device, null);
    }

    @Override
    public void changeCurrentDevice(Device oldDevice, Device newDevice) {
        for (Actions.IAction listener : listeners)
            if (listener instanceof Actions.IChangeCurrentDeviceAction)
                ((Actions.IChangeCurrentDeviceAction) listener).changeCurrentDevice(oldDevice, newDevice);
    }

    @Override
    public void pressKey(Device device, int keyCode) {
        for (Actions.IAction listener : listeners)
            if (listener instanceof Actions.IPressKeyAction)
                ((Actions.IPressKeyAction) listener).pressKey(device, keyCode);
    }

    @Override
    public void showError(String error) {
        for (Actions.IAction listener : listeners)
            if (listener instanceof Actions.IShowErrorAction)
                ((Actions.IShowErrorAction) listener).showError(error);
    }

    @Override
    public void hideMenu() {
        if (!appState.isMenuHidden())
            for (Actions.IAction listener : listeners)
                if (listener instanceof Actions.IHideMenuAction)
                    ((Actions.IHideMenuAction) listener).hideMenu();
    }

    @Override
    public void showMenu() {
        if (appState.isMenuHidden())
            for (Actions.IAction listener : listeners)
                if (listener instanceof Actions.IShowMenuAction)
                    ((Actions.IShowMenuAction) listener).showMenu();
    }

    @Override
    public void listenForKey() {
        for (Actions.IAction listener : listeners)
            if (listener instanceof Actions.IListenForKeyAction)
                ((Actions.IListenForKeyAction) listener).listenForKey();
    }

    @Override
    public void stopListeningForKey() {
        for (Actions.IAction listener : listeners)
            if (listener instanceof Actions.IStopListeningForKeyAction)
                ((Actions.IStopListeningForKeyAction) listener).stopListeningForKey();
    }

    @Override
    public void hideServiceGUI() {
        for (Actions.IAction listener : listeners)
            if (listener instanceof Actions.IHideServiceGUIAction)
                ((Actions.IHideServiceGUIAction) listener).hideServiceGUI();
    }

    @Override
    public void showServiceGUI() {
        for (Actions.IAction listener : listeners)
            if (listener instanceof Actions.IShowServiceGUIAction)
                ((Actions.IShowServiceGUIAction) listener).showServiceGUI();
    }

    @Override
    public void startService(MIDIMapperAccessibilityService service) {
        AppStateRepository.getInstance(service).getSettingsAsync(settings -> loadSettings(settings, service));

        for (Actions.IAction listener : listeners)
            if (listener instanceof Actions.IStartServiceAction)
                ((Actions.IStartServiceAction) listener).startService(service);
    }

    @Override
    public void stopService(MIDIMapperAccessibilityService service) {
        for (Actions.IAction listener : listeners)
            if (listener instanceof Actions.IStopServiceAction)
                ((Actions.IStopServiceAction) listener).stopService(service);
    }

    @Override
    public void startActivity(MIDIMapperActivity activity) {
        AppStateRepository.getInstance(activity).getSettingsAsync(settings -> loadSettings(settings, activity));

        for (Actions.IAction listener : listeners)
            if (listener instanceof Actions.IStartActivityAction)
                ((Actions.IStartActivityAction) listener).startActivity(activity);
    }

    @Override
    public void stopActivity(MIDIMapperActivity activity) {
        for (Actions.IAction listener : listeners)
            if (listener instanceof Actions.IStopActivityAction)
                ((Actions.IStopActivityAction) listener).stopActivity(activity);
    }

    @Override
    public void resumeActivity(MIDIMapperActivity activity) {
        for (Actions.IAction listener : listeners)
            if (listener instanceof Actions.IResumeActivityAction)
                ((Actions.IResumeActivityAction) listener).resumeActivity(activity);
    }
}
