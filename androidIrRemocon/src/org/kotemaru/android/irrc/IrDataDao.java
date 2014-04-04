package org.kotemaru.android.irrc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;
import android.webkit.JavascriptInterface;

/**
 * SqlLite 赤外線リモコンデータ保存用テーブル。
 */
public class IrDataDao extends SQLiteOpenHelper {
	private static final String TAG = "IrDataDao";

	public static final String DB_NAME = "IrData.db";
	// テーブル定義を更新する場合には１増やす。
	public static final int DB_VERSION = 2;

	public static final String PAGE_NAME = "page_name";
	public static final String BUTTON_ID = "button_id";
	public static final String IR_DATA = "ir_data";
	public static final String ATTRIBUTES = "attributes";
	public static final String UPDATE_TIME = "update_time";

	public static final String TEXT = "text";
	public static final String BLOB = "blob";
	public static final String INTEGER = "integer";

	// テーブル定義
	public static final String TABLE_NAME = "ir_data";
	public static final String[][] COLUMNS = {
			{ PAGE_NAME, TEXT, "not null" },			// リモコンのHTMLページ名
			{ BUTTON_ID, TEXT, "not null" },			// ボタンのID.
			{ IR_DATA, BLOB },							// 64byte固定長
			{ ATTRIBUTES, TEXT },						// 属性 JSON文字列
			{ UPDATE_TIME, INTEGER },					// 更新日時
	};
	public static final String PRIMARY_KEY = "primary key(" + PAGE_NAME + ", " + BUTTON_ID + ")";
	public static final String PKEY_WHERE = PAGE_NAME + "=? and " + BUTTON_ID + "=?";

	private Context context;
	
	public IrDataDao(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		this.context = context;
	}

	/**
	 * テーブル定義からDDLを合成してテーブルを作成。
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		StringBuilder ddl = new StringBuilder(256);
		ddl.append("create table ").append(TABLE_NAME).append("(");
		for (int i = 0; i < COLUMNS.length; i++) {
			if (i > 0) ddl.append(",");
			String[] colmunDef = COLUMNS[i];
			for (int j = 0; j < colmunDef.length; j++) {
				ddl.append(colmunDef[j]).append(" ");
			}
		}
		if (PRIMARY_KEY != null) {
			ddl.append(",").append(PRIMARY_KEY);
		}
		ddl.append(");");
		db.execSQL(ddl.toString());
	}

	/**
	 * テーブル定義更新処理。
	 * - 本来はバージョン差異に合わせた ALTER 文を発行する。
	 * - データをクリアして良い場合は DROP/CREATE TABLE して良い。
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table " + TABLE_NAME + ";");
		onCreate(db);
	}

	/**
	 * データの登録。
	 * - insert or update となる。
	 * @param pageName    ページ名(pkey1)
	 * @param buttonId    ボタンID(pkey2)
	 * @param irData      赤外線データ
	 * @param attributes  属性
	 */
	@JavascriptInterface
	public void putIrData(String pageName, String buttonId, Bytes irData, String attributes) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(PAGE_NAME, pageName);
		values.put(BUTTON_ID, buttonId);
		values.put(IR_DATA, irData.getBytes());
		values.put(ATTRIBUTES, attributes);
		values.put(UPDATE_TIME, System.currentTimeMillis());

		long id = db.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
		if (id == -1) {
			db.update(TABLE_NAME, values, PKEY_WHERE, new String[] { pageName, buttonId });
		}
		Log.i(TAG,"Update irData "+pageName+"#"+buttonId);
	}

	/**
	 * データ取得
	 * @param pageName
	 * @param buttonId
	 * @return
	 * @throws Exception
	 */
	@JavascriptInterface
	public Bytes getIrData(String pageName, String buttonId) throws Exception {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, new String[] { IR_DATA },
				PKEY_WHERE, new String[] { pageName, buttonId }, null, null, null);
		try {
			cursor.moveToFirst();
			byte[] irData = cursor.getCount() > 0 ? cursor.getBlob(0) : null;
			if (irData == null) return null;
			return new Bytes(irData);
		} finally {
			cursor.close();
		}
	}

	@JavascriptInterface
	public String getAttributes(String pageName, String buttonId) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, new String[] { ATTRIBUTES },
				PKEY_WHERE, new String[] { pageName, buttonId }, null, null, null);
		try {
			cursor.moveToFirst();
			String attributes = cursor.getCount() > 0 ? cursor.getString(0) : null;
			return attributes;
		} finally {
			cursor.close();
		}
	}

	public void backup(String dirName, String fileName) throws IOException {
		File dir = Environment.getExternalStoragePublicDirectory(dirName);
		dir.mkdirs();
		File file = new File(dir, fileName);
		File dbFile = context.getDatabasePath(DB_NAME);
		copyFile(dbFile, file);
	}
	public void restore(String dirName, String fileName) throws IOException {
		File dir = Environment.getExternalStoragePublicDirectory(dirName);
		File file = new File(dir, fileName);
		File dbFile = context.getDatabasePath(DB_NAME);
		copyFile(file, dbFile);
	}

	private void copyFile(File inFile, File outFile) throws IOException {
		outFile.getParentFile().mkdirs();
		
		@SuppressWarnings("resource")
		FileChannel inChannel = new FileInputStream(inFile).getChannel();
		try {
			@SuppressWarnings("resource")
			FileChannel outChannel = new FileOutputStream(outFile).getChannel();
			try {
				inChannel.transferTo(0, inChannel.size(), outChannel);
			} finally {
				outChannel.close();
			}
		} finally {
			inChannel.close();
		}
	}

}
