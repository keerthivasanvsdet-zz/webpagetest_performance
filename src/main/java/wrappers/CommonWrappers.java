package wrappers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.*;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

/**
 *
 * @author keerthivasan
 */

public class CommonWrappers extends CommonApiWrappers {

	public static WebDriver driver;
	public static WebDriverWait wait;
	
	public static WebDriver launchBrowser(String browsername) {
		getData();
		if (browsername == "CH") {
			System.setProperty("webdriver.chrome.driver", "drivers//chromedriver");
			DesiredCapabilities caps = DesiredCapabilities.chrome();
	        LoggingPreferences logPrefs = new LoggingPreferences();
	        logPrefs.enable(LogType.BROWSER, Level.ALL);
	        caps.setCapability(CapabilityType.LOGGING_PREFS, logPrefs);
			driver = new ChromeDriver();
			driver.manage().window().setSize(new Dimension(1280, 1024));
			driver.manage().window().maximize();
			webDriverWait(240);
		} else if (browsername == "FF") {
			System.setProperty("webdriver.gecko.driver", "drivers//geckodriver.exe");
			driver = new FirefoxDriver();
			driver.manage().window().setSize(new Dimension(1280, 1024));
			driver.manage().window().maximize();
		} else if (browsername == "IE") {
			System.setProperty("webdriver.ie.driver", "drivers//IEDriverServer");
			driver = new InternetExplorerDriver();
			driver.manage().window().setSize(new Dimension(1280, 1024));
			driver.manage().window().maximize();
		} else if (browsername == "HTMLUNIT") {
			driver = new HtmlUnitDriver();
			java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(Level.OFF); 
	        java.util.logging.Logger.getLogger("org.apache.commons.httpclient").setLevel(Level.OFF);			
		}
		driver.manage().timeouts().pageLoadTimeout(4, TimeUnit.MINUTES);
		driver.manage().timeouts().implicitlyWait(3, TimeUnit.MINUTES);
		return driver;
	}
	
	public WebDriver getDriver() {
        return driver;
    }
	
	public static WebDriver webDriverWait(int seconds){
		wait = new WebDriverWait(driver, seconds);
		return driver;
	}

	public static WebDriver openBrowser(String BrowserName, String url) {
		launchBrowser(BrowserName);
		launchUrl(url);
		return driver;
	}

	public static WebDriver navigateToUrl(String url) {
		driver.navigate().to(url);
		waitForPageLoad(driver);
		return driver;
	}

	public static void launchUrl(String url) {
		driver.manage().window().setSize(new Dimension(1280, 1024));
		driver.manage().window().maximize();
		driver.get(url);
	}

	public static WebDriver launchWithDiemensions(String browser, String url, int length, int width) {
		launchBrowser(browser);
		driver.manage().window().setSize(new Dimension(length, width));
		driver.get(url);
		return driver;
	}

	public static String getTimeStamp() {
		DateFormat DF = DateFormat.getDateTimeInstance();
		Date dte = new Date();
		String DateValue = DF.format(dte);
		DateValue = DateValue.replaceAll(":", "_");
		DateValue = DateValue.replaceAll(",", "");
		return DateValue;
	}

	public static String takeScreenshot(WebDriver driver, String DestFilePath) throws IOException {
		String TS = getTimeStamp();
		TakesScreenshot tss = (TakesScreenshot) driver;
		File srcfileObj = tss.getScreenshotAs(OutputType.FILE);
		DestFilePath = DestFilePath + TS + ".png";
		File DestFileObj = new File(DestFilePath);
		FileUtils.copyFile(srcfileObj, DestFileObj);
		return DestFilePath;
	}

	public static void tearDown(String cookieName) throws IOException {
		System.out.println(driver.manage().getCookieNamed(cookieName));
		analyzeLog();
		driver.quit();
	}
	
	public static void analyzeLog() {
		LogEntries logEntries = driver.manage().logs().get(LogType.BROWSER);
        for (LogEntry entry : logEntries) {
            System.out.println(new Date(entry.getTimestamp()) + " " + entry.getLevel() + " " + entry.getMessage());
            //do something useful with the data
        }
    }

	public static void takeSnapshot(String directory) throws IOException {
		takeScreenshot(driver, directory);
	}

	protected static void waitForElement(String element) throws InterruptedException {
		for (int second = 0;; second++) {
			if (second <= 20) {
				try {
					if (isElementPresent(element))
						break;
				} catch (Exception e) {
				}
				Thread.sleep(1000);
			} else {
				break;
			}
		}
	}

