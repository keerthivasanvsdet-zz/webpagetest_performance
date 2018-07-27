package performance_test;

import java.util.Calendar;
import org.json.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import annotations.SuiteMethods;
import wrappers.CommonApiWrappers;

public class MeasurePageLoadTimeForDesktop extends SuiteMethods {
	
	@BeforeClass
	public void startTestCase() {
		testCaseName = "Performance Check - MeasurePageLoadTimeForDesktop";
		testDescription = "Performance Data for A Web Page.";

	}

	public static String summaryUrl, testUrl;
	String dcloadtimeinsec, ttfbinsec, flloadtimeinsec;
	public String jsonurl;

	@Test
	public void getPageLoadTimeForDesktop() throws Exception {
		
		try{
		Calendar calendar = Calendar.getInstance();
		java.util.Date now = calendar.getTime();
		java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(now.getTime());
		System.out.println(currentTimestamp);

		String[] urlArray = { 
							  "https://www.google.co.in/", 							  						  
		};

		// Location: ap-south-1, Browser: Chrome, Connectivity: 3G
		// Singapore Location: ec2-ap-southeast-1
		// User Agent: uastring=Mozilla/5.0 (Windows NT 6.1; WOW64; rv:40.0) Gecko/20100101 Firefox/40.1 Params: &keepua=1
		// Old_Api_Key=A.c25260b50026c0adf795b3f529ab3f11
		// Timeline Graph: Params: &timeline=1&timelineStack=1
		// Time is indicated in [Seconds] || Speed Of Cable, 3G and 3G Fast || Cable - 5 Mbps down, 1 Mbps up, 28ms first-hop RTT, 0% packet loss || 3G - 1.6 Mbps down, 768 Kbps up, 300 ms first-hop RTT, 0% packet loss || 3GFast - 1.6 Mbps down, 768 Kbps up, 150 ms first-hop RTT, 0% packet loss		

		for (int x = 0; x < urlArray.length; x++) {

			launchBrowser("HTMLUNIT");
			SSLUtilities.trustAllHostnames();
			SSLUtilities.trustAllHttpsCertificates();
			CommonApiWrappers caw=new CommonApiWrappers();
			String result=caw.sendGetRequest("http://www.webpagetest.org/runtest.php?"+"url="+urlArray[x].toString()+
					"&k="+System.getProperty("api_key")+"&location="+System.getProperty("location_browser_network")+
					"&f="+System.getProperty("format")+"&private=1&width="+System.getProperty("width")+
					"&height="+System.getProperty("height"));
			
			JSONObject json=new JSONObject(result);
			int statusCode = (Integer) json.get("statusCode");
			System.out.println(statusCode);
			
			if (statusCode == 200) {
				Thread.sleep(5000L);
				System.out.println("JsonUrl: " + json.getJSONObject("data").getString("jsonUrl"));
				System.out.println("UserUrl: " + json.getJSONObject("data").getString("userUrl"));
				jsonurl = json.getJSONObject("data").getString("jsonUrl").toString();
				System.out.println(jsonurl);
				driver.navigate().to(jsonurl);
			}

			int statusCodeOfjsonResp = 0;
			while (statusCodeOfjsonResp != 200) {
				driver.navigate().refresh();
				JSONObject jsonresp = new JSONObject(readUrl(jsonurl));
				statusCodeOfjsonResp = (Integer) jsonresp.get("statusCode");
				System.out.println(statusCodeOfjsonResp);
				if (statusCodeOfjsonResp == 200) {
					Thread.sleep(5000L);
					System.out.println("Summary Url: " + jsonresp.getJSONObject("data").getString("summary"));
					summaryUrl = jsonresp.getJSONObject("data").getString("summary");
					System.out.println("Test Url: " + jsonresp.getJSONObject("data").getString("testUrl"));
					testUrl = jsonresp.getJSONObject("data").getString("testUrl");
					System.out.println("Document Complete Load Time: " + jsonresp.getJSONObject("data")
							.getJSONObject("runs").getJSONObject("1").getJSONObject("firstView").getString("loadTime"));
					String dcloadtime = jsonresp.getJSONObject("data").getJSONObject("runs").getJSONObject("1")
							.getJSONObject("firstView").getString("loadTime");
					float dcloadtimeinsecs = Float.valueOf(dcloadtime) / 1000;
					dcloadtimeinsec = Float.toString(dcloadtimeinsecs);
					System.out.println("TTFB: " + jsonresp.getJSONObject("data").getJSONObject("runs")
							.getJSONObject("1").getJSONObject("firstView").getString("TTFB"));
					String ttfb = jsonresp.getJSONObject("data").getJSONObject("runs").getJSONObject("1")
							.getJSONObject("firstView").getString("TTFB");
					float ttfbinsecs = Float.valueOf(ttfb) / 1000;
					ttfbinsec = Float.toString(ttfbinsecs);
					System.out.println("Fully Loaded Time: " + jsonresp.getJSONObject("data").getJSONObject("runs")
							.getJSONObject("1").getJSONObject("firstView").getString("fullyLoaded"));
					String flloadtime = jsonresp.getJSONObject("data").getJSONObject("runs").getJSONObject("1")
							.getJSONObject("firstView").getString("fullyLoaded");
					float flloadtimeinsecs = Float.valueOf(flloadtime) / 1000;
					flloadtimeinsec = Float.toString(flloadtimeinsecs);
				} else {
					Thread.sleep(5000L);
				}
			}

			driver.quit();
			continue;
		}
		}catch(Exception e){
			System.out.println("Exception Occured: "+e.getMessage());
			e.printStackTrace();
		}
	}
  }