package org.kotemaru.sqlhelper.generator;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.kotemaru.sqlhelper.ColumnMeta;
import org.kotemaru.sqlhelper.ColumnMetaBase;

public class MakeMetaData {
	private static final String URL = "jdbc:mysql://localhost:63306/gwebgldev?user=gwebgldev&password=gwebgldev";
	private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";


	public static void main(String[] args) throws Exception {
        Class.forName(JDBC_DRIVER).newInstance();

        String dir = args[0];
        HashMap<String,List<ColumnMeta>> tables = new HashMap<String,List<ColumnMeta>>();

		Connection conn = DriverManager.getConnection(URL);
		DatabaseMetaData dmd = conn.getMetaData();
		ResultSet rs = dmd.getColumns(null, "gwebgldev", null,null);
		try {
			while (rs.next()) {
				String tabelName = rs.getString("TABLE_NAME");
				List<ColumnMeta> columns = tables.get(tabelName);
				if (columns == null) {
					columns = new ArrayList<ColumnMeta>();
					tables.put(tabelName, columns);
				}
				columns.add(toColmunMeta(rs));
			}
		} finally {
			rs.close();
		}

		for (String tname : tables.keySet()) {
			writeTable(dir, tname, tables.get(tname));
		}
	}

	private static ColumnMeta toColmunMeta(ResultSet rs) throws SQLException {
		ColumnMeta meta = new ColumnMetaBase(
			null, 							//	TableMeta tableMeta,
			rs.getString("COLUMN_NAME"),	//	String colmunName,
			rs.getInt("DATA_TYPE"),		//		Integer dataType,
			rs.getInt("COLUMN_SIZE"),	//		Integer COLUMN_SIZE,
			rs.getInt("DECIMAL_DIGITS"),	//		Integer decimalDigits,
			rs.getInt("NULLABLE"),		//		Integer nullable,
			rs.getString("COLUMN_DEF"),		//		String columnDef,
			rs.getInt("CHAR_OCTET_LENGTH"),	//		Integer charOctetLength,
			rs.getString("IS_AUTOINCREMENT")	//		Integer isAutoincrement
		);
		return meta;
	}


	public static void writeTable(String dir, String tableName, List<ColumnMeta> columns) throws IOException {
	    try{
	    	Properties p = new Properties();
	    	p.setProperty("resource.loader", "class");
	    	p.setProperty("class.resource.loader.class",
	    	            "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
	    	p.setProperty("input.encoding", "UTF-8");
	    	Velocity.init(p);

	    	VelocityContext context = new VelocityContext();
	        context.put("package_meta", "org.kotemaru.sqlhelper.sample");
	        context.put("package_sqlhelper", "org.kotemaru.sqlhelper");
	        context.put("tableName", tableName);
	        context.put("columns", columns);

	        context.put("util", new MakeMetaData());

	        String fname = dir+"/org/kotemaru/sqlhelper/sample/"+tableName+".java";
	        Writer writer = new FileWriter(fname);
	        Template template = Velocity.getTemplate("/org/kotemaru/sqlhelper/generator/TableMeta.vm", "utf-8");
	        template.merge(context,writer);
	        //System.out.println(sw.toString());
	        writer.flush();
	        writer.close();

	    //} catch (ResourceNotFoundException e) {
	    //} catch (ParseErrorException e) {
	    //} catch (MethodInvocationException e) {
	    //} catch (Exception e) {
	    } finally {

	    }
	}

	public String quote(String str) {
		if (str == null) return "null";
		return "\""+str+"\"";
	}


}
