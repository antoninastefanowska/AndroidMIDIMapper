package com.softsquare.midimapper.service;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.Display;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sa90.materialarcmenu.ArcMenu;
import com.sa90.materialarcmenu.StateChangeListener;

import com.softsquare.midimapper.activity.MIDIMapperActivity;
import com.softsquare.midimapper.communication.AppActionPerformer;
import com.softsquare.midimapper.model.BindingsPreset;
import com.softsquare.midimapper.model.Device;
import com.softsquare.midimapper.model.KeyBinding;
import com.softsquare.midimapper.model.AppState;
import com.softsquare.midimapper.databinding.LayoutMarkerViewBinding;
import com.softsquare.midimapper.R;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.WINDOW_SERVICE;

import androidx.core.content.ContextCompat;

public class ServiceGUI {
    private final AppActionPerformer actionPerformer;
    private final AppState appState;
    private final Context context;

    private WindowManager windowManager;
    private WindowManager.LayoutParams mainParams;
    private WindowManager.LayoutParams menuParams;
    private LayoutInflater inflater;

    private FrameLayout configLayout;
    private FrameLayout mainLayout;
    private FrameLayout menuLayout;
    private FrameLayout markersContainer;
    private FloatingActionButton menuOptionAdd;
    private final Map<Integer, View> markerViews = new HashMap<>();

    private Animation blinkAnimation;

    private View.OnTouchListener draggableMarkerListener;

    private boolean tapReady = true;

    public ServiceGUI(Context context, AppState appState) {
        actionPerformer = AppActionPerformer.getInstance(context);
        this.appState = appState;
        this.context = context;
        createLayout();
        updateViews();
    }

    private void createLayout() {
        windowManager = (WindowManager)context.getSystemService(WINDOW_SERVICE);
        mainLayout = new FrameLayout(context);
        mainParams = new WindowManager.LayoutParams();

        inflater = LayoutInflater.from(context);

        mainParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        mainParams.type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY;
        mainParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        mainParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        mainParams.format = PixelFormat.TRANSLUCENT;
        mainParams.gravity = Gravity.TOP | Gravity.START;

        inflater.inflate(R.layout.layout_configuration, mainLayout);
        createMenu();
        showMenuWidget();

        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);

        configLayout = mainLayout.findViewById(R.id.config_layout);
        markersContainer = mainLayout.findViewById(R.id.markers_container);
        FloatingActionButton trashBin = mainLayout.findViewById(R.id.trash_bin);

        blinkAnimation = AnimationUtils.loadAnimation(context, R.anim.blink_animation);

        TextView deviceNameTextView = configLayout.findViewById(R.id.name_textview);
        TextView presetNameTextView = configLayout.findViewById(R.id.preset_textview);
        deviceNameTextView.setSelected(true);
        presetNameTextView.setSelected(true);

