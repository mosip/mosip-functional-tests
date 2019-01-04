package pom;

import javafx.geometry.VerticalDirection;
import lombok.Getter;
import util.ActionUtils;

@Getter
public enum DemographicPreview {
	NEXT("#nextBtn",""),
	EDIT("#editBtn",""),
;
	private String locator;
	private String value;
	private DemographicPreview(String locator, String value) {
		this.locator = locator;
		this.value = value;
	}
	public static void submitDetails() { 
		ActionUtils.robot.scroll(30, VerticalDirection.DOWN);
		ActionUtils.clickOn(NEXT.getLocator());
	}
	public static void submitDemographicDetails2() {
		ActionUtils.robot.scroll(30, VerticalDirection.DOWN);
		ActionUtils.clickOn(NEXT.getLocator());
	}
}
