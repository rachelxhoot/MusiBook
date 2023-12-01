package com.ebook.view.Popup;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

public abstract class Popup extends PopupWindow {

    protected Context mContext;
    protected View mConvertView;

    protected abstract View createConvertView();


    public Popup(Context context) {
        super(context);
        mContext = context;
        mConvertView = createConvertView();
        setContentView(mConvertView);

        //获取屏幕的宽高
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);

        setSize(metrics.widthPixels, metrics.heightPixels);

        setFocusable(true);
        setTouchable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new BitmapDrawable());

        //点击外部消失window
        setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    dismiss();
                    return true;
                }
                return false;
            }
        });

    }

     //设置popupWindow默认宽高
    protected void setSize(int width, int height) {
        setWidth(width);
        setHeight((int) (height * 0.55));

    }


}
