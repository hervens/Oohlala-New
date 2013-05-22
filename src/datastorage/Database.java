package datastorage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class Database {
	public static final String KEY_ROWID = "_id";
	public static final String KEY_USER_ID = "user_id";
	public static final String KEY_START_TEXT = "start_time_text";
	public static final String KEY_END_TEXT = "end_time_text";
	public static final String KEY_CHECK = "check_status";
	public static final String KEY_LOC = "location";
	public static final String KEY_LAT = "latitude";
	public static final String KEY_LONG = "longitude";
	public static final String KEY_ALERT = "alert_time";
	public static final String KEY_START = "start_time";
	public static final String KEY_END = "end_time";
	public static final String KEY_STATUS = "status";
	public static final String KEY_DESCR = "description";
	public static final String KEY_NEW = "new";
	
	public static final String KEY_EVENT_ID = "event_id";
	public static final String KEY_EXTRA_ID = "extra_id";
	public static final String KEY_TITLE = "title";
	public static final String KEY_TYPE = "type";
	
	public static final String KEY_COURSE_ID = "course_id";
	public static final String KEY_COURSE_NAME = "course_name";
	public static final String KEY_COURSE_CODE = "course_code";
	public static final String KEY_COURSE_TIME_ID = "course_time_id";
	public static final String KEY_IS_BIWEEKLY = "is_biweekly";
	public static final String KEY_DAY_OF_WEEK = "day_of_week";
	public static final String KEY_COLOR = "color";
	public static final String KEY_ICON = "icon";
	
	private static final String DATABASE_NAME = "Oohlala.db";
	private static final String DATABASE_TABLE_EVENTS_NAME = "events_table";
	private static final String DATABASE_TABLE_CLASSES_NAME = "classes_table";
	private static final int DATABASE_VERSION = 1;
	
	private DbHelper_Events Helper_Events;
	private DbHelper_Classes Helper_Classes;
	private final Context ourContext;
	private SQLiteDatabase Database_Events;
	private SQLiteDatabase Database_Classes;
	
	String[] eventsTableColumns = new String[]{KEY_ROWID, KEY_USER_ID, KEY_START_TEXT, KEY_END_TEXT, KEY_CHECK, KEY_LOC, 
			KEY_LAT, KEY_LONG, KEY_ALERT, KEY_START, KEY_END, KEY_STATUS, KEY_DESCR, KEY_EVENT_ID, 
			KEY_EXTRA_ID, KEY_TITLE, KEY_TYPE, KEY_NEW};
	String[] classesTableColumns = new String[]{KEY_ROWID, KEY_USER_ID, KEY_START_TEXT, KEY_END_TEXT, KEY_CHECK, KEY_LOC, 
			KEY_LAT, KEY_LONG, KEY_ALERT, KEY_START, KEY_END, KEY_STATUS, KEY_DESCR, KEY_COURSE_ID,
			KEY_COURSE_NAME, KEY_COURSE_CODE, KEY_COURSE_TIME_ID, KEY_IS_BIWEEKLY, KEY_DAY_OF_WEEK,
			KEY_COLOR, KEY_ICON, KEY_NEW};
	
	private static class DbHelper_Events extends SQLiteOpenHelper {

		public DbHelper_Events(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			db.execSQL("CREATE TABLE IF NOT EXISTS " + DATABASE_TABLE_EVENTS_NAME + " (" +
						KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
						KEY_USER_ID + " INTEGER, " +
						KEY_START_TEXT + " TEXT, " + 
						KEY_END_TEXT + " TEXT, " + 
						KEY_CHECK + " INTEGER DEFAULT 0, " +
						KEY_LOC + " TEXT, " + 
						KEY_LAT + " REAL, " +
						KEY_LONG + " REAL, " +
						KEY_ALERT + " INTEGER, " +
						KEY_START + " INTEGER, " +
						KEY_END + " INTEGER, " +
						KEY_STATUS + " INTEGER, " +
						KEY_DESCR + " TEXT, " + 
						KEY_EVENT_ID + " INTEGER, " +
						KEY_EXTRA_ID + " INTEGER, " +
						KEY_TITLE + " TEXT, " + 
						KEY_TYPE + " INTEGER, " + 
						KEY_NEW + " INTEGER);"
			);
			
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_EVENTS_NAME);
			onCreate(db);
		}
	}
	
	private static class DbHelper_Classes extends SQLiteOpenHelper {

		public DbHelper_Classes(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			db.execSQL("CREATE TABLE IF NOT EXISTS " + DATABASE_TABLE_CLASSES_NAME + " (" +
					KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
					KEY_USER_ID + " INTEGER, " +
					KEY_START_TEXT + " TEXT, " + 
					KEY_END_TEXT + " TEXT, " + 
					KEY_CHECK + " INTEGER DEFAULT 0, " +
					KEY_LOC + " TEXT, " + 
					KEY_LAT + " REAL, " +
					KEY_LONG + " REAL, " +
					KEY_ALERT + " INTEGER, " +
					KEY_START + " INTEGER, " +
					KEY_END + " INTEGER, " +
					KEY_STATUS + " INTEGER, " +
					KEY_DESCR + " TEXT, " + 
					KEY_COURSE_ID + " INTEGER, " +
					KEY_COURSE_NAME + " TEXT, " + 
					KEY_COURSE_CODE + " TEXT, " + 
					KEY_COURSE_TIME_ID + " INTEGER, " +
					KEY_IS_BIWEEKLY + " INTEGER, " +
					KEY_DAY_OF_WEEK + " INTEGER, " +
					KEY_COLOR + " TEXT, " + 
					KEY_ICON + " TEXT, " + 
					KEY_NEW + " INTEGER);"
			);
			
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_CLASSES_NAME);
			onCreate(db);
		}
	}
	
	public Database(Context c){
		ourContext = c;
	}
	
	public Database open(){
		Helper_Events = new DbHelper_Events(ourContext);
		Database_Events = Helper_Events.getWritableDatabase();
		//ourHelper.onUpgrade(ourDatabase, 1, 1);
		if (!isTableEventsExists(DATABASE_TABLE_EVENTS_NAME)){
			Helper_Events.onCreate(Database_Events);
		}
		
		Helper_Classes = new DbHelper_Classes(ourContext);
		Database_Classes = Helper_Classes.getWritableDatabase();
		//ourHelper.onUpgrade(ourDatabase, 1, 1);
		if (!isTableClassesExists(DATABASE_TABLE_CLASSES_NAME)){
			Helper_Classes.onCreate(Database_Classes);
		}
		
		return this;
	}
	
	public void close(){
		Helper_Events.close();
		Helper_Classes.close();
	}
	
	public boolean isTableEventsExists(String tableName) {
	    Cursor c = Database_Events.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '"+tableName+"'", null);
	    if(c!=null) {
	        if(c.getCount()>0) {
	            return true;
	        }
	    }
	    return false;
	}
	
	public boolean isTableClassesExists(String tableName) {
	    Cursor c = Database_Classes.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '"+tableName+"'", null);
	    if(c!=null) {
	        if(c.getCount()>0) {
	            return true;
	        }
	    }
	    return false;
	}
	
	public void createEntryForEventsTable(int user_id, String start_time_text, String end_time_text, int check_status, String location, 
			double latitude, double longitude, int alert_time, int start_time, int end_time, int status,
			String description, int event_id, int extra_id, String title, int type){
		ContentValues cv = new ContentValues();
		cv.put(KEY_USER_ID, user_id);
		cv.put(KEY_START_TEXT, start_time_text);
		cv.put(KEY_END_TEXT, end_time_text);
		cv.put(KEY_CHECK, 0);
		cv.put(KEY_LOC, location);
		cv.put(KEY_LAT, latitude);
		cv.put(KEY_LONG, longitude);
		cv.put(KEY_ALERT, alert_time);
		cv.put(KEY_START, start_time);
		cv.put(KEY_END, end_time);
		cv.put(KEY_STATUS, status);
		cv.put(KEY_DESCR, description);
		cv.put(KEY_EVENT_ID, event_id);
		cv.put(KEY_EXTRA_ID, extra_id);
		cv.put(KEY_TITLE, title);
		cv.put(KEY_TYPE, type);
		cv.put(KEY_NEW, 1);
			
		Database_Events.insert(DATABASE_TABLE_EVENTS_NAME, null, cv);
	}
	
	public boolean updateEntryForEventsTable(int user_id, String start_time_text, String end_time_text, int check_status, String location, 
			double latitude, double longitude, int alert_time, int start_time, int end_time, int status,
			String description, int event_id, int extra_id, String title, int type){
		ContentValues cv = new ContentValues();
		cv.put(KEY_USER_ID, user_id);
		cv.put(KEY_START_TEXT, start_time_text);
		cv.put(KEY_END_TEXT, end_time_text);
		cv.put(KEY_CHECK, 1);
		cv.put(KEY_LOC, location);
		cv.put(KEY_LAT, latitude);
		cv.put(KEY_LONG, longitude);
		cv.put(KEY_ALERT, alert_time);
		cv.put(KEY_START, start_time);
		cv.put(KEY_END, end_time);
		cv.put(KEY_STATUS, status);
		cv.put(KEY_DESCR, description);
		cv.put(KEY_EVENT_ID, event_id);
		cv.put(KEY_EXTRA_ID, extra_id);
		cv.put(KEY_TITLE, title);
		cv.put(KEY_TYPE, type);
		cv.put(KEY_NEW, 0);
		
		return Database_Events.update(DATABASE_TABLE_EVENTS_NAME, cv, KEY_EVENT_ID + "=" + event_id, null)>0;
	}
	
	public void createEntryForClassesTable(int user_id, String start_time_text, String end_time_text, int check_status, String location, 
			double latitude, double longitude, int alert_time, int start_time, int end_time, int status,
			String description, int course_id, String course_name, String course_code, int course_time_id,
			int is_biweekly, int day_of_week, String color, String icon){
		ContentValues cv = new ContentValues();
		cv.put(KEY_USER_ID, user_id);
		cv.put(KEY_START_TEXT, start_time_text);
		cv.put(KEY_END_TEXT, end_time_text);
		cv.put(KEY_CHECK, 0);
		cv.put(KEY_LOC, location);
		cv.put(KEY_LAT, latitude);
		cv.put(KEY_LONG, longitude);
		cv.put(KEY_ALERT, alert_time);
		cv.put(KEY_START, start_time);
		cv.put(KEY_END, end_time);
		cv.put(KEY_STATUS, status);
		cv.put(KEY_DESCR, description);
		cv.put(KEY_COURSE_ID, course_id);
		cv.put(KEY_COURSE_NAME, course_name);
		cv.put(KEY_COURSE_CODE, course_code);
		cv.put(KEY_COURSE_TIME_ID, course_time_id);
		cv.put(KEY_IS_BIWEEKLY, is_biweekly);
		cv.put(KEY_DAY_OF_WEEK, day_of_week);
		cv.put(KEY_COLOR, color);
		cv.put(KEY_ICON, icon);
		cv.put(KEY_NEW, 1);
			
		Database_Classes.insert(DATABASE_TABLE_CLASSES_NAME, null, cv);
	}
	
	public boolean updateEntryForClassesTable(int user_id, String start_time_text, String end_time_text, int check_status, String location, 
			double latitude, double longitude, int alert_time, int start_time, int end_time, int status,
			String description, int course_id, String course_name, String course_code, int course_time_id,
			int is_biweekly, int day_of_week, String color, String icon){
		ContentValues cv = new ContentValues();
		cv.put(KEY_USER_ID, user_id);
		cv.put(KEY_START_TEXT, start_time_text);
		cv.put(KEY_END_TEXT, end_time_text);
		cv.put(KEY_CHECK, 1);
		cv.put(KEY_LOC, location);
		cv.put(KEY_LAT, latitude);
		cv.put(KEY_LONG, longitude);
		cv.put(KEY_ALERT, alert_time);
		cv.put(KEY_START, start_time);
		cv.put(KEY_END, end_time);
		cv.put(KEY_STATUS, status);
		cv.put(KEY_DESCR, description);
		cv.put(KEY_COURSE_ID, course_id);
		cv.put(KEY_COURSE_NAME, course_name);
		cv.put(KEY_COURSE_CODE, course_code);
		cv.put(KEY_COURSE_TIME_ID, course_time_id);
		cv.put(KEY_IS_BIWEEKLY, is_biweekly);
		cv.put(KEY_DAY_OF_WEEK, day_of_week);
		cv.put(KEY_COLOR, color);
		cv.put(KEY_ICON, icon);
		cv.put(KEY_NEW, 0);
		
		return Database_Classes.update(DATABASE_TABLE_CLASSES_NAME, cv, KEY_COURSE_TIME_ID + "=" + course_time_id, null)>0;
	}
	
	public void findEventID(int user_id, String start_time_text, String end_time_text, int check_status, String location, 
			double latitude, double longitude, int alert_time, int start_time, int end_time, int status,
			String description, int event_id, int extra_id, String title, int type){
		Cursor c = Database_Events.query(DATABASE_TABLE_EVENTS_NAME, eventsTableColumns, KEY_EVENT_ID + "=" + event_id, null, null, null, null);
		
		if (c.getCount() == 0){
			createEntryForEventsTable(user_id, start_time_text, end_time_text, check_status, location, latitude, 
					longitude, alert_time, start_time, end_time, status, description,
					event_id, extra_id, title, type);
		} else {
			c.moveToFirst();
			
			updateEntryForEventsTable(user_id, start_time_text, end_time_text, check_status, location, latitude, 
					longitude, alert_time, start_time, end_time, status, description,
					event_id, extra_id, title, type);
		}	
		
		//Log.i("cursor: ", String.valueOf(c.getCount()));
		c.close();
	}
	
	public void findCourseTimeID(int user_id, String start_time_text, String end_time_text, int check_status, String location, 
			double latitude, double longitude, int alert_time, int start_time, int end_time, int status,
			String description, int course_id, String course_name, String course_code, int course_time_id,
			int is_biweekly, int day_of_week, String color, String icon){
		Cursor c = Database_Classes.query(DATABASE_TABLE_CLASSES_NAME, classesTableColumns, KEY_COURSE_TIME_ID + "=" + course_time_id, null, null, null, null);
		
		if (c.getCount() == 0){
			createEntryForClassesTable(user_id, start_time_text, end_time_text, check_status, location, latitude, 
					longitude, alert_time, start_time, end_time, status, description,
					course_id, course_name, course_code, course_time_id, is_biweekly,
					day_of_week, color, icon);
		} else {
			c.moveToFirst();
			
			updateEntryForClassesTable(user_id, start_time_text, end_time_text, check_status, location, latitude, 
					longitude, alert_time, start_time, end_time, status, description,
					course_id, course_name, course_code, course_time_id, is_biweekly,
					day_of_week, color, icon);
		}
		
		//Log.i("cursor: ", String.valueOf(c.getCount()));
		c.close();
	}
	
	public Cursor findEventOnThatDay(int epoch_start_day, int epoch_end_day){
		Cursor allRows = Database_Events.query(DATABASE_TABLE_EVENTS_NAME, eventsTableColumns, KEY_START + " < " + epoch_end_day + " AND " + KEY_END + " > " + epoch_start_day, null, null, null, KEY_START);
		
		return allRows;
	}
	
	public Cursor findClassDayOfWeek(int day_of_week){
		Cursor allRows = Database_Classes.query(DATABASE_TABLE_CLASSES_NAME, classesTableColumns, KEY_DAY_OF_WEEK + "=" + day_of_week, null, null, null, KEY_START);
		
		return allRows;
	}
	
	public Cursor findAllClass(){
		Cursor allRows = Database_Classes.query(DATABASE_TABLE_CLASSES_NAME, classesTableColumns, null, null, null, null, KEY_START);
		
		return allRows;
	}
	
	/*
	public int getHighestLevel(){
		String[] columns = new String[]{KEY_ROWID, KEY_LEVEL, KEY_SCORE};
		Cursor c = Database_Events.query(DATABASE_TABLE_EVENTS_NAME, columns, null, null, null, null, KEY_LEVEL + " DESC");
		
		int max = -1;
		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){
			if (c.getInt(1) > max){
				max = c.getInt(1);
			}
		}
		
		return max;
	}
	*/
	
	public void deleteEvent(int event_id){
		Database_Events.delete(DATABASE_TABLE_EVENTS_NAME, KEY_EVENT_ID + "=" + event_id, null);
	}
	
	public void deleteEvents(){
		Database_Events.delete(DATABASE_TABLE_EVENTS_NAME, KEY_CHECK + "=0 AND " + KEY_NEW + "=0", null);
		
		ContentValues cv = new ContentValues();
		cv.put(KEY_CHECK, 0);
		cv.put(KEY_NEW, 0);
		
		Database_Events.update(DATABASE_TABLE_EVENTS_NAME, cv, null, null);
	}
	
	public void deleteClass(int course_time_id){
		Database_Classes.delete(DATABASE_TABLE_CLASSES_NAME, KEY_COURSE_TIME_ID + "=" + course_time_id, null);
	}
	
	public void deleteClasses(int course_id){
		Database_Classes.delete(DATABASE_TABLE_CLASSES_NAME, KEY_COURSE_ID + "=" + course_id, null);
	}
	
	public void deleteClasses(){
		Database_Classes.delete(DATABASE_TABLE_CLASSES_NAME, KEY_CHECK + "=0 AND " + KEY_NEW + "=0", null);
		
		ContentValues cv = new ContentValues();
		cv.put(KEY_CHECK, 0);
		cv.put(KEY_NEW, 0);
		
		Database_Classes.update(DATABASE_TABLE_CLASSES_NAME, cv, null, null);
	}
}
