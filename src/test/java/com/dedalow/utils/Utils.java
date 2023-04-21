package com.dedalow.utils;

import java.io.FileInputStream;
import java.io.IOException;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.logging.ErrorManager;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.text.SimpleDateFormat;

import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.dedalow.Launcher;
import com.dedalow.report.Report;
import com.aventstack.extentreports.Status;
import com.dedalow.utils.Constant;

public class Utils {

    private static Logger logger = Logger.getLogger(Launcher.class.getName());
    private static Handler consoleHandler = initHandler();
    public static Properties prop;

    public static void checkConnection(Properties prop) {
        Pattern regex = Pattern.compile(":?(http(?:s)*://)*([^:|/]*):?([0-9]*)([^$]*)");
    	Matcher homePage = regex.matcher(prop.getProperty("WEB_URL"));
    	homePage.find();
    	try {
    		InetAddress.getByName(homePage.group(2));
		} catch (UnknownHostException e) {
			Report.reportConsoleLogs("No connection established", Level.SEVERE);
		}
    }

    public static Properties getConfigProperties() throws Exception {
        try {
            prop = new Properties();
            prop.load(new FileInputStream("config.properties"));
            checkConnection(prop);
            return prop;
        } catch (Exception e) {
            throw new Exception ("Can not find config.properties file");
        }
    }

    public static boolean isElementEnabled(WebElement x, WebDriver driver) {
        turnOffImplicitWaits(driver);
        boolean result = x.isEnabled();
        turnOnImplicitWaits(driver);
        return result;
    }

    private static void turnOffImplicitWaits(WebDriver driver) {
        driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
    }

    private static void turnOnImplicitWaits(WebDriver driver) {
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    }

