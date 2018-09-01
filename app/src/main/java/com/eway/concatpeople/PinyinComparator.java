package com.eway.concatpeople;

import com.eway.concatpeople.sortlist.SortModel;

import java.util.Comparator;


/**
 * 功能：实现接口的类
 * @author http://blog.csdn.net/finddreams
 * @Description:拼音的比较器 其中Comparator<T> 其中SortModel 是个对象可以进行对其中的数据类型进行排序的
 * Comparetor(a，b) 这个重写的接口中的方法，比较两个对象的属性排序
 *
 */
public class PinyinComparator implements Comparator<SortModel> {
    @Override
    public int compare(SortModel o1, SortModel o2) {
        if (o1.getSortLetters().equals("@")           // 01.拼音首字母，02.拼音首字母
                || o2.getSortLetters().equals("#")) {
            return -1;
        } else if (o1.getSortLetters().equals("#")
                || o2.getSortLetters().equals("@")) {
            return 1;
        } else {
            return o1.getSortLetters().compareTo(o2.getSortLetters());   // 比较两个对象的首字母，== 0 ，< -1 ,>1
        }
    }

}
