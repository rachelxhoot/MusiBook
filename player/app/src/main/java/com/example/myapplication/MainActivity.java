package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.myapplication.util.BitmapUtils;
import com.example.myapplication.util.GetTime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class MainActivity extends AppCompatActivity {

    private ImageView s_imgBg;
    private ImageView s_img;
    private RelativeLayout s_top;
    private ListView sl_list;
    private TextView s_Title;
    private TextView s_singerName;
    private ImageView s_last;
    private ImageView s_play;
    private ImageView s_next;
    private RelativeLayout s_list;
    private ImageView s_s_img;
    private TextView tv_total_time;
    private TextView tv_current_time;
    private SeekBar sb_main_progress;
    List<Bean> songList = new ArrayList<>();
    private MediaPlayer mediaPlayer;
    int indexOfSong=0;
    private ImageView search;
    private ImageView other;
    private TextView reading;
    private static final String TAG = "MainActivity";


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //让状态栏变成透明颜色
        makeStatusBarTransParent();


        //初始化view
        initView();

        //检查权限，如果权限通过则 从本地读取歌曲并保存到列表
        checkPermissions();

        // 把搜索到的音乐添加到listview列表中
        MyAdapter adapter = new MyAdapter(MainActivity.this, songList);
        sl_list.setAdapter(adapter);



        sl_list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        adapter.setSelectItem(indexOfSong);

        sl_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(MainActivity.this, "即将播放:"+songList.get(i).getSong()+"...", Toast.LENGTH_SHORT).show();
                indexOfSong=i;
                adapter.setSelectItem(i);
                Uri uri = playCommon(i);
                playMusic(uri);

                setBackground(i);

                handler.sendEmptyMessage(0);

            }

        });





        //添加seekbar的拖曳功能
        sb_main_progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override  //当到进度条发生改变的时候调用
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override  //开始拖动进度条的时候该方法执行
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override//停止拖动的时候该方法执行
            public void onStopTrackingTouch(SeekBar seekBar) {
                //seekto  让播放起跳到某一个位置进行播放
                    if(mediaPlayer==null){
                        Toast.makeText(MainActivity.this,"请选择音乐！",Toast.LENGTH_SHORT).show();
                        sb_main_progress.setProgress(0);
                    }else{

                        mediaPlayer.seekTo(seekBar.getProgress());
                    }


            }
        });


        //s_play按钮
        s_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                if(mediaPlayer.isPlaying()){
//                    s_play.setImageResource(R.mipmap.s_play);
//                }else{
//                    s_play.setImageResource(R.mipmap.s_stop);
//                }
                Uri uri = playCommon(indexOfSong);
                funByPlay(uri);
                handler.sendEmptyMessage(0);


            }
        });

        s_last.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(indexOfSong!=0){
                    indexOfSong-=1;
                    if(mediaPlayer.isPlaying()) mediaPlayer.stop();

                    setBackground(indexOfSong);
                    Uri uri=playCommon(indexOfSong);

                    adapter.setSelectItem(indexOfSong);
                    changeMusic(uri);
                    handler.sendEmptyMessage(0);

                }else{
                    Toast.makeText(MainActivity.this,"已经是第一首歌！",Toast.LENGTH_SHORT).show();
                }


            }
        });

        s_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(indexOfSong<songList.size()-1){
                    indexOfSong+=1;
                    if(mediaPlayer.isPlaying()) mediaPlayer.stop();
                    setBackground(indexOfSong);
                    Uri uri=playCommon(indexOfSong);

                    adapter.setSelectItem(indexOfSong);
                    changeMusic(uri);
                    handler.sendEmptyMessage(0);

                }else{
                    Toast.makeText(MainActivity.this,"已经是最后一首歌！",Toast.LENGTH_SHORT).show();
                }


            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,search.class);

                startActivityForResult(intent,1);
            }
        });

        other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder bulider=new AlertDialog.Builder(MainActivity.this);

                //自定义
                View dialodview= LayoutInflater.from(MainActivity.this).inflate(R.layout.other,null);

                bulider.setView(dialodview);
                final Dialog dialog=bulider.create();
                dialog.show();
                WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
                layoutParams.width = 500;
                layoutParams.height = 350;

                //获得屏幕的宽和高
                WindowManager mWindowManager = (WindowManager) MainActivity.this.getSystemService(MainActivity.this.WINDOW_SERVICE);
                int screenWidth = mWindowManager.getDefaultDisplay().getWidth();
                int screenHeight= mWindowManager.getDefaultDisplay().getHeight();
                layoutParams.x=screenWidth/2-200;
                layoutParams.y=-screenHeight/2;

                dialog.getWindow().setAttributes(layoutParams);

                dialog.getWindow().findViewById(R.id.reading).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ComponentName ebook = new ComponentName("open_open.com.androidagreedemo", "open_open.com.androidagreedemo.MainActivity");
                        Intent intent=new Intent();
                        intent.setComponent(ebook);
                        startActivity(intent);
                    }
                });

                dialog.getWindow().findViewById(R.id.setting).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent =new Intent(MainActivity.this,setting.class);
                        startActivityForResult(intent,2);
                    }
                });
            }
        });



    }

    private int bg_index=0;

    //接受search返还的数据
    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);






        if(data!=null){

            switch (requestCode){
                case 1:
                    if(resultCode==RESULT_OK){
                        indexOfSong=data.getIntExtra("DataReturn",-1);
                        Uri uri=playCommon(indexOfSong);
                        playMusic(uri);
                        setBackground(indexOfSong);
                        handler.sendEmptyMessage(0);
                    }
                    break;
                case 2:
                    if(resultCode==RESULT_OK){
                        bg_index=data.getIntExtra("index",0);
                        Drawable drawable = null;
                        if(bg_index!=0){
                            if(bg_index==1) drawable= ResourcesCompat.getDrawable(getResources(), R.drawable.bg1, null);
                            if(bg_index==2) drawable= ResourcesCompat.getDrawable(getResources(), R.drawable.bg2, null);
                            if(bg_index==3) drawable= ResourcesCompat.getDrawable(getResources(), R.drawable.bg3, null);
                            if(bg_index==4) drawable= ResourcesCompat.getDrawable(getResources(), R.drawable.bg4, null);

                            if(drawable!=null) sl_list.setBackground(drawable);

                        }
                    }

            }



        }




    }

    // 创建一个handler对象 该对象的作用是随时传递播放的进度到进度条
