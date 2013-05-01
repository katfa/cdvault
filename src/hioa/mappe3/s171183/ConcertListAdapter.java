package hioa.mappe3.s171183;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ConcertListAdapter extends ArrayAdapter<Concert> {

		private Context context;
		private ArrayList<Concert> concerts;
		private int textViewResourceId;
		
		public ConcertListAdapter(Context context, int textViewResourceId) {
			super(context, textViewResourceId);
		}

		public ConcertListAdapter(Context context, int textViewResourceId, ArrayList<Concert> concerts) {
			super(context, textViewResourceId, concerts);
			this.context = context;
			this.concerts = concerts;
			this.textViewResourceId = textViewResourceId;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View row = inflater.inflate(textViewResourceId, parent, false);
			Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Black.ttf");
			Typeface thin = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Thin.ttf");
			
			TextView concertArtist = (TextView) row.findViewById(R.id.concertArtist);
			if (concertArtist != null){
				concertArtist.setText(concerts.get(position).getArtist());
				concertArtist.setTypeface(thin);
			}
			
			TextView concertVenue = (TextView)row.findViewById(R.id.concertVenue);
			concertVenue.setTypeface(tf);
			concertVenue.setText(concerts.get(position).getVenue());

			TextView concertDate = (TextView) row.findViewById(R.id.concertDate);
			concertDate.setText(concerts.get(position).getDate());
			
			TextView concertTime = (TextView) row.findViewById(R.id.concertTime);
			concertTime.setTypeface(tf);
			concertTime.setText(concerts.get(position).getTime());
			
			return row;
			
		}
		
		@Override
		public int getCount(){
			return concerts.size();
		}
		
	}
