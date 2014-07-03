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

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;


public class PlayActivity extends Activity{//extends YouTubeFailureRecoveryActivity implements View.OnClickListener{
	private String id;
	private String embed_url = "http://www.youtube.com/embed/";
	private WebView wv;
	private VideoView video;
	private TextView tv;
	private myWebChromeClient chromeClient = null;
	private View mCustomView;
	private RelativeLayout mContentView;
	private FrameLayout mCustomViewContainer;
	private WebChromeClient.CustomViewCallback mCustomViewCallback;
  @SuppressLint("SetJavaScriptEnabled")
@Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.playerview);
    setupActionBar(); // Show the Up button in the action bar.
    wv = (WebView) findViewById(R.id.webview);
    chromeClient = new myWebChromeClient();
    wv.setWebChromeClient(chromeClient);
    
    AdView adView = (AdView)this.findViewById(R.id.adView);
    AdRequest adRequest = new AdRequest.Builder().build();
    adView.loadAd(adRequest);
    
	String oldId = id;
	if(oldId != null) Log.i("PLAYACTIVITYONCREATE", oldId);
	id = getIntent().getExtras().getString("id");
	  Log.i("PLAYACTIVITYONCREATE", id);
	if( oldId == null || (!oldId.equals("curr") && !oldId.equals(id))) {	
		  setUpVideo();
	}       
	tv = (TextView) findViewById(R.id.descriptionView);
    tv.setText(getIntent().getExtras().getString("desc"));
    tv.setMovementMethod(new ScrollingMovementMethod());
	
  }
  
  private void setupActionBar() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getItemId()) {
          case android.R.id.home:
        	  this.moveTaskToBack(true);
              return true;
          default:
              return super.onOptionsItemSelected(item);
      }
  }
  @Override
  protected void onNewIntent(Intent intent) {
	  String oldId = id;
	  Log.i("PLAYACTIVITY", oldId);
	  id = intent.getExtras().getString("id");
	  setupActionBar();
	  tv.setText(intent.getExtras().getString("desc"));
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
	    wv.setPadding(0, 0, 0, 0);
	    wv.getSettings().setPluginState(PluginState.OFF);
	    wv.setHorizontalScrollBarEnabled(false);
	    wv.setVerticalScrollBarEnabled(false);
	    wv.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
	    try {
	        wv.loadDataWithBaseURL("",
	        "<html><body><iframe class=\"youtube-player\" type=\"text/html5\" width=\""
	        + (w1 - 20)
	        + "\" height=\""
	        + h1
	        + "\" src=\""
	        + item
	        + "\" frameborder=\"0\"\"controls onclick=\"this.play()\">\n</iframe></body></html>",
	                                "text/html", "UTF-8", "");
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
  }
  
  private class myWebChromeClient extends WebChromeClient implements OnCompletionListener {
	  FrameLayout.LayoutParams LayoutParameters = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
	            FrameLayout.LayoutParams.MATCH_PARENT);
	    @Override
	    public void onShowCustomView(View view, CustomViewCallback callback) {
	    	callback.onCustomViewHidden();
	    	tToast("Sorry! Fullscreen is not available. Click the video twice to resume video.");
	    	// if a view already exists then immediately terminate the new one
	    	/*if (mCustomView != null) {
	            callback.onCustomViewHidden();
	            return;
	        }
	    	mContentView = (RelativeLayout) findViewById(R.id.RelativeLayout1);
	        mContentView.setVisibility(View.GONE);
	        mCustomViewContainer = new FrameLayout(PlayActivity.this);
	        mCustomViewContainer.setLayoutParams(LayoutParameters);
	        mCustomViewContainer.setBackgroundResource(android.R.color.black);
	        view.setLayoutParams(LayoutParameters);
	        mCustomViewContainer.addView(view);
	        mCustomView = view;
	        mCustomViewCallback = callback;
	        mCustomViewContainer.setVisibility(View.VISIBLE);
	        setContentView(mCustomViewContainer);
	        */
	    	/*
	        super.onShowCustomView(view, callback);
	        if (view instanceof FrameLayout) {
	            FrameLayout frame = (FrameLayout) view;
	            if (frame.getFocusedChild() instanceof VideoView) {
	                video = (VideoView) frame.getFocusedChild();
	                frame.removeView(video);
	                video.start();
	                tToast("Got the video object");
	            }
	        }*/
	    }
	    @Override
	    public void onHideCustomView() {
	        if (mCustomView == null) {
	            return;
	        } else {
	            // Hide the custom view.  
	            mCustomView.setVisibility(View.GONE);
	            // Remove the custom view from its container.  
	            mCustomViewContainer.removeView(mCustomView);
	            mCustomView = null;
	            mCustomViewContainer.setVisibility(View.GONE);
	            mCustomViewCallback.onCustomViewHidden();
	            // Show the content view.  
	            mContentView.setVisibility(View.VISIBLE);
	            setContentView(mContentView);
	        }
	    }
	    
		@Override
		public void onCompletion(MediaPlayer mp) {
			//Play looping
			mp.start();
		}
	};
	
	@Override
	public void onResume() {
		super.onResume();
		
	}
	
	@Override
	public void onPause() {
		//this.moveTaskToBack(true);
		super.onPause();
	}
	
	@Override
	public void onBackPressed() {
	    	this.moveTaskToBack(true);
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
	
}


