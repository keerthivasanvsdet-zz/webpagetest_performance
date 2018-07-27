package annotations;

import org.testng.ITestResult;
import org.testng.annotations.*;
import wrappers.CommonWrappers;
import utilities.DataInputProvider;
import utilities.Reporter;

public class SuiteMethods extends CommonWrappers {
	
	protected String excelName="";
	protected String dataSheetName="";
	protected static String testCaseName;
	protected static String testDescription;
	protected boolean configRep; 
	
	@BeforeSuite(groups={"inr","common"})
	public void beforeSuite(){
		Reporter.startResult();
	}


	@BeforeTest(groups={"common","inr"})
	public void beforeTest(){
		config();
	}

	@BeforeMethod(alwaysRun = true, groups = { "common", "inr" })
	public void beforeMethod() {
		Reporter.startTestCase(testCaseName, testDescription);
		//System.getProperties().list(System.out);
	}

	@AfterSuite(groups={"common","inr"})
	public void afterSuite(){

	}

	@AfterTest(groups={"common","inr"})
	public void afterTest(){
	}

	@AfterClass(groups={"common","inr"})
	public void afterClass(){
		
	}

	@AfterMethod(groups={"common","inr"})
	public void afterMethod(ITestResult rst){
		System.out.println("Method"+rst.getClass());
		Reporter.endResult();
	}

	@DataProvider(name="fetchData")
	public Object[][] getInputData(){
		return new DataInputProvider().getSheet(excelName,dataSheetName) ;		
	}


}
