package com.single.code.keepalive.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.single.code.keepalive.R;
import com.single.code.keepalive.media.MediaPlayerHelper;

import androidx.annotation.Nullable;

public class KeepActivity extends Activity {

    private static final String TAG = "KeepActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.e(TAG, "启动keep");
        Window mWindow = getWindow();
        mWindow.setGravity(Gravity.START | Gravity.TOP);
        WindowManager.LayoutParams attrParams = mWindow.getAttributes();
        attrParams.x = 0;
        attrParams.y = 0;
        attrParams.height = 1;
        attrParams.width = 1;
        mWindow.setAttributes(attrParams);
        //避免卡死，允许点击外部消失
        setFinishOnTouchOutside(true);
        // KeepActivity 创建一个弱引用
        KeepManager.getInstance().setKeep(this);
        //灭屏播放无声音乐，可有效避免CPU休眠导致socket断开等问题，缺点是耗电
        MediaPlayerHelper.getInstance().startMediaPlayer(this,"silent", R.raw.silent,true,null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "关闭keep");
        MediaPlayerHelper.getInstance().releaseMediaPlayer("silent");
    }
}
