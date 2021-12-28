package com.softsquare.midimapper.gui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.softsquare.midimapper.R;
import com.softsquare.midimapper.databinding.LayoutBindingItemBinding;
import com.softsquare.midimapper.databinding.LayoutPresetItemBinding;
import com.softsquare.midimapper.model.BindingsPreset;
import com.softsquare.midimapper.model.KeyBinding;

import java.util.ArrayList;
import java.util.List;

public class BindingAdapter extends RecyclerView.Adapter<BindingAdapter.BindingViewHolder> {
    private List<KeyBinding> bindings = new ArrayList<>();

    public static class BindingViewHolder extends RecyclerView.ViewHolder {
        LayoutBindingItemBinding binding;

        public BindingViewHolder(@NonNull View itemView, LayoutBindingItemBinding binding) {
            super(itemView);
            this.binding = binding;
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setPreset(BindingsPreset preset) {
        bindings = new ArrayList<>(preset.getBindings().values());
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BindingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        LayoutBindingItemBinding binding = LayoutBindingItemBinding.inflate(inflater, parent, false);
        return new BindingViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BindingViewHolder holder, int position) {
        KeyBinding binding = bindings.get(position);
        holder.binding.setBinding(binding);
        holder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return bindings.size();
    }
}
