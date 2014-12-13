package org.kotemaru.android.util.sql;

public class SqlHelper extends Sql {
	public SqlHelper select(Column... cols) {
		mBuff.append("SELECT ");
		if (cols.length == 0) {
			mBuff.append('*');
		} else {
			for (Column col : cols) {
				if (col != cols[0]) mBuff.append(',');
				mBuff.append(col.name());
			}
		}
		return this;
	}
	public SqlHelper from(Table table) {
		mBuff.append(" FROM ").append(table.getName());
		return this;
	}
	public SqlHelper where(Exp exp) {
		mBuff.append(" WHERE ");
		exp.apply(this);
		return this;
	}
	public SqlHelper orderBy(Order... orders) {
		mBuff.append(" ORDER BY ");
		for (Order order : orders) {
			order.apply(this);
		}
		return this;
	}
	public Order asc(Column col) {
		return new Order(col, " ASC");
	}
	public Order desc(Column col) {
		return new Order(col, " DESC");
	}
	public SqlHelper limit(Object val) {
		mBuff.append(" LIMIT ");
		this.sql(val);
		return this;
	}
	public SqlHelper offset(Object val) {
		mBuff.append(" OFFSET ");
		this.sql(val);
		return this;
	}

	public SqlHelper insert(Table table, Column... cols) {
		mBuff.append("INSERT INTO ").append(table.getName());
		if (cols.length > 0) {
			mBuff.append('(');
			for (Column col : cols) {
				if (col != cols[0]) mBuff.append(',');
				mBuff.append(col.name());
			}
			mBuff.append(')');
		}
		return this;
	}
	public SqlHelper values(Bind... binds) {
		mBuff.append(" VALUES (");
		for (Bind bind : binds) {
			if (bind != binds[0]) mBuff.append(',');
			mBuff.append('?');
		}
		mBuff.append(')');
		return this;
	}
	public SqlHelper update(Table table, Setter... setters) {
		mBuff.append("UPDATE ").append(table.getName()).append(" SET ");
			for (Setter setter : setters) {
				if (setter != setters[0]) mBuff.append(',');
				setter.apply(this);
			}
		return this;
	}
	public Setter set(Column col, Bind bind) {
		return new Setter(col, bind);
	}
	public static class Setter {
		private Column mColumn;
		private Bind mBind;

		public Setter(Column column, Bind bind) {
			mColumn = column;
			mBind = bind;
		}
		public void apply(Sql sql) {
			sql.mBuff.append(mColumn.name()).append("=?");
			sql.addBind(mBind);
		}
	}

	public static class Order {
		private Column mColumn;
		private String mOperator;

		public Order(Column column, String operator) {
			mColumn = column;
			mOperator = operator;
		}
		public void apply(Sql sql) {
			sql.mBuff.append(mColumn.getTable().getName()).append('.').append(mColumn.name()).append(mOperator);
		}
	}

	public static class BindingExp implements Exp {
		private Column mColumn;
		private String mOperator;
		private Bind mBind;

		public BindingExp(Column column, String operator, Bind bind) {
			mColumn = column;
			mOperator = operator;
			mBind = bind;
		}
		@Override
		public void apply(Sql sql) {
			if (mBind != null) sql.addBind(mBind);
			sql.mBuff.append(mColumn.getTable().getName()).append('.').append(mColumn.name()).append(mOperator);
		}
	}

	public static class StringExp implements Exp {
		private String mOperator;

		public StringExp(String operator) {
			mOperator = operator;
		}
		@Override
		public void apply(Sql sql) {
			sql.mBuff.append(mOperator);
		}
	}

	public interface Exp {
		public void apply(Sql sql);
	}

	// public static class SqlExpHelper {
	public static Exp like(final Column col, final Bind bind) {
		return new BindingExp(col, "LIKE ? ", bind);
	}
	public static Exp eq(final Column col, final Bind bind) {
		return new BindingExp(col, "=? ", bind);
	}
	public static Exp neq(final Column col, final Bind bind) {
		return new BindingExp(col, "<>? ", bind);
	}
	public static Exp isNull(final Column col) {
		return new BindingExp(col, " IS NULL ", null);
	}
	public static Exp isNotNull(final Column col) {
		return new BindingExp(col, " IS NOT NULL ", null);
	}
	public static Exp and(final Exp... exps) {
		return new Exp() {
			public void apply(Sql sql) {
				for (Exp exp : exps) {
					if (exp != exps[0]) sql.mBuff.append(" AND ");
					exp.apply(sql);
				}
			}
		};
	}
	public static Exp or(final Exp... exps) {
		return new Exp() {
			public void apply(Sql sql) {
				sql.mBuff.append('(');
				for (Exp exp : exps) {
					if (exp != exps[0]) sql.mBuff.append(" OR ");
					exp.apply(sql);
				}
				sql.mBuff.append(')');
			}
		};
	}
	// }

}
