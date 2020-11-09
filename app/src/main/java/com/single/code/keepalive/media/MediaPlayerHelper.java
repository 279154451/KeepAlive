package com.single.code.keepalive.media;

/**
 * 创建时间：2020/8/10
 * 创建人：singleCode
 * 功能描述：
 **/

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import androidx.annotation.NonNull;


/**
 * 参考博客链接 http://blog.csdn.net/u011516685/article/details/50510902
 * 这里注意使用stop和prepareAsync是耗时操作需要在线程里面执行，同时由于stop多次出错之后之前的MediaPlayer不能够被正常使用,所以这里才会采取每次新建一个MediaPlayer实例
 * 这里PlayMusicCompleteListener没有使用弱引用,因为在应用程序里面会存在被GC掉,所以这里使用Handler来避免内存泄露的方式实现
 * Created by Tangxb on 2016/9/1.
 */
public class MediaPlayerHelper implements IPlayCompleteListener{
    private String TAG = MediaPlayerHelper.class.getSimpleName();
    private static MediaPlayerHelper instance;
    private HandlerThread playHandlerThread;
    private Handler playHandler;
    private Map<String,MediaPlayerRequest> playerMap = new ConcurrentHashMap<>();
    /**
     * 播放
     */
    private static final int PLAY = 101;
    /**
     * 停止
     */
    private static final int STOP = 102;

    /**
     * 暂停
     */
    private static final int PAUSE = 103;

    /**
     * 继续
     */
    private static final int RESUME = 104;
    /**
     * 释放
     */
    private static final int RELEASE = 105;

    /**
     * 停止所有播放
     */
    private static final int STOP_ALL = 107;

    private Handler handler;


    private void createHandlerThreadIfNeed() {
        if (playHandlerThread == null) {
            playHandlerThread = new HandlerThread("playMediaThread");
            playHandlerThread.start();
        }
    }

    private void createHandlerIfNeed() {
        if (playHandler == null) {
            playHandler = new Handler(playHandlerThread.getLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    switch (msg.what) {
                        case PLAY:
                            MediaPlayerRequest request = (MediaPlayerRequest) msg.obj;
                            if(request != null){
                                Log.d(TAG, "handleMessage: PLAY:"+request.getKey());
                                if(playerMap.containsKey(request.getKey())){
                                    MediaPlayerRequest request1 = playerMap.get(request.getKey());
                                    request1.releaseMediaPlayer();
                                }
                                playerMap.put(request.getKey(),request);
                                request.playMusic();
                            }
                            break;
                        case PAUSE:
                            String key = (String) msg.obj;
                            if(!TextUtils.isEmpty(key) && playerMap.containsKey(key)){
                                MediaPlayerRequest request1 = playerMap.get(key);
                                if(request1 != null){
                                    Log.d(TAG, "handleMessage: PAUSE:"+request1.getKey());
                                    request1.pauseMediaPlayer();
                                }
                            }
                            break;
                        case RESUME:
                            String key1 = (String) msg.obj;
                            if(!TextUtils.isEmpty(key1) && playerMap.containsKey(key1)){
                                MediaPlayerRequest request2 = playerMap.get(key1);
                                if(request2 != null){
                                    Log.d(TAG, "handleMessage: RESUME:"+request2.getKey());
                                    request2.resumeMediaPlayer();
                                }
                            }
                            break;
                        case STOP:
                            String key3 = (String) msg.obj;
                            if(!TextUtils.isEmpty(key3) && playerMap.containsKey(key3)){
                                MediaPlayerRequest request3 = playerMap.get(key3);
                                if(request3 != null){
                                    Log.d(TAG, "handleMessage: STOP:"+request3.getKey());
                                    request3.stopMediaPlayer();
                                }
                            }
                            break;
                        case RELEASE:
                            String key4 = (String) msg.obj;
                            if(!TextUtils.isEmpty(key4) && playerMap.containsKey(key4)){
                                MediaPlayerRequest request4 = playerMap.get(key4);
                                if(request4 != null){
                                    Log.d(TAG, "handleMessage: RELEASE:"+request4.getKey());
                                    request4.releaseMediaPlayer();
                                }
                            }
                            break;
                        case STOP_ALL:
                            if(playerMap!=null){
                                Set<Map.Entry<String, MediaPlayerRequest>> entries = playerMap.entrySet();
                                Iterator<Map.Entry<String, MediaPlayerRequest>> iterator = entries.iterator();
                                while (iterator.hasNext()){
                                    Map.Entry<String, MediaPlayerRequest> next = iterator.next();
                                    MediaPlayerRequest playerRequest = next.getValue();
                                    if(playerRequest!=null){
                                        playerRequest.releaseMediaPlayer();
                                    }
                                }
                            }
                            break;
                    }
                }
            };
        }
    }


    private MediaPlayerHelper() {
        handler = new Handler(Looper.getMainLooper());
        createHandlerThreadIfNeed();
        createHandlerIfNeed();
    }

    public static MediaPlayerHelper getInstance() {
        if (instance == null) {
            instance = new MediaPlayerHelper();
        }
        return instance;
    }


    /**
     * 开始播放
     * @param context
     * @param key  key值与音频文件一一对应
     * @param url
     * @param looping
     * @param listener
     */
    public void startMediaPlayer(Context context, @NonNull String key, @NonNull Object url, boolean looping, IPlayCompleteListener listener) {
        MediaPlayerRequest request = new MediaPlayerRequest(context,url,looping,key,listener,this);
        Message message = Message.obtain();
        message.what = PLAY;
        message.obj = request;
        playHandler.sendMessage(message);
    }

    /**
     * 暂停
     */
    public void pauseMediaPlayer(String key){
        Message message = Message.obtain();
        message.what = PAUSE;
        message.obj = key;
        playHandler.sendMessage(message);
    }

    /**
     * 继续
     */
    public void resumeMediaPlayer(String key){
        Message message = Message.obtain();
        message.what = RESUME;
        message.obj = key;
        playHandler.sendMessage(message);
    }

    /**
     * 停止并释放资源
     */
    public void releaseMediaPlayer(String key) {
        Message message = Message.obtain();
        message.what = RELEASE;
        message.obj = key;
        playHandler.sendMessage(message);
    }

    /**
     * 停止
     */
    public void stopMediaPlayer(String key) {
        Message message = Message.obtain();
        message.what = STOP;
        message.obj = key;
        playHandler.sendMessage(message);
    }

    public void stopAllMedia(){
        playHandler.sendEmptyMessage(STOP_ALL);
    }


    @Override
    public void playMusicComplete(MediaPlayerRequest request) {
        Log.d(TAG, "playMusicComplete: "+request.getKey());
        if(playerMap.containsKey(request.getKey())){
            playerMap.remove(request.getKey());
        }
    }
}