package com.example.enjoytime;

import android.app.Notification;
import android.content.ComponentName;
import android.content.Intent;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.MediaSessionManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import java.util.List;

public class MusicNotificationListenerService extends NotificationListenerService {

    private final IBinder binder = new LocalBinder();
    private MediaController mediaController;
    private MediaControllerListener listener;

    public interface MediaControllerListener {
        void onMediaControllerChanged(MediaController newMediaController);
    }

    public class LocalBinder extends Binder {
        MusicNotificationListenerService getService() {
            return MusicNotificationListenerService.this;
        }
    }

    public void setListener(MediaControllerListener listener) {
        this.listener = listener;
        if (listener != null) {
            listener.onMediaControllerChanged(mediaController);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onListenerConnected() {
        super.onListenerConnected();
        findActiveMediaController();
    }

    private void updateMediaController(MediaController newController) {
        if (newController == null && mediaController == null) {
            return;
        }
        if (newController != null && mediaController != null && newController.getSessionToken().equals(mediaController.getSessionToken())) {
            return;
        }

        mediaController = newController;
        if (listener != null) {
            listener.onMediaControllerChanged(mediaController);
        }
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
        if (sbn == null) return;

        Bundle extras = sbn.getNotification().extras;
        if (extras.containsKey(Notification.EXTRA_MEDIA_SESSION)) {
            MediaSession.Token token = extras.getParcelable(Notification.EXTRA_MEDIA_SESSION);
            if (token != null) {
                if (mediaController == null || !mediaController.getSessionToken().equals(token)) {
                    updateMediaController(new MediaController(getApplicationContext(), token));
                }
            }
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
        if (sbn == null) return;

        Bundle extras = sbn.getNotification().extras;
        if (extras.containsKey(Notification.EXTRA_MEDIA_SESSION)) {
            MediaSession.Token token = extras.getParcelable(Notification.EXTRA_MEDIA_SESSION);
            if (token != null && mediaController != null && token.equals(mediaController.getSessionToken())) {
                updateMediaController(null);
                findActiveMediaController();
            }
        }
    }

    private void findActiveMediaController() {
        try {
            MediaSessionManager mediaSessionManager = (MediaSessionManager) getSystemService(MEDIA_SESSION_SERVICE);
            ComponentName componentName = new ComponentName(this, MusicNotificationListenerService.class);
            List<MediaController> activeSessions = mediaSessionManager.getActiveSessions(componentName);

            if (activeSessions != null && !activeSessions.isEmpty()) {
                MediaController newController = activeSessions.get(0);
                updateMediaController(newController);
            } else {
                 updateMediaController(null);
            }
        } catch (Exception e) {
            updateMediaController(null);
        }
    }
}
