package com.dedalow.testSuiteModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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
import com.dedalow.SharedDependencies;

import com.aventstack.extentreports.Status;



public class Test_TestCaseModel {

    

    @BeforeEach
    public void beforeEach() throws Exception {
    try {
        setUp();
		SharedDependencies.setUpEnvironment("MAIN_CONTEXT");
        
    } catch (AssertionError | Exception e) {
        Report.reportConsoleLogs(e.getMessage(), Level.SEVERE);
        SharedDependencies.finalResult = "BQ";
        Report.reportLog(e.getMessage(), SharedDependencies.level, 0, Status.FAIL, true, "isCatch", "", Throwables.getStackTraceAsString(e));
        throw new Exception(e);
        }
    }

    

    @Test
	@DisplayName("Test_TestCaseModel")
	public void test() throws Exception {
        try {
            
            
            
            Report.reportLog("Start of execution", "INFO", 0, Status.PASS, false, "", "", null);
            
            SharedDependencies.driver.get(SharedDependencies.prop.getProperty("WEB_URL") + "");
            Report.reportLog("Navigated to " + SharedDependencies.prop.getProperty("WEB_URL") + "", "INFO", 0, Status.PASS, true, "", "", null);
			
        } catch (AssertionError | Exception e) {
            Report.reportConsoleLogs(e.getMessage(), Level.SEVERE);
            if (SharedDependencies.finalResult != "BQ") {
				SharedDependencies.finalResult = "KO";
			}
            Report.reportLog(e.getMessage(), SharedDependencies.level, 0, Status.FAIL, true, "isCatch", "", Throwables.getStackTraceAsString(e));
            throw new Exception(e);
        }
    }

    

    @AfterEach
    public void afterEach()  {
        boolean screenShot = true;
        
        if (SharedDependencies.finalResult == "OK") {
            Report.reportLog("Result on Test_TestCaseModel: " + SharedDependencies.finalResult, "INFO", 0, Status.PASS, false, "", "", null);
        } else {
            Report.reportLog("Result on Test_TestCaseModel: " + SharedDependencies.finalResult, "INFO", 0, Status.FAIL, false, "", "", null);
        }
        SharedDependencies.logger.info("Result on Test_TestCaseModel: " + SharedDependencies.finalResult);
        SharedDependencies.initialize.flush();
        DriverInit.clearWebDrivers();
        SharedDependencies.results.add(SharedDependencies.finalResult);
        Report.addResults();
        Report.finalReports(screenShot);
        SharedDependencies.initialize.flush();
    }

    /**
     * Assign initial values to variables before execution
     * @throws Exception
     */
    
    public static void setUp() throws Exception {
        try {
            SharedDependencies.init();
            SharedDependencies.screenshot = SharedDependencies.utils.configScreenshot();
        SharedDependencies.timeout = Integer.parseInt(SharedDependencies.prop.getProperty("WEB_TIMEOUT"));
        SharedDependencies.utils.checkConnection(SharedDependencies.prop);
            SharedDependencies.defaultValues("TestSuiteModel", "Test_TestCaseModel");
            SharedDependencies.initialize = new ExtentHtml("Test_TestCaseModel");
            SharedDependencies.test = SharedDependencies.initialize.getTest();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e);
        }
    }

}