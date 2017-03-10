package com.google_decode.decode_google;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by James on 2017-03-10.
 */

public class DatabaseRetriever {

    private static final String TAG = "DatabaseRetriever";


    public static void sendMessage(String message){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");


        //write to the database
        myRef.setValue(message);




    }

    public static String getMessages(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");
        return null;
    }

    public static void getMessage(){
        // Read from the database

    }
}
