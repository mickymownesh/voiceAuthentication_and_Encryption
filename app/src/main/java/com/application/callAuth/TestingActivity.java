package com.application.callAuth;



import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class TestingActivity extends Activity {

    private Button Recordbtn, Sendbtn;


    private static final String LOG_TAG = "AudioCall";
    private static final int SAMPLE_RATE = 8000; // Hertz
    private static final int SAMPLE_INTERVAL = 20; // Milliseconds
    private static final int SAMPLE_SIZE = 2; // Bytes
    private static final int BUF_SIZE = SAMPLE_INTERVAL * SAMPLE_INTERVAL * SAMPLE_SIZE * 2; //Bytes
    private InetAddress address; // Address to call
    private int port = 50000; // Port the packets are addressed to
    private boolean mic = false; // Enable mic?
    private boolean speakers = false; // Enable speakers?

    File waveFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing);

        Recordbtn = (Button)findViewById(R.id.TestRecordbtn);
        Sendbtn = (Button)findViewById(R.id.testSendbtn);

        Recordbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                waveFile = new File(getFilesDir(),"firstTestRecording"+".wav");
                RecordWave.CreateWav(waveFile);
                startMic();

            }
        });

        Sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                muteMic();
                

            }
        });
    }

    public void muteMic() {

        mic = false;
    }

    public void startMic() {
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
                        RecordWave.fillWav(buf,bytes_read);
                        Thread.sleep(SAMPLE_INTERVAL, 0);
                    }
                    // Stop recording and release resources
                    audioRecorder.stop();
                    audioRecorder.release();
                    //closing the wav file
                    RecordWave.closeWav();
                    RecordWave.updateWavHeader(waveFile);
                    mic = false;
                    return;
                }
                catch(InterruptedException | IOException e) {

                    Log.e(LOG_TAG, "InterruptedException: " + e.toString());
                    mic = false;
                }
            }
        });
        thread.start();
    }

}