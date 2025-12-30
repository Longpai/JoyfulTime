package com.example.enjoytime;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.File;

public class ProfileFragment extends Fragment {

    private ImageView avatarImageView;
    private TextView usernameTextView;

    public interface OnLogoutListener {
        void onLogout();
        void onSwitchAccount();
    }

    private OnLogoutListener logoutListener;

    public static ProfileFragment newInstance(String username) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString("username", username);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnLogoutListener) {
            logoutListener = (OnLogoutListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnLogoutListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        avatarImageView = view.findViewById(R.id.profile_avatar_image_view);
        usernameTextView = view.findViewById(R.id.profile_username_text_view);
        Button switchAccountButton = view.findViewById(R.id.switch_account_button);
        Button logoutButton = view.findViewById(R.id.logout_button);

        if (getArguments() != null) {
            String username = getArguments().getString("username");
            usernameTextView.setText(username);

            SharedPreferences preferences = getActivity().getSharedPreferences("user_credentials", Context.MODE_PRIVATE);
            String avatarPath = preferences.getString(username + "_avatar", null);

            if (avatarPath != null) {
                File imgFile = new File(avatarPath);
                if (imgFile.exists()) {
                    avatarImageView.setImageBitmap(BitmapFactory.decodeFile(imgFile.getAbsolutePath()));
                } else {
                    avatarImageView.setImageResource(android.R.drawable.ic_menu_gallery);
                }
            } else {
                avatarImageView.setImageResource(android.R.drawable.ic_menu_gallery);
            }
        }

        switchAccountButton.setOnClickListener(v -> {
            if (logoutListener != null) {
                logoutListener.onSwitchAccount();
            }
        });

        logoutButton.setOnClickListener(v -> {
            if (logoutListener != null) {
                logoutListener.onLogout();
            }
        });
    }
}
