package com.google_decode.decode_google;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;


    private static final String TAG = "MainActivity";
    public static final String ANONYMOUS = "anonymous";

    private String mUsername;
    private String mPhotoUrl;
    private Uri downloadUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set default username is anonymous.
        mUsername = ANONYMOUS;
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        //uploadPic("/storage/emulated/0/DCIM/Camera/IMG_20170310_151727.jpg" , "IMG_20170310_151727.jpg");
        //Log.v("ImageUpload",downloadUrl.toString());
        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, SignInActivity.class));
            finish();
        } else {
            mUsername = mFirebaseUser.getDisplayName();
            if (mFirebaseUser.getPhotoUrl() != null) {
                mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
            }


//            final Button button = (Button) findViewById(R.id.btnTesting);
//            button.setOnClickListener(new View.OnClickListener() {
//                public void onClick(View v) {
//                    DatabaseRetriever.sendMessage("Working");
//
//                    DatabaseRetriever.getMessage();
//                }
//            });



//            FirebaseDatabase database = FirebaseDatabase.getInstance();
//            DatabaseReference myRef = database.getReference("message");
//
//            myRef.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    // This method is called once with the initial value and again
//                    // whenever data at this location is updated.
//                    String value = dataSnapshot.getValue(String.class);
//                    Log.d(TAG, "Value is: " + value);
//                    final TextView textViewTesting = (TextView) findViewById(R.id.textViewTesting);
//                    textViewTesting.setText(value);
//
//                }
//
//                @Override
//                public void onCancelled(DatabaseError error) {
//                    // Failed to read value
//                    Log.w(TAG, "Failed to read value.", error.toException());
//                }
//            });
        }




    }

    //Accepts the local file location of the image as well as the name that should be used to store the image on FireBase.
    private void uploadPic(String fileLoc, String fileName){
        //Setting up FireBase
        StorageReference mStorageRef;
        mStorageRef = FirebaseStorage.getInstance().getReference();

        //Retrieving Local Image URI
        Uri file = Uri.fromFile(new File(fileLoc));
        StorageReference riversRef = mStorageRef.child("/images/"+fileName);

        //Uploading the file to FireBase storage
        riversRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        //The url of the file online
                        //noinspection VisibleForTests
                        downloadUrl = taskSnapshot.getDownloadUrl();
                        mFirebaseAuth = FirebaseAuth.getInstance();
                        mFirebaseUser = mFirebaseAuth.getCurrentUser();
                        //Uploads the image details to FireBase Database
                        DatabaseRetriever.sendUri(downloadUrl,mFirebaseUser.getDisplayName());
                        Log.v("ImageUpload",downloadUrl.toString());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                        Toast.makeText(getApplicationContext(),"Sorry the file wasn't sent!",Toast.LENGTH_LONG).show();
                    }
                });

    }

    private void downloadPic(String src ){
        //This works using file name which downlaods directly from storage
//        File localFile = null;
//        try {
//            File storageDir = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//            Log.v("Downloader" , storageDir.toString());
//            localFile = File.createTempFile("downloadedImage", "jpg", storageDir);
//            Log.v("Downloader" , "Local file made");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        StorageReference mStorageRef;
//        mStorageRef = FirebaseStorage.getInstance().getReference();
//        StorageReference riversRef = mStorageRef.child("/images/"+fileName);
//        if (localFile != null) {
//            riversRef.getFile(localFile)
//                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
//                            // Successfully downloaded data to local file
//                            // ...
//                            Log.v("Downloader" , "File downloaded and stored");
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception exception) {
//                    // Handle failed download
//                    // ...
//                }
//            });
//        }

        new DownloadImageAsync(this).execute(src);
    }
}

    @OnClick(R.id.my_qr_icon)
    public void launchQr(View v) {
        Intent getBarcode = new Intent(MainActivity.this,QRBarcodeActivity.class);
        startActivity(getBarcode);
        finish();
    }
}

