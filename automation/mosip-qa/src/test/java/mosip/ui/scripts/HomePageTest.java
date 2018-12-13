package mosip.ui.scripts;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import mosip.ui.base.uiTestBase;
import mosip.ui.pages.HomePage;
import mosip.ui.pages.DemographicPage;

public class HomePageTest extends uiTestBase{
	DemographicPage demographicPage;
	HomePage homePage;
	
	public HomePageTest(){
		super();
	}
	
	@BeforeMethod
	public void setUp(){
		initialization();
		homePage = new HomePage();	
	}
	
	@Test(priority=1)
	public void loginPageTitleTest(){
		String title = homePage.verifyHomePageTitle();
		Assert.assertEquals(title, "PreRegistration");
	}
	
	
	
	@Test(priority=3)
	public void loginTest(){
		try {
			homePage.clickOnapplication();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	@AfterMethod
	public void tearDown(){
		driver.quit();
	}
	
	
	
	

}
