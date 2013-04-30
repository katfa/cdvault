package hioa.mappe3.s171183;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class TrackList extends Activity {
	private TrackListAdapter listAdapter;
	private ArrayList<Track> tracks;
	private Album album;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_track_list);

		DBAdapter dbAdapter = new DBAdapter(getBaseContext());
		dbAdapter.open();

		album = (Album) getIntent().getSerializableExtra("album");
		tracks = dbAdapter.getTracks(album.getId());

		TextView albumTitle = (TextView) findViewById(R.id.albumTitle);
		albumTitle.setText(album.getTitle() + " tracks");
		
		Typeface tf = Typeface.createFromAsset(getBaseContext().getAssets(),
				"fonts/Roboto-Thin.ttf");
		albumTitle.setTypeface(tf);
		
		listAdapter = new TrackListAdapter(getBaseContext(),
				R.layout.track_list_row, tracks);
		ListView trackView = (ListView) findViewById(R.id.trackList);
		trackView.setAdapter(listAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.track_list, menu);
		return true;
	}

	private class TrackListAdapter extends ArrayAdapter<Track> {
		private Context context;
		private ArrayList<Track> tracks;

		public TrackListAdapter(Context context, int resource,
				ArrayList<Track> tracks) {
			super(context, resource, tracks);
			this.context = context;
			this.tracks = tracks;
			
			sortByTrackNumber(tracks);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View row = inflater.inflate(R.layout.track_list_row, parent, false);
			Typeface tf = Typeface.createFromAsset(context.getAssets(),
					"fonts/Roboto-Thin.ttf");
			
			TextView trackNumber = (TextView) row.findViewById(R.id.track_number);
			trackNumber.setTypeface(tf);
			trackNumber.setText(tracks.get(position).getTrackNumber());
			
			TextView trackTitle = (TextView) row.findViewById(R.id.track_title);
			trackTitle.setTypeface(tf);
			trackTitle.setText(tracks.get(position).getTitle());

			return row;

		}

		@Override
		public int getCount() {
			return tracks.size();
		}

		private void sortByTrackNumber(ArrayList<Track> tracks){
			Collections.sort(tracks, new Comparator<Track>(){

				@Override
				public int compare(Track track1, Track track2) {
					int trackNumber1 = Integer.parseInt(track1.getTrackNumber());
					int trackNumber2 = Integer.parseInt(track2.getTrackNumber());
					if(trackNumber1 < trackNumber2) return -1;
					else return 1;
				}
				
			});
		}

	}

}
