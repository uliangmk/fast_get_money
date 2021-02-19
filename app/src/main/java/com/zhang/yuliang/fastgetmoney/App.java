package com.zhang.yuliang.fastgetmoney;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;
import com.zhang.yuliang.fastgetmoney.services.ListenMoneyService;

/**
 * @author YuliangZhang
 * @create 2018/1/26.
 * @description FastGetMoney
 */

public class App extends Application {
    private static App application;
    private Intent GestureServiceIntent;//跳转后台监听窗口变化服务

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        initLifecycleCallbacks();
        initLogger();
    }

    /**
     * 初始化logger
     */
    private void initLogger() {
        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(true)
                .methodCount(1)
                .tag("")
                .build();

        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy){
            @Override
            public boolean isLoggable(int priority, String tag) {
                return BuildConfig.DEBUG;
            }
        });

    }

    /**
     * 初始化生命周期回调
     */
    private void initLifecycleCallbacks() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                GestureServiceIntent = new Intent(App.getInstance(), ListenMoneyService.class);
            }

            @Override
            public void onActivityStarted(Activity activity) {
                //后台服务
                startService(GestureServiceIntent);
            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {
                //后台服务
                startService(GestureServiceIntent);
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
            }
        });
    }

    /**
     * 获取Application实例
     */
    public static App getInstance() {
        return application;
    }

}
