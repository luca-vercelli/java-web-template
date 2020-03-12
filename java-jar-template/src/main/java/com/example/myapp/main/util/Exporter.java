package com.example.myapp.main.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.ejb.Stateless;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.html.simpleparser.HTMLWorker;
import com.lowagie.text.pdf.PdfWriter;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This class is meant to export a list of beans into a table of some kind.
 * 
 * @author LV
 *
 */
@Stateless
public class Exporter {

	static final Logger logger = LoggerFactory.getLogger(Exporter.class);

	public static final String MEDIA_TYPE_XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
	public static final String MEDIA_TYPE_XLS = "application/vnd.ms-excel";
	public static final String MEDIA_TYPE_DOCX = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
	public static final String MEDIA_TYPE_DOC = "application/msword";
	public static final String MEDIA_TYPE_PDF = "application/pdf";

	/**
	 * This class represent all parameters that define a CSV file
	 *
	 */
	public static class CsvParameters {

		/**
		 * CSV with default parameters
		 */
		public CsvParameters() {
			fieldDelimiter = ", ";
			rowDelimiter = "\r\n";
			stringDelimiter = "\"";
			stringDelimiterReplacement = "'";
			nullReplacement = "";
			dateFormatter = DateFormat.getInstance();
			numberFormatter = NumberFormat.getInstance(Locale.US);
		}

		/**
		 * CSV with default parameters, but the fieldDelimiter
		 */
		public CsvParameters(String fieldDelimiter) {
			this();
			this.fieldDelimiter = fieldDelimiter;
		}

		/**
		 * CSV with custom parameters. All parameters may be "null", in that case
		 * default value will be used.
		 * 
		 * @param fieldDelimiter
		 * @param rowDelimiter
		 * @param stringDelimiter
		 * @param stringDelimiterReplacement
		 * @param dateFormatter
		 * @param numberFormatter
		 */
		public CsvParameters(String fieldDelimiter, String rowDelimiter, String stringDelimiter,
				String stringDelimiterReplacement, String nullReplacement, DateFormat dateFormatter,
				NumberFormat numberFormatter) {
			this();
			if (fieldDelimiter != null) {
				this.fieldDelimiter = fieldDelimiter;
			}
			if (rowDelimiter != null) {
				this.rowDelimiter = rowDelimiter;
			}
			if (stringDelimiter != null) {
				this.stringDelimiter = stringDelimiter;
			}
			if (stringDelimiterReplacement != null) {
				this.stringDelimiterReplacement = stringDelimiterReplacement;
			}
			if (nullReplacement != null) {
				this.nullReplacement = nullReplacement;
			}
			if (dateFormatter != null) {
				this.dateFormatter = dateFormatter;
			}
			if (numberFormatter != null) {
				this.numberFormatter = numberFormatter;
			}
		}

		private String fieldDelimiter;
		private String rowDelimiter;
		private String stringDelimiter;
		private String stringDelimiterReplacement;
		private String nullReplacement;
		private DateFormat dateFormatter;
		private NumberFormat numberFormatter;

		public String getFieldDelimiter() {
			return fieldDelimiter;
		}

		public void setFieldDelimiter(String fieldDelimiter) {
			this.fieldDelimiter = fieldDelimiter;
		}

		public String getRowDelimiter() {
			return rowDelimiter;
		}

		public void setRowDelimiter(String rowDelimiter) {
			this.rowDelimiter = rowDelimiter;
		}

		public String getStringDelimiter() {
			return stringDelimiter;
		}

		public void setStringDelimiter(String stringDelimiter) {
			this.stringDelimiter = stringDelimiter;
		}

		public String getStringDelimiterReplacement() {
			return stringDelimiterReplacement;
		}

		public void setStringDelimiterReplacement(String stringDelimiterReplacement) {
			this.stringDelimiterReplacement = stringDelimiterReplacement;
		}

		public String getNullReplacement() {
			return nullReplacement;
		}

		public void setNullReplacement(String nullReplacement) {
			this.nullReplacement = nullReplacement;
		}

		public DateFormat getDateFormatter() {
			return dateFormatter;
		}

