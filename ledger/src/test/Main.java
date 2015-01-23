package test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Date;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public class Main {

	public static void main(String[] args) throws Exception {
		test2(args);
	}
	public static void test1(String[] args) throws Exception {
		FileInputStream in = new FileInputStream(args[0]);
		HSSFWorkbook book = new HSSFWorkbook(in);
		in.close();

		Sheet sheet = book.getSheetAt(0);
		sheet.setForceFormulaRecalculation(true);

		getCell(sheet, "E3").setCellValue(new Date());
		getCell(sheet, "B6").setCellValue("株式会社 ○☓産業 様");
		getCell(sheet, "E5").setCellValue("東京都千代田区永田町１丁目７−１");
		getCell(sheet, "E6").setCellValue("株式会社　△△製作所");
		getCell(sheet, "E7").setCellValue("TEL: 03-1234-5678");
		getCell(sheet, "E8").setCellValue("FAX: 03-1234-5679");
		getCell(sheet, "B14").setCellValue("商品A");
		getCell(sheet, "E14").setCellValue(3);
		getCell(sheet, "F14").setCellValue(12345);
		getCell(sheet, "B15").setCellValue("商品B");
		getCell(sheet, "E15").setCellValue(2);
		getCell(sheet, "F15").setCellValue(7000);

		FileOutputStream out = new FileOutputStream(args[1]);
		book.write(out);
		out.close();
	}

	public static void test2(String[] args) throws Exception {
		FileInputStream in = new FileInputStream(args[0]);
		HSSFWorkbook book = new HSSFWorkbook(in);
		in.close();
		FileInputStream in2 = new FileInputStream(args[1]);
		HSSFWorkbook outbook = new HSSFWorkbook(in2);
		in2.close();

		Sheet sheet = book.getSheetAt(0);
		for (int i = 1; i < sheet.getLastRowNum(); i++) {
			Row row = sheet.getRow(i);
			for (int j = 0; j < row.getLastCellNum(); j++) {
				Date reqDate = row.getCell(0).getDateCellValue();
				Date resDate = row.getCell(1).getDateCellValue();
				String summary = row.getCell(2).getStringCellValue();
				double value = row.getCell(3).getNumericCellValue();
				Sheet sheetUriage = outbook.getSheetAt(0);
				Row outRow = sheetUriage.createRow(i);
				if (reqDate != null) {
					outRow.createCell(0).setCellValue(reqDate);
					outRow.createCell(1).setCellValue("売掛");
					outRow.createCell(2).setCellValue(summary);
					outRow.createCell(3).setCellValue(value);
					//outRow.createCell(4).setCellValue(0);
					int rowNo = i+1;
					if (i == 1) {
						outRow.createCell(5).setCellFormula("D"+rowNo+"-E"+rowNo);
					} else {
						outRow.createCell(5).setCellFormula("F"+i+"+D"+rowNo+"-E"+rowNo);
					}
				}
			}
		}
		
		FileOutputStream out = new FileOutputStream(args[2]);
		outbook.write(out);
		out.close();
	}
	private static void putCellValue(Sheet sheet, String cellId, Object value) {
		Cell cell = getCell(sheet, cellId);
		if (value instanceof String) {
			cell.setCellValue((String) value);
		} else if (value instanceof Integer) {
			cell.setCellValue((Integer) value);
		} else if (value instanceof Boolean) {
			cell.setCellValue((Boolean) value);
		} else if (value instanceof Date) {
			cell.setCellValue((Date) value);
		} else {
			throw new RuntimeException("Unknown data type " + value.getClass());
		}
	}
	private static Cell getCell(Sheet sheet, String cellId) {
		int rowNo = Integer.parseInt(cellId.substring(1)) - 1;
		int cellNo = cellId.charAt(0) - 'A';
		Row row = sheet.getRow(rowNo);
		return row.getCell(cellNo);
	}

}
