package com.studyandroid.hestersmile.mymusic.myactivity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.studyandroid.hestersmile.mymusic.action.ActionDefine;
import com.studyandroid.hestersmile.mymusic.adapter.MyFragmentPagerAdapter;
import com.studyandroid.hestersmile.mymusic.adapter.PopMusicListAdapter;
import com.studyandroid.hestersmile.mymusic.fragment.PlayMusicImgFragment;
import com.studyandroid.hestersmile.mymusic.fragment.PlayMusicLrcFragment;
import com.studyandroid.hestersmile.mymusic.service.MusicService;
import com.studyandroid.hestersmile.mymusic.R;
import com.studyandroid.hestersmile.mymusic.javabean.music;
import com.studyandroid.hestersmile.mymusic.util.Basetool;
import com.studyandroid.hestersmile.mymusic.util.Musicutil;
import com.studyandroid.hestersmile.mymusic.util.Singleton;

import java.util.ArrayList;
import java.util.List;

import static android.widget.SeekBar.*;

public class PlayMusicActivity extends AppCompatActivity implements PlayMusicImgFragment.OncallbackListener, AdapterView.OnItemClickListener {

    private Button playControlbtn;
    private int playflag = 0;//0是停止播放，1是播放；

    private String title; // 歌曲标题
    private String artist; // 歌曲艺术家
    private String url; // 歌曲路径
    private int listPosition; // 播放歌曲在mp3Infos的位置
    private int currentTime; // 当前歌曲播放时间
    private int duration; // 歌曲长度
    private int flag; // 播放标识
    private int repeatState;
    private boolean isShuffle;
    private Button playnextbtn;
    private Button playprebtn;
    private Button playsortbtn;
    private Button playlistbtn;
    private ImageButton navsharebtn;
    private ImageButton backbtn;
    private TextView mytitle;
    private TextView myauthor;
    private TextView mycurrenttime;
    private TextView mytotaltime;
    private SeekBar playseek;
    private long picbitmp;
    private long picid;
    private long totaltime;
    private boolean isfisrtplay = true;
    private PlayerReceiver playerReceiver;
    private List<music> mymusiclist = new ArrayList<music>();
    private Bitmap bitmap;
    private PendingIntent contentIntent;
    private notifyReceiver notification;
    private boolean pause = false;
    private Notification notify;
    private NotificationManager manager;
    private ViewPager mypager;
    private ArrayList<Fragment> fragmentsList;
    private PlayMusicImgFragment imgFragment;
    private PlayMusicLrcFragment lrcFragment;
    private FragmentManager fragmentManager;
    private PopupWindow popupWindow;
    private TextView poplistcoun;
    private TextView edittext;
    private ListView poplistview;
    private int playstyle = 0;
    private int playlistsort = 0;//0:默認是循環播放 1：隨機播放 2:單曲播放
    private String time;
    private int backactity = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_music);
        getSupportActionBar().hide();

        fragmentManager = getSupportFragmentManager();
        mymusiclist = Musicutil.getMp3Infos(this);
        getDataFromBundle();
        initView();
        registerReceiver();
        initViewPager();
        getCurrentprogress();
        Log.v("music", "startQQQ11");
    }

    private void getCurrentprogress() {
        if (playstyle <= 0) {
            playflag = 1;
            playControlbtn.setBackgroundResource(R.drawable.ic_stop);
            Intent intent = new Intent(PlayMusicActivity.this, MusicService.class);
            intent.setPackage("com.studyandroid.hestersmile.mymusic");
            intent.setAction(ActionDefine.MUSIC_SERVICE);
            intent.putExtra("music_status", "getprogress");
            intent.putExtra("music_url", url);
            startService(intent);
            if (playstyle == 0) {
                imgFragment.stopRoateImageview();
                playControlbtn.setBackgroundResource(R.drawable.ic_play);
                playflag = 0;
                isfisrtplay = false;
            }
        } else if (playstyle == 1) {

        }

    }

    private void initViewPager() {
        mypager = (ViewPager) this.findViewById(R.id.playViewpager);
        fragmentsList = new ArrayList<Fragment>();
        imgFragment = new PlayMusicImgFragment();
        lrcFragment = new PlayMusicLrcFragment();

        fragmentsList.add(imgFragment);
        fragmentsList.add(lrcFragment);
        Bundle data1 = new Bundle();

        data1.putLong("picid", mymusiclist.get(listPosition).getId());
        data1.putLong("picbmp", mymusiclist.get(listPosition).getAlbumId());
        imgFragment.setArguments(data1);
        Bundle data = new Bundle();

        data.putString("mytitle", mymusiclist.get(listPosition).getTitle());
        data.putString("myauthor", mymusiclist.get(listPosition).getArtist());
        lrcFragment.setArguments(data);

        mypager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), fragmentsList));
        mypager.setCurrentItem(0);
        mypager.setOnPageChangeListener(new MyOnPageChangeListener());
    }

    private void registerReceiver() {
        //定义和注册广播接收器
        playerReceiver = new PlayerReceiver();
        IntentFilter filter = new IntentFilter();
//        filter.addAction(UPDATE_ACTION);
        filter.addAction(ActionDefine.MUSIC_CURRENT);
        filter.addAction(ActionDefine.MUSIC_DURATION);
        filter.addAction(ActionDefine.UPDATE_PLAYMUSIC);
        filter.addAction(ActionDefine.GETPROGRESS);
        registerReceiver(playerReceiver, filter);
        IntentFilter filter1 = new IntentFilter();
        notification = new notifyReceiver();
        filter1.addAction(ActionDefine.NOTIFICATION_PLAY);
        registerReceiver(notification, filter1);
    }

    private void initView() {
        ViewOnclickListener myonclickListener = new ViewOnclickListener();
        playControlbtn = (Button) this.findViewById(R.id.music_playbtn);
        playnextbtn = (Button) this.findViewById(R.id.music_nextbtn);
        playprebtn = (Button) this.findViewById(R.id.music_prebtn);
        playsortbtn = (Button) this.findViewById(R.id.playsort);
        playlistbtn = (Button) this.findViewById(R.id.mysic_playinglistbtn);
        navsharebtn = (ImageButton) this.findViewById(R.id.nav_share);
        backbtn = (ImageButton) this.findViewById(R.id.mybacktitle);
        playControlbtn.setOnClickListener(myonclickListener);
        playprebtn.setOnClickListener(myonclickListener);
        playnextbtn.setOnClickListener(myonclickListener);
        playlistbtn.setOnClickListener(myonclickListener);
        backbtn.setOnClickListener(myonclickListener);
        playsortbtn.setOnClickListener(myonclickListener);
        mytitle = (TextView) this.findViewById(R.id.playing_title);
        myauthor = (TextView) this.findViewById(R.id.playing_author);
        mycurrenttime = (TextView) this.findViewById(R.id.playing_time);
        mytotaltime = (TextView) this.findViewById(R.id.playing_totaltime);
//        mypic = (ImageView) this.findViewById(R.id.playing_pic);
        playseek = (SeekBar) this.findViewById(R.id.play_seekbar);
        playseek.setEnabled(true);
//        playseek.setMax(totaltime);
        playseek.setProgress(0);
        playseek.setOnSeekBarChangeListener(new SeekBarChangeListener());
        mytitle.setText(title);

        myauthor.setText(artist);
        mytotaltime.setText(Musicutil.formatTime(totaltime));
        mycurrenttime.setText("00:00");
        bitmap = Musicutil.getArtwork(this, picid, picbitmp, true, false);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        listPosition = position;
        updateMyData(listPosition);
        updateMyWidget(listPosition);
        play();
    }

    private class ViewOnclickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Log.i("hahamusic", "playflag1" + playflag);

            switch (v.getId()) {
                case R.id.music_playbtn:
                    if (isfisrtplay) {

                        isfisrtplay = false;
                        if (playflag == 0) {
                            play();
                            playControlbtn.setBackgroundResource(R.drawable.ic_stop);
                            playflag = 1;
                        } else {
                            stop();
                            playControlbtn.setBackgroundResource(R.drawable.ic_play);
                            playflag = 0;
                        }

                    } else {
                        Log.v("music", "" + "secondpaly");
                        if (playflag == 0) {
                            resume();
                            playControlbtn.setBackgroundResource(R.drawable.ic_stop);
                            playflag = 1;
                        } else {
                            stop();
                            playControlbtn.setBackgroundResource(R.drawable.ic_play);
                            playflag = 0;
                        }
                    }
                    break;
                case R.id.music_nextbtn:
                    playflag = 1;
                    isfisrtplay = true;
                    if (playlistsort == 1) {
                        listPosition = (int) (Math.random() * (mymusiclist.size() - 1));
                        playControlbtn.setBackgroundResource(R.drawable.ic_stop);
                        playnext(listPosition);
                    } else {
                        listPosition = listPosition + 1;
                        Log.v("hahamusic", "nextlistPosition" + listPosition);
                        Log.v("hahamusic", "mymusiclist" + mymusiclist.size());
                        if (mymusiclist.size() > listPosition) {
                            playControlbtn.setBackgroundResource(R.drawable.ic_stop);
                            playnext(listPosition);
                        } else {
                            Toast.makeText(PlayMusicActivity.this, "最后一首了，无法下一首", Toast.LENGTH_SHORT).show();
                            listPosition -= 1;
                        }
                    }
                    break;
                case R.id.mybacktitle:
                    Intent backintent = new Intent();
                    Log.v("hahamusic", "++++playflag" + playflag);
                    Log.v("hahamusic", "++++listposition" + listPosition);
                    Singleton.getInstance().setListposition(listPosition);
                    Singleton.getInstance().setPlayStatus(playflag);
                    backintent.putExtra("playflag", playflag);
                    backintent.putExtra("listposition", listPosition);
                    setResult(RESULT_OK, backintent);
                    finish();
                    break;
                case R.id.playsort:
                    if (playlistsort == 0) {
                        playlistsort = 1;//1:隨機
                        playsortbtn.setBackgroundResource(R.drawable.ic_play_style_random);
                        Toast.makeText(PlayMusicActivity.this, "随机播放", Toast.LENGTH_SHORT).show();
                    } else if (playlistsort == 1) {
                        playlistsort = 2;
                        playsortbtn.setBackgroundResource(R.drawable.ic_play_style_onereylce);
                        Toast.makeText(PlayMusicActivity.this, "单曲播放", Toast.LENGTH_SHORT).show();
                    } else if (playlistsort == 2) {
                        playlistsort = 0;
                        playsortbtn.setBackgroundResource(R.drawable.ic_play_style_reycle);
                        Toast.makeText(PlayMusicActivity.this, "顺序播放", Toast.LENGTH_SHORT).show();
                    }
                    playsort(playlistsort);
                    break;
                case R.id.nav_share:
                    break;
                case R.id.music_prebtn:
                    playflag = 1;
                    isfisrtplay = true;
                    if (playlistsort == 1) {
                        listPosition = (int) (Math.random() * (mymusiclist.size() - 1));
                        playControlbtn.setBackgroundResource(R.drawable.ic_stop);
                        playpre(listPosition);
                    } else {

                        listPosition = listPosition - 1;
                        Log.v("hahamusic", "prelistPosition" + listPosition);
                        Log.v("hahamusic", "mymusiclist" + mymusiclist.size());
                        if (listPosition < 0) {
                            listPosition += 1;

                            Toast.makeText(PlayMusicActivity.this, "第一首了，无法上一首", Toast.LENGTH_SHORT).show();
                        } else {
                            playControlbtn.setBackgroundResource(R.drawable.ic_stop);
                            playpre(listPosition);
                        }
                    }
                    break;
                case R.id.mysic_playinglistbtn:
                    showListPop(v);
                    break;


                default:
                    break;
            }
        }
    }

    private void playsort(int sort) {
        Intent intent = new Intent(PlayMusicActivity.this, MusicService.class);
        intent.setPackage("com.studyandroid.hestersmile.mymusic");
        intent.setAction(ActionDefine.MUSIC_SORT);
        //这里你需要设置你应用的包名
        intent.putExtra("music_status", "sort");
        intent.putExtra("music_url", url);
        intent.putExtra("music_sort", sort);
        startService(intent);
    }

    private void showListPop(View parent) {
        // TODO Auto-generated method stub
        // 获取自定义布局文件activity_popupwindow_left.xml的视图
        //获取屏幕尺寸大小，是程序能在不同大小的手机上有更好的兼容性
        Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        int height = display.getHeight();
        int width = display.getWidth();
        if (popupWindow == null) {
            Log.i("hahamusic", "开始");
            View popupWindow_view = getLayoutInflater().inflate(R.layout.pop_music_list, null,
                    false);
            popupWindow = new PopupWindow(popupWindow_view, ViewGroup.LayoutParams.FILL_PARENT,
                    height / 2);
            poplistcoun = (TextView) popupWindow_view.findViewById(R.id.list_count);
            edittext = (TextView) popupWindow_view.findViewById(R.id.list_edit);
            poplistview = (ListView) popupWindow_view.findViewById(R.id.list_pop);
            poplistcoun.setText(mymusiclist.size() + "");
            PopMusicListAdapter popadapter = new PopMusicListAdapter(poplistview, mymusiclist, this);
            poplistview.setAdapter(popadapter);
            poplistview.setOnItemClickListener(this);
        }

        ColorDrawable cd = new ColorDrawable(0x000000);
        popupWindow.setBackgroundDrawable(cd);

        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.2f;
        getWindow().setAttributes(lp);

        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation((View) parent.getParent(), Gravity.BOTTOM, 0, 0);
        popupWindow.update();
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
            }
        });

    }

    private void getDataFromBundle() {
        Intent intent = getIntent();
        music myplaying = (music) intent.getSerializableExtra("musicObject");
        title = myplaying.getTitle();
        artist = myplaying.getArtist();
        url = myplaying.getUrl();
        picbitmp = myplaying.getAlbumId();
        totaltime = myplaying.getDuration();
        picid = myplaying.getId();
        Log.v("music", "pathurl" + url);
        listPosition = intent.getIntExtra("listposition", 0);
        playstyle = intent.getIntExtra("playstyle", 0);//playstyle -1 代表 從圖片進入的但是已經播放了 0代表 從圖片進來的mainactivy沒有播放歌曲 1、代表從item中選擇進來的

    }

    private void updateMyData(int position) {
        listPosition = position;
        music myplaying = mymusiclist.get(position);
        title = myplaying.getTitle();
        artist = myplaying.getArtist();
        url = myplaying.getUrl();
        picbitmp = myplaying.getAlbumId();
        totaltime = myplaying.getDuration();
        picid = myplaying.getId();
        bitmap = Musicutil.getArtwork(this, picid, picbitmp, true, false);
    }

    private void updateMyWidget(int position) {
        mytitle.setText(title);
        myauthor.setText(artist);
        mycurrenttime.setText("00:00");
        playseek.setProgress(0);
        putFragmentAlubmValue(position);
        putFragmentLrcValue(position);
        isfisrtplay = true;
        playflag = 1;
        playControlbtn.setBackgroundResource(R.drawable.ic_stop);

    }

    /**
     * 播放音乐
     */
    private void play() {
//        Log.v("hahamusic", "start");
        imgFragment.startRotatImage();
        Intent intent = new Intent(PlayMusicActivity.this, MusicService.class);
        intent.setPackage("com.studyandroid.hestersmile.mymusic");
        intent.setAction(ActionDefine.MUSIC_SERVICE);
        //这里你需要设置你应用的包名

        intent.putExtra("music_status", "play");
        intent.putExtra("music_url", url);
        startService(intent);

    }

    private void stop() {
        imgFragment.stopRoateImageview();
        Intent intent = new Intent(PlayMusicActivity.this, MusicService.class);
        intent.setAction(ActionDefine.MUSIC_SERVICE);
        intent.setPackage("com.studyandroid.hestersmile.mymusic");
        intent.putExtra("music_status", "stop");
        intent.putExtra("music_url", url);
        intent.putExtra("listPosition", listPosition);
        startService(intent);

    }

    private void playpre(int Position) {

        imgFragment.startRotatImage();
        Intent intent = new Intent(PlayMusicActivity.this, MusicService.class);
        intent.setAction(ActionDefine.MUSIC_SERVICE);
        intent.setPackage("com.studyandroid.hestersmile.mymusic");
        intent.putExtra("music_status", "playpre");
        intent.putExtra("listPosition", Position);
        intent.putExtra("music_url", mymusiclist.get(Position).getUrl());
        startService(intent);
    }

    private void playnext(int Position) {

        imgFragment.startRotatImage();
        Intent intent = new Intent(PlayMusicActivity.this, MusicService.class);
        intent.setAction(ActionDefine.MUSIC_SERVICE);
        intent.setPackage("com.studyandroid.hestersmile.mymusic");
        intent.putExtra("music_status", "playnext");
        intent.putExtra("listPosition", Position);
        intent.putExtra("music_url", mymusiclist.get(Position).getUrl());
        startService(intent);
    }

    private void resume() {
        imgFragment.startRotatImage();
        Intent intent = new Intent(PlayMusicActivity.this, MusicService.class);
        intent.setAction(ActionDefine.MUSIC_SERVICE);
        intent.setPackage("com.studyandroid.hestersmile.mymusic");
        intent.putExtra("music_status", "resume");
        startService(intent);
    }

    public class PlayerReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
