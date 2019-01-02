package util;

import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.hamcrest.Matcher;
import org.testfx.api.FxRobot;
import org.testfx.api.FxRobotContext;
import org.testfx.service.query.NodeQuery;
import org.testfx.service.query.PointQuery;

import com.google.common.base.Predicate;
import com.sun.javafx.robot.FXRobot;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.VerticalDirection;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.MouseButton;
import javafx.stage.Window;

/**
 * This class wraps the FxRobot class methods
 * 
 * @author M1044288
 *
 */
public class ActionUtils {

	/**
	 * 
	 * Creating a FxRobot class type object
	 * All the subsequent methods are called on this object
	 * 
	 */
	public static FxRobot robot ;

	public ActionUtils(FxRobot robot) {
		this.robot= robot;
	}
	public static FxRobot clickOn(MouseButton... buttons) {
		return robot.clickOn(buttons);
	}

	public static FxRobot clickOn(Bounds bounds, MouseButton... buttons) {
		return (FxRobot) robot.clickOn(bounds, buttons);
	}

	public static FxRobot clickOn(double x, double y, MouseButton... buttons) {
		return (FxRobot) robot.clickOn(x, y, buttons);
	}

	public static <T extends Node> FxRobot clickOn(Matcher<T> matcher, MouseButton... buttons) {
		return (FxRobot) robot.clickOn(matcher, buttons);
	}

	public static FxRobot clickOn(Node node, MouseButton... buttons) {
		return (FxRobot) robot.clickOn(node, buttons);
	}

	public static FxRobot clickOn(Point2D point, MouseButton... buttons) {
		return (FxRobot) robot.clickOn(point, buttons);
	}

	public static FxRobot clickOn(PointQuery pointQuery, MouseButton... buttons) {
		return (FxRobot) robot.clickOn(pointQuery, buttons);
	}

	public static <T extends Node> FxRobot clickOn(Predicate<T> predicate, MouseButton... buttons) {
		return (FxRobot) robot.clickOn(predicate, buttons);
	}

	public static FxRobot clickOn(Scene scene, MouseButton... buttons) {
		return (FxRobot) robot.clickOn(scene, buttons);
	}

	public static FxRobot clickOn(String query, MouseButton... buttons) {
		return (FxRobot) robot.clickOn(query, buttons);
	}

	public static FxRobot clickOn(Window window, MouseButton... buttons) {
		return (FxRobot) robot.clickOn(window, buttons);
	}

	public static FxRobot closeCurrentWindow() {
		return robot.closeCurrentWindow();
	}

	public static FxRobot doubleClickOn(MouseButton... buttons) {
		return robot.doubleClickOn(buttons);
	}

	public static FxRobot doubleClickOn(Bounds bounds, MouseButton... buttons) {
		return (FxRobot) robot.doubleClickOn(bounds, buttons);
	}

	public static <T extends Node> FxRobot doubleClickOn(Matcher<T> matcher, MouseButton... buttons) {
		return (FxRobot) robot.doubleClickOn(matcher, buttons);
	}

	public static FxRobot doubleClickOn(Node node, MouseButton... buttons) {
		return (FxRobot) robot.doubleClickOn(node, buttons);
	}

	public static FxRobot doubleClickOn(Point2D point, MouseButton... buttons) {
		return (FxRobot) robot.doubleClickOn(point, buttons);
	}

	public static FxRobot doubleClickOn(PointQuery pointQuery, MouseButton... buttons) {
		return (FxRobot) robot.doubleClickOn(pointQuery, buttons);
	}

	public static <T extends Node> FxRobot doubleClickOn(Predicate<T> predicate, MouseButton... buttons) {
		return (FxRobot) robot.doubleClickOn(predicate, buttons);
	}

	public static FxRobot doubleClickOn(Scene scene, MouseButton... buttons) {
		return (FxRobot) robot.doubleClickOn(scene, buttons);
	}

	public static FxRobot doubleClickOn(String query, MouseButton... buttons) {
		return (FxRobot) robot.doubleClickOn(query, buttons);
	}

	public static FxRobot doubleClickOn(Window window, MouseButton... buttons) {
		return (FxRobot) robot.doubleClickOn(window, buttons);
	}

	public static FxRobot doubleClickOn(double x, double y, MouseButton... buttons) {
		return (FxRobot) robot.doubleClickOn(x, y, buttons);
	}

	public static FxRobot drag(MouseButton... buttons) {
		return robot.drag(buttons);
	}

