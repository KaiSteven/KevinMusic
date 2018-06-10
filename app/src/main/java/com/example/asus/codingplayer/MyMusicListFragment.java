package com.example.asus.codingplayer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.asus.codingplayer.adapter.MyMusicListAdapter;
import com.example.asus.codingplayer.utils.MediaUtils;
import com.example.asus.codingplayer.vo.Mp3Info;

import java.util.ArrayList;

public class MyMusicListFragment extends Fragment implements OnItemClickListener, View.OnClickListener {
    private ListView listView_my_music;
    private ImageView imageView_album;
    private TextView textView_songName, textView2_singer;
    private ImageView imageView2_play_pause, imageView3_next;
    
    private ArrayList<Mp3Info> mp3Infos;
    private MyMusicListAdapter myMusicListAdapter;
    
    private MainActivity mainActivity;

    private  int position = 0;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
    }

    public static MyMusicListFragment newInstance() {
        MyMusicListFragment my = new MyMusicListFragment();
        return my;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_music_list_layout, null);
        listView_my_music = (ListView) view.findViewById(R.id.ListView_my_music);
        imageView_album = (ImageView) view.findViewById(R.id.imageView_album);
        imageView2_play_pause = (ImageView) view.findViewById(R.id.imageView2_play_pause);
        imageView3_next = (ImageView) view.findViewById(R.id.imageView3_next);
        textView_songName = (TextView) view.findViewById(R.id.textView_songName);
        textView2_singer = (TextView) view.findViewById(R.id.textView2_singer);

        listView_my_music.setOnItemClickListener(this);
        imageView2_play_pause.setOnClickListener(this);
        imageView3_next.setOnClickListener(this);
        imageView_album.setOnClickListener(this);
        loadData();
        return view;
    }
    public void onResume() {
        super.onResume();
        //绑定播放服务
        System.out.println("myMusiclistFragment   onResume...");
        mainActivity.bindPlayService();
    }

    @Override
    public void onPause() {
        super.onPause();
        //解除绑定播放服务
        mainActivity.unbindPlayService();
        System.out.println("myMusiclistFragment   onPause...");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    /**
     * 加载本地音乐列表
     */
    public void loadData() {
        mp3Infos = MediaUtils.getMp3Infos(mainActivity);
        myMusicListAdapter = new MyMusicListAdapter(mainActivity, mp3Infos);
        listView_my_music.setAdapter(myMusicListAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mainActivity.playService.play(position);
    }

    //回调播放状态下的UI设置
    public void changeUIStatusOnPlay(int position) {
        if (position >= 0 && position <= mp3Infos.size()) {
            Mp3Info mp3Info = mp3Infos.get(position);
            textView_songName.setText(mp3Info.getTitle());
            textView2_singer.setText(mp3Info.getArtist());
           if (mainActivity.playService.isPlaying()){
               imageView2_play_pause.setImageResource(R.mipmap.pause);
           }else{
               imageView2_play_pause.setImageResource(R.mipmap.play);
           }


            Bitmap albumBitmap = MediaUtils.getArtwork(mainActivity, mp3Info.getId(), mp3Info.getAlbumId(), true, true);
            imageView_album.setImageBitmap(albumBitmap);
            this.position = position;
        }
    }

    //播放功能按钮
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageView2_play_pause: {
                if (mainActivity.playService.isPlaying()) {
                    imageView2_play_pause.setImageResource(R.mipmap.player_btn_play_normal);
                    mainActivity.playService.pause();
                } else {
                    if (mainActivity.playService.isPause()) {
                        imageView2_play_pause.setImageResource(R.mipmap.player_btn_pause_normal);
                        mainActivity.playService.start();
                    } else {
                        mainActivity.playService.play(mainActivity.playService.getCurrentPosition());
                    }
                }
                break;
            }
            case R.id.imageView3_next:{
                mainActivity.playService.next();
                break;
            }
            case R.id.imageView_album:{
                Intent intent = new Intent(mainActivity,PlayActivity.class);
                startActivity(intent);
                break;
            }

            default:
                break;
        }

    }
}
