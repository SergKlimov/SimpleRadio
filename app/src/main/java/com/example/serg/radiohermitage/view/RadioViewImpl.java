package com.example.serg.radiohermitage.view;

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

    @Override
    public SeekBar getSeekBar() {
        return seekBar;
    }

    public void setSeekBar(SeekBar seekBar) {
        this.seekBar = seekBar;
    }
}
