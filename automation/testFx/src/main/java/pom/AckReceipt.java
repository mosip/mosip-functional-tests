package pom;

import javafx.geometry.VerticalDirection;
import lombok.Getter;
import util.ActionUtils;

@Getter
public enum AckReceipt {
SAVE("#save","");
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
}
