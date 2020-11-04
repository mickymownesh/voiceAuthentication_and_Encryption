package com.application.callAuth;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;

import androidx.core.app.ActivityCompat;
import de.thielegram.loomorec.Identifier;
import de.thielegram.loomorec.Recorder;

import AlizeSpkRec.AlizeException;
import AlizeSpkRec.IdAlreadyExistsException;
import AlizeSpkRec.SimpleSpkDetSystem;
import AlizeSpkRec.SimpleSpkDetSystem.SpkRecResult;
import java.io.File;



public class AuthenticatorTesting extends Activity {

    Button start , stop,add,verify ,newV , newVstop;
    TextView verified;
    Identifier identifierInstance;
    short[] recordingData = new short[0];
    Recorder firstRecorder,SecondRecorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticator_testing);

        firstRecorder = new Recorder();
        SecondRecorder = new Recorder();
        firstRecorder.initializeRecorder();
        SecondRecorder.initializeRecorder();

        checkRecordPermission();
        checkInternetPermission();
        checkStoragePermission();

        identifierInstance = new Identifier();

        start = (Button)findViewById(R.id.AuthStart);
        stop = (Button)findViewById(R.id.AuthStop);
        add = (Button)findViewById(R.id.addSpkrBtn);
        verify = (Button)findViewById(R.id.TestVoice);
        verified = (TextView)findViewById(R.id.VerfiedName);
        newV = (Button)findViewById(R.id.newAudio);
        newVstop = (Button)findViewById(R.id.NewVoiceStop);

        verified.setText("Not yet verified");

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                firstRecorder.startRecording();
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstRecorder.stopRecording();
                Log.i("ACT",firstRecorder.recordingData.toString());
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlizeInit(getApplicationContext());
                addSpeakersTest();
                //...
            }
        });

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SpkRecResult output = null;
                try {
                    output = identifierInstance.identifySpeaker(SecondRecorder.getRecording());
                } catch (AlizeException e) {
                    Log.i("ACT","error in speker result");
                }
                if(output != null){
                    verified.setText(String.valueOf(output.score));
                }else{
                    Log.i("ACT","output is null");
                }

            }
        });

        newV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SecondRecorder.startRecording();
            }
        });

        newVstop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SecondRecorder.stopRecording();
            }
        });

    }



    private void checkRecordPermission() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
                    123);
        }
    }

    private void checkStoragePermission() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    123);
        }
    }
    private void checkInternetPermission() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET},
                    123);
        }
    }

    private void AlizeInit(Context cx){

        try {
            identifierInstance.initializeSystem(cx);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            Log.i("APP","error in initializing the alize init");
        }

    }

    private void addSpeakersTest(){

        try {
            identifierInstance.removeSpeaker("mownesh");
        } catch (AlizeException e) {
            Log.i("ACT","Error in removing speaker in add speaker method");
        }
        recordingData = firstRecorder.getRecording();
        String name = "mownesh";

        try {
            identifierInstance.addSpeaker(recordingData,name);
        } catch (AlizeException e) {
            e.printStackTrace();
        } catch (IdAlreadyExistsException e) {
            Log.i("ACT","error in add speakers test method");
        }
    }


}