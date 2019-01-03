package pom;

import lombok.Getter;

@Getter
public enum NewRegistrationElements 
{
	//DEMOGRAPHIC PANE
	
	PREREGISTRATION_TXT("#preRegistrationId",""),
	PREREGID_LBL("#searchpreregd_lbl","%search_for_Pre_registration_id"),
	
	FETCH_BTN("#fetchBtn","%fetch"),
	
	AUTOFILL_BTN("#autoFillBtn","Click Me To Fill The Form!!"),
	
	FULLNAME_TXT("#fullName",""),
	FULLNAME_LBL("#fullName_lbl","%full_name"),
	
	FULLNAMELOCALLANG_TXT("#fullName_lc",""),
	FULLNAME_ICON("#fullName",""),
	
	AGE_TXT("#ageField",""),
	AGE_LBL("#age_lbl","%age"),
	TOGGLE_LBL1("#toggleLabel1",""),
	TOGGLE_LBL2("#toggleLabel2",""),
	
	DOB_LBL("#dob_lbl","%dob"),
	DATEPICKER("#ageDatePicker",""),
	
	
	GENDER_COMBO("#gender",""),
	GENDER_LBL("#gender_lbl","%gender"),
	
	
	ADDRESSL1_LBL("#addLine1_lbl","%address_line1"),
	ADDRESSLINE1_TXT("#addressLine1",""),
	ADDRESSL2_LBL("#addLine2_lbl","%address_line2"),
	ADDRESSLINE2_TXT("#addressLine2",""),
	ADDRESSL3_LBL("#addLine3_lbl","%address_line3"),
	ADDRESSLINE3_TXT("#addressLine3",""),
	
	COPYPASTEICON("#copyPasteIcon",""),

	ADD1LOCALLANG_TXT("#addressLine1LocalLanguage",""),
	ADD2LOCALLANG_TXT("#addressLine2LocalLanguage",""),
	ADD3LOCALLANG_TXT("#addressLine3LocalLanguage",""),
	ADDLINE1_ICON("#addressLine1",""),
	ADDLINE2_ICON("#addressLine2",""),
	ADDLINE3_ICON("#addressLine3",""),
	
	REGION_LBL("#region_lbl","%region"),	
	REGION_TXT("#region",""),
	
	CITY_LBL("#city_lbl","%city"),
	CITY_TXT("#city",""),
	
	PROVINCE_LBL("#province_lbl","%province"),
	PROVINCE_TXT("#province",""),
	
	POSTAL_LBL("#postalcode_lbl","%postal_code"),
	POSTALCODE_TXT("#postalCode",""),
	
	LOCALAUTH_LBL("#localauth_lbl","%local_administrative_authority"),
	LOCALAUTHOURITY_TXT("#localAdminAuthority",""),
	
	MOBNO_LBL("#mobno_lbl","%mobile_number"),
	MOBILENO_TXT("#mobileNo",""),
	
	EMAILID_LBL("#emailid_lbl","%email_id"),
	EMAILID_TXT("#emailId",""),
	
	PINNUM_LBL("#pinNum_lbl","CNIE Number / Pin Number"),
	PINNUMBER("#cniOrPinNumber",""),
	
	
	NEXT_BTN("#nextBtn","%next"),
	
	//APPLICANT BIOMETRICS
	BI0METRICPANE_CLK("#biometricTitlePane","Applicant Biometrics");
	
	
	
	
	
	
	
	
	
	
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
