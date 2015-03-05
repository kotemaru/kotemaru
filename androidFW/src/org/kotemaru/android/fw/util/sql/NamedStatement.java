package org.kotemaru.android.fw.util.sql;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.ParcelFileDescriptor;

public class NamedStatement {
	public static final String TAG = NamedStatement.class.getSimpleName();
	private SQLiteStatement mStatement;
	private BindingMap mBindingMap;

	public NamedStatement(SQLiteDatabase db, String sql) {
		String pureSql = parse(sql);
		mStatement = db.compileStatement(pureSql);
		mBindingMap = new BindingMap(mStatement);
	}

	public SQLiteStatement getStatement() {
		return mStatement;
	}
	public BindingMap getBindingMap() {
		return mBindingMap;
	}

	private String parse(String sql) {
		StringBuilder sbuf = new StringBuilder();
		int offset = 0;
		int nameStart = 0;
		int bindIndex = 0;
		while ((nameStart = sql.indexOf("?{", offset)) >= 0) {
			sbuf.append(sql.substring(offset, nameStart - 1));
			int nameEnd = sql.indexOf('}', nameStart);
			if (nameStart == -1) throw new RuntimeException("Parse error: " + sql.substring(nameStart));
			String name = sql.substring(nameStart + 2, nameEnd);
			addBinding(name, bindIndex++);
			offset = nameEnd + 1;
		}
		return sbuf.toString();
	}

	private void addBinding(String name, int idx) {
		Binding binding = mBindingMap.get(name);
		if (binding == null) {
			binding = new Binding(name);
			mBindingMap.put(name, binding);
		}
		binding.addIndex(idx);
	}

	public void clearBindings() {
		mStatement.clearBindings();
	}
	public void bindNull(String name) {
		Binding binding = mBindingMap.getBinding(name);
		for (int i = 0; i < binding.mIndexsSize; i++) {
			mStatement.bindNull(binding.mIndexs[i]);
		}
	}
	public void bindString(String name, String value) {
		Binding binding = mBindingMap.getBinding(name);
		for (int i = 0; i < binding.mIndexsSize; i++) {
			mStatement.bindString(binding.mIndexs[i], value);
		}
	}
	public void bindLong(String name, long value) {
		Binding binding = mBindingMap.getBinding(name);
		for (int i = 0; i < binding.mIndexsSize; i++) {
			mStatement.bindLong(binding.mIndexs[i], value);
		}
	}
	public void bindDouble(String name, double value) {
		Binding binding = mBindingMap.getBinding(name);
		for (int i = 0; i < binding.mIndexsSize; i++) {
			mStatement.bindDouble(binding.mIndexs[i], value);
		}
	}
	public void bindBlob(String name, byte[] value) {
		Binding binding = mBindingMap.getBinding(name);
		for (int i = 0; i < binding.mIndexsSize; i++) {
			mStatement.bindBlob(binding.mIndexs[i], value);
		}
	}

	public void execute() {
		mStatement.execute();
	}
	public int executeUpdateDelete() {
		return mStatement.executeUpdateDelete();
	}
	public long executeInsert() {
		return mStatement.executeInsert();
	}
	public long simpleQueryForLong() {
		return mStatement.simpleQueryForLong();
	}
	public String simpleQueryForString() {
		return mStatement.simpleQueryForString();
	}
	public ParcelFileDescriptor simpleQueryForBlobFileDescriptor() {
		return mStatement.simpleQueryForBlobFileDescriptor();
	}
}
