package hioa.mappe3.s171183;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ArtistFragment extends Fragment {
	private ArrayList<Artist> allArtists;
	private ArrayAdapter<Artist> artistAdapter;
	private DBAdapter dbAdapter;
	
	private View thisFragmentView;
	private FragmentManager fManager;
	
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
		for(Artist a : allArtists){
			System.out.println(a.getName());
		}
		
		artistAdapter = new ArtistAdapter(getActivity().getBaseContext(), R.layout.artist_row_layout, allArtists);
		ListView artistsList = (ListView) thisFragmentView.findViewById(R.id.artist_list);
		artistsList.setAdapter(artistAdapter);
		
		artistsList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				EditDeleteDialog dialog = new EditDeleteDialog((Artist)view.getTag(), ArtistFragment.this);
				dialog.show(fManager, "EditDeleteDialog");
			}
		});

		return thisFragmentView;
		
	}
	
	private class ArtistAdapter extends ArrayAdapter<Artist> {
		private Context context;
		private ArrayList<Artist> artists;

		public ArtistAdapter(Context context, int textViewResourceId, ArrayList<Artist> artists) {
			super(context, textViewResourceId, artists);
			this.context = context;
			this.artists = artists;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View row = inflater.inflate(R.layout.artist_row_layout, parent, false);
			
			
			TextView artistName = (TextView)row.findViewById(R.id.artist_name);
			Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Thin.ttf");
			artistName.setTypeface(tf);
			
			artistName.setText(artists.get(position).getName());
			row.setTag(artists.get(position));
			
			return row;
			
		}
		
	}

}
