package com.google_decode.decode_google;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.security.*;
import javax.crypto.*;

/**
 * Created by Temporary on 2017-03-10.
 */

public class RSA {

    KeyPairGenerator kpg;
    KeyPair kp;
    PublicKey publicKey;
    PrivateKey privateKey;

    public RSA(){
        try {
            this.generateKeys();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public void generateKeys() throws NoSuchAlgorithmException {
        this.kpg = KeyPairGenerator.getInstance("RSA");
        this.kpg.initialize(1024);
        this.kp = this.kpg.genKeyPair();
        this.publicKey = this.kp.getPublic();
        this.privateKey = this.kp.getPrivate();
    }

    public PublicKey getPubKey(){
        return this.publicKey;
    }

    /*public byte[] RSAEncrypt(final Bitmap b, PublicKey p) throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        byte[] array = bitmapToByte(b);

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, p);
        byte[] encryptedBytes = cipher.doFinal(array);
        //System.out.println("EEncrypted?????" + org.apache.commons.codec.binary.Hex.encodeHexString(encryptedBytes));
        return encryptedBytes;
    }*/

    public byte[] RSAEncrypt(byte[] array, PublicKey p) throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, p);
        byte[] encryptedBytes = cipher.doFinal(array);
        //System.out.println("EEncrypted?????" + org.apache.commons.codec.binary.Hex.encodeHexString(encryptedBytes));
        return encryptedBytes;
    }

    /*public Bitmap RSADecrypt(final byte[] encryptedBytes) throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        Cipher cipher1 = Cipher.getInstance("RSA");
        cipher1.init(Cipher.DECRYPT_MODE, this.privateKey);
        byte[] decryptedBytes = cipher1.doFinal(encryptedBytes);
        return byteToBitmap(decryptedBytes);
    }*/

    public byte[] RSADecrypt(final byte[] encryptedBytes) throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        Cipher cipher1 = Cipher.getInstance("RSA");
        cipher1.init(Cipher.DECRYPT_MODE, this.privateKey);
        byte[] decryptedBytes = cipher1.doFinal(encryptedBytes);
        return decryptedBytes;
    }

    public static byte[] bitmapToByte(Bitmap b){
        int numBytes = b.getByteCount();
        ByteBuffer buffer = ByteBuffer.allocate(numBytes); //Create a new buffer
        b.copyPixelsToBuffer(buffer); //Move the byte data to the buffer
        return buffer.array(); //Get the underlying array containing the data.
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        b.compress(Bitmap.CompressFormat.JPEG, 0, bos);
//        return bos.toByteArray();
    }

    public static Bitmap byteToBitmap(byte[] array, int width, int height){
//        Bitmap b;
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//        options.inJustDecodeBounds = true;
//
//
//        b = BitmapFactory.decodeByteArray(array, 0, array.length, options);
//        return b;

        Bitmap.Config configBmp = Bitmap.Config.ARGB_8888;
        Log.v("bitmap_config", configBmp.toString());
        Bitmap bitmap_tmp = Bitmap.createBitmap(width, height, configBmp);
        ByteBuffer buffer = ByteBuffer.wrap(array);
        bitmap_tmp.copyPixelsFromBuffer(buffer);

        return bitmap_tmp;
    }

    public static byte[] wrapKey(PublicKey pubKey, SecretKey symKey)
            throws InvalidKeyException, IllegalBlockSizeException {
        try {
            final Cipher cipher = Cipher
                    .getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding");
            cipher.init(Cipher.WRAP_MODE, pubKey);
            final byte[] wrapped = cipher.wrap(symKey);
            return wrapped;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new IllegalStateException(
                    "Java runtime does not support RSA/ECB/OAEPWithSHA1AndMGF1Padding",
                    e);
        }
    }

    public static SecretKey unwrapKey(PrivateKey privKey, byte[] secretKeyByte)
            throws InvalidKeyException, IllegalBlockSizeException {
        try {
            final Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding");
            cipher.init(Cipher.UNWRAP_MODE, privKey);
            final SecretKey unwrapped = (SecretKey) cipher.unwrap(secretKeyByte, "RSA/ECB/OAEPWithSHA1AndMGF1Padding", Cipher.SECRET_KEY);
            return unwrapped;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new IllegalStateException(
                    "Java runtime does not support RSA/ECB/OAEPWithSHA1AndMGF1Padding",
                    e);
        }
    }
}
