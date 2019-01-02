package pom;

import lombok.Getter;


@Getter
public enum MainPageElements{
	
	NEWREGISTER("#newRegistration",""),
	APPROVEPACKET("#approvePacket",""),
	UPLOADPACKET("#uploadPacket",""),
	LOGOUT("#logOuticon",""),LOGOUTVAL("#LogOutVal","");
	
	private String locator; 
	private String value;
	   // enum constructor - cannot be public or protected 
	    private MainPageElements(String locator,String value) 
	    { 
	    	
	    	this.locator=locator;
	        this.value = value; 
	    } 
	
}

