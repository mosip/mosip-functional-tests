package pom;

import static util.ActionUtils.clickOn;

import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import util.ActionUtils;

public enum IrisCapturePage {
	LEFTEYE("#leftIrisImage","")
	,RIGHTEYE("#rightIrisImage",""),
	SCAN("#scanIris",""),
	NEXT("#irisNext",""),
	PREVIOUS("#irisPrevious","");
	
private IrisCapturePage(String locator, String value) {
		this.locator = locator;
		this.value = value;
	}
private String locator;
private String value;

public static void captureIRIS() {
	clickOn(LEFTEYE.getLocator());
	clickOn(SCAN.getLocator());
	clickOn(ScanPage.SCANDOCUMENT.getLocator());
	Button okButton = ActionUtils.robot.lookup(ButtonType.OK.getText()).query();
	clickOn(okButton);
	clickOn(RIGHTEYE.getLocator());
	clickOn(SCAN.getLocator());
	clickOn(ScanPage.SCANDOCUMENT.getLocator());
	 okButton = ActionUtils.robot.lookup(ButtonType.OK.getText()).query();
	clickOn(okButton);
	
	for(int i=0;i<3;i++) {
		clickOn(RIGHTEYE.getLocator());
		clickOn(SCAN.getLocator());
		clickOn(ScanPage.SCANDOCUMENT.getLocator());
		 okButton = ActionUtils.robot.lookup(ButtonType.OK.getText()).query();
		clickOn(okButton);
	}
	clickOn(NEXT.getLocator());
}

public String getLocator() {
	return locator;
}

public String getValue() {
	return value;
}

public void setLocator(String locator) {
	this.locator = locator;
}

public void setValue(String value) {
	this.value = value;
}
}