	protected void waitForElement(String element, int seconds) throws InterruptedException {
		for (int second = 0; second < seconds; second++) {
			if (second < seconds) {
				try {
					if (isElementPresent(element))
						break;
				} catch (Exception e) {
				}
				Thread.sleep(1000);
			} else {
				break;
			}
		}
	}

	protected boolean isElementPresent(String csslocator, String message) {
		try {
			@SuppressWarnings("unused")
			WebElement element = driver.findElement(By.cssSelector(csslocator));
			return true;
		} catch (NoSuchElementException e) {
			Assert.fail(message);
			return false;
		}
	}

	protected static boolean isElementPresent(String csslocator) {

		try {
			By driver = null;
			@SuppressWarnings({ "unused", "null" })
			WebElement element = (WebElement) driver.findElements((SearchContext) By.cssSelector(csslocator));
			return true;
		} catch (NoSuchElementException e) {
			return false;
		}
	}

	protected boolean isElementPresent(WebElement element) {

		try {
			By driver = null;
			String xpath = null;
			@SuppressWarnings({ "unused", "null" })
			WebElement elements = (WebElement) driver.findElements((SearchContext) By.xpath(xpath));
			return true;
		} catch (NoSuchElementException e) {
			String message = null;
			Assert.fail(message);
			return false;
		}
	}

	// Wait For PageLoad Until "document.readyState"
	protected static void waitForPageLoad(WebDriver driver) {

		ExpectedCondition<Boolean> expectation = new ExpectedCondition<Boolean>() {

			public Boolean apply(WebDriver driver) {
				return ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
			}
		};

		Wait<WebDriver> wait = new WebDriverWait(driver, 50);
		try {
			wait.until(expectation);
		} catch (Throwable error) {
			Assert.assertFalse(true, "Timeout Waiting for Page Load Request to Complete.");
		}
	}

	// Wait for WebElement to Appear for 20 Seconds
	protected void waitForWebElement(WebElement element) throws InterruptedException {
		for (int second = 0;; second++) {
			if (second <= 20) {
				try {
					if (isElementPresent(element))
						break;
				} catch (Exception e) {
				}
				Thread.sleep(1000);
			} else {
				break;
			}
		}
	}

	// Select the dropdown using "select by visible text", so pass VisibleText
	// as 'Yellow' to function
	public static void selectByText(WebElement WE, String VisibleText) {
		Select selObj = new Select(WE);
		selObj.selectByVisibleText(VisibleText);
	}

	// Select the dropdown using "select by index", so pass IndexValue as '2' to
	// function
	public static void selectByIndex(WebElement WE, int IndexValue) {
		Select selObj = new Select(WE);
		selObj.selectByIndex(IndexValue);
	}

	// Select the dropdown using "select by value", so pass Value as "City" to
	// function
	public static void selectByValue(WebElement WE, String Value) {
		Select selObj = new Select(WE);
		selObj.selectByValue(Value);
	}

