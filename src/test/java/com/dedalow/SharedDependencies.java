package com.dedalow;

import com.dedalow.utils.*;
import com.dedalow.report.ExtentHtml;

import java.io.File;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.Properties;

import com.aventstack.extentreports.ExtentTest;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.JavascriptExecutor;

import io.restassured.response.Response;



public class SharedDependencies {

    public static FileSystem fileSystem = FileSystems.getDefault();
    private static Boolean initializationRequired = true;
    public static Utils utils;
    public static Properties prop;

    // Test
    public static boolean isAfter;
    public static String fileLocation;
    public static Response apiResponse;
    public static String suiteName;
    public static String caseName;
    public static String modelDocumentation;

    // WebDriver
	public static Map<String, WebDriver> contextsDriver;
    public static Integer timeout;
    public static DriverInit driverInit;
    public static WebDriver driver;
    public static String handler;
    public static JavascriptExecutor js;

    // Reports
    public static String root = System.getProperty("user.dir") + fileSystem.getSeparator() + "logs";
    public static DateFormat dfNameF = new SimpleDateFormat("dd_MM_yyyy_HH_mm");
    public static String dat = dfNameF.format(new Date());
    public static File folderLogs = new File(root + fileSystem.getSeparator() + dat);
    public static File folderTestSuite;
    public static File consoleLogFile = new File(folderLogs + fileSystem.getSeparator() + "consoleLogs.log");
    public static Logger logger;
    public static ArrayList<String> results;
    public static String afterResult;
    public static String captureLog;
    public static String screenshot;
    public static ExtentHtml initialize;
	public static ExtentTest test;
    public static Boolean capScreenExempt;
    public static String level;
    public static String finalResult;
    public static File folderTestCase;
    public static File folderDownloads;
  

    
    
    

    public static void init() throws Exception {
      if (initializationRequired) {
          initializationRequired = false;
          folderLogs.mkdirs();
          utils = new Utils();
          logger = Utils.logger();
          prop = Utils.getConfigProperties();
          driverInit = new DriverInit();
          level = prop.getProperty("LOG_LEVEL").trim().toUpperCase();
          Utils.setEncoding();
          
          
      }
    }

    public static void defaultValues(String testSuiteName, String testCaseName) throws Exception {
      suiteName = testSuiteName;
      caseName = testCaseName;
      isAfter = false;
      fileLocation = "";
      capScreenExempt = false;
      contextsDriver = new HashMap<String, WebDriver>();
      results = new ArrayList<String>();
      afterResult = "succesfully";
      captureLog ="OK";
      finalResult = "OK";
      folderTestSuite = new File(folderLogs + fileSystem.getSeparator() + testSuiteName);
      folderTestSuite.mkdirs();
      folderTestCase = new File(folderTestSuite + fileSystem.getSeparator() + testCaseName);
      folderTestCase.mkdirs();
      folderDownloads = new File(folderTestCase + fileSystem.getSeparator() + "files");
      folderDownloads.mkdirs();
    }

    public static void setUpEnvironment(String nameDriver) throws Exception {
      driver = driverInit.driverSelector(nameDriver);
      handler = driver.getWindowHandle();
      js = (JavascriptExecutor)driver;
    }

}