package userlib;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Random;
import java.util.concurrent.Callable;

import org.awaitility.Awaitility;
import org.json.simple.parser.ParseException;
import org.testfx.api.FxRobot;

import javafx.scene.Node;
import javafx.scene.control.Button;
import util.ActionUtils;
import util.TestDataParseJSON;

public class UserLibrary {
	public static Callable<Boolean> elementFound(final String locator) {
		return new Callable<Boolean>() {
			public Boolean call() throws Exception {
				boolean returnBack = false;
				Node n = null;
				try {
					n = ActionUtils.robot.lookup(locator).query();
					returnBack = n.isVisible();
					assertTrue(returnBack);
				} catch (org.testfx.service.query.EmptyNodeQueryException e) {
					return false;
				} catch (NullPointerException exp) {
					return false;
				}
				return returnBack;
			}
		};
	}

	public static boolean checkFullPageLoad(FxRobot robot, Class cs) throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
		for (Object obj : cs.getEnumConstants()) {
			Method method = cs.getDeclaredMethod("getLocator");
			String val = (String) method.invoke(obj);
			try {
				//Configure 20 secs 
				Awaitility.await().until(elementFound(val));
			} catch (org.awaitility.core.ConditionTimeoutException exp) {
				System.out.println("Following Element is Missing : " + val);
			}
			// System.out.println("value : " + val);
		}
		return false;

	}

	public static String getValueforIDRepo(String field, String language) throws IOException {
		TestDataParseJSON dataParseJSON = new TestDataParseJSON(
				System.getProperty("user.dir") + "\\test_data\\SampleData.json");
		String[] idObj = { "request", "demographicDetails", "identity", field, language };
		Object obj = null;
		try {
			obj = dataParseJSON.getDataFromJsonViaKey(idObj);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return obj.toString();
	}

	public static Integer randomIndexSelector(int max) {
		int index = new Random().nextInt(max)+1;
		System.out.println(index);
		return index;
	}

	public static Button GetCloseButton() {
		Awaitility.await().until(new Callable<Boolean>() {
			public Boolean call() throws Exception {
				try {
					return ActionUtils.robot.lookup(".button").query().isVisible();
				} catch (org.testfx.service.query.EmptyNodeQueryException exp) {
					return false;
				}
			}
		});
		return ActionUtils.robot.lookup(".button").query();
	}

}
