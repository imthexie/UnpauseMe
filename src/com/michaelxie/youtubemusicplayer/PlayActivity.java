/*
 * Copyright 2012 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.michaelxie.youtubemusicplayer;

import com.michaelxie.youtubemusicplayer.*;
import com.michaelxie.youtubemusicplayer.PlayActivity.MyPlaybackEventListener;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.PlaybackEventListener;
import com.google.android.youtube.player.YouTubePlayer.PlayerStyle;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerView;



import com.google.api.services.youtube.model.VideoPlayer;

import android.text.format.Time;
import android.util.DisplayMetrics;
import android.view.View;
import android.annotation.SuppressLint;
import android.app.Activity;
//import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.MediaController.MediaPlayerControl;
import android.widget.Toast;
import android.widget.VideoView;

class VideoLoader implements Runnable {
	YouTubePlayer player;
	String id;
	boolean restored;
	MyPlaybackEventListener playbackEventListener;
	public VideoLoader(YouTubePlayer p, String id, boolean restored, MyPlaybackEventListener listener) {
		player = p;
		this.id = id;
		this.restored = restored;
		playbackEventListener = listener;
	}
	
	/*void listenForEvents() {
		while(true) {
			try {
				wait();
				player.play();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}*/
	
	void setRestored(boolean status) {
		restored = status;
	}
	@Override
	public void run() {
	    if(!restored) player.cueVideo(id);
	    else player.play();
	    //listenForEvents();
	}
	
}

public class PlayActivity extends YouTubeFailureRecoveryActivity implements View.OnClickListener{
	private String id;
	public YouTubePlayer player;
	boolean restored;
	private Button playButton; 
	private Button pauseButton; 
	private MyPlaybackEventListener playbackEventListener;

	Thread th;
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.playerview);
    playButton = (Button)findViewById(R.id.play_button);
	pauseButton = (Button)findViewById(R.id.pause_button);
	playButton.setOnClickListener(this);
	pauseButton.setOnClickListener(this);
    if (savedInstanceState == null) {
    	id = getIntent().getExtras().getString("id");
    	if(th != null) {
    		th.stop();
    		try {
				th.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }
    YouTubePlayerView youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
    
    youTubeView.initialize(DeveloperKey.DEVELOPER_KEY, this);
  }
  
	
 
 
@Override
	public void onClick(View v) {
	    if (v == playButton) {
	      //player.play();
	    	//th.run();
	    } else if (v == pauseButton) {
	      //player.pause();
	      //playbackEventListener.isPlaying = false;
	    }
	}

	
	/*@Override
	public void onRestart() {
		//playbackEventListener.currMillis = player.getCurrentTimeMillis();
		Time now = new Time();
		now.setToNow();
			
		super.onRestart();
		//if(player.getCurrentTimeMillis() != playbackEventListener.currMillis) 
		int seekDiff = playbackEventListener.updateSeek(now);
		
		player.seekToMillis(playbackEventListener.currMillis + seekDiff);
		if(playbackEventListener.isPlaying) 
			player.play();
		tToast("" + playbackEventListener.currMillis);
	}@Override
	public void onResume() {
		super.onResume();
		if(player.getCurrentTimeMillis() != currMillis) 
			player.seekToMillis(currMillis);
		if(isPlaying) player.play();
		tToast("onResume");
	}*/
	
	
	
	@Override
	public void onPause() {
		super.onPause();
		th.run();
		tToast("onPause");
	}
	
	@Override
	public void onStop() {
		super.onStop();
		th.run();
		tToast("onStop");
	}
	
  	
private void tToast(String s) {
    Context context = getApplicationContext();
    int duration = Toast.LENGTH_SHORT;
    Toast toast = Toast.makeText(context, s, duration);
    toast.show();
}




	@Override
	public void onInitializationSuccess(Provider arg0, YouTubePlayer arg1,
			boolean arg2) {
		// TODO Auto-generated method stub
		player = arg1;
		restored = arg2;
		playbackEventListener = new MyPlaybackEventListener();
		player.setPlayerStyle(PlayerStyle.DEFAULT);
	    player.setPlaybackEventListener(playbackEventListener);
		VideoLoader vl = new VideoLoader(player, id, restored, playbackEventListener);
		th = new Thread(vl);
		th.setPriority(th.MAX_PRIORITY);
		th.start();
		arg1 = null;
		
	}




	@Override
	protected Provider getYouTubePlayerProvider() {
		return (YouTubePlayerView) findViewById(R.id.youtube_view);
	}
	
	class MyPlaybackEventListener implements PlaybackEventListener {
	    public boolean isPlaying = false;
	    public int currMillis = 0;
	    public Time updateTime = new Time();
	    @Override
	    public void onPlaying() {
	    	currMillis = player.getCurrentTimeMillis();
	    	isPlaying = true;
	    	
	    	tToast("playing");
	    }

	    @Override
	    public void onBuffering(boolean isBuffering) {
	    	//currMillis = player.getCurrentTimeMillis();
	    }

	    @Override
	    public void onStopped() {
	    	currMillis = player.getCurrentTimeMillis();
	    	
	    	//isPlaying = false;
	    	//tToast("stopped");
	    }

	    @Override
	    public void onPaused() {
	    	//currMillis = player.getCurrentTimeMillis();
	    	//tToast(""+currMillis);
	    	//isPlaying = false;
	    	//tToast("paused");
	    }

	    @Override
	    public void onSeekTo(int endPositionMillis) {
	    	currMillis = endPositionMillis;
	    }
	    
	    public int updateSeek(Time now) {
	    		updateTime = now;
	    		return (int) (1000 * updateTime.toMillis(false) - 1000 * now.toMillis(false));
	    }
	    
	  }
	
}