		public void setDateFormatter(DateFormat dateFormatter) {
			this.dateFormatter = dateFormatter;
		}

		public NumberFormat getNumberFormatter() {
			return numberFormatter;
		}

		public void setNumberFormatter(NumberFormat numberFormatter) {
			this.numberFormatter = numberFormatter;
		}
	}

	/**
	 * Simple export to a standard CSV file ("." as decimal separator, "," as column
	 * delimiter, '"' as string delimiter)
	 * 
	 * @param headers if null, header will be omitted
	 * @param rows
	 * @return
	 * @throws IOException
	 */
	public File exportCSV(String[] headers, List<Object[]> rows) throws IOException {
		return exportCSV(headers, rows, new CsvParameters());
	}

	/**
	 * Export to CSV file with given formatters.
	 * 
	 * Nulls are replaced with defaults.
	 * 
	 * @param headers if null, header will be omitted
	 * 
	 * @param rows
	 * @param params
	 * @return
	 * @throws IOException
	 */
	public File exportCSV(String[] headers, List<Object[]> rows, CsvParameters params) throws IOException {

		if (rows == null) {
			throw new IllegalArgumentException("rows cannot be null");
		}

		File f = File.createTempFile("export", ".csv");
		try (FileWriter fw = new FileWriter(f)) {

			if (headers != null) {
				String line = formatCsvRow(headers, params);
				fw.write(line);
			}

			for (Object[] row : rows) {

				String line = formatCsvRow(row, params);
				fw.write(line);
			}

			fw.flush();
		}
		return f;
	}

	/**
	 * Format a single row.
	 * 
	 * Arguments must not be null.
	 * 
	 * @param row
	 * @param params
	 * @return
	 */
	private String formatCsvRow(Object[] row, CsvParameters params) {
		boolean firstColumn = true;
		StringBuilder sb = new StringBuilder();
		for (Object value : row) {
			if (!firstColumn) {
				sb.append(params.fieldDelimiter);
			}
			sb.append(formatCsvField(value, params));
			firstColumn = false;
		}
		sb.append(params.rowDelimiter);
		return sb.toString();
	}

	/**
	 * Format a single field.
	 * 
	 * Arguments must not be null.
	 * 
	 * @param value
	 * @param params
	 * @return
	 */
	private String formatCsvField(Object value, CsvParameters params) {

		if (value == null) {
			return params.nullReplacement;
		} else if (value instanceof Number) {
			return params.numberFormatter.format(value);
		} else if (value instanceof Date) {
			String dateFmt = params.dateFormatter.format((Date) value);
			return params.stringDelimiter + dateFmt + params.stringDelimiter;
		} else if (value instanceof Calendar) {
			String dateFmt = params.dateFormatter.format(((Calendar) value).getTime());
			return params.stringDelimiter + dateFmt + params.stringDelimiter;
		} else {
			String sanitizedString = value.toString().replace("\r", "").replace("\n", "");
			if (!params.stringDelimiter.isEmpty()) {
				sanitizedString = sanitizedString.replace(params.stringDelimiter, params.stringDelimiterReplacement);
			}
			return params.stringDelimiter + sanitizedString + params.stringDelimiter;
		}
	}

