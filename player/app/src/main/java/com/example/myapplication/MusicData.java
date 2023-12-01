package com.example.myapplication;

import android.graphics.Bitmap;

import java.io.Serializable;

public class MusicData implements Serializable{
    /*音乐资源id*/
    private String mMusicRes;
    /*专辑图片id*/
    private int mMusicPicRes;
    /*音乐名称*/
    private String mMusicName;
    /*作者*/
    private String mMusicAuthor;

    private Bitmap bitmap;

    public MusicData(String mMusicRes, int mMusicPicRes, String mMusicName, String mMusicAuthor, Bitmap bitmap) {
        this.mMusicRes = mMusicRes;
        this.mMusicPicRes = mMusicPicRes;
        this.mMusicName = mMusicName;
        this.mMusicAuthor = mMusicAuthor;
        this.bitmap = bitmap;
    }

    public String getmMusicRes() {
        return mMusicRes;
    }

    public void setmMusicRes(String mMusicRes) {
        this.mMusicRes = mMusicRes;
    }

    public int getmMusicPicRes() {
        return mMusicPicRes;
    }

    public void setmMusicPicRes(int mMusicPicRes) {
        this.mMusicPicRes = mMusicPicRes;
    }

    public String getmMusicName() {
        return mMusicName;
    }

    public void setmMusicName(String mMusicName) {
        this.mMusicName = mMusicName;
    }

    public String getmMusicAuthor() {
        return mMusicAuthor;
    }

    public void setmMusicAuthor(String mMusicAuthor) {
        this.mMusicAuthor = mMusicAuthor;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }



    public int getMusicPicRes() {
        return mMusicPicRes;
    }

    public String getMusicName() {
        return mMusicName;
    }

    public String getMusicAuthor() {
        return mMusicAuthor;
    }
}
