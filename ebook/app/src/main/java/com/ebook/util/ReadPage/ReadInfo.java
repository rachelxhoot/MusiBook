package com.ebook.util.ReadPage;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ReadInfo implements Serializable {
    //即将读段落索引
    public int nextParaIndex;
    //上一次阅读向后
    public boolean isLastNext = true;
    //往后剩余字符串
    public boolean isNextRes;
    //往前剩余字符串
    public boolean isPreRes;
    //上次向前剩余line
    public List<String> preResLines = new ArrayList<>();
    //上次向后剩余line
    public List<String> nextResLines = new ArrayList<>();

}
