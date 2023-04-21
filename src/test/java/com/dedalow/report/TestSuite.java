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
import com.dedalow.SharedDependencies;

public class TestSuite {
	public String name;
	public String testSuiteTL;
	public Map<String, TestCase> testCases = new HashMap();

	public TestSuite(String name) {
		this.name = name;
		this.testSuiteTL = SharedDependencies.prop.getProperty("Testlink.suite." + this.name);
	}
}