package org.kotemaru.tool.ledger;

import java.util.Date;

public class JournalRow {
	Date date;
	String contra;
	String summary;
	double inValue;
	double outValue;

	public JournalRow(Date date, String contra, String summary, double inValue, double outValue) {
		this.date = date;
		this.contra = contra;
		this.summary = summary;
		this.inValue = inValue;
		this.outValue = outValue;
	}
}
