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

import android.os.AsyncTask;
import android.util.Log;

/**
 *  This class uses discogs to get album information.
 * 	@author kat
 *
 */
public class AlbumDetailsManager {
	
	private static final String UPC_SEARCH =  "http://api.discogs.com/database/search?q=";
    private static final String CALLBACK = "&callback=";
	
	public static HashMap<String,String> searchByUPC(String upcCode) throws InterruptedException, ExecutionException, JSONException {
		HashMap<String, String> results = new HashMap<String, String>();
		String result = new GetResultsByUPC().execute(UPC_SEARCH + upcCode + CALLBACK).get();
		JSONObject json = new JSONObject(result);
		JSONArray resultArray = json.getJSONArray("results");
		
		// some UPCs do not include last digit
		if(resultArray.length() == 0){
			result = new GetResultsByUPC().execute(UPC_SEARCH + upcCode.substring(0,  upcCode.length() - 1) + CALLBACK).get(); 
			json = new JSONObject(result);
			resultArray = json.getJSONArray("results");
		} 
		
		JSONObject resultObject = new JSONObject(resultArray.getString(0));
		String thumbnailPath = resultObject.getString("thumb");
		results.put("Album Art", thumbnailPath);
		
		String resourceURL = resultObject.getString("resource_url");
		results.put("resourceURL", resourceURL);
		
		return results;	
	}
	
	public static HashMap<String,String> searchByArtistAndAlbum(String artistName, String albumTitle) {
		return null;
		
	}
	
	
	public static byte[] getAlbumThumb(String url) throws InterruptedException, ExecutionException{
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
				Log.e("ERROR" , "urlexception: " + e2.getMessage());
			}
			catch (IOException e1) {
				Log.e("ERROR", "ioexception: " +  e1.getMessage());
			}
	        
	        return "";
		}
	}
	
	private static class GetAlbumThumb extends AsyncTask<String,String,byte[]>{
		@Override
		protected byte[] doInBackground(String... urls){
			URL url;        
	        InputStream inputStream;
	        BufferedInputStream bufferedInput;
	        try {
	            url = new URL(urls[0]);
	            inputStream = url.openStream();

	            bufferedInput = new BufferedInputStream(inputStream);
	            byte[] albumArtBytes = IOUtils.toByteArray(bufferedInput);
	            		
	            if (inputStream != null) {
	                inputStream.close();
	            }
	            if (bufferedInput != null) {
	                bufferedInput.close();
	            }

	            return albumArtBytes;
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
				Log.e("ERROR" , "url exception: " + e2.getMessage());
			}
			catch (IOException e1) {
				Log.e("ERROR" , e1.getMessage());
			} catch (JSONException e) {
				Log.e("ERROR" , "json problem getting details " + e.getMessage());
			}
	        ArrayList<TreeMap<?, String>> albumDetails = new ArrayList<TreeMap<?,String>>();
	        albumDetails.add(details);
	        albumDetails.add(tracks);
	        return albumDetails;
			
		}
	}
}
