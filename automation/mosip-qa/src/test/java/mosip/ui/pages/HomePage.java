package mosip.ui.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import mosip.ui.base.uiTestBase;

public class HomePage extends uiTestBase {

	@FindBy(xpath= "//span[text()='Application']")
	//@CacheLookup
	WebElement application;
	
	@FindBy(xpath="//input[@type='number']")
	WebElement numberApplication;
	
	@FindBy(xpath = "//span[text()='Ok']")
	WebElement okApplicant;

	
	// Initializing the Page Objects:
	public HomePage() {
		PageFactory.initElements(driver, this);
	}
	
	public String verifyHomePageTitle(){
		return driver.getTitle();
	}
	
	
	public boolean verifyapplication(){
		return application.isDisplayed();
	}
	
	public DemographicPage clickOnapplication() throws InterruptedException{
		application.click();
		numberApplication.sendKeys("1");;
		Thread.sleep(3000);
		okApplicant.click();
		Thread.sleep(3000);
		
		return new DemographicPage();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	

}
