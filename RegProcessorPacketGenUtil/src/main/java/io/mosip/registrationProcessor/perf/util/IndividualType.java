package io.mosip.registrationProcessor.perf.util;

public enum IndividualType {

	NEW_ADULT ("new_adult"),
	
	NEW_CHILD ("new_child");
	
	private String individualType;
	
	 IndividualType(String individualType){
		this.individualType =  individualType;
	}

	public String getIndividualType() {
		return individualType;
	}

	public void setIndividualType(String individualType) {
		this.individualType = individualType;
	}
	 
	 
	
}
