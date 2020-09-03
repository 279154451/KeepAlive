package com.single.code.keepalive.jobscheduler;

import android.annotation.SuppressLint;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.single.code.keepalive.KeepAliveHelper;
import com.single.code.keepalive.process.LocalService;
import com.single.code.keepalive.process.ProcessUtils;
import com.single.code.keepalive.process.RemoteService;

@SuppressLint("NewApi")
public class ZJobService extends JobService {

    private static final String TAG = "ZJobService";

    public static void startJob(Context context) {
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);

        //setPersisted 在设备重启依然执行
        JobInfo.Builder builder = new JobInfo.Builder(8, new ComponentName(context.getPackageName(),
                ZJobService.class.getName())).setPersisted(true);

        // 小于7.0
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            // 每隔 1s 执行一次 job
            // 版本23 开始 进行了改进，最小周期为 5s
            builder.setPeriodic(5000);
        } else {
            // 延迟执行任务
            builder.setMinimumLatency(5000);
        }

        jobScheduler.schedule(builder.build());
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.e(TAG, "onStartJob");

        // 如果7.0以上 轮询
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            startJob(this);
        }
        boolean isLocal = ProcessUtils.isRunningService(this, LocalService.class.getName());
        boolean isRemote = ProcessUtils.isRunningService(this, RemoteService.class.getName());
        if (!isLocal || !isRemote) {
            startService(this,LocalService.class, KeepAliveHelper.foreground);
            startService(this,RemoteService.class,false);
        }
        return false;
    }
    private void startService(Context context,Class clazz,boolean foreground) {
        Intent foregroundDaemonServiceIntent = new Intent(context, clazz);
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
    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
