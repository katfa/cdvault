package hioa.mappe3.s171183;

import java.io.ByteArrayInputStream;
import java.io.Serializable;

import android.graphics.drawable.Drawable;

public class Album implements Serializable{

	private static final long serialVersionUID = 1L;
	public int id;
	private int artistId;
	private String title, year;
	private byte[] albumArt;
	
	public Album(String title, String year, byte[] albumArt, int artistId){
		this.title = title;
		this.year = year;
		this.albumArt = albumArt;
		this.artistId = artistId;
	}
	
	public Album(int id, String title, String year, byte[] albumArt, int artistId){
		this.id = id;
		this.title = title;
		this.year = year;
		this.albumArt = albumArt;
		this.artistId = artistId;
	}
	
	public String getTitle(){
		return this.title;
	}
	
	public String getYear(){
		return this.year;
	}
	
	public byte[] getAlbumArt(){
		return this.albumArt;
	}
	
	public Drawable getAlbumArtDrawable(){
		ByteArrayInputStream inputStream = new ByteArrayInputStream(albumArt);
		return Drawable.createFromStream(inputStream, "albumThumb");
	}
	
	public int getArtistId() {
		return this.artistId;
	}

	public int getId() {
		return this.id;
	}
	
	public void setId(int id){
		this.id = id;
	}
}
