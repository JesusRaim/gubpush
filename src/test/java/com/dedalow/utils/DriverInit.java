package com.dedalow.utils;

import com.dedalow.SharedDependencies;
import com.dedalow.report.Report;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.logging.Level;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import java.time.Duration;
import java.net.URL;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.AbstractDriverOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import io.github.bonigarcia.wdm.WebDriverManager;

public class DriverInit {
    public String driverType;
    public String driverPath;
    public String[] driverOptions;
    public String pathFolderDownloads;
    public int timeOut;
    public List<String> listNamesChrome = Arrays.asList("chrome", "googlechrome", "remotechrome");
    public List<String> listNamesFirefox = Arrays.asList("firefox", "mozilla", "mozillafirefox", "gecko", "remotefirefox");
    public List<String> listNamesExplorer = Arrays.asList("ie", "internetexplorer", "explorer", "iexplorer");
    public List<String> listNameseEdge = Arrays.asList("edge", "msedge", "remoteedge");

    
	/**
	 * The corresponding function is called depending on the selected browser
	 * @param nameDriver
	 * @return
	 * @throws Exception
	 */
    public WebDriver driverSelector(String nameDriver) throws Exception {
        WebDriver driver;
        if (SharedDependencies.contextsDriver.get(nameDriver) != null) {
            driver = SharedDependencies.contextsDriver.get(nameDriver);
        } else {
            driverType = SharedDependencies.prop.getProperty("WebDriver.BROWSER").toLowerCase().replace(" ", "");
            driverOptions = SharedDependencies.prop.getProperty("WebDriver.DRIVER_OPTIONS").split(", ");
            pathFolderDownloads = SharedDependencies.prop.getProperty("FOLDER_DOWNLOAD");

            if (!pathFolderDownloads.isEmpty() && !pathFolderDownloads.equals("default")) {
                SharedDependencies.folderDownloads = new File(pathFolderDownloads);
            }

            if (listNamesChrome.contains(driverType)) {
                driver = initChromedriver();
            } else if (listNamesFirefox.contains(driverType)) {
                driver = initGeckodriver();
            } else if (listNamesExplorer.contains(driverType)) {
                driver = initIEDriverServer();
            } else if (listNameseEdge.contains(driverType)) {
                driver = initEdgedriver();
            } else {
                SharedDependencies.logger.info(
                        "The indicated browser does not match the available browsers [Chrome, Firefox, IExplorer, Edge], it is launched by default on chrome");
                driver = initChromedriver();
            }

            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(SharedDependencies.timeout));
            SharedDependencies.contextsDriver.put(nameDriver, driver);
        }
        return driver;
    }

	/**
     * Configure and start the Chrome browser
     * @return
     * @throws Exception
     */
    public WebDriver initChromedriver() throws Exception {
        try {
			HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
			chromePrefs.put("profile.default_content_settings.popups", 0);
			chromePrefs.put("download.default_directory", SharedDependencies.folderDownloads.getAbsolutePath());
			ChromeOptions optionsChrome = new ChromeOptions();
			if (!SharedDependencies.prop.getProperty("WebDriver.DRIVER_OPTIONS").isEmpty()) {
				optionsChrome.addArguments(driverOptions);
			}
			optionsChrome.setExperimentalOption("prefs", chromePrefs);
			if (SharedDependencies.prop.getProperty("WebDriver.BROWSER").toLowerCase().contains("remote")) {
				return getRemoteWebDriver(optionsChrome);
			} else {
				WebDriverManager.chromedriver().driverVersion(SharedDependencies.prop.getProperty("WebDriver.DRIVER_VERSION")).setup();
				return new ChromeDriver(optionsChrome);
			}
		} catch (IllegalStateException e) {
			throw new Exception(e.getMessage());
		}
    }

	/**
     * Configure and start the Fixefox browser
     * @return
     * @throws Exception
     */
    public WebDriver initGeckodriver() throws Exception {
        try {
			FirefoxProfile profile = new FirefoxProfile();
			profile.setPreference("browser.download.manager.useWindow", false);
			profile.setPreference("browser.download.dir", SharedDependencies.folderDownloads.getAbsolutePath());
			profile.setPreference("browser.download.manager.showAlertOnComplete", true);
			profile.setPreference("browser.helperApps.neverAsk.saveToDisk",
					"text/plain, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/octet-stream,"
							+ "application/binary, text/csv, application/csv, application/excel,"
							+ "text/comma-separated-values, text/xml, application/xml");
			profile.setPreference("browser.download.folderList", 2);
			FirefoxOptions optionsFirefox = new FirefoxOptions();
			if (!SharedDependencies.prop.getProperty("WebDriver.DRIVER_OPTIONS").isEmpty()) {
				optionsFirefox.addArguments(driverOptions);
			}
			optionsFirefox.setProfile(profile);
			if (SharedDependencies.prop.getProperty("WebDriver.BROWSER").toLowerCase().contains("remote")) {
				return getRemoteWebDriver(optionsFirefox);
			} else {
				WebDriverManager.firefoxdriver().driverVersion(SharedDependencies.prop.getProperty("WebDriver.DRIVER_VERSION")).setup();
				return new FirefoxDriver(optionsFirefox);
			}
		} catch (IllegalStateException e) {
			throw new Exception(e.getMessage());
		}
    }

	/**
     * Configure and start the Explorer browser
     * @return
     * @throws Exception
     */
    public WebDriver initIEDriverServer() throws Exception {
        try {
			WebDriverManager.iedriver().driverVersion(SharedDependencies.prop.getProperty("WebDriver.DRIVER_VERSION")).arch32().setup();
			WebDriver ieDriver = new InternetExplorerDriver();
			return ieDriver;
		} catch (IllegalStateException e) {
			throw new Exception(e.getMessage());
		}
    }

	/**
     * Configure and start the Edge browser
     * @return
     * @throws Exception
     */
    public WebDriver initEdgedriver() throws Exception {
        try {
			HashMap<String, Object> edgePrefs = new HashMap<String, Object>();
			edgePrefs.put("profile.default_content_settings.popups", 0);
			edgePrefs.put("download.default_directory", SharedDependencies.folderDownloads.getAbsolutePath());
			EdgeOptions optionsEdge = new EdgeOptions();
			if (!SharedDependencies.prop.getProperty("WebDriver.DRIVER_OPTIONS").isEmpty()) {
				optionsEdge.addArguments(driverOptions);
			}
			optionsEdge.setExperimentalOption("prefs", edgePrefs);
			if (SharedDependencies.prop.getProperty("WebDriver.BROWSER").toLowerCase().contains("remote")) {
				return getRemoteWebDriver(optionsEdge);
			} else {
				WebDriverManager.edgedriver().driverVersion(SharedDependencies.prop.getProperty("WebDriver.DRIVER_VERSION")).setup();
				return new EdgeDriver(optionsEdge);
			}
		} catch (IllegalStateException e) {
			throw new Exception(e.getMessage());
		}
	}

	/**
     * Configures and returns a RemoteWebDriver.
     * @param <T> type that inherits from AbstractDriverOptions.
     * @param options an object that inherits from AbstractDriverOptions. Examples: ChromeOptions, EdgeOptions or FirefoxOptions.
     * @return a configured RemoteWebDriver object.
     * @throws Exception
     */
	private <T extends AbstractDriverOptions<T>> RemoteWebDriver getRemoteWebDriver(T options) throws Exception{
    	StringBuilder errorMessages = new StringBuilder();
    	String remoteUrl = SharedDependencies.prop.getProperty("WebDriver.REMOTE_URL");
    	String remotePlatform = SharedDependencies.prop.getProperty("WebDriver.REMOTE_PLATFORM");
    	String browserVersion = SharedDependencies.prop.getProperty("WebDriver.BROWSER_VERSION");

    	if (remoteUrl == null)
    		errorMessages.append("The REMOTE_URL is not in the config.properties file.\n");
    	else
    		if (remoteUrl.isEmpty())
    			errorMessages.append("The REMOTE_URL field of the config.properties file has no value.\n");

    	if (remotePlatform == null)
    		errorMessages.append("The REMOTE_PLATFORM is not in the config.properties file.\n");

    	if (browserVersion == null)
    		errorMessages.append("The BROWSER_VERSION is not in the config.properties file.\n");

    	if (errorMessages.length() > 0) throw new Exception(errorMessages.toString());

    	options.setPlatformName(SharedDependencies.prop.getProperty("WebDriver.REMOTE_PLATFORM"));
		options.setBrowserVersion(SharedDependencies.prop.getProperty("WebDriver.BROWSER_VERSION"));

		return new RemoteWebDriver(new URL(remoteUrl), options);
    }

    public static void clearWebDrivers() {
      try {
          for (Map.Entry<String, WebDriver> context : SharedDependencies.contextsDriver.entrySet()) {
              if (!context.getValue().toString().contains("Firefox")) {
                  context.getValue().close();
              }
              context.getValue().quit();
          }
          SharedDependencies.contextsDriver.clear();
      } catch (Exception e) {
          Report.reportConsoleLogs(e.getMessage(), Level.SEVERE);
      }
    }

}