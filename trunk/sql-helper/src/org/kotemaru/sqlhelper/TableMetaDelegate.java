package org.kotemaru.sqlhelper;

public class TableMetaDelegate implements TableMeta {

	private TableMeta origin;
	private String asName;

	public TableMetaDelegate(TableMeta org) {
		this.origin = org;
	}

	@Override
	public String getAsName() {
		return asName;
	}
	@Override
	public void setAsName(String asName) {
		this.asName = asName;
	}

	//--------------------------------------------------
	//  delegate

	public String getTableName() {
		return origin.getTableName();
	}

	public void setTableName(String tableName) {
		origin.setTableName(tableName);
	}


	@Override
	public String getParamName() {
		return origin.getParamName();
	}

}
