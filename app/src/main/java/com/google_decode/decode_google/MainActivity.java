package com.google_decode.decode_google;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.util.Log;
import android.content.pm.PackageManager;
import android.Manifest.permission;
import android.os.Build;
import android.Manifest.permission_group;
import android.content.Context;


import com.google.firebase.appindexing.Action;
import com.google.firebase.appindexing.FirebaseUserActions;
import com.google.firebase.appindexing.builders.Actions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;

public class MainActivity extends Activity{ //used to be "extends AppCompatActivity"
    //will i get an error for changing to activity ??

    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;


    private static final String TAG = "MainActivity";
    public static final String ANONYMOUS = "anonymous";

    private String mUsername;
    private String mPhotoUrl;

    //camera stuff
    Button button;
    ImageView imageView;
    Bitmap bmp;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Set default username is anonymous.
        mUsername = ANONYMOUS;
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return;
        } else {
            mUsername = mFirebaseUser.getDisplayName();
            if (mFirebaseUser.getPhotoUrl() != null) {
                mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
            }
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView)findViewById(R.id.image_view);
        imageView.setAdjustViewBounds(false);
        imageView.setBackgroundColor(0xff0000);

        Log.v("test", "height: " + imageView.getHeight() + " and width: " + imageView.getWidth());
        Log.v("test", "x and y coordinates: " + imageView.getX() + " " + imageView.getY());
        button = (Button)findViewById(R.id.button); //space? error??
        //imageView = (ImageView)findViewById(R.id.image_view);
        //maybe fix - put this line on other section
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String imageFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/picture.jpg";
                Log.v("test", imageFilePath);
                //File folder = new File(Environment.getExternalStorageDirectory(), "myCoolImages");
                File imageFile = new File(imageFilePath);

                Context context = MainActivity.this;

                Uri imageFileUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", imageFile);

                //Uri imageFileUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", imageFile);

                //Uri imageFileUri = Uri.fromFile(imageFile); // convert path to Uri

                Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                camera_intent.putExtra (MediaStore.EXTRA_OUTPUT, imageFileUri);
                startActivityForResult(camera_intent, REQUEST_IMAGE_CAPTURE);
            }
        });

        isStoragePermissionGranted();
        isCameraPermissionGranted();

    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("test","Permission is granted");
                return true;
            } else {

                Log.v("test","Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("test","Permission is granted");
            return true;
        }
    }

    public  boolean isCameraPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("test","camera Permission is granted");
                return true;
            } else {

                Log.v("test","camera Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("test","camera Permission is granted");
            return true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) { //add && requestCode == REQUEST_IMAGE_CAPTURE ???

            imageView.setBackgroundColor(0xff0000);
            String imageFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/picture.jpg";
            Log.v("test", imageFilePath);

            //decode it
            BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
            bmpFactoryOptions.inJustDecodeBounds = false;

            //pass intent
            bmp = BitmapFactory.decodeFile(imageFilePath);

            if (bmp != null) {
                Log.v("test", "bmp is valid");
            } else {
                Log.v("test", "bmp is NULL");
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    imageView.setImageBitmap(bmp);
                    imageView.invalidate();
                }
            });



            //imageView.setImageBitmap(bmp);
            Log.v("test", "height: " + imageView.getHeight() + " and width: " + imageView.getWidth());
            Log.v("test", "x and y coordinates: " + imageView.getX() + " " + imageView.getY());

        }
        //String path = "myCoolImages/cam_image.jpg";
        //imageView.setImageDrawable(Drawable.createFromPath(path));
        //does this mean only one image at a time?
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        return Actions.newView("Main", "http://[ENTER-YOUR-URL-HERE]");
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        FirebaseUserActions.getInstance().start(getIndexApiAction());
    }

    @Override
    public void onStop() {

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        FirebaseUserActions.getInstance().end(getIndexApiAction());
        super.onStop();
    }
}
