package io.mosip.registrationProcessor.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.*;

import org.hibernate.Session;
import org.json.simple.JSONObject;

import com.google.gson.Gson;

import io.mosip.registrationProcessor.dao.RegProcPerfDaoImpl;
import io.mosip.registrationProcessor.entity.Location;
import io.mosip.registrationProcessor.regpacket.dto.DOBDto;
import io.mosip.registrationProcessor.regpacket.dto.FieldData;
import io.mosip.registrationProcessor.regpacket.dto.Identity;
import io.mosip.registrationProcessor.regpacket.dto.PhilIdentity;
import io.mosip.registrationProcessor.regpacket.dto.PhilIdentityObject;
import io.mosip.registrationProcessor.regpacket.dto.RegProcIdDto;

public class PacketDemoDataUtil {
	

	public PacketDemoDataUtil() {

	}

	String translateText(String sourceText, String sourceLang, String destLang) {

		return sourceText;
	}
	
	public String convertLocationEngToFrench(String locationName, int hierarchy_level, Session session) {

		String LANG_CODE_FR = "eng";
		RegProcPerfDaoImpl dao = new RegProcPerfDaoImpl();

		String result = dao.getTranslatedLocation(locationName, LANG_CODE_FR, hierarchy_level, session);
//		System.out.println(
//				LANG_CODE_FR + " of " + locationName + " at hierarchy level " + hierarchy_level + " is " + result);
		return result;
	}

	public String convertLocationEngToArabic(String locationName, int hierarchy_level, Session session) {

		String LANG_CODE_AR = "eng";
		RegProcPerfDaoImpl dao = new RegProcPerfDaoImpl();
		String result = dao.getTranslatedLocation(locationName, LANG_CODE_AR, hierarchy_level, session);
//		System.out.println(
//				LANG_CODE_AR + " of " + locationName + " at hierarchy level " + hierarchy_level + " is " + result);
		return result;
	}
	
