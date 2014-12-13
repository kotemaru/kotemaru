package org.kotemaru.android.util.sql;

import java.util.ArrayList;
import java.util.List;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

public class Sql {
	public final static String BIND = "?";
	protected StringBuilder mBuff = new StringBuilder();
	protected List<Bind> mBinds = new ArrayList<Bind>();
	protected SQLiteStatement mSQLiteStatement;

	public Sql init(Object... parts) {
		mSQLiteStatement = null;
		mBuff.setLength(0);
		return sql(parts);
	}
	protected Sql sql(Object... parts) {
		for (Object part : parts) {
			String str = part.toString();
			if (str.equals(BIND)) {
				addBind((Bind) part);
			}
			mBuff.append(str);
		}
		mSQLiteStatement = null;
		return this;
	}
	public Sql addBind(Bind bind) {
		mBinds.add(bind);
		return this;
	}
	public String toString() {
		return mBuff.toString();
	}

	public SQLiteStatement getStatement(SQLiteDatabase db) {
		if (mSQLiteStatement == null) {
			mSQLiteStatement = db.compileStatement(mBuff.toString());
		}
		return mSQLiteStatement;
	}
	public void execute(SQLiteDatabase db) {
		SQLiteStatement st = getStatement(db);
		st.clearBindings();
		for (int idx = 0; idx < mBinds.size(); idx++) {
			Bind bind = mBinds.get(idx);
			switch (bind.mType) {
			case BLOB:
				st.bindBlob(idx, bind.mValueBlob);
				break;
			case DOUBLE:
				st.bindDouble(idx, bind.mValueDouble);
				break;
			case LONG:
				st.bindLong(idx, bind.mValueLong);
				break;
			case NULL:
				st.bindNull(idx);
				break;
			case STRING:
				st.bindString(idx, bind.mValueString);
				break;
			}
		}
		st.execute();
	}


	private enum ValueType {
		BLOB, DOUBLE, LONG, NULL, STRING
	}

	public static class Bind {
		private ValueType mType;
		private byte[] mValueBlob;
		private double mValueDouble;
		private long mValueLong;
		private String mValueString;

		public void setBlob(byte[] value) {
			mType = ValueType.BLOB;
			mValueBlob = value;
			mValueString = null;
		}
		public void setDouble(double value) {
			mType = ValueType.DOUBLE;
			mValueDouble = value;
		}
		public void setLong(long value) {
			mType = ValueType.LONG;
			mValueLong = value;
		}
		public void setNull(int index) {
			mType = ValueType.NULL;
			mValueBlob = null;
			mValueString = null;
		}
		public void setString(String value) {
			mType = ValueType.STRING;
			mValueBlob = null;
			mValueString = value;
		}

		public String toString() {
			return Sql.BIND;
		}
	}
}
