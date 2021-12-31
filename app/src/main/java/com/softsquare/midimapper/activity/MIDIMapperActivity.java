package com.softsquare.midimapper.activity;

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

import com.softsquare.midimapper.communication.AppActionPerformer;
import com.softsquare.midimapper.model.AppState;
import com.softsquare.midimapper.service.MIDIMapperAccessibilityService;
import com.softsquare.midimapper.R;
import com.softsquare.midimapper.activity.adapters.DeviceAdapter;

public class MIDIMapperActivity extends AppCompatActivity {
    private LinearLayout settingViewsLayout;
    private LinearLayout devicesExpandablePanel;
    private ImageButton devicesExpandButton;
    private SwitchCompat menuVisibilitySwitch;
    private DeviceAdapter deviceAdapter;
    private AppActionPerformer actionPerformer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_midi_mapper);

        settingViewsLayout = findViewById(R.id.settings_views_layout);

        devicesExpandablePanel = findViewById(R.id.devices_expandable_panel);
        devicesExpandButton = findViewById(R.id.devices_expand_button);
        menuVisibilitySwitch = findViewById(R.id.menu_visibility_switch);
        menuVisibilitySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> visibilitySwitchChanged(isChecked));

        actionPerformer = AppActionPerformer.getInstance(this);
        actionPerformer.startActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        actionPerformer.stopActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        actionPerformer.resumeActivity(this);
    }

    public void initializeAdapters(AppState appState, AppActionPerformer actionPerformer) {
        RecyclerView devicesRecyclerView = findViewById(R.id.devices_recycler_view);
        devicesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        deviceAdapter = new DeviceAdapter(appState, actionPerformer, devicesRecyclerView);
        devicesRecyclerView.setAdapter(deviceAdapter);
    }

    public DeviceAdapter getDeviceAdapter() {
        return deviceAdapter;
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

    public void openAccessibilitySettings(View view) {
        Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivity(intent);
    }

    public void visibilitySwitchChanged(boolean isChecked) {
        if (isChecked)
            actionPerformer.showMenu();
        else
            actionPerformer.hideMenu();
    }

    public void onMenuShown() {
        menuVisibilitySwitch.setChecked(true);
    }

    public void onMenuHidden() {
        menuVisibilitySwitch.setChecked(false);
    }

    public void updateViews(AppState appState) {
        menuVisibilitySwitch.setChecked(!appState.isMenuHidden());
        ViewUtilities.setEnabledForGroup(MIDIMapperAccessibilityService.isServiceEnabled(this), settingViewsLayout);
    }
}