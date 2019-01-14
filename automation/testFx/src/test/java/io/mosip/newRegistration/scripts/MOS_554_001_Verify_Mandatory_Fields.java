package io.mosip.newRegistration.scripts;
import java.io.IOException;
import java.lang.reflect.Field;

import org.junit.jupiter.api.Assertions;
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
import pom.MainPageElements;
import pom.NewRegistrationElements;
import pom.OperatorAuthentication;
import pom.loginElements;
import userlib.UserLibrary;
import util.ScreenshotUtil;
import util.TestDataParseJSON;

@ExtendWith(ApplicationExtension.class)
class MOS_554_001_Verify_Mandatory_Fields {
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
	public void FirstTest(FxRobot robot) throws IOException {
		
		try {
			util.ActionUtils util = new util.ActionUtils(robot);
			/**
			 * File Dir =user.dir\\test_data\\userCredentials.json
			 */
			TestDataParseJSON jsonFromFile = new TestDataParseJSON(
					System.getProperty("user.dir") + "\\test_data\\userCredentials.json");
			//ImageCaptureUtility.captureImageOnFailure(ActionUtils.robot.lookup(loginElements.USERNAME.getLocator()).query());
			/**
			 * Login 
			 */
			loginElements.login(jsonFromFile.getDataFromJsonViaKey("username"),
					jsonFromFile.getDataFromJsonViaKey("password"));	
		
		   UserLibrary.checkFullPageLoad(robot, MainPageElements.class);
		   util.clickOn(MainPageElements.NEWREGISTER.getLocator());
		   UserLibrary.checkFullPageLoad(robot, NewRegistrationElements.class);
		   NewRegistrationElements.fillUpForm();

		} catch (Exception e) {
			ScreenshotUtil.takeScreenshot();
			e.printStackTrace();

		}
	}
	
}