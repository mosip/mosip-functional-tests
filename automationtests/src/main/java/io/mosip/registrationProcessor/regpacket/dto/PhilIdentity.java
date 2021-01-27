package io.mosip.registrationProcessor.regpacket.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhilIdentity {

	private DocumentData proofOfAddress;
	private List<FieldData> gender;
	private List<FieldData> city;	
	private List<FieldData> modeOfClaim;
	private List<FieldData> suffix;
	private List<FieldData> cregion;
	private String postalCode;
	private List<FieldData> cprovince;
	private String bloodType;
	private String referenceIdentityNumber;
	private BiometricData individualBiometrics;
	private BiometricData parentOrGuardianBiometrics;
	private List<FieldData> province;
	private List<FieldData> caddressLine4;
	private List<FieldData> zone;
	private List<FieldData> caddressLine3;
	private List<FieldData> caddressLine2;
	private List<FieldData> caddressLine1;
	private List<FieldData> addressLine1;
	private List<FieldData> addressLine2;
	private List<FieldData> residenceStatus;	
	private List<FieldData> addressLine3;
	private List<FieldData> addressLine4;
	private String email;
	private List<FieldData> czone;
	private List<FieldData> firstName;
	private List<FieldData> middleName;
	private List<FieldData> lastName;
	private String dateOfBirth;	
	private String cpostalCode;
	private List<FieldData> ccity;
	private DocumentData proofOfIdentity;
	private Float IDSchemaVersion;
	private String phone;
	private String UIN;
	private List<FieldData> region;
	private List<FieldData> maritalStatus;	
	private DocumentData proofOfRelationship;
	//private String CNIENumber;

}
