package com.dedalow.report;

import java.util.logging.Logger;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;

import java.io.File;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;

import com.dedalow.Launcher;
import com.dedalow.utils.*;
          import com.dedalow.utils.Constant;

	public class ExtentHtml {
		public static FileSystem fileSystem = FileSystems.getDefault();
		public static String resources =  System.getProperty("user.dir") + fileSystem.getSeparator() + "resources";
		public static Launcher launcher = new Launcher();
		public static Constant constant = launcher.constant;
		public static ExtentReports extent = new ExtentReports();
		public static boolean isNotInitializated = false;
		private static Logger logger = Utils.logger();
		public static ExtentTest test;
		public static ExtentTest extentSuiteName;
		private static ExtentHtmlReporter htmlReporter;

			public ExtentTest getTest() {
				return test;
			}

			public void flush() {
				extent.flush();
			}

		public ExtentHtml (String suiteName, String caseName, String modelDocumentation) {
			if (!isNotInitializated) {
				createTest();
				isNotInitializated = true;
			}
			try {
				test = extent.createTest(caseName, modelDocumentation);
				test.assignCategory(suiteName);
			} catch (IllegalArgumentException | SecurityException e) {
				logger.severe(e.getMessage());
			}


		}

		public static void createTest() {
			LoggerContext context = (LoggerContext) LogManager.getContext(false);
			File file = new File(resources + fileSystem.getSeparator() + "extentReports" + fileSystem.getSeparator() +"log4j.properties");
			context.setConfigLocation(file.toURI());
			htmlReporter = new ExtentHtmlReporter(constant.folderLogs + fileSystem.getSeparator() + "bugpushResults.html");
			htmlReporter.loadXMLConfig(resources + fileSystem.getSeparator()+ "extentReports" + fileSystem.getSeparator() + "extent_config.xml");
			customizeHtml(htmlReporter);
			isNotInitializated = true;
		}

		private static void customizeHtml(ExtentHtmlReporter htmlReporter) {
      String username = System.getProperty("user.name");
      String os = System.getProperty("os.name");
      String arch = System.getProperty("os.arch");
      String javaVersion = System.getProperty("java.specification.version");

      extent.setSystemInfo("Tester name", username);
      extent.setSystemInfo("Operative System", os);
      extent.setSystemInfo("Java Version", javaVersion);
      extent.attachReporter(htmlReporter);
		}
	}