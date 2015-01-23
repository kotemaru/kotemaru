package org.kotemaru.tool.ledger;

import java.util.Date;

import org.apache.poi.ss.usermodel.Row;

public class ExpenseRow {
	Date date;
	String kind;
	String summary;
	double value;
	float discountRate;
	
	public ExpenseRow(Date date, String kind, String summary, double value, float discountRate) {
		this.date = date;
		this.kind = kind;
		this.summary = summary;
		this.value = value;
		this.discountRate = discountRate;
	}
	
	public ExpenseRow() {
	}

	public ExpenseRow setValues(Row row) {
		date = row.getCell(0).getDateCellValue();
		kind = row.getCell(1).getStringCellValue();
		summary = row.getCell(2).getStringCellValue();
		value = row.getCell(3).getNumericCellValue();
		discountRate = (float) row.getCell(4).getNumericCellValue();
		return this;
	}

}
