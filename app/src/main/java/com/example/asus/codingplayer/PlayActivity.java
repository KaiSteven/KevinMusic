package com.example.asus.codingplayer;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.codingplayer.utils.MediaUtils;
import com.example.asus.codingplayer.vo.Mp3Info;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;

import java.util.ArrayList;

public class PlayActivity extends BaseActivity implements View.OnClickListener ,SeekBar.OnSeekBarChangeListener{

    private TextView textView1_title, textView1_end_time, textView1_start_time,textView1_artist;
    private ImageView imageView_favorite, imageView1_album, imageView1_play_mode, imageView3_previous, imageView2_play_pause, imageView3_next;
    private SeekBar seekBar1;
    private ViewPager viewPager;
    private ArrayList<View> views = new ArrayList<>();
    private ArrayList<Mp3Info> mp3Infos;
    private static final int UPDATE_TIME = 0x1;//更新播放时间的标记

    private CodingPlayerAPP app;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_play);

        app = (CodingPlayerAPP) getApplication();

        textView1_title = (TextView) findViewById(R.id.textView1_title);
        textView1_artist = (TextView) findViewById(R.id.textView1_artist);
        textView1_end_time = (TextView) findViewById(R.id.textView1_end_time);
        textView1_start_time = (TextView) findViewById(R.id.textView1_start_time);
        imageView1_album = (ImageView) findViewById(R.id.imageView1_album);
        imageView1_play_mode = (ImageView) findViewById(R.id.imageView1_play_mode);
        imageView3_previous = (ImageView) findViewById(R.id.imageView3_previous);
        imageView2_play_pause = (ImageView) findViewById(R.id.imageView2_play_pause);
        imageView3_next = (ImageView) findViewById(R.id.imageView3_next);
        imageView_favorite = (ImageView) findViewById(R.id.imageView_favorite);

        seekBar1 = (SeekBar) findViewById(R.id.seekBar1);
        imageView1_play_mode.setOnClickListener(this);
        imageView2_play_pause.setOnClickListener(this);
        imageView3_next.setOnClickListener(this);
        imageView3_previous.setOnClickListener(this);
        seekBar1.setOnSeekBarChangeListener(this);
        imageView_favorite.setOnClickListener(this);

        mp3Infos = MediaUtils.getMp3Infos(this);
        myHandler = new MyHandler(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindPlayService();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindPlayService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private static MyHandler myHandler;

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(fromUser){
            playService.pause();
            playService.seekTo(progress);
            playService.start();
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    static class MyHandler extends Handler {

        private PlayActivity playActivity;
        public MyHandler(PlayActivity playActivity) {
            this.playActivity = playActivity;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (playActivity != null) {
                switch (msg.what) {
                    case UPDATE_TIME:
                        playActivity.textView1_start_time.setText(MediaUtils.formatTime(msg.arg1));
                        break;
                }
            }
        }
    }

    @Override
    public void publish(int progress) {


        Message msg = myHandler.obtainMessage(UPDATE_TIME);
        msg.arg1 = progress;
        myHandler.sendMessage(msg);
        seekBar1.setProgress(progress);
    }

    @Override
    public void change(int position) {
        Mp3Info mp3Info = mp3Infos.get(position);
        textView1_title.setText(mp3Info.getTitle());//歌名
        textView1_artist.setText(mp3Info.getArtist());//歌手
        Bitmap albumBitmap = MediaUtils.getArtwork(this, mp3Info.getId(), mp3Info.getAlbumId(), true, false);
        imageView1_album.setImageBitmap(albumBitmap);//专辑图片
        textView1_end_time.setText(MediaUtils.formatTime(mp3Info.getDuration()));
        seekBar1.setProgress(0);
        seekBar1.setMax((int) mp3Info.getDuration());
        if (playService.isPlaying()) {
            imageView2_play_pause.setImageResource(R.mipmap.pause);
        } else {
            imageView2_play_pause.setImageResource(R.mipmap.play);
        }
        switch (playService.getPlay_mode()){
            case PlayService.ORDER_PLAY:
               imageView1_play_mode.setImageResource(R.mipmap.order);
               imageView1_play_mode.setTag(PlayService.ORDER_PLAY);
                break;
            case PlayService.RANDOM_PLAY:
                imageView1_play_mode.setImageResource(R.mipmap.random);
                imageView1_play_mode.setTag(PlayService.RANDOM_PLAY);
                break;
            case PlayService.SINGLE_PLAY:
                imageView1_play_mode.setImageResource(R.mipmap.single);
                imageView1_play_mode.setTag(PlayService.SINGLE_PLAY);
                break;
        }

        //初始化收藏状态
        try {
            Mp3Info likeMp3Info = app.dbUtils.findFirst(Selector.from(Mp3Info.class).where("mp3InfoId","=",mp3Info.getId()));
            if(likeMp3Info != null){
                imageView_favorite.setImageResource(R.mipmap.xin_hong);
            }else{
                imageView_favorite.setImageResource(R.mipmap.xin_bai);
            }

        } catch (DbException e) {
            e.printStackTrace();
        }


    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageView2_play_pause: {
                if (playService.isPlaying()) {
                    imageView2_play_pause.setImageResource(R.mipmap.play);
                    playService.pause();
                } else {
                    if (playService.isPause()) {
                        imageView2_play_pause.setImageResource(R.mipmap.pause);
                        playService.start();
                    } else {
                        playService.play(playService.getCurrentPosition());
                    }
                }
                break;
            }
            case R.id.imageView3_next: {
                playService.next();
                break;
            }
            case R.id.imageView3_previous: {
                playService.prev();
                break;
            }
            case R.id.imageView1_play_mode:{
                int mode = (int)imageView1_play_mode.getTag();
                switch (mode){
                    case PlayService.ORDER_PLAY:
                        imageView1_play_mode.setImageResource(R.mipmap.random);
                        imageView1_play_mode.setTag(PlayService.RANDOM_PLAY);
                        playService.setPlay_mode(PlayService.RANDOM_PLAY);
                        Toast.makeText(PlayActivity.this, getString(R.string.random_play), Toast.LENGTH_SHORT).show();//中文提示
                        break;
                        case PlayService.RANDOM_PLAY:
                        imageView1_play_mode.setImageResource(R.mipmap.single);
                        imageView1_play_mode.setTag(PlayService.SINGLE_PLAY);
                        playService.setPlay_mode(PlayService.SINGLE_PLAY);
                        Toast.makeText(PlayActivity.this, getString(R.string.single_play), Toast.LENGTH_SHORT).show();
                        break;
                        case PlayService.SINGLE_PLAY:
                        imageView1_play_mode.setImageResource(R.mipmap.order);
                        imageView1_play_mode.setTag(PlayService.ORDER_PLAY);
                        playService.setPlay_mode(PlayService.ORDER_PLAY);
                        Toast.makeText(PlayActivity.this, getString(R.string.order_play), Toast.LENGTH_SHORT).show();
                        break;
                }
                break;
            }
            case R.id.imageView_favorite:{
               Mp3Info mp3Info = mp3Infos.get(playService.getCurrentPosition());
               System.out.println(mp3Info);
                try {
                    Mp3Info likeMp3Info = app.dbUtils.findFirst(Selector.from(Mp3Info.class).where("mp3InfoId","=",mp3Info.getId()));
                    if (likeMp3Info == null){
                        mp3Info.setMp3InfoId(mp3Info.getId());
                        System.out.println(mp3Info);
                        app.dbUtils.save(mp3Info);
                        System.out.println("save")  ;
                        imageView_favorite.setImageResource(R.mipmap.xin_hong);
                    }else{
                        app.dbUtils.deleteById(Mp3Info.class,likeMp3Info.getId());
                        System.out.println("delete")  ;
                        imageView_favorite.setImageResource(R.mipmap.xin_bai);
                    }
                } catch (DbException e) {
                    e.printStackTrace();
                }
                break;
            }

            default:
                break;
        }
    }
}
