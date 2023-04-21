package com.dedalow.report;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
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
import com.dedalow.utils.Constant;
import com.dedalow.report.Excel;
import com.dedalow.Launcher;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;

public class Report {

	private static FileSystem fileSystem = FileSystems.getDefault();
	private static String root = System.getProperty("user.dir") + fileSystem.getSeparator() + "logs";
	private static File rootFile = new File(root);
	private static Launcher launcher = new Launcher();
	private static Constant constant = launcher.constant;
	private static Properties prop;
	private static Logger logger = constant.logger;
	private static JsonReport jsonReport = new JsonReport();
    private static File folderScreen = null;
	

	public static void addResults(String suiteName, String caseName, ArrayList<String> results) {
		TestSuite testSuite = jsonReport.testSuites.get(suiteName);
		testSuite =  new TestSuite(suiteName);

		TestCase testCase = new TestCase(caseName, results );
		TestCase testCaseExcel = new TestCase(caseName, results);
		testSuite.testCases.put(caseName, testCase);
		prop = new Properties();
		try {
			prop.load(new FileInputStream("config.properties"));

		} catch (FileNotFoundException e) {
			logger.severe(e.getMessage());
		} catch (IOException e) {
			logger.severe(e.getMessage());
		}

		jsonReport.testSuites.put(suiteName, testSuite);
		jsonReport.aLtestSuites.add(testSuite);
		jsonReport.alTestCases.add(testCaseExcel);
		
	}

