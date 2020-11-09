package io.mosip.registrationProcessor.perf.service;

import java.io.File;
import java.util.Map;

import org.hibernate.Session;
import org.json.simple.JSONObject;

import io.mosip.registrationProcessor.perf.dto.DOBDto;
import io.mosip.registrationProcessor.perf.entity.Location;
import io.mosip.registrationProcessor.perf.util.IndividualType;
import io.mosip.registrationProcessor.perf.util.JSONUtil;
import io.mosip.registrationProcessor.perf.util.PropertiesUtil;
import io.mosip.registrationProcessor.perf.util.TestDataUtility;
import io.mosip.resgistrationProcessor.perf.dbaccess.RegProcPerfDaoImpl;

public class IdObjectDataFeeder {

	private String id_json_path;

	private String SOURCE = "REGISTRATION_CLIENT";
	private Session session;
	private String countryCode = "MOR";
	private Location regionObject;
	private Location provinceObject;
	private Location cityObject;
	private Location zoneObject;
	private Location postalCodeObject;
	private String process;
	private PropertiesUtil prop;

	public IdObjectDataFeeder() {
	}

	public IdObjectDataFeeder(PropertiesUtil prop, String process, Session session, String individual_type) {
		String packetPath = "";
		this.session = session;
		this.prop = prop;
		this.process = process;
		if (individual_type.equalsIgnoreCase(IndividualType.NEW_ADULT.getIndividualType())) {
			packetPath = prop.NEW_PACKET_PATH;
		}else if(individual_type.equalsIgnoreCase(IndividualType.NEW_CHILD.getIndividualType())) {
			packetPath = prop.CHILD_PACKET_PATH;
		}
		assignIDJsonPathValue(packetPath, process);
	}

	private void assignIDJsonPathValue(String packetPath, String process) {

		File[] files = new File(packetPath).listFiles();
		if (files.length == 1) {
			String packet_home = files[0].getAbsolutePath();
			String regid = files[0].getName();
			String id_packet = packet_home + "/" + SOURCE + "/" + process + "/" + regid + "_id";
			String id_json_path = id_packet + "/" + "ID.json";
			this.id_json_path = id_json_path;
		}

	}

	public Object getIDObjectElementFromIdJson(String objectKey) {
		Object object = null;
		JSONUtil util = new JSONUtil();
		JSONObject jsonObject = util.loadJsonFromFile(this.id_json_path);

		Map identityMap = (Map) jsonObject.get("identity");
		object = identityMap.get(objectKey);
		return object;
	}

	public boolean doesIDObjectElementExist(String objectKey) {
		Object object = null;
		JSONUtil util = new JSONUtil();
		JSONObject jsonObject = util.loadJsonFromFile(this.id_json_path);

		Map identityMap = (Map) jsonObject.get("identity");
		object = identityMap.get(objectKey);
		if (object != null)
			return true;
		else
			return false;
	}

	public String generateIDFieldValueString(String id) {
		TestDataUtility testDataUtil = new TestDataUtility();
		String value = "";
		if (id.equalsIgnoreCase("fullName")) {
			value = testDataUtil.generateFullName();
		} else if (id.equalsIgnoreCase("firstName") || id.equalsIgnoreCase("middleName")
				|| id.equalsIgnoreCase("lastName")) {
			value = testDataUtil.generateRandomName();
		} else if (id.equalsIgnoreCase("gender")) {
			value = testDataUtil.genrateGenderEng();
		} else if (id.equalsIgnoreCase("modeOfClaim")) {
			value = "Delivery to permanent address";
		} else if (id.equalsIgnoreCase("bloodType")) {
			value = "O";
		} else if (id.equalsIgnoreCase("referenceIdentityNumber")) {
			value = testDataUtil.getReferenceIdentityNumber();
		} else if (id.contains("addressLine")) {
			value = testDataUtil.generateAddressLine();
		} else if (id.equalsIgnoreCase("residenceStatus")) {
			value = testDataUtil.generateResidenceStatusEng();
		} else if (id.contains("region")) {
			value = regionObject.getName();
		} else if (id.contains("province")) {
			value = provinceObject.getName();
		} else if (id.contains("city")) {
			value = cityObject.getName();
		} else if (id.contains("zone")) {
			value = zoneObject.getName();
		} else if (id.contains("postalCode")) {
			value = postalCodeObject.getName();
		} else if (id.contains("dateOfBirth")) {
			if (prop.IS_CHILD_PACKET) {
				DOBDto dobDto = testDataUtil.generateChildDOB();
				value = dobDto.getDate();
			} else {
				DOBDto dobDto = testDataUtil.generateDOB();
				value = dobDto.getDate();
			}
		} else if (id.equalsIgnoreCase("email")) {
			value = "abc.zyzs@email.com";
		} else if (id.equalsIgnoreCase("phone")) {
			value = testDataUtil.generatePhoneNumber().toString();
		} else if (id.equalsIgnoreCase("maritalStatus")) {
			value = "Married";
		} else if (id.equalsIgnoreCase("suffix")) {
			value = "Mr";
		} else if (id.equalsIgnoreCase("uin")) {
			if (process.equalsIgnoreCase("new"))
				value = "null";
		}
		return value;
	}

	public void populateLocationObjects(PropertiesUtil prop, Session session2) {

		TestDataUtility testDataUtil = new TestDataUtility();
		regionObject = testDataUtil.getLocation(countryCode, 1, session);
		provinceObject = testDataUtil.getLocation(regionObject.getCode(), 2, session);
		cityObject = testDataUtil.getLocation(provinceObject.getCode(), 3, session);
		zoneObject = testDataUtil.getLocation(cityObject.getCode(), 4, session);
		postalCodeObject = testDataUtil.getLocation(zoneObject.getCode(), 5, session);
	}

	public String convertLocationEngToArabic(String locationName, int hierarchy_level, Session session) {

		String LANG_CODE_AR = "ara";
		RegProcPerfDaoImpl dao = new RegProcPerfDaoImpl();
		String result = dao.getTranslatedLocation(locationName, LANG_CODE_AR, hierarchy_level, session);
//		System.out.println(
//				LANG_CODE_AR + " of " + locationName + " at hierarchy level " + hierarchy_level + " is " + result);
		return result;
	}

	public String generateIDFieldValue(String id, String primaryValue, String language) {
		String value = "";
		TestDataUtility testDataUtil = new TestDataUtility();
		if (language.equalsIgnoreCase("ara")) {
			if (id.equalsIgnoreCase("gender")) {
				value = testDataUtil.arabicTexts.get(primaryValue);
			} else if (id.equalsIgnoreCase("residenceStatus")) {
				value = testDataUtil.arabicTexts.get(primaryValue);
			} else if (id.equalsIgnoreCase("region")) {
				value = convertLocationEngToArabic(primaryValue, 1, session);
			} else if (id.equalsIgnoreCase("province")) {
				value = convertLocationEngToArabic(primaryValue, 2, session);
			} else if (id.equalsIgnoreCase("city")) {
				value = convertLocationEngToArabic(primaryValue, 3, session);
			} else if (id.equalsIgnoreCase("zone")) {
				value = convertLocationEngToArabic(primaryValue, 4, session);
			} else {
				value = primaryValue;
			}
		}
		if (language.equalsIgnoreCase("fra")) {

		}
		return value;
	}

}
