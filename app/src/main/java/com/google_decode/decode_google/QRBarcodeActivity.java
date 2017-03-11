package com.google_decode.decode_google;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.google_decode.decode_google.entity.User;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by sharonzheng on 2017-03-10.
 */

public class QRBarcodeActivity extends AppCompatActivity {

    private static final String TAG = QRBarcodeActivity.class.getSimpleName();

    public static final int REQUEST_BARCODE = 53;

    public final static int WHITE = 0xFFFFFFFF;
    public final static int BLACK = 0xFF000000;

    public final static int WIDTH = 400;
    public final static int HEIGHT = 400;

    public FirebaseAuth mFirebaseAuth;
    public FirebaseUser currentUser;
    public FirebaseDatabase mFirebaseDatabase;

    @BindView(R.id.qrCode)
    ImageView mQRImageView;

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

    public void setupFirebase() {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
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
        new IntentIntegrator(this).initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (scanResult != null && scanResult.getContents() != null) {
            Log.d("QR Scan Result", scanResult.getContents());
            handleQRResult(scanResult.getContents());
        }
        switch (requestCode) {
            case REQUEST_BARCODE:
                if (resultCode != RESULT_OK) return;

                break;
        }
    }

    private void handleQRResult(String qrResult) {
        DatabaseReference users = mFirebaseDatabase.getReference("users");
        DatabaseReference scannedUser = users.child(qrResult);

        // No user found
        if (TextUtils.isEmpty(scannedUser.child("uid").getKey())) {
            Intent viewIntent = new Intent(Intent.ACTION_VIEW);
            viewIntent.setData(Uri.parse(qrResult));
        } else {
            // user is found
            User.getUserFromFirebase(scannedUser, new User.OnValueCallback() {
                @Override
                public void done(User retrievedUser) {
                    Log.d(TAG, (retrievedUser != null ? retrievedUser.toString() : "Retrieved User is null"));
                    Log.d(TAG, (App.getCurrentUser() != null ? App.getCurrentUser().toString() : "Current User is null"));
                    final User targetUser = retrievedUser;
                    if (App.getCurrentUser().uid.equals(targetUser.uid)) { // user is adding himself
                        Toast.makeText(QRBarcodeActivity.this, "It\'s okay man, I have no friends either.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (App.getCurrentUser().contacts.contains(targetUser.uid)) { // if the scanned user is already a contact
                        Toast.makeText(QRBarcodeActivity.this, "This user is already your contact", Toast.LENGTH_SHORT).show();
                        // TODO: 17/3/11 go back to main and enter the chat
                        return;
                    }

                    // if the scanned user is not a contact
                    new AlertDialog.Builder(QRBarcodeActivity.this)
                            .setTitle("Add Contact")
                            .setMessage("Add " + targetUser.name + " as a new contact?")
                            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // add scanned user as friend, also the other way around
                                    User currentUser = App.getCurrentUser();
                                    currentUser.contacts.add(targetUser.uid);
                                    targetUser.contacts.add(currentUser.uid);

                                    currentUser.update();
                                    targetUser.update();

                                    finish();
                                }
                            })
                            .setNegativeButton("NO", null)
                            .show();

                }
            });
        }
    }


}
