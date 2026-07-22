package io.mosip.testrig.apirig.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import io.mosip.testrig.apirig.testrunner.BaseTestCase;

/**
 * Opt-in builder for the AddIdentity / UpdateIdentity request body from EVERY field in the live
 * IdSchema — required and optional — unlike {@link AdminTestUtil#modifySchemaGenerateHbs(boolean)},
 * which covers only the required set. Nothing wires this in by default; call it explicitly where a
 * module needs the optional fields present (e.g. idrepo, which tests handle behaviour on optional
 * handle fields). Modules that create an identity only as a prerequisite should keep using the
 * required-only builder.
 *
 * <p>Templates are cached per run (deterministic per schema). Optional handle fields use
 * {@code $HANDLEVALUE:<field>$} tokens; resolve them per request via
 * {@link AdminTestUtil#resolveSchemaHandleValueTokens(String)} to keep handle values unique.
 */
public class SchemaBasedIdentityTemplateBuilder {

	private static final Logger logger = Logger.getLogger(SchemaBasedIdentityTemplateBuilder.class);

	private static String addTemplateCache = null;
	private static String addTemplateV2Cache = null;
	private static String updateTemplateCache = null;
	private static String updateTemplateV2Cache = null;

	private SchemaBasedIdentityTemplateBuilder() {
	}

	public static String buildAddIdentityTemplate() {
		if (addTemplateCache == null) {
			addTemplateCache = AdminTestUtil.finalizeSchemaGenericFields(buildTemplate(false));
		}
		return addTemplateCache;
	}

	public static String buildAddIdentityTemplateV2() {
		if (addTemplateV2Cache == null) {
			addTemplateV2Cache = AdminTestUtil.finalizeSchemaGenericFields(wrapV2(buildTemplate(false)));
		}
		return addTemplateV2Cache;
	}

	public static String buildUpdateIdentityTemplate() {
		// Update flow emits bare {{field}} for generic fields (no sentinels), so finalize is a no-op.
		if (updateTemplateCache == null) {
			updateTemplateCache = buildTemplate(true);
		}
		return updateTemplateCache;
	}

	public static String buildUpdateIdentityTemplateV2() {
		if (updateTemplateV2Cache == null) {
			updateTemplateV2Cache = wrapV2(buildTemplate(true));
		}
		return updateTemplateV2Cache;
	}

	private static String wrapV2(String hbs) {
		JSONObject requestJson = new JSONObject(hbs);
		requestJson.getJSONObject("request").put("verifiedAttributes", "$$VERIFIED_ATTRIBUTES$$");
		return requestJson.toString().replace("\"$$VERIFIED_ATTRIBUTES$$\"", "{{{json verifiedAttributes}}}");
	}

