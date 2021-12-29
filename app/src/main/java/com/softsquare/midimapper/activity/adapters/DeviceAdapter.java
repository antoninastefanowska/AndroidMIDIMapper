package com.softsquare.midimapper.activity.adapters;

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

import com.softsquare.midimapper.activity.ViewUtilities;
import com.softsquare.midimapper.communication.AppActionPerformer;
import com.softsquare.midimapper.R;
import com.softsquare.midimapper.model.AppState;
import com.softsquare.midimapper.model.Device;
import com.softsquare.midimapper.databinding.LayoutDeviceItemBinding;
import com.softsquare.midimapper.service.MIDIMapperAccessibilityService;

import java.util.ArrayList;
import java.util.List;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder> {
    private final AppState appState;
    private final List<Device> devices;
    private final RecyclerView recyclerView;
    private final AppActionPerformer actionPerformer;

    public static class DeviceViewHolder extends RecyclerView.ViewHolder {
        public LayoutDeviceItemBinding binding;
        public PresetAdapter presetAdapter;
        public ImageButton removeButton;
        public ImageButton createPresetButton;
        public RadioButton selectDeviceRadioButton;

        public DeviceViewHolder(@NonNull View itemView, LayoutDeviceItemBinding binding, PresetAdapter presetAdapter) {
            super(itemView);
            this.binding = binding;
            this.presetAdapter = presetAdapter;
            setIsRecyclable(true);

            RecyclerView presetsRecyclerView = itemView.findViewById(R.id.presets_recycler_view);
            presetAdapter.setRecyclerView(presetsRecyclerView);
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

    public DeviceAdapter(AppState appState, AppActionPerformer actionPerformer, RecyclerView recyclerView) {
        this.appState = appState;
        this.actionPerformer = actionPerformer;
        this.recyclerView = recyclerView;
        devices = new ArrayList<>(appState.getDevices().values());
    }

    public PresetAdapter findPresetAdapter(Device device) {
        int position = devices.indexOf(device);
        DeviceViewHolder viewHolder = (DeviceViewHolder) recyclerView.findViewHolderForAdapterPosition(position);
        return viewHolder != null ? viewHolder.presetAdapter : null;
    }

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        LayoutDeviceItemBinding binding = LayoutDeviceItemBinding.inflate(inflater, parent, false);
        View itemView = binding.getRoot();
        ViewUtilities.setEnabledForGroup(MIDIMapperAccessibilityService.isServiceEnabled(context), (ViewGroup)itemView);
        PresetAdapter presetAdapter = new PresetAdapter(appState, actionPerformer);
        return new DeviceViewHolder(itemView, binding, presetAdapter);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {
        Device device = devices.get(position);
        holder.binding.setDevice(device);
        holder.binding.executePendingBindings();
        holder.presetAdapter.setDevice(device);
        holder.removeButton.setOnClickListener((View view) -> onRemove(device));
        holder.createPresetButton.setOnClickListener((View view) -> onCreatePreset(device));

        holder.selectDeviceRadioButton.setChecked(device.getSerialNumber().equals(appState.getCurrentDeviceSerialNumber()));
        holder.selectDeviceRadioButton.setEnabled(device.isConnected());
        holder.selectDeviceRadioButton.setOnCheckedChangeListener((buttonView, isChecked) -> setSelected(device, isChecked));
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    public void addItem(Device device) {
        devices.add(device);
        notifyItemInserted(devices.size() - 1);
    }

    public void updateItem(Device device) {
        int position = devices.indexOf(device);
        notifyItemChanged(position);
    }

    public void removeItem(Device device) {
        int position = devices.indexOf(device);
        notifyItemRemoved(position);
    }

    private void setSelected(Device device, boolean checked) {
        if (checked) {
            Device oldDevice = appState.getCurrentDevice();
            actionPerformer.changeCurrentDevice(oldDevice, device);
        }
    }

    private void onCreatePreset(Device device) {
        actionPerformer.createPreset(device);
    }

    private void onRemove(Device device) {
        actionPerformer.removeDevice(device);
    }
}
