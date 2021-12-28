package com.softsquare.midimapper.gui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.softsquare.midimapper.MIDIMapperAccessibilityService;
import com.softsquare.midimapper.R;
import com.softsquare.midimapper.Utilities;
import com.softsquare.midimapper.databinding.LayoutPresetItemBinding;
import com.softsquare.midimapper.model.BindingsPreset;
import com.softsquare.midimapper.model.MIDIControllerDevice;
import com.softsquare.midimapper.model.Settings;

import java.util.ArrayList;
import java.util.List;

public class PresetAdapter extends RecyclerView.Adapter<PresetAdapter.PresetViewHolder> {
    private MIDIControllerDevice device;
    private List<BindingsPreset> presets = new ArrayList<>();
    private Settings settings;

    public static class PresetViewHolder extends RecyclerView.ViewHolder {
        public LayoutPresetItemBinding binding;
        public BindingAdapter bindingAdapter;
        public ImageButton removeButton;
        public ImageButton changeNameButton;
        public RadioButton selectPresetRadioButton;
        public TextView presetNameTextView;
        public EditText presetNameEditText;
        public boolean editMode = false;

        public PresetViewHolder(@NonNull View itemView, LayoutPresetItemBinding binding) {
            super(itemView);
            this.binding = binding;
            bindingAdapter = new BindingAdapter();
            setIsRecyclable(true);

            RecyclerView bindingsRecyclerView = itemView.findViewById(R.id.bindings_recycler_view);
            bindingsRecyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
            bindingsRecyclerView.setAdapter(bindingAdapter);

            removeButton = itemView.findViewById(R.id.remove_preset_button);
            changeNameButton = itemView.findViewById(R.id.edit_preset_name_button);
            selectPresetRadioButton = itemView.findViewById(R.id.select_preset_radio_button);
            presetNameTextView = itemView.findViewById(R.id.preset_name_text_view);
            presetNameEditText = itemView.findViewById(R.id.preset_name_edit_text);

            ImageButton expandButton = itemView.findViewById(R.id.preset_expand_button);
            LinearLayout expandablePanel = itemView.findViewById(R.id.preset_expandable_panel);
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

    public PresetAdapter(Settings settings) {
        this.settings = settings;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setDevice(MIDIControllerDevice device) {
        this.device = device;
        presets = new ArrayList<>(device.getPresets().values());
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PresetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        LayoutPresetItemBinding binding = LayoutPresetItemBinding.inflate(inflater, parent, false);
        View itemView = binding.getRoot();
        Utilities.setEnabledForGroup(Utilities.isServiceEnabled(context), (ViewGroup)itemView);
        return new PresetViewHolder(itemView, binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PresetViewHolder holder, int position) {
        BindingsPreset preset = presets.get(position);
        holder.binding.setPreset(preset);
        holder.binding.executePendingBindings();
        holder.bindingAdapter.setPreset(preset);
        holder.removeButton.setOnClickListener(view -> remove(position));

        holder.selectPresetRadioButton.setChecked(preset.getName().equals(device.getCurrentPresetName()));
        holder.selectPresetRadioButton.setOnCheckedChangeListener((buttonView, isChecked) -> setSelected(preset, isChecked));
        holder.changeNameButton.setOnClickListener(view -> {
            if (!holder.editMode)
                startNameChange(holder);
            else
                finishNameChange(preset, holder);
        });
        holder.presetNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (device.presetNameExists(s.toString()) && !preset.getName().equals(s.toString()))
                    holder.presetNameEditText.setError("Preset name already exists");
                else
                    holder.presetNameEditText.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    @Override
    public int getItemCount() {
        return presets.size();
    }

    public void add(BindingsPreset preset) {
        presets.add(preset);
        notifyItemInserted(getItemCount() - 1);
    }

    private void setSelected(BindingsPreset preset, boolean checked) {
        if (checked) {
            int prevPosition = presets.indexOf(device.getCurrentPreset());
            device.setCurrentPresetName(preset.getName());
            notifyItemChanged(prevPosition);

            if (device.getSerialNumber().equals(settings.getCurrentDeviceSerialNumber()))
                updateConfigurationView();
        }
    }

    private void remove(int position) {
        BindingsPreset preset = presets.remove(position);
        device.removePreset(preset.getName());
        notifyItemRemoved(position);

        if (device.getSerialNumber().equals(settings.getCurrentDeviceSerialNumber()) && device.getCurrentPreset() == null)
            updateConfigurationView();
    }

    private void startNameChange(PresetViewHolder holder) {
        Context context = holder.itemView.getContext();
        holder.changeNameButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_done_24));
        holder.presetNameTextView.setVisibility(View.GONE);
        holder.presetNameEditText.setVisibility(View.VISIBLE);
        holder.editMode = true;
    }

    private void finishNameChange(BindingsPreset preset, PresetViewHolder holder) {
        int position = presets.indexOf(preset);
        Context context = holder.itemView.getContext();
        String newName = holder.presetNameEditText.getText().toString();
        holder.changeNameButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_edit_24));
        holder.presetNameTextView.setVisibility(View.VISIBLE);
        holder.presetNameEditText.setVisibility(View.GONE);
        device.changePresetName(preset, newName);
        holder.editMode = false;
        notifyItemChanged(position);

        if (device.getSerialNumber().equals(settings.getCurrentDeviceSerialNumber()) && device.getCurrentPresetName().equals(newName))
            updateConfigurationView();
    }

    private void updateConfigurationView() {
        MIDIMapperAccessibilityService service = MIDIMapperAccessibilityService.getInstance();
        if (service != null)
            service.updateConfigurationView();
    }
}
