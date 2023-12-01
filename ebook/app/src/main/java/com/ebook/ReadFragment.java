package com.ebook;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;


import com.ebook.model.Book;
import com.ebook.model.BookList;
import com.ebook.util.ReadPage.ReadPage;
import com.ebook.util.ReadPage.ReadInfo;
import com.ebook.util.SaveHelper;
import com.ebook.view.FlipView;
import com.ebook.view.Popup.Content;
import com.ebook.view.Popup.Font;

import java.util.ArrayList;
import java.util.List;

//implements继承接口
// extends继承对象
public class ReadFragment extends Fragment implements View.OnClickListener {
    public static final String ARG_FLIP_BOOK_ID = "ARG_FLIP_BOOK_ID ";
    public static final int TEXT_SIZE_DELTA = 50;

    private Context mContext;
    private int mBookId;
    private Book mBook;
    private ReadPage mReadPage;

    private Bitmap mPrePage;
    private Bitmap mNextPage;
    private List<Bitmap> mPageList = new ArrayList<>();

    private int[] mBgColors;

    private FlipView mFlipView;

    private LinearLayout mBottomBar;
    private Button[] mBottomBtns;

    private Content mContent;
    private Font mFont;

    private boolean isBottomBarShow = true;
    //是否是第一次
    private boolean isFirstRead = true;

    private float mBackgroundAlpha = 1.0f;
    //电量百分比
    private float mPowerPercent;

    private BatteryPowerReceiver mBatteryReceiver;//电池电量广播接收者

    public static ReadFragment newInstance(int bookId) {

        Bundle args = new Bundle();
        args.putInt(ARG_FLIP_BOOK_ID, bookId);
        ReadFragment fragment = new ReadFragment();
        fragment.setArguments(args);
        return fragment;
    }

