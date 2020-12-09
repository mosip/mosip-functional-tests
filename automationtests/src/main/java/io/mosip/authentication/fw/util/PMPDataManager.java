package io.mosip.authentication.fw.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import io.mosip.kernel.util.KernelAuthentication;
import io.mosip.kernel.util.KernelDataBaseAccess;
import io.restassured.response.Response;
import io.mosip.service.BaseTestCase;
public class PMPDataManager {

    KernelDataBaseAccess pmpDbAccess;
    String uploadCertiUrl = "/partnermanagement/v1/partners/partners/uploadPartnerCertificate";
    String getCertiUrl = "/idauthentication/v1/internal/getCertificate";
    KernelAuthentication authManager ;
	
    Logger logger = Logger.getLogger(PMPDataManager.class);
    /**
     *
     * @param isDataToBeInsert
     */
    public PMPDataManager(boolean isDataToBeInsert){
        pmpDbAccess = new KernelDataBaseAccess();
        if(isDataToBeInsert) {
            insertData();
            authManager = new KernelAuthentication();
            uploadPartnerCertificate();
        }
        if(!isDataToBeInsert){
            deleteData();
        }
    }

    public void uploadPartnerCertificate()
    {
    	Object certificate = getCertificate();
    	String uploadCertificateReq ="{\"id\": \"string\",\"metadata\": {},\"request\": {\"certificateData\": \"CERTIFICATE\",\"organizationName\": \"IITB\",\"partnerDomain\": \"Auth\",\"partnerId\": \"PID\",\"partnerType\": \"PMS Auth\"},\"requesttime\": \"TIMESTAMP\",\"version\": \"1.0\"}";
    	uploadCertificateReq = uploadCertificateReq.replace("PID", "1873299273").replace("TIMESTAMP", generateCurrentUTCTimeStamp());
    	JSONObject jsonReq = new JSONObject(uploadCertificateReq);
    	JSONObject request = new JSONObject(jsonReq.get("request").toString());
    	request.put("certificateData", certificate);
    	jsonReq.put("request", request);
    	Response apiResponse = RestClient.postRequestWithCookie(BaseTestCase.ApplnURI+uploadCertiUrl, jsonReq.toString(), MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, "Authorization", authManager.getTokenByRole("regproc"));
    	logger.info("response from upload certificate api:  "+apiResponse.asString());
    }
    private String generateCurrentUTCTimeStamp() {
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		return dateFormat.format(date);
	}
    
    public Object getCertificate()
    {
    	HashMap<String, String> pathParams = new HashMap<String, String>();
    	pathParams.put("applicationId", "IDA");
    	// this partner is created in the below queries and used for ekyc
    	pathParams.put("referenceId", "1873299273");
    	Response apiResponse=RestClient.getRequestWithCookieAndQueryParm(BaseTestCase.ApplnURI+getCertiUrl, pathParams, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, "Authorization", authManager.getTokenByRole("regproc"));
    	logger.info("response from get certificate api:  "+apiResponse.asString());
    	JSONObject jsonResponse = new JSONObject(apiResponse.asString());
    	return (jsonResponse.get("response")!=null)? new JSONObject(jsonResponse.get("response").toString()).get("certificate"): "no certificate in get certificate api response";
    }
    /**
     * This method deletes data.
     */
    private void deleteData() {
        pmpDbAccess.executeQuery(getDeleteQuery(),"pms");
    }

    /**
     *
     */
    private void insertData(){
        pmpDbAccess.executeQuery(getMISPInsertionQuery(),"pms");
        pmpDbAccess.executeQuery(getPolicyInsertionQuery(),"pms");
        pmpDbAccess.executeQuery(getPartnerInsertionQuery(),"pms");
    }

