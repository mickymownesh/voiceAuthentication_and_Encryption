package hw.dt83.udpchat;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashMap;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class audioBroadcaster extends Activity {

    private static final String LOG_TAG = "audioBroadcaster";
    public static final int BROADCAST_PORT = 50001; // Socket on which packets are sent/received
    private static final int BROADCAST_INTERVAL = 10000; // Milliseconds
    private static final int BROADCAST_BUF_SIZE = 1024;
    private boolean BROADCAST = true;
    private boolean LISTEN = true;
    private HashMap<String, InetAddress> contacts;
    private InetAddress broadcastIP;

    //variables for call

    private static final int SAMPLE_RATE = 8000; // Hertz
    private static final int SAMPLE_INTERVAL = 20; // Milliseconds
    private static final int SAMPLE_SIZE = 2; // Bytes
    private static final int BUF_SIZE = SAMPLE_INTERVAL * SAMPLE_INTERVAL * SAMPLE_SIZE * 2; //Bytes
    private InetAddress address; // Address to call
    private int port = 50000; // Port the packets are addressed to
    private boolean mic = false; // Enable mic?
    private boolean speakers = false; // Enable speakers?
    final Button startSample = (Button) findViewById(R.id.sendSample);
    private Button stopSample = (Button) findViewById(R.id.stopSample);

    //constructor

    audioBroadcaster(){

        broadcastName();
    }



    //broadcasting audio method

    public void broadcastName(final String name, final InetAddress broadcastIP) {

        //stop broadcasting

        startSample.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mic = true;
            }
        });

        //stop broadcasting

        stopSample.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mic = false;
            }
        });


        // Broadcasts the name of the device at a regular interval
        Log.i(LOG_TAG, "Broadcasting audio samples started !");
        Thread broadcastThread = new Thread(new Runnable() {

            @Override
            public void run() {

                Log.i(LOG_TAG, "Send thread started. Thread id: " + Thread.currentThread().getId());
                AudioRecord audioRecorder = new AudioRecord (MediaRecorder.AudioSource.VOICE_COMMUNICATION, SAMPLE_RATE,
                        AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT,
                        AudioRecord.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT)*10);
                int bytes_read = 0;
                int bytes_sent = 0;
                byte[] buf = new byte[BUF_SIZE];
                try {
                    // Create a socket and start recording
                    Log.i(LOG_TAG, "Packet destination: " + address.toString());
                    DatagramSocket socket = new DatagramSocket();
                    audioRecorder.startRecording();

                    String request = "SAMP:"+name;
                    byte[] message = request.getBytes();
                    DatagramSocket sendsock = new DatagramSocket();
                    sendsock.setBroadcast(true);
                    DatagramPacket pac = new DatagramPacket(message, message.length, broadcastIP, BROADCAST_PORT);
                    socket.send(pac);


                    while(mic) {
                        // Capture audio from the mic and transmit it

                        bytes_read = audioRecorder.read(buf, 0, BUF_SIZE);
                        DatagramPacket packet = new DatagramPacket(buf, bytes_read, broadcastIP, BROADCAST_PORT);
                        socket.send(packet);
                        bytes_sent += bytes_read;
                        Log.i(LOG_TAG, "Total bytes sent: " + bytes_sent);
                        Thread.sleep(SAMPLE_INTERVAL, 0);
                    }

                    // Stop recording and release resources

                    audioRecorder.stop();
                    audioRecorder.release();
                    socket.disconnect();
                    socket.close();
                    mic = false;
                    return;

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (SocketException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        broadcastThread.start();
    }


    //method for getting the broadcast ip address

    private InetAddress getBroadcastIp() {
        // Function to return the broadcast address, based on the IP address of the device
        try {

            @SuppressLint("WifiManagerLeak") WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipAddress = wifiInfo.getIpAddress();
            String addressString = toBroadcastIp(ipAddress);
            InetAddress broadcastAddress = InetAddress.getByName(addressString);
            return broadcastAddress;
        }
        catch(UnknownHostException e) {

            Log.e(LOG_TAG, "UnknownHostException in getBroadcastIP: " + e);
            return null;
        }

    }

    //method to convert normal ip to broadcast ip

    private String toBroadcastIp(int ip) {
        // Returns converts an IP address in int format to a formatted string
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                "255";
    }






}