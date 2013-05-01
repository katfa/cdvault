package hioa.mappe3.s171183;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.ArrayAdapter;
import android.widget.Toast;

@SuppressLint("ValidFragment")
public class EditDeleteDialog extends DialogFragment {
	private Artist artist;
	private ArtistFragment artistFragment;
	private Album album;
	private AlbumListFragment albumListFragment;

	private final String EDIT = "Edit", DELETE = "Delete",
			VIEW_ALBUMS = "View Albums", VIEW_TRACKS = "View Tracks",
			VIEW_CONCERTS = "View concerts in my city";

	public EditDeleteDialog() {
	}

	public EditDeleteDialog(Artist artist, ArtistFragment artistFragment,
			AlbumListFragment albumListFragment) {
		this.artist = artist;
		this.artistFragment = artistFragment;
		this.albumListFragment = albumListFragment;
	}

	public EditDeleteDialog(Album album, AlbumListFragment albumListFragment) {
		this.album = album;
		this.albumListFragment = albumListFragment;
	}

	public Dialog onCreateDialog(Bundle savedInstanceState) {

		if (artistFragment != null && albumListFragment != null) {
			AlertDialog.Builder builder = new AlertDialog.Builder(
					artistFragment.getActivity());
			builder.setTitle(getString(R.string.dialog_title));
			final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
					artistFragment.getActivity(),
					android.R.layout.select_dialog_item);
			arrayAdapter.add(VIEW_CONCERTS);
			arrayAdapter.add(VIEW_ALBUMS);
			arrayAdapter.add(EDIT);
			arrayAdapter.add(DELETE);

			builder.setNegativeButton("Cancel", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});

			builder.setAdapter(arrayAdapter, new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int arg1) {
					if(arrayAdapter.getItem(arg1).equals(VIEW_CONCERTS)){
						Intent intent = new Intent(getActivity(), ConcertsByArtist.class);
						intent.putExtra("artist", artist);
						startActivity(intent);
					}
					
					if (arrayAdapter.getItem(arg1).equals(VIEW_ALBUMS)) {
						Intent intent = new Intent(getActivity(),
								ArtistsAlbumList.class);
						intent.putExtra("artist", artist);
						startActivity(intent);
					}

					if (arrayAdapter.getItem(arg1).equals(EDIT)) {
						Intent intent = new Intent(getActivity(),
								EditArtist.class);
						intent.putExtra("artist", artist);
						startActivity(intent);
					}

					if (arrayAdapter.getItem(arg1).equals(DELETE)) {
						DBAdapter db = artistFragment.getDbAdapter();
						// Also delete all albums from this artist
						ArrayList<Album> artistsAlbums = artistFragment.getDbAdapter().getAlbumsByArtist(artist.getId());
						
						for(Album a : artistsAlbums){
							albumListFragment.getAlbumListAdapter().remove(a);
						}
						
						artistFragment.getArtistAdapter().remove(artist);
						db.deleteArtist(artist.getId());
						artistFragment.updateList(artistFragment.getDbAdapter()
								.getAllArtists());

						
						albumListFragment.updateList(artistFragment
								.getDbAdapter().getAllAlbums());

						Toast.makeText(
								getActivity(),
								artist.getName()
										+ " and all their albums deleted.",
								Toast.LENGTH_LONG).show();
					}

				}
			});

			return builder.create();
		}

		if (albumListFragment != null) {
			AlertDialog.Builder builder = new AlertDialog.Builder(
					albumListFragment.getActivity());
			builder.setTitle(getString(R.string.dialog_title));
			final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
					albumListFragment.getActivity(),
					android.R.layout.select_dialog_item);
			arrayAdapter.add(VIEW_TRACKS);
			arrayAdapter.add(EDIT);
			arrayAdapter.add(DELETE);

			builder.setNegativeButton("Cancel", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});

			builder.setAdapter(arrayAdapter, new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int arg1) {
					if (arrayAdapter.getItem(arg1).equals(VIEW_TRACKS)) {
						Intent intent = new Intent(getActivity(),
								TrackList.class);
						intent.putExtra("album", album);
						startActivity(intent);
					};
				}
			});

			return builder.create();

		}

		return null;
	}
}
