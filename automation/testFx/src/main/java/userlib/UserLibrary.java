package userlib;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;

import javafx.scene.Node;
import pom.MainPageElements;

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
			Method method = cs.getDeclaredMethod("getValue");
			String val = (String) method.invoke(obj);
			Awaitility.await().until(elementFound(robot, val));
			// System.out.println("value : " + val);
		}

		return false;

	}

	@Test
	public void testCheckFullPageLoad() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, InstantiationException {
		// MainPageElements.
		// MainPageElements.valueOf(loginElements);
		checkFullPageLoad(null, MainPageElements.class);
	}

}
