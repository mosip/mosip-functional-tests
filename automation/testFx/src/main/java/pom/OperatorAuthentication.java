package pom;

import lombok.Getter;
import static util.ActionUtils.*;
@Getter
public enum OperatorAuthentication {
	PASSWORD("#password",""),
	SUBMIT("#submit","");
private String locator;
private String value;
private OperatorAuthentication(String locator, String value) {
	this.locator = locator;
	this.value = value;
}
public static void operatorAuthentication(String password) {
	clickOn(PASSWORD.getLocator()).write(password);
	clickOn(SUBMIT.getLocator());
}
}
