package mosip.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import mosip.ui.base.uiTestBase;
import mosip.ui.base.uiTestBase;
import mosip.ui.pages.HomePage;

public class DemographicPage extends uiTestBase {

	@FindBy(xpath="//input[@placeholder='Full Name']")
	WebElement fullName;

	@FindBy(xpath = "//span[text()='Date Of Birth/Age']")
	//@FindBy(id="mat-select-0")
	WebElement DOB;
	

	@FindBy(xpath = "//span[text()='Age']")
	WebElement age;
	
	@FindBy(xpath="//input[@placeholder='Age']")
	WebElement ageInput;
	

	@FindBy(xpath = "//div[text()='Male']")
	WebElement maleOption;
	
	@FindBy(xpath = "//input[@placeholder='Address Line 1']")
	WebElement addressLine1;
	
	
	@FindBy(xpath="//span[text()='Region']")
	WebElement regionDropDown;
	
	@FindBy(xpath="//span[text()='Option']")
	WebElement regionOption;
	
	@FindBy(xpath="//span[text()='Province']")
	WebElement provinnce;
	
	@FindBy(id="mat-option-3")
	WebElement provinceOption;
	
	@FindBy(xpath="//span[text()='City']")
	WebElement city;
	
	@FindBy(id="mat-option-4")
	WebElement cityOption;
	
	@FindBy(xpath="//span[text()='Local Administrative Authority']")
	WebElement LocalAreaAdministrative;
	
	@FindBy(id="mat-option-5")
	WebElement LocalAreaAdministrativeOption;
	
	@FindBy(xpath="//input[@placeholder='Postal Code']")
	WebElement postalCode;
	
	@FindBy(xpath="//input[@placeholder='Mobile Number']")
	WebElement mobileNumber;
	
	@FindBy(xpath="//input[@placeholder='Email Id']")
	WebElement email;
	
	@FindBy(xpath="//input[@placeholder='CNE/PIN Number']")
	WebElement cnePinNumber;
	
	@FindBy(xpath="//span[text()=' Next ']")
	WebElement nextButton;
	
	
	// Initializing the Page Objects:
	public DemographicPage() {
		PageFactory.initElements(driver, this);
	}
	
	//VErify DemographicPage Title
	
	public String verifyDemographicPageTitle(){
		return driver.getTitle();
	}
	
	
	public FileUploadPage createNewPreRegistration(String FullName,String Age,String AddressLine1,String PostalCode, String MobileNo, String EmailId, String PinNumber){
		
		fullName.sendKeys(FullName);
		DOB.click();
		age.click();
		ageInput.sendKeys(Age);
		
		maleOption.click();
		addressLine1.sendKeys(AddressLine1);
		regionDropDown.click();
		regionOption.click();
		provinnce.click();
		provinceOption.click();
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		city.click();
		cityOption.click();
		LocalAreaAdministrative.click();
		LocalAreaAdministrativeOption.click();
		
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		postalCode.sendKeys(PostalCode);
		mobileNumber.sendKeys(MobileNo);
		email.sendKeys(EmailId);
		cnePinNumber.sendKeys(PinNumber);
		
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		nextButton.click();
		
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return new FileUploadPage();
		
	}
	
	
	

}
