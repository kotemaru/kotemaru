package org.kotemaru.sqlhelper;

public class TableMetaBase implements TableMeta , Cloneable {

	private String tableName;
	private String asName;


	@Override
	public String getAsName() {
		return asName;
	}


	@Override
	public void setAsName(String asName) {
		this.asName = asName;
	}


	@Override
	public String getTableName() {
		return tableName;
	}

	@Override
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	@Override
	public String getParamName() {
		if (getAsName() != null) return getAsName();
		return getTableName();
	}

}
