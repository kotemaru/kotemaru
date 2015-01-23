package org.kotemaru.tool.ledger;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Ledger {
	String sheetName;
	int sheetNo;
	boolean isReceiveOnly;
	List<JournalRow> journalList = new ArrayList<JournalRow>();
	
	public Ledger(String sheetName, int sheetNo, boolean isReceiveOnly) {
		this.sheetName = sheetName;
		this.sheetNo = sheetNo;
		this.isReceiveOnly = isReceiveOnly;
	}

	public void add(Date date, String contra, String summary, double inValue, double outValue) {
		JournalRow journal;
		if (isReceiveOnly) {
			journal = new JournalRow( date, contra, summary, outValue, 0.0);
		} else {
			journal = new JournalRow( date, contra, summary, inValue, outValue);
		}
		journalList.add(journal);
	}
	
	
}
