package com.studyandroid.hestersmile.mymusic.myactivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.studyandroid.hestersmile.mymusic.R;

public class AboutActivity extends AppCompatActivity {

    private ImageView myback;
    private RelativeLayout mlayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        getSupportActionBar().hide();
        SharedPreferences sharedPreferences = getSharedPreferences("mybackground",
                Context.MODE_PRIVATE);
        int backres = sharedPreferences.getInt("resource", R.drawable.ic_bac0);
        mlayout = (RelativeLayout) findViewById(R.id.aboutlayout);
        mlayout.setBackgroundResource(backres);
        myback = (ImageView) findViewById(R.id.back_img);
        myback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });
    }
}
