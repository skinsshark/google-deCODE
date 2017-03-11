package com.google_decode.decode_google;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

public class mailbox extends AppCompatActivity {

    //the URI used to get the picture
    private Uri downloadUrl;

    ImageButton imageButton;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mailbox);

        imageButton = (ImageButton) findViewById(R.id.imageButton);
        imageView = (ImageView) findViewById(R.id.imageView2);

        imageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                imageView.setImageResource(android.R.color.transparent);
                Log.v("test", "imageView on mailbox cleared");

                //GET Url from
                downloadPic(downloadUrl.toString());


            }
        });



    }

    //downloading Picture from Firebase to device
    private void downloadPic(String src) {
        new DownloadImageAsync(this).execute(src);
    }
}
