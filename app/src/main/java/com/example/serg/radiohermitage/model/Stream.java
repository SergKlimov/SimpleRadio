package com.example.serg.radiohermitage.model;

/**
 * Created by ASER on 13.09.2016.
 */
public class Stream {
    private String streamUrl;
    private State state;

    public Stream(String streamUrl, State state) {
        this.streamUrl = streamUrl;
        this.state = state;
    }

    public String getStreamUrl() {
        return streamUrl;
    }

    public void setStreamUrl(String streamUrl) {
        this.streamUrl = streamUrl;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }
}
