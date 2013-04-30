package hioa.mappe3.s171183;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class DBAdapter extends SQLiteOpenHelper {
	Context context;
	private static SQLiteDatabase dbWritable, dbReadable;

	private final String TAG = "DBAdapter", ARTIST_TABLE = "artist",
			NAME = "name", ALBUM_TABLE = "album", TITLE = "title",
			YEAR = "year", ARTIST_ID = "artist_id", ALBUM_ART = "album_art",
			TRACK_TABLE = "track", TRACK_NUMBER = "track_number",
			ALBUM_ID = "album_id", ID = BaseColumns._ID;

	private static final String DB_NAME = "cdvaultdata.db";
	private static final int DB_VERSION = 5;

	public DBAdapter(Context context, String databaseName,
			CursorFactory factory, int databaseVersion) {
		super(context, databaseName, factory, databaseVersion);
	}

	public DBAdapter(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	public DBAdapter open() throws SQLException {
		dbWritable = this.getWritableDatabase();
		dbReadable = this.getReadableDatabase();
		return this;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String createArtistTable = "create table " + ARTIST_TABLE + "(" + ID
				+ " integer primary key autoincrement, " + NAME
				+ " text unique);";

		String createAlbumTable = "create table " + ALBUM_TABLE + "(" + ID
				+ " integer primary key autoincrement, " + TITLE + " text, "
				+ YEAR + " text, " + ALBUM_ART + " blob, " + ARTIST_ID
				+ " integer, " + "FOREIGN KEY(" + ARTIST_ID + ") REFERENCES "
				+ ARTIST_TABLE + "(" + ID + "));";

		String createTrackTable = "create table " + TRACK_TABLE + "(" + ID
				+ " integer primary key autoincrement, " + TITLE + " text, "
				+ TRACK_NUMBER + " text, " + ALBUM_ID + " integer, "
				+ "FOREIGN KEY(" + ALBUM_ID + ") REFERENCES " + ALBUM_TABLE
				+ "(" + ID + "));";

		db.execSQL(createArtistTable);
		Log.d(TAG, "Created artist table");

		db.execSQL(createAlbumTable);
		Log.d(TAG, "Created album table");

		db.execSQL(createTrackTable);
		Log.d(TAG, "Created track table");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table if exists " + TRACK_TABLE);
		Log.d(TAG, "track table dropped.");

		db.execSQL("drop table if exists " + ALBUM_TABLE);
		Log.d(TAG, "album table dropped.");

		db.execSQL("drop table if exists " + ARTIST_TABLE);
		Log.d(TAG, "artist table dropped.");

		onCreate(db);
	}

	// ARTISTS

	public ArrayList<Artist> getAllArtists() {
		ArrayList<Artist> allArtists = new ArrayList<Artist>();
		String selectQuery = "select * from " + ARTIST_TABLE;

		Cursor cursor = dbReadable.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			while (cursor.isAfterLast() == false) {
				int id = cursor.getInt(cursor.getColumnIndex(ID));
				String name = cursor.getString(cursor.getColumnIndex(NAME));

				Artist a = new Artist(id, name);
				allArtists.add(a);
				cursor.moveToNext();
			}
		}

		cursor.close();
		return allArtists;
	}

	public void insertArtist(Artist artist) {
		ContentValues values = new ContentValues(1);
		values.put(NAME, artist.getName());

		dbWritable.insert(ARTIST_TABLE, null, values);
		Log.d(TAG, artist.getName() + " added to database.");
	}

	public boolean artistExists(Artist artist) {
		Cursor cursor = dbReadable.query(ARTIST_TABLE, new String[] { NAME }, NAME
				+ "=?", new String[] { artist.getName() }, null, null, null,
				null);
		return cursor.moveToFirst();
	}

	public Artist getArtistByName(String name) {
		Cursor cursor = dbReadable.query(ARTIST_TABLE, new String[] { ID, NAME }, NAME
				+ "=?", new String[] { name }, null, null, null, null);

		if (cursor.moveToFirst()) {
			int id = cursor.getInt(cursor.getColumnIndex(ID));
			String artistName = cursor.getString(cursor.getColumnIndex(NAME));
			cursor.close();
			return new Artist(id, artistName);
		} else return null;
	}

	public int getArtistId(Artist artist) {
		Artist a = getArtistByName(artist.getName());
		return a.getId();
	}

	public void deleteArtist(int id) {
		deleteAlbumsByArtist(id);
		dbWritable.delete(ARTIST_TABLE, ID + "=?", new String[] { String.valueOf(id) });
	}

	public int updateArtist(Artist artist, String newName) {
		ContentValues values = new ContentValues();
		int otherArtistId;
		
		/* check if an artist with the same name exists, if so, 
		 * delete parameter artist
		 * and update albums to have other artist's id. */
		
		Artist possibleMatch = getArtistByName(newName);
		if (possibleMatch != null) {
			otherArtistId = possibleMatch.getId();

			ArrayList<Album> albums = getAlbumsByArtist(getArtistId(artist));
			for (Album a : albums) {
				updateAlbumArtistId(a, otherArtistId);
			}
	
			deleteArtist(artist.getId());
			return 1;
		} else {
			values.put(NAME, newName);
			return dbWritable.update(ARTIST_TABLE, values, ID + "=?",
					new String[] { String.valueOf(artist.getId()) });
		}
	}

	// ALBUMS

	public ArrayList<Album> getAllAlbums() {
		ArrayList<Album> allAlbums = new ArrayList<Album>();
		String selectQuery = "select * from " + ALBUM_TABLE;

		Cursor cursor = dbReadable.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			while (cursor.isAfterLast() == false) {
				int id = cursor.getInt(cursor.getColumnIndex(ID));
				String title = cursor.getString(cursor.getColumnIndex(TITLE));
				String year = cursor.getString(cursor.getColumnIndex(YEAR));
				int artistID = cursor.getInt(cursor.getColumnIndex(ARTIST_ID));
				byte[] thumb = cursor.getBlob(cursor
						.getColumnIndex(ALBUM_ART));

				Album a = new Album(id, title, year, thumb, artistID);
				allAlbums.add(a);
				cursor.moveToNext();
			}
		}
		cursor.close();
		return allAlbums;
	}

	public void insertAlbum(Album album) {
		ContentValues values = new ContentValues(4);

		values.put(TITLE, album.getTitle());
		values.put(YEAR, album.getYear());
		values.put(ALBUM_ART, album.getAlbumArt());
		values.put(ARTIST_ID, album.getArtistId());

		dbWritable.insert(ALBUM_TABLE, null, values);
		Log.d(TAG, album.getTitle() + " added to database.");
	}

	public Album getAlbum(Album album) {
		Cursor cursor = dbReadable.query(
				ALBUM_TABLE,
				new String[] { ID, TITLE, YEAR, ALBUM_ART, ARTIST_ID },
				TITLE + "=? AND " + ARTIST_ID + "=?",
				new String[] { album.getTitle(),
						String.valueOf(album.getArtistId()) }, null, null,
				null, null);

		if (cursor.moveToFirst()) {
			album.setId(cursor.getInt(cursor.getColumnIndex(ID)));
			cursor.close();
			return album;
		} else
			return null;
	}

	public int getAlbumId(Album a) {
		Cursor cursor = dbReadable.query(
				ALBUM_TABLE,
				new String[] { ID },
				TITLE + "=? AND " + ARTIST_ID + "=? AND " + YEAR + "=?",
				new String[] { a.getTitle(), String.valueOf(a.getArtistId()),
						a.getYear() }, null, null, null, null);
		if (cursor.moveToFirst()) {
			return cursor.getInt(cursor.getColumnIndex(ID));
		} else
			return 0;
	}

	public ArrayList<Album> getAlbumsByArtist(int artistId) {
		Log.d("Database", "Getting all albums with artist id " + artistId);
		ArrayList<Album> albums = new ArrayList<Album>();
		Cursor cursor = dbReadable.query(ALBUM_TABLE, new String[] { ID, TITLE,
				ALBUM_ART, YEAR, ARTIST_ID }, ARTIST_ID + "=?",
				new String[] { String.valueOf(artistId) }, null, null, null,
				null);

		if (cursor.moveToFirst()) {
			while (cursor.isAfterLast() == false) {
				int id = cursor.getInt(cursor.getColumnIndex(ID));
				String title = cursor.getString(cursor.getColumnIndex(TITLE));
				byte[] thumb = cursor.getBlob(cursor
						.getColumnIndex(ALBUM_ART));
				String year = cursor.getString(cursor.getColumnIndex(YEAR));

				albums.add(new Album(id, title, year, thumb, artistId));
				cursor.moveToNext();
			}
		}
		cursor.close();
		return albums;
	}

	public void deleteAlbum(int albumId) {
		dbWritable.delete(ALBUM_TABLE, ID + " =?",
				new String[] { String.valueOf(albumId) });
	}
	
	public void deleteAlbumsByArtist(int artistId){
		dbWritable.delete(ALBUM_TABLE, ARTIST_ID + "=?", new String[] { String.valueOf(artistId) });
	}

	public boolean albumExists(Album album) {
		Cursor cursor = dbReadable.query(ALBUM_TABLE, new String[] { TITLE }, TITLE
				+ "=? AND " + ARTIST_ID + "=?", new String[] {
				album.getTitle(), String.valueOf(album.getArtistId()) }, null,
				null, null, null);
		return cursor.moveToFirst();
	}

	public int updateAlbumArtistId(Album album, int artistId) {
		ContentValues values = new ContentValues();
		values.put(ARTIST_ID, artistId);

		return dbWritable.update(ALBUM_TABLE, values, ID + "=?",
				new String[] { String.valueOf(album.getId()) });
	}

	// TRACKS

	public void insertTrack(Track track) {
		ContentValues values = new ContentValues(3);

		values.put(TITLE, track.getTitle());
		values.put(TRACK_NUMBER, track.getTrackNumber());
		values.put(ALBUM_ID, track.getAlbumId());

		dbWritable.insert(TRACK_TABLE, null, values);
		Log.d(TAG, track.getTitle() + " added to database.");
	}
	
	public ArrayList<Track> getTracks(int albumId){
		ArrayList<Track> tracks = new ArrayList<Track>();
		Cursor cursor = dbReadable.query(TRACK_TABLE, new String[] { ID, TRACK_NUMBER, TITLE }, 
				ALBUM_ID + "=?",
				new String[] { String.valueOf(albumId) }, null, null, null,
				null);

		if (cursor.moveToFirst()) {
			while (cursor.isAfterLast() == false) {
				int id = cursor.getInt(cursor.getColumnIndex(ID));
				String title = cursor.getString(cursor.getColumnIndex(TITLE));
				String trackNumber = cursor.getString(cursor.getColumnIndex(TRACK_NUMBER));
				
				tracks.add(new Track(id, trackNumber, title, albumId));
				cursor.moveToNext();
			}
		}
		cursor.close();
		return tracks;
		
	}
}