	public static FxRobot drag(Bounds bounds, MouseButton... buttons) {
		return robot.drag(bounds, buttons);
	}

	public static <T extends Node> FxRobot drag(Matcher<T> matcher, MouseButton... buttons) {
		return robot.drag(matcher, buttons);
	}

	public static FxRobot drag(Node node, MouseButton... buttons) {
		return robot.drag(node, buttons);
	}

	public static FxRobot drag(Point2D point, MouseButton... buttons) {
		return robot.drag(point, buttons);
	}

	public static FxRobot drag(PointQuery pointQuery, MouseButton... buttons) {
		return robot.drag(pointQuery, buttons);
	}

	public static <T extends Node> FxRobot drag(Predicate<T> predicate, MouseButton... buttons) {
		return robot.drag(predicate, buttons);
	}

	public static FxRobot drag(Scene scene, MouseButton... buttons) {
		return robot.drag(scene, buttons);
	}

	public static FxRobot drag(String query, MouseButton... buttons) {
		return robot.drag(query, buttons);
	}

	public static FxRobot drag(Window window, MouseButton... buttons) {
		return robot.drag(window, buttons);
	}

	public static FxRobot drag(double x, double y, MouseButton... buttons) {
		return robot.drag(x, y, buttons);
	}

	public static FxRobot drop() {
		return robot.drop();
	}

	public static FxRobot dropBy(double x, double y) {
		return robot.dropBy(x, y);
	}

	public static FxRobot dropTo(Bounds bounds) {
		return robot.dropTo(bounds);
	}

	public static <T extends Node> FxRobot dropTo(Matcher<T> matcher) {
		return robot.dropTo(matcher);
	}

	public static FxRobot dropTo(Node node) {
		return robot.dropTo(node);
	}

	public static FxRobot dropTo(Point2D point) {
		return robot.dropTo(point);
	}

	public static FxRobot dropTo(PointQuery pointQuery) {
		return robot.dropTo(pointQuery);
	}

	public static <T extends Node> FxRobot dropTo(Predicate<T> predicate) {
		return robot.dropTo(predicate);
	}

	public static FxRobot dropTo(Scene scene) {
		return robot.dropTo(scene);
	}

	public static FxRobot dropTo(String query) {
		return robot.dropTo(query);
	}

	public static FxRobot dropTo(Window window) {
		return robot.dropTo(window);
	}

	public static FxRobot dropTo(double x, double y) {
		return robot.dropTo(x, y);
	}

	public static FxRobot eraseText(int amount) {
		return robot.eraseText(amount);
	}

	public static NodeQuery from(Collection<Node> parentNodes) {
		return robot.from(parentNodes);
	}

	public static NodeQuery from(Node... parentNodes) {
		return robot.from(parentNodes);
	}

	public static NodeQuery from(NodeQuery nodeQuery) {
		return robot.from(nodeQuery);
	}

	public static NodeQuery fromAll() {
		return robot.fromAll();
	}

	public static <T> FxRobot interact(Callable<T> callable) {
		return robot.interact(callable);
	}

	public static FxRobot interact(Runnable runnable) {
		return robot.interact(runnable);
	}

	public static <T extends Node> NodeQuery lookup(Matcher<T> matcher) {
		return robot.lookup(matcher);
	}

	public static <T extends Node> NodeQuery lookup(Predicate<T> predicate) {
		return robot.lookup(predicate);
	}

	public static <T extends Node> NodeQuery lookup(String query) {
		return robot.lookup(query);
	}

	public static FxRobot moveBy(double x, double y) {
		return (FxRobot) robot.moveBy(x, y);
	}

	public static FxRobot moveTo(Bounds bounds) {
		return (FxRobot) robot.moveTo(bounds);
	}

	public static <T extends Node> FxRobot moveTo(Matcher<T> matcher) {
		return (FxRobot) robot.moveTo(matcher);
	}

	public static FxRobot moveTo(Node node) {
		return (FxRobot) robot.moveTo(node);
	}

	public static FxRobot moveTo(Point2D point) {
		return (FxRobot) robot.moveTo(point);
	}

	public static FxRobot moveTo(PointQuery pointQuery) {
		return (FxRobot) robot.moveTo(pointQuery);
	}

	public static <T extends Node> FxRobot moveTo(Predicate<T> predicate) {
		return (FxRobot) robot.moveTo(predicate);
	}

