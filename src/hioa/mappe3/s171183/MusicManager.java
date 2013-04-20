package hioa.mappe3.s171183;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;

/**
 *  This class uses both discogs and last.fm, hence the semi-repetitive methods.
 * 	@author kat
 *
 */
public class MusicManager {
	
	private static final String UPC_SEARCH =  "http://api.discogs.com/database/search?q=";
	private static final String LASTFM_API_KEY = "5c8eba7251df40a7f941aa51d2b9a8dc";
	private static final String LASTFM_SEARCH = "http://ws.audioscrobbler.com/2.0/?method=album.getinfo&api_key=" + LASTFM_API_KEY; 
	private static final String LASTFM_ARTIST_QUERY ="&artist=";
	private static final String LASTFM_ALBUM_QUERY = "&album=";
	private static final String LASTFM_FORMAT = "&format=json";
	
    private static final String CALLBACK = "&callback=";
	
	public static HashMap<String,String> searchByUPC(String upcCode) throws InterruptedException, ExecutionException, JSONException {
		HashMap<String, String> results = new HashMap<String, String>();
		String result = new GetResultsByUPC().execute(UPC_SEARCH + upcCode + CALLBACK).get();
		JSONObject json = new JSONObject(result);
		JSONArray resultArray = json.getJSONArray("results");
		JSONObject resultObject = new JSONObject(resultArray.getString(0));
		results.put("albumArt", resultObject.getString("thumb"));
		results.put("resourceURL", resultObject.getString("resource_URL"));
		
		return results;	
	}
	
	public static HashMap<String,String> searchByArtistAndAlbum(String artistName, String albumTitle) {
		return null;
		
	}
	
	
	public static Drawable getAlbumThumb(String url) throws InterruptedException, ExecutionException{
		return new GetAlbumThumb().execute(url).get();
	}
	
	public static ArrayList<TreeMap<?, String>> getAlbumDetails(String url) throws InterruptedException, ExecutionException{
		return new GetAlbumDetails().execute(url).get();
	}

	/* --- ASYNC TASKS --- */
	private static class GetResultsByUPC extends AsyncTask<String, String, String>{

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
	}
	
	private static class GetAlbumThumb extends AsyncTask<String,String,Drawable>{
		@SuppressWarnings("deprecation")
		@Override
		protected Drawable doInBackground(String... urls){
			URL url;        
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
	}
	
	private static class GetAlbumDetails extends AsyncTask<String, String, ArrayList<TreeMap<?,String>>>{
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
	}
}
