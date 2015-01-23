package org.kotemaru.tool.ledger;

import java.util.Date;

import org.apache.poi.ss.usermodel.Row;

public class GenericRow {
	Date date;
	String debit;
	String credit;
	String summary;
	double value;
	
	
	public GenericRow(Date date, String debit, String credit, String summary, double value) {
		this.date = date;
		this.debit = debit;
		this.credit = credit;
		this.summary = summary;
		this.value = value;
	}

		public GenericRow() {
	}

	public GenericRow setValues(Row row) {
		this.date = row.getCell(0).getDateCellValue();
		this.debit = row.getCell(1).getStringCellValue();
		this.credit = row.getCell(2).getStringCellValue();
		this.summary = row.getCell(3).getStringCellValue();
		this.value = row.getCell(4).getNumericCellValue();;
		return this;
	}

}
