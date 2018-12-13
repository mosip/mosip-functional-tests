package mosip.ui.scripts;

import java.util.HashMap;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import mosip.ui.base.uiTestBase;
import mosip.ui.pages.DemographicPage;
import mosip.ui.pages.HomePage;
import mosip.ui.util.ReadJson;


public class DemographicTest extends uiTestBase {
	
	HomePage homePage;
	DemographicPage demographicPage;

	public DemographicTest() {
		super();
	}

	//test cases should be separated -- independent with each other
	//before each test case -- launch the browser and login
	//@test -- execute test case
	//after each test case -- close the browser
	
	@BeforeMethod
	public void setUp() {
		initialization();
		homePage = new HomePage();
		demographicPage = new DemographicPage();
		try {
			homePage.clickOnapplication();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	@Test(priority=1)
	public void verifyDempgraphicPageTitleTest(){
		String demographicPageTitle = demographicPage.verifyDemographicPageTitle();
		Assert.assertEquals(demographicPageTitle, "PreRegistration");
	}
	
	
	
	@Test(priority=2)
	public void newPreRegistrationCreate(){
		
		Map<String, String> finalMap = new HashMap<String, String>();
		finalMap=	ReadJson.demographicIputReader(filepathdemo);//pass the file path here
		String fullName=finalMap.get("fullName");
		String age=finalMap.get("BirthDetails").substring(21);
		String ages[]=age.split("}");
		String Age=ages[0];
		String AddressLine1=finalMap.get("AddressLine1");
		String PostalCode=finalMap.get("Postal Code");
		String MobileNo=finalMap.get("mobile No");
		String EmailId=finalMap.get("emailid");
		String PinNumber=finalMap.get("Pin Number");
		demographicPage.createNewPreRegistration(fullName,Age,AddressLine1,PostalCode,MobileNo,EmailId,PinNumber);
	}
	
	
	
	@AfterMethod
	public void tearDown(){
		driver.quit();
	}
	
	

}
