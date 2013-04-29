package hioa.mappe3.s171183;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class AlbumListFragment extends Fragment{
	private ArrayList<Album> allAlbums;
	private static ArrayAdapter<Album> albumAdapter;
	private DBAdapter dbAdapter;
	
	private View thisFragmentView;
	private FragmentManager fManager;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d("albumListfragment", " created ");
		super.onCreate(savedInstanceState);
		thisFragmentView = inflater.inflate(R.layout.albums, container, false);
		fManager = this.getFragmentManager();

		dbAdapter = new DBAdapter(getActivity().getBaseContext());
		dbAdapter.open();
		
		allAlbums = dbAdapter.getAllAlbums();
		
		albumAdapter = new AlbumAdapter(getActivity().getBaseContext(), R.layout.album_row_layout, allAlbums);
		ListView albumList = (ListView) thisFragmentView.findViewById(R.id.album_list);
		albumList.setAdapter(albumAdapter);
		
		albumList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
					EditDeleteDialog dialog = new EditDeleteDialog((Album)view.getTag(), AlbumListFragment.this);
					dialog.show(fManager, "editDeleteDialog");
			}
		});

		return thisFragmentView;
		
	}
	
	public static void updateList(ArrayList<Album> albums) {
		((AlbumAdapter) albumAdapter).updateList(albums);
	}

	
	private class AlbumAdapter extends ArrayAdapter<Album> {
		private Context context;
		private ArrayList<Album> albums;

		public AlbumAdapter(Context context, int textViewResourceId, ArrayList<Album> albums) {
			super(context, textViewResourceId, albums);
			this.context = context;
			this.albums = albums;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View row = inflater.inflate(R.layout.album_row_layout, parent, false);
			Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Thin.ttf");
			
			
			TextView albumTitle = (TextView)row.findViewById(R.id.album_title);
			albumTitle.setTypeface(tf);
			albumTitle.setText(albums.get(position).getTitle());

			TextView albumYear = (TextView) row.findViewById(R.id.album_year);
			albumYear.setTypeface(tf);
			albumYear.setText(albums.get(position).getYear());
			
			ImageView albumThumb = (ImageView)row.findViewById(R.id.album_thumb);
			try {
				albumThumb.setBackground(MusicManager.getAlbumThumb(albums.get(position).getAlbumArtPath()));
			} catch (InterruptedException e) {
				Toast.makeText(context, "Could not fetch album art for " + albums.get(position).getTitle() + ". " + e.getMessage(), Toast.LENGTH_SHORT).show();
			} catch (ExecutionException e) {
				Toast.makeText(context, "Could not fetch album art for " + albums.get(position).getTitle() + ". " + e.getMessage(), Toast.LENGTH_SHORT).show();
			}
			
			row.setTag(albums.get(position));
			
			return row;
			
		}
		
		public void updateList(ArrayList<Album> albums){
			this.albums = albums;
		}
		
	}
}
