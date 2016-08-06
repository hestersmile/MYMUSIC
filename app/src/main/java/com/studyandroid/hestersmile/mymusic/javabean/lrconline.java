package com.studyandroid.hestersmile.mymusic.javabean;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by hestersmile on 2016/6/4.
 */
public class lrconline implements Serializable {
    private String filename;
    private  String songname;
    private String fileauthor;
    private String hash;
    private int duration;

    public lrconline(String filename,String songname, String fileauthor, String hash, int duration) {
        this.filename = filename;
        this.fileauthor = fileauthor;
        this.hash = hash;
        this.duration = duration;
        this.songname=songname;
    }

    public String getSongname() {
        return songname;
    }

    public void setSongname(String songname) {
        this.songname = songname;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getFileauthor() {
        return fileauthor;
    }

    public void setFileauthor(String fileauthor) {
        this.fileauthor = fileauthor;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
