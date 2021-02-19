package com.zhang.yuliang.fastgetmoney.activitys;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zhang.yuliang.fastgetmoney.R;
import com.zhang.yuliang.fastgetmoney.services.ListenMoneyService;
import com.zhang.yuliang.fastgetmoney.utils.DataUtils;
import com.zhang.yuliang.fastgetmoney.utils.MyToast;

import java.text.DecimalFormat;
import java.util.List;

public class MainActivity extends Activity implements View.OnClickListener {
    private long exitTime = 0;//退出应用当前时刻
    private final long APP_EXIT_TIMER = 2000;//双击退出时间间隔
    private Button btSetService;
    private TextView tvShowTips,tvTimes,tvMoneys;
    private ImageView imSettings;
    private AccessibilityManager accessibilityManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//状态栏
            setTranslucentStatus(true);
        }
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initButtonStatuse();
    }

    private void initView() {
        tvTimes= (TextView) findViewById(R.id.tv_times);
        tvMoneys= (TextView) findViewById(R.id.tv_moneys);
        btSetService = (Button) findViewById(R.id.bt_set_service);
        tvShowTips = (TextView) findViewById(R.id.tv_show_status);
        imSettings= (ImageView) findViewById(R.id.im_settings);
        btSetService.setOnClickListener(this);
        imSettings.setOnClickListener(this);
        accessibilityManager = (AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE);
        keepScreenLight();
    }
    /**
     * 打开屏幕
     */
    private void keepScreenLight() {
        if (!DataUtils.getPreferences(getApplicationContext(), DataUtils.NEED_SCREEN_LIGHT, false)) {
         return;
        }

        //屏幕常亮
        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
//        if (pm.isScreenOn()) return;//如果当前开屏
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "StartupReceiver");//最后的参数是LogCat里用的Tag
        wl.acquire();
    }

    private void initButtonStatuse() {
        if (isServiceEnabled()) {
            tvShowTips.setText(getResources().getString(R.string.app_is_running));
            btSetService.setText(getResources().getString(R.string.bt_close));
            btSetService.setBackground(getResources().getDrawable(R.drawable.bg_btn_on));
        } else {
            tvShowTips.setText(getResources().getString(R.string.app_is_gone));
            btSetService.setText(getResources().getString(R.string.bt_open));
            btSetService.setBackground(getResources().getDrawable(R.drawable.bg_btn_off));
        }
        DecimalFormat decimalFormat=new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
        String moneys=decimalFormat.format(DataUtils.getFloatPreferences(MainActivity.this,DataUtils.MONEY,0f));
        tvMoneys.setText(moneys);
        String times= String.valueOf(DataUtils.getIntPreferences(MainActivity.this,DataUtils.TIMES,0));
        tvTimes.setText(times);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Intent innerIntent = new Intent(this, ListenMoneyService.class);
        startService(innerIntent);
        keepScreenLight();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_set_service:
                if (btSetService.getText().equals(getResources().getString(R.string.bt_close))) {
                    MyToast.makeText(this,getResources().getString(R.string.close_service),1000*5).show();
                } else {//开启设置页面
                    MyToast.makeText(this,getResources().getString(R.string.open_service),1000*5).show();
                }
                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivity(intent);
                overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left);
                break;
            case R.id.im_settings:
                Intent intent2 = new Intent(MainActivity.this,SettingsActivity.class);
                startActivity(intent2);
                overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left);
                break;
        }
    }

    /**
     * 获取 HongbaoService 是否启用状态
     *
     * @return
     */
    private boolean isServiceEnabled() {
        List<AccessibilityServiceInfo> accessibilityServices = accessibilityManager.getEnabledAccessibilityServiceList
                (AccessibilityServiceInfo.FEEDBACK_GENERIC);
        for (AccessibilityServiceInfo info : accessibilityServices) {
            if (info.getId().equals(getPackageName() + "/.services.ListenMoneyService")) {
                return true;
            }
        }
        return false;
    }

    /**
     * 沉浸式状态栏
     */
    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    /**
     * 双击退出app
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > APP_EXIT_TIMER) {
                Toast.makeText(getApplicationContext(), getString(R.string.app_exit_notify_text), Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