	public PhilIdentityObject modifyPhilDemographicData(PropertiesUtil prop, Session session, String idJsonPath) {
		JSONUtil jsonUtil = new JSONUtil();
		TestDataUtility testDataUtil = new TestDataUtility();
		PhilIdentityObject idObject = jsonUtil.mapJsonFileToPhilObject(idJsonPath);
		String LANG_CODE_AR = "ara";
		String LANG_CODE_ENG = "eng";
		PhilIdentity identityDto = idObject.getIdentity();
		List<FieldData> fullName = new ArrayList<>();
		String fullNameEng = testDataUtil.generateFullName_Phill();

		String[] nameSegments = fullNameEng.split(" ");
//		FieldData fullNameArData = new FieldData(LANG_CODE_AR, fullNameEng);
//		FieldData fullNameEngData = new FieldData(LANG_CODE_ENG, fullNameEng);
//		fullName.add(fullNameEngData);
//		fullName.add(fullNameArData);
//		identityDto.setFullName(fullName);

		String firstNameEng = nameSegments[0];
		String middleNameEng = nameSegments[1];
		String lastNameEng = nameSegments[2];

		List<FieldData> firstName = new ArrayList<>();
		FieldData firstNameArData = new FieldData(LANG_CODE_AR, firstNameEng);
		FieldData firstNameEngData = new FieldData(LANG_CODE_ENG, firstNameEng);
		firstName.add(firstNameEngData);
		// firstName.add(firstNameArData);
		identityDto.setFirstName(firstName);

		List<FieldData> middleName = new ArrayList<>();
		FieldData middleNameArData = new FieldData(LANG_CODE_AR, middleNameEng);
		FieldData middleNameEngData = new FieldData(LANG_CODE_ENG, middleNameEng);
		// middleName.add(middleNameArData);
		middleName.add(middleNameEngData);
		identityDto.setFirstName(middleName);

		List<FieldData> lastName = new ArrayList<>();
		FieldData lastNameArData = new FieldData(LANG_CODE_AR, lastNameEng);
		FieldData lastNameEngData = new FieldData(LANG_CODE_ENG, lastNameEng);
		// lastName.add(lastNameArData);
		lastName.add(lastNameEngData);
		identityDto.setFirstName(lastName);

		if (prop.IS_CHILD_PACKET) {
			DOBDto dobDto = testDataUtil.generateChildDOB();
			identityDto.setDateOfBirth(dobDto.getDate());
		} else {
			DOBDto dobDto = testDataUtil.generateDOB();
			identityDto.setDateOfBirth(dobDto.getDate());
		}

		List<FieldData> gender = new ArrayList<>();
		String genderEng = testDataUtil.genrateGenderEng();
		String genderAr = testDataUtil.arabicTexts.get(genderEng);
		FieldData genderEngData = new FieldData(LANG_CODE_ENG, genderEng);
		FieldData genderArData = new FieldData(LANG_CODE_AR, genderAr);
		gender.add(genderEngData);
		// gender.add(genderArData);
		identityDto.setGender(gender);

		List<FieldData> residenceStatus = new ArrayList<>();
		String resStatusEng = testDataUtil.generateResidenceStatusEng();
		String resStatusAr = testDataUtil.arabicTexts.get(resStatusEng);
		FieldData resStatusEngData = new FieldData(LANG_CODE_ENG, resStatusEng);
		FieldData resStatusArData = new FieldData(LANG_CODE_AR, resStatusAr);
		residenceStatus.add(resStatusEngData);
		// residenceStatus.add(resStatusArData);
		identityDto.setResidenceStatus(residenceStatus);

		List<FieldData> addressLine1 = new ArrayList<>();
		String addressLine1_eng = testDataUtil.generateAddressLine();
		FieldData addressLine1_EngData = new FieldData(LANG_CODE_ENG, addressLine1_eng);
		FieldData addressLine1_arData = new FieldData(LANG_CODE_AR, addressLine1_eng);
		addressLine1.add(addressLine1_EngData);
		// addressLine1.add(addressLine1_arData);
		identityDto.setAddressLine1(addressLine1);
		identityDto.setCaddressLine1(addressLine1);

		List<FieldData> addressLine2 = new ArrayList<>();
		String addressLine2_eng = testDataUtil.generateAddressLine();
		FieldData addressLine2_engData = new FieldData(LANG_CODE_ENG, addressLine2_eng);
		FieldData addressLine2_arData = new FieldData(LANG_CODE_AR, addressLine2_eng);
		addressLine2.add(addressLine2_engData);
		// addressLine2.add(addressLine2_arData);
		identityDto.setAddressLine2(addressLine2);
		identityDto.setCaddressLine2(addressLine2);

		List<FieldData> addressLine3 = new ArrayList<>();
		String addressLine3_eng = testDataUtil.generateAddressLine();
		FieldData addressLine3_engData = new FieldData(LANG_CODE_ENG, addressLine3_eng);
		FieldData addressLine3_arData = new FieldData(LANG_CODE_AR, addressLine3_eng);
		addressLine3.add(addressLine3_engData);
		// addressLine3.add(addressLine3_arData);
		identityDto.setAddressLine3(addressLine3);
		identityDto.setCaddressLine3(addressLine3);

		List<FieldData> addressLine4 = new ArrayList<>();
		String addressLine4_eng = testDataUtil.generateAddressLine();
		FieldData addressLine4_engData = new FieldData(LANG_CODE_ENG, addressLine4_eng);
		FieldData addressLine4_arData = new FieldData(LANG_CODE_AR, addressLine4_eng);
		addressLine3.add(addressLine4_engData);
		// addressLine3.add(addressLine4_arData);
		identityDto.setAddressLine4(addressLine4);
		identityDto.setCaddressLine4(addressLine4);

		List<FieldData> region = new ArrayList<>();
		String countryCode = testDataUtil.getCountryCode(session, prop);
		Object[] regionObj = testDataUtil.getLocation(countryCode, 1, session);
		String regionName = (String)regionObj[1];
		String regionValAr = convertLocationEngToArabic(regionName, 1, session);
		FieldData regionEng = new FieldData(LANG_CODE_ENG, regionName);
		FieldData regionAr = new FieldData(LANG_CODE_AR, regionValAr);
		region.add(regionEng);
		// region.add(regionAr);
		identityDto.setRegion(region);
		identityDto.setCregion(region);

		List<FieldData> province = new ArrayList<>();
		Object[] provinceObj = testDataUtil.getLocation((String)regionObj[0], 2, session);
		String provinceName = (String)provinceObj[1];
		String provinceValAr = convertLocationEngToArabic(provinceName, 2, session);
		FieldData provinceEng = new FieldData(LANG_CODE_ENG, provinceName);
		FieldData provinceAr = new FieldData(LANG_CODE_AR, provinceValAr);
		province.add(provinceEng);
		// province.add(provinceAr);
		identityDto.setProvince(province);
		identityDto.setCprovince(province);

		List<FieldData> city = new ArrayList<>();
		Object[] cityObj = testDataUtil.getLocation((String)provinceObj[0], 3, session);
		String cityName = (String)cityObj[1];
		String cityValAr = convertLocationEngToArabic(cityName, 3, session);
		FieldData cityEng = new FieldData(LANG_CODE_ENG, cityName);
		FieldData cityAr = new FieldData(LANG_CODE_AR, cityValAr);
		city.add(cityEng);
		// city.add(cityAr);
		identityDto.setCity(city);
		identityDto.setCcity(city);

		identityDto.setReferenceIdentityNumber(testDataUtil.getReferenceIdentityNumber());

		List<FieldData> zone = new ArrayList<>();
		Object[] zoneObj = testDataUtil.getLocation((String)cityObj[0], 4, session);
		String zoneName = (String)zoneObj[1];
		String zoneValAr = convertLocationEngToArabic(zoneName, 4, session);
		FieldData zoneEng = new FieldData(LANG_CODE_ENG, zoneName);
		FieldData zoneAr = new FieldData(LANG_CODE_AR, zoneValAr);
		zone.add(zoneEng);
		// zone.add(zoneAr);
		identityDto.setZone(zone);

		Object[] postalCode = testDataUtil.getLocation((String)zoneObj[0], 5, session);
		String postalCodeStr = (String) postalCode[1];
		identityDto.setPostalCode(postalCodeStr);
		identityDto.setCpostalCode(postalCodeStr);

		identityDto.setPhone(testDataUtil.generatePhoneNumber().toString());

		String[] names = fullNameEng.split(" ");
		identityDto.setEmail(testDataUtil.generateEmailAddress(names[0], names[1]));

		idObject.setIdentity(identityDto);
		return idObject;
	}

