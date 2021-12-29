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

public class AppActionPerformer implements IActionPerformer {
    private static AppActionPerformer instance;
    private final List<IActionPerformer> listeners = new ArrayList<>();
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

    public KeyBinding createBinding(BindingsPreset preset, int keyCode) {
        KeyBinding binding = new KeyBinding(keyCode, 0.0f, 0.0f);
        addBinding(preset, binding);
        return binding;
    }

    public BindingsPreset createPreset(Device device) {
        String baseName = "preset", name;
        int suffix = 0;
        do {
            name = baseName + suffix;
            suffix++;
        } while (device.getPresets().containsKey(name));

        BindingsPreset preset = new BindingsPreset(name);
        addPreset(device, preset);
        return preset;
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
        for (IActionPerformer listener : listeners)
            listener.loadSettings(appState, context);

        showMenu();
    }

    @Override
    public void addBinding(BindingsPreset preset, KeyBinding binding) {
        for (IActionPerformer listener : listeners)
            listener.addBinding(preset, binding);
    }

    @Override
    public void removeBinding(BindingsPreset preset, KeyBinding binding) {
        for (IActionPerformer listener : listeners)
            listener.removeBinding(preset, binding);
    }

    @Override
    public void changeBindingPosition(BindingsPreset preset, KeyBinding binding, float x, float y) {
        for (IActionPerformer listener : listeners)
            listener.changeBindingPosition(preset, binding, x, y);
    }

    @Override
    public void addPreset(Device device, BindingsPreset preset) {
        for (IActionPerformer listener : listeners)
            listener.addPreset(device, preset);

        if (device != null) {
            BindingsPreset currentPreset = device.getCurrentPreset();
            if (currentPreset == null)
                changeCurrentPreset(device, null, preset);
        }
    }

    @Override
    public void removePreset(Device device, BindingsPreset preset) {
        for (IActionPerformer listener : listeners)
            listener.removePreset(device, preset);

        if (device != null) {
            BindingsPreset currentPreset = device.getCurrentPreset();
            if (currentPreset == null)
                changeCurrentPreset(device, preset, null);
        }
    }

    @Override
    public void renamePreset(Device device, BindingsPreset preset, String newName) {
        for (IActionPerformer listener : listeners)
            listener.renamePreset(device, preset, newName);

        Device currentDevice = appState.getCurrentDevice();
        if (currentDevice != null) {
            BindingsPreset currentPreset = currentDevice.getCurrentPreset();
            if (currentPreset == null)
                changeCurrentPreset(device, null, preset);
        }
    }

    @Override
    public void changeCurrentPreset(Device device, BindingsPreset oldPreset, BindingsPreset newPreset) {
        for (IActionPerformer listener : listeners)
            listener.changeCurrentPreset(device, oldPreset, newPreset);
    }

    @Override
    public void addDevice(Device device) {
        for (IActionPerformer listener : listeners)
            listener.addDevice(device);
    }

    @Override
    public void removeDevice(Device device) {
        for (IActionPerformer listener : listeners)
            listener.removeDevice(device);
    }

    @Override
    public void connectDevice(Device device) {
        for (IActionPerformer listener : listeners)
            listener.connectDevice(device);

        if (appState.getCurrentDeviceSerialNumber() == null)
            changeCurrentDevice(null, device);
    }

    @Override
    public void disconnectDevice(Device device) {
        for (IActionPerformer listener : listeners)
            listener.disconnectDevice(device);

        Device currentDevice = appState.getCurrentDevice();
        if (device == currentDevice)
            changeCurrentDevice(device, null);
    }

    @Override
    public void changeCurrentDevice(Device oldDevice, Device newDevice) {
        for (IActionPerformer listener : listeners)
            listener.changeCurrentDevice(oldDevice, newDevice);
    }

    @Override
    public void pressKey(Device device, int keyCode) {
        for (IActionPerformer listener : listeners)
            listener.pressKey(device, keyCode);
    }

    @Override
    public void showError(String error) {
        for (IActionPerformer listener : listeners)
            listener.showError(error);
    }

    @Override
    public void hideMenu() {
        if (!appState.isMenuHidden())
            for (IActionPerformer listener : listeners)
                listener.hideMenu();
    }

    @Override
    public void showMenu() {
        if (appState.isMenuHidden())
            for (IActionPerformer listener : listeners)
                listener.showMenu();
    }

    @Override
    public void listenForKey() {
        for (IActionPerformer listener : listeners)
            listener.listenForKey();
    }

    @Override
    public void stopListeningForKey() {
        for (IActionPerformer listener : listeners)
            listener.stopListeningForKey();
    }

    @Override
    public void hideServiceGUI() {
        for (IActionPerformer listener : listeners)
            listener.hideServiceGUI();
    }

    @Override
    public void showServiceGUI() {
        for (IActionPerformer listener : listeners)
            listener.showServiceGUI();
    }

    @Override
    public void startService(MIDIMapperAccessibilityService service) {
        AppStateRepository.getInstance(service).getSettingsAsync(settings -> loadSettings(settings, service));

        for (IActionPerformer listener : listeners)
            listener.startService(service);
    }

    @Override
    public void stopService(MIDIMapperAccessibilityService service) {
        for (IActionPerformer listener : listeners)
            listener.stopService(service);
    }

    @Override
    public void startActivity(MIDIMapperActivity activity) {
        AppStateRepository.getInstance(activity).getSettingsAsync(settings -> loadSettings(settings, activity));

        for (IActionPerformer listener : listeners)
            listener.startActivity(activity);
    }

    @Override
    public void stopActivity(MIDIMapperActivity activity) {
        for (IActionPerformer listener : listeners)
            listener.stopActivity(activity);
    }

    @Override
    public void resumeActivity(MIDIMapperActivity activity) {
        for (IActionPerformer listener : listeners)
            listener.resumeActivity(activity);
    }
}
