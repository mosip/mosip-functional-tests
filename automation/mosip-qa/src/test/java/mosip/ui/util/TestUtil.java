package mosip.ui.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import mosip.ui.base.uiTestBase;

public class TestUtil extends uiTestBase {

	public static long PAGE_LOAD_TIMEOUT = 20;
	public static long IMPLICIT_WAIT = 20;

	public static String TESTDATA_SHEET_PATH = "/Users//Documents/workspace"
			+ "/mosipqa/src/main/java/com/crm/qa/testdata/.json";

	
	static JavascriptExecutor js;

	public void switchToFrame() {
		driver.switchTo().frame("mainpanel");
	}

	public static void getTestData(String fileName) {
		FileInputStream file = null;
		try {
			file = new FileInputStream(TESTDATA_SHEET_PATH);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		//code to read json file
		System.out.println("try block");
		
	}

	public static void takeScreenshotAtEndOfTest() throws IOException {
		File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		String currentDir = System.getProperty("user.dir");
		FileUtils.copyFile(scrFile, new File(currentDir + "/screenshots/" + System.currentTimeMillis() + ".png"));
	}

	public static void runTimeInfo(String messageType, String message) throws InterruptedException {
		js = (JavascriptExecutor) driver;
		// Check for jQuery on the page, add it if need be
		js.executeScript("if (!window.jQuery) {"
				+ "var jquery = document.createElement('script'); jquery.type = 'text/javascript';"
				+ "jquery.src = 'https://ajax.googleapis.com/ajax/libs/jquery/2.0.2/jquery.min.js';"
				+ "document.getElementsByTagName('head')[0].appendChild(jquery);" + "}");
		Thread.sleep(5000);

		// Use jQuery to add jquery-growl to the page
		js.executeScript("$.getScript('https://the-internet.herokuapp.com/js/vendor/jquery.growl.js')");

		// Use jQuery to add jquery-growl styles to the page
		js.executeScript("$('head').append('<link rel=\"stylesheet\" "
				+ "href=\"https://the-internet.herokuapp.com/css/jquery.growl.css\" " + "type=\"text/css\" />');");
		Thread.sleep(5000);

		// jquery-growl w/ no frills
		js.executeScript("$.growl({ title: 'GET', message: '/' });");
//'"+color+"'"
		if (messageType.equals("error")) {
			js.executeScript("$.growl.error({ title: 'ERROR', message: '"+message+"' });");
		}else if(messageType.equals("info")){
			js.executeScript("$.growl.notice({ title: 'Notice', message: 'your notice message goes here' });");
		}else if(messageType.equals("warning")){
			js.executeScript("$.growl.warning({ title: 'Warning!', message: 'your warning message goes here' });");
		}else
			System.out.println("no error message");
		// jquery-growl w/ colorized output
//		js.executeScript("$.growl.error({ title: 'ERROR', message: 'your error message goes here' });");
//		js.executeScript("$.growl.notice({ title: 'Notice', message: 'your notice message goes here' });");
//		js.executeScript("$.growl.warning({ title: 'Warning!', message: 'your warning message goes here' });");
		Thread.sleep(5000);
	}

}
