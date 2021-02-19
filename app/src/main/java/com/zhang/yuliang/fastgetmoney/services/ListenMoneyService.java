package com.zhang.yuliang.fastgetmoney.services;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.orhanobut.logger.Logger;
import com.zhang.yuliang.fastgetmoney.R;
import com.zhang.yuliang.fastgetmoney.activitys.MainActivity;
import com.zhang.yuliang.fastgetmoney.config.Config;
import com.zhang.yuliang.fastgetmoney.utils.DataUtils;
import com.zhang.yuliang.fastgetmoney.utils.MyToast;

import java.util.List;

@TargetApi(Build.VERSION_CODES.DONUT)
public class ListenMoneyService extends AccessibilityService {
    private boolean getMoneySuccess = false;//是否确实抢到了钱
    private boolean sysClick = false;//是否是本程序点击的开（点了开不一定抢到钱）
    private String openViewFlag=Config.OPEN_DEAFULT_NAME;//点开控件名字
    private final static int GRAY_SERVICE_ID = 21474836;//广播优先级
    private Handler handler = new Handler();//定时回复
    private SoundPool soundPoolSuccess;//抢成功声音
    private SoundPool soundPoolFailed;//抢失败声音
    private SoundPool soundPoolComes;//来红包声音
    private SoundPool soundPoolBoss;//老板上线声音
    private String logTag = "godService";


