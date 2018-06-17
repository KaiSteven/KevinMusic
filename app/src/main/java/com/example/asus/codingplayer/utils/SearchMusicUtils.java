package com.example.asus.codingplayer.utils;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.example.asus.codingplayer.vo.SearchResult;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.parsers.ParserConfigurationException;

/**
 * 搜索音乐工具类
 */
public class SearchMusicUtils {
	private static final int SIZE = 20; //查询前20条歌曲
	private static final String URL = Constant.BAIDU_URL + Constant.BAIDU_SEARCH;
	private static SearchMusicUtils sInstance;
	private OnSearchResultListener mListener;
	
    private ExecutorService mThreadPool;

	public synchronized static SearchMusicUtils getInstance() {
		if (sInstance == null) {
			try {
				sInstance = new SearchMusicUtils();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			}
		}
		
		return sInstance;
	}

	private SearchMusicUtils() throws ParserConfigurationException {
		mThreadPool = Executors.newSingleThreadExecutor();
	}

	public SearchMusicUtils setListener(OnSearchResultListener l) {
		mListener = l;
		return this;
	}

	public void search(final String key, final int page) {
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case Constant.SUCCESS:
					if(mListener != null) mListener.onSearchResult((ArrayList<SearchResult>)msg.obj);
					break;
				case Constant.FAILED:
					if(mListener != null) mListener.onSearchResult(null);
					break;
				}
			}
		};
		
		mThreadPool.execute(new Runnable() {
			@Override
			public void run() {
				ArrayList<SearchResult> results = getMusicList(key, page);
				if(results == null) {
					handler.sendEmptyMessage(Constant.FAILED);
					return;
				}
				
				handler.obtainMessage(Constant.SUCCESS, results).sendToTarget();
			}
		});
	}

    //使用 Jsoup请求网络解析数据
	private ArrayList<SearchResult> getMusicList(final String key, final int page){
		final String start = String.valueOf((page - 1) * SIZE);
		
		try {
			Document doc = Jsoup.connect(URL)
					  .data("key", key, "start", start, "size", String.valueOf(SIZE))
					  .userAgent(Constant.USER_AGENT)
					  .timeout(60 * 1000).get();
//            System.out.println(doc);
            Elements songTitles = doc.select("div.song-item.clearfix");
			Elements songInfos;
			ArrayList<SearchResult> searchResults = new ArrayList<SearchResult>();
			
			TAG:
			for(Element song : songTitles) {
				songInfos = song.getElementsByTag("a");
				SearchResult searchResult = new SearchResult();
				for(Element info : songInfos) {
					// 收费的歌曲
					if(info.attr("href").startsWith("http://y.baidu.com/song/")) {
						continue TAG;
					}
					
					// 跳转到百度音乐盒的歌曲
					if(info.attr("href").equals("#") && !TextUtils.isEmpty(info.attr("data-songdata"))) {
						continue TAG;
					}
					
					// 歌曲链接
					if(info.attr("href").startsWith("/song")) {
						searchResult.setMusicName(info.text());
						searchResult.setUrl(info.attr("href"));
					}
					
					// 歌手链接
					if(info.attr("href").startsWith("/data")) {
						searchResult.setArtist(info.text());
					}
					
					// 专辑链接
					if(info.attr("href").startsWith("/album")) {
						searchResult.setAlbum(info.text().replaceAll("《|》", ""));
					}
				}
				
				searchResults.add(searchResult);
			}
//            System.out.println(searchResults);
            return searchResults;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	public interface OnSearchResultListener {
		public void onSearchResult(ArrayList<SearchResult> results);
	}
}
