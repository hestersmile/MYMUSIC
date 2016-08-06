package com.studyandroid.hestersmile.mymusic.util;

import com.studyandroid.hestersmile.mymusic.MusicApplication;
import com.studyandroid.hestersmile.mymusic.javabean.music;

/**
 * Created by hestersmile on 2016/6/12.
 */
public final class Singleton {
    private music mymusic;

    private volatile static Singleton singleton;
    private int listposition;
    private int playstuats;//1是播放 0是暂停

    private Singleton() {
    }

    public static Singleton getInstance() {

        if (singleton == null) {

            synchronized (Singleton.class) {

                if (singleton == null) {

                    singleton = new Singleton();

                }

            }

        }

        return singleton;

    }

    public void setListposition(int listposition) {
        this.listposition = listposition;
    }
    public void setPlayStatus(int playstuats) {
        this.playstuats = playstuats;
    }

    public int getPlaystuats() {
        return playstuats;
    }

    public int getListposition() {
        return listposition;
    }
}
