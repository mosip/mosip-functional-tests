package io.mosip.device.register.constants;

public interface Urls {
	public  static final String USER_AUTHENTICATE="/v1/authmanager/authenticate/useridPwd";
	public  static final String CREATE_POLICY_GROUP="/partnermanagement/v1/policies/policies/policyGroup";
	public  static final String GET_POLICY_GROUP="/partnermanagement/v1/pmpartners/pmpartners/policyname/";
	public  static final String PARTNER_SELF_REGISTRATION="/partnermanagement/v1/partners/partners";
	public  static final String RETREIVE_PARTNERS="/partnermanagement/v1/pmpartners/pmpartners";
	public  static final String REGISTER_DEVICE="/partnermanagement/v1/partners/devicedetail";
	public  static final String UPLOAD_CA_CERTIFICATE="/partnermanagement/v1/partners/partners/uploadCACertificate";
	public  static final String UPLOAD_Partner_CERTIFICATE="/partnermanagement/v1/partners/partners/uploadPartnerCertificate";
	public static final String APPROVE_BIOMETRICS="/partnermanagement/v1/partners/securebiometricinterface";
	public static final String SIGNED_REGISTER_BIOMETRICS="/partnermanagement/v1/partners/registereddevices";
}
