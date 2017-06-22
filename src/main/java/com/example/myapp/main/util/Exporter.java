package com.example.myapp.main.util;

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
	 * Create a (currently very simple) XLSX workbook that contains given data.
	 * 
	 * @param headers
	 * @param rows
	 * @return
	 */
	public XSSFWorkbook exportXLSX(String[] headers, List<Object[]> rows) {

		// FIXME handle columns format

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