    /**
     *
     * @return
     */
    private String getDeleteQuery(){
        return "DELETE FROM pms.misp_license WHERE misp_id IN ('5479834598','9870862555','5479834983','5479833455');\n" +
                "DELETE FROM pms.misp WHERE id IN ('5479834598','9870862555','5479834983','5479833455');\n" +
                "DELETE FROM pms.partner_policy WHERE policy_api_key IN ('0983222','99033487029341','99033487029342','928347872931','928347872932');\n" +
                "DELETE FROM pms.partner_policy_request WHERE id IN ('0983222','99033487029341','99033487029342','928347872931','928347872932');\n" +
                "DELETE FROM pms.partner WHERE id IN ('1873299273','1873299300','1873299776','1873293764','18248239994');\n" +
                "delete from pms.partner_type where code='PMS Auth' and cr_by='Test_User';\n" +
                "DELETE FROM pms.auth_policy WHERE id IN('0983222','9903348702934','92834787293');\n" +
                "DELETE FROM pms.policy_group WHERE id IN('0983222','9903348702934','92834787293');";
        
        
    }

    /**
     *
     * @return
     */
    private String getMISPInsertionQuery(){
        return "INSERT INTO pms.misp(Id,name,address,contact_no,email_id,user_id,is_active,status_code,cr_by,cr_dtimes)\n" +
                "VALUES('5479834598','Test_01','Test_01','1234567890','test@gmail.com','Test001',true,'approved','Test_user',CURRENT_DATE),\n" +
                "('9870862555','Test_02','Test_02','1234567890','test@gmail.com','Test001',true,'approved','Test_user',CURRENT_DATE),\n" +
                "('5479834983','Test_03','Test_03','1234567890','test@gmail.com','Test001',false,'approved','Test_user',CURRENT_DATE),\n" +
                "('5479833455','Test_04','Test_04','1234567890','test@gmail.com','Test001',true,'approved','Test_user',CURRENT_DATE);\n" +
                "\n" +
                "INSERT INTO pms.misp_license(misp_id,license_key,valid_from_date,valid_to_date,is_active,cr_by,cr_dtimes)\n" +
                "VALUES('5479834598','735899345',CURRENT_DATE, CURRENT_DATE + integer '12',true,'Test_user',CURRENT_DATE),\n" +
                "('9870862555','629877671',CURRENT_DATE, CURRENT_DATE + integer '12',true,'Test_user',CURRENT_DATE),\n" +
                "('5479834983','635899234',CURRENT_DATE, CURRENT_DATE + integer '12',false,'Test_user',CURRENT_DATE),\n" +
                "('5479833455','135898653',CURRENT_DATE, CURRENT_DATE - integer '12',true,'Test_user',CURRENT_DATE);";
    }

