package sample;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteSample extends SQLiteOpenHelper {
	static final String DB_NAME = "sample.db";
	static final int VERSION = 100;

	public interface Column {
		public String name();
		public String type();
		public String where();
	}

	// テーブル定義
	private static final String SAMPLE_TABLE = "SAMPLE_TABLE";
	public enum SampleCols implements Column {
		_ID("integer primary key autoincrement"),
		FIRST_NAME("text"),
		SECOND_NAME("text");

		// --- 以下、定形 ---
		private String mType;
		private String mWhere;

		SampleCols(String type) {
			mType = type;
			mWhere = name() + "=?";
		}
		// @formatter:off
		public String type() {return mType;}
		public String where() {return mWhere;}
		public long getLong(Cursor cursor) {return cursor.getLong(cursor.getColumnIndex(name()));}
		public int getInt(Cursor cursor) {return cursor.getInt(cursor.getColumnIndex(name()));}
		public String getString(Cursor cursor) {return cursor.getString(cursor.getColumnIndex(name()));}
		public void put(ContentValues values, long val) {values.put(name(), val);}
		public void put(ContentValues values, int val) {values.put(name(), val);}
		public void put(ContentValues values, String val) {values.put(name(), val);}
        // @formatter:on
	}
	private SampleBean toBean(Cursor cursor) {
		SampleBean data = new SampleBean(
				SampleCols._ID.getLong(cursor),
				SampleCols.FIRST_NAME.getString(cursor),
				SampleCols.SECOND_NAME.getString(cursor)
				);
		return data;
	}
	private ContentValues fromBean(SampleBean bean) {
		ContentValues values = new ContentValues();
		SampleCols._ID.put(values, bean.getId());
		SampleCols.FIRST_NAME.put(values, bean.getFirstName());
		SampleCols.SECOND_NAME.put(values, bean.getSecondName());
		return values;
	}
	
	
	SQLiteSample(Context context) {
		super(context, DB_NAME, null, VERSION);
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(getCreateTableDDL(SAMPLE_TABLE, SampleCols.values()));
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DELETE TABLE " + SAMPLE_TABLE + ";");
		onCreate(db);
	}

	public SampleBean query(long id) {
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query(SAMPLE_TABLE, null, SampleCols._ID.where(), toArgument(id), null,null,null);
		if (!cursor.moveToNext()) return null;
		return toBean(cursor);
	}
	public long insert(SampleBean bean) {
		SQLiteDatabase db = getWritableDatabase();
		long id = db.insert(SAMPLE_TABLE, null, fromBean(bean));
		return id;
	}
	public long update(SampleBean bean) {
		SQLiteDatabase db = getWritableDatabase();
		long id = db.update(SAMPLE_TABLE, fromBean(bean), SampleCols._ID.where(), toArgument(bean.getId()));
		return id;
	}
	public long delete(SampleBean bean) {
		SQLiteDatabase db = getWritableDatabase();
		long id = db.delete(SAMPLE_TABLE, SampleCols._ID.where(), toArgument(bean.getId()));
		return id;
	}
	
	private String[] toArgument(long id) {
		return  new String[] { Long.toString(id) };
	}

	private String getCreateTableDDL(String table, Column[] columns) {
		StringBuilder sbuf = new StringBuilder();
		sbuf.append("CREATE TABLE ").append(table).append('(');
		for (Column column : columns) {
			sbuf.append(column.name()).append(' ').append(column.type()).append(',');
		}
		sbuf.setLength(sbuf.length() - 1);
		sbuf.append(");");
		return sbuf.toString();
	}


}
