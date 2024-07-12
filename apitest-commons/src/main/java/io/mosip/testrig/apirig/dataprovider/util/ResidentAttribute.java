package io.mosip.testrig.apirig.dataprovider.util;

public enum ResidentAttribute {
	RA_Count ,
	RA_PRIMARAY_LANG,
	RA_SECONDARY_LANG,
	RA_THIRD_LANG,
	RA_FirstName,
	RA_LastName,
	RA_Country,
	RA_State,
	RA_City,
	RA_Age,
	RA_Infant,
	RA_Minor,
	RA_Adult,
	RA_Senior,
	RA_Gender,
	RA_Phone,
	RA_Photo,				//If set to faase, dont generate Photo
	RA_Iris,				//If set to false, dont generate Iris
	RA_Finger,				//if set to false dont generate finger prints
	RA_Document,
	RA_FingersList,			/* if RA_Finger is not set to false, use this list to generate only those finger prints (1111111111)  */
	RA_SKipGaurdian,		//if Set to true, and if Minor , dont generate gaurdian data
	RA_InvalidList,
	RA_MissList,
	RA_SCHEMA_VERSION
}

