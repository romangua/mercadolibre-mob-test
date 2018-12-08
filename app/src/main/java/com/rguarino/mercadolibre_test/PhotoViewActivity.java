package com.rguarino.mercadolibre_test;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.chrisbanes.photoview.PhotoView;
import com.github.chrisbanes.photoview.PhotoViewAttacher;

public class PhotoViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);

        PhotoView photoView = findViewById(R.id.photoView);
        Glide.with(this)
                .load(getIntent().getStringExtra("url"))
                .placeholder(Color.BLACK)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(photoView);

        PhotoViewAttacher mAttacher = new PhotoViewAttacher(photoView);
        mAttacher.update();

        Button btnCerrar = findViewById(R.id.btnClose);
        btnCerrar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
