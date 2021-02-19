package com.zhang.yuliang.fastgetmoney.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author YuliangZhang
 * @create 2017/1/23.
 * @description FastGetMoney
 */


public class DataUtils {
    public static String NEED_SOUND = "need_sound";//声音开关
    public static String NEED_REPLAY = "need_replay";//需要自动回复开关
    public static String NEED_BOSS = "need_boss";//老板上线开关
    public static String NEED_WAKE = "need_wake";//电源键开关
    public static String NEED_SCREEN_LIGHT = "need_screen_light";//屏幕敞亮开关
    public static String REPLAY_CONTEXT = "replay_context";//回复内容建
    public static String BOOS_CONTEXT = "boss_context";//老板名字

    public static String MONEY = "money";//金钱
    public static String TIMES = "times";//次数

    public static synchronized void putPreferences(Context context, String key, Boolean value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("fast", Context.MODE_PRIVATE); //私有数据
        SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
        editor.putBoolean(key, value);
        editor.commit();//提交修改
    }

    public static Boolean getPreferences(Context context, String key, boolean defValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("fast", Context.MODE_PRIVATE); //私有数据
        try {
            return sharedPreferences.getBoolean(key, defValue);
        } catch (Exception e) {
            return defValue;
        }
    }



    public static synchronized void putStringPreferences(Context context, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("fast", Context.MODE_PRIVATE); //私有数据
        SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
        editor.putString(key, value);
        editor.commit();//提交修改
    }

    public static String  getStringPreferences(Context context, String key, String defValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("fast", Context.MODE_PRIVATE); //私有数据
        try {
            return sharedPreferences.getString(key,defValue);
        } catch (Exception e) {
            return defValue;
        }
    }

    public static synchronized void putFloatPreferences(Context context, String key, Float value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("fast", Context.MODE_PRIVATE); //私有数据
        SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
        editor.putFloat(key, value);
        editor.commit();//提交修改
    }

    public static Float  getFloatPreferences(Context context, String key, Float defValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("fast", Context.MODE_PRIVATE); //私有数据
        try {
            return sharedPreferences.getFloat(key,defValue);
        } catch (Exception e) {
            return defValue;
        }
    }

    public static synchronized void putIntPreferences(Context context, String key, Integer value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("fast", Context.MODE_PRIVATE); //私有数据
        SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
        editor.putInt(key, value);
        editor.commit();//提交修改
    }

    public static Integer  getIntPreferences(Context context, String key, Integer defValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("fast", Context.MODE_PRIVATE); //私有数据
        try {
            return sharedPreferences.getInt(key,defValue);
        } catch (Exception e) {
            return defValue;
        }
    }
}
