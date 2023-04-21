package com.dedalow.utils;

import java.io.File;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import org.openqa.selenium.WebDriver;
import com.aventstack.extentreports.ExtentTest;
import com.dedalow.report.ExtentHtml;

import io.restassured.response.Response;


public class Constant {

    public FileSystem fileSystem = FileSystems.getDefault();
	public Utils utils;
	
    // Test
	public boolean isAfter;
	public String fileLocation;
	public Response apiResponse;
	public Boolean capScreenExempt;

    // WebDriver
	public Map<String, WebDriver> contextsDriver;
	public Integer timeout;

    // Log folder
	public String root = System.getProperty("user.dir") + fileSystem.getSeparator() + "logs";
	public DateFormat dfNameF = new SimpleDateFormat("dd_MM_yyyy_HH_mm");
	public String dat = dfNameF.format(new Date());
	public File folderLogs = new File(root + fileSystem.getSeparator() + dat);
	public File folderTestSuite;
	public File consoleLogFile = new File(folderLogs + fileSystem.getSeparator() + "consoleLogs.log");

    // Reports
	public Logger logger = Utils.logger();
	public ArrayList<String> results;
	public String afterResult;
	public String captureLog;
	public String screenshot;

    // Report HTML
	public ExtentHtml initialize;
	public ExtentTest test;

	/**
	 * Constructor
	 */
	public Constant() {
		utils = new Utils();
		folderLogs.mkdirs();
	}

	
	

	public void defaultValues() throws Exception {
		isAfter = false;
		fileLocation = "";
		capScreenExempt = false;
		contextsDriver = new HashMap<String, WebDriver>();
		results = new ArrayList<String>();
		afterResult = "succesfully";
		captureLog ="OK";
		screenshot = utils.configScreenshot();
		
	}

}