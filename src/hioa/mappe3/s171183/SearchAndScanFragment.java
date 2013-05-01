package hioa.mappe3.s171183;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;

import org.json.JSONException;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class SearchAndScanFragment extends Fragment {
	private HashMap<String, String> results = new HashMap<String, String>();
	private Drawable albumArt;
	private byte[] albumArtBytes;
	private ArrayList<TreeMap<?, String>> albumDetails = new ArrayList<TreeMap<?, String>>();

	private DBAdapter dbAdapter;
	private View thisFragmentView;
	
	private ArtistFragment artistFragment;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		dbAdapter = new DBAdapter(container.getContext());
		dbAdapter.open();

		thisFragmentView = inflater.inflate(R.layout.search_and_scan,
				container, false);

		ImageButton startScanner = (ImageButton) thisFragmentView
				.findViewById(R.id.startScanner);
		startScanner.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(
						"com.google.zxing.client.android.SCAN");
				intent.putExtra("SCAN_MODE", "PRODUCT_MODE");
				startActivityForResult(intent, 0);
			}
		});
		
		ImageButton search = (ImageButton) thisFragmentView.findViewById(R.id.searchButton);
		search.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				EditText input = (EditText) getActivity().findViewById(R.id.albumCheckInput);
				if(albumExists(input.toString().toLowerCase(Locale.getDefault()))) {
					Toast.makeText(getActivity().getBaseContext(), "You already have this album.", Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(getActivity().getBaseContext(), "You do not have this album yet.", Toast.LENGTH_LONG).show();
				}
				InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(
					      Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
			}
			
			private boolean albumExists(String albumTitle){
				ArrayList<Album> albums = dbAdapter.getAllAlbums();
				for(Album a : albums){
					String lowerCaseTitle = a.getTitle().toLowerCase(Locale.getDefault());
					System.out.println("titles are " + lowerCaseTitle + " and " + albumTitle);
					if(lowerCaseTitle.equals(albumTitle)) return true;
				}
				return false;
			}
		});

		getActivity().getActionBar().setTitle("Search or Scan");
		
		return thisFragmentView;
	}

	public String getFragmentTitle() {
		return "Search or Scan";
	}
	
	public void setArtistFragment(ArtistFragment artistFragment){
		this.artistFragment = artistFragment;
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == 0) {
			if (resultCode == getActivity().RESULT_OK) {
				String upcCode = intent.getStringExtra("SCAN_RESULT");
				Log.d("UPCCODE", upcCode);
				try {
					results = AlbumDetailsManager.searchByUPC(upcCode);
					setAlbumArt(fetchAlbumArt(results.get("Album Art")));
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

	private void showSaveButton() {
		Button saveButton = (Button) thisFragmentView
				.findViewById(R.id.saveAlbum);
		saveButton.setVisibility(View.VISIBLE);
		saveButton.setText(R.string.save);
		saveButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				TreeMap<?, String> details = albumDetails.get(0);

				Artist artist = new Artist(details.get("Artist"));
				if (!dbAdapter.artistExists(artist)) {
					dbAdapter.insertArtist(artist);
				}

				Album album = new Album(details.get("Album Title"), details
						.get("Year"), albumArtBytes, dbAdapter
						.getArtistId(artist));

				if (dbAdapter.albumExists(album)) {
					Toast.makeText(getActivity(),
							album.getTitle() + " is already in the database.",
							Toast.LENGTH_SHORT).show();

				} else {

					dbAdapter.insertAlbum(album);

					// tracks
					details = albumDetails.get(1);
					for (Entry<?, String> pair : details.entrySet()) {
						Track track = new Track(pair.getKey().toString(), pair.getValue(), dbAdapter.getAlbumId(album));
						dbAdapter.insertTrack(track);
					}

					Toast.makeText(getActivity(),
							album.getTitle() + " added to database.",
							Toast.LENGTH_SHORT).show();

				}
				
				artistFragment.updateList(dbAdapter.getAllArtists());
				
			}
		});
	}

	private Drawable fetchAlbumArt(String url) throws InterruptedException,
			ExecutionException {
		albumArtBytes =  AlbumDetailsManager.getAlbumThumb(url); 
		
		InputStream inputStream = new ByteArrayInputStream(albumArtBytes);
		albumArt = Drawable.createFromStream(inputStream, "albumThumb");
		
		// make album art ready to be saved to database
		ByteArrayOutputStream output =  new ByteArrayOutputStream();
		Bitmap image = BitmapFactory.decodeByteArray(albumArtBytes, 0, albumArtBytes.length);
		
		image.compress(CompressFormat.JPEG, 100, output);
		albumArtBytes = output.toByteArray();
	
		return albumArt;
	}

	@SuppressLint("NewApi")
	public void setAlbumArt(Drawable drawable) {
		ImageView albumArt = (ImageView) thisFragmentView
				.findViewById(R.id.albumArt);
		albumArt.setBackground(drawable);
	}

	private void fetchAlbumDetails(String url) throws InterruptedException,
			ExecutionException {
		albumDetails = AlbumDetailsManager.getAlbumDetails(url);
	}

	public void showAlbumDetails(TreeMap<?, String> treeMap) {
		TextView t = (TextView) thisFragmentView.findViewById(R.id.album);
		String info = "";
		for (Entry<?, String> pair : treeMap.entrySet()) {
			info += pair.getKey() + ": " + pair.getValue() + "\n";
		}

		t.setText(info);
	}

	public void showTracks(TreeMap<?, String> tracks) {
		TextView t = (TextView) thisFragmentView.findViewById(R.id.tracks);
		String trackList = "TRACKS: \n";
		for (Entry<?, String> track : tracks.entrySet()) {
			trackList += track.getKey() + " - " + track.getValue() + "\n";
		}

		t.setText(trackList);
	}

}
