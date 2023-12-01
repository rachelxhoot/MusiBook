package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.util.BitmapUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class search extends AppCompatActivity {


    List<Bean> songList = new ArrayList<>();
    private EditText song_search;
    private TextView song_result;
    private ImageView image_result;
    private TextView singer_result;
    private TextView search_bottom;
    private int songIndex=-1;

    private static final String TAG = "search";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initData();


        song_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                for(int i=0;i<songList.size();i++){
                    if(songList.get(i).getSong().equals(s.toString())){
                        song_result.setText(songList.get(i).getSong());
                        image_result.setImageBitmap(songList.get(i).getBitmap());
                        singer_result.setText(songList.get(i).getSinger());
                        search_bottom.setText("");
                        songIndex=i;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        song_result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!song_result.getText().toString().equals("")){

                    Intent intent=new Intent();
                    intent.putExtra("DataReturn",songIndex);
                    setResult(RESULT_OK,intent);


                    finish();
                }
            }
        });

    }

    /**
     * 获取手机上的音乐文件并放到list集合中去
     */
    private void initData() {

        song_result=findViewById(R.id.song_result);
        singer_result=findViewById(R.id.singer_result);
        image_result=findViewById(R.id.image_result);
        song_search=findViewById(R.id.song_text);
        search_bottom=findViewById(R.id.search_bottom);


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
        Cursor query = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, message,
                null,
                null,
                null);


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
                Bitmap bitmap = BitmapUtils.getArtwork(search.this, songId, albumId, true);
                Bean bean = new Bean(title, singer, R.mipmap.ic_launcher, path, bitmap);

                songList.add(bean);
            }
        }

    }



}