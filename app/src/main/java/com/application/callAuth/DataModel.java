package com.application.callAuth;

import java.io.File;

public class DataModel {

    private File audio;

    DataModel(File audio){
        this.audio = audio;
    }

    public void getAudio(File aud) {
        this.audio = aud;
    }

    public File seFile(){
        return audio;
    }
}
