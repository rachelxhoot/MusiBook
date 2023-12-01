package com.example.myapplication;

import android.graphics.Bitmap;

public class Bean {
    //歌曲名字
    private String song;
    //歌手
    private String singer;
    // 图片id
    private int picId;
    //歌曲路径
    private String path;
    //缩略图
    private Bitmap bitmap;

    @Override
    public String toString() {
        return "Bean{" +
                "song='" + song + '\'' +
                ", singer='" + singer + '\'' +
                ", picId=" + picId +
                ", path='" + path + '\'' +
                ", bitmap=" + bitmap +
                '}';
    }

    public Bean() {
    }

    public Bean(String song, String singer, int picId, String path, Bitmap bitmap) {
        this.song = song;
        this.singer = singer;
        this.picId = picId;
        this.path = path;
        this.bitmap = bitmap;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getSong() {
        return song;
    }

    public void setSong(String song) {
        this.song = song;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public int getPicId() {
        return picId;
    }

    public void setPicId(int picId) {
        this.picId = picId;
    }
}
