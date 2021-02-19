package com.zhang.yuliang.fastgetmoney.activitys;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.eftimoff.androipathview.PathView;
import com.zhang.yuliang.fastgetmoney.R;
import java.util.Random;

public class SplashActivity extends Activity {
    private TextView textView;//下面文字
    private PathView pathView;//路径图
    private RelativeLayout rlBackground;//背景色
    private static final int splashTime=1000*3;//闪屏时间
    private static final int svgTime=1000*2;//动画时间

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//隐藏状态栏
        setContentView(R.layout.activity_splash);
        textView = (TextView) findViewById(R.id.tv_splash);
        pathView = (PathView) findViewById(R.id.pathView);
        rlBackground= (RelativeLayout) findViewById(R.id.activity_splash);

        showSvgAnimation();
        toNextPage();
    }

    private void initView() {
        Intent intent = new Intent();
        intent.setClass(this, MainActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);

    }

    private void toNextPage() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initView();
            }
        }, splashTime);
    }

    private int randomText;//随机数
    private int randomBgColors;//随机数
    private int randomViews;//随机数
    private int[] svgs = {R.raw.dog, R.raw.ironman, R.raw.monitor,R.raw.dog2,R.raw.bulldog2,R.raw.bulldog,R.raw.backpack};
    private int[] bgColors = {R.color.grey700,R.color.cyana400,R.color.cyan800,R.color.deeppurplea200,R.color.red700,R.color.reda100,R.color.purplea400,R.color.pink900};
    private String[] texts = {"内部定制版","Be your self", "新春快乐！", "祝你狗年大吉吧", "广告位招租！！！","Just do it","饿了就用饿了么","中了，这次定有惊喜"};

    /**
     * 显示svg动画
     */
    private void showSvgAnimation() {
        Random random = new Random();
        randomText = random.nextInt(texts.length);
        randomBgColors = random.nextInt(bgColors.length);
        randomViews = random.nextInt(svgs.length);

        rlBackground.setBackgroundColor(getResources().getColor(bgColors[randomBgColors]));
        textView.setText(texts[randomText]);
        pathView.setSvgResource(svgs[randomViews]);
        pathView.getPathAnimator().delay(100).duration(svgTime).interpolator(new AccelerateDecelerateInterpolator()).start();
        pathView.useNaturalColors();//真实颜色
        pathView.setFillAfter(true);//真实图片展示
    }
}
