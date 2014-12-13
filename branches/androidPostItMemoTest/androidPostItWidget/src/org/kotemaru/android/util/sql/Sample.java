package org.kotemaru.android.util.sql;


public class Sample {

	static class SampleTable extends Table {
		public final Column _ID = type.pkey("autoincrement");
		public final Column FIRST_NAME = type.text();
		public final Column SECOND_NAME = type.text();
	}
	static SampleTable sampleTable = new SampleTable();

	public static void main(String[] args) {
		SampleTable table = new SampleTable();
		System.out.println(table.getName() + ":" + table.getColumns().length);
		MySql mySql = new MySql();
		mySql.id.setLong(100);
		System.out.println(mySql);
		//mySql.execute(db);
	}

	private static class MySql extends SqlHelper {
		Bind id = new Bind();
		public MySql(){
			Exp cond = or(eq(sampleTable._ID, id), isNull(sampleTable.FIRST_NAME));
			select().from(sampleTable).where(cond);
		}
	}

}