	/**
	 * Create a (currently very simple) XLSX workbook that contains given data.
	 * 
	 * @param headers
	 *            if null, header will be omitted
	 * @param rows
	 * @param headerStyle
	 *            CellStyle to be used for header cells. If null, a default will
	 *            be used.
	 * @param cellStyle
	 *            CellStyle to be used for all other cells. Can be null.
	 * @param sheetName
	 *            can be null
	 * @return
	 * @throws IOException
	 */
	public File exportXLSX(String[] headers, List<Object[]> rows, CellStyle headerStyle, CellStyle cellStyle,
			String sheetName) throws IOException {

		if (rows == null) {
			throw new IllegalArgumentException("rows cannot be null");
		}

		// FIXME handle columns format
		// FIXME auto size columns ???

		File f = File.createTempFile("export", ".xlsx");

		XSSFWorkbook wb = new XSSFWorkbook();
		Sheet sheet = wb.createSheet();
		if (sheetName != null && !sheetName.isEmpty()) {
			wb.setSheetName(0, sheetName);
		}
		int rownum = 0;

		if (headers != null) {
			if (headerStyle == null) {
				headerStyle = createCellStyleWhiteBold(wb);
			}
			createXlsxRow(headers, sheet, rownum, headerStyle);
			++rownum;
		}

		for (Object[] row : rows) {
			createXlsxRow(row, sheet, rownum, cellStyle);
			++rownum;
		}

		// Autosize columns, if there is some column
		if (headers != null || rows.size() > 0) {
			int numOfColumns = (headers != null) ? headers.length : rows.get(0).length;
			for (int colnum = 0; colnum < numOfColumns; ++colnum) {
				sheet.autoSizeColumn(colnum);
			}
		}
		
		FileOutputStream fos = new FileOutputStream(f);
		wb.write(fos);
		fos.close();

		return f;
	}

	/**
	 * Create a (currently very simple) XLSX workbook that contains given data.
	 * 
	 * @param headers
	 *            if null, header will be omitted
	 * @param rows
	 * @return
	 * @throws IOException
	 * @throws InvalidFormatException
	 */
	public File exportXLSX(String[] headers, List<Object[]> rows) throws IOException {
		return exportXLSX(headers, rows, null, null, null);
	}

	private void createXlsxRow(Object[] row, Sheet sheet, int rownum, CellStyle cellStyle) {
		Row sheeetRow = sheet.createRow(rownum);
		int colnum = 0;
		for (Object value : row) {
			createXlsxCell(value, sheeetRow, colnum, cellStyle);
			++colnum;
		}
	}

	private void createXlsxCell(Object value, Row sheeetRow, int colnum, CellStyle cellStyle) {

		Cell cell = sheeetRow.createCell(colnum);
		if (cellStyle != null)
			cell.setCellStyle(cellStyle);

		if (value == null)
			return;
		if (value instanceof Number) {
			cell.setCellValue(((Number) value).doubleValue());
		} else if (value instanceof Date) {
			cell.setCellValue((Date) value);
		} else if (value instanceof Calendar) {
			cell.setCellValue((Calendar) value);
		} else {
			cell.setCellValue(value.toString());
		}
	}

	private CellStyle createCellStyleWhiteBold(Workbook wb) {
		CellStyle cs = wb.createCellStyle();
		cs.setFillForegroundColor(IndexedColors.WHITE.index);
		cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		Font fontBlueBold = wb.createFont();
		fontBlueBold.setBold(true);
		fontBlueBold.setColor(IndexedColors.DARK_BLUE.index);
		cs.setFont(fontBlueBold);
		return cs;
	}

	/**
	 * Return a HTML &lt;TABLE&gt; using given data
	 * 
	 * @param headers
	 *            if null, header will be omitted
	 * @param rows
	 * @return
	 * @throws IOException
	 */
	public String exportHtmlTable(String[] headers, List<Object[]> rows) throws IOException {
		return exportHtmlTable(headers, rows, null, null);
	}

	/**
	 * 
	 * Return a HTML &lt;TABLE&gt; using given data
	 * 
	 * @param headers
	 *            if null, header will be omitted
	 * @param rows
	 * @param dateFormatter
	 * @param numberFormatter
	 * @return
	 * @throws IOException
	 */
	public String exportHtmlTable(String[] headers, List<Object[]> rows, DateFormat dateFormatter,
			NumberFormat numberFormatter) throws IOException {

		if (rows == null) {
			throw new IllegalArgumentException("rows cannot be null");
		}

		if (dateFormatter == null) {
			dateFormatter = DateFormat.getInstance();
		}
		if (numberFormatter == null) {
			numberFormatter = NumberFormat.getInstance(Locale.US);
		}

		StringBuilder sb = new StringBuilder();
		sb.append("<table>");
		if (headers != null) {
			sb.append("<thead><tr>");
			for (String c : headers) {
				sb.append("<td>").append(c).append("</td>");
			}
			sb.append("</tr></thead>");
		}
		sb.append("<tbody>");
		for (Object[] row : rows) {
			sb.append("<tr>");
			for (Object field : row) {
				String fieldAsStr = formatHtmlField(field, dateFormatter, numberFormatter);
				sb.append("<td>").append(fieldAsStr).append("</td>");
			}
			sb.append("</tr>");
		}
		sb.append("</tbody></table>");
		return sb.toString();
	}

