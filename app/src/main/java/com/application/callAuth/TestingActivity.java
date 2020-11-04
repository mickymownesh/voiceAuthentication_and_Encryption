package com.application.callAuth;



import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;


import com.bitsinharmony.recognito.MatchResult;
import com.bitsinharmony.recognito.Recognito;
import com.bitsinharmony.recognito.VoicePrint;

import androidx.core.app.ActivityCompat;

public class TestingActivity extends Activity {

    private Button Recordbtn, Sendbtn , downbtn,testbtn;


    private static final String LOG_TAG = "TestingActivity";
    private static final int SAMPLE_RATE = 16000; // Hertz
    private static final int SAMPLE_INTERVAL = 20; // Milliseconds
    private static final int SAMPLE_SIZE = 2; // Bytes
    private static final int BUF_SIZE = SAMPLE_INTERVAL * SAMPLE_INTERVAL * SAMPLE_SIZE * 2; //Bytes
    private InetAddress address; // Address to call
    private int port = 50000; // Port the packets are addressed to
    private static boolean mic = false; // Enable mic?
    private boolean speakers = false; // Enable speakers?
    MediaPlayer mp ;


    static File waveFile;
    static File downloadAudio;
    static File checkAudio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing);

        Recordbtn = (Button)findViewById(R.id.TestRecordbtn);
        Sendbtn = (Button)findViewById(R.id.testSendbtn);
        downbtn = (Button) findViewById(R.id.downloadAudio);
        testbtn = (Button) findViewById(R.id.testbtn);

        checkRecordPermission();
        checkInternetPermission();
        checkStoragePermission();

        downloadAudio = new File(getFilesDir(),"DownloadAudio"+".wav");
        checkAudio = new File(getFilesDir(),"CheckAudio"+".wav");

        Recordbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                waveFile = new File(getFilesDir(),"firstTestRecording"+".wav");
                RecordWave.CreateWav(waveFile);
                startMic();
               // startMic();

            }
        });

        testbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  audioAuthenticator.recognitoTest(downloadAudio);
            }
        });

        Sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] buff = new byte[BUF_SIZE];
                //int i = 800;
                //RecordWave.Write(buff);
                muteMic();
                //RecordWave.fillWav(buff,i);
               // muteMic();
                Toast.makeText(getApplicationContext(),"Stopping mic and uploading data",Toast.LENGTH_LONG).show();
                //fireBaseSupport obj = new fireBaseSupport(waveFile);
              //  fireBaseSupport.uploadAudio(waveFile);
            }
        });

        downbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileInputStream fin = null;
                FileOutputStream fout = null;
                int b;
               fireBaseSupport.downloadAudio(downloadAudio);
/*
                try {
                    fin = new FileInputStream(temp);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                try {
                    fout = new FileOutputStream(downloadAudio);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                try{

                    while ((b = fin.read()) != -1){
                        fout.write(b);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
                   Log.i(LOG_TAG,"File sizes not matched in testing activity: "+downloadAudio.length());
                   Log.i(LOG_TAG,"file location : "+downloadAudio.getPath());
                   Log.i(LOG_TAG,"is executable : "+ downloadAudio.canExecute());
                // calltest();*/
            }
        });



    }

    public void calltest(){
        MediaPlayer mp = new MediaPlayer();
        try {
            FileOutputStream player = new FileOutputStream(downloadAudio);
            mp.setDataSource(player.getFD());
            mp.prepare();
        } catch (IOException e) {
            Log.i(LOG_TAG,"Error in playing the audio : "+e);
        }
        mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
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

    public void muteMic() {

        mic = false;
    }

    public static void startMic() {
        // Creates the thread for capturing and transmitting audio
        mic = true;
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                // Create an instance of the AudioRecord class
                Log.i(LOG_TAG, "Send thread started. Thread id: " + Thread.currentThread().getId());
                AudioRecord audioRecorder = new AudioRecord (MediaRecorder.AudioSource.VOICE_COMMUNICATION, SAMPLE_RATE,
                        AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT,
                        AudioRecord.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT)*10);
                int bytes_read = 0;
                int bytes_sent = 0;
                byte[] buf = new byte[BUF_SIZE];
                try {
                    // Create a socket and start recording
                    audioRecorder.startRecording();
                    while(mic) {
                        // Capture audio from the mic and transmit it
                        bytes_read = audioRecorder.read(buf, 0, BUF_SIZE);
                        RecordWave.Write(buf,bytes_read);
                        Thread.sleep(SAMPLE_INTERVAL, 0);
                    }
                    // Stop recording and release resources
                    Log.i(LOG_TAG,"buff size : "+buf.length);
                    audioRecorder.stop();
                    audioRecorder.release();
                    RecordWave.updateHeader();
                    //closing the wav file
                    //RecordWave.closeWav();
                   // RecordWave.updateWavHeader(waveFile);
                    Log.i(LOG_TAG,"wavefile size : "+waveFile.length());
                    mic = false;
                    return;
                }
                catch(InterruptedException e) {

                    Log.e(LOG_TAG, "InterruptedException: " + e.toString());
                    mic = false;
                }
            }
        });
        thread.start();
    }

    public void playAudio(){
       Log.i(LOG_TAG,downloadAudio.getPath());
    }

}