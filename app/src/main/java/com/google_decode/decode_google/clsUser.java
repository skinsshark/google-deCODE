package com.google_decode.decode_google;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by James on 2017-03-10.
 */

public class clsUser {

    private String organization;
    private String name;
    private String uid;
    private String qrCode;
    private Map<String,Object> contacts;
    public clsUser(String user_id){

    }


    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("name", name);
        result.put("organization", organization);
        result.put("qrcode", qrCode);
        result.put("contacts", contacts);


        return result;
    }

}
