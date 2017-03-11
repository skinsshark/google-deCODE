package com.google_decode.decode_google;

import android.net.Uri;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google_decode.decode_google.entity.ImageMessage;



public class DatabaseRetriever {

    private static final String TAG = "DatabaseRetriever";


    public static void sendMessage(ImageMessage imageMessage){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference mailBox = database
                .getReference("users")
                .child(imageMessage.getToId())
                .child("mailbox");
        String key = mailBox.push().getKey();
        imageMessage.setIndex(key);
        mailBox.child(key).updateChildren(imageMessage.create());
        Log.i(TAG, "Sent message from :" + currentUser.getUid());
    }

    public static void deleteMessage(String key){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserID = currentUser.getUid();
        DatabaseReference mailBox = database.getReference("users")
                .child(currentUserID)
                .child("mailbox");

        mailBox.child(key).removeValue();
        Log.i(TAG, "Message key:" +key +" deleted.");
    }

    public static void readMessage(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserID = currentUser.getUid();
        DatabaseReference mailBox = database.getReference(currentUserID).child("mailbox");

    }

    public static void sendUri(Uri imageUri, String userName){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("UserName");
        //write to the database
        myRef.setValue(userName);
        myRef = database.getReference("Uri");
        myRef.setValue(imageUri.toString());


    }
}
