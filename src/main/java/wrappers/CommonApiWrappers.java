package wrappers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Map.Entry;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import utilities.Reporter;

public class CommonApiWrappers {
	
	public static String envi;
	
	public void config() {
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
		}
	}

	// HTTP POST Request
	public String sendPostRequest(String postUrl, String params) throws Exception {
		StringBuffer result = null;
		
		try{
		String url = postUrl;

		HttpClient client = HttpClientBuilder.create().build();
		HttpPost post = new HttpPost(url);

		// Add Header
		post.setHeader("User-Agent", "Mozilla/5.0");

		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();

		String key;
		String value;
		HashMap<String, String> map = new HashMap<String, String>();
		map = splitQuery(params);
		for (Entry<String, String> e : map.entrySet()) {
			key = e.getKey();
			value = e.getValue();
			urlParameters.add(new BasicNameValuePair(key, value));
			System.out.println(urlParameters);
		}

		System.out.println(urlParameters);
		post.setEntity(new UrlEncodedFormEntity(urlParameters));

		// Start Time Measurement
		long startTime = System.currentTimeMillis();

		HttpResponse response = client.execute(post);
		System.out.println("\nSending 'POST' Request to URL : " + url);
		Reporter.reportStep("\nSending 'POST' Request to URL : " + url, "PASS");
		int responseCode = response.getStatusLine().getStatusCode();
		System.out.println("Response Code : " + responseCode);
		System.out.println("Post Parameters : " + urlParameters);
		Reporter.reportStep("Post Parameters : " + urlParameters, "PASS");

		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

		// Stop Time Measurement
		float elapsedTime = System.currentTimeMillis() - startTime;
		Reporter.reportStep("Total Elapsed HTTP POST Request/Response Time In Seconds: " + elapsedTime / 1000, "PASS");
		System.out.println("Total Elapsed HTTP POST Request/Response Time In Seconds: " + elapsedTime / 1000);

		result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}

		System.out.println("API Response is: " + result.toString());

		if (responseCode == 200 || responseCode == 201) {
			Reporter.reportStep("Response Code is: " + responseCode, "PASS");
			Reporter.reportStep("API Response: " + result.toString(), "PASS");
		} else if(responseCode==422){
			Reporter.reportStep("Response Code is: "+responseCode,"WARNING");
			Reporter.reportStep("API Response: "+result.toString(), "WARNING");
		} else {
			Reporter.reportStep("API Response: "+result.toString(), "FAIL");
		}
		
		}catch(Exception e){
			e.printStackTrace();
			Reporter.reportStep("Exception Occured: "+e.getLocalizedMessage(), "FAIL");
		}

		return result.toString();
	}

	// HTTP GET request
	@SuppressWarnings("unused")
	public String sendGetRequest(String getUrl) throws Exception {
		String finalResult = null;
		StringBuffer result = null;
		
		try{
		String url = getUrl;

		HttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet(url);
		
		// Start Time Measurement
		long startTime = System.currentTimeMillis();
		
		//Send GET Request
		HttpResponse response = client.execute(request);

		System.out.println("\nSending 'GET' Request to URL : " + url);
		Reporter.reportStep("\nSending 'GET' Request to URL : " + url, "PASS");
		int responseCode=response.getStatusLine().getStatusCode();
		System.out.println("Response Code : " + responseCode);

		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

		// Stop Time Measurement
		float elapsedTime = System.currentTimeMillis() - startTime;
		Reporter.reportStep("Total Elapsed HTTP GET Request/Response Time In Seconds: " + elapsedTime / 1000, "PASS");
		System.out.println("Total Elapsed HTTP GET Request/Response Time In Seconds: " + elapsedTime / 1000);

		result = new StringBuffer();
		String line = "";

		while ((line = rd.readLine()) != null) {
			result.append(line);
		}

		System.out.println(result.toString());
		
		if (responseCode == 200 || responseCode == 201) {
			Reporter.reportStep("Response Code is: " + responseCode, "PASS");
			Reporter.reportStep("API Response: " + result.toString(), "PASS");
		} else if(responseCode==422 || responseCode == 400 || responseCode == 404){
			Reporter.reportStep("Response Code is: "+responseCode,"WARNING");
			Reporter.reportStep("API Response: "+result.toString(), "WARNING");
		} else {
			Reporter.reportStep("Response Code is: " + responseCode+" API Response: "+result.toString(), "FAIL");
		}
		
		}catch(Exception e){
			e.printStackTrace();
			Reporter.reportStep("Exception Occured: "+e.getLocalizedMessage(), "FAIL");
		}
		
		if(finalResult==null){
			return result.toString();
		}else{
			return finalResult;
		}
		
	}
	
	// HTTP DELETE request
	@SuppressWarnings("unused")
	public String sendDeleteRequest(String deleteUrl) throws Exception {
		String finalResult = null;
		StringBuffer result = null;
		
		try{
		String url = deleteUrl;

		HttpClient client = HttpClientBuilder.create().build();
		HttpDelete delete = new HttpDelete(url);
		
		// Add Header
		delete.setHeader("User-Agent", "Mozilla/5.0");
		
		// Start Time Measurement
		long startTime = System.currentTimeMillis();
		
		//Send GET Request
		HttpResponse response = client.execute(delete);
		System.out.println("\nSending 'DELETE' Request to URL : " + url);
		Reporter.reportStep("\nSending 'DELETE' Request to URL : " + url, "PASS");
		int responseCode=response.getStatusLine().getStatusCode();
		System.out.println("Response Code : " + responseCode);

		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

		// Stop Time Measurement
		float elapsedTime = System.currentTimeMillis() - startTime;
		Reporter.reportStep("Total Elapsed HTTP DELETE Request/Response Time In Seconds: " + elapsedTime / 1000, "PASS");
		System.out.println("Total Elapsed HTTP DELETE Request/Response Time In Seconds: " + elapsedTime / 1000);

		result = new StringBuffer();
		String line = "";

		while ((line = rd.readLine()) != null) {
			result.append(line);
		}

		System.out.println(result.toString());
		
		if(responseCode==200||responseCode==201){
			Reporter.reportStep("Response Code is: "+responseCode,"PASS");
			Reporter.reportStep("API Response: "+result.toString(), "PASS");
		}else{
			Reporter.reportStep("API Response: "+result.toString(), "FAIL");
		}
		
		}catch(Exception e){
			e.printStackTrace();
			Reporter.reportStep("Exception Occured: "+e.getLocalizedMessage(), "FAIL");
		}
		
		if(finalResult==null){
			return result.toString();
		}else{
			return finalResult;
		}
	}
	
	// HTTP PUT request
	public String sendPutRequest(String putUrl,String params) throws Exception {
		StringBuffer result = null;
		
		try{
		String url = putUrl;

		HttpClient client = HttpClientBuilder.create().build();
		HttpPut put = new HttpPut(url);
		
		// Add Header
		put.setHeader("User-Agent", "Mozilla/5.0");

		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		
		String key;
		String value;
		HashMap<String, String> map = new HashMap<String, String>();
		map = splitQuery(params);
		for (Entry<String, String> e : map.entrySet()) {
			key = e.getKey();
			value = e.getValue();
			urlParameters.add(new BasicNameValuePair(key, value));
			//System.out.println(urlParameters);
		}
		
		System.out.println(urlParameters);
		put.setEntity(new UrlEncodedFormEntity(urlParameters));

		// Start Time Measurement
		long startTime = System.currentTimeMillis();

		// Send GET Request
		HttpResponse response = client.execute(put);

		System.out.println("\nSending 'PUT' Request to URL : " + url);
		System.out.println("\nSending 'PUT' Request to URL : " + url);
		Reporter.reportStep("\nSending 'PUT' Request to URL : " + url, "PASS");
		int responseCode = response.getStatusLine().getStatusCode();
		System.out.println("Response Code : " + responseCode);
		System.out.println("Parameters : " + urlParameters);
		Reporter.reportStep("Parameters : " + urlParameters, "PASS");

		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

		// Stop Time Measurement
		float elapsedTime = System.currentTimeMillis() - startTime;
		Reporter.reportStep("Total Elapsed HTTP PUT Request/Response Time In Seconds: " + elapsedTime / 1000, "PASS");
		System.out.println("Total Elapsed HTTP PUT Request/Response Time In Seconds: " + elapsedTime / 1000);

		result = new StringBuffer();
		String line = "";

		while ((line = rd.readLine()) != null) {
			result.append(line);
		}

		System.out.println("API Response is: " + result.toString());

		if (responseCode == 200 || responseCode == 201) {
			Reporter.reportStep("Response Code is: " + responseCode, "PASS");
			Reporter.reportStep("API Response: " + result.toString(), "PASS");
		} else {
			Reporter.reportStep("API Response: " + result.toString(), "FAIL");
		}
		
		}catch(Exception e){
			e.printStackTrace();
			Reporter.reportStep("Exception Occured: "+e.getLocalizedMessage(), "FAIL");
		}

		return result.toString();
	}

	public JSONObject returnJson(String jsonResponse) {
		JSONObject json = null;
		try {
			json = new JSONObject(jsonResponse);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e.getLocalizedMessage());
		}
		return json;
	}

	public boolean validateJson(JSONObject apiResponse) {
		boolean bResult=false;
		try {
			JsonParser parser = new JsonParser();
			parser.parse(apiResponse.toString());
			bResult=true;
		} catch (JsonSyntaxException jse) {
			System.out.println("In-valid JSON String: " + jse.getMessage());
			Reporter.reportStep("In-valid JSON String is being returned. " + "API Response: " + apiResponse, "FAIL");
			throw new RuntimeException(jse.getLocalizedMessage());
		}
		return bResult;
	}

	/**
	 * This method will provide the current Date
	 * 
	 * @return String
	 */
	public String getCurrentDate() {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
		return sdf.format(date);
	}

	/**
	 * This method will provide the current Time
	 * 
	 * @return String
	 */
	public String getCurrentTimeInHoursAndMinutes() {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("HH.mm");
		return sdf.format(date);
	}
	
	/**
	 * This method will create random mail Id
	 * 
	 * @return String
	 */
	public String createRandomMailId(){
		String MailId = "";
		try {
			String randomMailId=RandomStringUtils.randomAlphanumeric(5).toLowerCase();
			MailId="test_"+randomMailId+"@"+randomMailId+".com";
			System.out.println("Random Mail ID Used: "+MailId);
		} catch(Exception e){
			e.printStackTrace();
		}
		return MailId;
	}
	
	/**
	 * This method will create random alphanumeric String
	 * 
	 * @return String
	 */
	public String createRandomAlphanumericString(){
		String randomAlphanumericString = "";
		try {
			randomAlphanumericString=RandomStringUtils.randomAlphanumeric(5).toLowerCase();
			System.out.println("Random Alphanumeric String: "+randomAlphanumericString);
		} catch(Exception e){
			e.printStackTrace();
		}
		return randomAlphanumericString;
	}
	
	/**
	 * This method will turn post params to key value pairs
	 * 
	 * @return HashMap
	 */	
	public static HashMap<String,String> splitQuery(String params) throws UnsupportedEncodingException {
		HashMap<String, String> map= new HashMap<String,String>();
		String query = params.replace("?", "").toString();
	    System.out.println(query);
	    String[] pairs = query.split("&");
	    for (String pair : pairs) {	    	
	        String[] parts=pair.split("=");     	          
	        map.put(parts[0],parts[1]);	        
	    }
	    return map;
	}
	
	public int getPageResponseCode(String url){
		int statusCode=0;
		try{
			URL Url = new URL(url);
			HttpURLConnection http = (HttpURLConnection)Url.openConnection();
			statusCode = http.getResponseCode();
		}
		catch(IOException e){
			e.printStackTrace();
		}
		return statusCode;
	}
	
	// HTTP POST Request - JSON Input
	public JSONObject sendPostRequestWithJSONInput(String postUrl, String input) throws Exception {
		
		StringBuffer result = null;
		
		HttpClient httpClient = HttpClientBuilder.create().build();
		HttpPost post=new HttpPost(postUrl);
		
		System.out.println("Data: "+input);
		StringEntity params =new StringEntity(input);
		post.addHeader("content-type", "application/json");
		post.setEntity(params);
		System.out.println("Request: "+post);
		System.out.println("Params: "+params);

		// Start Time Measurement
		long startTime = System.currentTimeMillis();

		HttpResponse response = httpClient.execute(post);
		System.out.println("Header Response: "+response);

		int responseCode = response.getStatusLine().getStatusCode();

		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

		// Stop Time Measurement
		float elapsedTime = System.currentTimeMillis() - startTime;
		System.out.println("Total Elapsed HTTP POST Request/Response Time In Seconds: " + elapsedTime / 1000);
		Reporter.reportStep("Total Elapsed HTTP POST Request/Response Time In Seconds: " + elapsedTime / 1000,"PASS");

		result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}

		System.out.println("API Response is: " + result.toString());
		System.out.println("Response Code is: " + responseCode);
	
		if (responseCode == 200 || responseCode == 201) {
			Reporter.reportStep("Response Code is: " + responseCode, "PASS");
			Reporter.reportStep("API Response: " + result.toString(), "PASS");
		} else {
			Reporter.reportStep("API Response: " + result.toString(), "FAIL");
		}

		/*String apiResponse=result.toString().replace("\"{", "{").replace("}\"", "}").replace("\\", "")
					   .replace("[", "").replace("]", "").replace("\"{", "{").replace("}\"", "}");
		System.out.println("FAR: "+apiResponse);*/
		JSONObject json1=new JSONObject(result.toString().trim());
		System.out.println("JSON Response: "+json1);
		
		return json1;
	}
	
	public int validateResponseCode(String url){
		
		int statusCode=0;
		try{
			URL Url = new URL(url);
			HttpURLConnection http = (HttpURLConnection)Url.openConnection();
			statusCode = http.getResponseCode();
			
			if (statusCode == 200 || statusCode == 201) {
				Reporter.reportStep("Response Code is: " + statusCode, "PASS");
				Reporter.reportStep("URL Response Code: " + statusCode + " URL: "+url, "PASS");
			} else {
				Reporter.reportStep("URL Response Code: " + statusCode + " URL: "+url, "FAIL");
			}
		}
		catch(IOException e){
			e.printStackTrace();
		}
		
		return statusCode;
	}
}