package com.dedalow.report;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Date;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import com.google.common.base.Throwables;
import com.google.common.io.Files;

import com.dedalow.utils.Utils;
import com.dedalow.report.Excel;
import com.dedalow.Launcher;
import com.dedalow.SharedDependencies;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;

public class Report {

	private static String root = System.getProperty("user.dir") + SharedDependencies.fileSystem.getSeparator() + "logs";
	private static File rootFile = new File(root);
	private static Launcher launcher = new Launcher();
	private static JsonReport jsonReport = new JsonReport();
    private static File folderScreen = null;
	

	public static void addResults() {
		TestSuite testSuite = jsonReport.testSuites.get(SharedDependencies.suiteName);
		testSuite =  new TestSuite(SharedDependencies.suiteName);

		TestCase testCase = new TestCase(SharedDependencies.caseName, SharedDependencies.results );
		TestCase testCaseExcel = new TestCase(SharedDependencies.caseName, SharedDependencies.results);
		testSuite.testCases.put(SharedDependencies.caseName, testCase);
		jsonReport.testSuites.put(SharedDependencies.suiteName, testSuite);
		jsonReport.aLtestSuites.add(testSuite);
		jsonReport.alTestCases.add(testCaseExcel);
		
	}

	public static void reportExcel() {
		HSSFWorkbook wk = null;
		String result;

		String[] columnsExcel = new String[SharedDependencies.results.size()+2];
		columnsExcel[0] = "Test Case Name";
		columnsExcel[1] = "Result";

		wk = Excel.createExcel(SharedDependencies.folderTestSuite, columnsExcel);
		HSSFSheet sheet = wk.getSheet(SharedDependencies.suiteName);

		Row row = sheet.createRow(sheet.getLastRowNum() + 1);
		Cell tc_cell = row.createCell(0);
		tc_cell.setCellValue(SharedDependencies.caseName);

		if (SharedDependencies.results.size() > 1) {
			if (SharedDependencies.results.contains("KO")) {
				result = "KO";
			} else if (SharedDependencies.results.contains("BQ")) {
				result = "BQ";
			} else {
				result = "OK";
			}
			for (int i = 0; i<SharedDependencies.results.size(); i++) {
				columnsExcel[i+2] = "Iteration: " + (i+1);
				Cell cell = row.createCell(i+2);
				cell.setCellValue(SharedDependencies.results.get(i));
				cell.setCellStyle(Excel.changeColor(SharedDependencies.results.get(i), wk));
				sheet.autoSizeColumn(i);
			}
		} else {
			result = SharedDependencies.results.get(0);
		}

		Cell cell = row.createCell(1);
		cell.setCellValue(result);
		cell.setCellStyle(Excel.changeColor(result, wk));
		sheet.autoSizeColumn(1);

		if (sheet.getRow(0).getPhysicalNumberOfCells() <= columnsExcel.length) {
			Excel.createHeaderExcel(wk, sheet, columnsExcel);
		}

		try {
			FileOutputStream fileOut = new FileOutputStream(
					SharedDependencies.folderTestSuite + SharedDependencies.fileSystem.getSeparator() + "ReportResult.xls");
			wk.write(fileOut);
			fileOut.close();
			wk.close();
		} catch (Exception e) {
			SharedDependencies.logger.severe(e.getMessage());
		}
	}

	
	public static void reportLog(String msg, String log, int wait) {
		try {
			rootFile.mkdirs();
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			String logPath = SharedDependencies.folderTestCase + SharedDependencies.fileSystem.getSeparator() + "Log_" + SharedDependencies.caseName + ".log";
			File logFile = new File(logPath);
			FileWriter fw = new FileWriter(logFile, true);
			if (msg != "") {
				switch(log) {
					case "INFO":
						fw.write(df.format(new Date()) + " - " + log + " - " + msg + "\r\n");
						break;
					case "DEBUG":
						if (SharedDependencies.level.equals("DEBUG")) {
							fw.write(df.format(new Date()) + " - " + log + " - " + msg + "\r\n");
						} else {
							fw.write(df.format(new Date()) + " - INFO - More info changing LOG_LEVEL in confing.properties file\r\n");
						}
						break;
					case "ASYNCHRONOUS":
						if (SharedDependencies.level.equals("DEBUG")) {
							fw.write(df.format(new Date()) + " - DEBUG - " + msg + "\r\n");
						}
						break;
				}
			}

			if (wait > 0) {
				fw.write(df.format(new Date()) + " - " + log + " - " + "Thread sleep " + wait + "ms" + "\r\n");
			}
			
			fw.close();

		} catch (IllegalArgumentException | SecurityException | IOException e) {
			SharedDependencies.logger.severe(e.getMessage());
		}
	}

