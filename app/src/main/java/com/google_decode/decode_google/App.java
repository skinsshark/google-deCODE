package com.google_decode.decode_google;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google_decode.decode_google.entity.User;

/**
 * Created by yifan on 17/3/11.
 */

public class App extends Application {

    private static final Gson sGson = new Gson();

    private static User currentUser;

    private static App application;

    public static App getInstance(){
        return application;
    }

    public static Context getContext() {
        return application.getApplicationContext();
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
        FirebaseDatabase.getInstance().getReference("users")
                .child(currentUser.uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        currentUser = User.parseFirebaseData(dataSnapshot);
                        Log.d("App", "Current user: " + currentUser.toString());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        databaseError.toException().printStackTrace();
                    }
                });
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
    }
}
