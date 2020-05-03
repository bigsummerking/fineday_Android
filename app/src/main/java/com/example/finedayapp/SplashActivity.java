package com.example.finedayapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/**
 * 最好不要浪费用户的时间去打开没有用的界面，然而好多应用一开始启动都会花费一些时间，
 * 特别是第一次的时候，这个时候数据时第一次加载，大部分都会出现空白的页面，
 * 所以为了用户的友好体验，建议使用启动页。第一次应该是最慢的，
 * 但是第一次缓存以后，再次打开应该是非常快的。
 **/
public class SplashActivity extends Activity {
    private static final int WHAT_DELAY = 0x11;// 启动页的延时跳转
    private static final int DELAY_TIME = 1500;// 延时时间

    // 创建Handler对象，处理接收的消息
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_DELAY:// 延时1500ms跳转
                    goHome();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        // 调用handler的sendEmptyMessageDelayed方法
        handler.sendEmptyMessageDelayed(WHAT_DELAY, DELAY_TIME);
    }

    /**
     * 跳转到主页面
     */
    private void goHome() {
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        finish();// 销毁当前活动界面
    }
}
