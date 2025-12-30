package com.example.enjoytime;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.io.File;

public class MainActivity extends AppCompatActivity implements ProfileFragment.OnLogoutListener {

    private boolean isLoggedIn = false;
    private String loggedInUsername;

    private LinearLayout userInfoLayout;
    private ImageView mainAvatarImageView;
    private TextView mainUsernameTextView;
    private TextView mineDefaultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userInfoLayout = findViewById(R.id.user_info_layout);
        mainAvatarImageView = findViewById(R.id.main_avatar_imageview);
        mainUsernameTextView = findViewById(R.id.main_username_textview);
        mineDefaultTextView = findViewById(R.id.mine_default_textview);

        View sectionMine = findViewById(R.id.view_section_mine);
        View section1 = findViewById(R.id.view_section_1);
        View section2 = findViewById(R.id.view_section_2);
        View section3 = findViewById(R.id.view_section_3);

        sectionMine.setOnClickListener(v -> {
            if (isLoggedIn) {
                showProfile();
            } else {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        section1.setOnClickListener(v -> {
            if (isLoggedIn) {
                Intent intent = new Intent(MainActivity.this, SectionOneActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            }
        });

        section2.setOnClickListener(v -> {
            if (isLoggedIn) {
                Intent intent = new Intent(MainActivity.this, SectionTwoActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            }
        });

        section3.setOnClickListener(v -> {
            if (isLoggedIn) {
                Intent intent = new Intent(MainActivity.this, FoodRecommendationActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            }
        });

        if (savedInstanceState == null) {
            showHome();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        boolean justLoggedIn = false;
        Intent intent = getIntent();
        
        if (intent != null && intent.hasExtra("username")) {
            loggedInUsername = intent.getStringExtra("username");
            isLoggedIn = true;
            justLoggedIn = true;
            saveLoginStatus(true, loggedInUsername);
            intent.removeExtra("username");
        } else {
            checkLoginStatus();
        }

        updateMineSection();

        if (justLoggedIn) {
            showProfile();
        }
    }

    private void checkLoginStatus() {
        SharedPreferences preferences = getSharedPreferences("login_status", MODE_PRIVATE);
        isLoggedIn = preferences.getBoolean("isLoggedIn", false);
        loggedInUsername = preferences.getString("username", null);
    }

    private void saveLoginStatus(boolean isLoggedIn, String username) {
        SharedPreferences preferences = getSharedPreferences("login_status", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isLoggedIn", isLoggedIn);
        editor.putString("username", username);
        editor.apply();
    }

    private void updateMineSection() {
        if (isLoggedIn && loggedInUsername != null) {
            mineDefaultTextView.setVisibility(View.GONE);
            userInfoLayout.setVisibility(View.VISIBLE);

            mainUsernameTextView.setText(loggedInUsername);

            SharedPreferences userPrefs = getSharedPreferences("user_credentials", MODE_PRIVATE);
            String avatarPath = userPrefs.getString(loggedInUsername + "_avatar", null);

            if (avatarPath != null) {
                File imgFile = new File(avatarPath);
                if (imgFile.exists()) {
                    mainAvatarImageView.setImageBitmap(BitmapFactory.decodeFile(imgFile.getAbsolutePath()));
                } else {
                    mainAvatarImageView.setImageResource(android.R.drawable.ic_menu_gallery);
                }
            } else {
                mainAvatarImageView.setImageResource(android.R.drawable.ic_menu_gallery);
            }
        } else {
            mineDefaultTextView.setVisibility(View.VISIBLE);
            userInfoLayout.setVisibility(View.GONE);
        }
    }

    private void showHome() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.right_content_container, new HomeFragment())
                .commit();
    }

    private void showProfile() {
        if (loggedInUsername != null) {
            Fragment profileFragment = ProfileFragment.newInstance(loggedInUsername);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.right_content_container, profileFragment)
                    .addToBackStack(null) // Allows user to navigate back to home
                    .commit();
        }
    }

    @Override
    public void onLogout() {
        isLoggedIn = false;
        loggedInUsername = null;
        saveLoginStatus(false, null);
        updateMineSection();
        showHome();
    }

    @Override
    public void onSwitchAccount() {
        onLogout();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
