package com.example.myapp.main.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;

import com.lowagie.text.Document;
import com.lowagie.text.html.simpleparser.HTMLWorker;
import com.lowagie.text.pdf.PdfWriter;

@Stateless
public class Exporter {

	@Inject
	Logger LOG;

	/**
	 * Simple export to a standard CSV file ("." as decimal separator, "," as
	 * column delimiter, '"' as string delimiter)
	 * 
	 * @param headers
	 * @param rows
	 * @return
	 * @throws IOException
	 */
	public File exportCSV(String[] headers, List<Object[]> rows) throws IOException {
		File f = File.createTempFile("export", ".csv");
		FileWriter fw = new FileWriter(f);

		final String FIELD_DELIMITER = ", ";
		final String ROW_DELIMITER = "\r\n";
		final String STRING_DELIMITER = "\"";

		String comma = "";
		for (String s : headers) {
			fw.write(comma + "\"" + s + "\"");
			comma = ", ";
		}

		for (Object[] item : rows) {
			comma = "";
			for (Object value : item) {

				fw.write(comma + formatCSVField(value, STRING_DELIMITER));
				comma = FIELD_DELIMITER;
			}
			fw.write(ROW_DELIMITER);
			fw.flush();
		}

		fw.close();
		return f;
	}

	private NumberFormat formatter = NumberFormat.getInstance(Locale.US);
	private DateFormat dateFormatter = DateFormat.getInstance();

	private String formatCSVField(Object value, String stringDelimiter) {

		if (value == null)
			return "";

		if (value instanceof Number) {
			return formatter.format(value);
		} else if (value instanceof Date) {
			return dateFormatter.format(value);
		} else {
			String sanitizedString = value.toString().replaceAll("\r", "").replaceAll("\n", "").replaceAll("\"", "'");
			return stringDelimiter + sanitizedString + stringDelimiter;
		}
	}

	/**
	 * Create a (currently very simple) XLSX workbook that contains given data.
	 * 
	 * @param headers
	 * @param rows
	 * @return
	 * @throws IOException
	 * @throws InvalidFormatException
	 */
	public File exportXLSX(String[] headers, List<Object[]> rows) throws IOException {

		// FIXME handle columns format

		File f = File.createTempFile("export", ".xlsx");

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

		int rownum = 1;
		for (Object[] item : rows) {
			row = sheet.createRow(rownum);
			colnum = 0;
			for (Object value : item) {
				Cell cell = row.createCell(colnum);
				formatXLSXField(value, cell);
				++colnum;
			}
			++rownum;
		}

		FileOutputStream fileOut = new FileOutputStream(f.getAbsolutePath());
		wb.write(fileOut);
		fileOut.close();

		return f;

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

	private void formatXLSXField(Object value, Cell c) {
		if (value == null)
			return;
		if (value instanceof Number) {
			c.setCellValue(((Number) value).doubleValue());
		} else if (value instanceof Date) {
			c.setCellValue((Date) value);
		} else if (value instanceof Calendar) {
			c.setCellValue((Calendar) value);
		} else {
			c.setCellValue(value.toString());
		}
	}

	/**
	 * Generate a PDF from given HTML source code.
	 * 
	 * @param HTMLtext
	 */
	public File HTML2PDFExport(String HTMLtext) {
		try {

			File f = File.createTempFile("export", ".xlsx");

			OutputStream os = new FileOutputStream(f);
			Document doc = new Document();
			PdfWriter.getInstance(doc, os);
			doc.open();
			HTMLWorker hw = new HTMLWorker(doc);
			hw.parse(new StringReader(HTMLtext));
			doc.close();
			os.close();

			return f;

		} catch (Exception e) {
			LOG.error("Exception during PDF conversion", e);
			return null;
		}
	}
}
