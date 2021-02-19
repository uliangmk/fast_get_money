package com.zhang.yuliang.fastgetmoney.activitys;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.zhang.yuliang.fastgetmoney.R;
import com.zhang.yuliang.fastgetmoney.utils.DataUtils;

public class SettingsActivity extends Activity implements View.OnClickListener {
    private ImageView imSetSound, imSetReplay,imSetBoss, imSetWake,imSetScreenLight, imBack;
    private EditText etPutReplay,etPutBoss;
    private RelativeLayout rlInputReplay,rlInputBoss;
    private Boolean vip1 = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//状态栏
            setTranslucentStatus(true);
        }
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initButtonStatus();

    }

    private void initButtonStatus() {
        etPutBoss.setHintTextColor(getResources().getColor(R.color.color_gold2));
        etPutReplay.setHintTextColor(getResources().getColor(R.color.color_gold2));
        SpannableString s = new SpannableString(DataUtils.getStringPreferences(getApplicationContext(), DataUtils.REPLAY_CONTEXT, "谢谢老板"));//这里输入自己想要的提示文字
        etPutReplay.setHint(s);


        if (DataUtils.getPreferences(SettingsActivity.this, DataUtils.NEED_SOUND, false)) {
            imSetSound.setBackground(getResources().getDrawable(R.mipmap.switch_on));
        } else {
            imSetSound.setBackground(getResources().getDrawable(R.mipmap.switch_off));
        }

        if (DataUtils.getPreferences(SettingsActivity.this, DataUtils.NEED_REPLAY, false)) {
            imSetReplay.setBackground(getResources().getDrawable(R.mipmap.switch_on));
            rlInputReplay.setVisibility(View.VISIBLE);
        } else {
            imSetReplay.setBackground(getResources().getDrawable(R.mipmap.switch_off));
            rlInputReplay.setVisibility(View.GONE);
        }

        if (DataUtils.getPreferences(SettingsActivity.this, DataUtils.NEED_BOSS, false)) {
            imSetBoss.setBackground(getResources().getDrawable(R.mipmap.switch_on));
            rlInputBoss.setVisibility(View.VISIBLE);
        } else {
            imSetBoss.setBackground(getResources().getDrawable(R.mipmap.switch_off));
            rlInputBoss.setVisibility(View.GONE);
        }

        if (DataUtils.getPreferences(SettingsActivity.this, DataUtils.NEED_WAKE, false)) {
            imSetWake.setBackground(getResources().getDrawable(R.mipmap.switch_on));
        } else {
            imSetWake.setBackground(getResources().getDrawable(R.mipmap.switch_off));
        }

        if (DataUtils.getPreferences(SettingsActivity.this, DataUtils.NEED_SCREEN_LIGHT, false)) {
            imSetScreenLight.setBackground(getResources().getDrawable(R.mipmap.switch_on));
        } else {
            imSetScreenLight.setBackground(getResources().getDrawable(R.mipmap.switch_off));
        }
    }


    private void initView() {
        rlInputReplay = (RelativeLayout) findViewById(R.id.rl_in_put_replay);
        rlInputBoss = (RelativeLayout) findViewById(R.id.rl_in_put_boss_come_in);
        etPutReplay = (EditText) findViewById(R.id.et_put_replay);
        etPutBoss = (EditText) findViewById(R.id.et_put_boss);
        imSetSound = (ImageView) findViewById(R.id.im_set_sound);
        imSetReplay = (ImageView) findViewById(R.id.im_set_replay);
        imSetBoss = (ImageView) findViewById(R.id.im_boss_come_in);
        imSetWake = (ImageView) findViewById(R.id.im_set_wake);
        imSetScreenLight = (ImageView) findViewById(R.id.im_set_screen);
        imBack = (ImageView) findViewById(R.id.im_back);
        imSetSound.setOnClickListener(this);
        imSetReplay.setOnClickListener(this);
        imSetBoss.setOnClickListener(this);
        imSetWake.setOnClickListener(this);
        imSetScreenLight.setOnClickListener(this);
        imBack.setOnClickListener(this);

        etPutReplay.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                //存储
                DataUtils.putStringPreferences(SettingsActivity.this, DataUtils.REPLAY_CONTEXT, s.toString());
                Toast.makeText(SettingsActivity.this, "设置回复为：" + s.toString(), Toast.LENGTH_LONG).show();
            }
        });
        etPutBoss.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                //存储
                DataUtils.putStringPreferences(SettingsActivity.this, DataUtils.BOOS_CONTEXT, s.toString());
                Toast.makeText(SettingsActivity.this, "设置bossName为：" + s.toString(), Toast.LENGTH_LONG).show();
            }
        });
