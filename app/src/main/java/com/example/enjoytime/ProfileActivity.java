package com.example.enjoytime;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    private ImageView avatarImageView;
    private TextView usernameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        avatarImageView = findViewById(R.id.profile_avatar_image_view);
        usernameTextView = findViewById(R.id.profile_username_text_view);

        SharedPreferences preferences = getSharedPreferences("user_credentials", MODE_PRIVATE);
        String username = getIntent().getStringExtra("username");
        String avatarUriString = preferences.getString(username + "_avatar", null);

        usernameTextView.setText(username);

        if (avatarUriString != null) {
            avatarImageView.setImageURI(Uri.parse(avatarUriString));
        } else {
            // Set a default avatar image if none is found
            avatarImageView.setImageResource(android.R.drawable.ic_menu_gallery);
        }
    }
}