//
            String playaction = intent.getAction();
            switch (playaction) {
                case ActionDefine.GETPROGRESS:
                    int currentprogress = intent.getIntExtra("progress", -1);
                    int totaltime1 = intent.getIntExtra("totalprogress", -1);
                    playseek.setMax(totaltime1);
                    playseek.setProgress(currentprogress);
                    break;
                case ActionDefine.MUSIC_CURRENT:
                    int currenttime = intent.getIntExtra("currentTime", -1);
                    Log.v("music", "----------CURRENTTIME" + currentTime);
                    mycurrenttime.setText(Musicutil.formatTime(currenttime));
                    playseek.setProgress(currenttime);
                    break;
                case ActionDefine.MUSIC_DURATION:
                    Log.v("hahamusic", "totaltime" + totaltime);
                    int totaltime = intent.getIntExtra("duration", -1);
                    mytotaltime.setText(Musicutil.formatTime(totaltime));
                    playseek.setMax(totaltime);
                    break;
                case ActionDefine.MUSIC_PROGRESS:
                    int progress = intent.getIntExtra("progress", -1);
                    Log.v("hahamusic", "progress2" + progress);
                    playseek.setProgress(progress);
                    break;
                case ActionDefine.UPDATE_PLAYMUSIC:
                    int current = intent.getIntExtra("current", -1);
                    Log.v("hahamusic", "current" + current);
                    listPosition = current;
                    updateMyData(listPosition);
                    updateMyWidget(listPosition);
                    mytitle.setText(title);
                    myauthor.setText(artist);
                    mycurrenttime.setText("00:00");
                    playseek.setProgress(0);
                    putFragmentAlubmValue(listPosition);
                    putFragmentLrcValue(listPosition);
                    if (pause == true) {
                        shownotify();
                    }
                    break;

                default:
                    break;
            }
        }


    }

    private void updateData(int position) {
        title = mymusiclist.get(position).getTitle();
        artist = mymusiclist.get(position).getTitle();
        url = mymusiclist.get(position).getUrl();
        picid = mymusiclist.get(position).getAlbumId();


        long timelong = mymusiclist.get(position).getDuration();
        time = Musicutil.formatTime(timelong);
        picid = mymusiclist.get(position).getId();
        picbitmp = mymusiclist.get(position).getAlbumId();
        artist = mymusiclist.get(position).getArtist();
        url = mymusiclist.get(position).getUrl();


    }

    private void shownotify() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        RemoteViews mRemoteViews = new RemoteViews(getPackageName(), R.layout.view_custom_button);

        mRemoteViews.setImageViewResource(R.id.notification_pic, R.drawable.ic_nomusic);
        //API3.0 以上的时候显示按钮，否则消失
        mRemoteViews.setTextViewText(R.id.notification_title, title);
        mRemoteViews.setTextViewText(R.id.notification_author, artist);
        mRemoteViews.setImageViewBitmap(R.id.notification_pic, bitmap);
        //如果版本号低于（3。0），那么不显示按钮
        if (Basetool.getAndroidOSVersion() <= 9) {
            mRemoteViews.setViewVisibility(R.id.notification_next, View.GONE);
            mRemoteViews.setViewVisibility(R.id.notification_play, View.GONE);
            mRemoteViews.setViewVisibility(R.id.notification_pre, View.GONE);

        } else {
            mRemoteViews.setViewVisibility(R.id.notification_next, View.VISIBLE);
            mRemoteViews.setViewVisibility(R.id.notification_play, View.VISIBLE);
            mRemoteViews.setViewVisibility(R.id.notification_pre, View.VISIBLE);
        }
        if (bitmap == null) {
            mRemoteViews.setImageViewResource(R.id.notification_pic, R.drawable.ic_nomusic);
        } else {
            mRemoteViews.setImageViewBitmap(R.id.notification_pic, bitmap);
        }
        if (playflag == 1) {
            mRemoteViews.setImageViewResource(R.id.notification_play, R.drawable.ic_notification_stop);
        } else if (playflag == 0) {
            mRemoteViews.setImageViewResource(R.id.notification_play, R.drawable.ic_notification_play);
        }
