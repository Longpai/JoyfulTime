package com.example.enjoytime;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashSet;
import java.util.Set;

public class SectionTwoActivity extends AppCompatActivity {

    private View selectedSceneView = null;
    private Set<View> selectedSeatViews = new HashSet<>();

    private View leftMenuContainer;
    private View napSettingsContainer;
    private View napStartContainer;
    private View napActiveContainer;
    private TextView textCountdown;
    private CountDownTimer napTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_section_two);

        setupNavigation();
        setupBackNavigation();
        setupNumberPicker();
        setupSceneSelection();
        setupSeatSelection();
        setupNapFunctionality();
    }

    private void setupNapFunctionality() {
        leftMenuContainer = findViewById(R.id.left_menu_container);
        napSettingsContainer = findViewById(R.id.nap_settings_container);
        napStartContainer = findViewById(R.id.nap_start_container);
        napActiveContainer = findViewById(R.id.nap_active_container);
        textCountdown = findViewById(R.id.text_countdown);

        Button btnStart = findViewById(R.id.btn_start_nap);
        Button btnStop = findViewById(R.id.btn_stop_nap);

        if (btnStart != null) {
            btnStart.setOnClickListener(v -> startNap());
        }

        if (btnStop != null) {
            btnStop.setOnClickListener(v -> stopNap());
        }
    }

    private void startNap() {
        NumberPicker numberPicker = findViewById(R.id.numberPicker);
        int minutes = 35; // default
        if (numberPicker != null) {
            minutes = numberPicker.getValue() * 5;
        }

        long durationMillis = minutes * 60 * 1000L;

        // Hide other views
        if (leftMenuContainer != null) leftMenuContainer.setVisibility(View.GONE);
        if (napSettingsContainer != null) napSettingsContainer.setVisibility(View.GONE);
        if (napStartContainer != null) napStartContainer.setVisibility(View.GONE);

        // Show active view
        if (napActiveContainer != null) napActiveContainer.setVisibility(View.VISIBLE);

        // Start Timer
        if (napTimer != null) {
            napTimer.cancel();
        }

        napTimer = new CountDownTimer(durationMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (textCountdown != null) {
                    long totalSeconds = millisUntilFinished / 1000;
                    long hours = totalSeconds / 3600;
                    long minutes = (totalSeconds % 3600) / 60;
                    long seconds = totalSeconds % 60;
                    textCountdown.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
                }
            }

            @Override
            public void onFinish() {
                if (textCountdown != null) {
                    textCountdown.setText("00:00:00");
                }
            }
        }.start();
    }

    private void stopNap() {
        if (napTimer != null) {
            napTimer.cancel();
            napTimer = null;
        }

        // Restore views
        if (leftMenuContainer != null) leftMenuContainer.setVisibility(View.VISIBLE);
        if (napSettingsContainer != null) napSettingsContainer.setVisibility(View.VISIBLE);
        if (napStartContainer != null) napStartContainer.setVisibility(View.VISIBLE);

        // Hide active view
        if (napActiveContainer != null) napActiveContainer.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (napTimer != null) {
            napTimer.cancel();
        }
    }

    private void setupSeatSelection() {
        // Find seat buttons
        View seatMainDriver = findViewById(R.id.seat_main_driver);
        View seatCoDriver = findViewById(R.id.seat_co_driver);
        View seatRearLeft = findViewById(R.id.seat_rear_left);
        View seatRearRight = findViewById(R.id.seat_rear_right);

        // Helper method to update seat selection state
        View.OnClickListener seatClickListener = v -> {
            if (selectedSeatViews.contains(v)) {
                selectedSeatViews.remove(v);
                updateSeatViewStyle(v, false);
            } else {
                selectedSeatViews.add(v);
                updateSeatViewStyle(v, true);
            }
        };

        // Assign listeners
        if (seatMainDriver != null) seatMainDriver.setOnClickListener(seatClickListener);
        if (seatCoDriver != null) seatCoDriver.setOnClickListener(seatClickListener);
        if (seatRearLeft != null) seatRearLeft.setOnClickListener(seatClickListener);
        if (seatRearRight != null) seatRearRight.setOnClickListener(seatClickListener);

        // Default selection: Main Driver
        if (seatMainDriver != null) {
            selectedSeatViews.add(seatMainDriver);
            updateSeatViewStyle(seatMainDriver, true);
        }
    }

    private void updateSeatViewStyle(View seatView, boolean isSelected) {
        // Change background
        seatView.setBackgroundResource(isSelected ? R.drawable.rounded_button_bg_selected : R.drawable.rounded_button_bg);

        // We need to change text and icon color based on selection
        // Since the layout structure is LinearLayout -> [ImageView, TextView]
        if (seatView instanceof LinearLayout) {
            LinearLayout container = (LinearLayout) seatView;
            for (int i = 0; i < container.getChildCount(); i++) {
                View child = container.getChildAt(i);
                if (child instanceof ImageView) {
                    ((ImageView) child).setColorFilter(isSelected ? 0xFF333333 : 0xFFFFFFFF); // Dark grey if selected, White if not
                } else if (child instanceof TextView) {
                    ((TextView) child).setTextColor(isSelected ? 0xFF333333 : 0xFFFFFFFF);
                }
            }
        }
    }

    private void setupSceneSelection() {
        // Find the main background container
        final FrameLayout rightContent = findViewById(R.id.right_content);

        // Find scene buttons
        View sceneQuiet = findViewById(R.id.scene_quiet);
        View sceneForest = findViewById(R.id.scene_forest);
        View sceneOcean = findViewById(R.id.scene_ocean);
        View sceneRain = findViewById(R.id.scene_rain);

        // Helper method to update selection state
        View.OnClickListener sceneClickListener = v -> {
            // If the clicked view is already selected, do nothing (or re-apply if needed, but usually redundant)
            if (selectedSceneView == v) {
                return;
            }

            // Deselect previous
            if (selectedSceneView != null) {
                selectedSceneView.setBackgroundResource(R.drawable.rounded_image_bg);
            }

            // Select new
            selectedSceneView = v;
            selectedSceneView.setBackgroundResource(R.drawable.rounded_image_bg_selected);

            // Update background based on selection
            if (v.getId() == R.id.scene_quiet) {
                if (rightContent != null) rightContent.setBackgroundResource(R.drawable.bg_quiet);
            } else if (v.getId() == R.id.scene_forest) {
                if (rightContent != null) rightContent.setBackgroundResource(R.drawable.bg_forest);
            } else if (v.getId() == R.id.scene_ocean) {
                if (rightContent != null) rightContent.setBackgroundResource(R.drawable.bg_ocean);
            } else if (v.getId() == R.id.scene_rain) {
                if (rightContent != null) rightContent.setBackgroundResource(R.drawable.bg_rain);
            }
        };

        // Assign listeners
        if (sceneQuiet != null) sceneQuiet.setOnClickListener(sceneClickListener);
        if (sceneForest != null) sceneForest.setOnClickListener(sceneClickListener);
        if (sceneOcean != null) sceneOcean.setOnClickListener(sceneClickListener);
        if (sceneRain != null) sceneRain.setOnClickListener(sceneClickListener);

        // Set default selection (Quiet)
        if (sceneQuiet != null) {
            // Manually trigger selection logic for default
            selectedSceneView = sceneQuiet;
            selectedSceneView.setBackgroundResource(R.drawable.rounded_image_bg_selected);
            // Background is already set to quiet in XML layout by default
        }
    }

    private void setupNumberPicker() {
        NumberPicker numberPicker = findViewById(R.id.numberPicker);
        // 找到界面上用来显示提示文字的 TextView
        final TextView endTimeText = findViewById(R.id.text_end_time);

        if (numberPicker != null) {
            numberPicker.setMinValue(1);
            numberPicker.setMaxValue(12);

            // 设置显示的数组： ["5", "10", ... "60"]
            String[] displayedValues = new String[12];
            for (int i = 0; i < 12; i++) {
                displayedValues[i] = String.valueOf((i + 1) * 5);
            }
            numberPicker.setDisplayedValues(displayedValues);

            // 设置默认值为 35 (索引 7，对应 7*5=35)
            numberPicker.setValue(7);

            // 如果 TextView 存在，才绑定监听器，防止闪退
            if (endTimeText != null) {
                // 1. 设置初始文本 (基于默认值 7)
                int initialMinutes = numberPicker.getValue() * 5;
                endTimeText.setText(String.format("将于 %d分钟 后结束小憩模式", initialMinutes));

                // 2. 添加滚动监听器：当数值变化时更新文本
                numberPicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
                    // newVal 是当前选中的数值 (1-12)
                    int selectedMinutes = newVal * 5;
                    endTimeText.setText(String.format("将于 %d分钟 后结束小憩模式", selectedMinutes));
                });
            }
        }
    }

    private void setupBackNavigation() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // If nap is active, stop it first? 
                // Requirement doesn't specify, but usually "Back" goes back to previous activity.
                // Or if we treat "Nap Active" as a state, back could stop nap.
                // For now, adhere to previous logic: Go to MainActivity.

                Intent intent = new Intent(SectionTwoActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }
        });
    }

    private void setupNavigation() {
        View sectionRest = findViewById(R.id.view_section_2);
        View sectionMusic = findViewById(R.id.view_section21);
        View sectionSeat = findViewById(R.id.view_section22);
        View sectionLight = findViewById(R.id.view_section23);

        if (sectionRest != null) {
            sectionRest.setOnClickListener(null);
            sectionRest.setClickable(false);
        }

        if (sectionMusic != null) {
            sectionMusic.setOnClickListener(v -> {
                Intent intent = new Intent(SectionTwoActivity.this, Section2_1.class);
                startActivity(intent);
            });
        }

        if (sectionSeat != null) {
            sectionSeat.setOnClickListener(v -> {
                Intent intent = new Intent(SectionTwoActivity.this, Section2_2.class);
                startActivity(intent);
            });
        }

        if (sectionLight != null) {
            sectionLight.setOnClickListener(v -> {
                Intent intent = new Intent(SectionTwoActivity.this, Section2_3.class);
                startActivity(intent);
            });
        }
    }
}
