package pom;

import lombok.Getter;

@Getter
public enum ScanPage {
	SCANDOCUMENT("#scanGenericButton", "");
	private String locator;
	private String value;
	private ScanPage(String locator, String value) {
		this.locator = locator;
		this.value = value;
	}
	
}
