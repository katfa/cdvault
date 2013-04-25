package hioa.mappe3.s171183;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;

import org.json.JSONException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {
	private HashMap<String,String> results = new HashMap<String, String>();
	private Drawable albumArt;
	private ArrayList<TreeMap<?, String>> albumDetails =  new ArrayList<TreeMap<?,String>>();

	private DBAdapter dbAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		dbAdapter = new DBAdapter(this);
		dbAdapter.open();
		

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
					results = MusicManager.searchByUPC(upcCode);
					setAlbumArt(fetchAlbumArt(results.get("albumArt")));
					fetchAlbumDetails(results.get("resourceURL"));
					showAlbumDetails(albumDetails.get(0));
					showTracks(albumDetails.get(1));
					showSaveButton();
					
				} catch (InterruptedException e) {
					Log.e("ERROR", e.getMessage());
				} catch (ExecutionException e) {
					Log.e("ERROR", e.getMessage());
				} catch (JSONException e) {
					Log.e("ERROR", e.getMessage());
				}
			} 
		}
	}
	
	private void showSaveButton(){
		Button saveButton = (Button) findViewById(R.id.saveAlbum);
		saveButton.setVisibility(View.VISIBLE);
		saveButton.setText("Save to my collection");
		saveButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// add artist, add album, add tracks
			}
		});
	}
	
	private Drawable fetchAlbumArt(String url) throws InterruptedException, ExecutionException{
		return MusicManager.getAlbumThumb(url);
	}
	
	@SuppressLint("NewApi")
	public void setAlbumArt(Drawable drawable){
		ImageView albumArt = (ImageView)findViewById(R.id.albumArt);
		albumArt.setBackground(drawable);
	}
	
	private void fetchAlbumDetails(String url) throws InterruptedException, ExecutionException{
		albumDetails = MusicManager.getAlbumDetails(url);
	}
	
	public void showAlbumDetails(TreeMap<?, String> treeMap){
		TextView t = (TextView)findViewById(R.id.album);
		String info = "";
		for (Entry<?, String> pair : treeMap.entrySet()){
			info += pair.getKey() + ": " + pair.getValue() + "\n";
		}
		
		t.setText(info);
	}

	public void showTracks(TreeMap<?,String> tracks){
		TextView t = (TextView)findViewById(R.id.tracks);
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
}