	/**
	 * Format a single field.
	 * 
	 * Arguments must not be null.
	 * 
	 * @param value
	 * @param dateFormatter
	 * @param numberFormatter
	 * @return
	 */
	private String formatHtmlField(Object value, DateFormat dateFormatter, NumberFormat numberFormatter) {

		String s;
		if (value == null) {
			s = "";
		} else if (value instanceof Number) {
			s = numberFormatter.format(value);
		} else if (value instanceof Date) {
			s = dateFormatter.format((Date) value);
		} else if (value instanceof Calendar) {
			s = dateFormatter.format(((Calendar) value).getTime());
		} else {
			s = value.toString();
		}
		return StringEscapeUtils.escapeHtml4(s);
	}

	/**
	 * Return a HTML file
	 * 
	 * @param headers
	 *            if null, header will be omitted
	 * @param rows
	 * @return
	 * @throws IOException
	 */
	public String exportHTML(String[] headers, List<Object[]> rows) throws IOException {
		return exportHTML(headers, rows, null, null);
	}

	/**
	 * 
	 * Return a HTML file
	 * 
	 * @param headers
	 *            if null, header will be omitted
	 * @param rows
	 * @param dateFormatter
	 * @param numberFormatter
	 * @return
	 * @throws IOException
	 */
	public String exportHTML(String[] headers, List<Object[]> rows, DateFormat dateFormatter,
			NumberFormat numberFormatter) throws IOException {
		return "<html><body>" + exportHtmlTable(headers, rows, dateFormatter, numberFormatter) + "</body></html>";
	}

	/**
	 * Generate a PDF from given HTML source code.
	 * 
	 * @param htmlText
	 * @throws IOException
	 * @throws DocumentException
	 */
	public File exportPDF(String htmlText) throws IOException, DocumentException {

		File f = File.createTempFile("export", ".pdf");

		OutputStream fos = new FileOutputStream(f);
		Document doc = new Document();
		PdfWriter.getInstance(doc, fos);
		doc.open();
		HTMLWorker hw = new HTMLWorker(doc);
		hw.parse(new StringReader(htmlText));
		doc.close();
		fos.close();

		return f;
	}

	/**
	 * Create a DOCX document with a single table within
	 * 
	 * @param headers
	 * @param rows
	 * @return
	 * @throws IOException
	 */
	public File exportDOCX(String[] headers, List<Object[]> rows) throws IOException {
		return exportDOCX(headers, rows, null, null);
	}

	/**
	 * Create a DOCX document with a single table within
	 * 
	 * @param headers
	 * @param rows
	 * @return
	 * @throws IOException
	 */
	public File exportDOCX(String[] headers, List<Object[]> rows, DateFormat dateFormatter,
			NumberFormat numberFormatter) throws IOException {

		if (rows == null) {
			throw new IllegalArgumentException("rows cannot be null");
		}

		File f = File.createTempFile("export", ".docx");

		if (dateFormatter == null) {
			dateFormatter = DateFormat.getInstance();
		}
		if (numberFormatter == null) {
			numberFormatter = NumberFormat.getInstance(Locale.US);
		}

		XWPFDocument document = new XWPFDocument();
		FileOutputStream fos = new FileOutputStream(f);
		XWPFTable table = document.createTable();

		if (headers != null) {
			// should make it bold
			createDocxRow(headers, table, dateFormatter, numberFormatter);
		}

		for (Object[] row : rows) {
			createDocxRow(row, table, dateFormatter, numberFormatter);
		}

		document.write(fos);
		fos.close();
		document.close();

		return f;
	}