	public static void reportExcel(Class reflectiveClass) {
		File route = null;
		String suiteName = null;
		String caseName = null;
		ArrayList<String> results = null;
		HSSFWorkbook wk = null;
		String result;

		try {
			route = constant.folderTestSuite;
			suiteName = (String) reflectiveClass.getField("suiteName").get(reflectiveClass);
			caseName = (String) reflectiveClass.getField("caseName").get(reflectiveClass);
			results = constant.results;
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			logger.severe(e.getMessage());
		}

		String[] columnsExcel = new String[results.size()+2];
		columnsExcel[0] = "Test Case Name";
		columnsExcel[1] = "Result";

		wk = Excel.createExcel(route, suiteName, columnsExcel);
		HSSFSheet sheet = wk.getSheet(suiteName);

		Row row = sheet.createRow(sheet.getLastRowNum() + 1);
		Cell tc_cell = row.createCell(0);
		tc_cell.setCellValue(caseName);

		if (results.size() > 1) {
			if (results.contains("KO")) {
				result = "KO";
			} else if (results.contains("BQ")) {
				result = "BQ";
			} else {
				result = "OK";
			}
			for (int i = 0; i<results.size(); i++) {
				columnsExcel[i+2] = "Iteration: " + (i+1);
				Cell cell = row.createCell(i+2);
				cell.setCellValue(results.get(i));
				cell.setCellStyle(Excel.changeColor(results.get(i), wk));
				sheet.autoSizeColumn(i);
			}
		} else {
			result = results.get(0);
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
					route + fileSystem.getSeparator() + "ReportResult.xls");
			wk.write(fileOut);
			fileOut.close();
			wk.close();
		} catch (Exception e) {
			logger.severe(e.getMessage());
		}
	}

	
	public static void reportLog(Class reflectiveClass, String msg, String log, int wait) {
		try {
			File route = (File) reflectiveClass.getField("folderTestCase").get(reflectiveClass);
			String script = (String) reflectiveClass.getField("caseName").get(reflectiveClass);
			String level = (String) reflectiveClass.getField("level").get(reflectiveClass);
			String finalResult = (String) reflectiveClass.getField("finalResult").get(reflectiveClass);

			rootFile.mkdirs();
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			String logPath = route + fileSystem.getSeparator() + "Log_" + script + ".log";
			File logFile = new File(logPath);
			FileWriter fw = new FileWriter(logFile, true);
			if (msg != "") {
				switch(log) {
					case "INFO":
						fw.write(df.format(new Date()) + " - " + log + " - " + msg + "\r\n");
						break;
					case "DEBUG":
						if (level.equals("DEBUG")) {
							fw.write(df.format(new Date()) + " - " + log + " - " + msg + "\r\n");
						} else {
							fw.write(df.format(new Date()) + " - INFO - More info changing LOG_LEVEL in confing.properties file\r\n");
						}
						break;
					case "ASYNCHRONOUS":
						if (level.equals("DEBUG")) {
							fw.write(df.format(new Date()) + " - DEBUG - " + msg + "\r\n");
						}
						break;
				}
			}

			if (wait > 0) {
				fw.write(df.format(new Date()) + " - " + log + " - " + "Thread sleep " + wait + "ms" + "\r\n");
			}
			
			fw.close();

		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException | IOException e) {
			logger.severe(e.getMessage());
		}
	}

	public static void reportLog (Class reflectiveClass, String msg, String log, int wait, Status status,
		Boolean screenShot, String request, String response, String debugMsg) {
		try {
			File route = (File) reflectiveClass.getField("folderTestCase").get(reflectiveClass);
			String script = (String) reflectiveClass.getField("caseName").get(reflectiveClass);

			rootFile.mkdirs();
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			String logPath = route + fileSystem.getSeparator() + "Log_" + script + ".log";
			File logFile = new File(logPath);
			FileWriter fw = new FileWriter(logFile, true);
			if (!request.equals("") && !response.equals("") && !response.equals("backendAssertion")) {
				constant.test.log(status, request);
				constant.test.log(status, response);
				reportLog(reflectiveClass, msg, log, wait);
			} else if (!request.equals("") && response.equals("backendAssertion")) {
				constant.test.log(status, request);
				reportLog(reflectiveClass, msg, log, wait);
			} else if (request.equals("isCatch")) {
				failedStepReport(msg, log, wait, reflectiveClass, status, debugMsg);
			} else {
				constant.test.log(status, msg);
				if (screenShot == true) {
					capScreenFrequency(reflectiveClass);
				}
				reportLog(reflectiveClass, msg, log, wait);
			}
		} catch (Exception e) {
			logger.severe(e.getMessage());
			constant.test.log(status, msg);
		}
	}

	public static void capScreenFrequency(Class reflectiveClass) throws Exception {
		switch(constant.screenshot) {
			case "always":
				capScreen(reflectiveClass);
				break;
			case "only":
				List<String> listResults = Arrays.asList("BQ", "KO");
				String result = constant.isAfter ? constant.captureLog : (String) reflectiveClass.getField("finalResult").get(reflectiveClass);
				if (listResults.contains(result)) {
					capScreen(reflectiveClass);
				}
				break;
			default:
				break;
		}
	}

	public static void capScreen(Class reflectiveClass) {
		try {
			String result = (String) reflectiveClass.getField("finalResult").get(reflectiveClass);
			File route = (File) reflectiveClass.getField("folderTestCase").get(reflectiveClass);
			String script = (String) reflectiveClass.getField("caseName").get(reflectiveClass);
			WebDriver driver = (WebDriver) reflectiveClass.getField("driver").get(reflectiveClass);
			String timeStamp = new SimpleDateFormat("HH.mm.ss.SSS").format(Calendar.getInstance().getTime());
			String name = "";

			if(constant.isAfter == true) {
				name = constant.captureLog + "_" + script;
			} else {
				name = result + "_" + script;
			}
			TakesScreenshot ts = (TakesScreenshot) driver;
			File sourcePath = ts.getScreenshotAs(OutputType.FILE);
			folderScreen = new File(route + fileSystem.getSeparator() + "screenshots");
			folderScreen.mkdir();
			String path = folderScreen + fileSystem.getSeparator() + name + "_" + timeStamp + ".png";
			

			File destination = new File(path);

			if (name.contains("BQ") || name.contains("KO")) {
				try {
					String relativePath = path.split(constant.dat)[1].substring(1);
					constant.test.addScreenCaptureFromPath(relativePath);
				} catch (IOException e) {
					constant.logger.log(Level.SEVERE, e.getMessage(), e);
				}
			}

			Files.copy(sourcePath, destination);

		} catch (Exception e) {
			constant.logger.log(Level.SEVERE, e.getMessage(), e);
			logger.severe(e.getMessage());
		}
	}

	
	

	private static void failedStepReport(String msg, String log, int wait,
		Class reflectiveClass, Status status, String debugMsg) throws Exception {
		File route = (File) reflectiveClass.getField("folderTestCase").get(reflectiveClass);
		String script = (String) reflectiveClass.getField("caseName").get(reflectiveClass);
		String level = (String) reflectiveClass.getField("level").get(reflectiveClass);
		
		rootFile.mkdirs();
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		String logPath = route + fileSystem.getSeparator() + "Log_" + script + ".log";
		File logFile = new File(logPath);
		FileWriter fw = new FileWriter(logFile, true);
		
		if (level.equals("DEBUG")) {
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

		if (level.equals("INFO")) { 
			msg = StringEscapeUtils.escapeHtml4(msg);
			constant.test.log(status, msg);
		} else {
			debugMsg = StringEscapeUtils.escapeHtml4(debugMsg);
			constant.test.log(status, debugMsg);
		}
		if (!constant.capScreenExempt && !debugMsg.contains("SQLException")) {
			capScreenFrequency(reflectiveClass);
		}
	}

	public static void reportConsoleLogs(String msg, Level logginLevel) {
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            try {
                FileWriter fw = new FileWriter(constant.consoleLogFile, true);
                if (logginLevel.equals(Level.SEVERE)) {
                    fw.write(df.format(new Date()) + " - " + "ERROR" + " - " + "\n" + msg + "\r\n");
                    logger.severe("\n" + msg);
                } else {
                    fw.write(df.format(new Date()) + " - " + "INFO" + " - " + "\n" + msg + "\r\n");
                    logger.info("\n" + msg);
                }
                fw.close();
            } catch (Exception e) {
                logger.severe("Error creating errors file");
            }
        }
}
