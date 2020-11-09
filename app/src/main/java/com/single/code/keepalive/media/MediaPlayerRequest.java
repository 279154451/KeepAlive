package com.single.code.keepalive.media;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;

/**
 * 创建时间：2020/11/5
 * 创建人：singleCode
 * 功能描述：
 **/
public class MediaPlayerRequest {
    private MediaPlayer mediaPlayer;
    private Context context;
    private Object url;
    private boolean looping = true;
    private String key;
    private IPlayCompleteListener listener;
    private IPlayCompleteListener callBack;

    public MediaPlayerRequest(Context context, Object url, boolean looping, String key, IPlayCompleteListener listener,IPlayCompleteListener callBack) {
        this.context = context.getApplicationContext();
        this.url = url;
        this.looping = looping;
        this.key = key;
        this.listener = listener;
        this.callBack = callBack;
    }

    private void createPlayerIfNeed() {
        if(mediaPlayer == null){
            mediaPlayer = new MediaPlayer();
        }
    }

    public void playMusic() {
        createPlayerIfNeed();
        try {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.reset();
            }
            if(url instanceof Integer){
                AssetFileDescriptor afd = context.getResources().openRawResourceFd((int)url);
                mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                afd.close();
            }else if(url instanceof String){
                mediaPlayer.setDataSource((String)url);
            }else if(url instanceof AssetFileDescriptor){
                AssetFileDescriptor afd = (AssetFileDescriptor) url;
                mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                afd.close();
            }else if(url instanceof Uri){
                mediaPlayer.setDataSource(context,(Uri)url);
            }
            mediaPlayer.setLooping(looping);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer player) {
                    if (mediaPlayer != null) {
                        mediaPlayer.start();
                    }
                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer player) {
                    stopMediaPlayer();
                }
            });
            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer player, int what, int extra) {
                    stopMediaPlayer();
                    return false;
                }
            });
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void stopMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.setOnPreparedListener(null);
            mediaPlayer.setOnCompletionListener(null);
            try {
                mediaPlayer.stop();
            } catch (IllegalStateException e) {

            }
        }
        mediaPlayer = null;
        onCompletion();
        listener = null;
//        callBack = null;
    }

    public void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
                mediaPlayer.release();
            } catch (IllegalStateException e) {

            }
        }
        mediaPlayer = null;
        // 避免内存泄露
        onCompletion();
        listener = null;
//        callBack = null;
    }


    public void pauseMediaPlayer() {
        if(mediaPlayer != null && mediaPlayer.isPlaying()){
            mediaPlayer.pause();
        }
    }

    public void resumeMediaPlayer() {
        if(mediaPlayer != null && !mediaPlayer.isPlaying()){
            mediaPlayer.start();
        }
    }
    private void onCompletion(){
        if(callBack != null){
            callBack.playMusicComplete(this);
        }
        if(listener != null){
            listener.playMusicComplete(this);
        }
    }

    public String getKey() {
        return key;
    }
}
