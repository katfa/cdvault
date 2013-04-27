package hioa.mappe3.s171183;

public class Album {
	
	public int id;
	private int artistId;
	private String title, year, albumArtPath;
	
	public Album(String title, String year, String albumArtPath, int artistId){
		this.title = title;
		this.year = year;
		this.albumArtPath = albumArtPath;
		this.artistId = artistId;
	}
	
	public Album(int id, String title, String year, String albumArtPath, int artistId){
		this.id = id;
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

	public int getId() {
		return this.id;
	}
	
	public void setId(int id){
		this.id = id;
	}
}
