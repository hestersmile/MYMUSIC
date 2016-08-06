package com.studyandroid.hestersmile.mymusic.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.studyandroid.hestersmile.mymusic.R;
import com.studyandroid.hestersmile.mymusic.action.ActionDefine;
import com.studyandroid.hestersmile.mymusic.adapter.PopMusicListAdapter;
import com.studyandroid.hestersmile.mymusic.util.Musicutil;

/**
 * Created by hestersmile on 2016/5/25.
 */
public class SearchResultFragment extends Fragment {
    private View activityView;
    private Bitmap bitmap;
    private ImageView mypic;
    private long picid;
    private long picbitmp;
    private ListView myresultlist;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activityView = inflater.inflate(R.layout.fragment_search_result, null);
        myresultlist = (ListView) activityView.findViewById(R.id.search_result);
//        PopMusicListAdapter myadpter = new PopMusicListAdapter();
//        myresultlist.setAdapter(myadpter);
        myresultlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
        return activityView;
    }

    private void iniview() {

    }


}
