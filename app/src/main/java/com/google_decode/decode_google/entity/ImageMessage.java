package com.google_decode.decode_google.entity;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ImageMessage {

    private String from;
    private String to;
    private String url;
    private String timestamp;
    private  boolean status;
    public ImageMessage(String from, String to, String url){
        this.from = from;
        this.to = to;
        this.url = url;
        this.timestamp =  DateFormat.getDateTimeInstance().format(new Date());
        this.status = false;
    }

    public Map<String,Object> create(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("from", from);
        result.put("to", to);
        result.put("url", url);
        result.put("timestamp", timestamp);
        result.put("status", status);

        return result;

    }
}
