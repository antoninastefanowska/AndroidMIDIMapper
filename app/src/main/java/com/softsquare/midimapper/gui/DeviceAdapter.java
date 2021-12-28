package com.softsquare.midimapper.gui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.softsquare.midimapper.MIDIMapperAccessibilityService;
import com.softsquare.midimapper.R;
import com.softsquare.midimapper.Utilities;
import com.softsquare.midimapper.databinding.LayoutDeviceItemBinding;
import com.softsquare.midimapper.model.BindingsPreset;
import com.softsquare.midimapper.model.MIDIControllerDevice;
import com.softsquare.midimapper.model.Settings;

import java.util.ArrayList;
import java.util.List;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder> {
    private final Settings settings;
    private final List<MIDIControllerDevice> devices;

    public static class DeviceViewHolder extends RecyclerView.ViewHolder {
        public LayoutDeviceItemBinding binding;
        public PresetAdapter presetAdapter;
        public ImageButton removeButton;
        public ImageButton createPresetButton;
        public RadioButton selectDeviceRadioButton;

        public DeviceViewHolder(@NonNull View itemView, LayoutDeviceItemBinding binding, Settings settings) {
            super(itemView);
            this.binding = binding;
            presetAdapter = new PresetAdapter(settings);
            setIsRecyclable(true);

            RecyclerView presetsRecyclerView = itemView.findViewById(R.id.presets_recycler_view);
            presetsRecyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
            presetsRecyclerView.setAdapter(presetAdapter);

            removeButton = itemView.findViewById(R.id.remove_device_button);
            createPresetButton = itemView.findViewById(R.id.create_preset_button);
            selectDeviceRadioButton = itemView.findViewById(R.id.select_device_radio_button);

            ImageButton expandButton = itemView.findViewById(R.id.device_expand_button);
            LinearLayout expandablePanel = itemView.findViewById(R.id.device_expandable_panel);
            expandButton.setOnClickListener(view -> {
                if (expandablePanel.getVisibility() == View.GONE) {
                    expandablePanel.setVisibility(View.VISIBLE);
                    expandButton.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_baseline_arrow_drop_up_24));
                } else {
                    expandablePanel.setVisibility(View.GONE);
                    expandButton.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_baseline_arrow_drop_down_24));
                }
            });
        }
    }

    public DeviceAdapter(Settings settings) {
        this.settings = settings;
        devices = new ArrayList<>(settings.getDevices().values());
    }

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        LayoutDeviceItemBinding binding = LayoutDeviceItemBinding.inflate(inflater, parent, false);
        View itemView = binding.getRoot();
        Utilities.setEnabledForGroup(Utilities.isServiceEnabled(context), (ViewGroup)itemView);
        return new DeviceViewHolder(itemView, binding, settings);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {
        MIDIControllerDevice device = devices.get(position);
        holder.binding.setDevice(device);
        holder.binding.executePendingBindings();
        holder.presetAdapter.setDevice(device);
        holder.removeButton.setOnClickListener((View view) -> remove(position));
        holder.createPresetButton.setOnClickListener((View view) -> createPreset(holder, device));

        holder.selectDeviceRadioButton.setChecked(device.getSerialNumber().equals(settings.getCurrentDeviceSerialNumber()));
        holder.selectDeviceRadioButton.setEnabled(device.isConnected());
        holder.selectDeviceRadioButton.setOnCheckedChangeListener((buttonView, isChecked) -> setSelected(device, isChecked));
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    private void setSelected(MIDIControllerDevice device, boolean checked) {
        if (checked) {
            int prevPosition = devices.indexOf(settings.getCurrentDevice());
            settings.setCurrentDeviceSerialNumber(device.getSerialNumber());
            notifyItemChanged(prevPosition);
            updateConfigurationView();
        }
    }

    public void deviceConnectionChanged(MIDIControllerDevice device) {
        int position = devices.indexOf(device);
        notifyItemChanged(position);
    }

    private void createPreset(DeviceViewHolder holder, MIDIControllerDevice device) {
        BindingsPreset preset = device.createNewPreset();
        holder.presetAdapter.add(preset);
    }

    private void remove(int position) {
        MIDIControllerDevice device = devices.remove(position);
        settings.removeDevice(device.getSerialNumber());
        notifyItemRemoved(position);
        if (settings.getCurrentDeviceSerialNumber() == null)
            updateConfigurationView();
    }

    private void updateConfigurationView() {
        MIDIMapperAccessibilityService service = MIDIMapperAccessibilityService.getInstance();
        if (service != null)
            service.updateConfigurationView();
    }
}
