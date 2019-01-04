package pom;
import static util.ActionUtils.clickOn;

import lombok.Getter;
@Getter
public enum loginElements{
	USERNAME("#userId","RO Username"),
	PASSWORD("#password","RO Password"),SUBMIT("#submit","Submit"),GETOTP("#getOTP","Get OTP"),RESEND("#resend",""),
	OTPVALIDITY("#otpValidity",""),FINGERPRINT("#fingerprint",""),FINGERIMAGE("#fingerImage","");
	public String locator;
	public String value;
    private loginElements(String locator,String value) 
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
public static void login(String name,String password)
{		
	clickOn(loginElements.USERNAME.locator).write(name);
    clickOn(loginElements.PASSWORD.locator).write(password);
	clickOn(loginElements.SUBMIT.locator);
}

}

