package pom;

import static util.ActionUtils.clickOn;

import lombok.Getter;
@Getter
public enum OperatorAuthentication {
	PASSWORD("#password",""),
	SUBMIT("Submit","");
private String locator;
private String value;
private OperatorAuthentication(String locator, String value) {
	this.locator = locator;
	this.value = value;
}
public void setLocator(String locator) {
	this.locator = locator;
}

public void setValue(String value) {
	this.value = value;
} 

public static void operatorAuthentication(String password) {
	clickOn(PASSWORD.getLocator()).write(password);
	clickOn(SUBMIT.getLocator());
}
}
