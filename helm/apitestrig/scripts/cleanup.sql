-- Below should be delete queries for the Auth Partner - pms-111999:

-- From PMS DB:
\c mosip_pms postgres
delete FROM pms.oidc_client where policy_id in (select id from pms.auth_policy where cr_by='pms-111999');
delete FROM pms.partner_policy where policy_id in (select id from pms.auth_policy where cr_by='pms-111999');
delete FROM pms.partner_policy_request where policy_id in (select id from pms.auth_policy where cr_by='pms-111999');
delete from pms.auth_policy where cr_by='pms-111999';
delete from pms.policy_group where cr_by='pms-111999';
delete FROM pms.partner_policy where cr_by='pms-111999';
delete FROM pms.partner_policy_request where cr_by='pms-111999';
delete from pms.partner where cr_by='pms-111999';
delete FROM pms.user_details where cr_by='pms-111999';

-- From Esignet DB:
\c mosip_esignet postgres
delete FROM esignet.client_detail where rp_id='pms-111999';

-- From IDA DB:
\c mosip_ida postgres
delete FROM ida.oidc_client_data where partner_id='pms-111999';
delete FROM ida.partner_data where partner_id = 'pms-111999';

-- From KeyManager DB:
\c mosip_keymgr postgres
delete from keymgr.key_alias where app_id = 'PARTNER' AND ref_id = 'pms-111999';

-- Below should be delete queries for the Device Partner - pms-111998:

-- From PMS DB:
\c mosip_pms postgres
delete from pms.device_detail_sbi where dprovider_id='pms-111998';
delete from pms.device_detail where dprovider_id='pms-111998';
delete from pms.secure_biometric_interface where provider_id='pms-111998';
delete from pms.partner where cr_by='pms-111998';
delete FROM pms.user_details where cr_by='pms-111998';

-- From KeyManager DB:
\c mosip_keymgr postgres
delete from keymgr.key_alias where app_id = 'PARTNER' AND ref_id = 'pms-111998';

-- From IDA DB:
\c mosip_ida postgres
delete FROM ida.partner_data where partner_id = 'pms-111998';

-- Below should be delete queries for the FTM Partner - pms-111888:
-- From PMS DB:
\c mosip_pms postgres
delete from pms.ftp_chip_detail where foundational_trust_provider_id='pms-111888';
delete from pms.partner where cr_by='pms-111888';
delete FROM pms.user_details where cr_by='pms-111888';

-- From IDA DB:
\c mosip_ida postgres
delete FROM ida.partner_data where partner_id = 'pms-111888';

-- Below should be delete queries for the Partner ADMIN - pms-111777:
-- From PMS DB:
\c mosip_pms postgres
delete from pms.auth_policy where cr_by='pms-111777';
delete from pms.policy_group where cr_by='pms-111777';
delete from pms.partner where cr_by='pms-111777';
delete FROM pms.user_details where cr_by='pms-111777';

-- From KeyManager DB:
\c mosip_keymgr postgres
delete from keymgr.key_alias where app_id = 'PARTNER' AND ref_id = 'pms-111777';

-- Below should be delete all uploaded certs:
-- From IDA DB:
\c mosip_ida postgres
delete FROM ida.ca_cert_store where cert_subject ='CN=PMS_API_CA,OU=PMS_API_CA,O=PMS_API_CA,L=PN,ST=MH,C=IN';
delete FROM ida.ca_cert_store where cert_subject ='CN=PMS_API_SUB_CA,OU=PMS_API_SUB_CA,O=PMS_API_SUB_CA,L=PN,ST=MH,C=IN';
delete from ida.ca_cert_store where cert_subject ='CN=PMS_API_ABC_ORG,OU=PMS_API_ABC_ORG,O=PMS_API_ABC_ORG,L=PN,ST=MH,C=IN';

-- From KeyManager DB:
\c mosip_keymgr postgres
delete FROM keymgr.ca_cert_store where cert_subject ='CN=PMS_API_CA,OU=PMS_API_CA,O=PMS_API_CA,L=PN,ST=MH,C=IN';
delete FROM keymgr.ca_cert_store where cert_subject ='CN=PMS_API_SUB_CA,OU=PMS_API_SUB_CA,O=PMS_API_SUB_CA,L=PN,ST=MH,C=IN';
delete from keymgr.ca_cert_store where cert_subject ='CN=PMS_API_ABC_ORG,OU=PMS_API_ABC_ORG,O=PMS_API_ABC_ORG,L=PN,ST=MH,C=IN';
delete from keymgr.partner_cert_store where cert_subject ='CN=PMS_API_ABC_ORG,OU=PMS_API_ABC_ORG,O=PMS_API_ABC_ORG,L=PN,ST=MH,C=IN';
