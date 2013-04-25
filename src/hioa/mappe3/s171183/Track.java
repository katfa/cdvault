package hioa.mappe3.s171183;

public class Track {

	private String trackNumber, title;
	
	public Track(String trackNumber, String title){
		this.trackNumber = trackNumber;
		this.title = title;
	}
	
	public String getTrackNumber(){
		return this.trackNumber;
	}
	
	public String getTitle(){
		return this.title;
	}
}
