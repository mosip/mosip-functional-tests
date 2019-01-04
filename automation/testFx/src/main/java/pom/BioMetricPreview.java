package pom;

import javafx.geometry.VerticalDirection;
import lombok.Getter;
import util.ActionUtils;

@Getter
public enum BioMetricPreview {
NEXT("#next",""),
PREVIOUS("#edit","");
	
	private String locator;
	private String value;
	private BioMetricPreview(String locator, String value) {
		this.locator = locator;
		this.value = value;
	}
	public static void submitBioMetric() {
		ActionUtils.robot.scroll(30, VerticalDirection.DOWN);
		ActionUtils.clickOn(NEXT.getLocator());
	}
}
