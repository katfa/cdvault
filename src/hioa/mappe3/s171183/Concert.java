package hioa.mappe3.s171183;

public class Concert {
	
	private String venue, artist, time, date;
	
	public Concert(String venue, String artist, String time, String date){
		this.venue = venue;
		this.artist = artist;
		this.time = time;
		this.date = date;
	}
	
	public String getVenue()  { return this.venue; }
	public String getArtist() { return this.artist; }
	public String getTime()   { return this.time; }
	public String getDate()   { return this.date; }
	

}
