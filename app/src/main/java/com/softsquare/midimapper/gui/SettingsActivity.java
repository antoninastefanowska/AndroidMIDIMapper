package com.softsquare.midimapper.gui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.softsquare.midimapper.MIDIMapperAccessibilityService;
import com.softsquare.midimapper.R;
import com.softsquare.midimapper.Utilities;
import com.softsquare.midimapper.database.repositories.BindingsPresetRepository;
import com.softsquare.midimapper.database.repositories.KeyBindingRepository;
import com.softsquare.midimapper.database.repositories.MIDIControllerDeviceRepository;
import com.softsquare.midimapper.database.repositories.SettingsRepository;
import com.softsquare.midimapper.model.MIDIControllerDevice;
import com.softsquare.midimapper.model.Settings;

public class SettingsActivity extends AppCompatActivity {
    private LinearLayout settingViewsLayout;
    private LinearLayout devicesExpandablePanel;
    private ImageButton devicesExpandButton;
    private Settings settings;
    private SwitchCompat menuVisibilitySwitch;
    private DeviceAdapter deviceAdapter;

    private static SettingsActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        KeyBindingRepository.createInstance(this);
        BindingsPresetRepository.createInstance(this);
        MIDIControllerDeviceRepository.createInstance(this);
        SettingsRepository.createInstance(this);

        settingViewsLayout = findViewById(R.id.settings_views_layout);
        Utilities.setEnabledForGroup(Utilities.isServiceEnabled(this), settingViewsLayout);

        devicesExpandablePanel = findViewById(R.id.devices_expandable_panel);
        devicesExpandButton = findViewById(R.id.devices_expand_button);
        menuVisibilitySwitch = findViewById(R.id.menu_visibility_switch);
        menuVisibilitySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> visibilitySwitchChanged(isChecked));

        instance = this;
        SettingsRepository.getInstance().getSettingsAsync(settings -> {
            RecyclerView devicesRecyclerView = findViewById(R.id.devices_recycler_view);
            devicesRecyclerView.setLayoutManager(new LinearLayoutManager(instance));
            deviceAdapter = new DeviceAdapter(settings);
            devicesRecyclerView.setAdapter(deviceAdapter);
            this.settings = settings;
            updateViews();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        instance = null;
    }

    public void deviceConnectionChanged(MIDIControllerDevice device) {
        if (deviceAdapter != null)
            deviceAdapter.deviceConnectionChanged(device);
    }

    public static SettingsActivity getInstance() {
        return instance;
    }

    public void expandPanel(View view) {
        if (devicesExpandablePanel.getVisibility() == View.GONE) {
            devicesExpandablePanel.setVisibility(View.VISIBLE);
            devicesExpandButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_arrow_drop_up_24));
        } else {
            devicesExpandablePanel.setVisibility(View.GONE);
            devicesExpandButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_arrow_drop_down_24));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Utilities.setEnabledForGroup(Utilities.isServiceEnabled(this), settingViewsLayout);
        updateViews();
    }

    public void updateViews() {
        if (settings != null)
            menuVisibilitySwitch.setChecked(!settings.isHidden());
    }

    public void openAccessibilitySettings(View view) {
        Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivity(intent);
    }

    public void visibilitySwitchChanged(boolean isChecked) {
        if (settings != null) {
            settings.setHidden(!isChecked);
            MIDIMapperAccessibilityService service = MIDIMapperAccessibilityService.getInstance();
            if (service != null) {
                if (isChecked)
                    service.showMenuWidget();
                else
                    service.hideMenuWidget();
            }
        }
    }
}