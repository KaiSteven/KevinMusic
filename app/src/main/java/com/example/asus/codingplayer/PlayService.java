package com.example.asus.codingplayer;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;

import com.example.asus.codingplayer.utils.MediaUtils;
import com.example.asus.codingplayer.vo.Mp3Info;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
//import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 音乐播放的服务组件
 * 实现的功能：
 * 1、播放
 * 2、暂停
 * 3、上一首
 * 4、下一首
 * 5、获取当前播放进度
 */
public class PlayService extends Service implements MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {
    private MediaPlayer mPlayer;
    private int currentPosition;//当前正在播放的歌曲的位置
    ArrayList<Mp3Info> mp3Infos;
    private MusicUpdateListener musicUpdateListener;//设置属性
    private ExecutorService es = Executors.newSingleThreadExecutor();

    private boolean isPause = false;

    //播放模式：顺序播放、单曲播放、随机播放
    private int play_mode = ORDER_PLAY;
    public static final int ORDER_PLAY = 1;//顺序播放
    public static final int RANDOM_PLAY = 2;//随机播放
    public static final int SINGLE_PLAY = 3;//单曲循环

    /**
     * @param play_mode ORDER_PLAY = 1;//顺序播放
     *                  RANDOM_PLAY = 2;//随机播放
     *                  SINGLE_PLAY = 3;//单曲循环
     */
    public void setPlay_mode(int play_mode) {
        this.play_mode = play_mode;
    }

    public int getPlay_mode() {
        return play_mode;
    }

    public boolean isPause() {
        return isPause;
    }

    public PlayService() {
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    private Random random = new Random();
//用于监听当前歌曲播放完后，下一首如何播放
    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        switch (play_mode) {
            case ORDER_PLAY:
                next();
                break;
            case RANDOM_PLAY:
                play(random.nextInt(mp3Infos.size()));
                break;
            case SINGLE_PLAY:
                play(currentPosition);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int extra) {
        mediaPlayer.reset();
        return false;
    }

    class PlayBinder extends Binder {
        public PlayService getPlayService() {
            return PlayService.this;
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        return new PlayBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        CodingPlayerAPP app = (CodingPlayerAPP) getApplication();
        currentPosition = app.sp.getInt("currentPosition", 0);
        play_mode = app.sp.getInt("play_mode", PlayService.ORDER_PLAY);
        mPlayer = new MediaPlayer();
        mPlayer.setOnCompletionListener(this);
        mPlayer.setOnErrorListener(this);
        mp3Infos = MediaUtils.getMp3Infos(this);
        es.execute(updateStatusRunnable);
    }
    //启动线程就得销毁
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (es != null && es.isShutdown()) {
            es.shutdown();
            es = null;
        }
    }

    Runnable updateStatusRunnable = new Runnable() {
        @Override
        public void run() {
            while (true) {
                if (musicUpdateListener != null && mPlayer != null && mPlayer.isPlaying()) {
                    musicUpdateListener.onPublish(getCurrentProgress());
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    //播放
    public void play(int position) {
        if (position >= 0 && position < mp3Infos.size()) {
            Mp3Info mp3Info = mp3Infos.get(position);
            try {
                mPlayer.reset();
                mPlayer.setDataSource(this, Uri.parse(mp3Info.getUrl()));
                mPlayer.prepare();
                mPlayer.start();
                currentPosition = position;
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (musicUpdateListener != null) {
                musicUpdateListener.onChange(currentPosition);
            }
        }

    }

    //暂停
    public void pause() {
        if (mPlayer.isPlaying()) {
            mPlayer.pause();
            isPause = true;
        }
    }

    //上一首
    public void prev() {
        if (currentPosition - 1 < 0) {
            currentPosition = mp3Infos.size() - 1;
        } else {
            currentPosition--;
        }
        play(currentPosition);
    }

    //下一首
    public void next() {
        if (currentPosition + 1 >= mp3Infos.size() - 1) {
            currentPosition = 0;
        } else {
            currentPosition++;
        }
        play(currentPosition);
    }

    public void start() {
        if (mPlayer != null && !mPlayer.isPlaying()) {
            mPlayer.start();
        }
    }

    public boolean isPlaying() {
        if (mPlayer != null) {
            return mPlayer.isPlaying();
        }
        return false;
    }

    public int getCurrentProgress() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            return mPlayer.getCurrentPosition();
        }
        return 0;
    }

    //在音乐播放中，获得播放的位置信息
    public int getDuration() {
        return mPlayer.getDuration();
    }

    public void seekTo(int msec) {
        mPlayer.seekTo(msec);
    }


    //更新状态的接口
    public interface MusicUpdateListener {
        public void onPublish(int progress);

        public void onChange(int position);
    }

    public void setMusicUpdateListener(MusicUpdateListener musicUpdateListener) {
        this.musicUpdateListener = musicUpdateListener;
    }
}
