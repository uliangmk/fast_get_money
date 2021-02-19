package com.zhang.yuliang.fastgetmoney.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zhang.yuliang.fastgetmoney.R;


/**
 * @author YuliangZhang
 * @create 2017/11/29.
 * @description Zhonglv_SecondHouseholds
 */

public class MyToast {
    private Toast mToast;
    private MyToast(Context context, CharSequence text, int duration) {
        View v = LayoutInflater.from(context).inflate(R.layout.view_my_toast, null);
        TextView textView = (TextView) v.findViewById(R.id.tv_toast);
        ImageView imageView = (ImageView) v.findViewById(R.id.im_header);
        textView.setText(text);
        mToast = new Toast(context);
        mToast.setDuration(duration);
        mToast.setView(v);
    }

    public static MyToast makeText(Context context, CharSequence text, int duration) {
        return new MyToast(context, text, duration);
    }

    public static MyToast showToastView(Context context, CharSequence text, int duration) {
        return new MyToast(context, text, duration);
    }


    public void show() {
        if (mToast != null) {
            mToast.setGravity(Gravity.CENTER, 0, 0);
            mToast.show();
        }
    }
    public void setGravity(int gravity, int xOffset, int yOffset) {
        if (mToast != null) {
            mToast.setGravity(gravity, xOffset, yOffset);
        }
    }
}
