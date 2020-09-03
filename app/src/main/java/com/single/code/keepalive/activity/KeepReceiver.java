package com.single.code.keepalive.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

public class KeepReceiver extends BroadcastReceiver {

    private static final String TAG = "KeepReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        Log.e(TAG, "onReceive: " + action);

        if (TextUtils.equals(action, Intent.ACTION_SCREEN_OFF)) {
            // 关闭屏幕时 开启1像素activity
            KeepManager.getInstance().startKeep(context);
        } else if (TextUtils.equals(action, Intent.ACTION_SCREEN_ON)) {
            // 打开屏幕时 关闭1像素activity
            KeepManager.getInstance().finishKeep();
        }
    }
}
