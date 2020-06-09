package io.mosip.authentication.fw.util;

import io.mosip.kernel.util.KernelDataBaseAccess;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class PMPDataManager {

    KernelDataBaseAccess pmpDbAccess;

    /**
     *
     * @param isDataToBeInsert
     */
    public PMPDataManager(boolean isDataToBeInsert){
        pmpDbAccess = new KernelDataBaseAccess();
        if(isDataToBeInsert) {
            insertData();
        }
        if(!isDataToBeInsert){
            deleteData();
        }
    }

    /**
     * This method deletes data.
     */
    private void deleteData() {
        pmpDbAccess.executeQuery(getDeleteQuery(),"pmp");
    }

    /**
     *
     */
    private void insertData(){
        pmpDbAccess.executeQuery(getMISPInsertionQuery(),"pmp");
        pmpDbAccess.executeQuery(getPolicyInsertionQuery(),"pmp");
        pmpDbAccess.executeQuery(getPartnerInsertionQuery(),"pmp");
    }

    /**
     *
     * @return
     */
    private String getDeleteQuery(){
        return "DELETE FROM pmp.misp_license WHERE misp_id IN ('5479834598','9870862555','5479834983','5479833455');\n" +
                "DELETE FROM pmp.misp WHERE id IN ('5479834598','9870862555','5479834983','5479833455');\n" +
                "DELETE FROM pmp.partner_policy WHERE policy_api_key IN ('0983222','99033487029341','99033487029342','928347872931','928347872932');\n" +
                "DELETE FROM pmp.partner_policy_request WHERE id IN ('0983222','99033487029341','99033487029342','928347872931','928347872932');\n" +
                "DELETE FROM pmp.partner WHERE id IN ('1873299273','1873299300','1873299776','1873293764','18248239994');\n" +
                "DELETE FROM pmp.auth_policy WHERE id IN('0983222','9903348702934','92834787293');\n" +
                "DELETE FROM pmp.policy_group WHERE id IN('0983222','9903348702934','92834787293');";
    }

    /**
     *
     * @return
     */
    private String getMISPInsertionQuery(){
        return "INSERT INTO pmp.misp(Id,name,address,contact_no,email_id,user_id,is_active,status_code,cr_by,cr_dtimes)\n" +
                "VALUES('5479834598','Test_01','Test_01','1234567890','test@gmail.com','Test001',true,'approved','Test_user',CURRENT_DATE),\n" +
                "('9870862555','Test_02','Test_02','1234567890','test@gmail.com','Test001',true,'approved','Test_user',CURRENT_DATE),\n" +
                "('5479834983','Test_03','Test_03','1234567890','test@gmail.com','Test001',false,'approved','Test_user',CURRENT_DATE),\n" +
                "('5479833455','Test_04','Test_04','1234567890','test@gmail.com','Test001',true,'approved','Test_user',CURRENT_DATE);\n" +
                "\n" +
                "INSERT INTO pmp.misp_license(misp_id,license_key,valid_from_date,valid_to_date,is_active,cr_by,cr_dtimes)\n" +
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
           return "INSERT INTO pmp.policy_group(id,name,descr,user_id,is_active,cr_by,cr_dtimes)\n" +
                   "VALUES('92834787293','Test_Policy_01','Test_Policy_01','Test_001',true,'Test_User',CURRENT_DATE),\n" +
                   "('9903348702934','Test_Policy_02','Test_Policy_02','Test_001',true,'Test_User',CURRENT_DATE),\n" +
                   "('0983222','Test_Policy_03','Test_Policy_03','Test_001',true,'Test_User',CURRENT_DATE);\n" +
                   "INSERT INTO pmp.auth_policy(id,policy_group_id,name,descr,policy_file_id,is_active,cr_by,cr_dtimes)\n" +
                   "\tVALUES('92834787293','92834787293','Test_Policy_01','Test_Policy_01','{ \"authPolicies\": [ { \"authType\": \"otp-request\", \"mandatory\": false }, { \"authType\": \"otp\", \"mandatory\": false }, { \"authType\": \"demo\", \"mandatory\": false }, { \"authType\": \"kyc\", \"mandatory\": false }, { \"authType\": \"pin\", \"mandatory\": false }, { \"authType\": \"bio\", \"authSubType\": \"FINGER\", \"mandatory\": false }, { \"authType\": \"bio\", \"authSubType\": \"IRIS\", \"mandatory\": false }, { \"authType\": \"bio\", \"authSubType\": \"FACE\", \"mandatory\": false } ], \"allowedKycAttributes\": [ { \"attributeName\": \"UIN\", \"required\": false, \"masked\": true }, { \"attributeName\": \"fullName\", \"required\": true }, { \"attributeName\": \"dateOfBirth\", \"required\": true }, { \"attributeName\": \"gender\", \"required\": true }, { \"attributeName\": \"phone\", \"required\": true }, { \"attributeName\": \"email\", \"required\": true }, { \"attributeName\": \"addressLine1\", \"required\": true }, { \"attributeName\": \"addressLine2\", \"required\": true }, { \"attributeName\": \"addressLine3\", \"required\": true }, { \"attributeName\": \"region\", \"required\": true }, { \"attributeName\": \"province\", \"required\": true }, { \"attributeName\": \"city\", \"required\": true }, { \"attributeName\": \"postalCode\", \"required\": false }, { \"attributeName\": \"photo\", \"required\": true } ] }',true,'Test_User',CURRENT_DATE),\n" +
                   "\t('9903348702934','9903348702934','Test_Policy_02','Test_Policy_02','{ \"authPolicies\": [ { \"authType\": \"otp\", \"mandatory\": false }, { \"authType\": \"pin\", \"mandatory\": false }, { \"authType\": \"bio\", \"authSubType\": \"FINGER\", \"mandatory\": true }, { \"authType\": \"bio\", \"authSubType\": \"IRIS\", \"mandatory\": false }, { \"authType\": \"bio\", \"authSubType\": \"FACE\", \"mandatory\": false } ], \"allowedKycAttributes\": [ { \"attributeName\": \"UIN\", \"required\": false, \"masked\": true }, { \"attributeName\": \"fullName\", \"required\": true }, { \"attributeName\": \"dateOfBirth\", \"required\": true }, { \"attributeName\": \"gender\", \"required\": true }, { \"attributeName\": \"phone\", \"required\": true }, { \"attributeName\": \"email\", \"required\": true }, { \"attributeName\": \"addressLine1\", \"required\": true }, { \"attributeName\": \"addressLine2\", \"required\": true }, { \"attributeName\": \"addressLine3\", \"required\": true }, { \"attributeName\": \"region\", \"required\": true }, { \"attributeName\": \"province\", \"required\": true }, { \"attributeName\": \"city\", \"required\": true }, { \"attributeName\": \"postalCode\", \"required\": false }, { \"attributeName\": \"photo\", \"required\": true } ] }',true,'Test_User',CURRENT_DATE),\n" +
                   "\t('0983222','0983222','Test_Policy_03','Test_Policy_03','{ \"authPolicies\": [ { \"authType\": \"otp\", \"mandatory\": true }, { \"authType\": \"pin\", \"mandatory\": true }, { \"authType\": \"bio\", \"authSubType\": \"FINGER\", \"mandatory\": true }, { \"authType\": \"bio\", \"authSubType\": \"IRIS\", \"mandatory\": false }, { \"authType\": \"bio\", \"authSubType\": \"FACE\", \"mandatory\": false } ], \"allowedKycAttributes\": [ { \"attributeName\": \"UIN\", \"required\": false, \"masked\": true }, { \"attributeName\": \"fullName\", \"required\": true }, { \"attributeName\": \"dateOfBirth\", \"required\": true }, { \"attributeName\": \"gender\", \"required\": true }, { \"attributeName\": \"phone\", \"required\": true }, { \"attributeName\": \"email\", \"required\": true }, { \"attributeName\": \"addressLine1\", \"required\": true }, { \"attributeName\": \"addressLine2\", \"required\": true }, { \"attributeName\": \"addressLine3\", \"required\": true }, { \"attributeName\": \"region\", \"required\": true }, { \"attributeName\": \"province\", \"required\": true }, { \"attributeName\": \"city\", \"required\": true }, { \"attributeName\": \"postalCode\", \"required\": false }, { \"attributeName\": \"photo\", \"required\": true } ] }',true,'Test_User',CURRENT_DATE);";
    }

    /**
     *
     * @return
     */
    private String getPartnerInsertionQuery(){
        return "INSERT INTO pmp.partner(id,policy_group_id,name,address,contact_no,email_id,user_id,is_active,cr_by,cr_dtimes)\n" +
                "VALUES('1873299273','92834787293','TP_05','TP_05','1234567890','tp01@gmail.com','Test_001',true,'Test_User',CURRENT_DATE),\n" +
                "('1873299300','92834787293','TP_01','TP_01','1234567890','tp01@gmail.com','Test_001',true,'Test_User',CURRENT_DATE),\n" +
                "('1873299776','9903348702934','TP_02','TP_02','1234567890','tp02@gmail.com','Test_001',true,'Test_User',CURRENT_DATE),\n" +
                "('1873293764','9903348702934','TP_03','TP_03','1234567890','tp03@gmail.com','Test_001',false,'Test_User',CURRENT_DATE),\n" +
                "('18248239994','0983222','TP_04','TP_04','1234567890','tp04@gmail.com','Test_001',true,'Test_User',CURRENT_DATE);\n" +
                "\n" +
                "INSERT INTO pmp.partner_policy_request(id,part_id,policy_id,request_datetimes,request_detail,status_code,cr_by,cr_dtimes)\n" +
                "VALUES('928347872931','1873299273','92834787293',CURRENT_DATE,'Payment','approved','Test_User',CURRENT_DATE),\n" +
                "('928347872932','1873299300','92834787293',CURRENT_DATE,'Payment','approved','Test_User',CURRENT_DATE),\n" +
                "('99033487029341','1873299776','9903348702934',CURRENT_DATE,'Payment','approved','Test_User',CURRENT_DATE),\n" +
                "('99033487029342','1873293764','9903348702934',CURRENT_DATE,'Payment','approved','Test_User',CURRENT_DATE),\n" +
                "('0983222','18248239994','0983222',CURRENT_DATE,'Payment','approved','Test_User',CURRENT_DATE);\n" +
                "\n" +
                "\n" +
                "INSERT INTO pmp.partner_policy(policy_api_key,part_id,policy_id,valid_from_datetime,valid_to_datetime,is_active,cr_by,cr_dtimes)\n" +
                "VALUES('928347872931','1873299273','92834787293',CURRENT_DATE,CURRENT_DATE + integer '12',true,'Test_User',CURRENT_DATE),\n" +
                "('928347872932','1873299300','92834787293',CURRENT_DATE,CURRENT_DATE + integer '12',true,'Test_User',CURRENT_DATE),\n" +
                "('99033487029341','1873299776','9903348702934',CURRENT_DATE,CURRENT_DATE + integer '12',true,'Test_User',CURRENT_DATE),\n" +
                "('99033487029342','1873293764','9903348702934',CURRENT_DATE,CURRENT_DATE + integer '12',false,'Test_User',CURRENT_DATE),\n" +
                "('0983222','18248239994','0983222',CURRENT_DATE,CURRENT_DATE + integer '12',true,'Test_User',CURRENT_DATE);";
    }
}
