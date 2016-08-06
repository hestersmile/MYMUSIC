package com.studyandroid.hestersmile.mymusic.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.studyandroid.hestersmile.mymusic.R;
import com.studyandroid.hestersmile.mymusic.javabean.music;
import com.studyandroid.hestersmile.mymusic.util.ImageCompressUtil;
import com.studyandroid.hestersmile.mymusic.util.Musicutil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.channels.Pipe;
import java.util.List;

/**
 * Created by hestersmile on 2016/4/28.
 */
public class GridListAdapter extends BaseAdapter {
    private int height;
    private int width;
    private Context mycontext;
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private GridView mgridView;
    private int[] mypicarray;

    public GridListAdapter(GridView listView, int[] picarray, int height, int width, Context mycontext) {
        this.height = height;
        this.width = width;
        this.mypicarray = picarray;
        this.mycontext = mycontext;
        this.mgridView = listView;
    }

    @Override
    public int getCount() {
        return mypicarray.length;
    }

    @Override
    public Object getItem(int position) {
        return mypicarray[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageview;
        if (convertView == null) {

            imageview = new ImageView(mycontext);
            imageview.setLayoutParams(new GridView.LayoutParams(3*width/7, width/3*2));
            imageview.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageview.setPadding(8, 8, 8, 8);
        } else {
            imageview = (ImageView) convertView;
        }
        Bitmap mybit = BitmapFactory.decodeResource(mycontext.getResources(), mypicarray[position]);
        Log.v("hahamusic", "bim" + mybit);
//        mybit = ImageCompressUtil.ratio(mybit,80,120);
        imageview.setImageBitmap(mybit);
//        imageLoader.displayImage(myurl, holder.mymusicpic, options, animateFirstListener);
        return imageview;
    }

    private Bitmap compressBmpFromBmp(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int options = 100;
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        while (baos.toByteArray().length / 1024 > 100) {
            baos.reset();
            options -= 10;
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
        return bitmap;
    }

}
