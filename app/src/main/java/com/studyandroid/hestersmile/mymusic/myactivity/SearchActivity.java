package com.studyandroid.hestersmile.mymusic.myactivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.studyandroid.hestersmile.mymusic.R;
import com.studyandroid.hestersmile.mymusic.adapter.GridListAdapter;
import com.studyandroid.hestersmile.mymusic.adapter.PopMusicListAdapter;
import com.studyandroid.hestersmile.mymusic.adapter.SearchMusicListAdapter;
import com.studyandroid.hestersmile.mymusic.javabean.music;
import com.studyandroid.hestersmile.mymusic.service.MusicService;
import com.studyandroid.hestersmile.mymusic.util.Musicutil;
import com.studyandroid.hestersmile.mymusic.util.Singleton;

import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private TextView myslidetitle;
    private int[] picarray;
    private LinearLayout mlayout;
    private ListView mylist;
    private TextView mynoresult;
    private ListView myresultlist;
    private ListView myhistorylist;
    private Button myseachbtn;
    private SearchView mysearchedit;
    private List<music> mymusiclist;
    private ImageView myback;
    private int listposition = -1;
    private int playflag;
    private int listPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getSupportActionBar().hide();
        mymusiclist = Musicutil.getMp3Infos(this);
        for (int i = 0; i < mymusiclist.size(); i++) {
            mymusiclist.get(i).setListposition(i);
        }
        initWidet();
    }

    private void initWidet() {

        myslidetitle = (TextView) findViewById(R.id.slide_bar_title);
        mynoresult = (TextView) findViewById(R.id.search_noresult);
        mysearchedit = (SearchView) findViewById(R.id.search_view);
        myback = (ImageView) findViewById(R.id.back_img);
        myback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });
        mylist = (ListView) findViewById(R.id.serach_list);
        mylist.setTextFilterEnabled(true);
        mylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent myintent = new Intent(SearchActivity.this, PlayMusicActivity.class);
                Bundle myBundle = new Bundle();
                myBundle.putSerializable("musicObject", mymusiclist.get(SearchMusicListAdapter.filterData.get(position).getListposition()));
                myintent.putExtras(myBundle);
                myintent.putExtra("listposition", SearchMusicListAdapter.filterData.get(position).getListposition());
                myintent.putExtra("playstyle", 1);
                startActivity(myintent);

            }
        });
        SearchMusicListAdapter myadapter = new SearchMusicListAdapter(mylist, mymusiclist, this);
        mylist.setAdapter(myadapter);
        mynoresult.setVisibility(View.GONE);
        myslidetitle.setText("搜索歌曲");
        mysearchedit.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText != null && newText.length() > 0) {
                    mylist.setFilterText(newText);   // 设置ListView的过滤关键字
                } else {
                    mylist.clearTextFilter();
                }
                return true;
            }
        });

    }

}
