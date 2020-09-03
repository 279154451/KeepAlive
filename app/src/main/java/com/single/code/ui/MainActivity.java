package com.single.code.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.single.code.keepalive.KeepAliveHelper;
import com.single.code.keepalive.R;
import com.single.code.keepalive.activity.KeepManager;
import com.single.code.keepalive.process.LocalService;
import com.single.code.keepalive.service.ForegroundDaemonService;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        KeepAliveHelper.getInstance().startKeepAlive(this,true);
        //1、一像素保活
//        KeepManager.getInstance().registerKeep(this);
//        //2、前台服务保活
//        startService(new Intent(this, ForegroundService.class));
//            initForegroundDaemonService(this);
//        //3、账户拉活
//        AccountHelper.addAccount(this);
//        AccountHelper.autoSync();
//        //4 JobScheduler
//        ZJobService.startJob(this);
//        //5、  双进程 + 前台服务 + job
//        startForegroundService(this,LocalService.class);
//        startForegroundService(this,RemoteService.class);
//        MyJobService.startJob(this);
//        //6、workManager+jobScheduler拉活
//        OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest
//                .Builder(KeepLiveWork.class)
//                .setInitialDelay(20, TimeUnit.SECONDS)
//                .build();
//        WorkManager.getInstance(this).enqueue(oneTimeWorkRequest);

    }
    private void startForegroundService(Context context,Class cass,boolean foreground){
        Intent foregroundDaemonServiceIntent = new Intent(context, cass);
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
    private void initForegroundDaemonService(Context context) {
        Intent foregroundDaemonServiceIntent = new Intent(context, ForegroundDaemonService.class);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
            context.startForegroundService(foregroundDaemonServiceIntent);
        }else {
            context.startService(foregroundDaemonServiceIntent);
        }
    }
}