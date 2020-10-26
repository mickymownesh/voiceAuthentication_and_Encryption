package com.application.callAuth;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class fireBaseSupport {

    private FirebaseDatabase masterDB = FirebaseDatabase.getInstance();
    private DatabaseReference masterDBRefrence = masterDB.getReference();

    //audio storage reference

    fireBaseSupport(){

    masterDBRefrence = masterDB.getReference().child("user");
    masterDBRefrence.setValue("test");

    }
}
