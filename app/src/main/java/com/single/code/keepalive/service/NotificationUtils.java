package com.single.code.keepalive.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

/**
 * 创建时间：2020/9/1
 * 创建人：singleCode
 * 功能描述：
 **/
public class NotificationUtils extends ContextWrapper {
    private static final String SILENT_NAME = "silent_Channel";
    public static final String SILENT_ID = "silent";
    private NotificationManager mManager;
    public NotificationUtils(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //android 8.0以上需要特殊处理，也就是targetSDKVersion为26以上
            createNotificationChannel(1);
        }
    }
    @TargetApi(Build.VERSION_CODES.O)
    private void createNotificationChannel(int notifyType) {
        //第一个参数：channel_id
        //第二个参数：channel_name
        //第三个参数：设置通知重要性级别
        //注意：该级别必须要在 NotificationChannel 的构造函数中指定，总共要五个级别；
        //范围是从 NotificationManager.IMPORTANCE_NONE(0) ~ NotificationManager.IMPORTANCE_HIGH(4)
        NotificationChannel channel = new NotificationChannel(SILENT_ID, SILENT_NAME,
                NotificationManager.IMPORTANCE_NONE);
        channel.enableLights(false);//闪光灯
        channel.enableVibration(false);//是否允许震动
        channel.setVibrationPattern(new long[]{0});
        channel.setSound(null, null);
        channel.setVibrationPattern(new long[]{0});//设置震动模式
        getManager().createNotificationChannel(channel);
    }
    /**
     * 获取创建一个NotificationManager的对象
     *
     * @return NotificationManager对象
     */
    public NotificationManager getManager() {
        if (mManager == null) {
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mManager;
    }
    public Notification getNotification(String title, String content, int icon) {
        Notification build;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //android 8.0以上需要特殊处理，也就是targetSDKVersion为26以上
            //通知用到NotificationCompat()这个V4库中的方法。但是在实际使用时发现书上的代码已经过时并且Android8.0已经不支持这种写法
            Notification.Builder builder = getChannelNotification(title, content, icon);
            build = builder.build();
        } else {
            NotificationCompat.Builder builder = getNotificationCompat(title, content, icon);
            build = builder.build();
        }
//        if (flags != null && flags.length > 0) {
//            for (int a = 0; a < flags.length; a++) {
//                build.flags |= flags[a];
//            }
//        }
        return build;
    }
    private Notification.Builder getChannelNotification(String title, String content, int icon) {
        Notification.Builder builder =  new Notification.Builder(getApplicationContext(), SILENT_ID);
        builder.setPriority(Notification.PRIORITY_MIN);
        Notification.Builder notificationBuilder = builder
                //设置标题
                .setContentTitle(title)
                //消息内容
                .setContentText(content)
                //设置通知的图标
                .setSmallIcon(icon)
                //让通知左右滑的时候是否可以取消通知
                .setOngoing(false)
//                //设置优先级
//                .setPriority(priority)
                .setGroup("xier")
                .setGroupSummary(true)//自动分组，超过四条归并到一个组
                //是否提示一次.true - 如果Notification已经存在状态栏即使在调用notify函数也不会更新
                .setOnlyAlertOnce(false)
                .setAutoCancel(true);
        notificationBuilder.setDefaults(NotificationCompat.FLAG_ONLY_ALERT_ONCE);
        notificationBuilder.setSound(null);
        notificationBuilder.setVibrate(new long[]{0});
        return notificationBuilder;
    }
    private NotificationCompat.Builder getNotificationCompat(String title, String content, int icon) {
        NotificationCompat.Builder builder =null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new NotificationCompat.Builder(getApplicationContext(), SILENT_ID);
            builder.setPriority(NotificationManager.IMPORTANCE_NONE);
        } else {
            //注意用下面这个方法，在8.0以上无法出现通知栏。8.0之前是正常的。这里需要增强判断逻辑
            builder = new NotificationCompat.Builder(getApplicationContext());
            builder.setPriority(NotificationManager.IMPORTANCE_NONE);
        }
        builder.setGroup("xier");
        builder.setGroupSummary(false);
        builder.setContentTitle(title);
        builder.setContentText(content);
        builder.setSmallIcon(icon);
        builder.setOnlyAlertOnce(false);
        builder.setOngoing(false);
        builder.setSound(null);
        builder.setVibrate(new long[]{0});
        builder.setDefaults(NotificationCompat.FLAG_ONLY_ALERT_ONCE);
        //点击自动删除通知
        builder.setAutoCancel(true);
        return builder;
    }
}
