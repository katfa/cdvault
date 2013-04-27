package hioa.mappe3.s171183;

public class Artist {
	
	public int id;
	private String name;
	
	public Artist(String name){
		this.name = name;
	}
	
	public Artist(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public String getName(){
		return this.name;
	}
	
	public int getId(){
		return this.id;
	}
	
	public void setId(int id){
		this.id = id;
	}

}
