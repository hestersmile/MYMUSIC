package com.studyandroid.hestersmile.mymusic.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.studyandroid.hestersmile.mymusic.R;
import com.studyandroid.hestersmile.mymusic.javabean.lrconline;
import com.studyandroid.hestersmile.mymusic.javabean.music;
import com.studyandroid.hestersmile.mymusic.util.Musicutil;

import java.util.List;

/**
 * Created by hestersmile on 2016/4/28.
 */
public class DialogeMusicListAdapter extends BaseAdapter {
    private List<lrconline> mp3list;
    private Context mycontext;
    private ViewHolder holder;
    private String title;
    private long time;
    private long size;
    private String authtor;
    private int pos = -1;

    public DialogeMusicListAdapter( List<lrconline> mp3list, Context mycontext) {
        this.mp3list = mp3list;
        this.mycontext = mycontext;
    }

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
            convertView = View.inflate(mycontext, R.layout.pop_music_list__item, null);
            holder.mymusictitle = (TextView) convertView.findViewById(R.id.list_pop_title);
            holder.mymusicauthor = (TextView) convertView.findViewById(R.id.list_pop_author);
            holder.mymusictime = (TextView) convertView.findViewById(R.id.list_pop_count);
            holder.mymusicid = (TextView) convertView.findViewById(R.id.list_pop_id);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();//通过getTag的方法将数据取出来
        }
        getmp3info(mp3list.get(position));
        holder.mymusicauthor.setText(authtor);
        holder.mymusictime.setText(""+size);
        holder.mymusictitle.setText(title);
        int id=position+1;
        holder.mymusicid.setText(id + "");
        return convertView;
    }

    public void getmp3info(lrconline lrc) {
        title = lrc.getSongname();
        size = lrc.getDuration();
        authtor = lrc.getFileauthor();
    }

    private class ViewHolder {
        private TextView mymusictitle;
        private TextView mymusictime;
        private TextView mymusicauthor;
        private TextView mymusicid;
    }
    /**
     * 图片加载第一次显示监听器
     * @author Administrator
     *
     */

}
