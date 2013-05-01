package hioa.mappe3.s171183;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.widget.ListView;
import android.widget.TextView;

public class ConcertsByArtist extends Activity {
	
	private Artist artist;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_concerts_by_artist);
		Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Thin.ttf");
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		
		artist = (Artist) getIntent().getSerializableExtra("artist");
		
		TextView title = (TextView) findViewById(R.id.concertsTitle);
		title.setTypeface(tf);
		
		String city = sharedPrefs.getString("city", ""); 
		if(city.equals("")){
			title.setText("Set a location by pressing the menu button.");
		} else {
			title.setText(artist.getName() + "'s upcoming concerts in " + city);
			ArrayList<Concert> concerts = new ArrayList<Concert>();
			try {
				concerts = ConcertManager.getConcertsByArtist(artist.getName(), sharedPrefs.getString("metroId", ""));
			} catch (InterruptedException e) {
				Log.e("ERROR", "interrupted " + e.getMessage());
			} catch (ExecutionException e) {
				Log.e("ERROR", "execution " + e.getMessage());
			}
			ConcertListAdapter concertListAdapter = new ConcertListAdapter(getBaseContext(), R.layout.concert_row_layout, concerts);
			
			ListView concertsList = (ListView) findViewById(R.id.artistsConcerts);
			concertsList.setAdapter(concertListAdapter);
		}

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
}