	// Wait for Pop/Overlays
	// Refer => http://www.seleniumeasy.com/selenium-tutorials/element-is-not-clickable-at-point-selenium-webdriver-exception
	public WebDriver waitForPopUp(String locator) throws InterruptedException {

		WebDriverWait wait = new WebDriverWait(driver, 30);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(locator)));
		waitForPageLoad(driver);
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath(locator)));
		//((JavascriptExecutor)driver).executeScript("window.scrollTo(0,"+driver.findElement(By.xpath(locator)).getLocation().y+")");
		Actions action = new Actions(driver);
		action.moveToElement(driver.findElement(By.xpath(locator))).click().perform();
		driver.findElement(By.xpath(locator)).click();
		Thread.sleep(2000L);
		return driver;
	}

	// Read Data From Properties File
	public static void getData() {

		String propertiesfilepath = new File("config//config.properties").getAbsolutePath();
		File file = new File(propertiesfilepath);
		FileInputStream fileInput = null;
		try {
			fileInput = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		Properties prop = new Properties();

		// Load Properties file
		try {
			prop.load(fileInput);
		} catch (IOException e) {
			e.printStackTrace();
		}

		Enumeration<Object> KeyValues = prop.keys();
		while (KeyValues.hasMoreElements()) {
			String key = (String) KeyValues.nextElement();
			String value = prop.getProperty(key);
			System.setProperty(key, value);
			// System.out.println(key + ": " + value);
		}
	}

	// To Read the URL
	protected static String readUrl(String urlString) throws Exception {
		BufferedReader reader = null;
		try {
			URL url = new URL(urlString);
			reader = new BufferedReader(new InputStreamReader(url.openStream()));
			StringBuffer buffer = new StringBuffer();
			int read;
			char[] chars = new char[1024];
			while ((read = reader.read(chars)) != -1)
				buffer.append(chars, 0, read);

			return buffer.toString();
		} finally {
			if (reader != null)
				reader.close();
		}
	}

	// Select Required Checkbox
	public void selectRequiredFilter(String filterXPath, String labelText) throws InterruptedException {

		List<WebElement> CheckBoxList = driver.findElements(By.xpath(filterXPath));
		Iterator<WebElement> itr = CheckBoxList.iterator();
		while (itr.hasNext()) {
			WebElement text = itr.next();
			System.out.println("Filter Name: " + text.getText());
			waitForPageLoad(driver);
			Thread.sleep(5000L);
			if (text.getText().equalsIgnoreCase(labelText)) {
				System.out.println("Location: " + text.getLocation());
				System.out.println("Displayed in Front-end: " + text.isDisplayed());
				Actions actions = new Actions(driver);
				actions.moveToElement(text).click().perform();
				Thread.sleep(5000L);
				waitForPageLoad(driver);
				break;
			}
		}
	}
	
	public static WebDriver sendKeysById(String id,String data) {
		waitForPageLoad(driver);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(id)));
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id(id)));
		WebElement element = driver.findElement(By.id(id));
		element.clear();
		element.click();
		element.sendKeys(data);
		return driver;
	}
	
	public static WebDriver clickByXpath(String xpath) throws InterruptedException {
		waitForPageLoad(driver);
		waitForElement(xpath);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
		wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
		WebElement element = driver.findElement(By.xpath(xpath));
		element.click();
		waitForPageLoad(driver);
		return driver;
	}
	
	public static WebDriver clickById(String id) {
		waitForPageLoad(driver);		
		WebElement element = driver.findElement(By.id(id));
		element.click();
		return driver;
	}
	
	public static WebDriver clickByCssselector(String cssselector) throws InterruptedException {
		waitForPageLoad(driver);
		waitForElement(cssselector);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(cssselector)));
		wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(cssselector)));
		WebElement element = driver.findElement(By.cssSelector(cssselector));
		waitForPageLoad(driver);
		element.click();
		waitForPageLoad(driver);
		return driver;
	}
	
	public static WebDriver scroll(int from,int to) {
		waitForPageLoad(driver);
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		jse.executeScript("window.scrollBy("+from+","+to+")", "");
		return driver;
	}
	
	public static WebDriver getTextByCssselector(String cssselector,String text){
		waitForPageLoad(driver);
		WebElement element = driver.findElement(By.cssSelector(cssselector));
		System.out.println(text+element.getText());		
		return driver;
	}
	
	public static String getCurrenUrl(String message){
		String url=driver.getCurrentUrl();
		System.out.println(message+ ": "+url);
		return url;		
	}
	
	public static String getCurrenUrl(){
		String url=driver.getCurrentUrl();
		System.out.println(url);
		return url;		
	}
	
	public static String getTextById(String id,String text){
		waitForPageLoad(driver);
		WebElement element = driver.findElement(By.id(id));
		System.out.println(text+element.getText());
		return element.getText();
	}
	
	public void writeStringToTextFile(String filename,String data){
		try {
			File file = new File(filename);
			FileWriter fileWriter = new FileWriter(file);
			fileWriter.write(data);
			fileWriter.flush();
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static int getCurrentTime(){
		Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH");
        System.out.println(sdf.format(cal.getTime()));
        String time=sdf.format(cal.getTime()).replaceAll("0", "");
        int tm = Integer.parseInt(time);
        System.out.println(tm);
        return tm;
	}
	
	public static int getCurrentDay(){
		Calendar calendar = Calendar.getInstance();
    	int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
    	System.out.println(dayOfWeek);
    	return dayOfWeek;
	}
	
	public boolean elementIsEnabled(String element){
		try{
			driver.findElement(By.id(element)).isEnabled();
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}		
	}
	
	public static String getTextByXpath(String xpath){
		waitForPageLoad(driver);
		WebElement element = driver.findElement(By.xpath(xpath));
		System.out.println(element.getText());
		return element.getText();
	}

}