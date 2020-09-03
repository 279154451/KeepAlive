package com.single.code.keepalive;

import android.content.Context;
import android.content.Intent;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.single.code.keepalive.account.AccountHelper;
import com.single.code.keepalive.activity.KeepManager;
import com.single.code.keepalive.jobscheduler.ZJobService;
import com.single.code.keepalive.process.LocalService;
import com.single.code.keepalive.process.RemoteService;
import com.single.code.keepalive.service.ForegroundDaemonService;
import com.single.code.keepalive.worker.KeepLiveWork;

import java.util.concurrent.TimeUnit;

/**
 * 创建时间：2020/9/2
 * 创建人：singleCode
 * 功能描述：
 **/
public class KeepAliveHelper {
    public static boolean foreground  = true;//开启前台服务
    private static KeepAliveHelper helper;
    public static KeepAliveHelper getInstance(){
        if(helper == null){
            helper = new KeepAliveHelper();
        }
        return helper;
    }

    public void startKeepAlive(Context context,boolean withForeGroundService){
        foreground = withForeGroundService;
        //1、一像素保活
        keepAliveWithActivity(context);
        //2、守护进程保活+JobScheduler
        keepAliveWithForeGroundService(context,true);
        //3、workManger保活
        keepAliveWithWorkManager(context);
        //4、账户拉活
        keepAliveWithAccount(context);
    }
    public void keepAliveWithAccount(Context context){
        AccountHelper.addAccount(context);
        AccountHelper.autoSync();
    }

    /**
     * 一像素保活
     * @param context
     */
    public void keepAliveWithActivity(Context context){
        KeepManager.getInstance().registerKeep(context);
    }
    public void keepAliveWithForeGroundService(Context context,boolean withDefend){
        if(withDefend){
            startProcessService(context,LocalService.class,foreground);
            startProcessService(context, RemoteService.class,false);
            ZJobService.startJob(context);
        }else {
            Intent foregroundDaemonServiceIntent = new Intent(context, ForegroundDaemonService.class);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
                context.startForegroundService(foregroundDaemonServiceIntent);
            }else {
                context.startService(foregroundDaemonServiceIntent);
            }
        }
    }
    public void keepAliveWithWorkManager(Context context){
        OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest
                .Builder(KeepLiveWork.class)
                .setInitialDelay(60, TimeUnit.SECONDS)
                .build();
        WorkManager.getInstance(context).enqueue(oneTimeWorkRequest);
    }
    private void startProcessService(Context context,Class cass,boolean foreground){
        Intent serviceIntent = new Intent(context, cass);
        if(foreground){
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
                context.startForegroundService(serviceIntent);
            }else {
                context.startService(serviceIntent);
            }
        }else {
            context.startService(serviceIntent);
        }
    }
}
