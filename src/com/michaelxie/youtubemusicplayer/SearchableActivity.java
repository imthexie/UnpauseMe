package com.michaelxie.youtubemusicplayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.support.v4.app.NavUtils;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.Lists;
//import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.youtube.YouTube;
//import com.google.api.services.youtube.model.ChannelListResponse;
//import com.google.api.services.youtube.model.PlaylistItem;
//import com.google.api.services.youtube.model.PlaylistItemListResponse;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
//import com.google.api.services.youtube.model.Video;
//import com.google.api.services.youtube.model.VideoListResponse;
//import com.google.api.services.youtube.model.VideoSnippet;


public class SearchableActivity extends ListActivity implements OnItemClickListener, OnScrollListener{

	private static final String TAG = "SEARCHABLE";
	
	final HttpTransport transport = AndroidHttp.newCompatibleTransport();
	final JsonFactory jsonFactory = new GsonFactory();
	//public String URL = "https://www.googleapis.com/youtube/v3/videos?search?&key=AIzaSyAo02NXZkIw_veXM_qT73A1s5so5I0UOKY&part=snippet,statistics&type=video&q="; 
	ListView listView;
	List<videoResultItem> resultList;
	String query;
	int numResults;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_searchable); 
		setupActionBar(); // Show the Up button in the action bar.
		resultList = new ArrayList<videoResultItem>();
		numResults = 25;
		handleIntent(getIntent());
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		numResults = 25;
	    setIntent(intent);
	    handleIntent(intent);
	}

	private void handleIntent(Intent intent) {
	    if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
	      query = intent.getStringExtra(SearchManager.QUERY);
	      search(query, numResults);
	    }   
	}
	
	@Override
	  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	    videoResultItem clickedItem = (videoResultItem) resultList.get(position);
	    Intent intent = new Intent(this, PlayActivity.class);
		try {
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			intent.putExtra("id", clickedItem.getId());
			startActivity(intent);		
		}
		catch(ActivityNotFoundException e) {
			System.err.println("Activity Not Found");
			finish();
		}
	}
	
	public class searchTask extends AsyncTask<String, Void, List<videoResultItem>> {
		List<videoResultItem> tempList;
		SearchableActivity activity;
		int numResults;
		public searchTask(SearchableActivity activity, int numResults) {
			super();
			tempList = new ArrayList<videoResultItem>();
			this.activity = activity;
			this.numResults = numResults;
		}
		protected List<videoResultItem> doInBackground(String... query) {
			HttpRequestInitializer initializer = new HttpRequestInitializer() {
																	public void initialize(HttpRequest request) throws IOException {}};		            
			YouTube youtube = new YouTube.Builder(transport, jsonFactory, initializer).setApplicationName("YoutubeMusicPlayer").build();
			try {
				// Define the API request for retrieving search results.
		        YouTube.Search.List searchRequest = youtube.search().list("id,snippet");
		        searchRequest.setKey(DeveloperKey.DEVELOPER_KEY);
		        searchRequest.setQ(query[0]);
		        searchRequest.setType("video");
		        searchRequest.setMaxResults((long) numResults);
		        searchRequest.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)");
		        SearchListResponse searchResponse = searchRequest.execute();
		        List<SearchResult> list = searchResponse.getItems();
		        for(int i = 0; i < list.size(); i++) {
		        	JSONObject resultSnippet = new JSONObject(list.get(i).getSnippet());
		        	try {
		        		videoResultItem item = new videoResultItem(resultSnippet.getString("title"), list.get(i).getId().getVideoId());
		        		tempList.add(item);	
		        		//System.out.println(item.getTitle());
		        	} catch(JSONException e) {
		        		System.err.println("JSONException");
		        	}
		        }
		        
			} catch (GoogleJsonResponseException e) {
		        System.err.println("Oops! Service error: " + e.getDetails().getCode() + " : "
		                + e.getDetails().getMessage());
		    } catch (IOException e) {
		    	System.err.println("Oops! IO error: " + e.getCause() + " : " + e.getMessage());				
		    } 
			return tempList;
		}
		 @Override
		protected void onPostExecute(List<videoResultItem> result) {
			 listView = activity.getListView();
			 DemoArrayAdapter adapter = new DemoArrayAdapter(activity, R.layout.list_item, result);
			 listView.setAdapter(adapter);
			 listView.setOnItemClickListener(activity);
			 listView.setOnScrollListener(activity);
			 activity.resultList = result;
		}
		
	}
	
	private void search(final String query, int numResults) {
		searchTask task = new searchTask(this, numResults);
		task.execute(query);
	}
	
	@Override
	public void onScroll(AbsListView lw, final int firstVisibleItem,
	                 final int visibleItemCount, final int totalItemCount) {

	    switch(lw.getId()) {
	        case android.R.id.list:     

	            // Make your calculation stuff here. You have all your
	            // needed info from the parameters of this function.

	            // Sample calculation to determine if the last 
	            // item is fully visible.
	             final int lastItem = firstVisibleItem + visibleItemCount;
	           if(lastItem == totalItemCount && totalItemCount == numResults && numResults < 50) {
	              search(query, 50);
	           }
	    }
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		case R.id.action_search: onSearchRequested(); return true;
	    	
		}
		return super.onOptionsItemSelected(item);
	}
	  
	private final class videoResultItem implements ListViewItem {
		String title;
		String ID;
		public videoResultItem(String title1, String ID1) {
			title = title1;
			ID = ID1;
		}
		public String getId() {
			return ID;
		}
		
		public String getTitle() {
			return title;
		}
	}
	
	/**
	 * A convenience class to make ListViews easier to use in the demo activities.
	 */
	public final class DemoArrayAdapter extends ArrayAdapter<videoResultItem> {

	  private final LayoutInflater inflater;

	  public DemoArrayAdapter(Context context, int textViewResourceId, List<videoResultItem> objects) {
	    super(context, textViewResourceId, objects);
	    inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	  }

	  @Override
	  public View getView(int position, View view, ViewGroup parent) {
	    if (view == null) {
	      view = inflater.inflate(R.layout.list_item, null);
	    }
	    TextView textView = (TextView) view.findViewById(R.id.list_item_text);
	    textView.setText(getItem(position).getTitle());
	    return view;
	  }

	}

	@Override
	public void onScrollStateChanged(AbsListView arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

}
