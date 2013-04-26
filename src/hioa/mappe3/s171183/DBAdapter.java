package hioa.mappe3.s171183;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class DBAdapter extends SQLiteOpenHelper {
	Context context;
	private SQLiteDatabase db;
	
	private final String TAG ="DBAdapter", 
						 ARTIST_TABLE = "artist",
						 NAME = "name",
						 ALBUM_TABLE = "album",
						 TITLE = "title",
						 YEAR = "year",
						 ARTIST_ID = "artist_id",
						 ALBUM_ART = "album_art",
						 TRACK_TABLE = "track",
						 TRACK_NUMBER = "track_number",
						 ALBUM_ID = "album_id",
						 ID = BaseColumns._ID;
	
	private static final String DB_NAME = "cdvault.db";
	private static final int DB_VERSION = 2;
	
	public DBAdapter (Context context, String databaseName, CursorFactory factory, int databaseVersion) {
		super(context, databaseName, factory, databaseVersion);
	}
	
	public DBAdapter(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}
	
	public DBAdapter open() throws SQLException {
		db = this.getWritableDatabase();
		return this;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		String createArtistTable = "create table " + ARTIST_TABLE +
				"(" + ID + " integer primary key autoincrement, " +
				NAME + " text);";
		
		String createAlbumTable = "create table " + ALBUM_TABLE +
				"(" + ID + " integer primary key autoincrement, " + 
				TITLE + " text, " +
				YEAR + " text, " + 
				ALBUM_ART + " text, " + 
				ARTIST_ID + " integer, " + 
				"FOREIGN KEY(" + ARTIST_ID + ") REFERENCES " + ARTIST_TABLE + "(" + ID  + "));";
		
		String createTrackTable = "create table " + TRACK_TABLE +
				"(" + ID + " integer primary key autoincrement, " + 
				TITLE + " text, " +
				TRACK_NUMBER + " text, " + 
				ALBUM_ID + " integer, " + 
				"FOREIGN KEY(" + ALBUM_ID + ") REFERENCES " + ALBUM_TABLE + "(" + ID  + "));";
				
		
		db.execSQL(createArtistTable);
		Log.d(TAG, "Created artist table");
		
		db.execSQL(createAlbumTable);
		Log.d(TAG, "Created album table");
		
		db.execSQL(createTrackTable);
		Log.d(TAG, "Created track table");
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
		db.execSQL("drop table if exists " + TRACK_TABLE);
		Log.d(TAG, "track table dropped.");
		
		db.execSQL("drop table if exists " + ALBUM_TABLE);
		Log.d(TAG, "album table dropped.");
		
		db.execSQL("drop table if exists " + ARTIST_TABLE);
		Log.d(TAG, "artist table dropped.");
		
		onCreate(db);
	}
	
	public void insertArtist(Artist artist){}
	public void insertAlbum(Album album){}
	public void insertTrack(Track track){}
	public Artist getArtist(int id){ return null; }
	public int getArtistId(Artist a) { return 0; }
	public int getAlbumId(Album a) { return 0; }
	
	
	
}
