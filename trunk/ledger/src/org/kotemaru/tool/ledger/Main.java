package org.kotemaru.tool.ledger;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public class Main {

	public static void main(String[] args) throws Exception {
		Main main = new Main();
		main.init();
		main.start(args[0], args[1], args[2]);
	}
	public static final String 売上 = "売上";
	public static final String 売掛 = "売掛";
	public static final String 口座 = "口座";
	public static final String 事業主借 = "事業主借";
	public static final String 事業主貸 = "事業主貸";
	public static final String 経費 = "経費";
	
	
	enum InSheet {
		売上(0),
		経費(1),
		その他(2);
		int no;
		InSheet(int no) {
			this.no = no;
		}
	};
	Map<String,Ledger> ledgerMap = new HashMap<String,Ledger>();

	public void init() throws Exception {
		ledgerMap.put(売上, new Ledger(売上,0,true));
		ledgerMap.put(売掛, new Ledger(売掛,1,false));
		ledgerMap.put(口座, new Ledger(口座,2,false));
		ledgerMap.put(事業主借, new Ledger(事業主借,3,true));
		ledgerMap.put(事業主貸, new Ledger(事業主貸,4,true));
		//ledgerMap.put("経費", new Ledger("",5,false));
	}
	private Ledger getLedger(String name) {
		Ledger ledger = ledgerMap.get(name);
		if (ledger == null) {
			throw new Error("Unknown ledger "+name+" in "+ledgerMap.keySet());
		}
		return ledger;
	}
	
	public void start(String inFile, String outBase, String outFile) throws Exception {
		HSSFWorkbook inBook = loadBook(inFile);
		HSSFWorkbook outBook = loadBook(outBase);

		List<SaleRow> salesList = getSaleList(inBook);
		makeSalesJournal(salesList);
		List<ExpenseRow> expenseList = getExpenseList(inBook);
		makeExpenseJournal(outBook, expenseList);
		List<GenericRow> otherList = getOtherList(inBook);
		makeOtherJournal(otherList);

		for (Ledger ledger : ledgerMap.values()) {
			makeSheet(outBook, ledger);
		}
		saveBook(outFile, outBook);
	}

	private void makeSalesJournal(List<SaleRow> list) {
		for (SaleRow row : list) {
			if (row.reqDate != null) {
				getLedger(売掛).add(row.reqDate, "売上", row.summary, row.value, 0.0);
				getLedger(売上).add(row.reqDate, "売掛金", row.summary, 0.0, row.value);
			}
			if (row.resDate != null) {
				getLedger(口座).add(row.resDate, "売掛金", row.summary, row.value, 0.0);
				getLedger(売掛).add(row.resDate, "普通預金", row.summary, 0.0 , row.value);
			}
		}
	}
	private void makeExpenseJournal(HSSFWorkbook book, List<ExpenseRow> list) {
		int sheetNo = 5;
		for (ExpenseRow row : list) {
			if (row.date == null) continue;
			Ledger ledger = ledgerMap.get(row.kind);
			if (ledger == null) {
				sheetNo++;
				book.cloneSheet(5);
				book.setSheetName(sheetNo, row.kind);
				Cell cell = book.getSheetAt(sheetNo).getRow(0).getCell(0);
				cell.setCellValue(cell.getStringCellValue()+row.kind);
				ledger =  new Ledger(row.kind,sheetNo,false);
				ledgerMap.put(row.kind, ledger);
			}
			double value = row.value * (1.0F - row.discountRate);
			ledger.add(row.date, "事業主借", row.summary, value, 0.0);
			getLedger(事業主借).add(row.date, row.kind, row.summary, 0.0, value);
		}
	}
	private void makeOtherJournal(List<GenericRow> list) {
		for (GenericRow row : list) {
			if (row.date == null) continue;
			getLedger(row.debit).add(row.date, row.credit, row.summary, row.value, 0.0);
			getLedger(row.credit).add(row.date, row.debit, row.summary, 0.0, row.value);
		}
	}

	private void makeSheet(HSSFWorkbook book, Ledger ledger) {
		Sheet sheet = book.getSheetAt(ledger.sheetNo);
		List<JournalRow> list = ledger.journalList;
		Collections.sort(list, new Comparator<JournalRow>() {
			@Override
			public int compare(JournalRow o1, JournalRow o2) {
				return o1.date.compareTo(o2.date);
			}
		});

		int rowNo = 3;
		for (JournalRow journal : list) {
			Row row = sheet.createRow(++rowNo);
			row.createCell(0).setCellValue(journal.date);
			row.createCell(1).setCellValue(journal.contra);
			row.createCell(2).setCellValue(journal.summary);
			if (journal.inValue != 0.0) {
				row.createCell(3).setCellValue(journal.inValue);
			}
			if (journal.outValue != 0.0) {
				row.createCell(4).setCellValue(journal.outValue);
			}
			row.createCell(5).setCellFormula(Fn(rowNo - 1) + "+" + Dn(rowNo) + "-" + En(rowNo));
		}
	}

	private HSSFWorkbook loadBook(String fileName) throws IOException {
		FileInputStream in = new FileInputStream(fileName);
		try {
			HSSFWorkbook book = new HSSFWorkbook(in);
			return book;
		} finally {
			if (in != null) in.close();
		}
	}
	private void saveBook(String fileName, HSSFWorkbook book) throws IOException {
		FileOutputStream out = new FileOutputStream(fileName);
		try {
			book.write(out);
		} finally {
			if (out != null) out.close();
		}
	}

	private List<SaleRow> getSaleList(HSSFWorkbook book) {
		Sheet sheet = book.getSheetAt(InSheet.売上.no);
		List<SaleRow> salesList = new ArrayList<SaleRow>(sheet.getLastRowNum());
		for (int i = 1; i < sheet.getLastRowNum(); i++) {
			Row row = sheet.getRow(i);
			salesList.add(new SaleRow().setValues(row));
		}
		return salesList;
	}
	private List<ExpenseRow> getExpenseList(HSSFWorkbook book) {
		Sheet sheet = book.getSheetAt(InSheet.経費.no);
		List<ExpenseRow> salesList = new ArrayList<ExpenseRow>(sheet.getLastRowNum());
		for (int i = 1; i < sheet.getLastRowNum(); i++) {
			Row row = sheet.getRow(i);
			salesList.add(new ExpenseRow().setValues(row));
		}
		return salesList;
	}
	private List<GenericRow> getOtherList(HSSFWorkbook book) {
		Sheet sheet = book.getSheetAt(InSheet.その他.no);
		List<GenericRow> list = new ArrayList<GenericRow>(sheet.getLastRowNum());
		for (int i = 1; i < sheet.getLastRowNum(); i++) {
			Row row = sheet.getRow(i);
			list.add(new GenericRow().setValues(row));
		}
		return list;
	}

	// @formatter:off
	//private String An(int n) {return "A"+(n+1);}
	//private String Bn(int n) {return "B"+(n+1);}
	//private String Cn(int n) {return "C"+(n+1);}
	private String Dn(int n) {return "D"+(n+1);}
	private String En(int n) {return "E"+(n+1);}
	private String Fn(int n) {return "F"+(n+1);}
	// @formatter:on

}
