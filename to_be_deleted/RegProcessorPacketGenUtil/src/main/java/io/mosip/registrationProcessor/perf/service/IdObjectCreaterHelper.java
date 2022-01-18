package io.mosip.registrationProcessor.perf.service;

import java.util.*;

import org.hibernate.Session;

import com.google.gson.Gson;

import io.mosip.registrationProcessor.perf.regPacket.dto.DocumentData;
import io.mosip.registrationProcessor.perf.regPacket.dto.FieldData;
import io.mosip.registrationProcessor.perf.schema.dto.SchemaObjectElement;
import io.mosip.registrationProcessor.perf.schema.dto.SchemaObjectList;
import io.mosip.registrationProcessor.perf.util.PropertiesUtil;

public class IdObjectCreaterHelper {

	SchemaObjectList parseIdSchemaResponse(List<Object> list) {
		SchemaObjectList obj = new SchemaObjectList();
		List<SchemaObjectElement> objectList = new ArrayList<SchemaObjectElement>();
		for (Object object : list) {
			Map map = (Map) object;
			String id = (String) map.get("id");
			String type = (String) map.get("type");
			String fieldCategory = (String) map.get("fieldCategory");
			boolean required = (Boolean) map.get("required");
			SchemaObjectElement ele = new SchemaObjectElement(id, type, fieldCategory, required);
			objectList.add(ele);
		}
		obj.setObjectList(objectList);
		return obj;
	}

