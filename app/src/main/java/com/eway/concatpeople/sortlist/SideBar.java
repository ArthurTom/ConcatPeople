package com.eway.concatpeople.sortlist;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.eway.concatpeople.R;

/**
 * @author http://blog.csdn.net/finddreams
 * @Description:右侧的sideBar,显示的是二十六个字母以及*，和#号， 点击字母，自动导航到相应拼音的汉字上
 */
public class SideBar extends View {
    // 触摸事件
    private OnTouchingLetterChangedListener onTouchingLetterChangedListener;
    // 26个字母，+☆，#
    public static String[] b = {"☆", "A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z", "#"};


    public int choose = -1;// 选中
    private Paint paint = new Paint();  // 画笔

    private TextView mTextDialog;      // 显示字母放大的textView

    /**
     * 功能：设置放大字母的textView对象
     *
     * @param mTextDialog TextView对象
     */
    public void setTextView(TextView mTextDialog) {
        this.mTextDialog = mTextDialog;
    }

    public SideBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public SideBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SideBar(Context context) {
        super(context);
    }

    /**
     * 重写这个方法，绘制字母的位置
     */
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 获取焦点改变背景颜色.
        int height = getHeight();// 获取对应高度
        int width = getWidth(); // 获取对应宽度
        int singleHeight = height / b.length;// 获取每一个字母的高度

        for (int i = 0; i < b.length; i++) {
            paint.setColor(getResources().getColor(R.color.gray));  // 设置画笔的颜色
            paint.setTypeface(Typeface.DEFAULT_BOLD);               //加粗字体
            paint.setAntiAlias(true);                              // 抗锯齿
            paint.setTextSize(20);                                  // 字体大小
            // 选中的状态
            if (i == choose) {
                paint.setColor(getResources().getColor(R.color.yellow)); // 选中时候，字母颜色是黄色
                paint.setFakeBoldText(true);                            // 设置为粗体
            }
            // x坐标等于中间-字符串宽度的一半.
            float xPos = width / 2 - paint.measureText(b[i]) / 2;
            float yPos = singleHeight * i + singleHeight;
            canvas.drawText(b[i], xPos, yPos, paint);    // 根据坐标绘制
            paint.reset();// 重置画笔
        }

    }


    @SuppressLint("NewApi")
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        final float y = event.getY();// 点击y坐标
        //  final int oldChoose = choose;  // 默认永远为-1，也就是未选中状态
        final int oldChoose = -1;
        final OnTouchingLetterChangedListener listener = onTouchingLetterChangedListener;
        final int c = (int) (y / getHeight() * b.length);// 点击y坐标所占总高度的比例*b数组的长度就等于点击b中的个数.

        switch (action) {
            case MotionEvent.ACTION_UP:
                setBackgroundDrawable(new ColorDrawable(0x00000000));
                choose = -1;//
                invalidate();
                if (mTextDialog != null) {
                    mTextDialog.setVisibility(View.INVISIBLE);  // 将放大的字母显示隐藏显示
                }
                break;

            default:
                setBackgroundResource(R.drawable.sidebar_background);
                setAlpha((float) 0.7);
                if (oldChoose != c) {
                    if (c >= 0 && c < b.length) {
                        if (listener != null) {
                            listener.onTouchingLetterChanged(b[c]);
                        }
                        if (mTextDialog != null) {
                            mTextDialog.setText(b[c]);                   // 设置放大显示的字母
                            mTextDialog.setVisibility(View.VISIBLE);     // 设置字母可见
                        }

                        choose = c;                                   //
                        invalidate();
                    }
                }

                break;
        }
        return true;
    }

    /**
     * 向外公开的方法
     *
     * @param onTouchingLetterChangedListener
     */
    public void setOnTouchingLetterChangedListener(
            OnTouchingLetterChangedListener onTouchingLetterChangedListener) {
        this.onTouchingLetterChangedListener = onTouchingLetterChangedListener;
    }

    /**
     * 接口
     *
     * @author coder
     * 字母改变的监听事件
     */
    public interface OnTouchingLetterChangedListener {
        public void onTouchingLetterChanged(String s);
    }

    /**
     * 更新字母的颜色
     */
    public void update(int Asic) {
        for (int i = 0; i < b.length; i++) {
            char[] a = b[i].toCharArray();

            if (a[0] == Asic) {
                choose = i;
            }
        }
        invalidate();
    }
}