    /**
     *
     * @return
     */
    private String getPolicyInsertionQuery(){
        String policy_file_Test_01 = "{ \"authPolicies\": [ { \"authType\": \"otp-request\", \"mandatory\": false }, { \"authType\": \"otp\", \"mandatory\": false }, { \"authType\": \"demo\", \"mandatory\": false }, { \"authType\": \"kyc\", \"mandatory\": false }, { \"authType\": \"pin\", \"mandatory\": false }, { \"authType\": \"bio\", \"authSubType\": \"FINGER\", \"mandatory\": false }, { \"authType\": \"bio\", \"authSubType\": \"IRIS\", \"mandatory\": false }, { \"authType\": \"bio\", \"authSubType\": \"FACE\", \"mandatory\": false } ], \"allowedKycAttributes\": [ { \"attributeName\": \"UIN\", \"required\": false, \"masked\": true }, { \"attributeName\": \"fullName\", \"required\": true }, { \"attributeName\": \"dateOfBirth\", \"required\": true }, { \"attributeName\": \"gender\", \"required\": true }, { \"attributeName\": \"phone\", \"required\": true }, { \"attributeName\": \"email\", \"required\": true }, { \"attributeName\": \"addressLine1\", \"required\": true }, { \"attributeName\": \"addressLine2\", \"required\": true }, { \"attributeName\": \"addressLine3\", \"required\": true }, { \"attributeName\": \"region\", \"required\": true }, { \"attributeName\": \"province\", \"required\": true }, { \"attributeName\": \"city\", \"required\": true }, { \"attributeName\": \"postalCode\", \"required\": false }, { \"attributeName\": \"photo\", \"required\": true } ] }";
        String policy_file_Test_02 = "{ \"authPolicies\": [ { \"authType\": \"otp\", \"mandatory\": false }, { \"authType\": \"pin\", \"mandatory\": false }, { \"authType\": \"bio\", \"authSubType\": \"FINGER\", \"mandatory\": true }, { \"authType\": \"bio\", \"authSubType\": \"IRIS\", \"mandatory\": false }, { \"authType\": \"bio\", \"authSubType\": \"FACE\", \"mandatory\": false } ], \"allowedKycAttributes\": [ { \"attributeName\": \"UIN\", \"required\": false, \"masked\": true }, { \"attributeName\": \"fullName\", \"required\": true }, { \"attributeName\": \"dateOfBirth\", \"required\": true }, { \"attributeName\": \"gender\", \"required\": true }, { \"attributeName\": \"phone\", \"required\": true }, { \"attributeName\": \"email\", \"required\": true }, { \"attributeName\": \"addressLine1\", \"required\": true }, { \"attributeName\": \"addressLine2\", \"required\": true }, { \"attributeName\": \"addressLine3\", \"required\": true }, { \"attributeName\": \"region\", \"required\": true }, { \"attributeName\": \"province\", \"required\": true }, { \"attributeName\": \"city\", \"required\": true }, { \"attributeName\": \"postalCode\", \"required\": false }, { \"attributeName\": \"photo\", \"required\": true } ] }";
        String policy_file_Test_03 = "{ \"authPolicies\": [ { \"authType\": \"otp\", \"mandatory\": true }, { \"authType\": \"pin\", \"mandatory\": true }, { \"authType\": \"bio\", \"authSubType\": \"FINGER\", \"mandatory\": true }, { \"authType\": \"bio\", \"authSubType\": \"IRIS\", \"mandatory\": false }, { \"authType\": \"bio\", \"authSubType\": \"FACE\", \"mandatory\": false } ], \"allowedKycAttributes\": [ { \"attributeName\": \"UIN\", \"required\": false, \"masked\": true }, { \"attributeName\": \"fullName\", \"required\": true }, { \"attributeName\": \"dateOfBirth\", \"required\": true }, { \"attributeName\": \"gender\", \"required\": true }, { \"attributeName\": \"phone\", \"required\": true }, { \"attributeName\": \"email\", \"required\": true }, { \"attributeName\": \"addressLine1\", \"required\": true }, { \"attributeName\": \"addressLine2\", \"required\": true }, { \"attributeName\": \"addressLine3\", \"required\": true }, { \"attributeName\": \"region\", \"required\": true }, { \"attributeName\": \"province\", \"required\": true }, { \"attributeName\": \"city\", \"required\": true }, { \"attributeName\": \"postalCode\", \"required\": false }, { \"attributeName\": \"photo\", \"required\": true } ] }";
        return "INSERT INTO pms.policy_group(id,name,descr,user_id,is_active,cr_by,cr_dtimes)\n" +
        "VALUES('92834787293','Test_Policy_01','Test_Policy_01','Test_001',true,'Test_User',CURRENT_DATE),\n" +
        "('9903348702934','Test_Policy_02','Test_Policy_02','Test_001',true,'Test_User',CURRENT_DATE),\n" +
        "('0983222','Test_Policy_03','Test_Policy_03','Test_001',true,'Test_User',CURRENT_DATE);\n" +
        "INSERT INTO pms.auth_policy(id,policy_group_id,name,descr,policy_file_id,policy_type,version,valid_from_date, valid_to_date,is_active,cr_by,cr_dtimes)\n" +
        "\tVALUES('92834787293','92834787293','Test_Policy_01','Test_Policy_01','{\"authTokenType\": \"partner\", \"allowedAuthTypes\": [ { \"authType\": \"otp-request\", \"mandatory\": false }, { \"authType\": \"otp\", \"mandatory\": false }, { \"authType\": \"demo\", \"mandatory\": false }, { \"authType\": \"kyc\", \"mandatory\": false }, { \"authType\": \"pin\", \"mandatory\": false }, { \"authType\": \"bio\", \"authSubType\": \"FINGER\", \"mandatory\": false }, { \"authType\": \"bio\", \"authSubType\": \"IRIS\", \"mandatory\": false }, { \"authType\": \"bio\", \"authSubType\": \"FACE\", \"mandatory\": false } ], \"allowedKycAttributes\": [ { \"attributeName\": \"UIN\", \"required\": false, \"masked\": true }, { \"attributeName\": \"fullName\", \"required\": true }, { \"attributeName\": \"dateOfBirth\", \"required\": true }, { \"attributeName\": \"gender\", \"required\": true }, { \"attributeName\": \"phone\", \"required\": true }, { \"attributeName\": \"email\", \"required\": true }, { \"attributeName\": \"addressLine1\", \"required\": true }, { \"attributeName\": \"addressLine2\", \"required\": true }, { \"attributeName\": \"addressLine3\", \"required\": true }, { \"attributeName\": \"region\", \"required\": true }, { \"attributeName\": \"province\", \"required\": true }, { \"attributeName\": \"city\", \"required\": true }, { \"attributeName\": \"postalCode\", \"required\": false }, { \"attributeName\": \"photo\", \"required\": true } ] }','Auth','1.0',CURRENT_DATE,CURRENT_DATE + integer '12',true,'Test_User',CURRENT_DATE),\n" +
        "\t('9903348702934','9903348702934','Test_Policy_02','Test_Policy_02','{ \"authTokenType\": \"partner\",\"allowedAuthTypes\": [ { \"authType\": \"otp\", \"mandatory\": false }, { \"authType\": \"pin\", \"mandatory\": false }, { \"authType\": \"bio\", \"authSubType\": \"FINGER\", \"mandatory\": true }, { \"authType\": \"bio\", \"authSubType\": \"IRIS\", \"mandatory\": false }, { \"authType\": \"bio\", \"authSubType\": \"FACE\", \"mandatory\": false } ], \"allowedKycAttributes\": [ { \"attributeName\": \"UIN\", \"required\": false, \"masked\": true }, { \"attributeName\": \"fullName\", \"required\": true }, { \"attributeName\": \"dateOfBirth\", \"required\": true }, { \"attributeName\": \"gender\", \"required\": true }, { \"attributeName\": \"phone\", \"required\": true }, { \"attributeName\": \"email\", \"required\": true }, { \"attributeName\": \"addressLine1\", \"required\": true }, { \"attributeName\": \"addressLine2\", \"required\": true }, { \"attributeName\": \"addressLine3\", \"required\": true }, { \"attributeName\": \"region\", \"required\": true }, { \"attributeName\": \"province\", \"required\": true }, { \"attributeName\": \"city\", \"required\": true }, { \"attributeName\": \"postalCode\", \"required\": false }, { \"attributeName\": \"photo\", \"required\": true } ] }','Auth','1.0',CURRENT_DATE,CURRENT_DATE + integer '12',true,'Test_User',CURRENT_DATE),\n" +
        "\t('0983222','0983222','Test_Policy_03','Test_Policy_03','{\"authTokenType\": \"partner\", \"allowedAuthTypes\": [ { \"authType\": \"pin\", \"mandatory\": false },{ \"authType\": \"kyc\", \"mandatory\": false }], \"allowedKycAttributes\": [ { \"attributeName\": \"UIN\", \"required\": false, \"masked\": true }, { \"attributeName\": \"fullName\", \"required\": true }, { \"attributeName\": \"dateOfBirth\", \"required\": true }, { \"attributeName\": \"gender\", \"required\": true }, { \"attributeName\": \"phone\", \"required\": true }, { \"attributeName\": \"email\", \"required\": true }, { \"attributeName\": \"addressLine1\", \"required\": true }, { \"attributeName\": \"addressLine2\", \"required\": true }, { \"attributeName\": \"addressLine3\", \"required\": true }, { \"attributeName\": \"region\", \"required\": true }, { \"attributeName\": \"province\", \"required\": true }, { \"attributeName\": \"city\", \"required\": true }, { \"attributeName\": \"postalCode\", \"required\": false }, { \"attributeName\": \"photo\", \"required\": true } ] }','Auth','1.0',CURRENT_DATE,CURRENT_DATE + integer '12',true,'Test_User',CURRENT_DATE);";
    }

