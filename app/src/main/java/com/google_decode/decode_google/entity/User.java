package com.google_decode.decode_google.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yifan on 17/3/10.
 */

public class User {

    public String name;
    public String uid;
    public List<Integer> contacts;
    public List<Integer> organizations;

    public User(String name, String uid, List<Integer> contacts, List<Integer> organizations) {
        this.name = name;
        this.uid = uid;
        this.contacts = contacts;
        this.organizations = organizations;
    }

    public User(String name, String uid) {
        this.name = name;
        this.uid = uid;
        contacts = new ArrayList<>();
        organizations = new ArrayList<>();
    }

    public Map<String, Object> toMap(){
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
     * @return
     */
    public Map<String, Object> create() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("name", name);
        return result;
    }

}
