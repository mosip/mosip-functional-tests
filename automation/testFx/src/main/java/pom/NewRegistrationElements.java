package pom;

import static util.ActionUtils.*;
import lombok.Getter;

@Getter
public enum NewRegistrationElements 
{
	//DEMOGRAPHIC PANE
	
	PREREGISTRATION_TXT("#preRegistrationId_txt",""),
	PREREGID_LBL("#searchpreregd_lbl","%search_for_Pre_registration_id"),
	
	autofill_btn("#autoFillBtn",""),
	
	FETCH_BTN("#fetch_Btn","%fetch"),
	
	
	FULLNAME_TXT("#fullName",""),
	FULLNAME_LBL("#fullName_lbl","%full_name"),
	
	FULLNAMELOCALLANG_TXT("#fullName_lc",""),
//same id as full name text field	
	FULLNAME_ICON("#fullName",""),
	
	AGE_TXT("#ageField_txt",""),
	AGE_LBL("#age_lbl","%age"),
	TOGGLE_LBL1("#toggleLabel1",""),
	TOGGLE_LBL2("#toggleLabel2",""),
	
	DOB_LBL("#dob_lbl","%dob"),
	DATEPICKER("#ageDatePicker",""),
	
	
	GENDER_COMBO("#gender",""),
	GENDER_LBL("#gender_lbl","%gender"),
	
	
	ADDRESSL1_LBL("#addLine1_lbl","%address_line1"),
	ADDRESSLINE1_TXT("#addressLine1_txt",""),
	ADDRESSL2_LBL("#addLine2_lbl","%address_line2"),
	ADDRESSLINE2_TXT("#addressLine2_txt",""),
	ADDRESSL3_LBL("#addLine3_lbl","%address_line3"),
	ADDRESSLINE3_TXT("#addressLine3_txt",""),
	
	COPYPASTEICON("#copyPasteIcon",""),

	ADD1LOCALLANG_LBL("#addressLine1LocalLanguagelbl",""),
	ADD2LOCALLANG_LBL("#addressLine2LocalLanguagelbl",""),
	ADD3LOCALLANG_LBL("#addressLine3LocalLanguagelbl",""),
	ADD1LOCALLANG_TXT("#addressLine1LocalLanguage_txt",""),
	ADD2LOCALLANG_TXT("#addressLine2LocalLanguage_txt",""),
	ADD3LOCALLANG_TXT("#addressLine3LocalLanguage_txt",""),
	ADDLINE1_ICON("#addressLine1",""),
	ADDLINE2_ICON("#addressLine2",""),
	ADDLINE3_ICON("#addressLine3",""),
	
	REGION_LBL("#region_lbl","%region"),	
	REGION_TXT("#region_txt",""),
	
	CITY_LBL("#city_lbl","%city"),
	CITY_TXT("#city_txt",""),
	
	PROVINCE_LBL("#province_lbl","%province"),
	PROVINCE_TXT("#province_txt",""),
	
	POSTAL_LBL("#postalcode_lbl","%postal_code"),
	POSTALCODE_TXT("#postalCode_txt",""),
	
	LOCALAUTH_LBL("#localauth_lbl","%local_administrative_authority"),
	LOCALAUTHOURITY_TXT("#localAdminAuthority_txt",""),
	
	MOBNO_LBL("#mobno_lbl","%mobile_number"),
	MOBILENO_TXT("#mobileNo_txt",""),
	
	EMAILID_LBL("#emailid_lbl","%email_id"),
	EMAILID_TXT("#emailId_txt",""),
	
	PINNUM_LBL("#pinNum_lbl","CNIE Number / Pin Number"),
	PINNUMBER("#cniOrPinNumber_txt",""),
	
	
	NEXT_BTN("#next_Btn","%next"),
	
	//APPLICANT BIOMETRICS
	BI0METRICPANE_TXT("#appBiometric_txt","Applicant Biometrics"),
	FINGERPRINT_LBL("#fingerprint_lbl","Fingerprint"),
	
	LEFTHAND_IMG("#leftHandPalm",""),
	RIGHTHAND_IMG("#rightHandPalm",""),
	THUMB_IMG("#thumb",""),
	
	QUALITY1_LBL("#quality_lbl1","Quality %       :"),
	QUALITY2_LBL("#quality_lbl2","Quality %       :"),	
	QUALITY3_LBL("#quality_lbl3","Quality %       :"),
	THRESHOLD_LBL1("#threshold1_lbl","Threshold %   :"),
	THRESHOLD_LBL2("#threshold1_lb2","Threshold %   :"),
	THRESHOLD_LBL3("#threshold1_lb3","Threshold %   :"),
	
	SCAN_BTN("#scan_btn","Scan"),
	BIONEXT_BTN("#next_btn","Next"),
	PREVIOUS_BTN("#previous_btn","Previous")
	
	
;
	
	
	public String locator;
	public String value;
	private NewRegistrationElements(String locator,String value) 
    { 
    	this.locator=locator;
        this.value = value; 
    }
	public String getLocator() {
		return locator;
	}

	public String getValue() {
		return value;
	}

	 public static void Verify_Autofill_demographicDetails(String PreRgID) {
		 
		 
		// clickOn(NewRegistrationElements.autofill_btn.locator) ;
		clickOn(NewRegistrationElements.PREREGISTRATION_TXT.locator).write("");
		clickOn(NewRegistrationElements.FETCH_BTN.locator);
	//add the code to fetch the details from DB
		
		System.out.println(lookup(NewRegistrationElements.PREREGISTRATION_TXT.locator).queryText());
		
	 }
	 public static void verify_fillDetails_PreReg() {
		 
		 		 	clickOn(NewRegistrationElements.FULLNAME_TXT.locator).write("");
		 		 	clickOn(NewRegistrationElements.AGE_TXT.locator).write("");
		 		 	clickOn(NewRegistrationElements.DATEPICKER.locator).write("");
		 		 	clickOn(NewRegistrationElements.ADDRESSLINE1_TXT.locator).write("");
		 		 	clickOn(NewRegistrationElements.ADDRESSLINE2_TXT.locator).write("");
		 		 	clickOn(NewRegistrationElements.ADDRESSLINE3_TXT.locator).write("");
		 		 	clickOn(NewRegistrationElements.REGION_TXT.locator).write("");
		 		 	clickOn(NewRegistrationElements.CITY_TXT.locator).write("");
		 		 	clickOn(NewRegistrationElements.PROVINCE_TXT.locator).write("");
		 		 	clickOn(NewRegistrationElements.POSTALCODE_TXT.locator).write("");
		 		 	clickOn(NewRegistrationElements.LOCALAUTHOURITY_TXT.locator).write("");
		 		 	clickOn(NewRegistrationElements.MOBILENO_TXT.locator).write("");
		 		 	clickOn(NewRegistrationElements.EMAILID_TXT.locator).write("");
		 		 	clickOn(NewRegistrationElements.PINNUMBER.locator).write("");
		 		 	
		 		 	clickOn(NewRegistrationElements.NEXT_BTN.locator);
		 		 	
		 		 	
	 }
	
}
