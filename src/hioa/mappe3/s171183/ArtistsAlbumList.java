package hioa.mappe3.s171183;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class ArtistsAlbumList extends Activity {
	private Artist artist;
	private ArrayList<Album> albums;
	private DBAdapter dbAdapter;
	private AlbumListAdapter listAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_artists_album_list);
		
		System.out.println("artists album list view");
		dbAdapter = new DBAdapter(this);
		dbAdapter.open();
		
		artist = (Artist) getIntent().getSerializableExtra("artist");
		albums = dbAdapter.getAlbumsByArtist(artist.getId());
		
		TextView viewTitle = (TextView) findViewById(R.id.artistAlbumsTitle);
		viewTitle.setText(artist.getName() + " albums");
		Typeface tf = Typeface.createFromAsset(getBaseContext().getAssets(), "fonts/Roboto-Thin.ttf");
		viewTitle.setTypeface(tf);
		
		listAdapter = new AlbumListAdapter(this, R.layout.album_row_layout, albums);
		
		ListView albumList = (ListView) findViewById(R.id.artistsAlbums);
		albumList.setAdapter(listAdapter);
		
		albumList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,
					long arg3) {

				Intent intent = new Intent(getBaseContext(), TrackList.class);
				intent.putExtra("album", (Album)view.getTag());
				startActivity(intent);
			}
		
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