	/**
	 * Create a single row.
	 * 
	 * Arguments must not be null.
	 * 
	 * @param row
	 * @param table
	 * @param dateFormatter
	 * @param numberFormatter
	 */
	private void createDocxRow(Object[] row, XWPFTable table, DateFormat dateFormatter, NumberFormat numberFormatter) {
		XWPFTableRow tableRow = table.createRow();
		int colnum = 0;
		for (Object value : row) {
			tableRow.getCell(colnum).setText(formaDocxField(value, dateFormatter, numberFormatter));
			++colnum;
		}
	}

	/**
	 * Format a single field.
	 * 
	 * Arguments must not be null.
	 * 
	 * @param value
	 * @param dateFormatter
	 * @param numberFormatter
	 * @return
	 */
	private String formaDocxField(Object value, DateFormat dateFormatter, NumberFormat numberFormatter) {

		String s;
		if (value == null) {
			s = "";
		} else if (value instanceof Number) {
			s = numberFormatter.format(value);
		} else if (value instanceof Date) {
			s = dateFormatter.format((Date) value);
		} else if (value instanceof Calendar) {
			s = dateFormatter.format(((Calendar) value).getTime());
		} else {
			s = value.toString();
		}
		return s;
	}

	/**
	 * Convert entity beans given in form of Map's into Array's, using given
	 * attribute names.
	 * 
	 * @param rows
	 * @param headers
	 * @return
	 */
	public List<Object[]> maps2Arrays(List<Map<String, Object>> rowsAsMaps, String[] attributeNames) {
		List<Object[]> list = new ArrayList<Object[]>(rowsAsMaps.size());
		for (Map<String, Object> rowAsMap : rowsAsMaps) {
			Object[] rowAsArray = new Object[attributeNames.length];
			for (int i = 0; i < attributeNames.length; ++i) {
				rowAsArray[i] = multiget(rowAsMap, attributeNames[i]);
			}
			list.add(rowAsArray);
		}
		return list;
	}

	/**
	 * Retrieve a property such as "element.parent.id"
	 * 
	 * @param map
	 * @param attributeNameWithDots
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Object multiget(Map<String, Object> map, String attributeNameWithDots) {
		if (attributeNameWithDots.contains(".")) {
			int index = attributeNameWithDots.indexOf('.');
			String firstPart = attributeNameWithDots.substring(0, index);
			String moreParts = attributeNameWithDots.substring(index + 1);
			Object first = map.get(firstPart);
			if (first instanceof Map) {
				return multiget((Map<String, Object>) first, moreParts);
			} else {
				// Parent property not found
				return null;
			}
		} else {
			return map.get(attributeNameWithDots);
		}
	}

	/**
	 * Convert entity beans given in form of Object's into Array's, using given
	 * attribute names.
	 * 
	 * @param rows
	 * @param headers
	 * @return
	 */
	public List<Object[]> objects2Arrays(List<? extends Object> rowsAsObjects, String[] attributeNames) {
		List<Object[]> list = new ArrayList<Object[]>(rowsAsObjects.size());
		ObjectMapper oMapper = new ObjectMapper();
		for (Object rowAsObject : rowsAsObjects) {
			@SuppressWarnings("unchecked")
			Map<String, Object> rowAsMap = oMapper.convertValue(rowAsObject, Map.class);
			Object[] rowAsArray = new Object[attributeNames.length];
			for (int i = 0; i < attributeNames.length; ++i) {
				rowAsArray[i] = rowAsMap.get(attributeNames[i]);
			}
			list.add(rowAsArray);
		}
		return list;
	}

	/**
	 * Convert entity beans given in form of Arrays's into Map's, using given
	 * attribute names.
	 * 
	 * @param items
	 * @param columns
	 * @return
	 */
	public List<Map<String, Object>> arrays2Maps(List<Object[]> rowsAsArrays, String[] attributeNames) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (Object[] row : rowsAsArrays) {
			Map<String, Object> rowAsMap = new HashMap<String, Object>();
			for (int i = 0; i < attributeNames.length; ++i) {
				rowAsMap.put(attributeNames[i], row[i]);
			}
			list.add(rowAsMap);
		}
		return list;
	}
}
