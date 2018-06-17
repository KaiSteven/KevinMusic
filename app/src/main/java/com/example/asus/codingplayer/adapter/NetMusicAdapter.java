package com.example.asus.codingplayer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.asus.codingplayer.R;
import com.example.asus.codingplayer.vo.SearchResult;

import java.util.ArrayList;


public class NetMusicAdapter extends BaseAdapter{
    private Context ctx;
    private ArrayList<SearchResult> searchResults;
    public NetMusicAdapter(Context ctx,ArrayList<SearchResult> searchResults) {
        this.ctx = ctx;
        this.searchResults = searchResults;
    }

    public ArrayList<SearchResult> getSearchResults() {
        return searchResults;
    }

    public void setSearchResults(ArrayList<SearchResult> searchResults) {
        this.searchResults = searchResults;
    }

    @Override
    public int getCount() {
        return searchResults.size();
    }

    @Override
    public Object getItem(int position) {
        return searchResults.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if(convertView==null){
            convertView = LayoutInflater.from(ctx).inflate(R.layout.net_item_music_list,null);
            vh = new ViewHolder();
            vh.textView1_title = (TextView) convertView.findViewById(R.id.textView1_title);
            vh.textView2_singer = (TextView) convertView.findViewById(R.id.textView2_singer);
            convertView.setTag(vh);
        }
        vh = (ViewHolder) convertView.getTag();
        SearchResult result = searchResults.get(position);
        vh.textView1_title.setText(result.getMusicName());
        vh.textView2_singer.setText(result.getArtist());
        return convertView;
    }

    static class ViewHolder{
        TextView textView1_title;
        TextView textView2_singer;
    }

}
