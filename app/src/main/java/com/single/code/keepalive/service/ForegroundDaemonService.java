package com.single.code.keepalive.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.single.code.keepalive.R;


/**
 * Project Name:learnDaemon
 * Package Name:com.lahm.learndaemon
 * Created by lahm on 2018/3/4 下午7:06 .
 */

public class ForegroundDaemonService extends Service {
    public static final int NOTICE_ID = 1024;
    private String TAG = ForegroundDaemonService.class.getSimpleName();
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setForeground();
    }

    public void setForeground() {
        //如果API大于18，需要弹出一个可见通知
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
            startForeground(NOTICE_ID, getNotification());
            // 如果觉得常驻通知栏体验不好
            // 可以通过启动CancelNoticeService，将通知移除，oom_adj值不变
            Intent intent = new Intent(this, CancelNoticeService.class);
            startService(intent);
        } else {
            startForeground(NOTICE_ID, new Notification());
        }
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 如果Service被终止
        // 当资源允许情况下，重启service
        return START_STICKY;
    }

    private Notification getNotification(){
        NotificationUtils notificationUtils = new NotificationUtils(this);
        Notification notification = notificationUtils.getNotification("", "", R.mipmap.ic_launcher);
        return notification;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        // 如果Service被杀死，干掉通知
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
            NotificationManager mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (mManager != null) {
                mManager.cancel(NOTICE_ID);
            }
        }
        // 重启自己
        Intent intent = new Intent(getApplicationContext(), ForegroundDaemonService.class);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
            startForegroundService(intent);
        }else {
            startService(intent);
        }
    }
}
