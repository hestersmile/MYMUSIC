package com.studyandroid.hestersmile.mymusic.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.studyandroid.hestersmile.mymusic.R;
import com.studyandroid.hestersmile.mymusic.action.ActionDefine;
import com.studyandroid.hestersmile.mymusic.myactivity.PlayMusicActivity;
import com.studyandroid.hestersmile.mymusic.util.Musicutil;

/**
 * Created by hestersmile on 2016/5/25.
 */
public class PlayMusicImgFragment extends Fragment {
    private View activityView;
    private Bitmap bitmap;
    private ImageView mypic;
    private long picid;
    private long picbitmp;
    private BroadcastReceiver myReceiver;
    private RotateAnimation an;
    OncallbackListener listener;
    private RelativeLayout mlayout;

    public interface OncallbackListener {
        void startPlay();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle mybundle = getArguments();
        picid = mybundle.getLong("picid");
        picbitmp = mybundle.getLong("picbmp");
//        initRegister();
    }

    private void initRegister() {
        Log.i("hahamusic", "111441");
//        myReceiver = new BroadReceiver();
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(getActivity());
        IntentFilter myFilter = new IntentFilter();
        myFilter.addAction(ActionDefine.FRAGMENT_ALUBM);
        broadcastManager.registerReceiver(myReceiver, myFilter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activityView = inflater.inflate(R.layout.fragment_play_imglayout, null);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("mybackground",
                Context.MODE_PRIVATE);
        int backres = sharedPreferences.getInt("resource", R.drawable.ic_bac0);
        mlayout = (RelativeLayout) activityView.findViewById(R.id.imglayout);
        mlayout.setBackgroundResource(backres);
        iniview();
        return activityView;
    }

    private void iniview() {

        mypic = (ImageView) activityView.findViewById(R.id.playing_pic);

        Log.i("hahamusic", "pictimp" + picbitmp);
        bitmap = Musicutil.getArtwork(getActivity(), picid, picbitmp, true, false);
        Log.v("music", "" + bitmap);
        if (bitmap == null) {
            mypic.setImageResource(R.drawable.ic_nomusic);
        } else {
            mypic.setImageBitmap(bitmap);
        }
        startPlay();
    }

    public void startPlay() {
        listener.startPlay();
    }

    @Override
    public void onAttach(Activity activity) {
        //将Activity向上转型成为这个内部接口
        if (activity != null) {
            listener = (OncallbackListener) activity;
        } else {
            try {
                //如果Activity未实现这个内部接口则抛出异常
                throw new Exception("没有可用的activity");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.onAttach(activity);
    }

    public void startRotatImage() {
        Log.i("hahamusic", "111111");
        an = new RotateAnimation(0, 359, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        an.setInterpolator(new LinearInterpolator());//不停顿
        an.setRepeatCount(-1);//重复次数
        an.setFillAfter(true);//停在最后
        an.setDuration(4000);
        an.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }
        });
        //动画开始
        mypic.startAnimation(an);
    }

    public void stopRoateImageview() {
        Log.i("hahamusic", "22222");
        if (an != null) {
            mypic.clearAnimation();
        }
    }

    public void changeMyPic(long pid, long picbtmp) {
        bitmap = Musicutil.getArtwork(getActivity(), pid, picbtmp, true, false);
        Log.i("hahamusic", "11111111" + bitmap);
        if (bitmap == null) {
            mypic.setImageResource(R.drawable.ic_nomusic);
        } else {
            mypic.setImageBitmap(bitmap);

        }

    }
}
