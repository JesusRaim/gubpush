package com.dedalow.report;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.logging.Level;

import com.dedalow.utils.Utils;
import com.dedalow.report.Report;

public class TestSuite {
	public static Properties prop;
	public String name;
	public String testSuiteTL;
	public Map<String, TestCase> testCases = new HashMap();
	private static Logger logger = Utils.logger();

	public TestSuite(String name) {
		prop = new Properties();
		try {
			prop.load(new FileInputStream("config.properties"));
		} catch (IOException e) {
			Report.reportConsoleLogs(e.getMessage(), Level.SEVERE);
		}
		this.name = name;
		this.testSuiteTL = prop.getProperty("Testlink.suite." + this.name);
	}
}