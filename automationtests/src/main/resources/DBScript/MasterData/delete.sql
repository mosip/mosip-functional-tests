delete from master.blocklisted_words where word='dumbo6';
delete from master.blocklisted_words where word='dumbo7';
delete from master.blocklisted_words where cr_by ='110005';
delete from master.machine_master where cr_by ='110005';
delete from master.machine_type where cr_by ='110005';
delete from master.machine_spec where cr_by ='110005';
delete FROM master.machine_type where cr_by ='110005';
delete FROM master.gender where cr_by ='110005';
delete FROM master.device_master where cr_by ='110005';
delete FROM master.device_spec where cr_by ='110005';
delete FROM master.device_type where cr_by ='110005';
delete FROM master.loc_holiday where cr_by ='110005';
delete FROM master.reg_center_type where cr_by ='110005';
delete FROM master.registration_center where cr_by ='110005';
delete from master.device_type where cr_by ='110005';
delete from master.doc_type where cr_by ='110005';
delete from master.doc_category where cr_by ='110005';
delete from master.loc_holiday where holiday_name in('AutoTest user Ara','AutoTest user Ara');
delete from master.reg_center_type where code in('ALT-3','ALT-5');
delete from master.registration_center where id='10000';
delete from master.device_type where code in ('GST3','GST4');
delete from master.doc_type where code in ('TestDocType0010','TestDocType0020');
delete from master.doc_category where code in ('DocTestCode123','DocTestCode321');
delete FROM master.location where cr_by ='110005';
delete from master.template_type where code='Test-info-Template-auto';
update master.location set is_active='true', is_deleted='false' where code='10114';
update master.template set is_active='true', is_deleted='false' where id='1101';
delete from master.location where code in('TST123','IND');
delete from master.template where id='445566777';

delete from master.valid_document where cr_by='110005';
delete from master.user_detail where cr_by='110005';
delete from master.template_type where cr_by='110005';
delete from master.template_file_format where cr_by='110005';
delete from master.reason_list where cr_by='110005';
delete from master.reason_category where cr_by='110005';
delete from master.language where cr_by='110005';
delete from master.identity_schema where cr_by='110005';

delete from master.biometric_attribute where cr_by='110005';

delete from master.biometric_type where cr_by='110005';
delete from master.appl_form_type where cr_by='110005';
delete from master.id_type where cr_by='110005';
delete from master.dynamic_field where cr_by='110005';