package pom;

public enum ScanPage {
	SCANDOCUMENT("#scanNow", "");
	private String locator;
	private String value;
	private ScanPage(String locator, String value) {
		this.locator = locator;
		this.value = value;
	}
	public void setLocator(String locator) {
		this.locator = locator;
	}

	public void setValue(String value) {
		this.value = value;
	}
	public String getLocator() {
		return locator;
	}
	public String getValue() {
		return value;
	} 

}
