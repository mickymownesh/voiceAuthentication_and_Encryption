package com.application.callAuth;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Locale;


public class AudioSaverWAV {



    // driver method

    protected void startWAV(){


    }

}

class RecordWave {

    private static final String LOG_TAG = "AudioRecordwaveFormat";
    private static final int SAMPLE_RATE = 8000; // Hertz    --> verified sample rate
    private static final int SAMPLE_INTERVAL = 20; // Milliseconds
    private static final int SAMPLE_SIZE = 2; // Bytes
    private static final int BUF_SIZE = SAMPLE_INTERVAL * SAMPLE_INTERVAL * SAMPLE_SIZE * 2; //Bytes
    private boolean mic = false; // Enable mic?
    private MediaPlayer mediaplayer;

    private static final int AUDIO_SOURCE = MediaRecorder.AudioSource.VOICE_COMMUNICATION;
    private static final int ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private static final int CHANNEL_MASK = AudioFormat.CHANNEL_IN_MONO;
    static  FileOutputStream wavOut = null;

    public static void CreateWav(File... files){

        try {
            wavOut = new FileOutputStream(files[0]);
            //method to fill the wav formate header
            writeWavHeader(wavOut,CHANNEL_MASK,SAMPLE_RATE,ENCODING);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();

        }
        if(wavOut != null){
            try{
                wavOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    //first wavheader method to decide the values

    private static void writeWavHeader(OutputStream out, int channelMask, int sampleRate, int encoding) throws IOException {
        short channels;
        switch (channelMask) {
            case AudioFormat.CHANNEL_IN_MONO:
                channels = 1;
                break;
            case AudioFormat.CHANNEL_IN_STEREO:
                channels = 2;
                break;
            default:
                throw new IllegalArgumentException("Unacceptable channel mask");
        }

        short bitDepth;
        switch (encoding) {
            case AudioFormat.ENCODING_PCM_8BIT:
                bitDepth = 8;
                break;
            case AudioFormat.ENCODING_PCM_16BIT:
                bitDepth = 16;
                break;
            case AudioFormat.ENCODING_PCM_FLOAT:
                bitDepth = 32;
                break;
            default:
                throw new IllegalArgumentException("Unacceptable encoding");
        }

        writeWavHeader(out, channels, sampleRate, bitDepth);
    }

    //second waveheader method to write the header values

    private static void writeWavHeader(OutputStream out, short channels, int sampleRate, short bitDepth) throws IOException {
        // Convert the multi-byte integers to raw bytes in little endian format as required by the spec
        byte[] littleBytes = ByteBuffer
                .allocate(14)
                .order(ByteOrder.LITTLE_ENDIAN)
                .putShort(channels)
                .putInt(sampleRate)
                .putInt(sampleRate * channels * (bitDepth / 8))
                .putShort((short) (channels * (bitDepth / 8)))
                .putShort(bitDepth)
                .array();

        // Not necessarily the best, but it's very easy to visualize this way
        out.write(new byte[]{
                // RIFF header
                'R', 'I', 'F', 'F', // ChunkID
                0, 0, 0, 0, // ChunkSize (must be updated later)
                'W', 'A', 'V', 'E', // Format
                // fmt subchunk
                'f', 'm', 't', ' ', // Subchunk1ID
                16, 0, 0, 0, // Subchunk1Size
                1, 0, // AudioFormat
                littleBytes[0], littleBytes[1], // NumChannels
                littleBytes[2], littleBytes[3], littleBytes[4], littleBytes[5], // SampleRate
                littleBytes[6], littleBytes[7], littleBytes[8], littleBytes[9], // ByteRate
                littleBytes[10], littleBytes[11], // BlockAlign
                littleBytes[12], littleBytes[13], // BitsPerSample
                // data subchunk
                'd', 'a', 't', 'a', // Subchunk2ID
                0, 0, 0, 0, // Subchunk2Size (must be updated later)
        });
    }

    //method to write into the wav file

    public static void fillWav(byte[] buff,int bytesRead){

        try {
            wavOut.write(buff,0,bytesRead);

        } catch (IOException e) {

            e.printStackTrace();
        }

    }

    public static void closeWav(){

        if(wavOut != null){
            try{
                wavOut.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }

    }


    public static void updateWavHeader(File wav) throws IOException{

        byte[] sizes = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN)
                .putInt((int) (wav.length() - 8))
                .putInt((int) (wav.length() - 44))
                .array();

        RandomAccessFile accessWave = null;

        try{
            accessWave = new RandomAccessFile(wav,"rw");
            accessWave.seek(4);
            accessWave.write(sizes,0,4);
            accessWave.seek(40);
            accessWave.write(sizes,4,4);
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            if(accessWave != null){
                try{
                    accessWave.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }

    }

}
