package com.studyandroid.hestersmile.mymusic.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.LinearGradient;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.studyandroid.hestersmile.mymusic.LrcFunction.DefaultLrcBuilder;
import com.studyandroid.hestersmile.mymusic.LrcFunction.ILrcBuilder;
import com.studyandroid.hestersmile.mymusic.LrcFunction.ILrcViewListener;
import com.studyandroid.hestersmile.mymusic.LrcFunction.LrcRow;
import com.studyandroid.hestersmile.mymusic.LrcFunction.LrcView;
import com.studyandroid.hestersmile.mymusic.R;
import com.studyandroid.hestersmile.mymusic.action.ActionDefine;
import com.studyandroid.hestersmile.mymusic.adapter.DialogeMusicListAdapter;
import com.studyandroid.hestersmile.mymusic.javabean.lrconline;
import com.studyandroid.hestersmile.mymusic.util.AsyncDownloadLrcUtil;
import com.studyandroid.hestersmile.mymusic.util.AsyncQueryLrcUtil;
import com.studyandroid.hestersmile.mymusic.util.Musicutil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.List;


/**
 * Created by hestersmile on 2016/5/25.
 */
public class PlayMusicLrcFragment extends Fragment {
    private View activityView;
    private String title;
    private String author;
    private TextView mytitle;
    private LrcView mylrccomtent;
    private TextView mylrc;
    private TextView myauthor;
    private RelativeLayout mlayout;
    private String lrccontent;
    private String mylrctitle;
    private Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x111) {
                Log.v("hahalrc", "lllllllllllll");
                showListPop(activityView);
                Log.v("hahalrc", "qqqqqqqqq");
                Bundle bundle = (Bundle) msg.obj;
                final List<lrconline> mlrclist = (List<lrconline>) bundle.getSerializable("MyObject");
                if (mlrclist != null) {
                    DialogeMusicListAdapter myadpter = new DialogeMusicListAdapter(mlrclist, getActivity());
                    poplistview.setAdapter(myadpter);
                    poplistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            AsyncDownloadLrcUtil downlrc = new AsyncDownloadLrcUtil(mlrclist.get(position), mhandler);
                            downlrc.execute();
                            mylrctitle = mlrclist.get(position).getSongname();
                            popupWindow.dismiss();
                        }
                    });
                } else {
                    mylrc.setText("不好意思，并未查询到相关歌词");
                    popupWindow.dismiss();
                }
            } else if (msg.what == 0x222) {
                Toast.makeText(getActivity(), "服务器错误，请重新尝试", Toast.LENGTH_SHORT).show();
            } else if (msg.what == 0x333) {
                Bundle bundle = (Bundle) msg.obj;
                lrccontent = bundle.getString("lrc");
                Log.v("hahalrc", "lrc:" + lrccontent);
                mylrc.setVisibility(View.GONE);
                mylrccomtent.setVisibility(View.VISIBLE);

                path = getActivity().getFilesDir() + "/" + "lrccache" + "/" + mylrctitle + ".lrc";
                saveFile(mylrctitle, lrccontent);
                String lrc = getFromLocal(path);
                Log.v("hahalrc", "getlrclocal" + lrc);
                //解析歌词构造器
                ILrcBuilder builder = new DefaultLrcBuilder();
                //解析歌词返回LrcRow集合
                List<LrcRow> rows = builder.getLrcRows(lrc);
                //将得到的歌词集合传给mLrcView用来展示
                mylrccomtent.setLrc(rows);
            }
            super.handleMessage(msg);
        }
    };
    private PopupWindow popupWindow;
    private ListView poplistview;
    private String path = null;
    private lrcReceiver lrcRec;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initRegister();
    }

    private void initRegister() {
        lrcRec = new lrcReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ActionDefine.FRAGMENT_LRC);
        getActivity().registerReceiver(lrcRec, filter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activityView = inflater.inflate(R.layout.fragment_play_lrclayout, null);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("mybackground",
                Context.MODE_PRIVATE);
        int backres = sharedPreferences.getInt("resource", R.drawable.ic_bac0);
        mlayout = (RelativeLayout) activityView.findViewById(R.id.lrclayout);
        mlayout.setBackgroundResource(backres);
        iniview();


        return activityView;
    }


    private void iniview() {
        Bundle mybundle = getArguments();
        title = mybundle.getString("mytitle");
        author = mybundle.getString("myauthor");
        mytitle = (TextView) activityView.findViewById(R.id.fragment_title);
        myauthor = (TextView) activityView.findViewById(R.id.fragment_author);
        mylrccomtent = (LrcView) activityView.findViewById(R.id.fragment_lrccontent);
        mylrc = (TextView) activityView.findViewById(R.id.fragment_lrc);
//        mylrccomtent.setListener(new ILrcViewListener() {
//            //当歌词被用户上下拖动的时候回调该方法,从高亮的那一句歌词开始播放
//            public void onLrcSeeked(int newPosition, LrcRow row) {
//                if (mPlayer != null) {
//                    Log.d(TAG, "onLrcSeeked:" + row.time);
//                    mPlayer.seekTo((int) row.time);
//                }
//            }
//        });
        initfile();
//        mylrc.setText("并未搜索到相关的lrc文件，是否下载");
        Log.i("hahamusic", "title" + title);
        Log.i("hahamusic", "author" + author);
        mytitle.setText(title);
        myauthor.setText(author);

    }

    public void initfile() {

        String filepath = String.valueOf(getActivity().getFilesDir() + "/" + "lrccache");
        path = filepath;
        File file = new File(filepath);
        if (!file.exists()) {
            try {
                file.mkdirs();
            } catch (Exception e) {
            }
        } else {
//           File file1 = new File(filepath+"/"+title+".lrc");
            Boolean res = toSearchFiles(file);
            Log.v("hahalrc", "pathinit" + path);
            if (res == true) {
                mylrc.setVisibility(View.GONE);
                mylrccomtent.setVisibility(View.VISIBLE);
                //从assets目录下读取歌词文件内容
                String lrc = getFromLocal(path);
                Log.v("hahalrc", "getlrclocal" + lrc);
                //解析歌词构造器
                ILrcBuilder builder = new DefaultLrcBuilder();
                //解析歌词返回LrcRow集合
                List<LrcRow> rows = builder.getLrcRows(lrc);
                //将得到的歌词集合传给mLrcView用来展示
                mylrccomtent.setLrc(rows);
                Log.v("hahalrc", "path：" + path);

            } else {
                Log.v("hahalrc", "path：" + "没有文件");
                mylrccomtent.setVisibility(View.GONE);
                mylrc.setVisibility(View.VISIBLE);
                mylrc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AsyncQueryLrcUtil myasync = new AsyncQueryLrcUtil(title, author, mhandler);
                        myasync.execute();
                    }
                });
            }

        }
    }

    public Boolean toSearchFiles(File file) {
        boolean search = false;
        File[] files = file.listFiles();
        for (File tf : files) {
            if (tf.isDirectory()) {
                toSearchFiles(tf);
            } else {
                try {
                    if (tf.getName().indexOf(title) > -1) {
                        search = true;
                        path = tf.getPath();
                    }
                } catch (Exception e) {
                }
            }
        }
        return search;
    }

    public void saveFile(String title, String lrc) {

        try {
            File file = new File(path);
            if (!file.exists()) {
                file.createNewFile();
            } else {
                file.delete();
                file.createNewFile();
            }
            FileOutputStream outStream = new FileOutputStream(file);
            outStream.write(lrc.getBytes());
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getFromLocal(String fileName) {
        try {
            InputStreamReader inputReader = new InputStreamReader(new FileInputStream(fileName), "UTF-8");
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";
            String result = "";
            while ((line = bufReader.readLine()) != null) {
                if (line.trim().equals(""))
                    continue;
                result += line + "\r\n";
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public void changeMyTitle(String mtitle, String mauthor) {
        title = mtitle;
        author = mauthor;
        mytitle.setText(mtitle);
        myauthor.setText(mauthor);
    }

    private void showListPop(View parent) {
        // TODO Auto-generated method stub
        // 获取自定义布局文件activity_popupwindow_left.xml的视图
        //获取屏幕尺寸大小，是程序能在不同大小的手机上有更好的兼容性
        Display display = ((WindowManager) getActivity().getSystemService(getActivity().WINDOW_SERVICE)).getDefaultDisplay();
        int height = display.getHeight();
        int width = display.getWidth();
        if (popupWindow == null) {
            Log.i("hahamusic", "开始");
            View popupWindow_view = getActivity().getLayoutInflater().inflate(R.layout.pop_music_list, null,
                    false);
            popupWindow = new PopupWindow(popupWindow_view, ViewGroup.LayoutParams.FILL_PARENT,
                    height / 2);
//            popupWindow.showAtLocation(parent, Gravity.CENTER_VERTICAL, 0, 0);
            poplistview = (ListView) popupWindow_view.findViewById(R.id.list_pop);

        }

        ColorDrawable cd = new ColorDrawable(0x000000);
        popupWindow.setBackgroundDrawable(cd);

        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = 0.2f;
        getActivity().getWindow().setAttributes(lp);

        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation((View) parent.getParent(), Gravity.BOTTOM, 0, 0);
        popupWindow.update();
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            public void onDismiss() {
                WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
                lp.alpha = 1f;
                getActivity().getWindow().setAttributes(lp);
            }
        });

    }

    public class lrcReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.v("hahalrc", "service22222");
            String playaction = intent.getAction();
            switch (playaction) {
                case ActionDefine.FRAGMENT_LRC:
                    long timePassed = intent.getLongExtra("timecurrent", -1);
                    Log.v("hahalrc", "service3333333" + timePassed);
                    mylrccomtent.seekLrcToTime(timePassed);
                    break;
                default:
                    break;
            }
        }


    }
}
