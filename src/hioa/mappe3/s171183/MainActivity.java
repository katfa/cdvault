package hioa.mappe3.s171183;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {
	String albumArtURL, resourceURL;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Button startScanner = (Button) findViewById(R.id.startScanner);
		startScanner.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(
						"com.google.zxing.client.android.SCAN");
				intent.putExtra("SCAN_MODE", "PRODUCT_MODE");
				startActivityForResult(intent, 0);
			}
		});
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == 0) {
			if (resultCode == RESULT_OK) {
				String upcCode = intent.getStringExtra("SCAN_RESULT");
				Log.d("UPCCODE", upcCode);
				try {
					sendRequest(upcCode);
				} catch (ClientProtocolException e) {
					Log.d("ERROR", e.getMessage());
				} catch (IOException e) {
					Log.d("ERROR",e.getMessage());
					
				}

			} else if (resultCode == RESULT_CANCELED) {
				// Handle cancel
			}
		}
	}
	
	private void sendRequest(final String upcCode) throws ClientProtocolException, IOException{
		Log.d("REQUEST", "now trying to get info for " + upcCode);
		String result;
		try {
			result = new GetResults().execute("http://api.discogs.com/database/search?q=" + upcCode + "&callback=").get();
			JSONObject json = new JSONObject(result);
			JSONArray resultArray = json.getJSONArray("results");
			JSONObject resultObject = new JSONObject(resultArray.getString(0));
			albumArtURL = resultObject.getString("thumb");
			resourceURL = resultObject.getString("resource_url");
			getAlbumArt(albumArtURL);
			getAlbumDetails(resourceURL);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
    }
	
	private void getAlbumArt(String url){
		new GetAlbumThumb().execute(url);
	}
	
	@SuppressWarnings("deprecation")
	public void setAlbumArt(Drawable drawable){
		ImageView albumArt = (ImageView)findViewById(R.id.albumArt);
		albumArt.setBackgroundDrawable(drawable);
	}
	
	private void getAlbumDetails(String url){
		new GetAlbumDetails().execute(url);
	}
	
	public void showAlbumDetails(TreeMap<?, String> treeMap){
		TextView t = (TextView)findViewById(R.id.upc);
		String info = "";
		for (Entry<?, String> pair : treeMap.entrySet()){
			info += pair.getKey() + ": " + pair.getValue() + "\n";
		}
		
		t.setText(info);
	}


	public void showTracks(TreeMap<?,String> tracks){
		TextView t = (TextView)findViewById(R.id.artist);
		String trackList = "TRACKS: \n";
		for(Entry<?, String> track : tracks.entrySet()){
			trackList += track.getKey() + " - " + track.getValue() + "\n";
		}
		
		t.setText(trackList);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private class GetResults extends AsyncTask<String, String, String>{

		@Override
		protected String doInBackground(String... urls) {
			URL url;
			HttpURLConnection urlConnection;
			try {
				url = new URL(urls[0]);
				urlConnection = (HttpURLConnection) url.openConnection();
				InputStream stream = new BufferedInputStream(urlConnection.getInputStream());
					StringWriter writer = new StringWriter();
					IOUtils.copy(stream,writer);
					return writer.toString();
				
			} catch (MalformedURLException e2) {
				Log.e("ERRor" , e2.getMessage());
			}
			catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	        
	        return "";
			
		}
		
		protected void onPostExecute(){
			Log.d("ASYNC", "done getting results");
		}
	
	}
	
	private class GetAlbumThumb extends AsyncTask<String,String,Drawable>{
		@SuppressWarnings("deprecation")
		@Override
		protected Drawable doInBackground(String... urls){
			 URL url;        
		        BufferedOutputStream outputStream;
		        InputStream inputStream;
		        BufferedInputStream bufferedInput;
		        try {
		            url = new URL(urls[0]);
		            inputStream = url.openStream();

		            bufferedInput = new BufferedInputStream(inputStream);
		            Bitmap bitMap = BitmapFactory.decodeStream(bufferedInput);
		            if (inputStream != null) {
		                inputStream.close();
		            }
		            if (bufferedInput != null) {
		                bufferedInput.close();
		            }

		            return new BitmapDrawable(bitMap);
		        }
		        catch(Exception e){
		        	
		        }
		        return null;
		}
		
		protected void onPostExecute(Drawable thumb){
			setAlbumArt(thumb);
		}
	}
	
	private class GetAlbumDetails extends AsyncTask<String, String, ArrayList<TreeMap<?,String>>>{
		TreeMap<String, String> details = new TreeMap<String,String>();
		TreeMap<Integer, String> tracks = new TreeMap<Integer,String>();

		@Override
		protected ArrayList<TreeMap<?, String>> doInBackground(String... urls) {
			
			URL url;
			HttpURLConnection urlConnection;
			try {
				url = new URL(urls[0]);
				urlConnection = (HttpURLConnection) url.openConnection();
				InputStream stream = new BufferedInputStream(urlConnection.getInputStream());
				StringWriter writer = new StringWriter();
				IOUtils.copy(stream,writer);
				JSONObject json = new JSONObject(writer.toString());
				JSONArray artistInfo = json.getJSONArray("artists");
				JSONArray trackArray = json.getJSONArray("tracklist");
				JSONObject artist = new JSONObject(artistInfo.getString(0));
				
				for(int i = 0; i < trackArray.length(); i++){
					tracks.put(Integer.valueOf(i + 1), trackArray.getJSONObject(i).getString("title"));
				}				
				
				details.put("Artist", (artist.getString("name")));
				details.put("Album Title", json.getString("title"));
				details.put("Year", json.getString("year"));
				
			} catch (MalformedURLException e2) {
				Log.e("ERRor" , e2.getMessage());
			}
			catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        ArrayList<TreeMap<?, String>> albumDetails = new ArrayList<TreeMap<?,String>>();
	        albumDetails.add(details);
	        albumDetails.add(tracks);
	        return albumDetails;
			
		}
		
		protected void onPostExecute( ArrayList<TreeMap<?, String>> albumDetails){
			showAlbumDetails(albumDetails.get(0));
			showTracks(albumDetails.get(1));
		}
		
	}
	

}
