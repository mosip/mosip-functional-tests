package constants;

public enum TestFxConstants {
	FULLNAME("FullName"),
	LANGUAGE_FR("fr"),
	ADDRESSLINE_1("addressLine1"),
	ADDRESSLINE_2("addressLine2"),
	ADDRESSLINE_3("addressLine3"),
	REGION("region"),
	PROVINCE("province"),
	CITY("city"),
	POSTALCODE("postalcode"),
	LOCALADMINISTRATIVEAUTHORITY("localAdministrativeAuthority"),
	EMAILID("emailId"),
	MOBILENUMBER("mobileNumber"),
	CNEORPINNUMBER("CNEOrPINNumber"),
	AGE("age"),
	SCROLL("30");
	String value;

	public String getValue() {
		return value;
	}

	private TestFxConstants(String value) {
		this.value = value;
	}
	
}