//    @SuppressLint("HandlerLeak")
    @SuppressLint("HandlerLeak")
    Handler handler=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            //获取歌曲的总时长 毫秒值
            int duration=mediaPlayer.getDuration();
            //获取歌曲的当前时长  毫秒值
            int currentPosition = mediaPlayer.getCurrentPosition();



            // 毫秒值转成时分秒
            String total = GetTime.generateTime(duration);
            String current = GetTime.generateTime(currentPosition);

            //设置音乐播放的时长
            tv_current_time.setText(current+"/");
            tv_total_time.setText(total);


            sb_main_progress.setMax(duration);
            sb_main_progress.setProgress(currentPosition);


            s_play.setImageResource(R.mipmap.s_play);

            if(!mediaPlayer.isPlaying()){
                s_play.setImageResource(R.mipmap.s_stop);
            }





            handler.sendEmptyMessageDelayed(0,100);


        }
    };

    /**
     *
     * @param i   点击条目的索引
     * @return    返回一个uri对象
     */
    private Uri playCommon(int i) {
        String path = songList.get(i).getPath();
        //把路径转换成uri对象
        Uri uri= Uri.parse(path);

        return  uri;
    }

    /**
     *
     * @param uri   参数 歌曲的地址
     * @return      返回mediaplayer对象
     */
    private MediaPlayer playMusic(Uri uri) {

        try {
            // 如果mediaPlayer为空  创建player
            if (mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();
                //吧数据源设置给player
                mediaPlayer.setDataSource(MainActivity.this, uri);
                //准备播放  加个缓冲时间
                mediaPlayer.prepare();
                // 开始播放
                mediaPlayer.start();
            }    //   如果player不为空，而且处于播放音乐状态
            else if (mediaPlayer!= null) {
                //先停掉播放
                mediaPlayer.stop();
                //播放器进行重置
                mediaPlayer.reset();
                mediaPlayer.setDataSource(MainActivity.this,uri);
                mediaPlayer.prepare();
                mediaPlayer.start();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mediaPlayer;

    }

    /** ctrl+Alt+M 抽取方法快捷键
     * 设置图片的样式 高斯模糊  圆角
     * @param index   条目对应的list集合的索引
     */
    private void setImageStyleAndText(int index) {
        Glide.with(this).load(songList.get(index).getBitmap())
                .apply(bitmapTransform(new BlurTransformation(6)))
                .into(s_imgBg); //模糊效果
        s_s_img.setImageBitmap(songList.get(index).getBitmap());
        ///  s_s_img.setImageBitmap(songList.get(0).getBitmap());
        Glide.with(MainActivity.this).load(songList.get(index).getBitmap())
                .apply(bitmapTransform(new CircleCrop()))
                .into(s_img);//圆角

    }

    private void setBackground(int i){

        Bitmap bitmap=songList.get(i).getBitmap();

        s_Title.setText(songList.get(i).getSong());
        s_singerName.setText(songList.get(i).getSinger());
        s_s_img.setImageBitmap(songList.get(i).getBitmap());



    }

    private void funByPlay(Uri uri){
        try{
            // 如果mediaPlayer为空  创建player

            if(mediaPlayer==null){
                setBackground(indexOfSong);
                mediaPlayer = new MediaPlayer();
                //吧数据源设置给player
                mediaPlayer.setDataSource(MainActivity.this, uri);
                //准备播放  加个缓冲时间
                mediaPlayer.prepare();
                // 开始播放
                mediaPlayer.start();
            }else{
                if (!mediaPlayer.isPlaying()) {
                    //播放器进行重置
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(MainActivity.this,uri);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    mediaPlayer.seekTo(sb_main_progress.getProgress());
                }    //   如果player不为空，而且处于播放音乐状态
                else if (mediaPlayer.isPlaying()) {

                    //先停掉播放
                    mediaPlayer.stop();

                    int currentPosition=mediaPlayer.getCurrentPosition();
                    sb_main_progress.setProgress(currentPosition);

                }
            }

        }catch(IOException e){
            e.printStackTrace();
        }


    }

    private void changeMusic(Uri uri){
        try{
            //播放器进行重置
            mediaPlayer.reset();
            mediaPlayer.setDataSource(MainActivity.this,uri);
            mediaPlayer.prepare();
            int currentPosition=mediaPlayer.getCurrentPosition();
            sb_main_progress.setProgress(currentPosition);
            mediaPlayer.start();
        }catch(IOException e){
            e.printStackTrace();
        }
    }


    /*
     * 检查是否具备了文件读写权限
     *
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkPermissions() {
        //检查权限是否获取
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            int result = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            {
                if (result == PackageManager.PERMISSION_GRANTED) {
                    initData();
                } else {
                    //申请权限方法 参数1：权限数组 参数2：权限代号(任意)
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
                }
            }
        }
        else
        {
            initData();
        }
    }

    //请求权限的结果
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //判断requestcode是否是我们请求的权限且权限结果是否大于0
        if (requestCode == 100 && grantResults.length > 0) {
            //取出结果
            int result = grantResults[0];
            //判断结果是否获取成功
            if (result == PackageManager.PERMISSION_GRANTED) {
                initData();
            }
        } else {
            Toast.makeText(this, "权限没有获取成功", Toast.LENGTH_SHORT).show();
            return;
        }
    }



    /**
     * 获取view控件
     */
    private void initView() {
        View view= this.getLayoutInflater().inflate((R.layout.activity_main), null);
        sl_list = (ListView) findViewById(R.id.sl_list);
        s_Title = (TextView) findViewById(R.id.s_Title);
        s_singerName = (TextView) findViewById(R.id.s_singerName);
        s_last = (ImageView) findViewById(R.id.s_last);
        s_play = (ImageView) findViewById(R.id.s_play);
        s_next = (ImageView) findViewById(R.id.s_next);
        s_list = (RelativeLayout) findViewById(R.id.s_list);
        s_s_img = (ImageView) findViewById(R.id.s_s_img);
        tv_total_time = (TextView) findViewById(R.id.tv_total_time);
        tv_current_time = (TextView) findViewById(R.id.tv_current_time);
        sb_main_progress = (SeekBar) findViewById(R.id.sb_main_progress);
        search=findViewById(R.id.search);
        other=findViewById(R.id.other);


    }



    /**
     * 获取手机上的音乐文件并放到list集合中去
     */
    private void initData() {
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
                Bitmap bitmap = BitmapUtils.getArtwork(MainActivity.this, songId, albumId, true);
                Bean bean = new Bean(title, singer, R.mipmap.ic_launcher, path, bitmap);

                songList.add(bean);
            }
        }

    }

    /**
     * 设置状态栏颜色为透明色，并保留状态栏电池 信号等图标
     */
    private void makeStatusBarTransParent() {
        //当版本号高于21   需要通过java代码来设置状态栏透明
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            //清除flag
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //添加flag
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            // 设置透明色
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            //给decorView设置uiflag
            getWindow().getDecorView().setSystemUiVisibility(uiFlags);
        }
    }

}