//        点击的事件处理
        Intent intentd = new Intent(this, PlayMusicActivity.class);

                /*add the followed two lines to resume the app same with previous statues*/
        intentd.setAction(Intent.ACTION_MAIN);
        intentd.addCategory(Intent.CATEGORY_LAUNCHER);
                /**/
        PendingIntent intent = PendingIntent.getActivity(this, 0, intentd, PendingIntent.FLAG_UPDATE_CURRENT);


        Intent playIntent = new Intent();
        playIntent.putExtra("buttonid", 1);
        playIntent.setAction(ActionDefine.NOTIFICATION_PLAY);
        Intent stopIntent = new Intent();
        stopIntent.putExtra("buttonid", 2);
        stopIntent.setAction(ActionDefine.NOTIFICATION_PLAY);

        contentIntent = PendingIntent.getBroadcast(this, 1, playIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.notification_play, contentIntent);

        Intent preintent = new Intent();
        preintent.setAction(ActionDefine.NOTIFICATION_PLAY);
        preintent.putExtra("buttonid", 3);
        Intent nextintent = new Intent();
        nextintent.setAction(ActionDefine.NOTIFICATION_PLAY);
        nextintent.putExtra("buttonid", 4);

        PendingIntent intent_next = PendingIntent.getBroadcast(this, 4, nextintent, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.notification_pre, intent_next);
        PendingIntent intent_pre = PendingIntent.getBroadcast(this, 3, preintent, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.notification_next, intent_pre);
        mBuilder.setContent(mRemoteViews).setContentIntent(contentIntent)
                .setContentIntent(intent)
                .setWhen(System.currentTimeMillis())// 通知产生的时间，会在通知信息里显示
                .setTicker("正在播放")
                .setPriority(Notification.PRIORITY_DEFAULT)// 设置该通知优先级
                .setOngoing(true)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_no);
        notify = mBuilder.build();
        notify.flags = Notification.FLAG_ONGOING_EVENT;
        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(100, notify);
    }

    @Override
    protected void onPause() {
        super.onPause();
        pause = true;
        Log.i("hahamusic", "pause");
        shownotify();

    }

    @Override
    protected void onResume() {
        super.onResume();
        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancelAll();
        pause = false;
    }

    public class notifyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
