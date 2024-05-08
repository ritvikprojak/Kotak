package com.filenet.ce.bulkdownload.util;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.JPanel;
import javax.swing.SwingWorker;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.filenet.api.core.Domain;
import com.filenet.api.exception.EngineRuntimeException;

public class ReadExcel {

	final static Logger logger = Logger.getLogger(ReadExcel.class);

	public void processExcelSheet(Domain domain,
			String cm_TARGETOS, String excelPath, String downloadPath)
			throws IOException {

		if (logger.isInfoEnabled()) {
			logger.info("<< Start  processing excel sheet ::");
		}
		XSSFRow rowSet;
		File inputFile = new File(excelPath);
		FileInputStream fis = new FileInputStream(inputFile);
		FileOutputStream fos = new FileOutputStream(downloadPath + "\\output_"
				+ inputFile.getName());

		XSSFWorkbook workbook = new XSSFWorkbook(fis);
		XSSFSheet spreadsheet = workbook.getSheetAt(0);
		Iterator<Row> rowIterator = spreadsheet.iterator();
		int len = spreadsheet.getLastRowNum();
		if(logger.isInfoEnabled()){
			logger.info("Total Entries in Input file:" + len);
		}
		UploadExcel.setMaximum(len);
//		int per = 0;
		int count = 0;
		boolean IsStatus = false;

		FilenetCEUtils filenetCEUtils = new FilenetCEUtils();
		LinkedList<String> rowHeaderList = new LinkedList<String>();
		LinkedList<String> rowDataList;
		int rowInt = 0;
		// int filesComp = 0;
		try {
			while (rowIterator.hasNext()) {
				count++;
				rowDataList = new LinkedList<String>();
				rowSet = (XSSFRow) rowIterator.next();
				Cell status;
				Cell errMessage;
				Iterator<Cell> cellIterator = rowSet.cellIterator();
				if (rowInt == 0) {
					while (cellIterator.hasNext()) {
						String cellValueString = cellIterator.next().toString();
						if (!cellValueString.equals("")) {
							if (logger.isInfoEnabled()) {
								logger.info("Header String: " + cellValueString);
							}
							if (cellValueString.equals("Status")) {
								IsStatus = true;
							}
							rowHeaderList.add(cellValueString);
						}
					}
					if (!IsStatus) {
						rowSet.createCell(rowSet.getLastCellNum())
								.setCellValue("Status");
						rowSet.createCell(rowSet.getLastCellNum())
								.setCellValue("Error Message");
					}

					rowInt++;
				} else {
					while (cellIterator.hasNext()) {
						String cellValueString = cellIterator.next().toString();
						if (!cellValueString.equals("")) {
							/*
							 * if(cellValueString.contains(".")){ int dotIndex =
							 * cellValueString.indexOf("."); cellValueString =
							 * cellValueString.substring(0, dotIndex); }
							 */
							if (logger.isInfoEnabled()) {
								logger.info("Cell Value String ::"
										+ cellValueString);
							}
							rowDataList.add(cellValueString);
						}
					}
					if (rowDataList.size() == 5) {
						String message = filenetCEUtils.uploadFiles(domain,
								cm_TARGETOS, rowHeaderList, rowDataList,
								downloadPath);
						if (logger.isInfoEnabled()) {
							logger.info("Response " + message);
						}
						if (message != null) {
							status = rowSet.getCell(rowSet.getLastCellNum(),
									Row.CREATE_NULL_AS_BLANK);
							errMessage = rowSet.getCell(
									rowSet.getLastCellNum(),
									Row.CREATE_NULL_AS_BLANK);
							status.setCellValue(message.split(",")[0]);
							errMessage.setCellValue(message.split(",")[1]);
						} else if (message == null) {
							System.out.println("!!!!!!!!!!!!!!!!!");
						}
					} else if (rowDataList.size() == 0) {
					} else {
						status = rowSet.getCell(rowHeaderList.size(),
								Row.CREATE_NULL_AS_BLANK);
						errMessage = rowSet.getCell(rowHeaderList.size() + 1,
								Row.CREATE_NULL_AS_BLANK);
						status.setCellValue("Failed");
						errMessage.setCellValue("Insufficient Data");
					}

				}
				
//				per = (int) Math.ceil((count / len) * 100);
				UploadExcel.setProgressBar(count);

			}
			if (logger.isInfoEnabled()) {
				logger.info(">> Excel sheet Processed successfully ::");
				logger.info("output file name ::" + downloadPath + "\\output_"
						+ inputFile.getName());
			}
			spreadsheet.getWorkbook().write(fos);
			fos.close();
			fis.close();

		}
		catch(Exception ex){
			logger.error(ex.getMessage());
			ex.printStackTrace();
			spreadsheet.getWorkbook().write(fos);
			fos.close();
			fis.close();
		}
	}
}