	public static FxRobot moveTo(Scene scene) {
		return (FxRobot) robot.moveTo(scene);
	}

	public static FxRobot moveTo(String query) {
		return (FxRobot) robot.moveTo(query);
	}

	public static FxRobot moveTo(Window window) {
		return (FxRobot) robot.moveTo(window);
	}

	public static FxRobot moveTo(double x, double y) {
		return (FxRobot) robot.moveTo(x, y);
	}

	public static PointQuery offset(Bounds bounds, double offsetX, double offsetY) {
		return robot.offset(bounds, offsetX, offsetY);
	}

	public static <T extends Node> PointQuery offset(Matcher<T> matcher, double offsetX, double offsetY) {
		return robot.offset(matcher, offsetX, offsetY);
	}

	public static PointQuery offset(Node node, double offsetX, double offsetY) {
		return robot.offset(node, offsetX, offsetY);
	}

	public static PointQuery offset(Point2D point, double offsetX, double offsetY) {
		return robot.offset(point, offsetX, offsetY);
	}

	public static <T extends Node> PointQuery offset(Predicate<T> predicate, double offsetX, double offsetY) {
		return robot.offset(predicate, offsetX, offsetY);
	}

	public static PointQuery offset(Scene scene, double offsetX, double offsetY) {
		return robot.offset(scene, offsetX, offsetY);
	}

	public static PointQuery offset(String query, double offsetX, double offsetY) {
		return robot.offset(query, offsetX, offsetY);
	}

	public static PointQuery offset(Window window, double offsetX, double offsetY) {
		return robot.offset(window, offsetX, offsetY);
	}

	public static PointQuery point(Bounds bounds) {
		return robot.point(bounds);
	}

	public static <T extends Node> PointQuery point(Matcher<T> matcher) {
		return robot.point(matcher);
	}

	public static PointQuery point(Node node) {
		return robot.point(node);
	}

	public static PointQuery point(Point2D point) {
		return robot.point(point);
	}

	public static <T extends Node> PointQuery point(Predicate<T> predicate) {
		return robot.point(predicate);
	}

	public static PointQuery point(Scene scene) {
		return robot.point(scene);
	}

	public static PointQuery point(String query) {
		return robot.point(query);
	}

	public static PointQuery point(Window window) {
		return robot.point(window);
	}

	public static PointQuery point(double x, double y) {
		return robot.point(x, y);
	}

	

	public static FxRobot press(KeyCode... keys) {
		return robot.press(keys);
	}

	public static FxRobot push(KeyCode... combination) {
		return robot.push(combination);
	}

	public static FxRobot push(KeyCodeCombination combination) {
		return robot.push(combination);
	}

	public static FxRobot release(KeyCode... keys) {
		return robot.release(keys);
	}

	public static FxRobot release(MouseButton... buttons) {
		return robot.release(buttons);
	}

	public static FxRobot rightClickOn() {
		return robot.rightClickOn();
	}

	public static FxRobot rightClickOn(Bounds bounds) {
		return (FxRobot) robot.rightClickOn(bounds);
	}

	public static <T extends Node> FxRobot rightClickOn(Matcher<T> matcher) {
		return (FxRobot) robot.rightClickOn(matcher);
	}

	public static FxRobot rightClickOn(Node node) {
		return (FxRobot) robot.rightClickOn(node);
	}

	public static FxRobotContext robotContext() {
		return robot.robotContext();
	}

	public static Node rootNode(Node node) {
		return robot.rootNode(node);
	}

	public static Node rootNode(Scene scene) {
		return robot.rootNode(scene);
	}

	public static Node rootNode(Window window) {
		return robot.rootNode(window);
	}

	public static FxRobot scroll(int amount, VerticalDirection direction) {
		return robot.scroll(amount, direction);
	}

	public static FxRobot scroll(VerticalDirection direction) {
		return robot.scroll(direction);
	}

	public static FxRobot sleep(long milliseconds) {
		return robot.sleep(milliseconds);
	}

	public static FxRobot sleep(long duration, TimeUnit timeUnit) {
		return robot.sleep(duration, timeUnit);
	}

	public static FxRobot type(KeyCode... keyCodes) {
		return robot.type(keyCodes);
	}

	public static FxRobot type(KeyCode keyCode, int times) {
		return robot.type(keyCode, times);
	}

	public static FxRobot write(char character) {
		return robot.write(character);
	}

	public static FxRobot write(String text) {
		return robot.write(text);
	}

}
