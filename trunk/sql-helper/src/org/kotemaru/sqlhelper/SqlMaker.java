package org.kotemaru.sqlhelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SqlMaker {
	private static final String PATT = "[?][{][a-zA-Z0-9_:]+[}]";

	private StringBuffer sbuf = new StringBuffer();
	private List<ColumnMeta> bindingList = new ArrayList<ColumnMeta>(16);

	private String toParam(ColumnMeta col) {
		bindingList.add(col);
		int no = bindingList.size();
		String param = "?{"+no+":"+col.getParamName()+"}";
		return param;
	}


	public String toString() {
		return sbuf.toString();
	}
	public PreparedStatement getPreparedStatement(Connection db) throws SQLException {
		String sql = sbuf.toString();
		PreparedStatement stmt = db.prepareStatement(correctSql(sql));
		for (int i=0; i<bindingList.size(); i++) {
			ColumnMeta col = bindingList.get(i);
			stmt.setObject(i+1, col.getValue(), col.getDataType());
		}
		System.out.println(sql+"\n"+bindingList);
		return stmt;
	}
	private String correctSql(String sql) {
		Pattern patt = Pattern.compile(PATT);
		Matcher m = patt.matcher(sql);

		int idx = 1;
		while (m.find()) {
			String no_name = sql.substring(m.start()+2, m.end()-1);
			String[] no_name_ary = no_name.split(":");
			Integer no = Integer.valueOf(no_name_ary[0]);
			if (idx != no) {
				throw new RuntimeException("BUG: Binding counter unmatch. "+idx+"!="+no_name);
			}
			idx++;
		}
		return sql.replaceAll(PATT, "?");
	}

	public SqlMaker _append(String str) {
		sbuf.append(str);
		return this;
	}
	//---------------------------------------------------------
	public SqlMaker select() {
		return _append("select * ");
	}

	public SqlMaker select(ColumnMeta... cols) {
		return select(Arrays.asList(cols));
	}
	public SqlMaker select(List<ColumnMeta> cols) {
		String buff = "select ";
		for (ColumnMeta col : cols) {
			String tableAsName = col.getTableMeta().getAsName();
			String columnAsName = col.getAsName();

			if (tableAsName != null) buff += tableAsName+".";
			buff += col.getColumnName();
			if (columnAsName != null) buff += " as "+columnAsName;
			buff += ", ";
		}
		buff = buff.replaceFirst(",[ ]$", " ");
		return _append(buff);
	}

	public SqlMaker from(TableMeta... metas) {
		String buff = "from ";
		for (TableMeta tm : metas) {
			buff += tm.getTableName();
			if (tm.getAsName() != null) {
				buff += " as "+tm.getAsName();
			}
			buff += ", ";
		}
		buff = buff.replaceFirst(",[ ]$", " ");
		return _append(buff);
	}

	public SqlMaker where() {
		return _append("where ");
	}

	public SqlMaker eq(ColumnMeta col) {
		String buff = col.getParamName()+"=";
		if (col.getValue() != null) {
			buff += toParam(col)+" ";
		} else {
			buff += "is null ";
		}
		return _append(buff);
	}

	public SqlMaker and() {
		return _append("and ");
	}
	public SqlMaker or() {
		return _append("or ");
	}

	//-------------------------
	public SqlMaker insert(TableMeta tableMeta) {
		return _append("insert into "+tableMeta.getTableName()+" ");
	}
	public SqlMaker insert(TableMeta tableMeta, ColumnMeta... cols) {
		return insert(tableMeta, Arrays.asList(cols));
	}
	public SqlMaker insert(TableMeta tableMeta, List<ColumnMeta> cols) {
		String buff = "insert into "+tableMeta.getTableName()+" (";
		for (ColumnMeta col : cols) {
			buff += col.getColumnName()+", ";
		}
		buff = buff.replaceFirst(",[ ]$", ") ");
		return _append(buff);
	}
	public SqlMaker values(ColumnMeta... cols) {
		return set(Arrays.asList(cols));
	}
	public SqlMaker values(List<ColumnMeta> cols) {
		String buff = "values (";
		for (ColumnMeta col : cols) {
			buff += toParam(col)+", ";
		}
		buff = buff.replaceFirst(",[ ]$", ") ");
		return _append(buff);
	}


	//-------------------------
	public SqlMaker update(TableMeta tableMeta) {
		return _append("update "+tableMeta.getTableName()+" ");
	}

	public SqlMaker set(ColumnMeta... cols) {
		return set(Arrays.asList(cols));
	}
	public SqlMaker set(List<ColumnMeta> cols) {
		String buff = "set ";
		for (ColumnMeta col : cols) {
			buff += col.getParamName()+"=";
			buff += toParam(col)+", ";
		}
		buff = buff.replaceFirst(",[ ]$", " ");
		return _append(buff);
	}



}
