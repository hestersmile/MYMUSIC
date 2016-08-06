package com.studyandroid.hestersmile.mymusic.myactivity;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.studyandroid.hestersmile.mymusic.R;
import com.studyandroid.hestersmile.mymusic.action.ActionDefine;
import com.studyandroid.hestersmile.mymusic.adapter.Madusiclistapter;
import com.studyandroid.hestersmile.mymusic.javabean.music;
import com.studyandroid.hestersmile.mymusic.service.MusicService;
import com.studyandroid.hestersmile.mymusic.util.Basetool;
import com.studyandroid.hestersmile.mymusic.util.Musicutil;
import com.studyandroid.hestersmile.mymusic.util.Singleton;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private List<music> mymusiclist = new ArrayList<music>();
    private Madusiclistapter myadapter;
    private ListView mylistview;
    private NotificationManager manager;
    private TextView mytime;
    private TextView mytitle;
    private ImageView mypic;
    private Button mynext;
    private Button mypre;
    private Button myplay;
    private String title;
    private String time;
    private long pid;
    private long alubmid;
    private Bitmap bitmap;
    private String author;
    private String url;
    private PendingIntent contentIntent;
    private int listPosition;
    private Notification notify;
    private int playflag = 0; //0是停止，1是播放
    private boolean isfisrtplay = true;
    private PlayerReceiver playerReceiver;
    private notifyReceiver notification;
    private boolean pause;
    private RelativeLayout mlayout;
    private Button myslidebtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND, WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        setContentView(R.layout.activity_main);
        SharedPreferences sharedPreferences = getSharedPreferences("mybackground",
                Context.MODE_PRIVATE);

        int backres = sharedPreferences.getInt("resource", R.drawable.ic_bac0);
        Bitmap mybit = BitmapFactory.decodeResource(getResources(), backres);
        mybit = blur(mybit, 18f);
        mlayout = (RelativeLayout) findViewById(R.id.mainlayout);
        Drawable bd = new BitmapDrawable(getResources(), mybit);
        mlayout.setBackground(bd);
        mylistview = (ListView) findViewById(R.id.mytotalmp3);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        initWidegt();
        findmymusiclist();
        registerReceiver();
    }

    private void registerReceiver() {
        //定义和注册广播接收器
        playerReceiver = new PlayerReceiver();
        IntentFilter filter = new IntentFilter();
//        filter.addAction(UPDATE_ACTION);
        filter.addAction(ActionDefine.MUSIC_CURRENT);
        filter.addAction(ActionDefine.MUSIC_DURATION);
        filter.addAction(ActionDefine.UPDATE_PLAYMUSIC);
        registerReceiver(playerReceiver, filter);
        IntentFilter filter1 = new IntentFilter();
        notification = new notifyReceiver();
        filter1.addAction(ActionDefine.NOTIFICATION_PLAY);
        registerReceiver(notification, filter1);
    }

    private void initWidegt() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("My Music");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(0x600d3370);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        myOnClick myOnClick = new myOnClick();
        mytitle = (TextView) findViewById(R.id.musicnow_title);
        mytime = (TextView) findViewById(R.id.musicnow_time);
        mypic = (ImageView) findViewById(R.id.musicnow_pic);
        mynext = (Button) findViewById(R.id.music_playnext);
        mypre = (Button) findViewById(R.id.music_playpre);
        myplay = (Button) findViewById(R.id.music_play);

        myplay.setOnClickListener(myOnClick);
        mypre.setOnClickListener(myOnClick);
        mynext.setOnClickListener(myOnClick);
        mypic.setOnClickListener(myOnClick);
        mypic.setBackgroundResource(R.drawable.ic_nomusic);
        mytime.setText("00:00");
        mytitle.setText("暂无歌曲");
    }

    private class myOnClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.musicnow_pic:
                    Intent playmusicIntent = new Intent(MainActivity.this, PlayMusicActivity.class);
                    Bundle myBundle = new Bundle();
                    myBundle.putSerializable("musicObject", mymusiclist.get(listPosition));
                    playmusicIntent.putExtras(myBundle);
                    playmusicIntent.putExtra("listposition", listPosition);
                    if (isfisrtplay == true) {

                    } else {
                        if (playflag == 1) {
                            Log.v("hahamusic", "play:" + playflag);
                            playmusicIntent.putExtra("playstyle", -1);
                            startActivityForResult(playmusicIntent, 0);
                        } else {
                            Log.v("hahamusic", "stop:" + playflag);
                            playmusicIntent.putExtra("playstyle", 0);
                            startActivityForResult(playmusicIntent, 0);
                        }
                    }

                    break;
                case R.id.music_play:
                    if (isfisrtplay) {
                        isfisrtplay = false;
                        if (playflag == 0) {
                            play();
                            myplay.setBackgroundResource(R.drawable.ic_stop);
                            playflag = 1;
                        } else {
                            stop();
                            myplay.setBackgroundResource(R.drawable.ic_play);
                            playflag = 0;
                        }
                    } else {
                        Log.v("music", "" + "secondpaly");
                        if (playflag == 0) {
                            resume();
                            myplay.setBackgroundResource(R.drawable.ic_stop);
                            playflag = 1;
                        } else {
                            stop();
                            myplay.setBackgroundResource(R.drawable.ic_play);
                            playflag = 0;
                        }
                    }
                    break;
                case R.id.music_playnext:
                    playflag = 1;
                    isfisrtplay = true;
                    listPosition = listPosition + 1;
                    Log.v("hahamusic", "nextlistPosition" + listPosition);
                    Log.v("hahamusic", "mymusiclist" + mymusiclist.size());
                    if (mymusiclist.size() > listPosition) {
                        myplay.setBackgroundResource(R.drawable.ic_stop);
                        playnext(listPosition);
                    } else {
                        Toast.makeText(MainActivity.this, "最后一首了，无法下一首", Toast.LENGTH_SHORT).show();
                        listPosition -= 1;
                    }
                    break;
                case R.id.music_playpre:
                    playflag = 1;
                    isfisrtplay = true;
                    listPosition = listPosition - 1;
                    Log.v("hahamusic", "prelistPosition" + listPosition);
                    Log.v("hahamusic", "mymusiclist" + mymusiclist.size());
                    if (listPosition < 0) {
                        listPosition += 1;

                        Toast.makeText(MainActivity.this, "第一首了，无法上一首", Toast.LENGTH_SHORT).show();
                    } else {
                        myplay.setBackgroundResource(R.drawable.ic_stop);
                        playpre(listPosition);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void findmymusiclist() {
        mymusiclist = Musicutil.getMp3Infos(this);
//        myadapter = new Madusiclistapter(mylistview, mymusiclist, this);
//        mylistview.setAdapter(myadapter);
        NewsAsyncTask mytask = new NewsAsyncTask();
        mytask.execute();
        mylistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent playmusicIntent = new Intent(MainActivity.this, PlayMusicActivity.class);
                Bundle myBundle = new Bundle();
                myBundle.putSerializable("musicObject", mymusiclist.get(position));
                playmusicIntent.putExtras(myBundle);
                playmusicIntent.putExtra("listposition", position);
                playmusicIntent.putExtra("playstyle", 1);

                startActivityForResult(playmusicIntent, 0);
            }
        });
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //
    class NewsAsyncTask extends AsyncTask<String, Void, List<music>> {

        @Override
        protected List<music> doInBackground(String... params) {
            for (int i = 0; i < mymusiclist.size(); i++) {
                long pid = mymusiclist.get(i).getId();
                long aulbmid = mymusiclist.get(i).getAlbumId();
                String url = Musicutil.getArtwork1(MainActivity.this, pid, aulbmid, true, true);
                mymusiclist.get(i).setAublmUrl(url);
            }
            return mymusiclist;
        }

        @Override
        protected void onPostExecute(List<music> result) {
            super.onPostExecute(result);

            Madusiclistapter newsAdapter = new Madusiclistapter(mylistview, result, MainActivity.this);
            mylistview.setAdapter(newsAdapter);

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        listPosition = Singleton.getInstance().getListposition();
        playflag = Singleton.getInstance().getPlaystuats();
        updateData(listPosition);
        updateWidget();
        if (playflag == 1) {
            myplay.setBackgroundResource(R.drawable.ic_stop);
            isfisrtplay = false;
        } else {
            myplay.setBackgroundResource(R.drawable.ic_play);
            isfisrtplay = true;
        }
        pause = false;
        SharedPreferences sharedPreferences = getSharedPreferences("mybackground",
                Context.MODE_PRIVATE);
        int backres = sharedPreferences.getInt("resource", R.drawable.ic_bac0);
        Bitmap mybit = BitmapFactory.decodeResource(getResources(), backres);
        mybit = blur(mybit, 18f);
        mlayout = (RelativeLayout) findViewById(R.id.mainlayout);
        Drawable bd = new BitmapDrawable(getResources(), mybit);
        mlayout.setBackground(bd);
        Log.v("hahamusic", "onresume");
        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancelAll();
    }

    @Override
    protected void onPause() {
        super.onPause();
        pause = true;
        Log.i("hahamusic", "pause");
        shownotify();
    }

    private void shownotify() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        RemoteViews mRemoteViews = new RemoteViews(getPackageName(), R.layout.view_custom_button);

        mRemoteViews.setImageViewResource(R.id.notification_pic, R.drawable.ic_nomusic);
        //API3.0 以上的时候显示按钮，否则消失
        mRemoteViews.setTextViewText(R.id.notification_title, title);
        mRemoteViews.setTextViewText(R.id.notification_author, author);
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
        Intent intentd = new Intent(this, MainActivity.class);

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

        Log.i("hahamusic", "1111playflag" + playflag);

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

    public class PlayerReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
//
            String playaction = intent.getAction();
            switch (playaction) {
                case ActionDefine.MUSIC_DURATION:
                    int totaltime = intent.getIntExtra("duration", -1);
                    mytime.setText(Musicutil.formatTime(totaltime));
                    break;

                case ActionDefine.UPDATE_PLAYMUSIC:
                    int current = intent.getIntExtra("current", -1);
                    Log.v("hahamusic", "current" + current);

                    updateData(current);
                    updateWidget();
                    if (pause == true) {
                        shownotify();
                    }
                    break;
                default:
                    break;
            }
        }


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
                        playflag = 1;
                        listPosition = listPosition + 1;
                        Log.v("hahamusic", "nextlistPosition" + listPosition);
                        Log.v("hahamusic", "mymusiclist" + mymusiclist.size());
                        if (mymusiclist.size() > listPosition) {
                            playnext(listPosition);
                        } else {
                            Toast.makeText(MainActivity.this, "最后一首了，无法下一首", Toast.LENGTH_SHORT).show();
                            listPosition -= 1;
                        }
                        break;
                    case 4:
                        isfisrtplay = true;
                        playflag = 1;
                        listPosition = listPosition - 1;
                        Log.v("hahamusic", "prelistPosition" + listPosition);
                        Log.v("hahamusic", "mymusiclist" + mymusiclist.size());
                        if (listPosition < 0) {
                            listPosition += 1;
                            Toast.makeText(MainActivity.this, "第一首了，无法上一首", Toast.LENGTH_SHORT).show();
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v("hahamusic", "onactivityresult");
        switch (requestCode) { //resultCode为回传的标记，我在B中回传的是RESULT_OK
            case 0:
                if (resultCode == RESULT_OK) {
                    playflag = data.getIntExtra("playflag", -1);//str即为回传的值
                    listPosition = data.getIntExtra("listposition", -1);
                    Log.v("hahamusic", "playflag:" + playflag);
                    Log.v("hahamusic", "listposition:" + listPosition);
                    updateData(listPosition);
                    updateWidget();
                    if (playflag == 1) {
                        myplay.setBackgroundResource(R.drawable.ic_stop);
                        isfisrtplay = false;
                    } else {
                        myplay.setBackgroundResource(R.drawable.ic_play);
                        isfisrtplay = true;
                    }
                }
                break;
            case 1:
                if (resultCode == RESULT_OK) {

                }
                break;
            default:
                break;
        }
    }

    /**
     * 播放音乐
     */
    private void play() {
        Intent intent = new Intent(MainActivity.this, MusicService.class);
        intent.setPackage("com.studyandroid.hestersmile.mymusic");
        intent.setAction(ActionDefine.MUSIC_SERVICE);
        //这里你需要设置你应用的包名

        intent.putExtra("music_status", "play");
        intent.putExtra("music_url", url);
        startService(intent);

    }

    private void stop() {
        Intent intent = new Intent(MainActivity.this, MusicService.class);
        intent.setAction(ActionDefine.MUSIC_SERVICE);
        intent.setPackage("com.studyandroid.hestersmile.mymusic");
        intent.putExtra("music_status", "stop");
        intent.putExtra("music_url", url);
        intent.putExtra("listPosition", listPosition);
        startService(intent);

    }

    private void playpre(int Position) {
        Intent intent = new Intent(MainActivity.this, MusicService.class);
        intent.setAction(ActionDefine.MUSIC_SERVICE);
        intent.setPackage("com.studyandroid.hestersmile.mymusic");
        intent.putExtra("music_status", "playpre");
        intent.putExtra("listPosition", Position);
        intent.putExtra("music_url", mymusiclist.get(Position).getUrl());
        startService(intent);
    }

    private void playnext(int Position) {
        Intent intent = new Intent(MainActivity.this, MusicService.class);
        intent.setAction(ActionDefine.MUSIC_SERVICE);
        intent.setPackage("com.studyandroid.hestersmile.mymusic");
        intent.putExtra("music_status", "playnext");
        intent.putExtra("listPosition", Position);
        intent.putExtra("music_url", mymusiclist.get(Position).getUrl());
        startService(intent);
    }

    private void resume() {
        Intent intent = new Intent(MainActivity.this, MusicService.class);
        intent.setAction(ActionDefine.MUSIC_SERVICE);
        intent.setPackage("com.studyandroid.hestersmile.mymusic");
        intent.putExtra("music_status", "resume");
        startService(intent);
    }

    private void updateData(int Position) {
        listPosition = Position;
        title = mymusiclist.get(Position).getTitle();
        long timelong = mymusiclist.get(Position).getDuration();
        time = Musicutil.formatTime(timelong);
        pid = mymusiclist.get(Position).getId();
        alubmid = mymusiclist.get(Position).getAlbumId();
        author = mymusiclist.get(Position).getArtist();
        url = mymusiclist.get(Position).getUrl();
        bitmap = Musicutil.getArtwork(this, pid, alubmid, true, false);
    }

    private void updateWidget() {
        mytime.setText(time);
        mytitle.setText(title);
        mypic.setImageBitmap(bitmap);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

     if (id == R.id.nav_background) {
            Intent bacIntent = new Intent(MainActivity.this, ChangBackGroundActivity.class);
            startActivity(bacIntent);
        } else if (id == R.id.nav_audioedit) {
            Intent bacIntent = new Intent(MainActivity.this, SearchActivity.class);
            startActivity(bacIntent);
        } else if (id == R.id.nav_exit) {
            AlertDialog alertDialog = new AlertDialog.Builder(this).setTitle("提醒")
                    .setMessage("是否退出程序")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Intent.ACTION_MAIN);
                            intent.addCategory(Intent.CATEGORY_HOME);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            android.os.Process.killProcess(android.os.Process.myPid())  ;  //获取PID
                            System.exit(0);   //常规java、c#的标准退出法，返回值为0代表正常退出
                        }

                    }).setNegativeButton("取消",

                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    return;
                                }
                            }).create(); // 创建对话框
            alertDialog.show(); // 显示对话框
        } else if (id == R.id.nav_manger) {
            deleteFile(new File(String.valueOf(getFilesDir()+"/"+"lrccache")));
        } else if (id == R.id.nav_about) {
         Intent bacIntent = new Intent(MainActivity.this, AboutActivity.class);
         startActivity(bacIntent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private Bitmap blur(Bitmap sentBitmap, float radius) {
        Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
        final RenderScript rs = RenderScript.create(this);
        final Allocation input = Allocation.createFromBitmap(rs, sentBitmap, Allocation.MipmapControl.MIPMAP_NONE,
                Allocation.USAGE_SCRIPT);
        final Allocation output = Allocation.createTyped(rs, input.getType());
        final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        script.setRadius(radius /* e.g. 3.f */);
        script.setInput(input);
        script.forEach(output);
        output.copyTo(bitmap);
        return bitmap;
    }

    public void deleteFile(File file) {
        if (file.exists()) { // 判断文件是否存在
            if (file.isFile()) { // 判断是否是文件
                file.delete(); // delete()方法 你应该知道 是删除的意思;
            } else if (file.isDirectory()) { // 否则如果它是一个目录
                File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
                for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
                    this.deleteFile(files[i]); // 把每个文件 用这个方法进行迭代
                }
            }
            file.delete();
            Toast.makeText(MainActivity.this, "清除完毕", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, "并没有下载歌词", Toast.LENGTH_SHORT).show();
        }
    }

}
