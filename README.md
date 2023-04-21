# PROJECT: bugpush
    This project contains automated tests based on a Java Selenium JUNIT 5 and Maven architecture.
	In this project we can find features like:
	Logs generation, automated screenshot creation, testlink integration, and usage of Excel files as input datasets for Test Cases.

### Requirements
-	In order to run this project from an IDE, the JAVA Virtual Machine or a JAVA code interpreter should be installed.

-	In case of JAR execution only JAVA code interprester would be neccesary.

-	In case of using maven to run the execution, MAVEN should be installed.

### How configure tests
By default, *config.properties* file it's automatcally configured by Dedalow. If a modification of config would be neccessary, it can be done as follows:

**	Configuring Driver Options:**

Below, some of the available parameters when executing a test using Chrome (we can get the whole list using the command chromedriver –help) :

	- start-maximized: Opens Chrome in maximize mode
	- incognito: Opens Chrome in incognito mode
	- headless: Opens Chrome in headless mode
	- disable-extensions: Disables existing extensions on Chrome browser
	- disable-popup-blocking: Disables pop-ups displayed on Chrome browser
	- make-default-browser: Makes Chrome the default browser
	- version: Prints chrome browser version
	- disable-infobars: Prevents Chrome from displaying the notification 'Chrome is being controlled by automated software
	- window-size: To stablish desired window size

For using one of this parameters:

	WebDriver.DRIVEROPTIONS = --no-sandbox

For using multiple parameters, you should separate all parameters with comma and space:

	WebDriver.DRIVEROPTIONS = --no-sandbox, --window-size=1920,1080, --incognito

**Configuring Browser:**

We can choose between *Chrome, Firefox, Edge or Explorer* to run our Test Plan.

	Webdriver.DRIVER = Chrome

We can select the driver version that we want, writting the number version.

	Webdriver.DRIVER_VERSION = 95.0.4638.69

#####If you choose *Explorer* you have to do a few changes on Internet Explorer to run our Test Cases:

We have to maximize internet explorer's zoom:

	Settings->Zoom->100%

Then, we have to change Internet Explorer's security:

	Settings->Internet options->Security.
	Now we have to activate the check "Enable Protected Mode" in each zones (Internet, Local intranet, Trued Sites, Restricted Sites)

**Configuring Home page**

We can change the home page URL for your Test Plan

	WebDriver.HOME_PAGE = https://dedalow.com/

**Configuring download folder path:**

We can select any path, but if we don't change it, it will select it by default

	WebDriver.FOLDER_DOWNLOAD = default

**Configuring the screenshot**

We have 3 options for taking screenshots: Always, Only on error and Never. Any other value will generate an error and tests will not be executed. By default, Always will be selected

	Application example: SCREENSHOT = Always

**Excel:**

We can use an Excel file as data set, then the test will be executed as many times as different rows we have defined in the Excel.
Here we can change excel's path:

	EXCEL = C:/Users/dedalow/excel/example.xlsx

**Logger level:**

We can choose between *INFO* or *DEBUG* options, depending on detailed information we want to show

	LOG_LEVEL = INFO

**Timeout**

We can modify timeout value for all Test Plan

	WebDriver.TIMEOUT = 20

**Backend Home page**

We can change backend's home page URL for your api tests

	RestAssure.BACKEND_HOME_URL = https://dedalow.com/

**Backend Timeout**

We can modify backend's timeout value for all api tests

	RestAssure.BACKEND_TIMEOUT = 20

**Configurating Testlink:**

Here we have to set our Testlink URL

    Testlink.TESTLINK = https://steps.everis.com/testlink

Here we have to set our Testlink APIKEY

    Testlink.APIKEY = xxxxx

Here we have to set our Testlink projectName

    Testlink.PROJECT_NAME_TESTLINK = projectName

Here we have to set our Testlink buildName

    Testlink.BUILD_NAME = buildName

Here we have to set our Testlink patform

    Testlink.PLATFORM_NAME = chrome

We have one for each suite to set testlink suite name configuration

    Testlink.suite.nameSuite = nameSuite

We have one for each testCase to set testlink testCase name configuration

    Testlink.testCase.Test_caseName = caseName

### How execute using JAVA

**Config.properties configuration**

Selecting the only TestCase that we want to execute (caseName = name of the Test Case without spaces):

	TESTCASES = caseName

Selecting various TestCases that we want to execute (Separation using space or coma):

	TESTCASES = caseName1 caseName2

Selecting one Suite from the Test Plan (nameSuite = name of the Test Suite without spaces):

	TESTSUITES = nameSuite

Selecting various Suites from the Test Plan (Separation using space or coma)

	TESTSUITES = nameSuite1, nameSuite2

Selecting all Test Plan, set blank both properties

	TESTSUITES =
	TESTCASES =

**Run jar file reading config.properties file configuration, execute this command from the target folder**

	java -jar bugpushExe.jar

### How execute using MAVEN

**Run the entire project (All suites included with no parameters), execute this command from the root folder**

	mvn test

**Run using parametrized execution, execute these commands from the root folder**

Run by selecting the only TestCase that we want to execute (caseName = name of the Test Case without spaces)

	mvn -Dtest=Test_caseName test

Run by selecting various TestCases that we want to execute

	mvn -Dtest=Test_caseName1,Test_caseName2 test

Run only one Suite from the Test Plan (nameSuite = name of the Test Suite without spaces)

	mvn -Dtest=com.dedalow.nameSuite.Test* test

Run by selecting various Suites from the Test Plan

	mvn -Dtest=com.dedalow.nameSuite1.Test*,com.dedalow.nameSuite2.Test* test

### FAQ
Test is not executed because the driver is not updated and no message is displayed

	Update the driver and try again

Excel dataset is not correctly referenced

	Check the excel dataset URL in the config file.

I don’t know how to open the console in the unzipped folder

	Hold Shift + Right Click on the folder you want it opened on, and click on "open command window here". That should do the trick!
	OR
	You can also type cmd into the Windows File Explorer address bar (use Ctrl+L to focus the address bar) and then press Enter to open the shell.


## This project has follow TestSuites & TestCases available
**TestSuiteModel**
- TestCaseModel
