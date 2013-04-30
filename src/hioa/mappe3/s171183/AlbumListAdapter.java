package hioa.mappe3.s171183;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AlbumListAdapter extends ArrayAdapter<Album> {

	private Context context;
	private ArrayList<Album> albums;
	
	public AlbumListAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
	}

	public AlbumListAdapter(Context context, int textViewResourceId, ArrayList<Album> albums) {
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
		albumThumb.setBackground(albums.get(position).getAlbumArtDrawable());
		
		row.setTag(albums.get(position));
		
		return row;
		
	}
	
	@Override
	public int getCount(){
		return albums.size();
	}
	
	public void updateList(ArrayList<Album> albums){
		this.albums = albums;
		this.notifyDataSetChanged();
	}

}
