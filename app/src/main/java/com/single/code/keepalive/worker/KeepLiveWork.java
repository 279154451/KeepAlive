package com.single.code.keepalive.worker;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.single.code.keepalive.KeepAliveHelper;
import com.single.code.keepalive.jobscheduler.ZJobService;


public class KeepLiveWork extends Worker {

    public KeepLiveWork(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.e("KeepLiveWork", "doWork: ");
        KeepAliveHelper.getInstance().keepAliveWithWorkManager(getApplicationContext());
        ZJobService.startJob(getApplicationContext());
        return Result.success();
    }
}