    //数据封装
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            WindowManager.LayoutParams layoutParams = getActivity().getWindow().getAttributes();
            layoutParams.alpha = (Float) msg.obj;
            getActivity().getWindow().setAttributes(layoutParams);

        }
    };


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDatas();
    }


    @Nullable
    @Override
    //inflater.inflate加载布局文件返回
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.reading_layout, container, false);
        initViews(v);
        initEvents();

        return v;
    }

    //界面用户可见
    @Override
    public void onStart() {
        super.onStart();

        //电量变化广播接收者
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        mBatteryReceiver = new BatteryPowerReceiver();
        mContext.registerReceiver(mBatteryReceiver, filter);

    }


    @Override
    public void onStop() {
        super.onStop();
        //取消广播接收者
        mContext.unregisterReceiver(mBatteryReceiver);

        //FlipView
        SaveHelper.save(mContext, SaveHelper.IS_PRE_PAGE_OVER, mFlipView.isPrePageOver());

        SaveHelper.saveObject(mContext, mBookId + SaveHelper.DRAW_INFO, mReadPage.getReadInfo());
        SaveHelper.saveObject(mContext, SaveHelper.PAINT_INFO, mReadPage.getPaintInfo());


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            //目录设置
            case R.id.button_content:

                mContent.setAnimationStyle(R.style.pop_window_anim_style);
                mContent.showAsDropDown(mBottomBar, 0, -mContent.getHeight());

                lightOff();
                break;

            case R.id.button_font:

                mFont.setAnimationStyle(R.style.pop_window_anim_style);
                mFont.showAsDropDown(mBottomBar, 0, -mFont.getHeight());

                lightOff();

                break;
        }

    }



    private void initDatas() {
        mContext = getActivity();
        mBookId = getArguments().getInt(ARG_FLIP_BOOK_ID);
        mBook = BookList.newInstance(mContext).getBookList().get(mBookId);
        mReadPage = new ReadPage(mContext, mBookId);

        mBgColors = new int[]{
                0xffe7dcbe,
        };

    }

    private void initEvents() {

        if (isBottomBarShow)
            hideBottomBar();

        int theme = SaveHelper.getInt(mContext, SaveHelper.THEME);
        setTheme(theme);

        mFlipView.setOnPageFlippedListener(new FlipView.OnPageFlippedListener() {
            @Override
            public List<Bitmap> onNextPageFlipped() {
                //向后读一页

                mNextPage = mReadPage.drawNextPage(mPowerPercent);

                if (mNextPage == null)
                    return null;

                mPageList.remove(0);
                mPageList.add(mNextPage);

                return mPageList;
            }

            @Override
            public List<Bitmap> onPrePageFlipped() {
                //向前读一页
                mPrePage = mReadPage.drawPrePage(mPowerPercent);
                if (mPrePage == null)
                    return null;

                mPageList.remove(1);
                mPageList.add(0, mPrePage);

                return mPageList;
            }

            @Override
            public void onFlipStarted() {
                if (isBottomBarShow)
                    hideBottomBar();


            }

            @Override
            public void onFoldViewClicked() {
                if (isBottomBarShow)
                    hideBottomBar();
                else
                    showBottomBar();

            }


        });

        for (Button button : mBottomBtns) {
            button.setOnClickListener(this);
        }


        setPopupWindowListener();
    }


    private void setPopupWindowListener() {


        mContent.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                lightOn();
                hideBottomBar();

            }
        });

        mFont.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                lightOn();
                hideBottomBar();
            }
        });





        mContent.setOnContentClicked(new Content.OnContentSelectedListener() {

            @Override
            public void OnContentClicked(int paraIndex) {
                mPageList = mReadPage.updatePagesByContent(paraIndex, mPowerPercent);
                mFlipView.setPageByContent(mPageList);

                mContent.dismiss();
            }
        });

        mFont.setOnFontSelectedListener(new Font.OnFontSelectedListener() {

            @Override
            public void onTypefaceSelected(int typeIndex) {
                mPageList = mReadPage.updateTypeface(typeIndex, mPowerPercent);
                mFlipView.updateBitmapList(mPageList);
            }

            @Override
            public void onColorSelected(int color) {

                mPageList = mReadPage.updateTextColor(color, mPowerPercent);
                mFlipView.updateBitmapList(mPageList);


            }
        });





    }

    private void setTheme(int theme) {
        mBottomBar.setBackgroundColor(mBgColors[theme]);
        mContent.setBackgroundColor(mBgColors[theme]);
    }


    private void initViews(View v) {
        mFlipView = (FlipView) v.findViewById(R.id.flip_view);
        mBottomBar = (LinearLayout) v.findViewById(R.id.bottom_bar_layout);

        mBottomBtns = new Button[]{
                (Button) v.findViewById(R.id.button_content),
                (Button) v.findViewById(R.id.button_font),

        };

        mContent = new Content(mContext, mBook);
        mFont = new Font(mContext);

    }


    private void showBottomBar() {
        mBottomBar.setVisibility(View.VISIBLE);
        isBottomBarShow = true;
    }

    private void hideBottomBar() {
        mBottomBar.setVisibility(View.INVISIBLE);
        isBottomBarShow = false;
    }

    private void lightOff() {
        //背景内容变暗
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mBackgroundAlpha > 0.4f) {
                    try {
                        Thread.sleep(8);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mBackgroundAlpha -= 0.01f;
                    Message message = mHandler.obtainMessage();
                    message.obj = mBackgroundAlpha;
                    mHandler.sendMessage(message);

                }
            }
        }).start();
    }

    private void lightOn() {
        //背景内容逐渐变暗
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mBackgroundAlpha < 1.0f) {
                    try {
                        Thread.sleep(8);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mBackgroundAlpha += 0.01f;
                    Message message = mHandler.obtainMessage();
                    message.obj = mBackgroundAlpha;
                    mHandler.sendMessage(message);

                }
            }
        }).start();
    }


    private class BatteryPowerReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int current = intent.getExtras().getInt("level");
            int total = intent.getExtras().getInt("scale");
            mPowerPercent = current * 1f / total;


            if (isFirstRead) {

                ReadInfo readInfo = SaveHelper.getObject(mContext, mBookId + SaveHelper.DRAW_INFO);

                if (readInfo != null) {
                    mPageList = mReadPage.drawCurTwoPages(mPowerPercent);
                    mFlipView.updateBitmapList(mPageList);

                } else {
                    mPageList.add(mReadPage.drawNextPage(mPowerPercent));
                    mPageList.add(mReadPage.drawNextPage(mPowerPercent));
                    mFlipView.setPrePageOver(false);
                    mFlipView.updateBitmapList(mPageList);
                }

                isFirstRead = false;
            }


        }
    }


}


