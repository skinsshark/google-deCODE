package com.google_decode.decode_google;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class AES {

    SecretKey key;

    public AES(){
        this.key = this.generateKey();
    }

    public SecretKey generateKey(){
        KeyGenerator keyGenerator = null;
        try {
            keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(128);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return keyGenerator.generateKey();
    }

    public byte[] encrypt(byte[] array, SecretKey key){
        byte[] ciphertext = null;
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            ciphertext = cipher.doFinal(array);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ciphertext;
    }

    public byte[] decrypt(byte[] ciphertext, SecretKey key){
        byte[] finalBytes = null;
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key);
            finalBytes = cipher.doFinal(ciphertext);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return finalBytes;
    }
}
