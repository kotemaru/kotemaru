package org.kotemaru.android.postit;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.LongSparseArray;

public class PostItDataProvider extends ContentProvider {
	private static final String CONTENT_URI_BASE = "content://"
			+ PostItDataProvider.class.getCanonicalName().toLowerCase(Locale.US);
	public static final String MIMETYPE_DIR = "vnd.android.cursor.dir/vnd.kotemaru.postitmain";
	public static final String MIMETYPE_ITEM = "vnd.android.cursor.item/vnd.kotemaru.postitmain";

	public static final Uri CONTENT_URI = Uri.parse(CONTENT_URI_BASE);

	public static class Columns {
		public static final String ID = "ID";
		public static final String ENABLED = "ENABLED";
		public static final String COLOR = "COLOR";
		public static final String POS_X = "POS_X";
		public static final String POS_Y = "POS_Y";
		public static final String WIDTH = "WIDTH";
		public static final String HEIGHT = "HEIGHT";
		public static final String FONT_SIZE = "FONT_SIZE";
		public static final String MEMO = "MEMO";
	}

	private static final String MAIN_TABLE = "POST_IT_MAIN";
	private static String[][] COLUMNS_POST_IT_MAIN = {
			{ Columns.ID, "integer primary key autoincrement" },
			{ Columns.ENABLED, "integer" },
			{ Columns.COLOR, "integer" },
			{ Columns.POS_X, "integer" },
			{ Columns.POS_Y, "integer" },
			{ Columns.WIDTH, "integer" },
			{ Columns.HEIGHT, "integer" },
			{ Columns.FONT_SIZE, "integer" },
			{ Columns.MEMO, "text" }
	};

	private static class SqlHelper extends SQLiteOpenHelper {
		static final String DB_NAME = "post_it.db";
		static final int VERSION = 100;

		SqlHelper(Context context) {
			super(context, DB_NAME, null, VERSION);
		}
		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(getCreateTableDDL(MAIN_TABLE, COLUMNS_POST_IT_MAIN));
		}

		private String getCreateTableDDL(String table, String[][] colsDef) {
			StringBuilder sbuf = new StringBuilder();
			sbuf.append("CREATE TABLE ").append(table).append('(');
			for (String[] colDef : colsDef) {
				sbuf.append(colDef[0].toString()).append(' ').append(colDef[1]).append(',');
			}
			sbuf.setLength(sbuf.length() - 1);
			sbuf.append(");");
			return sbuf.toString();
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// This is first version.
			db.execSQL("DELETE TABLE " + MAIN_TABLE + ";");
			onCreate(db);
		}
	}

	private SqlHelper sqlHelper;

	@Override
	public boolean onCreate() {
		sqlHelper = new SqlHelper(getContext());
		return true;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = sqlHelper.getWritableDatabase();
		long id = db.replace(MAIN_TABLE, null, values);
		return Uri.parse(CONTENT_URI_BASE + '/' + MAIN_TABLE + '/' + id);
	}
	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		SQLiteDatabase db = sqlHelper.getWritableDatabase();
		int n = db.update(MAIN_TABLE, values, selection, selectionArgs);
		return n;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteDatabase db = sqlHelper.getReadableDatabase();
		Cursor cursor = db.query(MAIN_TABLE,
				projection, selection, selectionArgs, null, null, sortOrder);
		return cursor;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = sqlHelper.getWritableDatabase();
		db.delete(MAIN_TABLE, selection, selectionArgs);
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		List<String> paths = uri.getPathSegments();
		if (paths.size() >= 2) {
			return MIMETYPE_ITEM;
		} else {
			return MIMETYPE_DIR;
		}
	}

	public static PostItData toPostItData(Cursor cursor) {
		PostItData data = new PostItData(
				cursor.getLong(cursor.getColumnIndex(Columns.ID)),
				cursor.getInt(cursor.getColumnIndex(Columns.ENABLED)),
				cursor.getInt(cursor.getColumnIndex(Columns.COLOR)),
				cursor.getInt(cursor.getColumnIndex(Columns.POS_X)),
				cursor.getInt(cursor.getColumnIndex(Columns.POS_Y)),
				cursor.getInt(cursor.getColumnIndex(Columns.WIDTH)),
				cursor.getInt(cursor.getColumnIndex(Columns.HEIGHT)),
				cursor.getInt(cursor.getColumnIndex(Columns.FONT_SIZE)),
				cursor.getString(cursor.getColumnIndex(Columns.MEMO))
				);
		return data;
	}
	public static ContentValues fromPostItData(ContentValues values, PostItData data) {
		if (values == null) values = new ContentValues();
		values.put(Columns.ID, data.getId());
		values.put(Columns.ENABLED, data.getEnabled());
		values.put(Columns.COLOR, data.getColor());
		values.put(Columns.POS_X, data.getPosX());
		values.put(Columns.POS_Y, data.getPosY());
		values.put(Columns.WIDTH, data.getWidth());
		values.put(Columns.HEIGHT, data.getHeight());
		values.put(Columns.FONT_SIZE, data.getFontSize());
		values.put(Columns.MEMO, data.getMemo());
		return values;
	}
	
	public static PostItData getPostItData(Context context, long id) {
		Cursor cursor = context.getContentResolver().query(PostItDataProvider.CONTENT_URI, null, 
				Columns.ID + "=?",	new String[] { Long.toString(id) }, null);
		if (cursor.moveToNext()) {
			PostItData data = PostItDataProvider.toPostItData(cursor);
			return data;
		}
		return null;
	}
	
	public static List<PostItData> getAllPostItData(Context context) {
		List<PostItData> list = new ArrayList<PostItData>();
		Cursor cursor = context.getContentResolver().query(PostItDataProvider.CONTENT_URI, null, null, null, null);
		try {
			while (cursor.moveToNext()) {
				PostItData data = PostItDataProvider.toPostItData(cursor);
				list.add(data);
			}
		} finally {
			cursor.close();
		}
		return list;
	}
	public static LongSparseArray<PostItData> getPostItDataMap(Context context) {
		LongSparseArray<PostItData> map = new LongSparseArray<PostItData>();
		Cursor cursor = context.getContentResolver().query(PostItDataProvider.CONTENT_URI, null, null, null, null);
		try {
			while (cursor.moveToNext()) {
				PostItData data = PostItDataProvider.toPostItData(cursor);
				map.put(data.getId(), data);
			}
		} finally {
			cursor.close();
		}
		return map;
	}
	
	
	public static void setAllPostItData(Context context, List<PostItData> list) {
		ContentResolver content = context.getContentResolver();
		ContentValues values = new ContentValues();
		for (PostItData data : list) {
			content.insert(PostItDataProvider.CONTENT_URI, PostItDataProvider.fromPostItData(values, data));
		}
	}

	public static long createPostItData(Context context, PostItData data) {
		ContentResolver content = context.getContentResolver();
		ContentValues values =PostItDataProvider.fromPostItData(null, data);
		values.remove(Columns.ID);
		Uri uri = content.insert(PostItDataProvider.CONTENT_URI, values);
		long id = Long.parseLong(uri.getLastPathSegment());
		return id;
	}
	public static void updatePostItData(Context context, PostItData data) {
		ContentResolver content = context.getContentResolver();
		content.insert(PostItDataProvider.CONTENT_URI, PostItDataProvider.fromPostItData(null, data));
	}
	public static void removePostItData(Context context, PostItData data) {
		ContentResolver content = context.getContentResolver();
		content.delete(PostItDataProvider.CONTENT_URI, Columns.ID + "=?",
				new String[] { Long.toString(data.getId()) });
	}

}
