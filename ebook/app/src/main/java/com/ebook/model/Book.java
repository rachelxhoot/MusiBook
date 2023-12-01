package com.ebook.model;

//bitmap包用于图片处理
import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//储存每一本书的内容
public class Book {
    private static final String TAG = "Book";

    //String 类来创建和操作字符串（书籍名称）
    private String mBookTitle;
    //Bitmap类获取图片（封面）
    private Bitmap mBookCover;
    //格式化文本，将文本以段落为单位保存mParagraphList中（该list中均为String类型）
    private List<String> mParagraphList;


    //目录集合(章)
    private List<String> mBookContents;
    //目录对应的在段落集合中的索引
    //Integer 类在对象中包装了一个基本类型 int 的值。
    // Integer 类对象包含一个 int 类型的字段。此外，该类提供了多个方法，能在 int 类型和 String 类型之间互相转换
    private List<Integer> mContentParaIndexs;

    //首行缩进
    private String mSpace = "\t\t\t\t\t\t";

    //格式化文本，将txt文本以段落为单位保存
    private void formatText(String text) {
        //初始化每个段落为空String
        String paragraph = "";

        //按段落切分文本 储存在paragraphs String数组 中 regex为正则表达式 后面表示两个大空格
        String[] paragraphs = text.split("\\s{2,}");

        //格式化每一个段落
        for (int i = 0; i < paragraphs.length; i++) {
            if (paragraphs[i].isEmpty()) {
                continue;
            }
            paragraph = "\n" + mSpace + paragraphs[i];

            mParagraphList.add(paragraph);

        }

    }


    //找到段落中的章节标题 并保存章节所在的段落列表索引
    private void findContents(List<String> paraList) {
        //字符串匹配模式
        //正则表达式 S：匹配非空白字符 s：匹配空白字符
        String patternString = "第\\S{2,4}\\s\\S{2,}";
        //将给定的正则表达式编译并赋值给Pattern类
        Pattern pattern = Pattern.compile(patternString);


        for (String para:paraList) {
            Matcher matcher = pattern.matcher(para);

            if (matcher.find()){

                int start = matcher.start();
                int end = matcher.end();
                //将匹配到的内容切割 存储至subString
                String subString = para.substring(start, end);

                mBookContents.add(subString);   //目录
                mContentParaIndexs.add(paraList.indexOf(para)); //目录对应的在段落集合中的索引

            }

        }

    }

    //参数：书名 书的封面 书的全文 （即可从asset中读取到的全部书籍信息）
    public Book(String bookTitle, Bitmap bookCover, String fullText) {

        //初始化书的段落列表 书的目录 书的目录在段落中的索引
        //以便从读取中的信息中进一步提取到以下信息
        mParagraphList = new ArrayList<>();
        mBookContents = new ArrayList<>();
        mContentParaIndexs=new ArrayList<>();

        mBookTitle = bookTitle;
        mBookCover = bookCover;

        formatText(fullText);

        findContents(mParagraphList);
    }

    public Bitmap getBookCover() {
        return mBookCover;
    }

    public List<String> getParagraphList() {
        return mParagraphList;
    }

    public List<String> getBookContents() {
        return mBookContents;
    }

    public List<Integer> getContentParaIndexs() {
        return mContentParaIndexs;
    }
}
