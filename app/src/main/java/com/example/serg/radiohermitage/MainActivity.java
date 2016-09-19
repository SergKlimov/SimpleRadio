package com.example.serg.radiohermitage;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.serg.radiohermitage.presenter.IRadioPresenter;
import com.example.serg.radiohermitage.presenter.RadioPresenterImpl;
import com.example.serg.radiohermitage.view.IRadioView;
import com.example.serg.radiohermitage.view.RadioViewImpl;

public class MainActivity extends AppCompatActivity implements IAudioManagerProvider, IButtonClickProvider {

    private static final String TAG = "MainActivity";

    private IPlayer player = null;
    private IRadioView radioView;
    private IRadioPresenter radioPresenter;
    private ProgressDialog progressDialog;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.getAction().equals(Consts.ACT_NOTIFY)){
                radioPresenter.onResume();
            } else if(intent.getAction().equals(Consts.ACT_PREPARED)){
                radioView.getPlayPause().setEnabled(true);
                progressDialog.dismiss();
            } else if(intent.getAction().equals(Consts.ACT_NOT_PREPARED)){
                radioView.getPlayPause().setEnabled(false);
                Log.d(TAG, "show: " + progressDialog.toString());
                progressDialog.show();
            }
        }
    };

    private void createDialog(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Preparing audio for playback");
        progressDialog.setMessage("Waiting for media...");
        progressDialog.setCancelable(false);
    }

    private LocalBroadcastManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        radioView = new RadioViewImpl((ImageButton)findViewById(R.id.playPause),
                (SeekBar)findViewById(R.id.seekBar));
        radioPresenter = new RadioPresenterImpl(radioView, this, this);
        radioPresenter.initViews();

        createDialog();

        Intent startService = new Intent(this, PlayService.class);
        if(player==null)
            startService(startService);
        bindService(startService, mConnection, 0);

        manager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Consts.ACT_NOTIFY);
        intentFilter.addAction(Consts.ACT_PREPARED);
        intentFilter.addAction(Consts.ACT_NOT_PREPARED);
        manager.registerReceiver(broadcastReceiver, intentFilter);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_VOLUME_UP){
            radioPresenter.volUp();
            return true;
        } else if(keyCode == KeyEvent.KEYCODE_VOLUME_DOWN){
            radioPresenter.volDown();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            PlayService.PlayerBinder playerBinder = (PlayService.PlayerBinder) iBinder;
            player = playerBinder.getService();
            radioPresenter.setStream(player.getStream());
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        radioPresenter.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
        unbindService(mConnection);
        progressDialog.dismiss();
        manager.unregisterReceiver(broadcastReceiver);
        //progressDialog = null;
        Intent intent = new Intent(this, PlayService.class);
        stopService(intent);
    }

    private boolean needExit = false;

    @Override
    public void onBackPressed() {

        if(needExit){
            Intent intent = new Intent(this, PlayService.class);
            stopService(intent);
            super.onBackPressed();
            return;
        }

        needExit = true;
        Toast.makeText(getApplicationContext(), "Press back to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                needExit = false;
            }
        }, 1500);
    }

    @Override
    public AudioManager getAudioManager() {
        return (AudioManager)getSystemService(Context.AUDIO_SERVICE);
    }

    @Override
    public void onBtnClick() {
        if(radioView.getPlayPause().isEnabled())
            player.toggleRadio();
        else
            Toast.makeText(getApplicationContext(), "not prepared!", Toast.LENGTH_SHORT).show();
    }
}
