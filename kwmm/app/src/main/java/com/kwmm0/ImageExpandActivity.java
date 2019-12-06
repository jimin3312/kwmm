package com.kwmm0;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import com.bumptech.glide.Glide;

import uk.co.senab.photoview.PhotoView;

public class ImageExpandActivity extends AppCompatActivity {
    PhotoView photoView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.image_expand_layout);
        getWindow().setStatusBarColor(Color.rgb(0, 0, 0));

        photoView= (PhotoView) findViewById(R.id.photo_view);
        Glide.with(ImageExpandActivity.this)
                .load(getIntent().getStringExtra("image"))
                .into(photoView);
    }
}
