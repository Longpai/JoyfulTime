package com.example.enjoytime;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaMetadata;
import android.media.session.MediaController;
import android.media.session.PlaybackState;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.Locale;

public class Section2_1 extends AppCompatActivity implements MusicNotificationListenerService.MediaControllerListener {

    private static final String TAG = "Section2_1";

    private ImageView iconPlayPause;
    private SeekBar seekBar;
    private TextView textCurrentTime;
    private TextView textTotalTime;
    private TextView textSongTitle;
    private TextView textArtistName;
    private ImageView imageAlbumArt;

    private MediaController mediaController;
    private MediaController.Callback mediaControllerCallback;
    private MusicNotificationListenerService musicService;
    private boolean isBound = false;

    private final Handler seekBarUpdateHandler = new Handler(Looper.getMainLooper());
    private Runnable seekBarUpdateRunnable;

    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    // After getting Bluetooth permission, check for the notification permission.
                    checkNotificationListenerPermission();
                } else {
                    Toast.makeText(this, "未授予蓝牙权限，无法播放音乐", Toast.LENGTH_SHORT).show();
                }
            }
    );

    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            MusicNotificationListenerService.LocalBinder binder = (MusicNotificationListenerService.LocalBinder) service;
            musicService = binder.getService();
            musicService.setListener(Section2_1.this);
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            isBound = false;
            if (musicService != null) {
                musicService.setListener(null);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_section21);

        setupUI();
        setupNavigation();
        setupBackNavigation();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, MusicNotificationListenerService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
        checkPermissions();
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.BLUETOOTH_CONNECT);
                return; 
            }
        }
        checkNotificationListenerPermission();
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (isBound) {
            unbindService(connection);
            isBound = false;
        }
        stopSeekBarUpdate();
    }

    private void checkNotificationListenerPermission() {
        if (!isNotificationServiceEnabled()) {
            startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
        }
    }

    private boolean isNotificationServiceEnabled() {
        ComponentName cn = new ComponentName(this, MusicNotificationListenerService.class);
        String flat = Settings.Secure.getString(getContentResolver(), "enabled_notification_listeners");
        return flat != null && flat.contains(cn.flattenToString());
    }

    private void setupUI() {
        iconPlayPause = findViewById(R.id.icon_play_pause);
        seekBar = findViewById(R.id.music_seekbar);
        textCurrentTime = findViewById(R.id.text_current_time);
        textTotalTime = findViewById(R.id.text_total_time);
        textSongTitle = findViewById(R.id.text_song_title);
        textArtistName = findViewById(R.id.text_artist_name);
        imageAlbumArt = findViewById(R.id.image_album_art);

        findViewById(R.id.btn_play_pause).setOnClickListener(v -> {
            if (mediaController != null) {
                PlaybackState state = mediaController.getPlaybackState();
                if (state != null) {
                    if (state.getState() == PlaybackState.STATE_PLAYING) {
                        mediaController.getTransportControls().pause();
                    } else {
                        mediaController.getTransportControls().play();
                    }
                }
            }
        });

        findViewById(R.id.btn_prev).setOnClickListener(v -> {
            if (mediaController != null) mediaController.getTransportControls().skipToPrevious();
        });

        findViewById(R.id.btn_next).setOnClickListener(v -> {
            if (mediaController != null) mediaController.getTransportControls().skipToNext();
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaController != null) {
                    mediaController.getTransportControls().seekTo(progress);
                }
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void updateMetadata(MediaMetadata metadata) {
        if (metadata == null) {
            clearUI();
            return;
        }

        textSongTitle.setText(metadata.getString(MediaMetadata.METADATA_KEY_TITLE));
        textArtistName.setText(metadata.getString(MediaMetadata.METADATA_KEY_ARTIST));
        long duration = metadata.getLong(MediaMetadata.METADATA_KEY_DURATION);
        textTotalTime.setText(formatTime(duration));
        seekBar.setMax((int) duration);

        Bitmap albumArt = metadata.getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART);
        if (albumArt != null) {
            imageAlbumArt.setImageBitmap(albumArt);
        } else {
            imageAlbumArt.setImageResource(android.R.color.darker_gray);
        }
    }

    private void updatePlaybackState(PlaybackState state) {
        if (state == null) {
            clearUI();
            return;
        }

        int playbackState = state.getState();
        iconPlayPause.setImageResource(playbackState == PlaybackState.STATE_PLAYING ? R.drawable.ic_pause : R.drawable.ic_play_arrow);
        updateSeekBar(state);

        if (playbackState == PlaybackState.STATE_PLAYING) {
            startSeekBarUpdate();
        } else {
            stopSeekBarUpdate();
        }
    }

    private void startSeekBarUpdate() {
        stopSeekBarUpdate();
        seekBarUpdateRunnable = new Runnable() {
            @Override
            public void run() {
                if (mediaController != null && mediaController.getPlaybackState() != null) {
                    updateSeekBar(mediaController.getPlaybackState());
                    seekBarUpdateHandler.postDelayed(this, 1000);
                }
            }
        };
        seekBarUpdateHandler.post(seekBarUpdateRunnable);
    }

    private void stopSeekBarUpdate() {
        if (seekBarUpdateRunnable != null) {
            seekBarUpdateHandler.removeCallbacks(seekBarUpdateRunnable);
        }
    }

    private void updateSeekBar(PlaybackState state) {
        if (state != null) {
            long position = state.getPosition();
            seekBar.setProgress((int) position);
            textCurrentTime.setText(formatTime(position));
        }
    }

    private void clearUI() {
        textSongTitle.setText("未连接到音乐播放器");
        textArtistName.setText("请在手机上播放音乐");
        textCurrentTime.setText("00:00");
        textTotalTime.setText("00:00");
        seekBar.setMax(100);
        seekBar.setProgress(0);
        iconPlayPause.setImageResource(R.drawable.ic_play_arrow);
        imageAlbumArt.setImageResource(android.R.color.darker_gray);
        stopSeekBarUpdate();
    }

    private String formatTime(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }

    @Override
    public void onMediaControllerChanged(MediaController newMediaController) {
        if (mediaController != null && mediaControllerCallback != null) {
            mediaController.unregisterCallback(mediaControllerCallback);
        }

        mediaController = newMediaController;

        if (mediaController != null) {
            mediaControllerCallback = new MediaController.Callback() {
                @Override
                public void onPlaybackStateChanged(PlaybackState state) {
                    updatePlaybackState(state);
                }

                @Override
                public void onMetadataChanged(MediaMetadata metadata) {
                    updateMetadata(metadata);
                }

                @Override
                public void onSessionDestroyed() {
                    onMediaControllerChanged(null);
                }
            };
            mediaController.registerCallback(mediaControllerCallback);

            updateMetadata(mediaController.getMetadata());
            updatePlaybackState(mediaController.getPlaybackState());
        } else {
            clearUI();
        }
    }

    private void setupBackNavigation() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(Section2_1.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }
        });
    }

    private void setupNavigation() {
        findViewById(R.id.view_section_2).setOnClickListener(v -> startActivity(new Intent(this, SectionTwoActivity.class)));
        findViewById(R.id.view_section21).setClickable(false);
        findViewById(R.id.view_section22).setOnClickListener(v -> startActivity(new Intent(this, Section2_2.class)));
        findViewById(R.id.view_section23).setOnClickListener(v -> startActivity(new Intent(this, Section2_3.class)));
    }
}
