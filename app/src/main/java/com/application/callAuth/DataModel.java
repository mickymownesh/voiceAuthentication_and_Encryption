package com.application.callAuth;

import java.io.File;

public class DataModel {

    private File audio;
    private static boolean verification = false;

    DataModel(){

    }

    public void getAudio(File aud) {
        this.audio = aud;
    }

    public File seFile(){
        return audio;
    }

    public void setVerification(boolean v){
        this.verification = v;
    }

    public boolean getVerification(){
        return verification;
    }
}
