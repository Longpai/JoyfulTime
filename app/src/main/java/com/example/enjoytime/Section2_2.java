package com.example.enjoytime;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Section2_2 extends AppCompatActivity {
    private final Map<Integer, Integer> seatHeatingColorCounters = new HashMap<>();
    private final int[] seatHeatingColors = {
            Color.parseColor("#E0E0E0"), // Default Gray
            Color.parseColor("#7678ed"),
            Color.parseColor("#f7b801"),
            Color.parseColor("#f18701"),
            Color.parseColor("#f35b04")
    };
    private List<TextView> massageOptions;
    private List<TextView> massageIntensityOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_section22);

        setupNavigation();
        setupBackNavigation();
        setupSeatOptions();
        setupSeatHeating();
        setupMassageOptions();
        setupMassageIntensityOptions();
        setupMassageSeatOptions();
    }

    private void setupMassageOptions() {
        massageOptions = new ArrayList<>();
        massageOptions.add(findViewById(R.id.massage_option_wave));
        massageOptions.add(findViewById(R.id.massage_option_pulse));
        massageOptions.add(findViewById(R.id.massage_option_knead));
        massageOptions.add(findViewById(R.id.massage_option_off));

        for (TextView option : massageOptions) {
            option.setOnClickListener(v -> {
                for (TextView otherOption : massageOptions) {
                    otherOption.setSelected(false);
                    otherOption.setBackgroundResource(R.drawable.bg_option_unselected);
                    otherOption.setTextColor(ContextCompat.getColor(this, R.color.default_text_color));
                }
                v.setSelected(true);
                v.setBackgroundResource(R.drawable.bg_option_selected);
                ((TextView) v).setTextColor(Color.WHITE);
            });
        }

        // Set initial selection
        massageOptions.get(1).setSelected(true);
        massageOptions.get(1).setBackgroundResource(R.drawable.bg_option_selected);
        massageOptions.get(1).setTextColor(Color.WHITE);
    }

    private void setupMassageIntensityOptions() {
        massageIntensityOptions = new ArrayList<>();
        massageIntensityOptions.add(findViewById(R.id.massage_intensity_light));
        massageIntensityOptions.add(findViewById(R.id.massage_intensity_medium));
        massageIntensityOptions.add(findViewById(R.id.massage_intensity_heavy));

        for (TextView option : massageIntensityOptions) {
            option.setOnClickListener(v -> {
                for (TextView otherOption : massageIntensityOptions) {
                    otherOption.setSelected(false);
                    otherOption.setBackgroundResource(R.drawable.bg_option_unselected);
                    otherOption.setTextColor(ContextCompat.getColor(this, R.color.default_text_color));
                }
                v.setSelected(true);
                v.setBackgroundResource(R.drawable.bg_option_selected);
                ((TextView) v).setTextColor(Color.WHITE);
            });
        }

        // Set initial selection
        massageIntensityOptions.get(1).setSelected(true);
        massageIntensityOptions.get(1).setBackgroundResource(R.drawable.bg_option_selected);
        massageIntensityOptions.get(1).setTextColor(Color.WHITE);
    }

    private void setupMassageSeatOptions() {
        TextView massageSeatDriver = findViewById(R.id.massage_seat_driver);
        TextView massageSeatPassenger = findViewById(R.id.massage_seat_passenger);
        TextView massageSeatRearLeft = findViewById(R.id.massage_seat_rear_left);
        TextView massageSeatRearRight = findViewById(R.id.massage_seat_rear_right);

        View.OnClickListener seatClickListener = v -> v.setSelected(!v.isSelected());

        massageSeatDriver.setOnClickListener(seatClickListener);
        massageSeatPassenger.setOnClickListener(seatClickListener);
        massageSeatRearLeft.setOnClickListener(seatClickListener);
        massageSeatRearRight.setOnClickListener(seatClickListener);
    }


    private void setupSeatHeating() {
        int[] seatHeatingIds = {
                R.id.seat_heating_driver,
                R.id.seat_heating_passenger,
                R.id.seat_heating_rear_left,
                R.id.seat_heating_rear_center,
                R.id.seat_heating_rear_right
        };

        for (int id : seatHeatingIds) {
            seatHeatingColorCounters.put(id, 0);
            TextView seatHeatingView = findViewById(id);
            seatHeatingView.setBackgroundColor(seatHeatingColors[0]); // Set initial color
            seatHeatingView.setOnClickListener(v -> {
                Integer counter = seatHeatingColorCounters.get(v.getId());
                if (counter != null) {
                    counter = (counter + 1) % seatHeatingColors.length;
                    v.setBackgroundColor(seatHeatingColors[counter]);
                    seatHeatingColorCounters.put(v.getId(), counter);
                }
            });
        }
    }

    private void setupSeatOptions() {
        TextView seatOptionDriver = findViewById(R.id.seat_option_driver);
        TextView seatOptionPassenger = findViewById(R.id.seat_option_passenger);
        TextView seatOptionRearLeft = findViewById(R.id.seat_option_rear_left);
        TextView seatOptionRearRight = findViewById(R.id.seat_option_rear_right);

        View.OnClickListener seatClickListener = v -> v.setSelected(!v.isSelected());

        seatOptionDriver.setOnClickListener(seatClickListener);
        seatOptionPassenger.setOnClickListener(seatClickListener);
        seatOptionRearLeft.setOnClickListener(seatClickListener);
        seatOptionRearRight.setOnClickListener(seatClickListener);
    }

    private void setupBackNavigation() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(Section2_2.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }
        });
    }

    private void setupNavigation() {
        // Find views
        View btnRest = findViewById(R.id.view_section_2);
        View btnMusic = findViewById(R.id.view_section21);
        View btnSeat = findViewById(R.id.view_section22);
        View btnLight = findViewById(R.id.view_section23);

        // "Rest"
        if (btnRest != null) {
            btnRest.setOnClickListener(v -> {
                Intent intent = new Intent(Section2_2.this, SectionTwoActivity.class);
                startActivity(intent);
            });
        }

        // "Music"
        if (btnMusic != null) {
            btnMusic.setOnClickListener(v -> {
                Intent intent = new Intent(Section2_2.this, Section2_1.class);
                startActivity(intent);
            });
        }

        // "Seat" (Current)
        if (btnSeat != null) {
            // No action
            btnSeat.setOnClickListener(null);
            btnSeat.setClickable(false);
        }

        // "Light"
        if (btnLight != null) {
            btnLight.setOnClickListener(v -> {
                Intent intent = new Intent(Section2_2.this, Section2_3.class);
                startActivity(intent);
            });
        }
    }
}