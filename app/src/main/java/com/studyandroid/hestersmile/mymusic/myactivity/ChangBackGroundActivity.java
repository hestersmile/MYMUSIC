package com.studyandroid.hestersmile.mymusic.myactivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.studyandroid.hestersmile.mymusic.R;
import com.studyandroid.hestersmile.mymusic.adapter.GridListAdapter;

public class ChangBackGroundActivity extends AppCompatActivity {

    private TextView myslidetitle;
    private GridView backgrid;
    private int[] picarray;
    private LinearLayout mlayout;
    private ImageView myback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_background);
        getSupportActionBar().hide();
        SharedPreferences sharedPreferences = getSharedPreferences("mybackground",
                Context.MODE_PRIVATE);
        int backres = sharedPreferences.getInt("resource", R.drawable.ic_bac0);
        mlayout = (LinearLayout) findViewById(R.id.mylayout);
        mlayout.setBackgroundResource(backres);
        initArray();
        initWidet();
    }

    private void initArray() {
        picarray = new int[]{R.drawable.ic_bac1, R.drawable.ic_bac2, R.drawable.ic_bac3,
                R.drawable.ic_bac4, R.drawable.ic_bac5, R.drawable.ic_bac6, R.drawable.ic_bac7,
                R.drawable.ic_bac8, R.drawable.ic_bac9, R.drawable.ic_bac10, R.drawable.ic_bac11,
                R.drawable.ic_bac12, R.drawable.ic_bac13, R.drawable.ic_bac14, R.drawable.ic_bac15,
                R.drawable.ic_bac16, R.drawable.ic_bac17, R.drawable.ic_bac18, R.drawable.ic_bac19,
                R.drawable.ic_bac20};

    }

    private void initWidet() {
        myback=(ImageView)findViewById(R.id.back_img);
        myback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        int height = display.getHeight();
        int width = display.getWidth();
        myslidetitle = (TextView) findViewById(R.id.slide_bar_title);
        backgrid = (GridView) findViewById(R.id.grid_background);
        GridListAdapter myadpter = new GridListAdapter(backgrid, picarray, height, width, this);
        backgrid.setAdapter(myadpter);

        backgrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences preferences = getSharedPreferences(
                        "mybackground", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("resource", picarray[position]);
                editor.commit();
                mlayout.setBackgroundResource(picarray[position]);

            }
        });
    }

}
