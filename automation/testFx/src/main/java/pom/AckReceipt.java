package pom;

import javafx.geometry.VerticalDirection;
import util.ActionUtils;


public enum AckReceipt {
SAVE("Save","");
	private String locators;
	private String value;
	private AckReceipt(String locators, String value) {
		this.locators = locators;
		this.value = value;
	}
	public static void saveReceipt() {
		ActionUtils.robot.scroll(30, VerticalDirection.DOWN);
		ActionUtils.clickOn(SAVE.getLocators());
	}
	public void setLocators(String locators) {
		this.locators = locators;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getLocators() {
		return locators;
	}
	public String getValue() {
		return value;
	}
	
}
