package com.rsu.jayanthsunchu.redditsuitedup;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class RedditorDB {
	Context appContext;
	private SQLiteDatabase sqlDb;
	private final String sqlDb_Name = "redditorDBase";
	
	private final int sqlDb_Version = 1;

	private final String table_name = "reddituseraccounts";
	//private final String tAttributeOne = "id";
	private final String tAttributeTwo = "username";
	private final String tAttributeThree = "password";
	private final String tAttributeFour = "cookie";
	private final String tAttributeFive = "modhash";
	private final String tAttributeSix = "expiry";
	private final String tAttributeSeven = "defaultflag";
	
	private final String second_table = "subreddits";
	private final String sAttributeOne = "name";

	public RedditorDB(Context appContext) {
		this.appContext = appContext;
		checkHelperClass createOrCheck = new checkHelperClass(appContext);
		this.sqlDb = createOrCheck.getWritableDatabase();
	}
	
	public void closeDb(){
		if(sqlDb!= null)sqlDb.close();
	}

	public long newUser(String username, String password, String cookie,
			String modhash, String expiryDate, String defaultValue) {
		ContentValues currentValues = new ContentValues();
		currentValues.put(tAttributeTwo, username);
		currentValues.put(tAttributeThree, password);
		currentValues.put(tAttributeFour, cookie);
		currentValues.put(tAttributeFive, modhash);
		currentValues.put(tAttributeSix, expiryDate);
		currentValues.put(tAttributeSeven, defaultValue);
		long returnValue = sqlDb.insert(table_name, null, currentValues);

		return returnValue;

	}
	
	public long addSubReddit(String subreddit){
		ContentValues currentValues = new ContentValues();
		currentValues.put(sAttributeOne, subreddit);
		return sqlDb.insert(second_table, null, currentValues);
	}
	
	

	public class AuthenticationObject {
		String AUserName;
		String APassWord;
		String ACookie;
		String AModhash;

		public AuthenticationObject(String username, String password,
				String cookie, String modhash) {
			this.AUserName = username;
			this.APassWord = password;
			this.ACookie = cookie;
			this.AModhash = modhash;
		}

	}

	public int removeDefaults() {
		try {
			ContentValues cv = new ContentValues();
			cv.put(tAttributeSeven, "false");
			String filter = tAttributeSeven + "=?";
			int noOfRows = sqlDb.update(table_name, cv, filter,
					new String[] { "true" });
			return noOfRows;
		} catch (SQLiteException ex) {
			return 0;
		}
	}

	public int removeDefaultsAndUpdate(String userName) {
		try {
			ContentValues cv = new ContentValues();
			cv.put(tAttributeSeven, "false");
			String filter = tAttributeSeven + "=?";
			String filter2 = tAttributeTwo + "=?";
			ContentValues cv2 = new ContentValues();
			cv2.put(tAttributeSeven, "true");
			sqlDb.update(table_name, cv, filter, new String[] { "true" });
			int anotherExecution = sqlDb.update(table_name, cv2, filter2,
					new String[] { userName });
			return anotherExecution;
		} catch (SQLiteException ex) {
			return 0;
		}
	}

	public String[] getCurrentUser() {
		Cursor c;
		String filter = tAttributeSeven + "=?";
		c = sqlDb.query(table_name, new String[] { tAttributeTwo,
				tAttributeFour, tAttributeFive }, filter,
				new String[] { "true" }, null, null, null);
		c.moveToFirst();
		String arr[]= new String[3];
		
		while (!c.isAfterLast()) {
			arr[0] = c.getString(0) ;
			arr[1] = c.getString(1);
			arr[2] = c.getString(2);
			
			c.moveToNext();
		}
		
		c.close();
		
		return arr;
	}

	public int getCount() {

		String queryForNo = "Select Count(*) As Number from " + table_name;
		Cursor c;
		int numberOfAccounts = 0;

		c = sqlDb.rawQuery(queryForNo, null);
		c.moveToFirst();
		if (!c.isAfterLast()) {
			numberOfAccounts = c.getInt(0);
		}
		c.close();

		return numberOfAccounts;

	}

	public String[] getAllRows() {
		Cursor c;

		c = sqlDb.query(table_name, new String[] { tAttributeTwo,
				tAttributeThree, tAttributeFour, tAttributeFive,
				tAttributeSeven }, null, null, null, null, null);
		c.moveToFirst();
		String[] arr = new String[c.getCount()];
		int i = 0;
		while (!c.isAfterLast()) {
			arr[i] = c.getString(0) + "," + c.getString(4);
			i++;
			c.moveToNext();
		}
		c.close();
		return arr;

	}

	public int checkExistingUserNames(String userName) {
		Cursor c;
		String filter = tAttributeTwo + "=?";

		c = sqlDb.query(table_name, new String[] { tAttributeTwo }, filter,
				new String[] { userName }, null, null, null);
		c.moveToFirst();
		int existingAccount = c.getCount();
		c.close();
		return existingAccount;
	}

	public AuthenticationObject getAuthentication() {

		Cursor c;
		AuthenticationObject obj;

		c = sqlDb.query(table_name, new String[] { tAttributeTwo,
				tAttributeThree, tAttributeFour, tAttributeFive }, null, null,
				null, null, null, null);
		c.moveToFirst();
		if (!c.isAfterLast()) {

			obj = new AuthenticationObject(c.getString(0), c.getString(1), c
					.getString(2), c.getString(3));
			c.close();
			return obj;
		}
		if(c!=null)c.close();
		return null;
	}

	private class checkHelperClass extends SQLiteOpenHelper {

		public checkHelperClass(Context context) {
			super(context, sqlDb_Name, null, sqlDb_Version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			String createTableQuery = "create table reddituseraccounts(id integer primary key autoincrement not null, username text, password text, cookie text, modhash text, expiry text, defaultflag text);";
			
			db.execSQL(createTableQuery);
			
			String createSecondQuery = "create table subreddits(name text);";
			
			db.execSQL(createSecondQuery);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// Upgrade DB Version
		}

		@Override
		public synchronized void close() {
			if (sqlDb != null) {
				sqlDb.close();
				super.close();
			}
			
		}

	}

}
