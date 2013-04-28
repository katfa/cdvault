package hioa.mappe3.s171183;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
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
	
	public EditDeleteDialog(){}
	public EditDeleteDialog(Artist artist, ArtistFragment artistFragment){
		this.artist = artist;
		this.artistFragment = artistFragment;
	}
	
	public EditDeleteDialog(Album album, AlbumListFragment albumListFragment){
		this.album = album;
		this.albumListFragment = albumListFragment;
	}
	
	public Dialog onCreateDialog(Bundle savedInstanceState){
		if(albumListFragment != null){
			String title = album.getTitle();
			AlertDialog.Builder builder = new AlertDialog.Builder(albumListFragment.getActivity());
			builder.setTitle(getString(R.string.dialog_title));
			final ArrayAdapter<String> arrayAdapter =  new ArrayAdapter<String>(albumListFragment.getActivity(), android.R.layout.select_dialog_item);
			arrayAdapter.add("View tracks");
			arrayAdapter.add("Edit");
			arrayAdapter.add("Delete");
			
			builder.setNegativeButton("Cancel", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			
			builder.setAdapter(arrayAdapter, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int arg1) {
					Toast.makeText(albumListFragment.getActivity().getBaseContext(), "You selected " + arrayAdapter.getItem(arg1), Toast.LENGTH_SHORT).show();
				}
			});
			
			return builder.create();
					
		} 
		
		if(artistFragment != null){
			String name = artist.getName();
			AlertDialog.Builder builder = new AlertDialog.Builder(artistFragment.getActivity());
			builder.setTitle(getString(R.string.dialog_title));
			final ArrayAdapter<String> arrayAdapter =  new ArrayAdapter<String>(artistFragment.getActivity(), android.R.layout.select_dialog_item);
			arrayAdapter.add("View Albums");
			arrayAdapter.add("Edit");
			arrayAdapter.add("Delete");
			
			builder.setNegativeButton("Cancel", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			
			builder.setAdapter(arrayAdapter, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int arg1) {
					Toast.makeText(artistFragment.getActivity().getBaseContext(), "You selected " + arrayAdapter.getItem(arg1), Toast.LENGTH_SHORT).show();
				}
			});
			
			return builder.create();
					
		}
		return null;
	}
}
