package hioa.mappe3.s171183;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class SearchAndScanFragment extends Fragment{
	private HashMap<String, String> results = new HashMap<String, String>();
	private Drawable albumArt;
	private ArrayList<TreeMap<?, String>> albumDetails = new ArrayList<TreeMap<?, String>>();

	private DBAdapter dbAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		dbAdapter = new DBAdapter(container.getContext());
	    dbAdapter.open();
	    
	    View thisFragmentView =  inflater.inflate(R.layout.search_and_scan, container, false);

	    Button startScanner = (Button) thisFragmentView.findViewById(R.id.startScanner);
	    startScanner.setOnClickListener(new OnClickListener() {

	      @Override
	      public void onClick(View arg0) {
	        Intent intent = new Intent(
	            "com.google.zxing.client.android.SCAN");
	        intent.putExtra("SCAN_MODE", "PRODUCT_MODE");
	        startActivityForResult(intent, 0);
	      }
	    });
	    
		return thisFragmentView;
	}
}
