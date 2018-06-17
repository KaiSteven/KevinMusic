package com.example.asus.codingplayer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ListView;

import com.example.asus.codingplayer.adapter.MyMusicListAdapter;
import com.example.asus.codingplayer.vo.Mp3Info;
import com.lidroid.xutils.exception.DbException;

import java.util.ArrayList;

public class MyLikeMusicListActivity extends BaseActivity{

    private ListView listView_like;
    private CodingPlayerAPP app;
    private ArrayList<Mp3Info> likemp3Infos;
    private MyMusicListAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        app = (CodingPlayerAPP) getApplication();
        setContentView(R.layout.activity_like_music_list);
        listView_like = (ListView) findViewById(R.id.listView_like);
        initData();
    }

    private void initData() {
        try {
            likemp3Infos = (ArrayList<Mp3Info>) app.dbUtils.findAll(Mp3Info.class);
            adapter = new MyMusicListAdapter(this,likemp3Infos);
            listView_like.setAdapter(adapter);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void publish(int progress) {

    }

    @Override
    public void change(int position) {

    }
}
