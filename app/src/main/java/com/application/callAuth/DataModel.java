package com.application.callAuth;

import java.io.File;

public class DataModel {

    private File audio;
    private static boolean verification = false;
    private static String USERID;

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

    public void setUSERID(String user){this.USERID = user;}

    public String getUSERID(){return USERID;}
}
