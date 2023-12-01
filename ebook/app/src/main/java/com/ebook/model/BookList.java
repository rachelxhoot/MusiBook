package com.ebook.model;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class BookList {
    public static final String TEXT = "text";
    public static final String IMAGE = "image";
    private static BookList sBookList;

    //AssetMnager assets目录下存放的原始资源文件
    private AssetManager mAssetManager;
    private List<Book> mBookList;//书籍list
    //asset中的封面名字清单 asset中的text名字清单
    private String[] mAssetsImageList;
    private String[] mAssetsTextList;

    //静态方法来实例化对象
    public static BookList newInstance(Context context) {
        if (sBookList == null) {
            sBookList = new BookList(context);
        }
        return sBookList;
    }

    private BookList(Context context) {
        mAssetManager = context.getAssets();
        //加载assets中的文件 循环得到每一本书的书名 封面 文本 构成书 并依次添加至booklist
        loadAssetsFiles();
    }

    //加载assets中的文件 循环得到每一本书的书名 封面 文本 构成书 并依次添加至booklist
    private void loadAssetsFiles() {
        mBookList = new ArrayList<>();
        //获取image、text中的文件名list
        try {
            mAssetsImageList = mAssetManager.list(IMAGE);
            mAssetsTextList = mAssetManager.list(TEXT);
        } catch (IOException e) {
            //捕获异常处理
            e.printStackTrace();
        }

        //以txt文件为基准 循环每一个txt 获取相应的书名 封面 文本
        //获得的书名 封面 文本作为参数构成一本书
        for (int i = 0; i < mAssetsTextList.length; i++) {
            //书名
            String[] nameSplit = mAssetsTextList[i].split("_");
            String nameSecond = nameSplit[nameSplit.length - 1];
            String bookTitle = nameSecond.replace(".txt", "");

            //封面
            String imagePath = IMAGE + "/" + mAssetsImageList[i];
            Bitmap bookCover = loadImage(imagePath);

            //文本
            String textPath = TEXT + "/" + mAssetsTextList[i];
            String bodyText = loadText(textPath);

            //获得的书名 封面 文本作为参数构成一本书
            Book book = new Book(bookTitle, bookCover, bodyText);
            mBookList.add(book);

        }

    }


    //从assets中读取文本
    private String loadText(String path) {
        InputStream in = null;
        // BufferedReader类从字符输入流中读取文本并缓冲字符
        BufferedReader reader = null;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            in = mAssetManager.open(path);
            reader = new BufferedReader(new InputStreamReader(in));

            String line = "";
            //bufferreader.readline() 每次读一行
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return stringBuilder.toString();

    }

    //从assets中读取图片
    private Bitmap loadImage(String path) {
        Bitmap image = null;
        InputStream in = null;
        try {
            in = mAssetManager.open(path);
            //解码图片
            image = BitmapFactory.decodeStream(in);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return image;
    }

    public List<Book> getBookList() {
        return mBookList;
    }
}
