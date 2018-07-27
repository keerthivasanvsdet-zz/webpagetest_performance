package selenium;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;;

public class LaunchFirefox {
	
	public static void main(String[] args) {	
		
		System.setProperty("webdriver.gecko.driver","drivers//geckodriver.exe");
		WebDriver driver = new FirefoxDriver();
		driver.manage().window().maximize();
		driver.get("https://www.google.co.in/");
		try {
			Thread.sleep(2000L);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		driver.close();
	}

}