//            Log.i("hahamusic", "intent11111" + intent.getAction());
            String playaction = intent.getAction();
            if (playaction.equals(ActionDefine.NOTIFICATION_PLAY)) {
                int buttonId = intent.getIntExtra("buttonid", 0);

                switch (buttonId) {

                    case 1:
                        if (isfisrtplay) {
                            isfisrtplay = false;
                            if (playflag == 0) {
                                playflag = 1;
                                play();
                            } else {
                                playflag = 0;
                                stop();
                            }
                        } else {
                            if (playflag == 0) {
                                playflag = 1;
                                resume();
                            } else {
                                playflag = 0;
                                stop();
                            }
                        }
                        shownotify();
                        break;
                    case 3:

                        isfisrtplay = true;
                        listPosition = listPosition + 1;
                        Log.v("hahamusic", "nextlistPosition" + listPosition);
                        Log.v("hahamusic", "mymusiclist" + mymusiclist.size());
                        if (mymusiclist.size() > listPosition) {
                            playnext(listPosition);
                        } else {
                            Toast.makeText(PlayMusicActivity.this, "最后一首了，无法下一首", Toast.LENGTH_SHORT).show();
                            listPosition -= 1;
                        }
                        break;
                    case 4:
                        isfisrtplay = true;
                        listPosition = listPosition - 1;
                        Log.v("hahamusic", "prelistPosition" + listPosition);
                        Log.v("hahamusic", "mymusiclist" + mymusiclist.size());
                        if (listPosition < 0) {
                            listPosition += 1;
                            Toast.makeText(PlayMusicActivity.this, "第一首了，无法上一首", Toast.LENGTH_SHORT).show();
                        } else {
                            playpre(listPosition);
                        }

                        break;
                    default:
                        break;
                }
            }
        }


    }

    private class SeekBarChangeListener implements OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                Intent myprogressIntent = new Intent(PlayMusicActivity.this, MusicService.class);

                myprogressIntent.setPackage("com.studyandroid.hestersmile.mymusic");
                myprogressIntent.setAction(ActionDefine.MUSIC_SERVICE);
                myprogressIntent.putExtra("music_status", "progresschange");
                myprogressIntent.putExtra("music_url", url);
                myprogressIntent.putExtra("music_listposition", listPosition);
                myprogressIntent.putExtra("music_changeprogess", progress);
                startService(myprogressIntent);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }

    ;


    private class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
            if (arg0 == 0) {
                if (null == imgFragment) {//可以避免切换的时候重复创建
                    imgFragment = new PlayMusicImgFragment();
                    putFragmentAlubmValue(listPosition);
                }

            } else {
                if (null == lrcFragment) {//可以避免切换的时候重复创建
                    lrcFragment = new PlayMusicLrcFragment();
                    putFragmentLrcValue(listPosition);
                }
            }
        }


    }

    private void putFragmentLrcValue(int position) {

        if (lrcFragment != null) {
            lrcFragment.changeMyTitle(mymusiclist.get(position).getTitle(), mymusiclist.get(position).getArtist());
            lrcFragment.initfile();
        }
    }

    private void putFragmentAlubmValue(int position) {
        if (imgFragment != null) {
            imgFragment.changeMyPic(mymusiclist.get(position).getId(), mymusiclist.get(position).getAlbumId());
        }

    }

    @Override
    public void startPlay() {
        /**在SecondFragment中执行此方法*/
        if (playstyle == 1) {
            play();
            Log.v("hahamusic", "999999990");
            isfisrtplay = false;
            playflag = 1;
            playControlbtn.setBackgroundResource(R.drawable.ic_stop);
        } else if (playstyle == -1) {
            imgFragment.startRotatImage();
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            Intent backintent = new Intent();
            Log.v("hahamusic", "++++playflag" + playflag);
            Log.v("hahamusic", "++++listposition" + listPosition);
            Singleton.getInstance().setListposition(listPosition);
            Singleton.getInstance().setPlayStatus(playflag);
            backintent.putExtra("playflag", playflag);
            backintent.putExtra("listposition", listPosition);
            setResult(RESULT_OK, backintent);
            finish();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_HOME) {

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}


