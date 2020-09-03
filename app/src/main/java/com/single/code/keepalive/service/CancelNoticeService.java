package com.single.code.keepalive.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;

import androidx.annotation.Nullable;

import com.single.code.keepalive.R;


/**
 * Project Name:learnDaemon
 * Package Name:com.lahm.learndaemon
 * Created by lahm on 2018/3/4 下午7:09 .
 */

public class CancelNoticeService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
            startForeground(ForegroundDaemonService.NOTICE_ID,getNotification());
            // 开启一条线程，去移除DaemonService弹出的通知
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // 延迟1s
                    SystemClock.sleep(1000);
                    // 取消CancelNoticeService的前台
                    stopForeground(true);
                    // 移除DaemonService弹出的通知
                    NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    if (manager != null) {
                        manager.cancel(ForegroundDaemonService.NOTICE_ID);
                        manager.cancelAll();
                    }
                    // 任务完成，终止自己
                    stopSelf();
                }
            }).start();
        }
        return super.onStartCommand(intent, flags, startId);
    }
    private Notification getNotification(){
        NotificationUtils notificationUtils = new NotificationUtils(this);
        Notification notification = notificationUtils.getNotification("", "", R.mipmap.ic_launcher);
        return notification;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}