	public static void reportLog (String msg, String log, int wait, Status status,
		Boolean screenShot, String request, String response, String debugMsg) {
		try {
			rootFile.mkdirs();
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			String logPath = SharedDependencies.folderTestCase + SharedDependencies.fileSystem.getSeparator() + "Log_" + SharedDependencies.caseName + ".log";
			File logFile = new File(logPath);
			FileWriter fw = new FileWriter(logFile, true);
			if (!request.equals("") && !response.equals("") && !response.equals("backendAssertion")) {
				SharedDependencies.test.log(status, request);
				SharedDependencies.test.log(status, response);
				reportLog(msg, log, wait);
			} else if (!request.equals("") && response.equals("backendAssertion")) {
				SharedDependencies.test.log(status, request);
				reportLog(msg, log, wait);
			} else if (request.equals("isCatch")) {
				failedStepReport(msg, log, wait, status, debugMsg);
			} else {
				SharedDependencies.test.log(status, msg);
				if (screenShot == true) {
					capScreenFrequency();
				}
				reportLog(msg, log, wait);
			}
		} catch (Exception e) {
			SharedDependencies.logger.severe(e.getMessage());
			SharedDependencies.test.log(status, msg);
		}
	}

	public static void capScreenFrequency() throws Exception {
		switch(SharedDependencies.screenshot) {
			case "always":
				capScreen();
				break;
			case "only":
				List<String> listResults = Arrays.asList("BQ", "KO");
				String result = SharedDependencies.isAfter ? SharedDependencies.captureLog : SharedDependencies.finalResult;
				if (listResults.contains(result)) {
					capScreen();
				}
				break;
			default:
				break;
		}
	}

	public static void capScreen() {
		try {
			String timeStamp = new SimpleDateFormat("HH.mm.ss.SSS").format(Calendar.getInstance().getTime());
			String name = "";

			if(SharedDependencies.isAfter == true) {
				name = SharedDependencies.captureLog + "_" + SharedDependencies.caseName;
			} else {
				name = SharedDependencies.finalResult + "_" + SharedDependencies.caseName;
			}
			TakesScreenshot ts = (TakesScreenshot) SharedDependencies.driver;
			File sourcePath = ts.getScreenshotAs(OutputType.FILE);
			folderScreen = new File(SharedDependencies.folderTestCase + SharedDependencies.fileSystem.getSeparator() + "screenshots");
			folderScreen.mkdir();
			String path = folderScreen + SharedDependencies.fileSystem.getSeparator() + name + "_" + timeStamp + ".png";
			

			File destination = new File(path);

			if (name.contains("BQ") || name.contains("KO")) {
				try {
					String relativePath = path.split(SharedDependencies.dat)[1].substring(1);
					SharedDependencies.test.addScreenCaptureFromPath(relativePath);
				} catch (IOException e) {
					SharedDependencies.logger.log(Level.SEVERE, e.getMessage(), e);
				}
			}

			Files.copy(sourcePath, destination);

		} catch (Exception e) {
			SharedDependencies.logger.log(Level.SEVERE, e.getMessage(), e);
			SharedDependencies.logger.severe(e.getMessage());
		}
	}

	
	

	private static void failedStepReport(String msg, String log, int wait,
		Status status, String debugMsg) throws Exception {
		
		rootFile.mkdirs();
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		String logPath = SharedDependencies.folderTestCase + SharedDependencies.fileSystem.getSeparator() + "Log_" + SharedDependencies.caseName + ".log";
		File logFile = new File(logPath);
		FileWriter fw = new FileWriter(logFile, true);
		
		if (SharedDependencies.level.equals("DEBUG")) {
			fw.write(df.format(new Date()) + " - " + "ERROR" + " - " + msg + "\r\n");
			fw.write(df.format(new Date()) + " - " + log + " - " + debugMsg + "\r\n");
		} else {
			fw.write(df.format(new Date()) + " - " + "ERROR" + " - " + msg + "\r\n");
			fw.write(df.format(new Date()) + " - " + "INFO" +
			" - " + "More info changing LOG_LEVEL in confing.properties file\r\n");
		}
		if (wait > 0) {
			fw.write(df.format(new Date()) + " - " + log + " - " + "Thread sleep " + wait + "ms" + "\r\n");
		}
		
		fw.close();

		if (SharedDependencies.level.equals("INFO")) { 
			msg = StringEscapeUtils.escapeHtml4(msg);
			SharedDependencies.test.log(status, msg);
		} else {
			debugMsg = StringEscapeUtils.escapeHtml4(debugMsg);
			SharedDependencies.test.log(status, debugMsg);
		}
		if (!SharedDependencies.capScreenExempt && !debugMsg.contains("SQLException")) {
			capScreenFrequency();
		}
	}

	public static void reportConsoleLogs(String msg, Level logginLevel) {
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            try {
                FileWriter fw = new FileWriter(SharedDependencies.consoleLogFile, true);
                if (logginLevel.equals(Level.SEVERE)) {
                    fw.write(df.format(new Date()) + " - " + "ERROR" + " - " + "\n" + msg + "\r\n");
                    SharedDependencies.logger.severe("\n" + msg);
                } else {
                    fw.write(df.format(new Date()) + " - " + "INFO" + " - " + "\n" + msg + "\r\n");
                    SharedDependencies.logger.info("\n" + msg);
                }
                fw.close();
            } catch (Exception e) {
                SharedDependencies.logger.severe("Error creating errors file");
            }
        }

  public static void finalReports(boolean screenShot) {
        try {
          Report.reportExcel();
          
        } catch (Exception e) {
          Report.reportConsoleLogs(e.getMessage(), Level.SEVERE);
        }
      }
}
