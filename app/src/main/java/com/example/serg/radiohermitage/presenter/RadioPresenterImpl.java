package com.example.serg.radiohermitage.presenter;

import android.media.AudioManager;
import android.view.View;
import android.widget.SeekBar;

import com.example.serg.radiohermitage.IAudioManagerProvider;
import com.example.serg.radiohermitage.IButtonClickProvider;
import com.example.serg.radiohermitage.R;
import com.example.serg.radiohermitage.model.State;
import com.example.serg.radiohermitage.model.Stream;
import com.example.serg.radiohermitage.view.IRadioView;

/**
 * Created by ASER on 14.09.2016.
 */
public class RadioPresenterImpl implements IRadioPresenter {
    private IRadioView radioView;
    private IAudioManagerProvider audioManagerProvider;
    private IButtonClickProvider buttonClickProvider;
    private Stream stream = null;

    public Stream getStream() {
        return stream;
    }

    @Override
    public void onResume(){
        if(stream!=null){
            if(stream.getState().equals(State.PLAYING)){
                setButtonImage(R.drawable.pause_btn);
            } else {
                setButtonImage(R.drawable.play_btn);
            }
        }
    }

    @Override
    public void setStream(Stream stream) {
        this.stream = stream;
    }

    public RadioPresenterImpl(IRadioView radioView, IAudioManagerProvider audioManagerProvider, IButtonClickProvider buttonClickProvider) {
        this.radioView = radioView;
        this.audioManagerProvider = audioManagerProvider;
        this.buttonClickProvider = buttonClickProvider;
    }

    @Override
    public void initViews() {
        radioView.getPlayPause().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonClickProvider.onBtnClick();
            }
        });
        radioView.setUpSeekBar(
                audioManagerProvider.getAudioManager().getStreamMaxVolume(AudioManager.STREAM_MUSIC),
                audioManagerProvider.getAudioManager().getStreamVolume(AudioManager.STREAM_MUSIC),
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        audioManagerProvider.getAudioManager().setStreamVolume(AudioManager.STREAM_MUSIC, i, 0);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                }
        );
        /*radioView.getSeekBar().setMax(audioManagerProvider.getAudioManager().getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        radioView.getSeekBar().setProgress(audioManagerProvider.getAudioManager().getStreamVolume(AudioManager.STREAM_MUSIC));
        radioView.getSeekBar().setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                audioManagerProvider.getAudioManager().setStreamVolume(AudioManager.STREAM_MUSIC, i, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });*/

    }

    @Override
    public void volUp(){
        int i = audioManagerProvider.getAudioManager().getStreamVolume(AudioManager.STREAM_MUSIC);
        audioManagerProvider.getAudioManager().setStreamVolume(AudioManager.STREAM_MUSIC, i+1, AudioManager.FLAG_SHOW_UI);
        radioView.setSeekBarProgress(i+1);
        //radioView.getSeekBar().setProgress(i+1);
    }

    @Override
    public void volDown(){
        int i = audioManagerProvider.getAudioManager().getStreamVolume(AudioManager.STREAM_MUSIC);
        audioManagerProvider.getAudioManager().setStreamVolume(AudioManager.STREAM_MUSIC, i-1, AudioManager.FLAG_SHOW_UI);
        radioView.setSeekBarProgress(i-1);
        //radioView.getSeekBar().setProgress(i-1);
    }

    @Override
    public void setButtonImage(int resId) {
        radioView.getPlayPause().setBackgroundResource(resId);
    }
}
