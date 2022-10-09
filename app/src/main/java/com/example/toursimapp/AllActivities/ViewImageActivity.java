package com.example.toursimapp.AllActivities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.toursimapp.Models.Functions;
import com.example.toursimapp.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class ViewImageActivity extends AppCompatActivity {

    ImageView imagefull;
    String imageurl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new Functions().checkTheme(getApplicationContext());
        setContentView(R.layout.activity_view_image);

        imagefull = findViewById(R.id.imagefull);
        ImageButton back_viewimagefull_btn = findViewById(R.id.back_viewimagefull_btn);
        Intent getImagefull = getIntent();

        back_viewimagefull_btn.setOnClickListener(v -> finish());

        imageurl = getImagefull.getStringExtra("FullImageURL");
        new Viewimage(imageurl).execute();

    }

    @SuppressLint("StaticFieldLeak")
    private class Viewimage extends AsyncTask<String, Integer, Bitmap> {
        String imgLink;

        public Viewimage(String link) {
            imgLink = link;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            Bitmap bitmap = null;
            InputStream inputStream = null;
            try {
                URL imageurl = new URL(imgLink);
                inputStream = (InputStream) imageurl.getContent();
                bitmap = BitmapFactory.decodeStream(inputStream);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            imagefull.setImageBitmap(bitmap);
        }
    }
}