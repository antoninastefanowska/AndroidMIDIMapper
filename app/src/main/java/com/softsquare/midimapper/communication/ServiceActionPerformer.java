package com.softsquare.midimapper.communication;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Pair;

import com.softsquare.midimapper.activity.MIDIMapperActivity;
import com.softsquare.midimapper.model.BindingsPreset;
import com.softsquare.midimapper.model.Device;
import com.softsquare.midimapper.model.KeyBinding;
import com.softsquare.midimapper.model.AppState;
import com.softsquare.midimapper.service.MIDIDeviceConnector;
import com.softsquare.midimapper.service.MIDIMapperAccessibilityService;
import com.softsquare.midimapper.service.ServiceGUI;

public class ServiceActionPerformer implements IActionPerformer {
    private static ServiceActionPerformer instance;

    private MIDIMapperAccessibilityService service;
    private ServiceGUI serviceGUI;
    private MIDIDeviceConnector deviceController;
    private AppState appState;

    private AppActionPerformer actionPerformer;

    private ServiceActionPerformer() { }

    public static ServiceActionPerformer getInstance() {
        if (instance == null)
            instance = new ServiceActionPerformer();
        return instance;
    }

    @Override
    public void loadSettings(AppState appState, Context context) {
        this.appState = appState;
        this.actionPerformer = AppActionPerformer.getInstance(context);

        if (serviceGUI == null && service != null)
            new Handler(Looper.getMainLooper()).post(() -> serviceGUI = new ServiceGUI(context, appState));
        if (deviceController == null)
            deviceController = new MIDIDeviceConnector(context, appState);
    }

    @Override
    public void addBinding(BindingsPreset preset, KeyBinding binding) {
        if (appState != null) {
            Device currentDevice = appState.getCurrentDevice();
            if (currentDevice != null) {
                BindingsPreset currentPreset = currentDevice.getCurrentPreset();
                if (preset == currentPreset && serviceGUI != null)
                    new Handler(Looper.getMainLooper()).post(() -> serviceGUI.createNewMarker(binding));
            }
        }
    }

    @Override
    public void removeBinding(BindingsPreset preset, KeyBinding binding) {
        if (appState != null) {
            Device currentDevice = appState.getCurrentDevice();
            if (currentDevice != null) {
                BindingsPreset currentPreset = currentDevice.getCurrentPreset();
                if (preset == currentPreset && serviceGUI != null)
                    serviceGUI.updateViews(); // TODO: Simplify to marker removal only.
            }
        }
    }

    @Override
    public void changeBindingPosition(BindingsPreset preset, KeyBinding binding, float x, float y) { }

    @Override
    public void addPreset(Device device, BindingsPreset preset) { }

    @Override
    public void removePreset(Device device, BindingsPreset preset) { }

    @Override
    public void renamePreset(Device device, BindingsPreset preset, String newName) {
        if (appState != null) {
            Device currentDevice = appState.getCurrentDevice();
            if (currentDevice != null) {
                BindingsPreset currentPreset = currentDevice.getCurrentPreset();
                if (currentPreset == preset && serviceGUI != null)
                    serviceGUI.updateViews(); // TODO: Simplify to label update only.
            }
        }
    }

    @Override
    public void changeCurrentPreset(Device device, BindingsPreset oldPreset, BindingsPreset newPreset) {
        if (appState != null) {
            Device currentDevice = appState.getCurrentDevice();
            if (device == currentDevice && serviceGUI != null)
                serviceGUI.updateViews();
        }
    }

    @Override
    public void addDevice(Device device) { }

    @Override
    public void removeDevice(Device device) { }

    @Override
    public void connectDevice(Device device) { }

    @Override
    public void disconnectDevice(Device device) { }

    @Override
    public void changeCurrentDevice(Device oldDevice, Device newDevice) {
        if (serviceGUI != null)
            serviceGUI.updateViews();
    }

    @Override
    public void pressKey(Device device, int keyCode) {
        if (appState != null) {
            KeyBinding keyBinding = device.getCurrentPreset().getBinding(keyCode);

            if (service != null && keyBinding != null && (appState.isMenuHidden() || serviceGUI.isTapReady())) {
                Pair<Float, Float> tapPosition = serviceGUI.getTapPosition(keyBinding);
                service.dispatchTapAt(tapPosition.first, tapPosition.second);
            } else if (actionPerformer != null && serviceGUI != null && appState.isListeningForKey() && keyBinding == null)
                actionPerformer.createBinding(device.getCurrentPreset(), keyCode);

            if (serviceGUI != null && appState.isServiceGUIOpened())
                new Handler(Looper.getMainLooper()).post(() -> serviceGUI.makeRipple(keyCode));
        }
    }

    @Override
    public void showError(String error) {
        if (serviceGUI != null)
            serviceGUI.showError(error);
    }

    @Override
    public void hideMenu() {
        if (serviceGUI != null)
            serviceGUI.hideMenuWidget();
    }

    @Override
    public void showMenu() {
        if (serviceGUI != null)
            serviceGUI.showMenuWidget();
    }

    @Override
    public void listenForKey() {
        if (serviceGUI != null)
            serviceGUI.startListeningForKey();
    }

    @Override
    public void stopListeningForKey() {
        if (serviceGUI != null)
            serviceGUI.stopListeningForKey();
    }

    @Override
    public void hideServiceGUI() {
        if (serviceGUI != null)
            serviceGUI.hideLayout();
    }

    @Override
    public void showServiceGUI() {
        if (serviceGUI != null)
            serviceGUI.showLayout();
    }

    @Override
    public void startService(MIDIMapperAccessibilityService service) {
        this.service = service;
    }

    @Override
    public void stopService(MIDIMapperAccessibilityService service) {
        this.service = null;
        this.serviceGUI = null;
    }

    @Override
    public void startActivity(MIDIMapperActivity activity) { }

    @Override
    public void stopActivity(MIDIMapperActivity activity) { }

    @Override
    public void resumeActivity(MIDIMapperActivity activity) { }
}
