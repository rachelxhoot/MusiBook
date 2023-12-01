package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class setting_bg extends AppCompatActivity {

    private ImageView iv;
    private ImageView[] iv1=new ImageView[5];
    private int ii=0;
    private static final String TAG = "setting_bg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_bg);

        initview();

        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ii!=0){
                    Intent intent=new Intent();
                    intent.putExtra("index",ii);
                    setResult(RESULT_OK,intent);
                    finish();
                }
            }
        });
        Myclick click = new Myclick();
        for(int i=0;i<4;i++){
            iv1[i].setOnClickListener(click);
        }

    }

    private class Myclick implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            int temp=v.getId();
            Drawable drawable1= ResourcesCompat.getDrawable(getResources(), R.drawable.bg_setting, null);
            Drawable drawable2= ResourcesCompat.getDrawable(getResources(), R.drawable.bg_setting2, null);

            if(temp==R.id.bg1) {
                iv1[0].setBackground(drawable1);
                ii=1;
            }
            else iv1[0].setBackground(drawable2);
            if(temp==R.id.bg2) {
                ii=2;
                iv1[1].setBackground(drawable1);
            }
            else iv1[1].setBackground(drawable2);
            if(temp==R.id.bg3) {
                ii=3;
                iv1[2].setBackground(drawable1);
            }
            else iv1[2].setBackground(drawable2);
            if(temp==R.id.bg4) {
                ii=4;
                iv1[3].setBackground(drawable1);
            }
            else iv1[3].setBackground(drawable2);





        }
    }

    private void initview(){
        iv=findViewById(R.id.s_b);
        iv1[0]=findViewById(R.id.bg1);
        iv1[1]=findViewById(R.id.bg2);
        iv1[2]=findViewById(R.id.bg3);
        iv1[3]=findViewById(R.id.bg4);
    }


}