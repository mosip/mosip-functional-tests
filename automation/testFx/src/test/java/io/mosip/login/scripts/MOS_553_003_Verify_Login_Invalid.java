package io.mosip.login.scripts;

import java.io.IOException;
import java.lang.reflect.Field;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.Start;
import org.testfx.service.finder.NodeFinder;

import io.mosip.registration.config.AppConfig;
import io.mosip.registration.controller.Initialization;
import io.mosip.registration.controller.auth.LoginController;
import io.mosip.utils.ActionUtils;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import pom.loginElements;
import util.ScreenshotUtil;
import util.TestDataParseJSON;

public class MOS_553_003_Verify_Login_Invalid {
//invalid password
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
	public void Login_Invalid(FxRobot robot) throws IOException {
		
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
					jsonFromFile.getDataFromJsonViaKey("invalidPassword"));	
			
			//add code to validate the error message
			//no there is a method alertbox get textr
			//you can do node .getTxt
			
			
			
		} catch (Exception e) {
			ScreenshotUtil.takeScreenshot();
			e.printStackTrace();

		}
	}
}
