package com.application.callAuth;

import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageRegistrar;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import java.io.File;

import androidx.annotation.NonNull;


public class fireBaseSupport {

    private FirebaseDatabase masterDB = FirebaseDatabase.getInstance();
    private DatabaseReference masterDBRefrence = masterDB.getReference();
    static FirebaseStorage storage = FirebaseStorage.getInstance();
    private static final String LOG_TAG = "Firebase Support";
    static File LocalFile = null;


    //audio storage reference
    fireBaseSupport(int i){


        masterDBRefrence = masterDB.getReference().child(String.valueOf(i));
        masterDBRefrence.setValue("checkd In : ");

    }

    public static void uploadAudio(File wavAudio){

        StorageReference cloudReference = storage.getReferenceFromUrl("gs://callauth-253e1.appspot.com/");
        //child reference
        StorageReference cloudAudioReference = cloudReference.child("audio.wav");

        File audioFile = wavAudio;
        InputStream AudioStreamFile = null;

        try {
           AudioStreamFile = new FileInputStream(audioFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        UploadTask uploadTask;
        uploadTask = cloudAudioReference.putStream(AudioStreamFile);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
            }
        });


    }

    public static File downloadAudio(File downloadAudio){

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference cloudReference = storage.getReference().child("audio.wav");
        //child reference


        /*try {
            LocalFile = File.createTempFile("testAudio","wav");
        } catch (IOException e) {
            Log.i(LOG_TAG,"Error in creating local file");
        }*/
        cloudReference.getFile(downloadAudio).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(LOG_TAG,"Audio file not downloaded in the file");
            }
        }).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                //Log.i(LOG_TAG,"Audio file size : "+LocalFile.length());
                Log.i(LOG_TAG,"audio file sucessfully downloaded");
               // Log.i(LOG_TAG,"audio file path : "+ LocalFile.getPath());

            }
        });

        if(LocalFile == null){
            Log.i(LOG_TAG,"Log file is still null");
        }else {
            Log.i(LOG_TAG, "audio file size : " + LocalFile.length());
        }
        return LocalFile;
    }


}
