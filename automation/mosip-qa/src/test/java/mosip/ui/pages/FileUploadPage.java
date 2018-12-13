package mosip.ui.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import mosip.ui.base.uiTestBase;

public class FileUploadPage extends uiTestBase {

	//Locate the WEbElements on the FileUpload Page
	
	// Initializing the Page Objects:
	public FileUploadPage() {
		PageFactory.initElements(driver, this);
	}
	
	public String verifyHomePageTitle(){
		return driver.getTitle();
	}
	
	
	public boolean verifyapplication(){
		//Check Presence of Element to Page
		return true;
	}
	
	public void uploadFile() throws InterruptedException{
		//code to upload file
	}
	
	
	
	
	
	
	
	
	
	
	
	
	

}
