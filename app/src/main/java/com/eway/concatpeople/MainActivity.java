package com.eway.concatpeople;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.eway.concatpeople.sortlist.CharacterParser;
import com.eway.concatpeople.sortlist.SideBar;
import com.eway.concatpeople.sortlist.SortModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * @author http://blog.csdn.net/finddreams
 * @Description:联系人显示界面
 */
public class MainActivity extends Activity {

    private View mBaseView;
    private ListView sortListView;
    private SideBar sideBar;
    private TextView dialog;
    private SortAdapter adapter;
    private ClearEditText mClearEditText;
    private Map<String, String> callRecords;  // 手机联系人的目录

    private CharacterParser characterParser;
    private List<SortModel> SourceDateList;   // 填充的数据

    private PinyinComparator pinyinComparator;

    private int lsatFirstVisibleItem = -1;
    private LinearLayout titlelayout;
    private TextView titleTextView;


//    private    int section;
//
//    int nextGroupPosition = -1;  // 下一个组字母要显示的位置

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_contact);
        initView();
        initData();
    }

    /**
     * 初始化view
     */
    private void initView() {
        sideBar = (SideBar) this.findViewById(R.id.sidrbar);  // 右侧的字母列表
        dialog = (TextView) this.findViewById(R.id.dialog);   // 选中的字母后，放大的效果

        sortListView = (ListView) this.findViewById(R.id.sortlist);  // 显示联系人列表的方法

        titlelayout = this.findViewById(R.id.title_layout);
        titleTextView = this.findViewById(R.id.title_tit);
    }

    /**
     * 填充数据
     */
    private void initData() {
        // 实例化汉字转拼音类
        characterParser = CharacterParser.getInstance();

        pinyinComparator = new PinyinComparator();  // 对拼音首字母进行排序

        sideBar.setTextView(dialog);   //

        // 设置右侧触摸监听,也就是字母的点击事件，让list滚动到相应的位置
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @SuppressLint("NewApi")
            @Override
            public void onTouchingLetterChanged(String s) {    // s是回调过来的字母
                // 该字母首次出现的位置
                int position = adapter.getPositionForSection(s.charAt(0));  // s.chart(0)返回string中第一个字符的值，然后position返回的是list位置
                if (position != -1) {
                    sortListView.setSelection(position);  // 让list滚动到某一个位置
                }
            }
        });

        // listview的监听事件
        sortListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // 这里要利用adapter.getItem(position)来获取当前position所对应的对象
                // Toast.makeText(getApplication(),
                // ((SortModel)adapter.getItem(position)).getName(),
                // Toast.LENGTH_SHORT).show();
                String number = callRecords.get(((SortModel) adapter
                        .getItem(position)).getName());     // 通过点击的position获取这个位置的名字，然后通过名字获取map<String,String> 中电话号码
                Toast.makeText(MainActivity.this, number, Toast.LENGTH_SHORT).show();
            }
        });


        new ConstactAsyncTask().execute(0);


    }


    /**
     * 异步线程进行操作
     */
    private class ConstactAsyncTask extends AsyncTask<Integer, Integer, Integer> {

        @Override
        protected Integer doInBackground(Integer... arg0) {   // 异步操作，取出手机联系人的数据
            int result = -1;
            PermissionUtil.getPersimmions(MainActivity.this, Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CONTACTS,
                    Manifest.permission.GET_ACCOUNTS);
            callRecords = ConstactUtil.getAllCallRecords(MainActivity.this); // 获取手机上所有人的联系目录
//            Map<String, String> map = new HashMap<>();
//            map.put("阿毛", "11111");
//            map.put("逼哥", "222222");
//            map.put("cadaver", "333333");
//            map.put("四个", "444444");
//            callRecords = map;
            result = 1;
            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {   // UI线程，更新UI
            super.onPostExecute(result);
            if (result == 1) {
                List<String> constact = new ArrayList<String>();
                for (Iterator<String> keys = callRecords.keySet().iterator(); keys
                        .hasNext(); ) {
                    String key = keys.next();
                    constact.add(key);
                }
                String[] names = new String[]{};
                names = constact.toArray(names);    //
                SourceDateList = filledData(names);

                // 根据a-z进行排序源数据
                Collections.sort(SourceDateList, pinyinComparator); // 这是对第一个参数list,进行按照第二个参数的compator的方式进行排序（第二个必须实现了comparator接口）
                adapter = new SortAdapter(MainActivity.this, SourceDateList); // 这个里面的数据已经是有序的了
                sortListView.setAdapter(adapter);
                // 左侧滑动，右侧字母跟着滑动
                sortListView.setOnScrollListener(new AbsListView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(AbsListView view, int scrollState) {


                    }

                    @Override
                    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                        int a = adapter.getSectionForPosition(firstVisibleItem);
                        sideBar.update(a);



                        /*----------*/
                        int section = adapter.getSectionForPosition(firstVisibleItem);  // 第一个位置位置的字母a
//
                        int nextGroupPosition = -1;  // 下一个组字母要显示的位置

                        if (adapter.list != null && adapter.list.size() > 0 && section < adapter.getSectionForPosition(adapter.list.size() - 1)) {

                            do {

                                nextGroupPosition = adapter.getPositionForSection(++section);
                            } while (nextGroupPosition == -1);

                        }
                        Log.d("zwk", "" + nextGroupPosition);

                        if (firstVisibleItem != lsatFirstVisibleItem) {  // 上拉的时候，会相同
                            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) titlelayout.getLayoutParams();
                            params.topMargin = 0;
                            titlelayout.setLayoutParams(params);
                            Log.d("zwk", "hasHave的值为" + nextGroupPosition);
                            // 第一个位置特俗，上面所有的方法获取的，都是下下个字母的位置，而我们要显示下个字母的值，只需要下下个字母的位置-1，
                            // 就是上个字母的最后一个item的位置，获取其字母值也是一样的
//                            if (nextGroupPosition!=-1){
//                                titleTextView.setText((char) adapter.getSectionForPosition(nextGroupPosition - 1) + "");
//                            }else {
//                                titleTextView.setText((char) adapter.getSectionForPosition(section ) + "");
//                            }
                            if (nextGroupPosition != -1) {
                                titleTextView.setText((char) adapter.getSectionForPosition(nextGroupPosition - 1) + "");
                            } else if (adapter.list.size() > 0 && nextGroupPosition == -1) {
                                titleTextView.setText(((char) adapter.getSectionForPosition(adapter.list.size() - 1)+""));
                            }


                        }


                        if (nextGroupPosition == firstVisibleItem + 1) {
                            View childView = view.getChildAt(0);
                            if (childView != null) {
                                int titleHeight = titlelayout.getHeight();

                                int bottom = childView.getBottom();  // 获取当前第一个item，要消失的时候，最底部的距离listView最上面的距离
                                Log.d("zwk", "tittle的高度为" + titleHeight + "----------" + "距离底部的大小" + bottom);
                                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) titlelayout.getLayoutParams();
                                // 由于title是悬浮覆盖在listview上面，所以会遮盖首字母哪里，所以title高设置40dp ,当第一个item上滑
                                // 底部正好等于title的底部的时候，这时，是新的item的顶部和title的底部正好接触，继续向上滑的时候，bottom-titleHeight=向上滑动的距离，
                                // 这时候，不断地调整titlelayout的margin，让他等于-xxx,制造挤出的效果，同时更改title的值，此时，新的分组出来了，但是由于
                                // 我们判断的是分组最后一个item距离list控件的bottom的高度，所以等待下一个分组的首字母的进入进行判断。同时，bottom>titleHeght，
                                // 将title恢复到原来的位置
                                if (bottom < titleHeight) {
                                    float pushDistance = bottom - titleHeight;
                                    params.topMargin = (int) pushDistance;
                                    titlelayout.setLayoutParams(params);
                                } else {
                                    if (params.topMargin != 0) {
                                        params.topMargin = 0;
                                        titlelayout.setLayoutParams(params);
                                    }
                                }
                            }
                        }
                        lsatFirstVisibleItem = firstVisibleItem;

                    }
                });


                mClearEditText = (ClearEditText) MainActivity.this
                        .findViewById(R.id.filter_edit);
                mClearEditText.setOnFocusChangeListener(new OnFocusChangeListener() {

                    @Override
                    public void onFocusChange(View arg0, boolean arg1) {
                        mClearEditText.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);

                    }
                });
                // 根据输入框输入值的改变来过滤搜索
                mClearEditText.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void onTextChanged(CharSequence s, int start,
                                              int before, int count) {
                        // 当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                        filterData(s.toString());
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start,
                                                  int count, int after) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });
            }

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

    }

    /**
     * 为ListView填充数据
     *
     * @param date 名字数组
     * @return
     */
    private List<SortModel> filledData(String[] date) {
        List<SortModel> mSortList = new ArrayList<SortModel>();

        for (int i = 0; i < date.length; i++) {
            SortModel sortModel = new SortModel();
            sortModel.setName(date[i]);               // 将名字设置到对象中
            // 汉字转换成拼音
            String pinyin = characterParser.getSelling(date[i]);      // 将名字全部转化为拼音
            String sortString = pinyin.substring(0, 1).toUpperCase();    // 截取名字第一个首字母，然后转为大写

            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                sortModel.setSortLetters(sortString.toUpperCase());          // 将首字母设置进去
            } else {
                sortModel.setSortLetters("#");                         // 首字母不是字母的，设置为#
            }

            mSortList.add(sortModel);                     // 得到数据list<SortModel> 集合
        }
        return mSortList;

    }

    /**
     * 根据输入框中的值来过滤数据并更新ListView
     *
     * @param filterStr 搜索的字符串
     */
    private void filterData(String filterStr) {
        List<SortModel> filterDateList = new ArrayList<SortModel>();  // 存放要搜索的字符串

        if (TextUtils.isEmpty(filterStr)) {
            filterDateList = SourceDateList;              //  如果，搜索的字符串是空的，就填充全部的内容
        } else {
            filterDateList.clear();                      // 如果搜索的不为空，先删除以前的数据
            for (SortModel sortModel : SourceDateList) {   // 循环的遍历所有的数据
                String name = sortModel.getName();            // 得到每一个数据的名称
                if (name.indexOf(filterStr.toString()) != -1          // 在名字中查找输入内容字符串出现的位置，
                        || characterParser.getSelling(name).startsWith(
                        filterStr.toString())) {     // 字符串的开头和搜索的内容的字符串的字母开头相同
                    filterDateList.add(sortModel);  // 符合条件的设计进更新的list中
                }
            }
        }

        // 根据a-z进行排序
        Collections.sort(filterDateList, pinyinComparator);
        adapter.updateListView(filterDateList);
    }


}
