package pom;

import lombok.Getter;


@Getter
public enum MainPageElements{
	
	NEWREGISTER("#newRegistration",""),
	APPROVEPACKET("#approvePacket",""),
	UPLOADPACKET("#uploadPacket","");
	
	private String locator; 
	private String value;
	    private MainPageElements(String locator,String value) 
	    { 
	    	
	    	this.locator=locator;
	        this.value = value; 
	    } 
	
}

