package com.example.asus.codingplayer;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.astuetz.viewpager.extensions.sample.SuperAwesomeCardFragment;

public class FindMusicListFragment extends Fragment{
    private ListView Listview_my_music;

    public static FindMusicListFragment newInstance() {
        FindMusicListFragment my = new FindMusicListFragment();
        return my;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_music_list_layout,null);
        Listview_my_music = (ListView)view.findViewById(R.id.ListView_my_music);
        return view;
    }
}