	private static String buildTemplate(boolean isUpdate) {
		JSONObject props = AdminTestUtil.getIdentitySchemaProperties();
		double schemaVersion = Double.parseDouble(AdminTestUtil.generateLatestSchemaVersion());

		String phoneFieldName = AdminTestUtil.getValueFromAuthActuator("json-property", "phone_number")
				.replaceAll("\\[\"|\"]", "");
		String emailFieldName = AdminTestUtil.getValueFromAuthActuator("json-property", "emailId")
				.replaceAll("\\[\"|\"]", "");

		JSONObject requestJson = new JSONObject();
		requestJson.put("id", "{{id}}");
		requestJson.put("request", new JSONObject());
		requestJson.getJSONObject("request").put("status", isUpdate ? "{{status}}" : "ACTIVATED");
		requestJson.getJSONObject("request").put("registrationId", "{{registrationId}}");

		JSONObject identityJson = new JSONObject();
		identityJson.put("UIN", "{{UIN}}");
		JSONArray handleTag = new JSONArray().put("handle");
		List<String> selectedHandles = new ArrayList<>();
		boolean hasBiometrics = false;

		for (String fieldName : props.keySet()) {
			if (fieldName.equals("UIN") || fieldName.equals("selectedHandles")) {
				continue;
			}
			JSONObject fieldDef = props.getJSONObject(fieldName);
			String ref = fieldDef.optString("$ref", "");
			String fieldType = fieldDef.optString("type", "string");
			boolean isHandle = fieldDef.optBoolean("handle", false);

			if (fieldName.equals("IDSchemaVersion")) {
				identityJson.put(fieldName, schemaVersion);

			} else if (fieldName.equals("preferredLang")) {
				identityJson.put(fieldName, "$1STLANG$");

			} else if (fieldName.equals("packetCreatedOn")) {
				// System audit timestamp; $TIMESTAMP$ is filled per request by inputJsonKeyWordHandeler.
				identityJson.put(fieldName, "$TIMESTAMP$");

			} else if (!ref.isEmpty() && ref.contains("simpleType")) {
				if (isHandle) {
					selectedHandles.add(fieldName);
				}
				JSONArray arr = new JSONArray();
				for (String lang : BaseTestCase.getLanguageList()) {
					if (lang != null && !lang.isEmpty()) {
						JSONObject val = new JSONObject();
						val.put("language", lang);
						val.put("value", "TEST_" + fieldName + lang);
						arr.put(val);
					}
				}
				identityJson.put(fieldName, arr);

			} else if (!ref.isEmpty() && ref.contains("documentType")) {
				if (!isUpdate) { // update does not re-upload documents
					JSONObject doc = new JSONObject();
					doc.put("format", "txt");
					doc.put("type", "DOC001");
					doc.put("value", "fileReferenceID");
					identityJson.put(fieldName, doc);
				}

			} else if (!ref.isEmpty() && ref.contains("biometricsType")) {
				if (!isUpdate) { // update does not re-upload biometrics
					JSONObject bio = new JSONObject();
					bio.put("format", "cbeff");
					bio.put("version", 1.0);
					bio.put("value", "fileReferenceID");
					identityJson.put(fieldName, bio);
					hasBiometrics = true;
				}

			} else if (!ref.isEmpty() && ref.contains("hashType")) {
				if (StringUtils.isBlank(AdminTestUtil.addIdentityPassword)
						|| StringUtils.isBlank(AdminTestUtil.addIdentitySalt)) {
					AdminTestUtil.getPasswordSaltFromKeyManager(AdminTestUtil.PASSWORD_FOR_ADDIDENTITY_AND_REGISTRATION);
				}
				JSONObject hash = new JSONObject();
				hash.put("hash", AdminTestUtil.addIdentityPassword);
				hash.put("salt", AdminTestUtil.addIdentitySalt);
				identityJson.put(fieldName, hash);

			} else if (fieldName.equals(phoneFieldName) || fieldName.equals(emailFieldName)) {
				// Handled together and shape-driven (string vs array is per-schema). Phone key is always {{phone}}.
				String placeholder = fieldName.equals(phoneFieldName) ? "{{phone}}" : "{{" + fieldName + "}}";
				if ("array".equals(fieldType)) {
					JSONObject entry = new JSONObject();
					entry.put("value", placeholder);
					if (isHandle) {
						entry.put("tags", handleTag);
					}
					identityJson.put(fieldName, new JSONArray().put(entry));
				} else {
					identityJson.put(fieldName, placeholder);
				}
				if (isHandle) {
					selectedHandles.add(fieldName);
				}

			} else if ("array".equals(fieldType) && isHandle) {
				JSONObject entry = new JSONObject();
				entry.put("value", "$HANDLEVALUE:" + fieldName + "$");
				entry.put("tags", handleTag);
				identityJson.put(fieldName, new JSONArray().put(entry));
				selectedHandles.add(fieldName);

			} else if (isHandle) {
				identityJson.put(fieldName, "$HANDLEVALUE:" + fieldName + "$");
				selectedHandles.add(fieldName);

			} else if (isUpdate) {
				// Update flow: bare {{fieldName}} (renders empty if the YAML omits it).
				identityJson.put(fieldName, "{{" + fieldName + "}}");

			} else {
				// Create flow: sentinel -> presence-aware {{schemaFieldValue "field"}} helper (see AdminTestUtil).
				identityJson.put(fieldName,
						AdminTestUtil.SCHEMA_FIELD_SENTINEL_PREFIX + fieldName + AdminTestUtil.SCHEMA_FIELD_SENTINEL_SUFFIX);
			}
		}

		if (!selectedHandles.isEmpty()) {
			identityJson.put("selectedHandles", selectedHandles);
			AdminTestUtil.setfoundHandlesInIdSchema(true);
		}

		requestJson.getJSONObject("request").put("identity", identityJson);

		if (hasBiometrics) {
			JSONArray requestDocArray = new JSONArray();
			JSONObject docJson = new JSONObject();
			docJson.put("value", "{{value}}");
			docJson.put("category", "{{category}}");
			requestDocArray.put(docJson);
			requestJson.getJSONObject("request").put("documents", requestDocArray);
		}

		requestJson.put("requesttime", "{{requesttime}}");
		requestJson.put("version", "{{version}}");

		logger.info((isUpdate ? "Full-schema UpdateIdentity" : "Full-schema AddIdentity") + " template: " + requestJson);
		return requestJson.toString();
	}
}