	public String frameIdentityJson(List<SchemaObjectElement> objectList, PropertiesUtil prop, Session session,
			String idSchemaVersion, String process, String individual_type) {

		String[] fieldCategories = { "pvt", "kyc", "none", "evidence", "optional" };
		String[] types = { "simpleType", "documentType", "biometricsType", "number", "string" };
		Map<String, Object> identityObject = new HashMap<>();
		Map<String, Object> identityMap = new HashMap<>();
		IdObjectDataFeeder idObjectDataFeeder = new IdObjectDataFeeder(prop, process, session, individual_type);
		idObjectDataFeeder.populateLocationObjects(prop, session);

		System.out.println("objectList size before removing: " + objectList.size());
		List<SchemaObjectElement> listToRemove = new ArrayList<>();
		objectList.forEach(element -> {
			if (element.getFieldCategory().equals("evidence") || element.getFieldCategory().equals("optional")) {
				listToRemove.add(element);
			}
		});
		objectList.removeAll(listToRemove);
		System.out.println("objectList size after removing: " + objectList.size());
		String[] languages = prop.MULTI_LANG_VAL.split(",");
		for (SchemaObjectElement element : objectList) {

//			element.getId();
//			element.getType();
//			element.getRequired();

			if (element.getType().equalsIgnoreCase("documentType")) {
				Object object = idObjectDataFeeder.getIDObjectElementFromIdJson(element.getId());
				if (object != null) {

					// DocumentData documentData = (DocumentData) object;
					identityMap.put(element.getId(), object);

				}
			} else if (element.getType().equalsIgnoreCase("biometricsType")) {
				Object object = idObjectDataFeeder.getIDObjectElementFromIdJson(element.getId());
				if (object != null) {

					identityMap.put(element.getId(), object);

				}
			} else if (element.getType().equalsIgnoreCase("simpleType")) {
				// boolean elementExists =
				// idObjectDataFeeder.doesIDObjectElementExist(element.getId());
				// if (idObjectDataFeeder.doesIDObjectElementExist(element.getId())) {
				if (element.getRequired() == false && !idObjectDataFeeder.doesIDObjectElementExist(element.getId())) {

				} else {
					List<FieldData> elList = new ArrayList<FieldData>();
					if (prop.MULTI_LANG) {
						if (element.getId().contains("addressLine1")) {
							if (element.getId().equals("addressLine1")) {
								if (identityMap.containsKey("caddressLine1")) {
									identityMap.put("addressLine1", identityMap.get("caddressLine1"));
								} else {
									FieldData fieldData = new FieldData();
									fieldData.setLanguage(languages[0]);
									String fieldValue = idObjectDataFeeder.generateIDFieldValueString(element.getId());
									fieldData.setValue(fieldValue);
									elList.add(fieldData);
									FieldData fieldData1 = new FieldData();
									fieldData1.setLanguage(languages[1]);
									fieldData1.setValue(fieldValue);
									elList.add(fieldData1);
									identityMap.put("addressLine1", elList);
								}
							} else if (element.getId().equals("caddressLine1")) {
								if (identityMap.containsKey("addressLine1")) {
									identityMap.put("caddressLine1", identityMap.get("addressLine1"));
								} else {
									FieldData fieldData = new FieldData();
									fieldData.setLanguage(languages[0]);
									String fieldValue = idObjectDataFeeder.generateIDFieldValueString(element.getId());
									fieldData.setValue(fieldValue);
									elList.add(fieldData);
									FieldData fieldData1 = new FieldData();
									fieldData1.setLanguage(languages[1]);
									fieldData1.setValue(fieldValue);
									elList.add(fieldData1);
									identityMap.put("caddressLine1", elList);
								}
							}
						} else if (element.getId().contains("addressLine2")) {

							if (element.getId().equals("addressLine2")) {
								if (identityMap.containsKey("caddressLine2")) {
									identityMap.put("addressLine2", identityMap.get("caddressLine2"));
								} else {
									FieldData fieldData = new FieldData();
									fieldData.setLanguage(languages[0]);
									String fieldValue = idObjectDataFeeder.generateIDFieldValueString(element.getId());
									fieldData.setValue(fieldValue);
									elList.add(fieldData);
									FieldData fieldData1 = new FieldData();
									fieldData1.setLanguage(languages[1]);
									fieldData1.setValue(fieldValue);
									elList.add(fieldData1);
									identityMap.put("addressLine2", elList);
								}
							} else if (element.getId().equals("caddressLine2")) {
								if (identityMap.containsKey("addressLine2")) {
									identityMap.put("caddressLine2", identityMap.get("addressLine2"));
								} else {
									FieldData fieldData = new FieldData();
									fieldData.setLanguage(languages[0]);
									String fieldValue = idObjectDataFeeder.generateIDFieldValueString(element.getId());
									fieldData.setValue(fieldValue);
									elList.add(fieldData);
									FieldData fieldData1 = new FieldData();
									fieldData1.setLanguage(languages[1]);
									fieldData1.setValue(fieldValue);
									elList.add(fieldData1);
									identityMap.put("caddressLine2", elList);
								}
							}
						} else if (element.getId().contains("addressLine3")) {
							if (element.getId().equals("addressLine3")) {
								if (identityMap.containsKey("caddressLine3")) {
									identityMap.put("addressLine3", identityMap.get("caddressLine3"));
								} else {
									FieldData fieldData = new FieldData();
									fieldData.setLanguage(languages[0]);
									String fieldValue = idObjectDataFeeder.generateIDFieldValueString(element.getId());
									fieldData.setValue(fieldValue);
									elList.add(fieldData);
									FieldData fieldData1 = new FieldData();
									fieldData1.setLanguage(languages[1]);
									fieldData1.setValue(fieldValue);
									elList.add(fieldData1);
									identityMap.put("addressLine3", elList);
								}
							} else if (element.getId().equals("caddressLine3")) {
								if (identityMap.containsKey("addressLine3")) {
									identityMap.put("caddressLine3", identityMap.get("addressLine3"));
								} else {
									FieldData fieldData = new FieldData();
									fieldData.setLanguage(languages[0]);
									String fieldValue = idObjectDataFeeder.generateIDFieldValueString(element.getId());
									fieldData.setValue(fieldValue);
									elList.add(fieldData);
									FieldData fieldData1 = new FieldData();
									fieldData1.setLanguage(languages[1]);
									fieldData1.setValue(fieldValue);
									elList.add(fieldData1);
									identityMap.put("caddressLine3", elList);
								}
							}
						} else if (element.getId().contains("addressLine4")) {
							if (element.getId().equals("addressLine4")) {
								if (identityMap.containsKey("caddressLine4")) {
									identityMap.put("addressLine4", identityMap.get("caddressLine4"));
								} else {
									FieldData fieldData = new FieldData();
									fieldData.setLanguage(languages[0]);
									String fieldValue = idObjectDataFeeder.generateIDFieldValueString(element.getId());
									fieldData.setValue(fieldValue);
									elList.add(fieldData);
									FieldData fieldData1 = new FieldData();
									fieldData1.setLanguage(languages[1]);
									fieldData1.setValue(fieldValue);
									elList.add(fieldData1);
									identityMap.put("addressLine4", elList);
								}
							} else if (element.getId().equals("caddressLine4")) {
								if (identityMap.containsKey("addressLine4")) {
									identityMap.put("caddressLine4", identityMap.get("addressLine4"));
								} else {
									FieldData fieldData = new FieldData();
									fieldData.setLanguage(languages[0]);
									String fieldValue = idObjectDataFeeder.generateIDFieldValueString(element.getId());
									fieldData.setValue(fieldValue);
									elList.add(fieldData);
									FieldData fieldData1 = new FieldData();
									fieldData1.setLanguage(languages[1]);
									fieldData1.setValue(fieldValue);
									elList.add(fieldData1);
									identityMap.put("caddressLine4", elList);
								}
							}
						} else {

							FieldData fieldData = new FieldData();
							fieldData.setLanguage(languages[0]);
							String fieldValue = idObjectDataFeeder.generateIDFieldValueString(element.getId());
							fieldData.setValue(fieldValue);
							elList.add(fieldData);
							FieldData fieldData1 = new FieldData();
							String fieldValSec = idObjectDataFeeder.generateIDFieldValue(element.getId(), fieldValue, languages[1]);
							fieldData1.setLanguage(languages[1]);
							fieldData1.setValue(fieldValSec);
							elList.add(fieldData1);
							identityMap.put(element.getId(), elList);

						}

					} else {
						if (element.getId().contains("addressLine1")) {
							if (element.getId().equals("addressLine1")) {
								if (identityMap.containsKey("caddressLine1")) {
									identityMap.put("addressLine1", identityMap.get("caddressLine1"));
								} else {
									FieldData fieldData = new FieldData();
									fieldData.setLanguage("eng");
									String fieldValue = idObjectDataFeeder.generateIDFieldValueString(element.getId());
									fieldData.setValue(fieldValue);
									elList.add(fieldData);
									identityMap.put("addressLine1", elList);
								}
							} else if (element.getId().equals("caddressLine1")) {
								if (identityMap.containsKey("addressLine1")) {
									identityMap.put("caddressLine1", identityMap.get("addressLine1"));
								} else {
									FieldData fieldData = new FieldData();
									fieldData.setLanguage("eng");
									String fieldValue = idObjectDataFeeder.generateIDFieldValueString(element.getId());
									fieldData.setValue(fieldValue);
									elList.add(fieldData);
									identityMap.put("caddressLine1", elList);
								}
							}
						} else if (element.getId().contains("addressLine2")) {
							if (element.getId().equals("addressLine2")) {
								if (identityMap.containsKey("caddressLine2")) {
									identityMap.put("addressLine2", identityMap.get("caddressLine2"));
								} else {
									FieldData fieldData = new FieldData();
									fieldData.setLanguage("eng");
									String fieldValue = idObjectDataFeeder.generateIDFieldValueString(element.getId());
									fieldData.setValue(fieldValue);
									elList.add(fieldData);
									identityMap.put("addressLine2", elList);
								}
							} else if (element.getId().equals("caddressLine2")) {
								if (identityMap.containsKey("addressLine2")) {
									identityMap.put("caddressLine2", identityMap.get("addressLine2"));
								} else {
									FieldData fieldData = new FieldData();
									fieldData.setLanguage("eng");
									String fieldValue = idObjectDataFeeder.generateIDFieldValueString(element.getId());
									fieldData.setValue(fieldValue);
									elList.add(fieldData);
									identityMap.put("caddressLine2", elList);
								}
							}
						} else if (element.getId().contains("addressLine3")) {
							if (element.getId().equals("addressLine3")) {
								if (identityMap.containsKey("caddressLine3")) {
									identityMap.put("addressLine3", identityMap.get("caddressLine3"));
								} else {
									FieldData fieldData = new FieldData();
									fieldData.setLanguage("eng");
									String fieldValue = idObjectDataFeeder.generateIDFieldValueString(element.getId());
									fieldData.setValue(fieldValue);
									elList.add(fieldData);
									identityMap.put("addressLine3", elList);
								}
							} else if (element.getId().equals("caddressLine3")) {
								if (identityMap.containsKey("addressLine3")) {
									identityMap.put("caddressLine3", identityMap.get("addressLine3"));
								} else {
									FieldData fieldData = new FieldData();
									fieldData.setLanguage("eng");
									String fieldValue = idObjectDataFeeder.generateIDFieldValueString(element.getId());
									fieldData.setValue(fieldValue);
									elList.add(fieldData);
									identityMap.put("caddressLine3", elList);
								}
							}
						} else if (element.getId().contains("addressLine4")) {
							if (element.getId().equals("addressLine4")) {
								if (identityMap.containsKey("caddressLine4")) {
									identityMap.put("addressLine4", identityMap.get("caddressLine4"));
								} else {
									FieldData fieldData = new FieldData();
									fieldData.setLanguage("eng");
									String fieldValue = idObjectDataFeeder.generateIDFieldValueString(element.getId());
									fieldData.setValue(fieldValue);
									elList.add(fieldData);
									identityMap.put("addressLine4", elList);
								}
							} else if (element.getId().equals("caddressLine4")) {
								if (identityMap.containsKey("addressLine4")) {
									identityMap.put("caddressLine4", identityMap.get("addressLine4"));
								} else {
									FieldData fieldData = new FieldData();
									fieldData.setLanguage("eng");
									String fieldValue = idObjectDataFeeder.generateIDFieldValueString(element.getId());
									fieldData.setValue(fieldValue);
									elList.add(fieldData);
									identityMap.put("caddressLine4", elList);
								}
							}
						} else {

							FieldData fieldData = new FieldData();
							fieldData.setLanguage("eng");
							String fieldValue = idObjectDataFeeder.generateIDFieldValueString(element.getId());
							fieldData.setValue(fieldValue);
							elList.add(fieldData);
							identityMap.put(element.getId(), elList);

						}
					}
				}

				// }

			} else if (element.getType().equalsIgnoreCase("string")) {

				if (idObjectDataFeeder.doesIDObjectElementExist(element.getId())) {
					identityMap.put(element.getId(), idObjectDataFeeder.generateIDFieldValueString(element.getId()));
				}

			} else if (element.getType().equalsIgnoreCase("number")) {
				if (element.getId().equalsIgnoreCase("IDSchemaVersion")) {
					identityMap.put(element.getId(), Float.parseFloat(idSchemaVersion));
				}
			}

		}

		Gson gson = new Gson();
		identityObject.put("identity", identityMap);
		System.out.println(gson.toJson(identityObject));
		return gson.toJson(identityObject);

	}

}
