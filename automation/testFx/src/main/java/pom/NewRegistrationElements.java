package pom;

import static util.ActionUtils.clickOn;

import javafx.geometry.VerticalDirection;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.input.KeyCode;
import lombok.Getter;
import userlib.UserLibrary;
import util.ActionUtils;

@Getter
public enum NewRegistrationElements {
	PREREGISTRATION_TXT("#preRegistrationId", ""), FETCH_BTN("#fetchBtn", "%fetch"),
	AUTOFILL_BTN("#autoFillBtn", "Click Me To Fill The Form!!"), FULLNAME_TXT("#fullName", ""),
	GENDER_LBL("#gender", ""), AGE_TXT("#ageField", ""), DATEPICKER("#ageDatePicker", ""),
	TOGGLE_LBL1("#toggleLabel1", ""), TOGGLE_LBL2("#toggleLabel2", ""), ADDRESSLINE1("#line1", ""),
	ADDRESSLINE2("#line2", ""), ADDRESSLINE3("#line3", ""), REGION("#region", ""), CITY("#city", ""),
	PROVINCE("#province", ""), POSTALCODE("#postalcode", ""), ADMIN("#localAdministrative", ""),
	MOBILE("#mobilePhone", ""), NEXTBUTTON("#next", ""), PINNUMBER("#pinNumber", ""), POADOCUMENT("#poaDocuments", ""),
	POABUTTON("#scanPOA", ""), POIDOCUMENT("#poiDocuments", ""), POIBUTTON("#scanPOI", ""),
	PORDOCUMENT("#porDocuments", ""), PORBUTTON("#scanPOR", ""), DOBDOCUMENT("#dobDocuments", ""),
	 TOBIOMETRICUPLOAD("#NextBioMetric", ""), DOBBUTTON("#sanDOB", ""),
	EMAIL("#emailId", ""),
	PARENTNAME("#parentName",""),
	RID("#rid","");
	public String locator;
	public String value;

	private NewRegistrationElements(String locator, String value) {
		this.locator = locator;
		this.value = value;
	}

	public String getLocator() {
		return locator;
	}

	public String getValue() {
		return value;
	}

	public static void fillUpForm() {
		String fullName = UserLibrary.getValueforIDRepo("FullName", "fr");
		String addressLine1 = UserLibrary.getValueforIDRepo("addressLine1", "fr");
		String addressLine2 = UserLibrary.getValueforIDRepo("addressLine2", "fr");
		String addressLine3 = UserLibrary.getValueforIDRepo("addressLine3", "fr");
		String region = UserLibrary.getValueforIDRepo("region", "fr");
		String province = UserLibrary.getValueforIDRepo("province", "fr");
		String city = UserLibrary.getValueforIDRepo("city", "fr");
		String postalcode = UserLibrary.getValueforIDRepo("postalcode", "fr");
		String localAdministrativeAuthority = UserLibrary.getValueforIDRepo("localAdministrativeAuthority", "fr");
		String emailId = UserLibrary.getValueforIDRepo("emailId", "fr");
		String mobileNumber = UserLibrary.getValueforIDRepo("mobileNumber", "fr");
		String CNEOrPINNumber = UserLibrary.getValueforIDRepo("CNEOrPINNumber", "fr");

		clickOn(NewRegistrationElements.FULLNAME_TXT.getLocator()).write(fullName);
		DatePicker picker = ActionUtils.robot.lookup(NewRegistrationElements.DATEPICKER.getLocator()).query();
		picker.setValue(java.time.LocalDate.now());
		Node gender = ActionUtils.robot.lookup(NewRegistrationElements.GENDER_LBL.getLocator()).query();
		clickOn(gender).type(KeyCode.DOWN).type(KeyCode.ENTER);

		clickOn(NewRegistrationElements.ADDRESSLINE1.getLocator()).write(addressLine1);
		clickOn(NewRegistrationElements.ADDRESSLINE2.getLocator()).write(addressLine2);
		clickOn(NewRegistrationElements.ADDRESSLINE3.getLocator()).write(addressLine3);
		clickOn(NewRegistrationElements.REGION.getLocator()).write(region);
		clickOn(NewRegistrationElements.PROVINCE.getLocator()).write(province);
		clickOn(NewRegistrationElements.CITY.getLocator()).write(city);
		clickOn(NewRegistrationElements.POSTALCODE.getLocator()).write(postalcode);
		ActionUtils.robot.scroll(30, VerticalDirection.DOWN);
		clickOn(NewRegistrationElements.ADMIN.getLocator()).write(localAdministrativeAuthority);
		clickOn(NewRegistrationElements.MOBILE.getLocator()).write(mobileNumber);
		clickOn(NewRegistrationElements.EMAIL.getLocator()).write(emailId);
		clickOn(NewRegistrationElements.PINNUMBER.getLocator()).write(CNEOrPINNumber);
		clickOn(NewRegistrationElements.NEXTBUTTON.getLocator());
	}

	public static void uploadForm() {
		Node options = ActionUtils.robot.lookup(NewRegistrationElements.POADOCUMENT.getLocator()).query();
		clickOn(options).type(KeyCode.DOWN).type(KeyCode.ENTER);

		reuseFunction(options, NewRegistrationElements.POABUTTON.getLocator());

		options = ActionUtils.robot.lookup(NewRegistrationElements.POIDOCUMENT.getLocator()).query();
		clickOn(options).type(KeyCode.DOWN).type(KeyCode.ENTER);
		reuseFunction(options, NewRegistrationElements.POIBUTTON.getLocator());

		options = ActionUtils.robot.lookup(NewRegistrationElements.PORDOCUMENT.getLocator()).query();
		clickOn(options).type(KeyCode.DOWN).type(KeyCode.ENTER);

		reuseFunction(options, NewRegistrationElements.PORBUTTON.getLocator());

		options = ActionUtils.robot.lookup(NewRegistrationElements.DOBDOCUMENT.getLocator()).query();
		clickOn(options).type(KeyCode.DOWN).type(KeyCode.ENTER);

		reuseFunction(options, NewRegistrationElements.DOBBUTTON.getLocator());
		clickOn(PARENTNAME.getLocator()).write("PARENTDUMMY");
		clickOn(RID.getLocator()).write("11221122121");
		clickOn(NewRegistrationElements.TOBIOMETRICUPLOAD.getLocator());
	}

	public static void reuseFunction(Object options, String button) {
		// options.getSelectionModel().select(UserLibrary.randomIndexSelector(options.getItems().size()));
		clickOn(button);
		clickOn(ScanPage.SCANDOCUMENT.getLocator());
		// Button buttonOk = UserLibrary.GetCloseButton();
		// ActionUtils.robot.lookup(ButtonType.OK)
		Button okButton = ActionUtils.robot.lookup(ButtonType.OK.getText()).query();

		// Button okButton = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
		clickOn(okButton);
	}
}
