package org.kotemaru.tool.ledger;

import java.util.Date;

import org.apache.poi.ss.usermodel.Row;

public class SaleRow {
	Date reqDate;
	Date resDate;
	String summary;
	double value;

	public SaleRow setValues(Row row) {
		reqDate = row.getCell(0).getDateCellValue();
		resDate = row.getCell(1).getDateCellValue();
		summary = row.getCell(2).getStringCellValue();
		value = row.getCell(3).getNumericCellValue();
		return this;
	}
}
