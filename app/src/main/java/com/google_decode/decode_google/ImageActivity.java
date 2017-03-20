package com.google_decode.decode_google;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ImageActivity extends AppCompatActivity {

    public static final String EXTRA_URI = "extra_uri";

    @BindView(R.id.image)
    ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        ButterKnife.bind(this);
        String uri = getIntent().getStringExtra(EXTRA_URI);

        Glide.with(this)
                .load(uri)
                .fitCenter()
                .into(mImageView);
    }

    @OnClick(R.id.image)
    public void click() {
        onBackPressed();
    }
}
