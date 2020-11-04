package com.application.callAuth;

import android.util.Log;

import com.bitsinharmony.recognito.MatchResult;
import com.bitsinharmony.recognito.Recognito;
import com.bitsinharmony.recognito.VoicePrint;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class audioAuthenticator {

 /*   private static final String LOG_TAG = "TestingActivity";
    private static final int SAMPLE_RATE = 16000; // Hertz

    audioAuthenticator(){
        Recognito<String> recognito = new Recognito<String>(16000.0f);

    }

    public static void recognitoTest(File downloadAudio){

        Recognito<String> recognito = new Recognito<>(SAMPLE_RATE);
        VoicePrint print= null;
        MatchResult<String> match = null;
        //print = recognito.createVoicePrint("mownesh",downloadAudio);

        try {
            List<MatchResult<String>> matches = recognito.identify(downloadAudio);
            match = matches.get(0);

        } catch (IOException e) {
            e.printStackTrace();
            Log.i(LOG_TAG,"error in matching the audio samples");

        }
        if(match.getKey().equals("mownesh")){
            Log.i(LOG_TAG,"Match is perfect matched the audio signals");
        }
        else{
            Log.i(LOG_TAG,"Match is imperfect");
        }
    }
*/
}