        draggableMarkerListener = (view, event) -> {
            view.performClick();
            ClipData dragData = new ClipData(
                    (String)view.getTag(),
                    new String[] { ClipDescription.MIMETYPE_TEXT_PLAIN },
                    new ClipData.Item((String)view.getTag())
            );
            view.setVisibility(View.GONE);
            view.startDragAndDrop(
                    dragData,
                    new View.DragShadowBuilder(view),
                    null,
                    0);
            return true;
        };
        markersContainer.setOnDragListener((view, event) -> {
            if (event.getAction() == DragEvent.ACTION_DROP) {
                ClipData data = event.getClipData();
                String tag = data.getItemAt(0).getText().toString();
                int keyCode = Integer.parseInt(tag);
                View markerView = markerViews.get(keyCode);

                if (markerView != null) {
                    markerView.setX(event.getX() - markerView.getWidth() / 2.0f);
                    markerView.setY(event.getY() - markerView.getHeight() / 2.0f);
                    markerView.setVisibility(View.VISIBLE);

                    BindingsPreset preset = appState.getCurrentDevice().getCurrentPreset();
                    KeyBinding binding = preset.getBinding(keyCode);
                    actionPerformer.changeBindingPosition(preset, binding, event.getX(), event.getY());
                }
            }
            return true;
        });
        trashBin.setOnDragListener((view, event) -> {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_ENTERED:
                    view.animate()
                            .scaleX(1.5f)
                            .scaleY(1.5f)
                            .setInterpolator(new BounceInterpolator())
                            .setDuration(100);
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    view.animate()
                            .scaleX(1.0f)
                            .scaleY(1.0f)
                            .setInterpolator(new BounceInterpolator())
                            .setDuration(100);
                    break;
                case DragEvent.ACTION_DROP:
                    ClipData data = event.getClipData();
                    String tag = data.getItemAt(0).getText().toString();
                    int keyCode = Integer.parseInt(tag);
                    View markerView = markerViews.remove(keyCode);

                    if (markerView != null) {
                        markersContainer.removeView(markerView);
                        BindingsPreset preset = appState.getCurrentDevice().getCurrentPreset();
                        KeyBinding binding = preset.getBinding(keyCode);
                        actionPerformer.removeBinding(preset, binding);
                    }
                    view.animate()
                            .scaleX(1.0f)
                            .scaleY(1.0f)
                            .setInterpolator(new BounceInterpolator())
                            .setDuration(100);
                    break;

            }
            return true;
        });
    }

    private void createMenu() {
        menuLayout = new FrameLayout(context);

        menuParams = new WindowManager.LayoutParams();
        menuParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        menuParams.type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY;
        menuParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        menuParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        menuParams.format = PixelFormat.TRANSLUCENT;
        menuParams.gravity = Gravity.BOTTOM | Gravity.START;

        inflater.inflate(R.layout.layout_menu, menuLayout);

        ArcMenu menu = menuLayout.findViewById(R.id.menu_widget);
        menu.setStateChangeListener(new StateChangeListener() {
            @Override
            public void onMenuOpened() {
                actionPerformer.showServiceGUI();
            }

            @Override
            public void onMenuClosed() {
                actionPerformer.hideServiceGUI();
            }
        });
        menuOptionAdd = menu.findViewById(R.id.menu_button_add);
        menuOptionAdd.setOnClickListener(v -> {
            if (!appState.isListeningForKey() &&
                 appState.getCurrentDevice() != null &&
                 appState.getCurrentDevice().getCurrentPreset() != null)
                actionPerformer.listenForKey();
            else
                actionPerformer.stopListeningForKey();
        });
        FloatingActionButton menuOptionOpenSettings = menu.findViewById(R.id.menu_button_open_settings);
        menuOptionOpenSettings.setOnClickListener(v -> {
            Intent intent = new Intent(context, MIDIMapperActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            context.startActivity(intent);
        });
        FloatingActionButton menuOptionHideWidget = menu.findViewById(R.id.menu_button_hide_widget);
        menuOptionHideWidget.setOnClickListener(v -> {
            actionPerformer.hideMenu();
            Toast.makeText(context, context.getString(R.string.layout_hidden_message), Toast.LENGTH_SHORT).show();
        });
    }

    public void hideMenuWidget() {
        if (mainLayout.getWindowToken() != null && menuLayout.getWindowToken() != null) {
            windowManager.removeView(mainLayout);
            windowManager.removeView(menuLayout);
        }
    }

    public void showMenuWidget() {
        if (mainLayout.getWindowToken() == null && menuLayout.getWindowToken() == null) {
            windowManager.addView(mainLayout, mainParams);
            windowManager.addView(menuLayout, menuParams);
        }
    }

    public void updateViews() {
        updateMarkers();
        updateLabels();
    }

    public Pair<Float, Float> getLayoutPosition() {
        int[] position = new int[2];
        markersContainer.getLocationOnScreen(position);
        float x = position[0];
        float y = position[1];
        return new Pair<>(x, y);
    }

    public void hideLayout() {
        configLayout.animate()
                .alpha(0.0f)
                .setDuration(250)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);

                        mainParams.flags |= WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
                        windowManager.updateViewLayout(mainLayout, mainParams);

                        configLayout.clearAnimation();
                        configLayout.setVisibility(View.GONE);
                        configLayout.setEnabled(false);
                        tapReady = true;
                    }
                });
    }

    public void showLayout() {
        tapReady = false;
        configLayout.animate()
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);

                        mainParams.flags &= ~WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
                        windowManager.updateViewLayout(mainLayout, mainParams);

                        configLayout.clearAnimation();
                        configLayout.setVisibility(View.VISIBLE);
                        configLayout.setEnabled(true);
                    }
                })
                .alpha(1.0f)
                .setDuration(250);
    }

    public void createNewMarker(KeyBinding binding) {
        if (!markerViews.containsKey(binding.getKeyCode())) {
            LayoutMarkerViewBinding dataBinding = LayoutMarkerViewBinding.inflate(inflater, markersContainer, false);
            View markerView = dataBinding.getRoot();
            dataBinding.setBinding(binding);

            markerView.setTag(String.valueOf(binding.getKeyCode()));
            markerView.setOnTouchListener(draggableMarkerListener);

            markerView.setX(binding.getX() - markerView.getWidth() / 2.0f);
            markerView.setY(binding.getY() - markerView.getHeight() / 2.0f);

            markersContainer.addView(markerView);
            markerViews.put(binding.getKeyCode(), markerView);
        }
        stopListeningForKey();
    }

    private void updateMarkers() {
        Device device = appState.getCurrentDevice();
        markersContainer.removeAllViews();
        markerViews.clear();
        if (device != null) {
            BindingsPreset preset = device.getCurrentPreset();
            if (preset != null)
                for (Map.Entry<Integer, KeyBinding> entry : preset.getBindings().entrySet())
                    createNewMarker(entry.getValue());
        }
    }

    public void updateLabels() {
        TextView deviceNameTextView = configLayout.findViewById(R.id.name_textview);
        TextView presetNameTextView = configLayout.findViewById(R.id.preset_textview);
        Device device = appState.getCurrentDevice();

        if (device != null) {
            deviceNameTextView.setText(context.getString(R.string.header, device.getName(), device.getManufacturer()));
            presetNameTextView.setText(device.getCurrentPresetName());
            hideError();
        } else {
            deviceNameTextView.setText("");
            presetNameTextView.setText("");
            showError(context.getString(R.string.device_not_detected));
        }
    }

    public void showError(String message) {
        TextView deviceNameTextView = configLayout.findViewById(R.id.name_textview);
        TextView presetNameTextView = configLayout.findViewById(R.id.preset_textview);
        TextView messageTextView = configLayout.findViewById(R.id.message_textview);

        deviceNameTextView.setText("");
        presetNameTextView.setText("");
        messageTextView.setText(message);
        messageTextView.startAnimation(blinkAnimation);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        dialogBuilder.setTitle(context.getString(R.string.error));
        dialogBuilder.setMessage(message);
        dialogBuilder.setNeutralButton(R.string.confirm, (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = dialogBuilder.create();
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY);
        dialog.show();
    }

    public void hideError() {
        TextView messageTextView = configLayout.findViewById(R.id.message_textview);
        messageTextView.setText("");
        messageTextView.clearAnimation();
    }

    public void makeRipple(int keyCode) {
        View markerView = markerViews.get(keyCode);
        if (markerView != null) {
            ImageView rippleView = markerView.findViewById(R.id.marker_ripple);

            rippleView.animate().cancel();
            rippleView.setScaleX(0.0f);
            rippleView.setScaleY(0.0f);
            rippleView.setAlpha(0.5f);

            rippleView.animate()
                    .scaleX(1.0f)
                    .scaleY(1.0f)
                    .setDuration(200)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            rippleView.animate()
                                    .alpha(0.0f)
                                    .setDuration(100)
                                    .setListener(new AnimatorListenerAdapter() {
                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            super.onAnimationEnd(animation);
                                            rippleView.setScaleX(0.0f);
                                            rippleView.setScaleY(0.0f);
                                            rippleView.setAlpha(0.5f);
                                        }
                                    });
                        }
                    });
        }
    }

    public void startListeningForKey() {
        TextView messageTextView = configLayout.findViewById(R.id.message_textview);
        messageTextView.setText(R.string.press_key);
        messageTextView.startAnimation(blinkAnimation);
        menuOptionAdd.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_close_24));
    }

    public void stopListeningForKey() {
        TextView messageTextView = configLayout.findViewById(R.id.message_textview);
        menuOptionAdd.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_add_24));
        messageTextView.setText("");
        messageTextView.clearAnimation();
        blinkAnimation.cancel();
        blinkAnimation.reset();
    }

    public boolean isTapReady() {
        return tapReady;
    }
}
