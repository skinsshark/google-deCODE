package com.google_decode.decode_google;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by sharonzheng on 2017-03-10.
 */

public class QRBarcodeActivity extends AppCompatActivity{

    public static final int REQUEST_BARCODE = 53;

    public final static int WHITE = 0xFFFFFFFF;
    public final static int BLACK = 0xFF000000;

    public final static int WIDTH = 400;
    public final static int HEIGHT = 400;

    public FirebaseAuth mFirebaseAuth;
    public FirebaseUser currentUser;

    @BindView(R.id.qrCode)
    ImageView mQRImageView;

    boolean isScanning;

    @BindView(R.id.user_name_text)
    TextView mUserNameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_qr);
        ButterKnife.bind(this);

        setupFirebase();

        setupUI();

        try {
            Bitmap bitmap = encodeAsBitmap(currentUser.getUid());
            mQRImageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void setupUI() {
        getSupportActionBar().hide();
        mUserNameText.setText(currentUser.getDisplayName());
    }

    public void setupFirebase(){
        mFirebaseAuth = FirebaseAuth.getInstance();
        currentUser = mFirebaseAuth.getCurrentUser();
    }

    private Bitmap encodeAsBitmap(String str) throws WriterException {
        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, WIDTH, HEIGHT, null);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }

        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    @OnClick(R.id.scan_button)
    public void scan() {
        isScanning = true;
        new IntentIntegrator(this).initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (scanResult != null && scanResult.getContents() != null) {
            // TODO: 17/3/10 Add stuff
            Log.d("QRBarcodeActivity", scanResult.getContents());
        }
        switch (requestCode) {
            case REQUEST_BARCODE:
                if (resultCode != RESULT_OK) return;

                break;
        }
    }

}
