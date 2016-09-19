package com.example.serg.radiohermitage;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.serg.radiohermitage.model.State;
import com.example.serg.radiohermitage.model.Stream;

import java.io.IOException;

/**
 * Created by ASER on 13.09.2016.
 */
public class PlayService extends Service implements IPlayer {

    private static final String TAG = "MainService";
    private static final String ACTION_PLAY = "com.example.serg.radiohermitage.ACTION_PLAY";

    private MediaPlayer mediaPlayer = null;
    private final IBinder mBinder = new PlayerBinder();
    private Stream stream;

    @Override
    public void onCreate() {
        Log.d(TAG, "got onCreate!");
        createPlayer();
        super.onCreate();
    }

    private void createPlayer() {
        if (mediaPlayer == null) {
            Log.d(TAG, "player created!");
            mediaPlayer = new MediaPlayer();
            //stream = new Stream("https://cs1-44v4.vk-cdn.net/p21/45582ef382ead3.mp3", State.STOPPED);
            //stream = new Stream("http://91.190.117.131:8000/live", State.STOPPED);
            //stream = new Stream("http://81.19.85.197/echo.mp3", State.STOPPED);
            //stream = new Stream("http://stream02.media.rambler.ru/businessfmspb128.mp3", State.STOPPED);
            stream = new Stream("http://icecast.vgtrk.cdnvideo.ru/vestifm_mp3_64kbps", State.STOPPED);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }/*else
            mediaPlayer.reset();*/
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "got onBind!");
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "got onStartCMD!");
        if (intent.getAction() != null) {
            if (intent.getAction().equals(ACTION_PLAY)) {
                toggleRadio();
            } else if (intent.getAction().equals(Consts.ACT_PREPARING)) {
                Toast.makeText(getApplicationContext(), "Not prepared!", Toast.LENGTH_SHORT).show();
            }
        } else {
            showNotification(ACTION_PLAY, R.drawable.action_play);
            //preparePlayer(false);
        }
        sendBroadcastMsg(Consts.ACT_NOTIFY);
        return START_NOT_STICKY;
    }

    public class PlayerBinder extends Binder {
        IPlayer getService() {
            return PlayService.this;
        }
    }

    private void preparePlayer(final boolean start) {
        if (!mediaPlayer.isPlaying()) {
            Log.d(TAG, "enter prepare!");
            sendBroadcastMsg(Consts.ACT_NOT_PREPARED);
            showNotification(Consts.ACT_PREPARING, R.drawable.action_play);
            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(stream.getStreamUrl());
                mediaPlayer.prepareAsync();
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        Log.d(TAG, "prepared!");
                        stream.setState(State.STOPPED);
                        if (start) {
                            stream.setState(State.PLAYING);
                            mediaPlayer.start();
                        }
                        sendBroadcastMsg(Consts.ACT_NOTIFY);
                        sendBroadcastMsg(Consts.ACT_PREPARED);
                        showNotification(ACTION_PLAY, R.drawable.action_pause);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "not prepared!");
            }
        }
    }

    int notifId = 190;

    private void showNotification(String action, int resId) {
        String text = null;
        if (resId == R.drawable.action_play) {
            text = "Play";
        } else if (resId == R.drawable.action_pause) {
            text = "Pause";
        }
        Intent playIntent = new Intent(this, PlayService.class);
        playIntent.setAction(action);
        PendingIntent pPlayIntent = PendingIntent.getService(this, (int) System.currentTimeMillis(), playIntent, 0);
        NotificationCompat.Action actionPlay = new NotificationCompat.Action.Builder(resId, text, pPlayIntent).build();
        Notification n = new NotificationCompat.Builder(this)
                .setContentTitle("Vesti FM")
                .setContentText("Radio playing")
                .setSmallIcon(R.drawable.action_play)
                .addAction(actionPlay)
                .build();

        startForeground(notifId, n);
    }

    @Override
    public void toggleRadio() {
        Log.d(TAG, "got toggleRadio!");
        if (stream.getState().equals(State.IDLE)) {
            Log.d(TAG, "IDLE state");
        } else if (stream.getState().equals(State.STOPPED)) {
            Log.d(TAG, "Stopped state");
            preparePlayer(true);
            sendBroadcastMsg(Consts.ACT_NOTIFY);
        } else if (stream.getState().equals(State.PLAYING)) {
            Log.d(TAG, "Playing state");
            mediaPlayer.stop();

            stream.setState(State.STOPPED);
            sendBroadcastMsg(Consts.ACT_NOTIFY);
            showNotification(ACTION_PLAY, R.drawable.action_play);
        }
    }

    private void sendBroadcastMsg(String action){
        Intent intent = new Intent(action);
        LocalBroadcastManager.getInstance(PlayService.this).sendBroadcast(intent);
    }

    @Override
    public Stream getStream() {
        return stream;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "got onDestroy!");
        stopForeground(true);
        mediaPlayer.stop();
        mediaPlayer.reset();
        mediaPlayer.release();
        mediaPlayer = null;
        super.onDestroy();
    }
}
