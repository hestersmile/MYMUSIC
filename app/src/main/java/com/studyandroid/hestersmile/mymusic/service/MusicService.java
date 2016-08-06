package com.studyandroid.hestersmile.mymusic.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Switch;

import com.studyandroid.hestersmile.mymusic.action.ActionDefine;
import com.studyandroid.hestersmile.mymusic.fragment.PlayMusicLrcFragment;
import com.studyandroid.hestersmile.mymusic.javabean.music;
import com.studyandroid.hestersmile.mymusic.myactivity.PlayMusicActivity;
import com.studyandroid.hestersmile.mymusic.util.Musicutil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.LogRecord;

/**
 * Created by hestersmile on 2016/5/9.
 */
public class MusicService extends Service {
    private MediaPlayer mediaPlayer;
    private String path;
    private String msg;
    private boolean ispause;
    private int playstatus = 1;
    private int currentTime;
    private int currentposition;
    private List<music> musiclist = new ArrayList<music>();
    private int playsort = 0;

    private Handler mhandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0x111:
                    if (mediaPlayer != null) {
                        currentTime = mediaPlayer.getCurrentPosition(); // 获取当前音乐播放的位置
                        Intent intent = new Intent();
                        intent.setAction(ActionDefine.MUSIC_CURRENT);
                        intent.putExtra("currentTime", currentTime);
                        sendBroadcast(intent);
//                        Log.v("hahamusic", "CURRENTTIME" + currentTime);
                        // 给PlayerActivity发送广播
                        mhandler.sendEmptyMessageDelayed(0x111, 1000);
                    }
                    break;

                default:
                    break;
            }
        }

    };
    private TimerTask mTask;
    private Timer mTimer;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        musiclist = Musicutil.getMp3Infos(MusicService.this);
        mediaPlayer.setOnCompletionListener(new mycompletemusiclistener());
        Log.v("music", "startQQQ");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Log.v("music", "start");
        path = intent.getStringExtra("music_url");
        Log.v("hahamusic", "path" + path);
        //歌曲路径
//        current = intent.getIntExtra("listPosition", -1);	//当前播放歌曲的在mp3Infos的位置
        msg = intent.getStringExtra("music_status");            //播放信息
        if (msg.equals("play")) {    //直接播放音乐
            play(0);
        } else if (msg.equals("stop")) {    //暂停
            pause();
        } else if (msg.equals("resume")) {
            resume();
        } else if (msg.equals("progresschange")) {
            currentposition = intent.getIntExtra("music_listposition", -1);
            int newprogress = intent.getIntExtra("music_changeprogess", 0);
            progesschange(newprogress);
        } else if (msg.equals("playpre")) {
            currentposition = intent.getIntExtra("listPosition", -1);
            playpre(currentposition);
        } else if (msg.equals("playnext")) {
            currentposition = intent.getIntExtra("listPosition", -1);
            playnext(currentposition);

        } else if (msg.equals("getprogress")) {
            getprogess();

        } else if (msg.equals("sort")) {
            playsort = intent.getIntExtra("music_sort", -1);

        }
        super.onStart(intent, startId);
    }

    private void getprogess() {
        Intent progressIntent = new Intent();
        progressIntent.setAction(ActionDefine.GETPROGRESS);
        progressIntent.putExtra("progress", mediaPlayer.getCurrentPosition());//通过Intent来传递歌曲的总长度
        progressIntent.putExtra("totalprogress", mediaPlayer.getDuration());
        sendBroadcast(progressIntent);
    }


    private void progesschange(int progress) {
        Intent progressIntent = new Intent();
        progressIntent.setAction(ActionDefine.MUSIC_PROGRESS);
        progressIntent.putExtra("progress", progress);    //通过Intent来传递歌曲的总长度
        Log.v("hahamusic", "progress1" + progress);
        mediaPlayer.seekTo(progress);
        sendBroadcast(progressIntent);
    }

    private void play(int currentTime) {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(path);
                mediaPlayer.prepare();
                mediaPlayer.setOnPreparedListener(new PreparedListener(currentTime));
                mediaPlayer.start();
                mhandler.sendEmptyMessage(0x111);
                Log.v("music", "play" + 11111);
            } else {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(path);
                mediaPlayer.start();
                Log.v("music", "play" + 11111);
            }
