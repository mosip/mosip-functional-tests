package pom;

import lombok.Getter;

@Getter
public enum NewRegistrationElements 
{
	//DEMOGRAPHIC PANE
	
	PREREGISTRATION_TXT("#preRegistrationId_txt",""),
	PREREGID_LBL("#searchpreregd_lbl","%search_for_Pre_registration_id"),
	
	FETCH_BTN("#fetch_Btn","%fetch"),
	
	AUTOFILL_BTN("#autoFill_Btn","Click Me To Fill The Form!!"),
	
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
	BI0METRICPANE_TXT("#appBiometric_txt","Applicant Biometrics");
	
	
	
	
	
	
	
	
	
	
	
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


}
