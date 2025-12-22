package com.example.enjoytime;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Section2_3 extends AppCompatActivity {

    private List<TextView> rhythmOptions;
    private List<View> colorOptions;
    private Map<View, Integer> originalColorMap = new HashMap<>();
    private View lightControlsContainer;
    private View mainContainer;
    private TextView brightnessValue;
    private int selectedColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_section23);

        setupNavigation();
        setupBackNavigation();
        setupLightControls();
    }

    private void setupLightControls() {
        Switch lightSwitch = findViewById(R.id.light_switch);
        lightControlsContainer = findViewById(R.id.right_content);
        mainContainer = findViewById(R.id.main);
        lightControlsContainer.setBackgroundColor(Color.TRANSPARENT); // Make right content transparent

        SeekBar colorTempSeekBar = findViewById(R.id.color_temp_seekbar);
        SeekBar brightnessSeekBar = findViewById(R.id.brightness_seekbar);
        brightnessValue = findViewById(R.id.brightness_value);

        // Rhythm options
        rhythmOptions = new ArrayList<>();
        rhythmOptions.add(findViewById(R.id.rhythm_option_static));
        rhythmOptions.add(findViewById(R.id.rhythm_option_breath));
        rhythmOptions.add(findViewById(R.id.rhythm_option_rhythm));

        for (TextView option : rhythmOptions) {
            option.setOnClickListener(v -> {
                for (TextView otherOption : rhythmOptions) {
                    otherOption.setSelected(false);
                    otherOption.setBackgroundResource(R.drawable.bg_option_unselected);
                    otherOption.setTextColor(ContextCompat.getColor(this, R.color.default_text_color));
                }
                v.setSelected(true);
                v.setBackgroundResource(R.drawable.bg_option_selected);
                ((TextView) v).setTextColor(Color.WHITE);
            });
        }

        rhythmOptions.get(0).setSelected(true);
        rhythmOptions.get(0).setBackgroundResource(R.drawable.bg_option_selected);
        rhythmOptions.get(0).setTextColor(Color.WHITE);

        // Color options
        colorOptions = new ArrayList<>();
        colorOptions.add(findViewById(R.id.color_option_white));
        colorOptions.add(findViewById(R.id.color_option_1));
        colorOptions.add(findViewById(R.id.color_option_2));
        colorOptions.add(findViewById(R.id.color_option_3));
        colorOptions.add(findViewById(R.id.color_option_4));
        colorOptions.add(findViewById(R.id.color_option_5));

        for (View option : colorOptions) {
            originalColorMap.put(option, ((ColorDrawable) option.getBackground()).getColor());
            option.setOnClickListener(v -> {
                for (View otherOption : colorOptions) {
                    // Reset background color for unselected options
                    otherOption.setBackgroundColor(originalColorMap.get(otherOption));
                }
                v.setBackgroundResource(R.drawable.color_option_border); // Highlight selected
                selectedColor = originalColorMap.get(v);
                if (lightSwitch.isChecked()) {
                    updateBackgroundColor(brightnessSeekBar.getProgress(), colorTempSeekBar.getProgress());
                }
            });
        }

        // Set initial selection for color
        colorOptions.get(0).setBackgroundResource(R.drawable.color_option_border);
        selectedColor = Color.WHITE;


        // Switch listener
        lightSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Enable/disable and fade controls
            for (int i = 2; i < ((android.view.ViewGroup) lightControlsContainer).getChildCount(); i++) {
                View child = ((android.view.ViewGroup) lightControlsContainer).getChildAt(i);
                child.setAlpha(isChecked ? 1.0f : 0.5f);
                child.setEnabled(isChecked);
                setClickable(child, isChecked);
            }

            if (isChecked) {
                updateBackgroundColor(brightnessSeekBar.getProgress(), colorTempSeekBar.getProgress());
            } else {
                mainContainer.setBackgroundColor(Color.parseColor("#F5F5F5"));
            }
        });

        // Seekbar listeners
        colorTempSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (lightSwitch.isChecked()) {
                    updateBackgroundColor(brightnessSeekBar.getProgress(), progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        brightnessSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                brightnessValue.setText(progress + "%");
                if (lightSwitch.isChecked()) {
                    updateBackgroundColor(progress, colorTempSeekBar.getProgress());
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });
        
        // Set initial background color if switch is on
        if (lightSwitch.isChecked()) {
            updateBackgroundColor(brightnessSeekBar.getProgress(), colorTempSeekBar.getProgress());
        } else {
             mainContainer.setBackgroundColor(Color.parseColor("#F5F5F5"));
        }
    }
    
    private void updateBackgroundColor(int brightness, int colorTemp) {
        float[] hsv = new float[3];
        Color.colorToHSV(selectedColor, hsv);
        hsv[2] = brightness / 100.0f;
        
        // Adjust saturation based on color temperature
        // As colorTemp (0-100) increases, saturation (0-1) decreases
        hsv[1] = hsv[1] * (1.0f - (colorTemp / 200.0f)); // Reducing the effect of temperature change

        mainContainer.setBackgroundColor(Color.HSVToColor(hsv));
    }

    private void setClickable(View view, boolean clickable) {
        if (view instanceof android.view.ViewGroup) {
            android.view.ViewGroup group = (android.view.ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                setClickable(group.getChildAt(i), clickable);
            }
        }
        view.setClickable(clickable);
    }

    private void setupBackNavigation() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(Section2_3.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }
        });
    }

    private void setupNavigation() {
        View btnRest = findViewById(R.id.view_section_2);
        View btnMusic = findViewById(R.id.view_section21);
        View btnSeat = findViewById(R.id.view_section22);
        View btnLight = findViewById(R.id.view_section23);

        if (btnRest != null) {
            btnRest.setOnClickListener(v -> {
                Intent intent = new Intent(Section2_3.this, SectionTwoActivity.class);
                startActivity(intent);
            });
        }

        if (btnMusic != null) {
            btnMusic.setOnClickListener(v -> {
                Intent intent = new Intent(Section2_3.this, Section2_1.class);
                startActivity(intent);
            });
        }

        if (btnSeat != null) {
            btnSeat.setOnClickListener(v -> {
                Intent intent = new Intent(Section2_3.this, Section2_2.class);
                startActivity(intent);
            });
        }

        if (btnLight != null) {
            btnLight.setOnClickListener(null);
            btnLight.setClickable(false);
        }
    }
}