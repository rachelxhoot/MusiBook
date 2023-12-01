package com.ebook.util.ReadPage;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.ebook.model.Book;
import com.ebook.model.BookList;
import com.ebook.util.SaveHelper;
import com.ebook.view.Popup.Font;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ReadPage {
    private Context mContext;

    //app屏幕的大小
    private int mWidth;
    private int mHeight;
    private int marginWidth;
    private int marginHeight;

    private float mVisibleWidth;
    private float mVisibleHeight;

    private int mBookId;

    //行高 一页能容纳的行数
    private float mLineHeight;
    private int mLineCount;

    //段落list 目录list 目录索引
    private List<String> mParaList;
    private int mParaListSize;

    private List<String> mContents;
    private List<Integer> mContentParaIndex;


    private List<String> mPageLines = new ArrayList<>();
    private String mCurContent;
    //paint用于画几何图形 文本和bitmap
    private Paint mPaint;

    //背景颜色与文本颜色
    private int[] mBgColors;
    private int[] mTextColors;

    //使用typeface设置字体
    private List<Typeface> mTypefaceList = new ArrayList<>();

    private PaintInfo mPaintInfo;
    private ReadInfo mReadInfo;
    private String percentStr;


    public ReadPage(Context context, int bookId) {
        mContext = context;
        mBookId = bookId;

        //获取屏幕的宽高
        calWidthAndHeight();
        //得到字体清单mTypefaceList
        getFontFromAssets();

        initDatas();
    }

    //获取屏幕的宽高
    private void calWidthAndHeight() {
        //windowmanager用于窗口管理
        WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        //提供关于屏幕显示的通用信息，用于获取屏幕大小
        DisplayMetrics metrics = new DisplayMetrics();
        //获取app屏幕信息
        windowManager.getDefaultDisplay().getMetrics(metrics);

        mWidth = metrics.widthPixels;
        mHeight = metrics.heightPixels;

    }

    //得到字体清单mTypefaceList
    private void getFontFromAssets() {
        //初始化typefacelist
        mTypefaceList.add(Typeface.DEFAULT);
        //字体名字清单
        String[] fontNameList = null;
        AssetManager assetManager = mContext.getAssets();
        try {
            fontNameList = assetManager.list(Font.FONTS);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < fontNameList.length; i++) {

            String fontPath = Font.FONTS + "/" + fontNameList[i];
            //根据路径得到Typeface字体
            Typeface typeface = Typeface.createFromAsset(assetManager, fontPath);//根据路径得到Typeface
            mTypefaceList.add(typeface);
        }

    }

    //初始化页面数据
    private void initDatas() {

        Book book = BookList.newInstance(mContext).getBookList().get(mBookId);

        //得到以段落为单位保存的文本list
        mParaList = book.getParagraphList();
        mParaListSize = mParaList.size();
        //得到目录以及目录索引list
        mContents = book.getBookContents();
        mContentParaIndex = book.getContentParaIndexs();

        //由屏幕大小得到边缘空出的大小，从而计算出可读界面大小
        marginWidth = (int) (mWidth / 30f);
        marginHeight = (int) (mHeight / 60f);
        mVisibleWidth = mWidth - marginWidth * 2;
        mVisibleHeight = mHeight - marginHeight * 2;

        mBgColors = new int[]{
                0xffe7dcbe,
        };

        mTextColors = new int[]{
                0x8A000000,
        };


        //savehelper 得到之前的阅读设置
        PaintInfo paintInfo = SaveHelper.getObject(mContext, SaveHelper.PAINT_INFO);
        if (paintInfo != null)
            mPaintInfo = paintInfo;
        else
            mPaintInfo = new PaintInfo();

        //得到行高，从而得出一页可以容纳的行数
        mLineHeight = mPaintInfo.textSize * 1.5f;
        mLineCount = (int) (mVisibleHeight / mLineHeight) - 1;

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextAlign(Paint.Align.LEFT);

        mPaint.setColor(mPaintInfo.textColor);
        mPaint.setTextSize(mPaintInfo.textSize);
        mPaint.setTypeface(mTypefaceList.get(mPaintInfo.typeIndex));

        ReadInfo info = SaveHelper.getObject(mContext, mBookId + SaveHelper.DRAW_INFO);
        if (info != null)
            mReadInfo = info;
        else
            mReadInfo = new ReadInfo();
    }

    //向后翻页
    public Bitmap drawNextPage(float powerPercent) {
        //上一次阅读是向后阅读
        if (!mReadInfo.isLastNext) {
            pageDown();
            mReadInfo.isLastNext = true;
        }
        Bitmap bitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(mPaintInfo.bgColor);
        //下一页
        mPageLines = getNextPageLines();
        //是否到最后一页
        if (mPageLines.size() == 0 || mPageLines == null) {
            return null;
        }
        float y = mPaintInfo.textSize;
        for (String strLine : mPageLines) {
            y += mLineHeight;
            canvas.drawText(strLine, marginWidth, y, mPaint);
        }
        //显示在底部信息
        drawInfo(canvas, powerPercent);
        return bitmap;
    }

    //pre
    public Bitmap drawPrePage(float powerPercent) {
        if (mReadInfo.isLastNext) {
            pageUp();

            mReadInfo.isLastNext = false;
        }
        Bitmap bitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(mPaintInfo.bgColor);
        //下一页
        mPageLines = getPrePageLines();
        //第一页
        if (mPageLines.size() == 0 || mPageLines == null) {
            return null;
        }
        float y = mPaintInfo.textSize;

        for (String strLine : mPageLines) {
            y += mLineHeight;
            canvas.drawText(strLine, marginWidth, y, mPaint);
        }
        //显示信息
        drawInfo(canvas, powerPercent);

        return bitmap;
    }

    public List<Bitmap> drawCurTwoPages(float powerPercent) {

        setIndexToCurStart();

        mPaint.setColor(mPaintInfo.textColor);
        mPaint.setTextSize(mPaintInfo.textSize);
        mPaint.setTypeface(mTypefaceList.get(mPaintInfo.typeIndex));

        List<Bitmap> bitmaps = new ArrayList<>();
        if (mReadInfo.isLastNext) {
            bitmaps.add(drawNextPage(powerPercent));
            bitmaps.add(drawNextPage(powerPercent));
        } else {
            bitmaps.add(drawPrePage(powerPercent));
            bitmaps.add(0, drawPrePage(powerPercent));
        }

        return bitmaps;
    }

    private void setIndexToCurStart() {

        if (mReadInfo.isLastNext) {
            pageUp();
            mReadInfo.nextParaIndex += 1;

            if (!mReadInfo.isPreRes)
                return;

            String string = mParaList.get(mReadInfo.nextParaIndex);

            while (string.length() > 0) {
                //一行显示多少字
                int size = mPaint.breakText(string, true, mVisibleWidth, null);

                mReadInfo.nextResLines.add(string.substring(0, size));

                string = string.substring(size);

            }

            mReadInfo.nextResLines.clear();
            mReadInfo.isNextRes = true;
            mReadInfo.nextParaIndex += 1;

            mReadInfo.preResLines.clear();
            mReadInfo.isPreRes = false;


        } else {
            pageDown();
            mReadInfo.nextParaIndex -= 1;

            if (!mReadInfo.isNextRes)
                return;

            String string = mParaList.get(mReadInfo.nextParaIndex);

            while (string.length() > 0) {
                //检测一行能够显示多少字
                int size = mPaint.breakText(string, true, mVisibleWidth, null);

                mReadInfo.preResLines.add(string.substring(0, size));

                string = string.substring(size);

            }

            mReadInfo.preResLines.removeAll(mReadInfo.nextResLines);
            mReadInfo.isPreRes = true;
            mReadInfo.nextParaIndex -= 1;

            mReadInfo.nextResLines.removeAll(mReadInfo.preResLines);
            mReadInfo.isNextRes = false;


        }


    }

    //page对应的目录
    private String findContent(int paraIndex) {
        for (int i = 0; i < mContentParaIndex.size() - 1; i++) {
            if (paraIndex >= mContentParaIndex.get(i) && paraIndex < mContentParaIndex.get(i + 1)) {
                if (i == 0)
                    i = 1;

                return mContents.get(i);
            }

        }

        return mContents.get(mContentParaIndex.size() - 1);

    }

    private void drawInfo(Canvas canvas, float powerPercent) {

        Paint infoPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        infoPaint.setTextAlign(Paint.Align.LEFT);
        infoPaint.setTextSize(32);
        infoPaint.setColor(0xff5c5c5c);

        float offsetY = mHeight - marginHeight;

        //当前目录
        canvas.drawText(mCurContent, marginWidth, marginHeight, infoPaint);

        //阅读进度
        float percent = mReadInfo.nextParaIndex * 1.0f / mParaListSize;
        DecimalFormat df = new DecimalFormat("#0.00");
        percentStr = df.format(percent * 100) + "%";
        canvas.drawText(percentStr, marginWidth, offsetY, infoPaint);

        //系统时间
        Time time = new Time();
        time.setToNow();
        int hour = time.hour;
        int minute = time.minute;
        String timeStr = "";
        if (minute < 10) {
            timeStr = hour + ":0" + minute;
        } else {
            timeStr = hour + ":" + minute;
        }
        canvas.drawText(timeStr, mWidth - 7f * marginWidth, offsetY, infoPaint);

        //电池电量
        infoPaint.reset();
        //设置画笔样式为仅描边
        infoPaint.setStyle(Paint.Style.STROKE);
        infoPaint.setStrokeWidth(1);
        infoPaint.setColor(0xff5c5c5c);
        float left = mWidth - 3.8f * marginWidth;
        float right = mWidth - 2.2f * marginWidth;
        float height = 0.8f * marginHeight;
        //电池左边外框
        RectF rectF = new RectF(left, offsetY - height, right, offsetY);
        canvas.drawRect(rectF, infoPaint);
        //电池左边电量
        infoPaint.setStyle(Paint.Style.FILL);
        float width = (right - left) * powerPercent;
        rectF = new RectF(left + 1.5f, offsetY - height + 1.5f, left + width - 1.5f, offsetY - 1.5f);
        canvas.drawRect(rectF, infoPaint);
        //电池右边
        rectF = new RectF(right, offsetY - 0.7f * height, right + 0.2f * marginWidth, offsetY - 0.3f * height);
        canvas.drawRect(rectF, infoPaint);
    }

    //得到下一页的各行内容
    private List<String> getNextPageLines() {

        String string = "";

        List<String> lines = new ArrayList<>();
        //上次往后读有剩余字符串
        if (mReadInfo.isNextRes) {
            lines.addAll(mReadInfo.nextResLines);

            mReadInfo.nextResLines.clear();

            mReadInfo.isNextRes = false;
        }
        //若已经读完
        if (mReadInfo.nextParaIndex >= mParaListSize) {
            return lines;
        }
        //更新目前的目录
        mCurContent = findContent(mReadInfo.nextParaIndex);
        //上次翻页剩余的行数小于页面行数并且没有读完，更新章节索引添加内容至各行直至填满
        while (lines.size() < mLineCount && mReadInfo.nextParaIndex < mParaListSize) {

            string = mParaList.get(mReadInfo.nextParaIndex);

            mReadInfo.nextParaIndex++;

            while (string.length() > 0) {

                int size = mPaint.breakText(string, true, mVisibleWidth, null);

                lines.add(string.substring(0, size));

                string = string.substring(size);

            }

        }
        //删除页面最后一章多余的行数
        while (lines.size() > mLineCount) {
            mReadInfo.isNextRes = true;

            int end = lines.size() - 1;

            mReadInfo.nextResLines.add(0, lines.get(end));

            lines.remove(end);
        }

        return lines;
    }


    private List<String> getPrePageLines() {

        String string = "";
        List<String> lines = new ArrayList<>();

        if (mReadInfo.isPreRes) {

            lines.addAll(mReadInfo.preResLines);

            mReadInfo.preResLines.clear();

            mReadInfo.isPreRes = false;
        }

        if (mReadInfo.nextParaIndex < 0) {

            return lines;
        }

        mCurContent = findContent(mReadInfo.nextParaIndex);

        while (lines.size() < mLineCount && mReadInfo.nextParaIndex >= 0) {

            List<String> paraLines = new ArrayList<>();

            string = mParaList.get(mReadInfo.nextParaIndex);

            mReadInfo.nextParaIndex--;

            while (string.length() > 0) {

                int size = mPaint.breakText(string, true, mVisibleWidth, null);

                paraLines.add(string.substring(0, size));

                string = string.substring(size);

            }

            lines.addAll(0, paraLines);

        }

        while (lines.size() > mLineCount) {
            mReadInfo.isPreRes = true;

            mReadInfo.preResLines.add(lines.get(0));

            lines.remove(0);
        }

        return lines;

    }

    //向后*2
    private void pageDown() {
        //更新储存的下个段落的索引
        mReadInfo.nextParaIndex += 1;

        String string = "";

        List<String> lines = new ArrayList<>();

        //上一次向前阅读剩余的行数
        int totalLines = 2 * mLineCount + mReadInfo.preResLines.size();

        //充值readinfo
        reset();

        //当下个章节索引小于总章节数时
        while (lines.size() < totalLines && mReadInfo.nextParaIndex < mParaListSize) {

            //储存下一个章节 并且更新下一个章节的索引
            string = mParaList.get(mReadInfo.nextParaIndex);

            mReadInfo.nextParaIndex++;

            //将该章节按行分割 逐行加入lines
            while (string.length() > 0) {

                //检测一行能够显示多少字
                int size = mPaint.breakText(string, true, mVisibleWidth, null);

                lines.add(string.substring(0, size));

                string = string.substring(size);

            }

        }

        //该章节在本页之后仍有剩余字符串
        while (lines.size() > totalLines) {
            mReadInfo.isNextRes = true;

            //得到上一次向后读剩余的行数
            int end = lines.size() - 1;

            mReadInfo.nextResLines.add(0, lines.get(end));

            lines.remove(end);
        }


    }

    //向前*2
    private void pageUp() {
        mReadInfo.nextParaIndex -= 1;

        String string = "";

        List<String> lines = new ArrayList<>();

        int totalLines = 2 * mLineCount + mReadInfo.nextResLines.size();

        reset();

        while (lines.size() < totalLines && mReadInfo.nextParaIndex >= 0) {

            List<String> paraLines = new ArrayList<>();

            string = mParaList.get(mReadInfo.nextParaIndex);

            mReadInfo.nextParaIndex--;

            while (string.length() > 0) {

                int size = mPaint.breakText(string, true, mVisibleWidth, null);

                paraLines.add(string.substring(0, size));

                string = string.substring(size);

            }

            lines.addAll(0, paraLines);

        }

        while (lines.size() > totalLines) {
            mReadInfo.isPreRes = true;

            mReadInfo.preResLines.add(lines.get(0));

            lines.remove(0);
        }

    }

    private void reset() {
        mReadInfo.preResLines.clear();
        mReadInfo.isPreRes = false;

        mReadInfo.nextResLines.clear();
        mReadInfo.isNextRes = false;
    }



    public List<Bitmap> updatePagesByContent(int nextParaIndex, float powerPercent) {
        mReadInfo.nextParaIndex = nextParaIndex;

        if (mReadInfo.nextParaIndex == 1)
            mReadInfo.nextParaIndex = 0;
        reset();

        mReadInfo.isLastNext = true;
        List<Bitmap> bitmaps = new ArrayList<>();
        bitmaps.add(drawNextPage(powerPercent));
        bitmaps.add(drawNextPage(powerPercent));

        return bitmaps;
    }

    public List<Bitmap> updateTypeface(int typeIndex, float powerPercent) {
        mPaintInfo.typeIndex = typeIndex;
        return drawCurTwoPages(powerPercent);
    }

    public List<Bitmap> updateTextColor(int textColor, float powerPercent) {
        mPaintInfo.textColor = textColor;
        return drawCurTwoPages(powerPercent);
    }

    public ReadInfo getReadInfo() {
        return mReadInfo;
    }
    public PaintInfo getPaintInfo() {
        return mPaintInfo;
    }
}
