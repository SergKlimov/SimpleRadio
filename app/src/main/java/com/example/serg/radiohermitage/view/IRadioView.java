package com.example.serg.radiohermitage.view;

import android.widget.ImageButton;
import android.widget.SeekBar;

/**
 * Created by ASER on 14.09.2016.
 */
public interface IRadioView {
    public ImageButton getPlayPause();
    public void setSeekBarProgress(int progress);
    public void setUpSeekBar(int max, int progress, SeekBar.OnSeekBarChangeListener changeListener);
}
