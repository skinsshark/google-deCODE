package com.google_decode.decode_google.entity;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yifan on 17/3/10.
 */

public class User {

    private static final String TAG = User.class.getSimpleName();

    private static final Gson sGson = new Gson();

    public static final String NAME = "name";
    public static final String UID = "uid";
    public static final String ORGANIZATION = "organization";
    public static final String CONTACTS = "contacts";

    public String name;
    public String uid;
    public List<String> contacts;
    public List<Organization> organizations;

    public User() {
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("name", name);
        result.put("contacts", contacts);
        result.put("organization", organizations);

        return result;
    }

    /**
     * Used for initial create for the user, using toMap() will cause user's contacts and organization
     * being overwritten
     *
     * @return
     */
    public Map<String, Object> create() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("name", name);
        return result;
    }

    /**
     * update local changes to firebase
     */
    public void update() {
        FirebaseDatabase.getInstance()
                .getReference("users")
                .child(uid)
                .updateChildren(this.toMap());
    }

    public static void getUserFromFirebase(final DatabaseReference scannedUser, final OnValueCallback callback) {
        scannedUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // {name=Adam Yang, uid=2vVTI9dd26fhjJYRd435Anoj6im1, organization=[{name=Google, organization_id=100}, {name=Shopify, organization_id=200}]}
                Log.d(TAG, "dataSnapshot.getValue():" + dataSnapshot.getValue());
                User user = parseFirebaseData(dataSnapshot);
                Log.d(TAG, user.toString());
                callback.done(user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                databaseError.toException().printStackTrace();
            }
        });
    }

    public static User parseFirebaseData(DataSnapshot dataSnapshot) {
        User user = new User();
        user.name = dataSnapshot.child(NAME).getValue() != null ?
                dataSnapshot.child(NAME).getValue().toString(): "Unknown username";

        user.uid = dataSnapshot.child(UID).getValue() != null ?
            dataSnapshot.child(UID).getValue().toString(): "Unknown uid";

        Type stringListType = new TypeToken<List<String>>() {}.getType();
        if (dataSnapshot.child(CONTACTS).getValue() != null) {
            user.contacts = sGson.fromJson(dataSnapshot.child(CONTACTS).getValue().toString(), stringListType);
        } else {
            user.contacts = new ArrayList<>();
        }

        Type organizationListType = new TypeToken<List<Organization>>() {}.getType();
        if (dataSnapshot.child(ORGANIZATION).getValue() != null) {
            user.organizations = sGson.fromJson(dataSnapshot.child(ORGANIZATION).getValue().toString(), organizationListType);
        } else {
            user.organizations = new ArrayList<>();
        }

        return user;
    }

    public static class Organization {
        public String name;
        public String organization_id;

        @Override
        public String toString() {
            return name + " " + organization_id;
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Name: ").append(name).append("\n")
                .append("Uid: ").append(uid).append("\n");

        if (contacts != null) {
            builder.append("Contacts: ");
            for (String contact : contacts) {
                builder.append(contact).append("\n");
            }
        }

        if (organizations != null) {
            builder.append("Organization: ");
            for (Organization organization : organizations) {
                builder.append(organization.toString()).append("\n");
            }
        }

        return builder.toString();
    }

    /**
     * It takes time for Firebase to retrieve the value
     */
    public interface OnValueCallback {
        void done(User retrievedUser);
    }

}
