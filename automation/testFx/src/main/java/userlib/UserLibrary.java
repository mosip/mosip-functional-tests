package userlib;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Random;
import java.util.concurrent.Callable;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;

import javafx.scene.Node;
import javafx.scene.control.Button;
import net.minidev.json.parser.ParseException;
import pom.MainPageElements;
import util.ActionUtils;
import util.TestDataParseJSON;

public class UserLibrary {
	public static Callable<Boolean> elementFound(final FxRobot robot, final String locator) {
		return new Callable<Boolean>() {
			public Boolean call() throws Exception {
				boolean returnBack = false;
				Node n = null;
				try {
					n = robot.lookup(locator).query();
					returnBack = n.isVisible();
				} catch (org.testfx.service.query.EmptyNodeQueryException e) {
					return false;
				} catch (NullPointerException exp) {
					return false;
				}
				return returnBack; // The condition that must be fulfilledObject
									// java.lang.reflect.Method.invoke(Object obj, Object... args) throws
									// IllegalAccessException,
			}
		};
	}

	public static boolean checkFullPageLoad(FxRobot robot, Class cs) throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
		for (Object obj : cs.getEnumConstants()) {
			Method method = cs.getDeclaredMethod("getLocator");
			String val = (String) method.invoke(obj);
			try {
			Awaitility.await().until(elementFound(robot, val));
			}catch(org.awaitility.core.ConditionTimeoutException exp) {
				System.out.println("Following Element is Missing : "+val);
			}
			// System.out.println("value : " + val);
		}
		return false;

	}
//	public static void getAllNodes  (FxRobot robot) {
//	  robot.
//	}
	public static String getValueforIDRepo(String field,String language) {
		TestDataParseJSON dataParseJSON = new TestDataParseJSON(System.getProperty("user.dir") + "\\test_data\\SampleData.json");
		String[] idObj = { "request", "demographicDetails", "identity", field,language};
		// fields in a nested json obj
		  Object obj = null;
		try {
			obj = dataParseJSON.getDataFromJsonViaKey(idObj);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return obj.toString();
	}

	public static Integer  randomIndexSelector(int max) {
		int index =new Random().nextInt(max);
		System.out.println(index);
		return index;
	}
	public static Button GetCloseButton(){
		Awaitility.await().until(new Callable<Boolean>() {

			@Override
			public Boolean call() throws Exception {
				try {
				return ActionUtils.robot.lookup(".button").query().isVisible();
				}catch(org.testfx.service.query.EmptyNodeQueryException exp) {
					return false;
				}
			}
		});
	    return ActionUtils.robot.lookup(".button").query();
	}
	@Test
	public void testCheckFullPageLoad() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, InstantiationException {
		// MainPageElements.
		// MainPageElements.valueOf(loginElements);
		checkFullPageLoad(null, MainPageElements.class);
	}

}
