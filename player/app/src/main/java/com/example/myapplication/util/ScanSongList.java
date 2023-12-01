package com.example.myapplication.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.provider.MediaStore;

import com.example.myapplication.Bean;
import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class ScanSongList {



    //单例模式
    // 创建一个静态的，私有的 当前类对象
    private static volatile ScanSongList scanSongList;
    List<Bean> songList=new ArrayList<>();

    // 构造方法私有化

    private ScanSongList() {
    }

    // 写getInstance方法
    public static  synchronized ScanSongList getInstance() {
        if (scanSongList == null) {
            scanSongList=new ScanSongList();
        }
        return scanSongList;
    }


    /**
     * 获取手机上的音乐文件并放到list集合中去
     */
    public List<Bean> initData(Context context) {
        /*
         * 读取手机内存中的音频数据
         * */
        //创建一个数组
        String[] message = new String[]{
                MediaStore.Audio.Media.TITLE,      //歌曲名字
                MediaStore.Audio.Media.DURATION,    //歌曲的播放时间
                MediaStore.Audio.Media.ARTIST,      //歌曲的歌手
                MediaStore.Audio.Media._ID,         //歌曲的id
                MediaStore.Audio.Media.ALBUM,       //歌曲的专辑
                MediaStore.Audio.Media.DISPLAY_NAME,//歌曲的专辑名
                MediaStore.Audio.Media.DATA,        //歌曲的路径
                MediaStore.Audio.Media.ALBUM_ID,    //获取歌曲的图片缩略图
        };
        //通过ContentREsolver获取对应的歌曲
        //MediaStore.Audio.Media.EXTERNAL_CONTENT_URI   查询内存中的媒体文件
        //query里面的参数： 地址，查询的内容，查询条件，参数，按什么排序
        Cursor query = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                message,
                null,
                null,
                null);
        // 解析查询结果
        if (query != null) {
            //我们不知道query里面有多少数据
            while (query.moveToNext()) {
                String title = query.getString(0);
                Long durition = query.getLong(1);
                String singer = query.getString(2);
                int songId = query.getInt(3);
                String path = query.getString(6);
                long albumId = query.getLong(7);
                //调用工具类把图片生成 BitmapUtils 工具类可以直接copy
                Bitmap bitmap = BitmapUtils.getArtwork(context, songId,
                        albumId, true);
                Bean bean = new Bean(title, singer, R.mipmap.ic_launcher, path, bitmap);
                //用来传递javabean对象
                System.out.println(bean.toString());
                songList.add(bean);
            }
        }
        return songList;

    }
}
