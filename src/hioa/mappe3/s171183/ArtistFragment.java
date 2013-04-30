package hioa.mappe3.s171183;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ArtistFragment extends Fragment{
	private ArrayList<Artist> allArtists;
	public ArrayAdapter<Artist> artistAdapter;
	public DBAdapter dbAdapter;
	private ListView artistsList;

	private View thisFragmentView;
	private FragmentManager fManager;
	
	private AlbumListFragment albumListFragment;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d("listfragment", " created ");
		super.onCreate(savedInstanceState);
		thisFragmentView = inflater.inflate(R.layout.artists, container, false);
		fManager = this.getFragmentManager();

		dbAdapter = new DBAdapter(getActivity().getBaseContext());
		dbAdapter.open();
		

		allArtists = dbAdapter.getAllArtists();

		artistAdapter = new ArtistAdapter(getActivity().getBaseContext(),
				R.layout.artist_row_layout, allArtists);
	
		artistAdapter.notifyDataSetChanged();
		artistsList = (ListView) thisFragmentView
				.findViewById(R.id.artist_list);
		artistsList.setAdapter(artistAdapter);

		artistsList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				EditDeleteDialog dialog = new EditDeleteDialog((Artist) view
						.getTag(), ArtistFragment.this, albumListFragment);
				dialog.show(fManager, "EditDeleteDialog");
			}
		});

		return thisFragmentView;

	}

	public void updateList(ArrayList<Artist> artists) {
		((ArtistAdapter) artistAdapter).updateList(artists);
		artistAdapter.notifyDataSetChanged();
	}

	public ArtistAdapter getArtistAdapter() {
		return (ArtistAdapter) this.artistAdapter;
	}

	public DBAdapter getDbAdapter() {
		return this.dbAdapter;
	}
	
	public void setAlbumListFragment(AlbumListFragment albumListFragment){
		this.albumListFragment = albumListFragment;
	}

	public class ArtistAdapter extends ArrayAdapter<Artist> {
		private Context context;
		public ArrayList<Artist> artists;

		public ArtistAdapter(Context context, int textViewResourceId,
				ArrayList<Artist> artists) {
			super(context, textViewResourceId, artists);
			this.context = context;
			this.artists = artists;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View row = inflater.inflate(R.layout.artist_row_layout, parent,
					false);
				TextView artistName = (TextView) row
						.findViewById(R.id.artist_name);
				Typeface tf = Typeface.createFromAsset(context.getAssets(),
						"fonts/Roboto-Thin.ttf");
				artistName.setTypeface(tf);

				artistName.setText(artists.get(position).getName());
				row.setTag(artists.get(position));
	
			return row;

		}
				
		@Override
		public int getCount(){
			return artists.size();
		}

		public void updateList(ArrayList<Artist> artists) {
			this.artists = artists;
			this.notifyDataSetChanged();
		}

	}

}
