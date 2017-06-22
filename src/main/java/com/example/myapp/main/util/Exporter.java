package com.example.myapp.main.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import javax.ejb.Stateless;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@Stateless
public class Exporter {

	/**
	 * Simple and buggy export to a standard CSV file ("." as decimal separator,
	 * "," as column delimiter, '"' as string delimiter)
	 * 
	 * @param headers
	 * @param rows
	 * @return
	 * @throws IOException
	 */
	public File exportCSV(String[] headers, List<Object[]> rows) throws IOException {
		File f = File.createTempFile("export", ".csv");
		FileWriter fw = new FileWriter(f);

		String comma = "";
		for (String s : headers) {
			fw.write(comma + "\"" + s + "\"");
			comma = ", ";
		}

		for (Object[] item : rows) {
			comma = "";
			for (Object value : item) {

				if (value instanceof Number) {
					fw.write(comma + Double.toString(((Number) value).doubleValue()).replaceAll(",", "."));
				} else {
					fw.write(comma + "\"" + value.toString() + "\"");
				}
				comma = ", ";
			}
			fw.flush();
		}

		fw.close();
		return f;
	}

	/**
	 * Create a (currently very simple) XLSX workbook that contains given data.
	 * 
	 * @param headers
	 * @param rows
	 * @return
	 */
	public XSSFWorkbook exportXLSX(String[] headers, List<Object[]> rows) {

		// FIXME handle columns format

		// FIXME better to save to a File?

		XSSFWorkbook wb = new XSSFWorkbook();
		Sheet sheet = wb.createSheet();
		Row row = sheet.createRow(0);

		CellStyle headerStyle = createCellStyleWhiteBold(wb);

		int colnum = 0;
		for (String s : headers) {
			Cell c = row.createCell(colnum++);
			c.setCellStyle(headerStyle);
			c.setCellValue(s);
		}

		for (Object[] item : rows) {
			colnum = 0;
			for (Object value : item) {
				Cell c = row.createCell(colnum++);
				// FIXME many more types
				if (value instanceof Number) {
					c.setCellValue(((Number) value).doubleValue());
				} else {
					c.setCellValue(value.toString());
				}
			}
		}

		return wb;

	}

	private CellStyle createCellStyleWhiteBold(Workbook wb) {
		CellStyle cs = wb.createCellStyle();
		cs.setFillForegroundColor(HSSFColor.WHITE.index);
		cs.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		Font fontBlueBold = wb.createFont();
		fontBlueBold.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		fontBlueBold.setColor(HSSFColor.DARK_BLUE.index);
		cs.setFont(fontBlueBold);
		return cs;
	}
}
