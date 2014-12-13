package org.kotemaru.android.util.sql;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class Table {
	private Column[] mColumns;
	private String mName;

	public Table() {
		mName = this.getClass().getSimpleName();
	}
	public String toString() {
		return getName();
	}

	public Column[] getColumns() {
		if (mColumns == null) mColumns = getColumns(this);
		return mColumns;
	}

	public static Column[] getColumns(Table table) {
		try {
			List<Column> list = new ArrayList<Column>(20);
			Field[] fields = table.getClass().getFields();
			for (Field field : fields) {
				Object val = field.get(table);
				if (val instanceof Column) {
					list.add((Column) val);
				}
			}
			return list.toArray(new Column[list.size()]);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Not found public field me.", e);
		}
	}

	public String getName() {
		return mName;
	}
	
	public String getCreateTableDDL() {
		StringBuilder sbuf = new StringBuilder();
		sbuf.append("CREATE TABLE ").append(getName()).append('(');
		for (Column column : getColumns()) {
			sbuf.append(column.name()).append(' ').append(column.type()).append(',');
		}
		sbuf.setLength(sbuf.length() - 1);
		sbuf.append(");");
		return sbuf.toString();
	}

	protected Type type = new Type();

	public class Type {
		public Column pkey(String... options) {
			return new Column(Table.this, "INTEGER PRIMARY KEY" + join(options));
		}
		public Column text(String... options) {
			return new Column(Table.this, "TEXT" + join(options));
		}
		public Column real(String... options) {
			return new Column(Table.this, "REAL" + join(options));
		}
		public Column blob(String... options) {
			return new Column(Table.this, "BLOB" + join(options));
		}
		public Column integer(String... options) {
			return new Column(Table.this, "INTEGER" + join(options));
		}
		private StringBuilder join(String[] options) {
			StringBuilder sbuf = new StringBuilder();
			for (String opt : options) {
				sbuf.append(' ').append(opt);
			}
			return sbuf;
		}
	}
}
