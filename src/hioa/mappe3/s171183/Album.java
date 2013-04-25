package hioa.mappe3.s171183;

public class Album {
	
	private String title, year, albumArtPath;
	
	public Album(String title, String year, String albumArtPath){
		this.title = title;
		this.year = year;
		this.albumArtPath = albumArtPath;
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

}