    /**
     *
     * @return
     */
    private String getPartnerInsertionQuery(){
    	return "INSERT INTO pms.partner_type(code, partner_description, is_active, cr_by, cr_dtimes, upd_by, upd_dtimes, is_deleted, del_dtimes, is_policy_required)\n" +
                "VALUES ('PMS Auth', 'PMS Auth Desc', true, 'Test_User', CURRENT_DATE, null, null, null, null, false);\n" +                
				"\n" +
				"INSERT INTO pms.partner(id,policy_group_id,name,address,contact_no,email_id,certificate_alias,user_id,partner_type_code,approval_status,is_active,cr_by,cr_dtimes)\n" +
                "VALUES('1873299273','92834787293','TP_05','TP_05','1234567890','tp01@gmail.com',null,'Test_001','PMS Auth','Activated',true,'Test_User',CURRENT_DATE),\n" +
                "('1873299300','92834787293','TP_01','TP_01','1234567890','tp01@gmail.com',null,'Test_001','PMS Auth','Activated',true,'Test_User',CURRENT_DATE),\n" +
                "('1873299776','9903348702934','TP_02','TP_02','1234567890','tp02@gmail.com',null,'Test_001','PMS Auth','Activated',true,'Test_User',CURRENT_DATE),\n" +
                "('1873293764','9903348702934','TP_03','TP_03','1234567890','tp03@gmail.com',null,'Test_001','PMS Auth','Activated',false,'Test_User',CURRENT_DATE),\n" +
                "('18248239994','0983222','TP_04','TP_04','1234567890','tp04@gmail.com',null,'Test_001','PMS Auth','Activated',true,'Test_User',CURRENT_DATE);\n" +
                "\n" +
                "INSERT INTO pms.partner_policy_request(id,part_id,policy_id,request_datetimes,request_detail,status_code,cr_by,cr_dtimes)\n" +
                "VALUES('928347872931','1873299273','92834787293',CURRENT_DATE,'Payment','approved','Test_User',CURRENT_DATE),\n" +
                "('928347872932','1873299300','92834787293',CURRENT_DATE,'Payment','approved','Test_User',CURRENT_DATE),\n" +
                "('99033487029341','1873299776','9903348702934',CURRENT_DATE,'Payment','approved','Test_User',CURRENT_DATE),\n" +
                "('99033487029342','1873293764','9903348702934',CURRENT_DATE,'Payment','approved','Test_User',CURRENT_DATE),\n" +
                "('0983222','18248239994','0983222',CURRENT_DATE,'Payment','approved','Test_User',CURRENT_DATE);\n" +
                "\n" +
                "\n" +
                "INSERT INTO pms.partner_policy(policy_api_key,part_id,policy_id,valid_from_datetime,valid_to_datetime,is_active,cr_by,cr_dtimes)\n" +
                "VALUES('928347872931','1873299273','92834787293',CURRENT_DATE,CURRENT_DATE + integer '12',true,'Test_User',CURRENT_DATE),\n" +
                "('928347872932','1873299300','92834787293',CURRENT_DATE,CURRENT_DATE + integer '12',true,'Test_User',CURRENT_DATE),\n" +
                "('99033487029341','1873299776','9903348702934',CURRENT_DATE,CURRENT_DATE + integer '12',true,'Test_User',CURRENT_DATE),\n" +
                "('99033487029342','1873293764','9903348702934',CURRENT_DATE,CURRENT_DATE + integer '12',true,'Test_User',CURRENT_DATE),\n" +
                "('0983222','18248239994','0983222',CURRENT_DATE,CURRENT_DATE + integer '12',true,'Test_User',CURRENT_DATE);";
    }
}
