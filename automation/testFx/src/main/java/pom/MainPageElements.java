package pom;

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