    public static String generateJSONBody (String JSONPath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(JSONPath)));
    }

    public static Handler initHandler() {
        return new Handler() {
            @Override
            public void publish(LogRecord record) {
                if (getFormatter() == null) {
                    setFormatter(new SimpleFormatter());
                }

                try {
                    String message = getFormatter().format(record);
                    if (record.getLevel().intValue() >= Level.WARNING.intValue()) {
                        System.err.write(message.getBytes());
                    } else {
                        System.out.write(message.getBytes());
                    }
                } catch (Exception exception) {
                    reportError(null, exception, ErrorManager.FORMAT_FAILURE);
                }
            }

            @Override
            public void close() throws SecurityException {
            }

            @Override
            public void flush() {
            }
        };
    }

    public static Logger logger() {
        for (Handler handler : logger.getHandlers()) {
            logger.removeHandler(handler);
        }
        logger = Logger.getLogger(Launcher.class.getName());
        logger.setUseParentHandlers(false);
        logger.addHandler(consoleHandler);
        return logger;
    }

    public static Class getReflective(String classRoute) {
        Class reflectiveClass = null;
        try {
            Object reflective = Class.forName(classRoute).newInstance();
            reflectiveClass = reflective.getClass();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            Report.reportConsoleLogs(e.getMessage(), Level.SEVERE);
        }
        return reflectiveClass;
    }

    /**
     * Reads and convert to lower cases the SCREENSHOT property of the config.properties file
     * @return String
     * @throws Exception 
     */
    public String configScreenshot() throws Exception {
        getConfigProperties();
        List<String> options = Arrays.asList("always", "only", "never");
        String screenshot = prop.getProperty("SCREENSHOT");
        int spacePosition = screenshot.indexOf(" ");
        String result = screenshot.toLowerCase();

        if (spacePosition > 0) {
            result = result.substring(0, spacePosition);
        }

        if (!options.contains(result)) throw new Exception ("The option of the variable SCREENSHOT in the file config.properties is not correct. "
				+ "It must contain one of these options: Always, Only on error or Never");

        return result;
    }

    public static ArrayList<String> getTestCasesSelected() throws Exception {
    ArrayList<String> testCasesSelected = new ArrayList<String>();
    getConfigProperties();
    if (!prop.getProperty("TESTSUITES").isEmpty() || !prop.getProperty("TESTCASES").isEmpty()) {

        if (!prop.getProperty("TESTSUITES").isEmpty()) {
            String[] testSuites = prop.getProperty("TESTSUITES").split(", | |,");
            for (String suite : testSuites) {
                String nameSuite = suite.substring(0, 1).toLowerCase() + suite.substring(1);
                testCasesSelected = getTestCases(nameSuite, testCasesSelected);
            }
        }

        if(!prop.getProperty("TESTCASES").isEmpty()) {
            String[] testCases = prop.getProperty("TESTCASES").split(", | |,");
            for (String testCase : testCases) {
                ArrayList<String> listTestCases = new ArrayList<String>();
                boolean testCaseExist = false;
                String nameCase = testCase.substring(0, 1).toUpperCase() + testCase.substring(1);

                listTestCases = getTestCases("complete", listTestCases);
                for (String listCase : listTestCases) {
                    if (listCase.matches(".+Test_" + nameCase)) {
                        testCasesSelected.add(listCase);
                        testCaseExist = true;
                    }
                }
                if (!testCaseExist) {
                    throw new Exception ("The TestCase " + nameCase + " does not exist");
                }
            }
        }

    } else {
        testCasesSelected = getTestCases("complete", testCasesSelected);
    }

    return testCasesSelected;
}

    public static ArrayList<String> getTestCases(String option, ArrayList<String> testCases) throws Exception {
        switch (option) {
            case "testSuiteModel":
            	testCases.add("com.dedalow.testSuiteModel.Test_TestCaseModel");
			
            break;
			
            case "complete":
                	testCases.add("com.dedalow.testSuiteModel.Test_TestCaseModel");
			
                break;
            default:
                throw new Exception ("The TestSuite " + option + " does not exist");
        }

        return testCases;
    }

    public static void tearDown(Class reflectiveClass) {
    try {
        String finalResult = (String) reflectiveClass.getField("finalResult").get(reflectiveClass);
        String suiteName = (String) reflectiveClass.getField("suiteName").get(reflectiveClass);
        String caseName = (String) reflectiveClass.getField("caseName").get(reflectiveClass);
        Constant constant = (Constant) reflectiveClass.getField("constant").get(reflectiveClass);
        
        constant.results.add(finalResult);
        Report.addResults(suiteName, caseName, constant.results);
        constant.initialize.flush();
        for (Map.Entry<String, WebDriver> context : constant.contextsDriver.entrySet()) {
            if (!context.getValue().toString().contains("Firefox")) {
                context.getValue().close();
            }
            context.getValue().quit();
        }
        constant.contextsDriver.clear();
    } catch (Exception e) {
        Report.reportConsoleLogs(e.getMessage(), Level.SEVERE);
    }
}

    public static void finalReports(Class reflectiveClass, boolean screenShot) {
    try {
        

        Report.reportExcel(reflectiveClass);

    } catch (Exception e) {
        Report.reportConsoleLogs(e.getMessage(), Level.SEVERE);
    }
}

    public static Boolean checkDownload(Class reflectiveClass, String path,
		Integer directoryLength, File directoryPath) throws Exception {
		long start = System.currentTimeMillis();
		long end = start + Long.parseLong(prop.getProperty("WEB_TIMEOUT"))*1000;

		while (System.currentTimeMillis() <= end) {
			if (directoryLength != directoryPath.listFiles().length) {
				if (!isDownloadInProgress(directoryPath)) {
					Report.reportLog(reflectiveClass, "File downloaded in " + path, "INFO", 0, Status.PASS, false, "", "",
							null);
					return true;
				}
			}
		}

		Report.reportLog(reflectiveClass, "Reached timeOut. Specify more time in config.properties file", "INFO", 0,
				Status.FAIL, false, "", "", null);

		return false;
	}

    public static Boolean isDownloadInProgress (File directoryPath) {
        String [] partialDownloadExtensions = {".crdownload", ".tmp", ".part"};
        File[] files  = directoryPath.listFiles();
        Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());
		try {
            String lastFile = files[0].toString();
            return Arrays.stream(partialDownloadExtensions).anyMatch(extension -> lastFile.endsWith(extension));
		} catch (IndexOutOfBoundsException e) {
            return true;
		}
    }

    public static void setEncoding() {
		try {
			System.setProperty("file.encoding", "UTF-8");
			Field charset = Charset.class.getDeclaredField("defaultCharset");
			charset.setAccessible(true);
			charset.set(null, null);
		} catch (Exception e) {
			Report.reportConsoleLogs(e.getMessage(), Level.SEVERE);
		}
	}

    /**
     * Gets the values stored in variables using ${}
     * @param value
     * @param variableList
     * @param Variables
     */
    public static String getVariables(String value, String[] variableList, HashMap<String, String> Variables, String type) {
      for (int i = 0; i < variableList.length; i++) {
        if (type.equals("json") && !value.contains("\"${" + variableList[i] + "}\"")) {
          value = value.replace("${" + variableList[i] + "}", "\"" + Variables.get(variableList[i]) + "\"");
        } else {
          value = value.replace("${" + variableList[i] + "}", Variables.get(variableList[i]));
        }
      }
      return value;
    }

}