package com.dedalow.report;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.logging.Level;

import com.dedalow.utils.Utils;
import com.dedalow.report.Report;

public class TestCase {
	public String name;
    public ArrayList<String> result;
    public String externalId;
    public static Properties prop;
    public ArrayList<String> notes;
    public ArrayList<String> screenShootsPaths;
    private static Logger logger = Utils.logger();

    public TestCase(String name, ArrayList<String> result, ArrayList<String> notes, ArrayList<String> screenShootsPaths) {
        prop = new Properties();
        try {
			prop.load(new FileInputStream("config.properties"));
		} catch (IOException e) {
			Report.reportConsoleLogs(e.getMessage(), Level.SEVERE);
		}    
        this.name = name;
        this.result = result;
        this.externalId = prop.getProperty("Testlink.testCase." + this.name);
        this.notes = notes;
        this.screenShootsPaths = screenShootsPaths;
    }
    
    public TestCase(String name, ArrayList<String> result) {
    	this(name, result, null, null);
    }
}