	public RegProcIdDto modifyDemographicData(PropertiesUtil prop, Session session, String idJsonPath) {

		// RegProcPerfDaoImpl dao = new RegProcPerfDaoImpl();
		TestDataUtility testDataUtility = new TestDataUtility();
		JSONUtil jsonUtil = new JSONUtil();
		TestDataUtility testDataUtil = new TestDataUtility();
		// String GOOGLE_API_KEY = "AIzaSyCm--C8RpN6FvNQtHtPKdtM20_k0R0284M";
		String GOOGLE_LANG_CODE_ENG = "en";
		String GOOGLE_LANG_CODE_FR = "fr";
		String GOOGLE_LANG_CODE_AR = "ar";
		String LANG_CODE_FR = "fra";
		String LANG_CODE_AR = "ara";
		String LANG_CODE_ENG = "eng";

		// String idJsonPath = "";
		RegProcIdDto regProcDemodto = jsonUtil.mapJsonFileToObject(idJsonPath);
		Identity identityDto = regProcDemodto.getIdentity();
		List<FieldData> fullName = new ArrayList<>();
		String fullNameEng = testDataUtil.generateFullName();
		String fullNameFr = translateText(fullNameEng, GOOGLE_LANG_CODE_ENG, GOOGLE_LANG_CODE_FR);
		String fullNameAr = translateText(fullNameEng, GOOGLE_LANG_CODE_ENG, GOOGLE_LANG_CODE_AR);
		FieldData fullNameFrData = new FieldData(LANG_CODE_FR, fullNameFr);
		FieldData fullNameArData = new FieldData(LANG_CODE_AR, fullNameAr);
		fullName.add(fullNameFrData);
		fullName.add(fullNameArData);
		identityDto.setFullName(fullName);

		DOBDto dobDto = testDataUtil.generateDOB();
		identityDto.setDateOfBirth(dobDto.getDate());
		// identityDto.setAge(dobDto.getAge());

		List<FieldData> gender = new ArrayList<>();
		String genderEng = testDataUtil.genrateGenderEng();
		String genderFr = testDataUtility.frenchTexts.get(genderEng);
		String genderAr = testDataUtility.arabicTexts.get(genderEng);
		FieldData genderFrData = new FieldData(LANG_CODE_FR, genderFr);
		FieldData genderArData = new FieldData(LANG_CODE_AR, genderAr);
		gender.add(genderFrData);
		gender.add(genderArData);
		identityDto.setGender(gender);
		Gson gson = new Gson();
		List<FieldData> residenceStatus = new ArrayList<>();
		String resStatusEng = testDataUtility.generateResidenceStatusEng();
		String resStatusFr = testDataUtility.frenchTexts.get(resStatusEng);
		String resStatusAr = testDataUtility.arabicTexts.get(resStatusEng);
		FieldData resStatusFrData = new FieldData(LANG_CODE_FR, resStatusFr);
		FieldData resStatusArData = new FieldData(LANG_CODE_AR, resStatusAr);
		residenceStatus.add(resStatusFrData);
		residenceStatus.add(resStatusArData);
		identityDto.setResidenceStatus(residenceStatus);

		List<FieldData> addressLine1 = new ArrayList<>();
		String addressLine1_eng = testDataUtility.generateAddressLine();
		String addressLine1_fr = translateText(addressLine1_eng, GOOGLE_LANG_CODE_ENG, GOOGLE_LANG_CODE_FR);
		String addressLine1_ar = translateText(addressLine1_eng, GOOGLE_LANG_CODE_ENG, GOOGLE_LANG_CODE_AR);
		FieldData addressLine1_frData = new FieldData(LANG_CODE_FR, addressLine1_fr);
		FieldData addressLine1_arData = new FieldData(LANG_CODE_AR, addressLine1_ar);
		addressLine1.add(addressLine1_frData);
		addressLine1.add(addressLine1_arData);
		identityDto.setAddressLine1(addressLine1);

		List<FieldData> addressLine2 = new ArrayList<>();
		String addressLine2_eng = testDataUtility.generateAddressLine();
		String addressLine2_fr = translateText(addressLine2_eng, GOOGLE_LANG_CODE_ENG, GOOGLE_LANG_CODE_FR);
		String addressLine2_ar = translateText(addressLine2_eng, GOOGLE_LANG_CODE_ENG, GOOGLE_LANG_CODE_AR);
		FieldData addressLine2_frData = new FieldData(LANG_CODE_FR, addressLine2_fr);
		FieldData addressLine2_arData = new FieldData(LANG_CODE_AR, addressLine2_ar);
		addressLine2.add(addressLine2_frData);
		addressLine2.add(addressLine2_arData);
		identityDto.setAddressLine2(addressLine2);

		List<FieldData> addressLine3 = new ArrayList<>();
		String addressLine3_eng = testDataUtility.generateAddressLine();
		String addressLine3_fr = translateText(addressLine3_eng, GOOGLE_LANG_CODE_ENG, GOOGLE_LANG_CODE_FR);
		String addressLine3_ar = translateText(addressLine3_eng, GOOGLE_LANG_CODE_ENG, GOOGLE_LANG_CODE_AR);
		FieldData addressLine3_frData = new FieldData(LANG_CODE_FR, addressLine3_fr);
		FieldData addressLine3_arData = new FieldData(LANG_CODE_AR, addressLine3_ar);
		addressLine3.add(addressLine3_frData);
		addressLine3.add(addressLine3_arData);
		identityDto.setAddressLine3(addressLine3);

		List<FieldData> region = new ArrayList<>();
		String countryCode = testDataUtil.getCountryCode(session, prop);
		Object[] regionObj = testDataUtil.getLocation(countryCode, 1, session);
		String regionName = (String)regionObj[1];
		String regionValFr = convertLocationEngToFrench(regionName, 1, session);

		String regionValAr = convertLocationEngToArabic(regionName, 1, session);

		FieldData regionFr = new FieldData(LANG_CODE_FR, regionValFr);
		FieldData regionAr = new FieldData(LANG_CODE_AR, regionValAr);
		region.add(regionFr);
		region.add(regionAr);
		identityDto.setRegion(region);

//				System.out.println("Region is");
//				System.out.println(gson.toJson(region));

		List<FieldData> province = new ArrayList<>();
		Object[] provinceObj = testDataUtil.getLocation((String)regionObj[0], 2, session);
		String provinceName = (String)provinceObj[1];
		String provinceValFr = convertLocationEngToFrench(provinceName, 2, session);
		String provinceValAr = convertLocationEngToArabic(provinceName, 2, session);
		FieldData provinceFr = new FieldData(LANG_CODE_FR, provinceValFr);

		FieldData provinceAr = new FieldData(LANG_CODE_AR, provinceValAr);
		province.add(provinceFr);
		province.add(provinceAr);
		identityDto.setProvince(province);

		List<FieldData> city = new ArrayList<>();
		Object[] cityObj = testDataUtil.getLocation((String)provinceObj[0], 3, session);
		String cityName = (String)cityObj[1];
		String cityValFr = convertLocationEngToFrench(cityName, 3, session);
		String cityValAr = convertLocationEngToArabic(cityName, 3, session);
		FieldData cityFr = new FieldData(LANG_CODE_FR, cityValFr);
		FieldData cityAr = new FieldData(LANG_CODE_AR, cityValAr);
		city.add(cityFr);
		city.add(cityAr);
		identityDto.setCity(city);
		identityDto.setReferenceIdentityNumber(testDataUtil.getReferenceIdentityNumber());

		List<FieldData> zone = new ArrayList<>();
		Object[] zoneObj = testDataUtil.getLocation((String)cityObj[0], 4, session);
		String localAdministrativeAuthName = (String)zoneObj[1];
		String zoneValFr = convertLocationEngToFrench(localAdministrativeAuthName, 4, session);
		String zoneValAr = convertLocationEngToArabic(localAdministrativeAuthName, 4, session);
		FieldData zoneFr = new FieldData(LANG_CODE_FR, zoneValFr);
		FieldData zoneAr = new FieldData(LANG_CODE_AR, zoneValAr);
		zone.add(zoneFr);
		zone.add(zoneAr);
		identityDto.setZone(zone);

//				System.out.println("localAdministrativeAuthority is");
//				System.out.println(gson.toJson(localAdministrativeAuthority));

		Object[] postalCode = testDataUtil.getLocation((String)zoneObj[0], 5, session);
		String postalCodeStr = (String)postalCode[1];
		identityDto.setPostalCode(postalCodeStr);
		identityDto.setPhone(testDataUtil.generatePhoneNumber().toString());
		String[] names = fullNameEng.split(" ");
//				identityDto.setEmail(testDataUtil.generateEmailAddress(names[0], names[1]));
		System.out.println(identityDto.getEmail());
		identityDto.getEmail();

		// identityDto.setCNIENumber(testDataUtil.generateCnieNumber());

		regProcDemodto.setIdentity(identityDto);
		return regProcDemodto;
	}

