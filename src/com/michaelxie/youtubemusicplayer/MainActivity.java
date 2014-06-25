package com.michaelxie.youtubemusicplayer;


import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;



public class MainActivity extends Activity {
	public final String TAG = "MainActivity";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//ActionBar actionbar = getActionBar();
		
		//button1 = (Button) findViewById(R.id.button1);
		//button1.setOnClickListener(this);
	
		//View view = getLayoutInflater().inflate(R.menu.main, null);
		//actionbar.setCustomView(view);
		
		/*PackageInfo packageInfo = null;
	    PackageManager pm = getPackageManager();
	    try {
	      packageInfo = pm.getPackageInfo(getPackageName(), PackageManager.GET_ACTIVITIES | PackageManager.GET_META_DATA);

	    } catch (NameNotFoundException e) {
	      Log.e(TAG, "Could not find package with name " + getPackageName());
	      finish();
	    }*/
	    
	    /*for(ActivityInfo info : packageInfo.activities) {
	    	Log.i(TAG, info.name);
	    }
	    Log.i(TAG, "" + packageInfo.activities.length);*/
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
	    if (item.getItemId() == R.id.action_search) {
	    	onSearchRequested();
	    }
	    return true;
	}
}
	

	//public void clickEvent(View v) {
		/*if(v.getId() == R.id.button1) {
			Intent intent = new Intent();
			intent.setComponent(new ComponentName(getPackageName(), "com.michaelxie.youtubemusicplayer.PlayActivity"));
			try {
				intent.putExtra("id", "M0U73NRSIkw");
				startActivity(intent);		
			}
			catch(ActivityNotFoundException e) {
				Log.i(TAG, "ActivityNotFound");
				finish();
			}
		}*/
		
		/*if(v == searchView1) {
			Intent intent = new Intent();
			intent.putExtra("QUERY", (String)searchView1.getQuery());
			String query = (String)searchView1.getQuery();
			if(!query.equals("")) {
				String result = callWebService(query);
			}
		}*/
		
	//}
	
	/*public String callWebService(String q){  
        HttpClient httpclient = new DefaultHttpClient();  
        HttpGet request = new HttpGet(URL + q); 
        String result = "";
        ResponseHandler<String> handler = new BasicResponseHandler();  
        try {  
            result = httpclient.execute(request, handler);  
        } catch (ClientProtocolException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        httpclient.getConnectionManager().shutdown();  
        Log.i(TAG, result); 
        return result;
    }*/
//}

