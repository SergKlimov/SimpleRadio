package com.example.serg.radiohermitage.view;

import android.media.AudioManager;
import android.widget.ImageButton;
import android.widget.SeekBar;

/**
 * Created by ASER on 14.09.2016.
 */
public class RadioViewImpl implements IRadioView {
    private ImageButton playPause;
    private SeekBar seekBar;

    public RadioViewImpl(ImageButton playPause, SeekBar seekBar) {
        this.playPause = playPause;
        this.seekBar = seekBar;
    }

    @Override
    public ImageButton getPlayPause() {
        return playPause;
    }

    public void setPlayPause(ImageButton playPause) {
        this.playPause = playPause;
    }

    public SeekBar getSeekBar() {
        return seekBar;
    }

    public void setSeekBar(SeekBar seekBar) {
        this.seekBar = seekBar;
    }

    @Override
    public void setSeekBarProgress(int progress){
        seekBar.setProgress(progress);
    }

    @Override
    public void setUpSeekBar(int max, int progress, SeekBar.OnSeekBarChangeListener changeListener){
        seekBar.setMax(max);
        seekBar.setProgress(progress);
        seekBar.setOnSeekBarChangeListener(changeListener);
    }
}
