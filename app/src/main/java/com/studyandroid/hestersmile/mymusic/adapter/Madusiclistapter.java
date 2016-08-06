package com.studyandroid.hestersmile.mymusic.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.studyandroid.hestersmile.mymusic.R;
import com.studyandroid.hestersmile.mymusic.javabean.music;
import com.studyandroid.hestersmile.mymusic.util.Musicutil;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by hestersmile on 2016/4/28.
 */
public class Madusiclistapter extends BaseAdapter {
    private Handler handler;
    private List<music> mp3list;
    private Context mycontext;
    private ViewHolder holder;
    private String title;
    private String pic;
    private long size;
    private String authtor;
    private int pos = -1;
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private ListView mListView;
    private int mStart;
    private int mEnd;
    private boolean isFirstIn = true;
    private DisplayImageOptions options;
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

    public Madusiclistapter(ListView listView, List<music> mp3list, Context mycontext) {
        this.mp3list = mp3list;
        this.mycontext = mycontext;
        this.mListView = listView;
        myopion();

    }

    private Handler mhandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

        }
    };

    @Override
    public int getCount() {
        return mp3list.size();
    }

    @Override
    public Object getItem(int position) {
        return mp3list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        holder = new ViewHolder();
        if (convertView == null) {
            convertView = View.inflate(mycontext, R.layout.mymusic_list_item, null);
            holder.mymusictitle = (TextView) convertView
                    .findViewById(R.id.music_title);
            holder.mymusictime = (TextView) convertView
                    .findViewById(R.id.music_time);
            holder.mymusicauthor = (TextView) convertView
                    .findViewById(R.id.music_author);
            holder.mymusicpic = (ImageView) convertView
                    .findViewById(R.id.music_pic);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();//通过getTag的方法将数据取出来
        }
        getmp3info(mp3list.get(position));
        if (position == pos) {
            holder.mymusicpic.setImageResource(R.drawable.ic_nomusic);
        } else {
            holder.mymusicpic.setTag(position);
            if (holder.mymusicpic.getTag() != null
                    && holder.mymusicpic.getTag().equals(position)) {
                String myurl = mp3list.get(position).getAublmUrl();

                holder.mymusicpic.setTag(position);
                if (myurl == null) {
                    Bitmap bitmap = Musicutil.getArtwork(mycontext, mp3list.get(position).getId(), mp3list.get(position).getAlbumId(), true, true);
                    Log.i("hahaha", myurl + "++++++++++++++++");
                    if (bitmap == null) {
                        holder.mymusicpic.setImageResource(R.drawable.ic_nomusic);
                    } else {
                        holder.mymusicpic.setImageBitmap(bitmap);

                    }
                } else {
                    myurl = "file://" + myurl;
                    imageLoader.displayImage(myurl, holder.mymusicpic, options, animateFirstListener);
                }
            } else {

                holder.mymusicpic.setImageResource(R.drawable.ic_nomusic);
            }


        }
        holder.mymusicauthor.setText(authtor);
        holder.mymusictime.setText(Musicutil.formatTime(size));
        holder.mymusictitle.setText(title);

        return convertView;
    }

    public void getmp3info(music music) {
        title = music.getTitle();
        pic = music.getAlbum();
        size = music.getDuration();
        authtor = music.getArtist();

    }

    private class ViewHolder {
        private TextView mymusictitle;
        private TextView mymusictime;
        private TextView mymusicauthor;
        private ImageView mymusicpic;

    }

    private void myopion() {
        // 使用DisplayImageOptions.Builder()创建DisplayImageOptions
        options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.ic_nomusic)            // 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.drawable.ic_nomusic)    // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.ic_nomusic)        // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true)                        // 设置下载的图片是否缓存在内存中
                .cacheOnDisc(true)                            // 设置下载的图片是否缓存在SD卡中
                .displayer(new RoundedBitmapDisplayer(20))    // 设置成圆角图片
                .build();                                    // 创建配置过得DisplayImageOption对象

    }

    private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

        static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                // 是否第一次显示
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    // 图片淡入效果
                    FadeInBitmapDisplayer.animate(imageView, 500);
                    displayedImages.add(imageUri);
                }
            }
        }
    }
}
