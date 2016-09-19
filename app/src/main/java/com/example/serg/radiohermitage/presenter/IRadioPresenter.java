package com.example.serg.radiohermitage.presenter;

import com.example.serg.radiohermitage.model.Stream;

/**
 * Created by ASER on 14.09.2016.
 */
public interface IRadioPresenter {
    public void initViews();
    public void setButtonImage(int resId);
    public void setStream(Stream stream);
    public void onResume();
    public void volUp();
    public void volDown();
}
