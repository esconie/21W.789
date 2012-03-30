package Shopaholix.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
public class DBAdapter
{
//	public static final String KEY_ROWID = "_id";
	public static final String KEY_PRODNAME = "prodName";
	public static final String KEY_UPC = "UPC";
	public static final String KEY_RATING = "rating";
	public static final String KEY_RATING_PERSONAL = "ratingPersonal";
	
	private static final String TAG = "DBAdapter";
	private static final String DATABASE_NAME = "Items";
	private static final String DATABASE_TABLE = "Items";
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_CREATE_1 =
			"create table items ("
					+ "prodName text not null, " 
					+ "UPC integer, "
					+ "ratingPersonal int);"; 
	private static final String DATABASE_CREATE_2 =
			"create table tags ("
					+ "tagName text not null, " 
					+ "ratingPersonal int);";
	private final Context context;
	private DatabaseHelper DBHelper;
	private SQLiteDatabase db;
	
	public enum Rating{
		GOOD, BAD, NEUTRAL, UNRATED
	}
	
	public DBAdapter(Context ctx) {
		this.context = ctx;
		DBHelper = new DatabaseHelper(context);
	}
	
	private static class DatabaseHelper extends SQLiteOpenHelper{
		
		DatabaseHelper(Context context){
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
		
		@Override
		public void onCreate(SQLiteDatabase db){
			db.execSQL(DATABASE_CREATE_1);
			db.execSQL(DATABASE_CREATE_2);
		}
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion,
				int newVersion){
			Log.w(TAG, "Upgrading database from version " + oldVersion
					+ " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS tags");
			db.execSQL("DROP TABLE IF EXISTS items");
			onCreate(db);
		}
	}
	
	//---opens the database---
	public DBAdapter open() throws SQLException
	{
		db = DBHelper.getWritableDatabase();
		return this;
	}
	//---closes the database---
	public void close()
	{
		DBHelper.close();
	}
	//---insert an item into the database---
	public long insertItem(String prodName, int UPC)
	{
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_PRODNAME, prodName);
		initialValues.put(KEY_UPC, UPC);
		return db.insert(DATABASE_TABLE, null, initialValues);
	}
	//---updates personal item rating---
	public boolean updateItemRating(int UPC, Rating rating){
		ContentValues args = new ContentValues();
		args.put(KEY_RATING_PERSONAL, rating.ordinal());
		return db.update(DATABASE_TABLE, args,
				KEY_UPC + "=" + UPC, null) > 0;
	}
	//---updates family member's rating of item---
	public boolean updateItemFamilyRating(int UPC, String name, Rating rating){
		ContentValues args = new ContentValues();
		args.put(KEY_RATING + name, rating.ordinal());
		return db.update(DATABASE_TABLE, args,
				KEY_UPC + "=" + UPC, null) > 0;
	}
	//---deletes a particular item by UPC---
	public boolean deleteTitle(int UPC)
	{
		return db.delete(DATABASE_TABLE, KEY_UPC +
				"=" + UPC, null) > 0;
	}
	//---retrieves all the items---
	public Cursor getAllItems()
	{
		return db.query(DATABASE_TABLE, null,
				null,
				null,
				null,
				null,
				null);
	}
	//---retrieves a particular item by UPC---
	public Cursor getTitle(int UPC) throws SQLException
	{
		Cursor mCursor =
				db.query(true, DATABASE_TABLE, null,
				KEY_UPC + "=" + UPC, 
				null,
				null,
				null,
				null,
				null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}
	
	/**Family functionality
	 * 
	 */
	//---deletes a particular family member column---
	public void deleteFamily(String famName)
		{
			db.execSQL("ALTER TABLE " + DATABASE_TABLE + " DROP COLUMN rating" + famName);
		}
	
	//---add a particular family member column---
	public void addFamily(String famName)
		{
			db.execSQL("ALTER TABLE " + DATABASE_TABLE + " ADD rating" + famName + "double NOT NULL DEFAULT(0.0)");
		}
}
