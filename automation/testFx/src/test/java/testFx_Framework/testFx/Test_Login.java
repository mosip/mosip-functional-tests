package testFx_Framework.testFx;
import java.lang.reflect.Field;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import io.mosip.registration.config.AppConfig;
import io.mosip.registration.controller.Initialization;
import io.mosip.registration.controller.auth.LoginController;
import javafx.stage.Stage;
import pom.AckReceipt;
import pom.BioMetricPreview;
import pom.DemographicPreview;
import pom.FingurePrintCapture;
import pom.IrisCapturePage;
import pom.MainPageElements;
import pom.NewRegistrationElements;
import pom.OperatorAuthentication;
import pom.loginElements;
import userlib.UserLibrary;
import util.ScreenshotUtil;
import util.TestDataParseJSON;

@ExtendWith(ApplicationExtension.class)
class Test_Login {
	@Start
	void onStart(Stage stage) {
		
		System.setProperty("java.net.useSystemProxies", "true");
		/**
		 * Java Advance Concept Reflection 
		 */
		ApplicationContext context = Initialization.getApplicationContext();
		context = new AnnotationConfigApplicationContext(AppConfig.class);
		Field field = null;
		try {
			field = Initialization.class.getDeclaredField("applicationContext");
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		field.setAccessible(true);
		try {
			field.set(null, context);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		LoginController loginController = Initialization.getApplicationContext().getBean(LoginController.class);
		loginController.loadInitialScreen(stage);
	}

	@Test
	public void FirstTest(FxRobot robot) {
		
		try {
			util.ActionUtils util = new util.ActionUtils(robot);
			/**
			 * File Dir =user.dir\\test_data\\userCredentials.json
			 */
			TestDataParseJSON jsonFromFile = new TestDataParseJSON(
					System.getProperty("user.dir") + "\\test_data\\userCredentials.json");
			//ImageCaptureUtility.captureImageOnFailure(ActionUtils.robot.lookup(loginElements.USERNAME.getLocator()).query());
			loginElements.login(jsonFromFile.getDataFromJsonViaKey("username"),
					jsonFromFile.getDataFromJsonViaKey("password"));	
			UserLibrary.checkFullPageLoad(robot, MainPageElements.class);
		   util.clickOn(MainPageElements.NEWREGISTER.getLocator());
		   UserLibrary.checkFullPageLoad(robot, NewRegistrationElements.class);
		   NewRegistrationElements.fillUpForm();
		   NewRegistrationElements.uploadForm(jsonFromFile);
		   UserLibrary.checkFullPageLoad(robot, FingurePrintCapture.class);
		   FingurePrintCapture.scanFingerPrint();
		   IrisCapturePage.captureIRIS();
		   DemographicPreview.submitDetails();
           DemographicPreview.submitDemographicDetails2();
		   BioMetricPreview.submitBioMetric();
      UserLibrary.checkFullPageLoad(robot, OperatorAuthentication.class);
	   OperatorAuthentication.operatorAuthentication(jsonFromFile.getDataFromJsonViaKey("password"));
		   AckReceipt.saveReceipt();
		} catch (Exception e) {
			ScreenshotUtil.takeScreenshot();
			e.printStackTrace();

		}
	}
	
}