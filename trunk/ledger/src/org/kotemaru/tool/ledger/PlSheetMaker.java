package org.kotemaru.tool.ledger;

import java.util.HashSet;

import org.apache.poi.ss.usermodel.Sheet;

public class PlSheetMaker {
	public static final String 売上 = "売上";
	
	Ledgers ledgers;
	
	public PlSheetMaker(Ledgers ledgers) {
		super();
		this.ledgers = ledgers;
	}


	public void make(Sheet sheet) {
		int ROW_OFF = 3;
		int COL_OFF = 2;
		
		Ledger 売上帳 = ledgers.get(売上);
		for (int i=0; i<12; i++) {
			sheet.getRow(i+ROW_OFF).getCell(COL_OFF).setCellValue(売上帳.月小計[i]);
		}
		
		HashSet<String> expenses = new HashSet<String>();
		for (Ledger ledger : ledgers.values()) {
			if (!ledger.isExpense) continue;
			expenses.add(ledger.sheetName);
		}
		
		for (int i=0; i<17; i++) {
			String name = sheet.getRow(i+ROW_OFF).getCell(COL_OFF+2).getStringCellValue();
			Ledger ledger = ledgers.getWithNull(name);
			if (ledger != null) {
				expenses.remove(ledger.sheetName);
				sheet.getRow(i+ROW_OFF).getCell(COL_OFF+3).setCellValue(ledger.合計);
			}
		}
		
		if (!expenses.isEmpty()) {
			throw new Error("expenses not empty:"+expenses);
		}
		
	}
	
}
