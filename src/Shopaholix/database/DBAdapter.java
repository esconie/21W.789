package Shopaholix.database;

import java.util.ArrayList;

import Shopaholix.database.ItemRatings.Rating;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter {
	// public static final String KEY_ROWID = "_id";
	public static final String KEY_PRODNAME = "prodName";
	public static final String KEY_UPC = "UPC";
	public static final String KEY_RATING_PERSONAL = "ratingPersonal";
	
	public static final String KEY_TAGNAME = "tagName";
	public static final String KEY_TAGCOUNT = "tagCount";
	
	private static final String TAG = "DBAdapter";
	private static final String DATABASE_NAME = "Items";
	private static final String DATABASE_TAGS = "tags";
	private static final String DATABASE_ITEMS = "items";
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_CREATE_1 = "create table items ("
			+ "prodName text not null, " + "UPC integer, "
			+ "ratingPersonal integer);";
	private static final String DATABASE_CREATE_2 = "create table tags ("
			+ "tagName text not null, " + "tagCount integer);";
	private final Context context;
	private DatabaseHelper DBHelper;
	private SQLiteDatabase db;

	public DBAdapter(Context ctx) {
		this.context = ctx;
		DBHelper = new DatabaseHelper(context);
	}

	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DATABASE_CREATE_1);
			db.execSQL(DATABASE_CREATE_2);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS tags");
			db.execSQL("DROP TABLE IF EXISTS items");
			onCreate(db);
		}
	}

	// ---opens the database---
	public DBAdapter open() throws SQLException {
		db = DBHelper.getWritableDatabase();
		return this;
	}

	// ---closes the database---
	public void close() {
		DBHelper.close();
	}

	// ---insert an item into the database given UPC and product name---
	private long putItem(String upc, String prodName) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_PRODNAME, prodName);
		initialValues.put(KEY_UPC, upc);
		ContentValues tags = new ContentValues();
		String[] tagNames = prodName.split("\\s+");
		for(String tag: tagNames){
			tags.put(KEY_TAGNAME, tag);
			tags.put(KEY_TAGCOUNT, getTagCount(tag) + 1);
		}
		db.insertWithOnConflict(DATABASE_TAGS, null, tags, SQLiteDatabase.CONFLICT_REPLACE);
		return db.insert(DATABASE_ITEMS, null, initialValues);
	}
	
	// ---get how many times a tag has come up so far---
	private int getTagCount(String tag){
		Cursor tagCount = db.query(true, DATABASE_TAGS, null, KEY_TAGNAME + "="
				+ tag, null, null, null, null, null);
		if(tagCount.getCount() < 1)
			return 0;
		return tagCount.getInt(tagCount.getColumnIndex(KEY_TAGCOUNT));
	}
	
	// ---insert an item into the database---
	public long putItem(Item item){
		return putItem(item.upc, item.name);
	}

	// ---returns item given UPC---
	public Item getItem(String UPC) throws SQLException {
		Cursor item = db.query(true, DATABASE_ITEMS, null, KEY_UPC + "="
				+ UPC, null, null, null, null, null);
		
		int upcIndex = item.getColumnIndex(KEY_UPC);
		int prodNameIndex = item.getColumnIndex(KEY_PRODNAME);
		int personalRatingIndex = item.getColumnIndex(KEY_RATING_PERSONAL);
		
		if (item.getCount() > 0) {
			// Hash User to Rating in ItemRatings object
			ItemRatings ratings = new ItemRatings();
			for (int i = personalRatingIndex + 1; i < item.getColumnCount(); i++)
				ratings.put(new User(item.getColumnName(i)), Rating.values()[item.getInt(i)]);
			// Getting UPC and item name
			String upc = item.getString(upcIndex);
			String name = item.getString(prodNameIndex);
			return new Item(upc, name, ratings);
		}
		return null;
	}
	
	// ---deletes a particular item by UPC---
	public boolean deleteTitle(long UPC) {
		return db.delete(DATABASE_ITEMS, KEY_UPC + "=" + UPC, null) > 0;
	}
	
	// ---updates personal item rating---
	public boolean updateItemRating(String uPC, Rating rating) {
		ContentValues args = new ContentValues();
		args.put(KEY_RATING_PERSONAL, rating.ordinal());
		return db.update(DATABASE_ITEMS, args, KEY_UPC + "=" + uPC, null) > 0;
	}

	// ---updates family member's rating of item---
	public boolean updateItemFamilyRating(String uPC, String name, Rating rating) {
		ContentValues args = new ContentValues();
		args.put(name, rating.ordinal());
		return db.update(DATABASE_ITEMS, args, KEY_UPC + "=" + uPC, null) > 0;
	}

	// ---retrieves all the tags---
	public ArrayList<Tag> getAllTags(){
		ArrayList<Tag> output = new ArrayList<Tag>();
		Cursor allTags = db.query(DATABASE_TAGS, null, null, null, null,
				null, null);
		if (allTags.moveToFirst()) {
			while (allTags.moveToNext()) {
				output.add(new Tag(allTags.getString(allTags.getColumnIndex(KEY_TAGNAME))));
			}
		}
		return output;
	}

	// ---retrieves all the items---
	public ArrayList<Item> getAllItems() {
		Cursor allItems = db.query(DATABASE_ITEMS, null, null, null, null,
				null, null);
		// indices in the cursor object for the respective columns
		int upcIndex = allItems.getColumnIndex(KEY_UPC);
		int prodNameIndex = allItems.getColumnIndex(KEY_PRODNAME);
		int personalRatingIndex = allItems.getColumnIndex(KEY_RATING_PERSONAL);

		ArrayList<Item> output = new ArrayList<Item>();
		// Create Items for each row
		if (allItems.moveToFirst()) {
			while (allItems.moveToNext()) {
				// Hash User to Rating in ItemRatings object
				ItemRatings ratings = new ItemRatings();
				for (int i = personalRatingIndex + 1; i < allItems.getColumnCount(); i++)
					ratings.put(new User(allItems.getColumnName(i)), Rating.values()[allItems.getInt(i)]);
				// Getting UPC and item name
				String upc = allItems.getString(upcIndex);
				String name = allItems.getString(prodNameIndex);
				output.add(new Item(upc, name, ratings));
			}
		}
		return output;
	}
	
	// ---returns boolean of whether database contains item---
	public boolean contains(String upc) throws SQLException {
		Cursor mCursor = db.query(true, DATABASE_ITEMS, null, KEY_UPC + "="
				+ upc, null, null, null, null, null);
		if (mCursor.getCount() > 0) {
			return true;
		}
		else
			return false;
	}
	
	// ---returns User object with "Personal" as name---
	public User getMe(){
		return new User("Personal");
	}

	/**
	 * Family functionality
	 * 
	 */
	// ---deletes a particular family member column---
	public void removeFamilyMember(User member) {
		String famName = member.name;
		db.execSQL("ALTER TABLE " + DATABASE_ITEMS + " DROP COLUMN " + famName);
	}

	// ---add a particular family member column---
	public void addFamilyMember(User member) {
		String famName = member.name;
		db.execSQL("ALTER TABLE " + DATABASE_ITEMS + " ADD " + famName
				+ "double NOT NULL DEFAULT(0.0)");
	}
	// ---get all Users, including yourself---
	public ArrayList<User> getUsers(){
		ArrayList<User> output = new ArrayList<User>();
		output.add(new User("Personal"));
		
		//Find column names with PRAGMA query
		Cursor columns = db.rawQuery("PRAGMA table_info(mytable)", null);
		boolean isFamilyMemberColumn = false;
		
		//Create User object for each family member
		if ( columns.moveToFirst() ) {
		    do {
		    	if(isFamilyMemberColumn)
		    		output.add(new User(columns.getString(1)));
		    	if(columns.getString(1).equals(KEY_RATING_PERSONAL))
		    		isFamilyMemberColumn = true;
		    } while (columns.moveToNext());
		}
		
		return output;
	}
}
