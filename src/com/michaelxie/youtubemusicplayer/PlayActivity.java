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

import com.michaelxie.youtubemusicplayer.R;












import android.text.format.Time;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.annotation.SuppressLint;
import android.app.Activity;
//import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
	int width, height; 
	String itemName;
	WebView wv;
	
	public VideoLoader(WebView view, int w1, int h1, String item) {
		wv = view;
		width = w1;
		height = h1;
		itemName = item;
	}
	public void loadVideo() {
		  try {
		        wv.loadDataWithBaseURL("",
		        "<html><body><iframe class=\"youtube-player\" type=\"text/html5\" width=\""
		        + (width - 20)
		        + "\" height=\""
		        + height
		        + "\" src=\""
		        + itemName
		        + "\" frameborder=\"0\"\"controls onclick=\"this.play()\">\n</iframe></body></html>",
		                                "text/html", "UTF-8", "");
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
	  }   

	@Override
	public void run() {
		// TODO Auto-generated method stub
		loadVideo();
	}
	
}

public class PlayActivity extends Activity{//extends YouTubeFailureRecoveryActivity implements View.OnClickListener{
	private String id;
	private String embed_url = "http://www.youtube.com/embed/";
	private WebView wv;
	private VideoView video;
	Thread videoThread;
	private myWebChromeClient chromeClient = new myWebChromeClient();
  @SuppressLint("SetJavaScriptEnabled")
@Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.playerview);
 
    wv = (WebView) findViewById(R.id.webview);
  
	String oldId = id;
	 if(oldId != null) Log.i("PLAYACTIVITYONCREATE", oldId);
	id = getIntent().getExtras().getString("id");
	  Log.i("PLAYACTIVITYONCREATE", id);
	if( oldId == null || (!oldId.equals("curr") && !oldId.equals(id))) {	
		  setUpVideo();
	}       
	
	
  }
  
  @Override
  protected void onNewIntent(Intent intent) {
	  String oldId = id;
	 Log.i("PLAYACTIVITY", oldId);
	  id = intent.getExtras().getString("id");
	  if(oldId == null || (!oldId.equals("curr") && !oldId.equals(id))) {	
		  setUpVideo();
	  } 
		
  }
  
  private void setUpVideo() {
	  String item = embed_url + id;
	    DisplayMetrics metrics = getResources().getDisplayMetrics();
	    int w1 = (int) (metrics.widthPixels / metrics.density), h1 = w1 * 3 / 5;
	    
	    wv.setWebChromeClient(chromeClient);
	    wv.getSettings().setJavaScriptEnabled(true);
	    wv.getSettings().setPluginState(WebSettings.PluginState.ON);
	    wv.setHorizontalScrollBarEnabled(false);
	    wv.setVerticalScrollBarEnabled(false);
	    VideoLoader vl = new VideoLoader(wv, w1, h1, item);
	    if(videoThread != null) {
	    	videoThread.stop();
	    	try {
				videoThread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    
	    videoThread = new Thread(vl);
	    videoThread.start();
  }
  
  private class myWebChromeClient extends WebChromeClient implements OnCompletionListener {

	    @Override
	    public void onShowCustomView(View view, CustomViewCallback callback) {
	        super.onShowCustomView(view, callback);
	        if (view instanceof FrameLayout) {
	            FrameLayout frame = (FrameLayout) view;
	            if (frame.getFocusedChild() instanceof VideoView) {
	                video = (VideoView) frame.getFocusedChild();
	                frame.removeView(video);
	                video.start();
	                tToast("Got the video object");
	            }
	        }
	    }

		@Override
		public void onCompletion(MediaPlayer mp) {
			// TODO Auto-generated method stub
			videoThread.stop();
			try {
				videoThread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};
	
	@Override
	public void onResume() {
		super.onResume();
		
	}
	
	@Override
	public void onPause() {
		this.moveTaskToBack(true);
		super.onPause();
	}
	
  /*@Override
  public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player,
      boolean wasRestored) {
	  this.player = player;
	  player.setPlayerStyle(PlayerStyle.DEFAULT);
	  player.setPlaybackEventListener(playbackEventListener);
	  if (!wasRestored) {
	    player.cueVideo(id);
	  }
  }

  @Override
  protected YouTubePlayer.Provider getYouTubePlayerProvider() {
    return (YouTubePlayerView) findViewById(R.id.youtube_view);
  }

@Override
	public void onClick(View v) {
	    if (v == playButton) {
	      player.play();
	    } else if (v == pauseButton) {
	      player.pause();
	      playbackEventListener.isPlaying = false;
	    }
	  }*/

	
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
	}*/
	
	/*@Override
	public void onResume() {
		super.onResume();
		if(player.getCurrentTimeMillis() != currMillis) 
			player.seekToMillis(currMillis);
		if(isPlaying) player.play();
		tToast("onResume");
	}
	
	@Override
	public void onPause() {
		playbackEventListener.currMillis = player.getCurrentTimeMillis();
		playbackEventListener.updateTime.setToNow();
		super.onPause();
		//tToast("onPause");
		if(playbackEventListener.isPlaying || playbackEventListener.currMillis == 0) 
			player.play();
	}
	
	@Override
	public void onStop() {
		playbackEventListener.currMillis = player.getCurrentTimeMillis();
		super.onStop();
		//tToast("onStop");
		//if(isPlaying || currMillis == 0) 
		if(playbackEventListener.isPlaying || playbackEventListener.currMillis == 0) 
			player.play();
	}
	
	
	public void onDestroy() {
		super.onStop();
		//tToast("onDestroy");
		player.play();
	}*/
  	

  	
	private void tToast(String s) {
	    Context context = getApplicationContext();
	    int duration = Toast.LENGTH_SHORT;
	    Toast toast = Toast.makeText(context, s, duration);
	    toast.show();
	}
	
	/*private final class MyPlaybackEventListener implements PlaybackEventListener {
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
	*/
}


