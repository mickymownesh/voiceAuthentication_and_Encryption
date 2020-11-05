package com.application.callAuth;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;

import androidx.core.app.ActivityCompat;
import de.thielegram.loomorec.Identifier;
import de.thielegram.loomorec.Recorder;

import AlizeSpkRec.AlizeException;
import AlizeSpkRec.IdAlreadyExistsException;
import AlizeSpkRec.SimpleSpkDetSystem;
import AlizeSpkRec.SimpleSpkDetSystem.SpkRecResult;

public class RegisterActivity extends Activity {

    TextView registerText,micIcon,micOff,instructions,fillSpeach;
    EditText userID;
    Button login ,register,complete;
    LinearLayout micLayout;
    Intent nextActivity;

    static Identifier identifierInstance;
    private String LogID = "RegisterActivity";
    short[] recordingData = new short[0];
    Recorder SoundRecorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerText = (TextView) findViewById(R.id.RegisterTextView);
        login = (Button)findViewById(R.id.LoginButton);
        userID = (EditText)findViewById(R.id.UserIDEdit);
        register = (Button)findViewById(R.id.RegisterButton);
        micLayout = (LinearLayout)findViewById(R.id.MicLayout);
        micIcon = (TextView)findViewById(R.id.MicIcon);
        micOff = (TextView)findViewById(R.id.MicOffIcon);
        instructions = (TextView)findViewById(R.id.SpeaktheF);
        fillSpeach = (TextView)findViewById(R.id.FillSpeech);
        complete = (Button)findViewById(R.id.CompleteRegistration);

        //getting user permissions

        checkRecordPermission();
        checkInternetPermission();
        checkStoragePermission();


        identifierInstance = new Identifier();
        SoundRecorder = new Recorder();
        SoundRecorder.initializeRecorder();

        //initializing intent
        nextActivity = new Intent(this,MainActivity.class);

        //Initializing the alize system

        try {
            identifierInstance.initializeSystem(getApplicationContext());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (AlizeException e) {
            Log.i("RegisterActivity","Error in initializing the alize system");
        }


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] name = new String[20] ;
                try {
                    name = identifierInstance.getSpeakerList();
                } catch (AlizeException e) {
                    e.printStackTrace();
                }

                try{
                    if(Arrays.asList(name).contains(userID.getText().toString())){
                        Toast.makeText(getApplicationContext(),"Login Sucessfull",Toast.LENGTH_SHORT).show();
                        nextActivity.putExtra("UserID",userID.getText().toString());
                        startActivity(nextActivity);

                    }else
                        Toast.makeText(getApplicationContext(),"User Not found",Toast.LENGTH_LONG).show();
                }catch (Exception e){
                    Log.i(LogID,"Exception in comparing name list");
                }

            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerText.setVisibility((registerText.getVisibility() == View.VISIBLE)
                        ? View.GONE:View.VISIBLE);
                login.setVisibility((login.getVisibility() == View.VISIBLE)
                        ? View.GONE:View.VISIBLE);
                micIcon.setVisibility((micIcon.getVisibility() == View.VISIBLE)
                        ? View.GONE:View.VISIBLE);
                micOff.setVisibility((micOff.getVisibility() == View.VISIBLE)
                        ? View.GONE:View.VISIBLE);
                register.setVisibility((register.getVisibility() == View.VISIBLE)
                        ? View.GONE:View.VISIBLE);
                complete.setVisibility((complete.getVisibility() == View.VISIBLE)
                        ? View.GONE:View.VISIBLE);

            }
        });

        micIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ins = "Hello , This is recoderd for registration process , I am [your_name]"+
                        " It is nice to use this app."+"I am registering my voice for authenticating process";
                fillSpeach.setText(ins);
                instructions.setVisibility((instructions.getVisibility() == View.VISIBLE)
                        ? View.GONE:View.VISIBLE);
                fillSpeach.setVisibility((fillSpeach.getVisibility() == View.VISIBLE)
                        ? View.GONE:View.VISIBLE);

                SoundRecorder.startRecording();

                Toast.makeText(getApplicationContext(),"Started recording Follow the instructions",Toast.LENGTH_SHORT).show();
            }
        });

        micOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fillSpeach.setVisibility(View.GONE);
                try {
                    SoundRecorder.stopRecording();
                }catch (Exception e){
                    Log.i(LogID,"Error in stoping the recorder");
                    Toast.makeText(getApplicationContext(),"First start recording",Toast.LENGTH_SHORT).show();

                }
                instructions.setText("Recording completed !! click register ");
            }
        });

        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                instructions.setVisibility(View.GONE);
                recordingData = SoundRecorder.getRecording();
                try {
                    identifierInstance.addSpeaker(recordingData,userID.getText().toString());
                    complete.setVisibility(View.GONE);
                    micIcon.setVisibility(View.GONE);
                    micOff.setVisibility(View.GONE);
                    login.setVisibility(View.VISIBLE);
                    register.setVisibility(View.VISIBLE);

                } catch (AlizeException e) {
                    Log.i(LogID,"unable to add the username");
                } catch (IdAlreadyExistsException e) {
                    Log.i(LogID,"Error in creating user user already exist");
                    Toast.makeText(getApplicationContext(),"User already exist",Toast.LENGTH_SHORT).show();
                    complete.setVisibility(View.GONE);
                    micIcon.setVisibility(View.GONE);
                    micOff.setVisibility(View.GONE);
                    login.setVisibility(View.VISIBLE);
                    register.setVisibility(View.VISIBLE);
                }

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
}