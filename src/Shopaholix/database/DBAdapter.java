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
	public static final String KEY_PRODNAME = "productName";
	public static final String KEY_UPC = "UPC";
	public static final String KEY_RATING = "rating";
	
	private static final String TAG = "DBAdapter";
	private static final String DATABASE_NAME = "personal";
	private static final String DATABASE_TABLE = "prefs";
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_CREATE =
			"create table prefs (_id int primary key autoincrement, "
					+ "prodName text not null, " 
					+ "UPC integer, "
					+ "rating int);"; //TODO: decide on how rating is going to work
	private final Context context;
	private DatabaseHelper DBHelper;
	private SQLiteDatabase db;
	
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
			db.execSQL(DATABASE_CREATE);
		}
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion,
				int newVersion){
			Log.w(TAG, "Upgrading database from version " + oldVersion
					+ " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS prefs");
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
	public long insertItem(String prodName, int UPC, int rating)
	{
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_PRODNAME, prodName);
		initialValues.put(KEY_UPC, UPC);
		initialValues.put(KEY_RATING, rating);
		return db.insert(DATABASE_TABLE, null, initialValues);
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
		return db.query(DATABASE_TABLE, new String[] {
//				KEY_ROWID,
				KEY_PRODNAME,
				KEY_UPC,
				KEY_RATING},
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
				db.query(true, DATABASE_TABLE, new String[] {
//					KEY_ROWID,
					KEY_PRODNAME,
					KEY_UPC,
					KEY_RATING},
				KEY_UPC + "=" + UPC, //orig was KEY_ROWID = rowId
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
	//---updates an item---
	public boolean updateItem(String prodName, int UPC, int rating){
		ContentValues args = new ContentValues();
		args.put(KEY_PRODNAME, prodName);
		args.put(KEY_UPC, UPC);
		args.put(KEY_RATING, rating);
		return db.update(DATABASE_TABLE, args,
				KEY_UPC + "=" + UPC, null) > 0;
	}
}
