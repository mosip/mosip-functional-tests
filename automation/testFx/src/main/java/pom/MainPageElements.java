package pom;

import lombok.Getter;


@Getter
public enum MainPageElements{
	
	NEWREGISTER("New Registration","New Registration"),
	APPROVEPACKET("Approve Packet","Approve Packet"),
	UPLOADPACKET("Upload Packet","");
	
	private String locator; 
	private String value;
	    private MainPageElements(String locator,String value) 
	    { 
	    	
	    	this.locator=locator;
	        this.value = value; 
	    } 
	
}

