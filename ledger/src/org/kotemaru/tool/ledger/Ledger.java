package org.kotemaru.tool.ledger;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Ledger {
	String sheetName;
	int sheetNo;
	boolean isReceiveOnly;
	List<JournalRow> journalList = new ArrayList<JournalRow>();
	double 月小計[] = new double[12];
	double 合計 = 0.0;
	boolean isExpense = false;
	
	public Ledger(String sheetName, int sheetNo, boolean isReceiveOnly) {
		this.sheetName = sheetName;
		this.sheetNo = sheetNo;
		this.isReceiveOnly = isReceiveOnly;
	}

	public void add(Date date, String contra, String summary, double inValue, double outValue) {
		JournalRow journal;
		if (isReceiveOnly) {
			inValue = outValue;
			outValue = 0.0;
		}
		journal = new JournalRow( date, contra, summary, inValue, outValue);
		journalList.add(journal);
		
		@SuppressWarnings("deprecation")
		int month = date.getMonth();
		月小計[month] += (inValue-outValue);
		合計 += (inValue-outValue);
	}
	
	
}
