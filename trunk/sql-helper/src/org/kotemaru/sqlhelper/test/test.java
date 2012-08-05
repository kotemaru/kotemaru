package org.kotemaru.sqlhelper.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.*;

import org.kotemaru.sqlhelper.*;
import org.kotemaru.sqlhelper.sample.pro_b1_category;

public class test {
	private static final String URL = "jdbc:mysql://localhost:63306/gwebgldev?user=gwebgldev&password=gwebgldev";
	private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";

	public static void main(String[] args) throws Exception {
        Class.forName(JDBC_DRIVER).newInstance();

		pro_b1_category b1 = pro_b1_category.as("a");
		List<ColumnMeta> params = b1.getAllColumns();
		params.get(0).setAsName("id");

		SqlMaker sql = new SqlMaker();
		sql.select(params).from(b1).where().eq(b1.B1ID.value(5)).or().eq(b1.B1ID.value(7));

		Connection db = DriverManager.getConnection(URL);
		PreparedStatement stmt = sql.getPreparedStatement(db);
		ResultSet rs = stmt.executeQuery();
		ResultSetMetaData meta = rs.getMetaData();
		int colCount = meta.getColumnCount();
		while (rs.next()) {
			String s = "";
			for (int i=0; i<colCount; i++) {
				s += meta.getColumnLabel(i+1)+"="+rs.getObject(i+1)+", ";
			}
			System.out.println(s);
		}
		db.close();
	}
}
