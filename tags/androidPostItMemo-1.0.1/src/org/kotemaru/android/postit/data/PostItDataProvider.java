package org.kotemaru.android.postit.data;

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
import android.support.v4.util.LongSparseArray;

/**
 * 付箋データDB。
 * <li>ContentProviderになっているが公開はしていない。
 * <li>テーブルは１つだけで新規作成、更新、削除、一覧取得の基本動作を行う。
 * <li>
 * @author kotemaru.org
 */
public class PostItDataProvider extends ContentProvider {
	private static final String CONTENT_URI_BASE = "content://"
			+ PostItDataProvider.class.getCanonicalName().toLowerCase(Locale.US);
	public static final String MIMETYPE_DIR = "vnd.android.cursor.dir/vnd.kotemaru.postitmain";
	public static final String MIMETYPE_ITEM = "vnd.android.cursor.item/vnd.kotemaru.postitmain";

	public static final Uri CONTENT_URI = Uri.parse(CONTENT_URI_BASE);

	private static final String MAIN_TABLE = "POST_IT_MAIN";

	public interface Column {
		public String name();
		public String type();
	}
	
	/**
	 * テーブル定義。typeはSQL型。
	 */
	public enum POST_IT_COLS implements Column {
		_ID("integer primary key autoincrement"),
		ENABLED("integer"), // 0=false, 1=true
		COLOR("integer"), // PostItColor class
		POS_X("integer"), // px
		POS_Y("integer"), // px
		WIDTH("integer"), // dp
		HEIGHT("integer"), // dp
		FONT_SIZE("integer"), // sp
		TIMER_IS_REPEATE("integer"),
		TIMER_PATTERN("text"),
		TIMER("integer"),
		MEMO("text"), ;

		private String mType;
		private String mWhere;

		POST_IT_COLS(String type) {
			mType = type;
			mWhere = name() + "=?";
		}
		@Override
		public String type() {
			return mType;
		}
		public String where() {
			return mWhere;
		}
		// @formatter:off
		public long getLong(Cursor cursor) {return cursor.getLong(cursor.getColumnIndex(name()));}
		public int getInt(Cursor cursor) {return cursor.getInt(cursor.getColumnIndex(name()));}
		public String getString(Cursor cursor) {return cursor.getString(cursor.getColumnIndex(name()));}
		public void put(ContentValues values, long val) {values.put(name(), val);}
		public void put(ContentValues values, int val) {values.put(name(), val);}
		public void put(ContentValues values, String val) {values.put(name(), val);}
        // @formatter:on
	}

	private static class SqlHelper extends SQLiteOpenHelper {
		static final String DB_NAME = "post_it.db";
		static final int VERSION = 100;

		SqlHelper(Context context) {
			super(context, DB_NAME, null, VERSION);
		}
		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(getCreateTableDDL(MAIN_TABLE, POST_IT_COLS.values()));
		}

		/**
		 * カラム定義から CREATE TABLE 文を生成する。
		 * @param table テーブル名
		 * @param columns カラム定義
		 * @return SQL文
		 */
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

	// --------------------------------------------------------------------------------------------
	// 以下、コンビニメソッド。

	/**
	 * DBレコードからBeanに変換する。
	 * @param cursor
	 * @return 付箋データBean
	 */
	public static PostItData toPostItData(Cursor cursor) {
		PostItData data = new PostItData(
				POST_IT_COLS._ID.getLong(cursor),
				POST_IT_COLS.ENABLED.getInt(cursor),
				POST_IT_COLS.COLOR.getInt(cursor),
				POST_IT_COLS.POS_X.getInt(cursor),
				POST_IT_COLS.POS_Y.getInt(cursor),
				POST_IT_COLS.WIDTH.getInt(cursor),
				POST_IT_COLS.HEIGHT.getInt(cursor),
				POST_IT_COLS.FONT_SIZE.getInt(cursor),
				POST_IT_COLS.MEMO.getString(cursor)
				);
		return data;
	}
	/**
	 * BeanからDBレコードに変換する。
	 * @param values DBレコード。nullならば内部で生成。
	 * @param data 付箋データBean
	 * @return DBレコード
	 */
	public static ContentValues fromPostItData(ContentValues values, PostItData data) {
		if (values == null) values = new ContentValues();
		POST_IT_COLS._ID.put(values, data.getId());
		POST_IT_COLS.ENABLED.put(values, data.getEnabled());
		POST_IT_COLS.COLOR.put(values, data.getColor());
		POST_IT_COLS.POS_X.put(values, data.getPosX());
		POST_IT_COLS.POS_Y.put(values, data.getPosY());
		POST_IT_COLS.WIDTH.put(values, data.getWidth());
		POST_IT_COLS.HEIGHT.put(values, data.getHeight());
		POST_IT_COLS.FONT_SIZE.put(values, data.getFontSize());
		POST_IT_COLS.MEMO.put(values, data.getMemo());
		return values;
	}

	/**
	 * IDから付箋データを取得する。
	 * @param context
	 * @param id 付箋ID
	 * @return 付箋データ
	 */
	public static PostItData getPostItData(Context context, long id) {
		Cursor cursor = context.getContentResolver().query(PostItDataProvider.CONTENT_URI, null,
				POST_IT_COLS._ID.where(), new String[] { Long.toString(id) }, null);
		if (cursor.moveToNext()) {
			PostItData data = PostItDataProvider.toPostItData(cursor);
			return data;
		}
		return null;
	}

	/**
	 * すべての付箋データを取得する。
	 * @param context
	 * @return 付箋データのリスト。nullは無い。
	 */
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

	/**
	 * すべての付箋データをマップで取得する。
	 * @param context
	 * @return 付箋IDをキーとする付箋データのマップ。nullは無い。
	 */
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

	/**
	 * 付箋データの一覧から一括更新。
	 * @param context
	 * @param list 付箋データの一覧
	 */
	public static void setAllPostItData(Context context, List<PostItData> list) {
		ContentResolver content = context.getContentResolver();
		ContentValues values = new ContentValues();
		for (PostItData data : list) {
			content.insert(PostItDataProvider.CONTENT_URI, PostItDataProvider.fromPostItData(values, data));
		}
	}

	/**
	 * 付箋データの新規作成。
	 * @param context
	 * @param data 元になる付箋データ。IDは無視。
	 * @return 生成された付箋ID
	 */
	public static long createPostItData(Context context, PostItData data) {
		ContentResolver content = context.getContentResolver();
		ContentValues values = PostItDataProvider.fromPostItData(null, data);
		values.remove(POST_IT_COLS._ID.name());
		Uri uri = content.insert(PostItDataProvider.CONTENT_URI, values);
		long id = Long.parseLong(uri.getLastPathSegment());
		return id;
	}

	/**
	 * 付箋データの更新。
	 * @param context
	 * @param data 付箋データ
	 */
	public static void updatePostItData(Context context, PostItData data) {
		ContentResolver content = context.getContentResolver();
		content.insert(PostItDataProvider.CONTENT_URI, PostItDataProvider.fromPostItData(null, data));
	}
	/**
	 * 付箋データの削除。
	 * @param context
	 * @param data 付箋データ。使うのはIDだけ。
	 */
	public static void removePostItData(Context context, PostItData data) {
		ContentResolver content = context.getContentResolver();
		content.delete(PostItDataProvider.CONTENT_URI, POST_IT_COLS._ID.where(),
				new String[] { Long.toString(data.getId()) });
	}

}
