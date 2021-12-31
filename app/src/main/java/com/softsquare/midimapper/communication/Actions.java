package com.softsquare.midimapper.communication;

import android.content.Context;

import com.softsquare.midimapper.activity.MIDIMapperActivity;
import com.softsquare.midimapper.model.AppState;
import com.softsquare.midimapper.model.BindingsPreset;
import com.softsquare.midimapper.model.Device;
import com.softsquare.midimapper.model.KeyBinding;
import com.softsquare.midimapper.service.MIDIMapperAccessibilityService;

public abstract class Actions {
    public interface IAction { }

    public interface ILoadSettingsAction extends IAction {
        void loadSettings(AppState appState, Context context);
    }

    public interface IAddBindingAction extends IAction {
        void addBinding(BindingsPreset preset, KeyBinding binding);
    }

    public interface IRemoveBindingAction extends IAction {
        void removeBinding(BindingsPreset preset, KeyBinding binding);
    }

    public interface IChangeBindingPositionAction extends IAction {
        void changeBindingPosition(BindingsPreset preset, KeyBinding binding, float x, float y);
    }

    public interface IAddPresetAction extends IAction {
        void addPreset(Device device, BindingsPreset preset);
    }

    public interface IRemovePresetAction extends IAction {
        void removePreset(Device device, BindingsPreset preset);
    }

    public interface IRenamePresetAction extends IAction {
        void renamePreset(Device device, BindingsPreset preset, String newName);
    }

    public interface IChangeCurrentPresetAction extends IAction {
        void changeCurrentPreset(Device device, BindingsPreset oldPreset, BindingsPreset newPreset);
    }

    public interface IAddDeviceAction extends IAction {
        void addDevice(Device device);
    }

    public interface IRemoveDeviceAction extends IAction {
        void removeDevice(Device device);
    }

    public interface IConnectDeviceAction extends IAction {
        void connectDevice(Device device);
    }

    public interface IDisconnectDeviceAction extends IAction {
        void disconnectDevice(Device device);
    }

    public interface IChangeCurrentDeviceAction extends IAction {
        void changeCurrentDevice(Device oldDevice, Device newDevice);
    }

    public interface IPressKeyAction extends IAction {
        void pressKey(Device device, int keyCode);
    }

    public interface IShowErrorAction extends IAction {
        void showError(String error);
    }

    public interface IHideMenuAction extends IAction {
        void hideMenu();
    }

    public interface IShowMenuAction extends IAction {
        void showMenu();
    }

    public interface IListenForKeyAction extends IAction {
        void listenForKey();
    }

    public interface IStopListeningForKeyAction extends IAction {
        void stopListeningForKey();
    }

    public interface IHideServiceGUIAction extends IAction {
        void hideServiceGUI();
    }

    public interface IShowServiceGUIAction extends IAction {
        void showServiceGUI();
    }

    public interface IStartServiceAction extends IAction {
        void startService(MIDIMapperAccessibilityService service);
    }

    public interface IStopServiceAction extends IAction {
        void stopService(MIDIMapperAccessibilityService service);
    }

    public interface IStartActivityAction extends IAction {
        void startActivity(MIDIMapperActivity activity);
    }

    public interface IStopActivityAction extends IAction {
        void stopActivity(MIDIMapperActivity activity);
    }

    public interface IResumeActivityAction extends IAction {
        void resumeActivity(MIDIMapperActivity activity);
    }
}
