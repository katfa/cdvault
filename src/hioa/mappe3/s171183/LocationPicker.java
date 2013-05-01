package hioa.mappe3.s171183;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class LocationPicker extends Activity {

	private LocationManager locationManager;
	private List<String> providers;
	private Location location;
	private int timeInterval = 0;
	private int distance = 0;

	private double latitude, longitude;
	private String errorMessage;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location_picker);

		Typeface tf = Typeface.createFromAsset(getBaseContext().getAssets(),
				"fonts/Roboto-Thin.ttf");
		TextView locationTitle = (TextView) findViewById(R.id.locationTitle);
		locationTitle.setTypeface(tf);

		getCoordinates();

		try {
			if (latitude != 0.0 || longitude != 0.0)
				disableGPS();
			
			HashMap<String, String> locationSuggestions = ConcertManager
					.getLocationSuggestions(latitude, longitude);
			final ArrayList<String> cities = new ArrayList<String>();
			final ArrayList<String> metroIds = new ArrayList<String>();
			
			for(Map.Entry<String,String> entry : locationSuggestions.entrySet()){
				cities.add(entry.getKey());
			}
			
			for(Map.Entry<String,String> entry : locationSuggestions.entrySet()){
				metroIds.add(entry.getValue());
			}

			ListView locationList = (ListView)findViewById(R.id.locationList);
			locationList.setAdapter(new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, cities));
			
			locationList.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View view,
						int position, long arg3) {
					saveToPreferences(metroIds.get(position), cities.get(position));
				}
				
				private void saveToPreferences(String metroId, String city){
					SharedPreferences sharedPrefs = PreferenceManager
							.getDefaultSharedPreferences(getApplication());
					SharedPreferences.Editor editor = sharedPrefs.edit();
					editor.putString("metroId", metroId);
					editor.putString("city", city);
					editor.commit();
				}
			});
		
		} catch (InterruptedException e) {
			Log.e("ERROR", "Interrupted " + e.getMessage());
		} catch (ExecutionException e) {
			Log.e("ERROR", e.getMessage());
		}

		

		
	}

	private void getCoordinates() {
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		providers = locationManager.getAllProviders();
		String provider = LocationManager.GPS_PROVIDER;

		for (String s : providers) {
			if(locationManager.getLastKnownLocation(s) != null)
			{
				location = locationManager.getLastKnownLocation(s);
				provider = s;
				break;
			}
		}
		
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.NO_REQUIREMENT);
		criteria.setPowerRequirement(Criteria.NO_REQUIREMENT);

		locationManager.requestLocationUpdates(provider, timeInterval,
				distance, myLocationListener);
		
		updateLocation(location);
	}

	private final LocationListener myLocationListener = new LocationListener() {

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

		@Override
		public void onProviderEnabled(String provider) {
			updateLocation(location);
		}

		@Override
		public void onProviderDisabled(String provider) {
		}

		@Override
		public void onLocationChanged(Location location) {
			updateLocation(location);
		}
	};

	private void updateLocation(Location location) {
		latitude = location.getLatitude();
		longitude = location.getLongitude();
	}

	private void disableGPS() {
		final Intent turnOffGPS = new Intent();
		turnOffGPS.setClassName("com.android.settings",
				"com.android.settings.widget.SettingsAppWidgetProvider");
		turnOffGPS.addCategory(Intent.CATEGORY_ALTERNATIVE);
		turnOffGPS.setData(Uri.parse("3"));
		sendBroadcast(turnOffGPS);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.location_picker, menu);
		return true;
	}
	
}
