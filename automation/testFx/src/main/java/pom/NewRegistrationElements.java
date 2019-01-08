package pom;

import java.io.IOException;

import org.awaitility.Awaitility;
import org.json.simple.parser.ParseException;

import constants.TestFxConstants;
import javafx.geometry.VerticalDirection;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.input.KeyCode;
import lombok.Getter;
import userlib.FileUploadLibrary;
import userlib.UserLibrary;
import util.ActionUtils;
import util.TestDataParseJSON;

import static util.ActionUtils.*;

@Getter
public enum NewRegistrationElements {
	PREREGISTRATION_TXT("#preRegistrationId", ""), FETCH_BTN("#fetchBtn", "%fetch"),
	AUTOFILL_BTN("#autoFillBtn", "Click Me To Fill The Form!!"), FULLNAME_TXT("#fullName", ""),
	FULLNAMELANGUAGE("#fullNameLocalLanguage", ""),
	GENDER_LBL("#gender", ""), AGE_TXT("#ageField", ""), DATEPICKER("#ageDatePicker", ""),
	TOGGLE_LBL1("#toggleLabel1", ""), TOGGLE_LBL2("#toggleLabel2", ""), ADDRESSLINE1("#line1", ""),
	ADDRESSLINE2("#line2", ""), ADDRESSLINE3("#line3", ""), REGION("#region", ""), CITY("#city", ""),
	PROVINCE("#province", ""), POSTALCODE("#postalCode", ""), ADMIN("#localAdminAuthority", ""),
	MOBILE("#mobileNo", ""), NEXTBUTTON("#nextBtn", ""), PINNUMBER("#cniOrPinNumber", ""),
	POADOCUMENT("#poaDocuments", ""), POABUTTON("#poaScanBtn", ""), POIDOCUMENT("#poiDocuments", ""),
	POIBUTTON("#poiScanBtn", ""), PORDOCUMENT("#porDocuments", ""), PORBUTTON("#porScanBtn", ""),
	DOBDOCUMENT("#dobDocuments", ""), TOBIOMETRICUPLOAD("#pane2NextBtn", ""), DOBBUTTON("#dobScanBtn", ""),
	EMAIL("#emailId", ""), PARENTNAME("#parentName", ""),

	RID("#uinId", "");
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

	public static void fillUpForm() throws IOException {
		String fullName = UserLibrary.getValueforIDRepo(TestFxConstants.FULLNAME.getValue(), TestFxConstants.LANGUAGE_FR.getValue());
		String addressLine1 = UserLibrary.getValueforIDRepo(TestFxConstants.ADDRESSLINE_1.getValue(), TestFxConstants.LANGUAGE_FR.getValue());
		String addressLine2 = UserLibrary.getValueforIDRepo(TestFxConstants.ADDRESSLINE_2.getValue(), TestFxConstants.LANGUAGE_FR.getValue());
		String addressLine3 = UserLibrary.getValueforIDRepo(TestFxConstants.ADDRESSLINE_3.getValue(), TestFxConstants.LANGUAGE_FR.getValue());
		String region = UserLibrary.getValueforIDRepo(TestFxConstants.REGION.getValue(), TestFxConstants.LANGUAGE_FR.getValue());
		String province = UserLibrary.getValueforIDRepo(TestFxConstants.PROVINCE.getValue(),  TestFxConstants.LANGUAGE_FR.getValue());
		String city = UserLibrary.getValueforIDRepo(TestFxConstants.CITY.getValue(),  TestFxConstants.LANGUAGE_FR.getValue());
		String postalcode = UserLibrary.getValueforIDRepo(TestFxConstants.POSTALCODE.getValue(),  TestFxConstants.LANGUAGE_FR.getValue());
		String localAdministrativeAuthority = UserLibrary.getValueforIDRepo(TestFxConstants.LOCALADMINISTRATIVEAUTHORITY.getValue(),  TestFxConstants.LANGUAGE_FR.getValue());
		String emailId = UserLibrary.getValueforIDRepo(TestFxConstants.EMAILID.getValue(),  TestFxConstants.LANGUAGE_FR.getValue());
		String mobileNumber = UserLibrary.getValueforIDRepo(TestFxConstants.MOBILENUMBER.getValue(),  TestFxConstants.LANGUAGE_FR.getValue());
		String CNEOrPINNumber = UserLibrary.getValueforIDRepo(TestFxConstants.CNEORPINNUMBER.getValue(),  TestFxConstants.LANGUAGE_FR.getValue());

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
		ActionUtils.robot.scroll(Integer.parseInt(TestFxConstants.SCROLL.getValue()), VerticalDirection.DOWN);
		clickOn(NewRegistrationElements.ADMIN.getLocator()).write(localAdministrativeAuthority);
		clickOn(NewRegistrationElements.MOBILE.getLocator()).write(mobileNumber);
		clickOn(NewRegistrationElements.EMAIL.getLocator()).write(emailId);
		clickOn(NewRegistrationElements.PINNUMBER.getLocator()).write(CNEOrPINNumber);
		clickOn(NewRegistrationElements.NEXTBUTTON.getLocator());
	}

	public static void uploadForm(TestDataParseJSON jsonFromFile) throws IOException, ParseException {
		Node options = ActionUtils.robot.lookup(NewRegistrationElements.POADOCUMENT.getLocator()).query();
		reuseFunction(options, NewRegistrationElements.POABUTTON.getLocator(),1);
		options = ActionUtils.robot.lookup(NewRegistrationElements.POIDOCUMENT.getLocator()).query();
		reuseFunction(options, NewRegistrationElements.POIBUTTON.getLocator(),2);
		options = ActionUtils.robot.lookup(NewRegistrationElements.PORDOCUMENT.getLocator()).query();
		reuseFunction(options, NewRegistrationElements.PORBUTTON.getLocator(),3);
		options = ActionUtils.robot.lookup(NewRegistrationElements.DOBDOCUMENT.getLocator()).query();
		reuseFunction(options, NewRegistrationElements.DOBBUTTON.getLocator(),3);
		clickOn(PARENTNAME.getLocator()).write(jsonFromFile.getDataFromJsonViaKey("parentName"));
		clickOn(RID.getLocator()).write(jsonFromFile.getDataFromJsonViaKey("RID"));
		clickOn(NewRegistrationElements.TOBIOMETRICUPLOAD.getLocator());
	}

	public static void reuseFunction(Node options, String button,int Selector) throws IOException, ParseException {
		int selector = Selector;
		for (int i = 0; i < selector; i++) {
			clickOn(options).type(KeyCode.DOWN);
		}
		clickOn(options).type(KeyCode.ENTER);
		String type=FileUploadLibrary.documentTypeSwitch(selector);
		String file=FileUploadLibrary.getFileToUpload(type);
		FileUploadLibrary.ovverideImage(file);
		clickOn(button);
		Awaitility.await().until(UserLibrary.elementFound(ScanPage.SCANDOCUMENT.getLocator()));
		clickOn(ScanPage.SCANDOCUMENT.getLocator());
		Button okButton = ActionUtils.robot.lookup(ButtonType.OK.getText()).query();
		clickOn(okButton);
	}
}
