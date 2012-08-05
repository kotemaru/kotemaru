package org.kotemaru.sqlhelper;

public interface ColumnMeta {

	public ColumnMeta as(String name);
	public ColumnMeta value(Object val);
	public String getAsName();
	public void setAsName(String asName);
	public String getParamName();

	public Object getValue();
	public void setValue(Object value);

	public TableMeta getTableMeta();
	public void setTableMeta(TableMeta tableMeta);
	public String getColumnName();
	public void setColumnName(String colmunName);
	public Integer getDataType();
	public void setDataType(Integer dataType);
	public Integer getDecimalDigits();
	public void setDecimalDigits(Integer decimalDigits);
	public Integer getNullable();
	public void setNullable(Integer nullable);
	public String getColumnDef();
	public void setColumnDef(String columnDef);
	public Integer getCharOctetLength();
	public void setCharOctetLength(Integer charOctetLength);
	public String getIsAutoincrement();
	public void setIsAutoincrement(String isAutoincrement);
	public Integer getColumnSize();
	public void setColumnSize(Integer columnSize);
}
