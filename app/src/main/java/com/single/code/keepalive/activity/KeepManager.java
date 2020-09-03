package com.single.code.keepalive.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.lang.ref.WeakReference;

public class KeepManager {

    private static final KeepManager mInstance = new KeepManager();
    //广播
    private KeepReceiver mKeepReceiver;
    //弱引用
    private WeakReference<Activity> mKeepActivity;

    private KeepManager() {

    }

    public static KeepManager getInstance() {
        return mInstance;
    }

    /**
     * 注册 开屏 关屏 广播
     *
     * @param context
     */
    public void registerKeep(Context context) {
        IntentFilter filter = new IntentFilter();

        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);

        mKeepReceiver = new KeepReceiver();
        context.registerReceiver(mKeepReceiver, filter);
    }

    /**
     * 注销 广播接收者
     *
     * @param context
     */
    public void unregisterKeep(Context context) {
        if (mKeepReceiver != null) {
            context.unregisterReceiver(mKeepReceiver);
        }
    }

    /**
     * 开启1像素Activity
     *
     * @param context
     */
    public void startKeep(Context context) {
        Intent intent = new Intent(context, KeepActivity.class);
        // 结合 taskAffinity 一起使用 在指定栈中创建这个activity
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 关闭1像素Activity
     */
    public void finishKeep() {
        if (mKeepActivity != null) {
            Activity activity = mKeepActivity.get();
            if (activity != null) {
                activity.finish();
            }
            mKeepActivity = null;
        }
    }

    /**
     * 设置弱引用
     *
     * @param keep
     */
    public void setKeep(KeepActivity keep) {
        mKeepActivity = new WeakReference<Activity>(keep);
    }

}
