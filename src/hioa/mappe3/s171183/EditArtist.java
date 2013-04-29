package hioa.mappe3.s171183;

import hioa.mappe3.s171183.ArtistFragment.ArtistAdapter;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;
import android.widget.Toast;

public class EditArtist extends FragmentActivity {
	private TextView artistName;
	private Button updateButton;

	private DBAdapter dbAdapter;
	private Artist artist;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_artist);

		dbAdapter = new DBAdapter(getBaseContext());
		artist = (Artist) getIntent().getSerializableExtra("artist");
		artistName = (TextView)findViewById(R.id.artistName);
		artistName.setText(artist.getName());
		
		updateButton = (Button)findViewById(R.id.updateArtistButton);
		updateButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String newName = artistName.getText().toString();
				if (dbAdapter.updateArtist(artist, newName) == 1) {
					Toast.makeText(getApplicationContext(), "Artist updated. Press refresh icon.", Toast.LENGTH_LONG).show();
					finish();
				} else {
					Toast.makeText(getApplicationContext(), "Artist was not updated ", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_artist, menu);
		return true;
	}
}