	@SuppressWarnings({ "rawtypes", "unchecked", "deprecation" })
	public void modifyPacketMetaInfo(String packetMetaInfoFile, String regId, String centerId, String machineId,
			String userId) {
		JSONUtil jsonUtil = new JSONUtil();
		JSONObject jsonObject = jsonUtil.loadJsonFromFile(packetMetaInfoFile);
		com.google.gson.internal.LinkedTreeMap identity = null;
		java.util.ArrayList metadata = null;
		java.util.ArrayList operationsData = null;
		if (!new File(packetMetaInfoFile).exists()) {
			System.out.println(packetMetaInfoFile + " not found");
			System.out.println("Stopping thread " + Thread.currentThread().getName());
			Thread.currentThread().stop();
		} else {
			for (Object key : jsonObject.keySet()) {
				if ("identity".equals((String) key)) {
					Object inObj = jsonObject.get(key);
					identity = (com.google.gson.internal.LinkedTreeMap) inObj;
					for (Object key1 : identity.keySet()) {
						if ("metaData".equals((String) key1)) {
							Object metadataObj = identity.get(key1);
							metadata = (java.util.ArrayList) metadataObj;

						} else if ("operationsData".equals((String) key1)) {
							Object osiDataObj = identity.get(key1);
							operationsData = (java.util.ArrayList) osiDataObj;
						}
					}

					break;
				}
			}
			int counter = 1;
			String creationDate = getCurrUTCDate();
			// System.out.println(packetMetaInfoFile);
			// System.out.println("line 425 PacketDemoDataUtil " + metadata.toString());
			for (int i = 0; i < metadata.size(); i++) {

				Map keyVal = (Map) metadata.get(i);
				if ("registrationId".equals(keyVal.get("label"))) {
					keyVal.put("value", regId);
					counter++;
				} else if ("machineId".equals(keyVal.get("label"))) {
					keyVal.put("value", machineId);
					counter++;
				} else if ("centerId".equals(keyVal.get("label"))) {
					keyVal.put("value", centerId);
					counter++;
				} else if ("creationDate".equals(keyVal.get("label"))) {
					keyVal.put("value", creationDate);
					counter++;
				} else if (counter == 5) {
					break;
				}

			}

			for (int i = 0; i < operationsData.size(); i++) {

				Map keyVal = (Map) operationsData.get(i);
				if ("officerId".equals(keyVal.get("label"))) {
					keyVal.put("value", userId);
					break;
				}

			}

			identity.put("metaData", metadata);
			identity.put("operationsData", operationsData);
			/*
			 * Edit creationDate time in identity
			 */
			
			identity.put("creationDate", creationDate);
			jsonObject.put("identity", identity);
			// System.out.println(identity);
			jsonUtil.writeJSONToFile(packetMetaInfoFile, jsonObject);
			// System.out.println(gson.toJson(jsonObject));

		}

	}
	
	private String getCurrUTCDate() {
		String timestamp = "";
		String timestampFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS";
		SimpleDateFormat f = new SimpleDateFormat(timestampFormat);
		f.setTimeZone(TimeZone.getTimeZone("UTC"));
		timestamp = f.format(new Date());
		timestamp = timestamp + "Z";
		return timestamp;
	}

	String getCurrDate() {
		String timestamp = "";
		String timestampFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS";
		Date currDate = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(currDate);
		// cal.add(Calendar.HOUR, -6);
		Date date = cal.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat(timestampFormat);
		timestamp = sdf.format(date);
		// System.out.println(timestamp);
		timestamp = timestamp + "Z";
		return timestamp;
	}

	public void writeChecksumToFile(String filepath, String checksum) throws FileNotFoundException {

		PrintWriter writer = new PrintWriter(filepath);
		writer.print(checksum);
		writer.close();
		
	}

}
