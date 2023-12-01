package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class setting extends AppCompatActivity {

    private RelativeLayout setting1;
    private RelativeLayout setting2;
    private ImageView setting_back;
    private static final String TAG = "setting";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        //initview()
        setting1=findViewById(R.id.setting1);
        setting2=findViewById(R.id.setting2);
        setting_back=findViewById(R.id.setting_back);



        setting1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(setting.this,setting_bg.class);
                startActivityForResult(intent,1);
            }
        });


        setting2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(setting.this,us.class);
                startActivity(intent);
            }
        });

        setting_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.putExtra("index",index);
                setResult(RESULT_OK,intent);
                finish();
            }
        });



    }

    private int index;

    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "return data: ");
        if(data!=null){
            int RETURN_DATA=data.getIntExtra("index",0);
            Log.d(TAG, "return data: "+RETURN_DATA);
            switch (requestCode){
                case 1:
                    if(resultCode==RESULT_OK){
                        index=RETURN_DATA;
                        Log.d(TAG, "onActivityResult: "+index);
                    }

                    break;
            }
        }



    }
}