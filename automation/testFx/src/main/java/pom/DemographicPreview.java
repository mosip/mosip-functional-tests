package pom;

import javafx.geometry.VerticalDirection;
import util.ActionUtils;

public enum DemographicPreview {
	NEXT("#nextBtn",""),
	EDIT("#editBtn",""),
;
	private String locator;
	private String value;
	
	public String getLocator() {
		return locator;
	}
	public void setLocator(String locator) {
		this.locator = locator;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
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
