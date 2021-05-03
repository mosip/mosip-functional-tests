package io.mosip.device.register.constants;

public interface Urls {
	public  static final String USER_AUTHENTICATE="/v1/authmanager/authenticate/useridPwd";
	public  static final String CREATE_POLICY_GROUP="/v1/policymanager/policies/group/new";
	public  static final String GET_POLICY_GROUP="/v1/policymanager/policies/group/";
	public  static final String PARTNER_SELF_REGISTRATION="/v1/partnermanager/partners";
	public  static final String RETREIVE_PARTNERS="/v1/partnermanager/partners?partnerType=";
	public  static final String REGISTER_DEVICE="/v1/partnermanager/devicedetail";
	public  static final String UPLOAD_CA_CERTIFICATE="/v1/partnermanager/partners/certificate/ca/upload";
	public  static final String UPLOAD_Partner_CERTIFICATE="/v1/partnermanager/partners/certificate/upload";
	public static final String APPROVE_BIOMETRICS="/v1/partnermanager/securebiometricinterface";
	public static final String SIGNED_REGISTER_BIOMETRICS="/v1/partnermanager/registereddevices";
}
