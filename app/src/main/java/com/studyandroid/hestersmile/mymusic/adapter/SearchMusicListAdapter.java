package com.studyandroid.hestersmile.mymusic.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.studyandroid.hestersmile.mymusic.R;
import com.studyandroid.hestersmile.mymusic.javabean.music;
import com.studyandroid.hestersmile.mymusic.util.Musicutil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hestersmile on 2016/4/28.
 */
public class SearchMusicListAdapter extends BaseAdapter implements Filterable {
    private  List<music> mp3list, copyData;
    private Context mycontext;
    private ViewHolder holder;
    private String title;
    private long time;
    private long size;
    private String authtor;
    private int pos = -1;
    private ImageLoader imageLoader;
    private ListView mListView;
    private int count;
    public static List<music> filterData;

    public SearchMusicListAdapter(ListView listView, List<music> mp3list, Context mycontext) {
        this.mp3list = mp3list;
        this.mycontext = mycontext;
        this.mListView = listView;
        copyData = mp3list;
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
        holder.mymusictime.setText(Musicutil.formatTime(size) + "");
        holder.mymusictitle.setText(title);
        int id = position + 1;
        holder.mymusicid.setText(id + "");
        return convertView;
    }

    public void getmp3info(music music) {
        title = music.getTitle();
        size = music.getDuration();
        authtor = music.getArtist();
    }


    private Filter myFilter;

    @Override
    public Filter getFilter() {
        if (null == myFilter) {
            myFilter = new MyFilter();
        }
        return myFilter;
    }

    class MyFilter extends Filter {
        // 定义过滤规则
        protected FilterResults performFiltering(CharSequence constraint) {
            filterData = new ArrayList<music>();

            if (constraint != null && constraint.toString().trim().length() > 0) {
                String key = constraint.toString().trim().toLowerCase();
                for (music item : copyData) {
                    if (item.getTitle().toLowerCase().indexOf(key) != -1) {
                        filterData.add(item);
                    }
                }
            } else {    //如果搜索框为空，就恢复原始数据
                filterData = copyData;
                copyData=filterData;
            }
            FilterResults results = new FilterResults();
            results.values = filterData;
            results.count = filterData.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {

            mp3list = (List<music>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }

    }

    ;

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
