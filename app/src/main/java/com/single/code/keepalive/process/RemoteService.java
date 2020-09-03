package com.single.code.keepalive.process;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.Nullable;

import com.single.code.keepalive.KeepAliveHelper;
import com.single.code.keepalive.R;
import com.single.code.keepalive.service.CancelNoticeService;
import com.single.code.keepalive.service.ForegroundDaemonService;
import com.single.code.keepalive.service.NotificationUtils;
import com.single.code.process.IMyAidlInterface;

public class RemoteService extends Service {
    private static final String TAG = "ProcessService";
    private ServiceConnection serviceConnection;
    private MyBinder myBinder;
    class MyBinder extends IMyAidlInterface.Stub {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        myBinder = new MyBinder();
        serviceConnection = new MyServiceConnection();

//        // 让服务变成前台服务
//        if(LocalService.foreground){
//            setForeground();
//        }
    }
    public void setForeground() {
        //如果API大于18，需要弹出一个可见通知
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
            startForeground(LocalService.NOTICE_ID, getNotification());
            // 如果觉得常驻通知栏体验不好
            // 可以通过启动CancelNoticeService，将通知移除，oom_adj值不变
            Intent intent = new Intent(this, InnerService.class);
            startService(intent);
        } else {
            startForeground(LocalService.NOTICE_ID, new Notification());
        }
    }
    private Notification getNotification(){
        NotificationUtils notificationUtils = new NotificationUtils(this);
        Notification notification = notificationUtils.getNotification("", "", R.mipmap.ic_launcher);
        return notification;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        bindService(new Intent(this, LocalService.class),
                serviceConnection, BIND_AUTO_CREATE);
        return super.onStartCommand(intent, flags, startId);
    }

    class MyServiceConnection implements ServiceConnection {

        // 服务连接后回调
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
        }

        // 连接中断后回调
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e(TAG, "LocalService 可能被杀死了，拉活");

            startLocalService(RemoteService.this, KeepAliveHelper.foreground);
            bindService(new Intent(RemoteService.this, LocalService.class),
                    serviceConnection, BIND_AUTO_CREATE);
        }
    }
    private void startLocalService(Context context,boolean foreground) {
        Intent foregroundDaemonServiceIntent = new Intent(context, LocalService.class);
        if(foreground){
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
                context.startForegroundService(foregroundDaemonServiceIntent);
            }else {
                context.startService(foregroundDaemonServiceIntent);
            }
        }else {
            context.startService(foregroundDaemonServiceIntent);
        }
    }
    public static class InnerService extends Service {

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
                startForeground(LocalService.NOTICE_ID,getNotification());
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
                            manager.cancel(LocalService.NOTICE_ID);
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
}
