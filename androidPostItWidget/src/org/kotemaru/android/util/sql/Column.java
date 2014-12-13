package org.kotemaru.android.util.sql;

import java.lang.reflect.Field;

import android.content.ContentValues;
import android.database.Cursor;

public class Column {
	private final Table mTable;
	private String mName;
	private final String mType;
	private String mWhere;
	

	public Column(Table table, String type) {
		mTable = table;
		mType = type;
	}
	public Table getTable() {
		return mTable;
	}
	public String name() {
		if (mName == null) mName = findName(mTable);
		return mName;
	}
	public String type() {
		return mType;
	}
	public String where() {
		if (mWhere == null) mWhere = name() + "=?";
		return mWhere;
	}
	@Override
	public String toString() {
		return name();
	}

	// @formatter:off
	public long getLong(Cursor cursor) {return cursor.getLong(cursor.getColumnIndex(name()));}
	public int getInt(Cursor cursor) {return cursor.getInt(cursor.getColumnIndex(name()));}
	public String getString(Cursor cursor) {return cursor.getString(cursor.getColumnIndex(name()));}
	public void put(ContentValues values, long val) {values.put(name(), val);}
	public void put(ContentValues values, int val) {values.put(name(), val);}
	public void put(ContentValues values, String val) {values.put(name(), val);}
	// @formatter:on

	public String findName(Table table) {
		try {
			Field[] fields = table.getClass().getFields();
			for (Field field : fields) {
				if (field.get(table) == this) return field.getName();
			}
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Not found public field me.", e);
		}
		throw new RuntimeException("Not found public field me.");
	}

}
