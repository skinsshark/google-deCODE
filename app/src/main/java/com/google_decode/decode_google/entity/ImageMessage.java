package com.google_decode.decode_google.entity;

import com.google.firebase.database.DataSnapshot;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImageMessage {

    private String from;
    private String to;
    private String fromId;
    private String toId;
    private String uri;
    private String timestamp;
    private  boolean status;
    private int width;
    private int height;
    private String index;


    private static final Gson sGson = new Gson();

    public static final String FROM = "from";
    public static final String FROMID = "fromId";
    public static final String TO = "to";
    public static final String TOID = "toId";
    public static final String URI = "uri";
    public static final String TIMESTAMP = "timestamp";
    public static final String INDEX = "index";
    public static final String FIREBASEPACKET = "firebasepacket";
    //public static final String IMG = "img";

    public ImageMessage(){

    }
    public ImageMessage(String from, String fromId, String to, String toId, String uri){
        this.from = from;
        this.fromId = fromId;
        this.to = to;
        this.toId = toId;
        this.uri = uri;
        this.timestamp =  DateFormat.getDateTimeInstance().format(new Date());
        this.status = false;
    }

    public String getFrom(){
        return this.from;
    }

    public String getToId(){
        return this.toId;
    }
    public String getFromId(){
        return this.fromId;
    }

    public String getUri(){
        return this.uri;
    }
    public String getIndex(){
        return index;
    }
    public void setIndex(String index){
        this.index = index;
    }
    public Map<String,Object> create(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("from", from);
        result.put("fromId", fromId);
        result.put("to", to);
        result.put("toId", toId);
        result.put("uri", uri);
        result.put("timestamp", timestamp);
        result.put("status", status);
        result.put("index", index);
        return result;

    }

    public static List<ImageMessage> parseImageList(DataSnapshot dataSnapshot) {
        List<ImageMessage> result = new ArrayList<>();

        for (DataSnapshot snapshot: dataSnapshot.getChildren()){
            result.add(parseSingleImage(snapshot));
        }

        return result;
    }
    public static ImageMessage parseSingleImage(DataSnapshot dataSnapshot) {
        ImageMessage im = new ImageMessage();
        im.fromId = dataSnapshot.child(FROMID).getValue() != null ?
                dataSnapshot.child(FROMID).getValue().toString(): "Unknown from id";
        im.from = dataSnapshot.child(FROM).getValue() != null ?
                dataSnapshot.child(FROM).getValue().toString(): "Unknown from id";
        im.to = dataSnapshot.child(TO).getValue() != null ?
                dataSnapshot.child(TO).getValue().toString(): "Unknown from id";
        im.toId = dataSnapshot.child(TOID).getValue() != null ?
                dataSnapshot.child(TOID).getValue().toString(): "Unknown from id";
        im.fromId = dataSnapshot.child(FROMID).getValue() != null ?
                dataSnapshot.child(FROMID).getValue().toString(): "Unknown from id";
        im.index = dataSnapshot.child(INDEX).getValue() != null ?
                dataSnapshot.child(INDEX).getValue().toString(): "Unknown from id";

        im.uri = dataSnapshot.child(URI).getValue() != null?
                dataSnapshot.child(URI).getValue().toString() : "Unknown from id";

        return im;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append("from").append(from).append('\n')
                .append("to").append(to).append('\n')
                .append("fromId").append(fromId).append('\n')
                .append("toId").append(toId).append('\n')
                .append("uri").append(uri).append('\n')
                .append("timestamp").append(timestamp).append('\n');

        return builder.toString();
    }
}