//            mediaPlayer.reset();// 把各项参数恢复到初始状态
//            mediaPlayer.setDataSource(path);
//            mediaPlayer.prepare(); // 进行缓冲

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final class PreparedListener implements MediaPlayer.OnPreparedListener {
        int currenttime;

        public PreparedListener(int time) {
            this.currenttime = time;
        }

        @Override

        public void onPrepared(MediaPlayer mp) {
            mp.start();
            if(mTimer == null){
                mTimer = new Timer();
                mTask = new LrcTask();
                mTimer.scheduleAtFixedRate(mTask, 0, 1000);
            }else{
                mTimer.cancel();
                mTimer = new Timer();
                mTask = new LrcTask();
                mTimer.scheduleAtFixedRate(mTask, 0, 1000);
            }
            if (currenttime > 0) {
                mp.seekTo(currenttime);
            }
            Intent prepareIntent = new Intent();
            prepareIntent.setAction(ActionDefine.MUSIC_DURATION);
            prepareIntent.putExtra("duration", mp.getDuration());    //通过Intent来传递歌曲的总长度
            sendBroadcast(prepareIntent);


        }
    }

    /**
     * 暂停音乐
     */
    private void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            if(mTimer != null){
                mTimer.cancel();
                mTimer = null;
            }
        }
    }

    private void playnext(int currentposition) {
        Log.v("hahamusic", "next" + 1);
        Intent playnextIntent = new Intent();
        playnextIntent.setAction(ActionDefine.UPDATE_PLAYMUSIC);
        playnextIntent.putExtra("current", currentposition);
        Log.v("hahamusic", "nextcurrent" + currentposition);
        sendBroadcast(playnextIntent);
        play(0);
    }

    private void playpre(int currentposition) {
        Log.v("hahamusic", "pre" + 2);
        Intent playpreIntent = new Intent();
        playpreIntent.setAction(ActionDefine.UPDATE_PLAYMUSIC);
        playpreIntent.putExtra("current", currentposition);
        Log.v("hahamusic", "precurrenttion" + currentposition);
        sendBroadcast(playpreIntent);
        play(0);
    }

    private void resume() {

        mediaPlayer.start();

    }
    public long getMediaPlayStatus(){
        if(mediaPlayer==null){
            return 0;
        }
        return  mediaPlayer.getCurrentPosition();
    }

    private final class mycompletemusiclistener implements MediaPlayer.OnCompletionListener {
        @Override
        public void onCompletion(MediaPlayer mp) {
            switch (playsort) {
                case 0://循环播放
                    currentposition++;
                    if (currentposition < musiclist.size()) {
                        mediaPlayer.seekTo(0);
                        Intent sendIntent = new Intent(ActionDefine.UPDATE_PLAYMUSIC);
                        sendIntent.putExtra("current", currentposition);
                        // 发送广播，将被Activity组件中的BroadcastReceiver接收到
                        sendBroadcast(sendIntent);
                        path = musiclist.get(currentposition).getUrl();
                        play(0);
                    } else {
                        currentposition = 0;
                        mediaPlayer.seekTo(0);
                        Intent sendIntent = new Intent(ActionDefine.UPDATE_PLAYMUSIC);
                        sendIntent.putExtra("current", currentposition);
                        // 发送广播，将被Activity组件中的BroadcastReceiver接收到
                        sendBroadcast(sendIntent);
                        path = musiclist.get(currentposition).getUrl();
                        play(0);
                    }
                    break;
                case 2:
                    Intent sendIntent = new Intent(ActionDefine.UPDATE_PLAYMUSIC);
                    sendIntent.putExtra("current", currentposition);
                    // 发送广播，将被Activity组件中的BroadcastReceiver接收到
                    sendBroadcast(sendIntent);
                    path = musiclist.get(currentposition).getUrl();
                    play(0);
                    break;
                case 1:
                    currentposition=(int)(Math.random()*(musiclist.size()-1));
//                    play(currentposition);
                    Intent sendIntent1 = new Intent(ActionDefine.UPDATE_PLAYMUSIC);
                    sendIntent1.putExtra("current", currentposition);
                    // 发送广播，将被Activity组件中的BroadcastReceiver接收到
                    sendBroadcast(sendIntent1);
                    path = musiclist.get(currentposition).getUrl();
                    play(0);
                    break;
                case 4:

                    break;
                case 5:

                    break;


                default:
                    break;
            }
        }
    }
    /**
     * 展示歌曲的定时任务
     */
    class LrcTask extends TimerTask{
        @Override
        public void run() {
            //获取歌曲播放的位置
            final long timePassed = mediaPlayer.getCurrentPosition();
            Intent lrcinent = new Intent();
            lrcinent.setAction(ActionDefine.FRAGMENT_LRC);
            lrcinent.putExtra("timecurrent", timePassed);
            Log.v("hahalrc", "service1111111" + timePassed);
            sendBroadcast(lrcinent);
        }
    };
}
