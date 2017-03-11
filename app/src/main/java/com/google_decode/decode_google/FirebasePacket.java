package com.google_decode.decode_google;

/**
 * Created by nekhi on 3/11/2017.
 */

public class FirebasePacket {

    private byte[] _AESEncryptedArray;
    private byte[] _RSAEncryptedSecretKey;
    private int _width;
    private int _height;

    public FirebasePacket() {
        //Empty Constructor
    }

    public FirebasePacket(byte[] AESEncryptedArray, byte[] RSAEncryptedSecretKey, int width, int height){
        this._AESEncryptedArray = AESEncryptedArray;
        this._RSAEncryptedSecretKey = RSAEncryptedSecretKey;
        this._width = width;
        this._height=height;
    }

    public byte[] get_AESEncryptedArray() {
        return _AESEncryptedArray;
    }

    public void set_AESEncryptedArray(byte[] _AESEncryptedArray) {
        this._AESEncryptedArray = _AESEncryptedArray;
    }

    public byte[] get_RSAEncryptedSecretKey() {
        return _RSAEncryptedSecretKey;
    }

    public void set_RSAEncryptedSecretKey(byte[] _RSAEncryptedSecretKey) {
        this._RSAEncryptedSecretKey = _RSAEncryptedSecretKey;
    }

    public int get_width() {
        return _width;
    }

    public void set_width(int _width) {
        this._width = _width;
    }

    public int get_height() {
        return _height;
    }

    public void set_height(int _height) {
        this._height = _height;
    }
}
