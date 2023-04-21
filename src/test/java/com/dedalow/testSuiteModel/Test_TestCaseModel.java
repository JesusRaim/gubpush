package com.dedalow.testSuiteModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.List;
import java.util.logging.Level;
import java.time.Duration;


import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.common.base.Throwables;
import com.dedalow.utils.*;
import com.dedalow.report.ExtentHtml;
import com.dedalow.report.Report;
import com.dedalow.Launcher;

import com.aventstack.extentreports.Status;



public class Test_TestCaseModel {

    private static Class reflectiveClass;
    private static Launcher launcher = new Launcher();
    public static DriverInit driverInit = new DriverInit();
    public static Constant constant = launcher.constant;
    public static String suiteName = "TestSuiteModel";
    public static String caseName = Test_TestCaseModel.class.getSimpleName();
    public static String modelDocumentation = "";
    public static WebDriver driver;

    public static Properties prop;
    public static String level;
    public static String handler;
    public static String finalResult = "OK";
    public static File folderTestCase;
    public static File folderDownloads;

    
    

    @BeforeEach
    public void beforeEach() throws Exception {
    try {
        constant.defaultValues();
        prop = Utils.getConfigProperties();
        setUp();
        driver = setUpEnvironment(folderDownloads, prop, "MAIN_CONTEXT", constant.contextsDriver);
        
    } catch (AssertionError | Exception e) {
        Report.reportConsoleLogs(e.getMessage(), Level.SEVERE);
        finalResult = "BQ";
        Report.reportLog(reflectiveClass, e.getMessage(), level, 0, Status.FAIL, true, "isCatch", "", Throwables.getStackTraceAsString(e));
        throw new Exception(e);
        }
    }

    

    @Test
	@DisplayName("Test_TestCaseModel")
	public void test() throws Exception {
        try {
            
            
            
            Report.reportLog(reflectiveClass, "Start of execution", "INFO", 0, Status.PASS, false, "", "", null);
            
            driver.get(prop.getProperty("WEB_URL") + "");
            Report.reportLog(reflectiveClass, "Navigated to " + prop.getProperty("WEB_URL") + "", "INFO", 0, Status.PASS, true, "", "", null);
			
        } catch (AssertionError | Exception e) {
            Report.reportConsoleLogs(e.getMessage(), Level.SEVERE);
            if (finalResult != "BQ") {
				finalResult = "KO";
			}
            Report.reportLog(reflectiveClass, e.getMessage(), level, 0, Status.FAIL, true, "isCatch", "", Throwables.getStackTraceAsString(e));
            throw new Exception(e);
        }
    }

    

    @AfterEach
    public void afterEach()  {
        boolean screenShot = true;
        
        if (finalResult == "OK") {
            Report.reportLog(reflectiveClass, "Result on Test_TestCaseModel: " + finalResult, "INFO", 0, Status.PASS, false, "", "", null);
        } else {
            Report.reportLog(reflectiveClass, "Result on Test_TestCaseModel: " + finalResult, "INFO", 0, Status.FAIL, false, "", "", null);
        }
        constant.logger.info("Result on Test_TestCaseModel: " + finalResult);
        Utils.tearDown(reflectiveClass);
        Utils.finalReports(reflectiveClass, screenShot);
        constant.initialize.flush();
    }

    /**
     * Assign initial values to variables before execution
     * @throws Exception
     */
    
    public static void setUp() throws Exception {
        try {
            constant.initialize = new ExtentHtml(suiteName, caseName, modelDocumentation);
            constant.test = constant.initialize.getTest();
            constant.folderTestSuite = new File(constant.folderLogs + constant.fileSystem.getSeparator() + suiteName);
            constant.folderTestSuite.mkdirs();
            folderTestCase = new File(constant.folderTestSuite + constant.fileSystem.getSeparator() + caseName);
            folderTestCase.mkdirs();
            folderDownloads = new File(folderTestCase + constant.fileSystem.getSeparator() + "files");
            folderDownloads.mkdirs();
            level = prop.getProperty("LOG_LEVEL").trim().toUpperCase();
            constant.timeout = Integer.parseInt(prop.getProperty("WEB_TIMEOUT"));
            Utils.setEncoding();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e);
        }
    }

    public static WebDriver setUpEnvironment(File folderDownloads, Properties prop,
        String nameDriver, Map<String, WebDriver> contextsDriver) throws Exception {
        driver = driverInit.driverSelector(folderDownloads, prop, nameDriver, contextsDriver);
            handler = driver.getWindowHandle();
        reflectiveClass = Utils.getReflective(Test_TestCaseModel.class.getCanonicalName());
        return driver;
    }
}