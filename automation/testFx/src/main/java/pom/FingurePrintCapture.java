package pom;

import static util.ActionUtils.clickOn;

import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import util.ActionUtils;

public enum FingurePrintCapture {
	FINGERPRINT1("#leftHandPalmImageview",""),
	FINGERPRINT2("#rightHandPalmImageview",""),
	FINGERPRINT3("#thumbImageview",""),
	SCAN("#scanBtn",""),
	NEXT("#nextBtn",""),
	PREVIOUS("#previousBtn","");
	
private String locator;
private String value ;

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

private FingurePrintCapture(String locator, String value) {
	this.locator = locator;
	this.value = value;
}

public static void scanFingerPrint() { 
	
	clickOn(FINGERPRINT1.getLocator());
	clickOn(SCAN.getLocator());
	clickOn(ScanPage.SCANDOCUMENT.getLocator());
	Button okButton = ActionUtils.robot.lookup(ButtonType.OK.getText()).query();

	// Button okButton = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
	clickOn(okButton);
	clickOn(FINGERPRINT2.getLocator());
	clickOn(SCAN.getLocator());
	clickOn(ScanPage.SCANDOCUMENT.getLocator());
	 okButton = ActionUtils.robot.lookup(ButtonType.OK.getText()).query();

	// Button okButton = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
	clickOn(okButton);
	clickOn(FINGERPRINT3.getLocator());
	clickOn(SCAN.getLocator());
	clickOn(ScanPage.SCANDOCUMENT.getLocator());
	 okButton = ActionUtils.robot.lookup(ButtonType.OK.getText()).query();

	// Button okButton = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
	clickOn(okButton);
	clickOn(NEXT.getLocator());
	clickOn(FINGERPRINT2.getLocator());
	clickOn(SCAN.getLocator());
	clickOn(ScanPage.SCANDOCUMENT.getLocator());
	 okButton = ActionUtils.robot.lookup(ButtonType.OK.getText()).query();

	// Button okButton = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
	clickOn(okButton);
	clickOn(NEXT.getLocator());
}


}
