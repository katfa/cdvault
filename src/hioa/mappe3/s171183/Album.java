package hioa.mappe3.s171183;

public class Album {
	
	private String title, year, albumArtPath;
	private int artistId;
	
	public Album(String title, String year, String albumArtPath, int artistId){
		this.title = title;
		this.year = year;
		this.albumArtPath = albumArtPath;
		this.artistId = artistId;
	}
	
	public String getTitle(){
		return this.title;
	}
	
	public String getYear(){
		return this.year;
	}
	
	public String getAlbumArtPath(){
		return this.albumArtPath;
	}
	
	public int getArtistId() {
		return this.artistId;
	}

}
