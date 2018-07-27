package utilities;

import java.io.File;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import wrappers.CommonApiWrappers;

public class Reporter {

	private static ExtentHtmlReporter htmlReporter;
	private static ExtentTest test;
	private static ExtentReports extent = new ExtentReports();

	public static void reportStep(String desc, String status) {
		
		// Write if it is successful or failure or information
		if (status.toUpperCase().equals("PASS")) {
			test.log(Status.PASS,MarkupHelper.createLabel(desc,ExtentColor.GREEN));
		} else if (status.toUpperCase().equals("FAIL")) {
			test.log(Status.FAIL,MarkupHelper.createLabel(desc,ExtentColor.RED));
			throw new RuntimeException("FAILED");
		} else if (status.toUpperCase().equals("WARNING")) {
			test.log(Status.WARNING,MarkupHelper.createLabel(desc,ExtentColor.YELLOW));
		}
		
	}

	public static void startResult() {
		
		new CommonApiWrappers().config();
		htmlReporter = new ExtentHtmlReporter("reports//performance-result-"+"-"+new CommonApiWrappers().getCurrentDate().replace(" ", "-")+".html");
		htmlReporter.loadXMLConfig(new File("extent-config.xml"));
		htmlReporter.setAppendExisting(true);
		htmlReporter.config().setChartVisibilityOnOpen(false);
		extent.attachReporter(htmlReporter);
		
		extent.setSystemInfo("From", "Keerthivasan");
		
	}

	public static void startTestCase(String testCaseName, String testDescription) {
		
		test = extent.createTest(testCaseName, testDescription);
	}

	public static void endResult() {
		
		extent.flush();
	}

}
