package org.kotemaru.sqlhelper;

public class ColumnMetaDelegate implements ColumnMeta {

	private ColumnMeta origin;

	private String asName;
	private Object value;

	public ColumnMetaDelegate(ColumnMeta org) {
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
	@Override
	public Object getValue() {
		return value;
	}
	@Override
	public void setValue(Object value) {
		this.value = value;
	}

	public String toString() {
		return this.getParamName()+"="+this.getValue();
	}

	//--------------------------------------------------
	//  delegate

	public ColumnMeta as(String name) {
		return origin.as(name);
	}

	public ColumnMeta value(Object val) {
		return origin.value(val);
	}


	public TableMeta getTableMeta() {
		return origin.getTableMeta();
	}

	public void setTableMeta(TableMeta tableMeta) {
		origin.setTableMeta(tableMeta);
	}

	public String getColumnName() {
		return origin.getColumnName();
	}

	public void setColumnName(String colmunName) {
		origin.setColumnName(colmunName);
	}

	public Integer getDataType() {
		return origin.getDataType();
	}

	public void setDataType(Integer dataType) {
		origin.setDataType(dataType);
	}

	public Integer getColumnSize() {
		return origin.getColumnSize();
	}

	public void setColumnSize(Integer columnSize) {
		origin.setColumnSize(columnSize);
	}

	public Integer getDecimalDigits() {
		return origin.getDecimalDigits();
	}

	public void setDecimalDigits(Integer decimalDigits) {
		origin.setDecimalDigits(decimalDigits);
	}

	public Integer getNullable() {
		return origin.getNullable();
	}

	public void setNullable(Integer nullable) {
		origin.setNullable(nullable);
	}

	public String getColumnDef() {
		return origin.getColumnDef();
	}

	public void setColumnDef(String columnDef) {
		origin.setColumnDef(columnDef);
	}

	public Integer getCharOctetLength() {
		return origin.getCharOctetLength();
	}

	public void setCharOctetLength(Integer charOctetLength) {
		origin.setCharOctetLength(charOctetLength);
	}

	public String getIsAutoincrement() {
		return origin.getIsAutoincrement();
	}

	public void setIsAutoincrement(String isAutoincrement) {
		origin.setIsAutoincrement(isAutoincrement);
	}

	@Override
	public String getParamName() {
		return origin.getParamName();
	}

}
