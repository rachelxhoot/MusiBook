package com.example.myapplication.util;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.myapplication.Bean;
import com.example.myapplication.MyAdapter;
import com.example.myapplication.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class PlayMusicUtils {

    //单例模式
    // 创建一个静态的，私有的 当前类对象
    private static volatile PlayMusicUtils playMusicUtils;

    List<Bean> songList=new ArrayList<>();
    Context context;


    // 构造方法私有化

    private PlayMusicUtils() {
    }

    private PlayMusicUtils( Context context,List<Bean> songList) {
        this.songList = songList;
        this.context = context;
    }

    // 写getInstance方法
    public static  synchronized PlayMusicUtils getInstance(Context context,List<Bean> songList) {
        if (playMusicUtils == null) {
            playMusicUtils=new PlayMusicUtils(context,songList);
        }
        return playMusicUtils;
    }




    /**
     * @param i 点击条目的索引
     * @return 返回一个uri对象
     */
    public Uri playCommon(int i) {
        String path = songList.get(i).getPath();
        //把路径转换成uri对象
        Uri uri = Uri.parse(path);

        return uri;
    }


    /**
     * 通过传入的索引  让我们的项目去播放对应索引上的歌曲并且根据索引设置条目选中状态和展示
     * 歌曲对应的专辑图片
     * @param i
     */
    public void playByIndex(int i, ImageView s_play) {



        //设置对应歌曲的条目选中状态
        Uri uri = playCommon(i);
        // 播放对应的歌曲
        playMusic(uri,s_play);

    }

    /**
     * 通过传入的索引  让我们的项目去播放对应索引上的歌曲并且根据索引设置条目选中状态和展示
     * 歌曲对应的专辑图片
     * @param i
     */
    public void playByIndex(int i, MyAdapter adapter, List<Bean> songList, ImageView s_play, ImageView s_imgBg, ImageView s_s_img ,
                            ImageView s_img, TextView s_singerName, TextView s_Title) {



        //设置对应歌曲的条目选中状态
        adapter.setSelectItem(i);
        Uri uri = playCommon(i);
        // 播放对应的歌曲
        playMusic(uri,s_play);
        // 设置对应歌曲歌曲名称歌手名称图片
        setImageStyleAndText(i,s_imgBg,s_s_img,s_img,s_singerName,s_Title);
    }


    private MediaPlayer mediaPlayer;
    /**
     * 改方法是来播放音乐的   传一个uri进去就可以了
     *
     * @param uri 参数 歌曲的地址
     * @return 返回mediaplayer对象
     */
    private MediaPlayer playMusic( Uri uri, ImageView s_play) {
        try {
            // 如果mediaPlayer为空  创建player
            if (mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();
                //吧数据源设置给player
                mediaPlayer.setDataSource(context, uri);
                //准备播放  加个缓冲时间
                mediaPlayer.prepare();
                // 开始播放
                mediaPlayer.start();
            }    //   如果player不为空，而且处于播放音乐状态
            else if (mediaPlayer != null) {
                //先停掉播放
                mediaPlayer.stop();
                //播放器进行重置
                mediaPlayer.reset();
                mediaPlayer.setDataSource(context, uri);
                mediaPlayer.prepare();
                mediaPlayer.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //根据音乐的播放状态切换播放按钮的图片显示效果
        if (mediaPlayer.isPlaying()) {
            s_play.setImageResource(R.mipmap.s_pause);
        }else {
            s_play.setImageResource(R.mipmap.s_play);
        }


        return mediaPlayer;
    }

    /**
     * ctrl+Alt+M 抽取方法快捷键
     * 设置图片的样式 高斯模糊  圆角
     *
     * @param index 条目对应的list集合的索引
     */
    private void setImageStyleAndText(int index, ImageView s_imgBg, ImageView s_s_img ,
                                      ImageView s_img, TextView s_singerName,TextView s_Title) {
        Glide.with(context).load(songList.get(index).getBitmap())
                .apply(bitmapTransform(new BlurTransformation(6)))
                .into(s_imgBg); //模糊效果
        s_s_img.setImageBitmap(songList.get(index).getBitmap());
        ///  s_s_img.setImageBitmap(songList.get(0).getBitmap());
        Glide.with(context).load(songList.get(index).getBitmap())
                .apply(bitmapTransform(new CircleCrop()))
                .into(s_img);//圆角

        //设置歌曲名字和歌手名
        s_singerName.setText(songList.get(index).getSinger());
        s_Title.setText(songList.get(index).getSong());
    }

}
