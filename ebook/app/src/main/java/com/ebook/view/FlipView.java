package com.ebook.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.ebook.util.SaveHelper;

import java.util.List;

public class FlipView extends View {

    private Context mContext;
    private List<Bitmap> mBitmapList;

    // 宽高
    private int mViewWidth, mViewHeight;
    //对角线长度
    float mDiagonalLength;
    //触摸点
    private PointF mTouch;

    private PointF mCorner;
    //手指落下时的触摸点
    private PointF mLastDownPoint;

    private boolean isPrePageOver;
    private boolean isDrawOnMove;
    private boolean isFlipNext;
    private int mPrePage = 0; //前一页索引
    private int mNextPage = 1;//后一页索引

    private OnPageFlippedListener mListener;

    public void setOnPageFlippedListener(OnPageFlippedListener listener) {

        mListener = listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mTouch.x = event.getX();
        mTouch.y = event.getY();

        //判断是翻页还是点击事件
        float width = mDiagonalLength / 100f;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastDownPoint.x = mTouch.x;//保存下手指落下时刻的触摸点
                mLastDownPoint.y = mTouch.y;

                isDrawOnMove = false;
                break;

            case MotionEvent.ACTION_MOVE:

                if (!isDrawOnMove) {
                    //翻前一页
                    if (mTouch.x - mLastDownPoint.x > width) {
                        isFlipNext = false;
                        if (!isPrePageOver) {
                            //回调获得前一页page
                            List<Bitmap> temp = null;
                            if (mListener != null)
                                temp = mListener.onPrePageFlipped();
                            if (temp == null) {
                                Toast.makeText(mContext, "已经是第一页了", Toast.LENGTH_SHORT).show();
                                return true;
                            }
                            mBitmapList = temp;
                            isDrawOnMove = true;
                        } else {
                            isPrePageOver = false;
                            isDrawOnMove = true;
                        }
                    }
                    //翻下一页
                    if (mTouch.x - mLastDownPoint.x < -width) {
                        isFlipNext = true;

                        if (isPrePageOver) {
                            //回调获得后一页page

                            List<Bitmap> temp = null;

                            if (mListener != null)
                                temp = mListener.onNextPageFlipped();

                            if (temp == null) {
                                Toast.makeText(mContext, "已经是最后一页了", Toast.LENGTH_SHORT).show();
                                return true;
                            }

                            mBitmapList = temp;
                            isPrePageOver = false;
                            isDrawOnMove = true;

                        } else {

                            isDrawOnMove = true;
                        }
                    }


                } else {

                    if (mListener != null)
                        mListener.onFlipStarted();

                    invalidate();
                }
                break;

            case MotionEvent.ACTION_UP:

                float dis = (float) Math.hypot(mTouch.x - mLastDownPoint.x, mTouch.y - mLastDownPoint.y);
                if (dis < width) {   //没有触发翻页效果，认为是点击事件
                    //强制设置touch点坐标，防止因设置visibility而重绘导致的画面突变
                    mTouch.x = mCorner.x;
                    mTouch.y = mCorner.y;

                    if (mListener != null)
                        mListener.onFoldViewClicked();

                    return true;
                }

                if (!isDrawOnMove) {
                    return true;
                }
                break;

        }

        return true;
    }


    public interface OnPageFlippedListener {

        List<Bitmap> onNextPageFlipped();

        List<Bitmap> onPrePageFlipped();

        void onFlipStarted();

        void onFoldViewClicked();
    }

    public FlipView(Context context) {
        this(context, null);
    }

    public FlipView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initObjects();
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mViewWidth = w;
        mViewHeight = h;
        initDatas();
    }


    @Override
    protected void onDraw(Canvas canvas) {

        if (mBitmapList == null || mBitmapList.size() == 0) {
            return;
        }
        //首次进入绘制前一页
        if (!isPrePageOver && mTouch.x == 0 && mTouch.y == 0) {
            canvas.drawBitmap(mBitmapList.get(mPrePage), 0, 0, null);
            return;
        }


        if (!isPrePageOver) {
            flipNoEffect(canvas);


        } else {
            //前一页已经完全翻完，直接绘制下一页区域
            canvas.save();
            canvas.drawBitmap(mBitmapList.get(mNextPage), 0, 0, null);
            canvas.restore();

        }

    }


    private void flipNoEffect(Canvas canvas) {
        if (isFlipNext) {
            canvas.drawBitmap(mBitmapList.get(mNextPage), 0, 0, null);
            isPrePageOver = true;

        } else {
            canvas.drawBitmap(mBitmapList.get(mPrePage), 0, 0, null);
        }
    }



    //更新当前page
    public void updateBitmapList(List<Bitmap> bitmapList) {

        mBitmapList = bitmapList;
        invalidate();
    }


    public void setPageByContent(List<Bitmap> bitmapList) {

        mBitmapList = bitmapList;

        isPrePageOver = false;

        mTouch.x = 0;
        mTouch.y = 0;

        invalidate();
    }

    private void initDatas() {
        //控件对角线长度
        mDiagonalLength = (float) Math.hypot(mViewWidth, mViewHeight);
        //计算自滑界限位置
        isPrePageOver = SaveHelper.getBoolean(mContext, SaveHelper.IS_PRE_PAGE_OVER);

    }


    private void initObjects() {

        mTouch = new PointF();
        mCorner = new PointF();
        mLastDownPoint = new PointF();

    }

    public void setPrePageOver(boolean prePageOver) {
        isPrePageOver = prePageOver;
    }

    public boolean isPrePageOver() {
        return isPrePageOver;
    }
    }
