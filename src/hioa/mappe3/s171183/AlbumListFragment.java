package hioa.mappe3.s171183;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class AlbumListFragment extends Fragment{
	private ArrayList<Album> allAlbums;
	private DBAdapter dbAdapter;
	
	private View thisFragmentView;
	private FragmentManager fManager;
	private AlbumListAdapter albumListAdapter;
	
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
		
		albumListAdapter = new AlbumListAdapter(getActivity().getBaseContext(), R.layout.album_row_layout, allAlbums);
		ListView albumList = (ListView) thisFragmentView.findViewById(R.id.album_list);
		albumList.setAdapter(albumListAdapter);
		
		albumList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
					OptionsDialog dialog = new OptionsDialog((Album)view.getTag(), AlbumListFragment.this);
					dialog.show(fManager, "editDeleteDialog");
			}
		});

		return thisFragmentView;
		
	}
	
	public void updateList(ArrayList<Album> albums) {
		albumListAdapter.updateList(albums);
	}
	
	public AlbumListAdapter getAlbumListAdapter(){
		return this.albumListAdapter;
	}
	
	public DBAdapter getDbAdapter(){
		return this.dbAdapter;
	}
}
