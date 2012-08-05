package org.kotemaru.sqlhelper;

public interface TableMeta {
	public String getAsName();
	public void setAsName(String asName);
	public String getParamName();

	public String getTableName();
	public void setTableName(String tableName);
}
