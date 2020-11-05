package com.application.callAuth;

import AlizeSpkRec.AlizeException;
import AlizeSpkRec.SimpleSpkDetSystem;
import androidx.appcompat.app.AppCompatActivity;
import de.thielegram.loomorec.Identifier;
import de.thielegram.loomorec.Recorder;
import android.content.Intent;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.application.callAuth.DataModel;

import java.util.Arrays;

public class VerificationActivity extends Activity {


    TextView micIcon,micOffIcon,fillSpeech,ins;
    Button verify,cancel;

    short[] recordingData = new short[0];
    Recorder SoundRecorder;
    Identifier identifierInstance;
    String UserID="mownesh";
    Intent nextActivity;
    DataModel db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        micIcon = (TextView)findViewById(R.id.VerificationMicIcon);
        micOffIcon = (TextView)findViewById(R.id.VerficationMicOffIcon);
        fillSpeech = (TextView)findViewById(R.id.VerficationFillSpeech);
        ins = (TextView)findViewById(R.id.VerificationSpeaktheF);

        nextActivity = new Intent(getApplicationContext(),MainActivity.class);
        db = new DataModel();


        final String inst = "Hello , This is recoderd for registration process , I am [your_name]"+
                " It is nice to use this app."+"I am registering my voice for authenticating process";
        fillSpeech.setText(inst);

        //initializing sound Recorder
        SoundRecorder = new Recorder();
        SoundRecorder.initializeRecorder();

        //initializingIdentifier

        identifierInstance = RegisterActivity.identifierInstance;


        verify = (Button) findViewById(R.id.VerificationVerifybtn);
        cancel = (Button)findViewById(R.id.VerificationCancelbtn);

        micIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                micOffIcon.setVisibility(View.VISIBLE);
                fillSpeech.setVisibility(View.VISIBLE);

                SoundRecorder.startRecording();

                Toast.makeText(getApplicationContext(),"Started recording Follow the instructions",Toast.LENGTH_SHORT).show();
            }
        });

        micOffIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundRecorder.stopRecording();
                recordingData = SoundRecorder.getRecording();
                Toast.makeText(getApplicationContext(),"Recording stoped",Toast.LENGTH_SHORT).show();
            }
        });

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleSpkDetSystem.SpkRecResult output = null;
                try {
                    output = identifierInstance.identifySpeaker(recordingData);
                } catch (AlizeException e) {
                    Log.i("ACT", "error in speker result");
                }
                if (output != null) {
                    ins.setText("Your verification score : " + String.valueOf(output.score));
                } else {
                    Log.i("ACT", "output is null");
                }

                //verifying

                String[] name = new String[20];
                try {
                    name = identifierInstance.getSpeakerList();
                } catch (AlizeException e) {
                    e.printStackTrace();
                }


                try{
                    if(Arrays.asList(name).contains(UserID) ){
                        Toast.makeText(getApplicationContext(), "Verification Sucessfull", Toast.LENGTH_SHORT).show();
                        db.setVerification(true);
                        finish();

                    }else
                        Toast.makeText(getApplicationContext(),"User Not found",Toast.LENGTH_LONG).show();
                }catch (Exception e){
                    Log.i("verificationActivity","Exception in comparing name list");
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Verification not Sucessfull", Toast.LENGTH_SHORT).show();
                db.setVerification(false);
                finish();
            }
        });

    }
}