    /**
     * 监听所有变化回调
     */
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int eventType = event.getEventType();
        Logger.t(logTag).i("所有变化回调： " + eventType);
        switch (eventType) {
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                Logger.t(logTag).i("--通知栏发生改变时");
                dealNotificationChange(event);
                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                String className = event.getClassName().toString();
                Logger.t(logTag).i("--窗口的状态或内容发生改变时:  " + className);
                dealWindowChange(className);
                break;
        }
    }

    /**
     * 处理通知栏
     */
    private void dealNotificationChange(AccessibilityEvent event) {
        List<CharSequence> texts = event.getText();
        String bossName= DataUtils.getStringPreferences(getApplicationContext(), DataUtils.BOOS_CONTEXT,Config.DEAFULT_BOSS_NAME);
        if (!texts.isEmpty()) {
            for (CharSequence text : texts) {
                String content = text.toString();
                //通知栏包括威信红包文字
                if (content.contains("[微信红包]")) {
                    needSound(soundPoolComes);
                    needOpenPower();
                    if (event.getParcelableData() != null && event.getParcelableData() instanceof Notification) {
                        try {
                            Notification notification = (Notification) event.getParcelableData();
                            PendingIntent pendingIntent = notification.contentIntent;
                            pendingIntent.send();
                            Logger.t(logTag).i("进入微信详细页面");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                //通知栏包括老板名字
                else if (content.contains(bossName)) {
                    needSound(soundPoolBoss);
                }

            }
        }
    }


    /**
     * 处理窗口变化
     */
    private void dealWindowChange(String className) {
        switch (className) {
            case "com.tencent.mm.ui.LauncherUI":
            case "android.widget.TextView":
            case "android.widget.ListView":
                Logger.t(logTag).i("----聊天详情或列表页面");
                clickLuckMoneyList();
                break;
            case "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI":
                Logger.t(logTag).i("----红包页面");
                clickOpen();
                break;
            case "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI":
                Logger.t(logTag).i("----红包点开后结果页面");
                dealLuckMoneyResult();
                break;
        }
    }

    /**
     * 获取当前页面包含红包的列表，并进行模拟点击-------------------------------
     */
    private void clickLuckMoneyList() {
        Logger.t(logTag).i("----点击红包列表");
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        if (rootNode != null && rootNode.getChildCount() > 0) {
            recycleContent(rootNode);
        }

    }

    /**
     * 递归函数每一个节点，并将含有"领取红包"点击
     * rootNode 当前页面树状结构句柄
     */
    public void recycleContent(AccessibilityNodeInfo rootNode) {
        if (rootNode.getChildCount() == 0) {
            if (rootNode.getText() != null) {
                //聊天详情页
                if (Config.CLICK_NAME.equals(rootNode.getText().toString())) {
                    rootNode.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }

            }
        } else {
            for (int i = rootNode.getChildCount() - 1; i >= 0; i--) {
                if (rootNode.getChild(i) != null) {
                    recycleContent(rootNode.getChild(i));
                }
            }
        }
    }

    /**
     * 点击开红包按钮----------------------------------------------------------
     */
    private void clickOpen() {
        Logger.t(logTag).i("点击开红包按钮");
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo != null && nodeInfo.getChildCount() > 0) {
            //倒序挨个点击
            for (int i = nodeInfo.getChildCount() - 1; i >= 0; i--) {
                dealClickOpenCondition(nodeInfo.getChild(i));
            }
        }
    }

    /**
     * 红包已经派送完处理（红包弹窗页面）
     */
    private void dealClickOpenCondition(AccessibilityNodeInfo nodeInfo) {

        //TODO 开的按钮是插件android.widget.Button，关闭按钮是android.widget.ImageView
        if (nodeInfo.getClassName().toString().contains("Button")) {
            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            sysClick = true;
            return;
        }


        //没有文字的view设置默认文字标志
        if (nodeInfo.getText() == null) {
            openViewFlag = Config.OPEN_DEAFULT_NAME;
        } else {
            openViewFlag = nodeInfo.getText().toString();
        }

        switch (openViewFlag) {
            //已经抢过了
            case "手慢了，红包派完了":
            case "该红包已被领取":
            case "看看大家的手气":
                needSound(soundPoolFailed);
                break;
        }
    }


    /**
     * 处理红包领取详情页面---------------------------------------------------
     */
    private void dealLuckMoneyResult() {
        Logger.t(logTag).i("处理红包领取结果页面");
        //不是本软件点的开
        if (!sysClick){
            return;
        }

        //点开不一定抢到了
        getMoneySuccess = false;

        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        workOutMoneyCount(rootNode);

        if (getMoneySuccess) {
            needSound(soundPoolSuccess);
            needReplay();
        } else {
            needSound(soundPoolFailed);
        }
    }

    /**
     * 获取抢到的金钱数（领取详情页面 正向递归）
     */
    public void workOutMoneyCount(AccessibilityNodeInfo rootNode) {
        if (rootNode.getChildCount() == 0) {
            if (rootNode.getText() != null) {
                try {
                    //TODO 随微信红包详情页面变化而改变（自己领的不带元，别人领取列表无元）
                    if (rootNode.getText().toString().contains(".") && !rootNode.getText().toString().contains("元")) {

                        Float money = Float.valueOf(rootNode.getText().toString());
                        Float allMoney = DataUtils.getFloatPreferences(getApplicationContext(), DataUtils.MONEY, 0f);
                        DataUtils.putFloatPreferences(getApplicationContext(), DataUtils.MONEY, allMoney + money);

                        int times = DataUtils.getIntPreferences(getApplicationContext(), DataUtils.TIMES, 0);
                        DataUtils.putIntPreferences(getApplicationContext(), DataUtils.TIMES, ++times);

                        getMoneySuccess = true;
                        sysClick = false;
                    }
                } catch (Exception e) {
                    Logger.t(logTag).e("----数字转换异常");
                }
            }
        } else {
            for (int i = 0; i < rootNode.getChildCount(); i++) {
                if (rootNode.getChild(i) != null) {
                    workOutMoneyCount(rootNode.getChild(i));
                }
            }
        }
    }




    /**
     * 是否需要播放声音
     */
    private void needSound(SoundPool soundPool) {
        if (DataUtils.getPreferences(getApplicationContext(), DataUtils.NEED_SOUND, false)) {
            soundPool.play(1, 1, 1, 0, 0, 1);//播放音效
        }
    }

    /**
     * 是否需要锁屏打开屏幕
     */
    private void needOpenPower() {
        if (!DataUtils.getPreferences(getApplicationContext(), DataUtils.NEED_WAKE, false)) {
            return;
        }
        //屏幕唤醒
        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
//        if (pm.isScreenOn()) return;//如果当前开屏
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "StartupReceiver");//最后的参数是LogCat里用的Tag
        wl.acquire();
        //键盘解锁
        if (pm.isScreenOn()) {
            KeyguardManager km = (KeyguardManager) getApplicationContext().getSystemService(Context.KEYGUARD_SERVICE);
            KeyguardManager.KeyguardLock kl = km.newKeyguardLock("StartupReceiver");//参数是LogCat里用的Tag
            kl.disableKeyguard();
        }
    }

    /**
     * 是否需要自动回复----------------------------------------------
     */
    private void needReplay() {
        if (!DataUtils.getPreferences(getApplicationContext(), DataUtils.NEED_REPLAY, false)){
            return;
        }

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);   // 返回
                try {
                    Thread.sleep(600); // 停600毫秒, 否则在微信主界面没进入聊天界面就执行了fillInputBar
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                fillInputBar(DataUtils.getStringPreferences(getApplicationContext(), DataUtils.REPLAY_CONTEXT, "谢谢老板"));
                findAndPerformAction("android.widget.Button", "发送");
            }
        }, 1500);
    }

    /**
     * 填充输入框（自动回复）
     */
    private boolean fillInputBar(String reply) {
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        if (rootNode != null) {
            return findInputBar(rootNode, reply);
        }
        return false;
    }

    /**
     * 查找EditText控件（自动回复）
     * @param rootNode 根结点
     * @param reply    回复内容
     * @return 找到返回true, 否则返回false
     */
    private boolean findInputBar(AccessibilityNodeInfo rootNode, String reply) {
        int count = rootNode.getChildCount();
        for (int i = 0; i < count; i++) {
            AccessibilityNodeInfo node = rootNode.getChild(i);
            if ("android.widget.EditText".equals(node.getClassName())) {   // 找到输入框并输入文本
                fillText(node, reply);
                return true;
            }
            if (findInputBar(node, reply)) {    // 递归查找
                return true;
            }
        }
        return false;
    }

    /**
     * 填充文本（自动回复）
     */
    private void fillText(AccessibilityNodeInfo node, String reply) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Bundle args = new Bundle();
            args.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, reply);
            node.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, args);
        } else {
            ClipData data = ClipData.newPlainText("reply", reply);
            ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            clipboardManager.setPrimaryClip(data);
            node.performAction(AccessibilityNodeInfo.ACTION_FOCUS); // 获取焦点
            node.performAction(AccessibilityNodeInfo.ACTION_PASTE); // 执行粘贴
        }
    }

    /**
     * 查找UI控件并点击（自动回复）
     * @param widget 控件完整名称, 如android.widget.Button, android.widget.TextView
     * @param text   控件文本
     */
    private void findAndPerformAction(String widget, String text) {
        // 取得当前激活窗体的根节点
        if (getRootInActiveWindow() == null) {
            return;
        }
        // 通过文本找到当前的节点
        List<AccessibilityNodeInfo> nodes = getRootInActiveWindow().findAccessibilityNodeInfosByText(text);
        if (nodes != null) {
            for (AccessibilityNodeInfo node : nodes) {
                if (node.getClassName().equals(widget) && node.isEnabled()) {
                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK); // 执行点击
                    break;
                }
            }
        }
    }




    @Override
    public void onCreate() {
        super.onCreate();
    }

    /**
     * 当启动服务的时候就会被调用
     */
    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        MyToast.makeText(this,getResources().getString(R.string.open_success),1000*5).show();
    }

    /**
     * 中断服务的回调
     */
    @Override
    public void onInterrupt() {
        MyToast.makeText(this,getResources().getString(R.string.close_success),1000*5).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        startService(new Intent());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initSoundPool();
        sentNotification();
        return START_STICKY;
    }


    /**
     * 初始化声音池
     */
    private void initSoundPool() {
        //首尾都不为空才不初始化
        if (soundPoolBoss != null&&soundPoolComes!=null) {
            return;
        }
        soundPoolComes = new SoundPool(1, AudioManager.STREAM_ALARM, 0);
        soundPoolComes.load(this, R.raw.comes, 1);
        soundPoolSuccess = new SoundPool(1, AudioManager.STREAM_ALARM, 0);
        soundPoolSuccess.load(this, R.raw.yeah, 1);
        soundPoolFailed = new SoundPool(1, AudioManager.STREAM_ALARM, 0);
        soundPoolFailed.load(this, R.raw.mygod, 1);
        soundPoolBoss = new SoundPool(1, AudioManager.STREAM_ALARM, 0);
        soundPoolBoss.load(this, R.raw.boss, 1);
    }

    /**
     * 系统通知栏点击进入---------------------------
     */
    private void sentNotification() {
        Intent intent2 = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent2, PendingIntent.FLAG_CANCEL_CURRENT);
        Notification notification = new Notification.Builder(this).setContentTitle("上帝之手").setContentText("正在帮您抢红包，点击进入").setSmallIcon(R.mipmap.ic_launcher).setContentIntent(contentIntent).build();
        if (Build.VERSION.SDK_INT < 18) {
            startForeground(GRAY_SERVICE_ID, notification);//API < 18 ，此方法能有效隐藏Notification上的图标
        } else {
            Intent innerIntent = new Intent(this, GrayInnerService.class);
            startService(innerIntent);
            startForeground(GRAY_SERVICE_ID, notification);
        }
    }


    /**
     * 通知栏新服务（系统通知栏）
     */
    public static class GrayInnerService extends Service {
        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            startForeground(GRAY_SERVICE_ID, new Notification());
            stopForeground(true);
            stopSelf();
            return super.onStartCommand(intent, flags, startId);
        }

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }
    }

}