//        etPutVipTitle.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                //存储
//                Log.i("ulog", "---------" + s.toString());
//                if (vip1) {
//                    DataUtils.putStringPreferences(SettingsActivity.this, DataUtils.VIP1, s.toString());
//                    Toast.makeText(SettingsActivity.this, "设置闪屏1用语：" + s.toString(), Toast.LENGTH_LONG).show();
//                    vip1 = false;
//                } else {
//                    DataUtils.putStringPreferences(SettingsActivity.this, DataUtils.VIP2, s.toString());
//                    Toast.makeText(SettingsActivity.this, "设置闪屏2用语：" + s.toString(), Toast.LENGTH_LONG).show();
//                }
//                etPutVipTitle.setVisibility(View.INVISIBLE);
//            }
//
//
//        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.im_set_sound:
                if (DataUtils.getPreferences(SettingsActivity.this, DataUtils.NEED_SOUND, false)) {
                    imSetSound.setBackground(getResources().getDrawable(R.mipmap.switch_off));
                    DataUtils.putPreferences(SettingsActivity.this, DataUtils.NEED_SOUND, false);
                } else {
                    imSetSound.setBackground(getResources().getDrawable(R.mipmap.switch_on));
                    DataUtils.putPreferences(SettingsActivity.this, DataUtils.NEED_SOUND, true);
                }
                break;
            case R.id.im_set_replay:
                if (DataUtils.getPreferences(SettingsActivity.this, DataUtils.NEED_REPLAY, false)) {
                    imSetReplay.setBackground(getResources().getDrawable(R.mipmap.switch_off));
                    DataUtils.putPreferences(SettingsActivity.this, DataUtils.NEED_REPLAY, false);
                    rlInputReplay.setVisibility(View.GONE);
                } else {
                    imSetReplay.setBackground(getResources().getDrawable(R.mipmap.switch_on));
                    DataUtils.putPreferences(SettingsActivity.this, DataUtils.NEED_REPLAY, true);
                    rlInputReplay.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.im_boss_come_in:
                if (DataUtils.getPreferences(SettingsActivity.this, DataUtils.NEED_BOSS, false)) {
                    imSetBoss.setBackground(getResources().getDrawable(R.mipmap.switch_off));
                    DataUtils.putPreferences(SettingsActivity.this, DataUtils.NEED_BOSS, false);
                    rlInputBoss.setVisibility(View.GONE);
                } else {
                    imSetBoss.setBackground(getResources().getDrawable(R.mipmap.switch_on));
                    DataUtils.putPreferences(SettingsActivity.this, DataUtils.NEED_BOSS, true);
                    rlInputBoss.setVisibility(View.VISIBLE);
                    Toast.makeText(SettingsActivity.this, "当前关注人名称：" + DataUtils.getStringPreferences
                            (getApplicationContext(),DataUtils.BOOS_CONTEXT,"空"),
                            Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.im_set_wake:
                if (DataUtils.getPreferences(SettingsActivity.this, DataUtils.NEED_WAKE, false)) {
                    imSetWake.setBackground(getResources().getDrawable(R.mipmap.switch_off));
                    DataUtils.putPreferences(SettingsActivity.this, DataUtils.NEED_WAKE, false);
                } else {
                    imSetWake.setBackground(getResources().getDrawable(R.mipmap.switch_on));
                    DataUtils.putPreferences(SettingsActivity.this, DataUtils.NEED_WAKE, true);
                }
                break;
            case R.id.im_set_screen:
                if (DataUtils.getPreferences(SettingsActivity.this, DataUtils.NEED_SCREEN_LIGHT, false)) {
                    imSetScreenLight.setBackground(getResources().getDrawable(R.mipmap.switch_off));
                    DataUtils.putPreferences(SettingsActivity.this, DataUtils.NEED_SCREEN_LIGHT, false);
                } else {
                    imSetScreenLight.setBackground(getResources().getDrawable(R.mipmap.switch_on));
                    DataUtils.putPreferences(SettingsActivity.this, DataUtils.NEED_SCREEN_LIGHT, true);
                }break;
            case R.id.im_back:
                finish();
                break;
//            case R.id.im_first_words:
//                vip1 = true;
//            case R.id.im_second_words:
//                break;
        }
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
}
