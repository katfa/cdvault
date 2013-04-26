package hioa.mappe3.s171183;

public class Track {

	private String trackNumber, title;
	private int albumId;
	
	public Track(String trackNumber, String title, int albumId){
		this.trackNumber = trackNumber;
		this.title = title;
		this.albumId = albumId;
	}
	
	public String getTrackNumber(){
		return this.trackNumber;
	}
	
	public String getTitle(){
		return this.title;
	}
	
	public int getAlbumId(){
		return this.albumId;
	}
}
