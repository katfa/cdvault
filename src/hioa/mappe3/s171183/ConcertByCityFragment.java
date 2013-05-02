package hioa.mappe3.s171183;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class ConcertByCityFragment extends Fragment {
	private ArrayList<Concert> concerts = new ArrayList<Concert>();
	private View thisFragmentView;
	private ConcertListAdapter concertListAdapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		thisFragmentView = inflater.inflate(R.layout.concerts, container, false);
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());
		String metroId = sharedPrefs.getString("metroId", "");
		
		if(!metroId.equals("")){
			try {
				concerts = ConcertManager.getConcertsInCity(metroId);
			} catch (InterruptedException e) {
				Log.e("ERROR", "getting concerts by city interrupted ");
			} catch (ExecutionException e) {
				Log.e("ERROR", "getting concerts by city not executed ");
			}
			ListView concertList = (ListView) thisFragmentView.findViewById(R.id.concert_list);
			concertListAdapter = new ConcertListAdapter(getActivity().getBaseContext(), R.layout.concert_row_layout_with_artist, concerts);
			concertList.setAdapter(concertListAdapter);
			concertList.setSelector(android.R.color.transparent);
		}
		
		return thisFragmentView;
	}
	
	public ConcertListAdapter getConcertListAdapter(){
		return this.concertListAdapter;
	}
	
	public void updateCity(String cityId) throws InterruptedException, ExecutionException{
		concertListAdapter.clear();
		concerts = ConcertManager.getConcertsInCity(cityId);
		concertListAdapter.updateConcerts(concerts);
		concertListAdapter.notifyDataSetChanged();
	}
}
