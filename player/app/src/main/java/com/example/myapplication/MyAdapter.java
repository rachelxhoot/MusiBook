package com.example.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class MyAdapter extends BaseAdapter {
    private Context context;
    private List<Bean> songList;
    private boolean pd=false;

    public MyAdapter() {
    }

    public MyAdapter(Context context, List<Bean> songList) {
        this.context = context;
        this.songList = songList;
    }

    @Override
    public int getCount() {
        return songList.size();
    }

    @Override
    public Object getItem(int i) {
        return songList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view== null) {
            //获取view对象
            view = View.inflate(context, R.layout.item, null);
            holder=new ViewHolder();
            // 关联控件
            holder.image=view.findViewById(R.id.image);
            holder.tv_singer=view.findViewById(R.id.tv_singer);
            holder.tv_song=view.findViewById(R.id.tv_song);
            //view和holder进行关联
            view.setTag(holder);

        }else{
            holder= (ViewHolder) view.getTag();
        }

        holder.tv_song.setText(songList.get(i).getSong());
        holder.tv_singer.setText(songList.get(i).getSinger());

        Bitmap bitmap = songList.get(i).getBitmap();
        if (bitmap == null) {
            holder.image.setImageResource(R.mipmap.ic_launcher);
        }else{
            holder.image.setImageBitmap(bitmap);
        }


        //做判断如果传递创建view的时候 传递过来的index和当前的索引i相同我们就认为是选中状态

            if (itemIndex == i) {
                view.setBackgroundColor(Color.parseColor("#88f6f5ec"));
            }else {
                view.setBackgroundColor(Color.TRANSPARENT);
            }




        return view;
    }

    private int itemIndex;
    /**
     * 设置listviewitem的选中状态
     * @param i  传递的是索引
     */
    public void setSelectItem(int i) {
        itemIndex=i;
        notifyDataSetChanged();
    }




    public static class ViewHolder {
        public View rootView;
        public ImageView image;
        public TextView tv_song;
        public TextView tv_singer;

        public ViewHolder() {
        }

        public ViewHolder(View rootView) {
            this.rootView = rootView;
            this.image = (ImageView) rootView.findViewById(R.id.image);
            this.tv_song = (TextView) rootView.findViewById(R.id.tv_song);
            this.tv_singer = (TextView) rootView.findViewById(R.id.tv_singer);
        }

    }
}

