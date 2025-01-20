package io.mosip.testrig.apirig.dataprovider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
//import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
//import java.time.LocalDateTime;
//import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
//import java.util.Arrays;
import java.util.Base64;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jamesmurty.utils.XMLBuilder;
//import java.util.Date;

import io.mosip.kernel.biometrics.commons.CbeffValidator;
import io.mosip.kernel.biometrics.entities.BIR;
import io.mosip.mock.sbi.test.CentralizedMockSBI;
import io.mosip.testrig.apirig.dataprovider.mds.MDSClient;
import io.mosip.testrig.apirig.dataprovider.mds.MDSClientInterface;
import io.mosip.testrig.apirig.dataprovider.models.BioModality;
import io.mosip.testrig.apirig.dataprovider.models.BiometricDataModel;
import io.mosip.testrig.apirig.dataprovider.models.IrisDataModel;
import io.mosip.testrig.apirig.dataprovider.models.ResidentBiometricModel;
import io.mosip.testrig.apirig.dataprovider.models.mds.MDSDevice;
import io.mosip.testrig.apirig.dataprovider.models.mds.MDSDeviceCaptureModel;
import io.mosip.testrig.apirig.dataprovider.models.mds.MDSRCaptureModel;
import io.mosip.testrig.apirig.dataprovider.util.CommonUtil;
import io.mosip.testrig.apirig.dataprovider.util.DataProviderConstants;
import io.mosip.testrig.apirig.testrunner.BaseTestCase;
import io.mosip.testrig.apirig.utils.RestClient;
import io.restassured.response.Response;

public class BiometricDataProvider {

	public static HashMap<String, Integer> portmap = new HashMap();
	private static final Logger logger = Logger.getLogger(BiometricDataProvider.class);

	// String constants
	private static final String XMLNS = "xmlns";
	private static final String MAJOR = "Major";
	private static final String MINOR = "Minor";
	private static final String CBEFFVERSION = "CBEFFVersion";
	private static final String VERSION = "Version";
	private static final String FALSE = "false";
	private static final String BDBINFO = "BDBInfo";
	private static final String BIRINFO = "BIRInfo";
	private static final String INTEGRITY = "Integrity";
	private static final String FORMAT = "Format";
	private static final String CREATIONDATE = "CreationDate";
	private static final String ORGANIZATION = "Organization";
	private static final String MOSIP = "Mosip";
	private static final String SUBTYPE = "Subtype";
	private static final String PURPOSE = "Purpose";
	private static final String LEVEL = "Level";
	private static final String SHA_256 = "SHA-256";
	private static final String ENROLL = "Enroll";
	private static final String QUALITY = "Quality";
	private static final String ALGORITHM = "Algorithm";
	private static final String SCORE = "Score";
	private static final String EXCEPTION = "EXCEPTION";
	private static final String OTHERS = "others";
	private static final String ENTRY = "entry";
	private static final String RETRIES = "RETRIES";
	private static final String SDK_SCORE = "SDK_SCORE";
	private static final String FORCE_CAPTURED = "FORCE_CAPTURED";
	private static final String PAYLOAD = "PAYLOAD";
	private static final String SPEC_VERSION = "SPEC_VERSION";
	public static final String AUTHCERTSPATH = "authCertsPath";
	private static final String LEFTEYE = "leftEye";
	private static final String RIGHTEYE = "rightEye";
	private static final String RIGHT = "Right";
	private static final String MOUNTPATH = "mountPath";
	private static final String DIRPATH = "dirPath ";
	private static final String SCENARIO = "scenario";
	private static HashMap<String, String> biometricDataMap = new HashMap();
	
	
	public static void addToBiometricMap(String key, String value) {
		biometricDataMap.put(key, value);
	}
	
	public static String getFromBiometricMap(String key) {
		return biometricDataMap.get(key);
	}
	
	public static void main(String[] args) {
		try {
			generateBiometricTestData("Registration");
		} catch (Exception e) {
			logger.error("Failed to generate biometric test data " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static String toBase64Url(String input) {
        // Encode the input string to bytes
        byte[] byteData = input.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        // Base64 URL-safe encode the byte data
        String base64UrlStr = Base64.getUrlEncoder().withoutPadding().encodeToString(byteData);
        return base64UrlStr;
    }
	
	public static Boolean generateBiometricTestData(String mdsMode) throws Exception {
		ResidentBiometricModel resident = new ResidentBiometricModel();
		String cbeff = null;
		MDSRCaptureModel capture = BiometricDataProvider.regenBiometricViaMDS(resident, mdsMode, 70);
		if (capture == null) {
			logger.error("Failed to generate biometric via mds");
			return false;
		}
		String strCBeff = toCBEFFFromCapture(Arrays.asList(DataProviderConstants.schemaNames), capture, null, false);
		
//		boolean isValid = CbeffValidator.validateXML(getBirs(strCBeff));
		
		
		String encodedCBeff = toBase64Url(strCBeff);
		
		
		
//		byte[] decodedBytes = Base64.getUrlDecoder().decode(encodedCBeff);
//        String decodedCBeff = new String(decodedBytes, StandardCharsets.UTF_8);
		
		
//		isValid = CbeffValidator.validateXML(getBirs(decodedCBeff));
		
		String strCBeffWithoutFace = toCBEFFFromCapture(Arrays.asList(DataProviderConstants.schemaNames), capture, null,
				true);
		
		String encodedCBeffWithoutFace = toBase64Url(strCBeffWithoutFace);
		
		logger.info("strCBeff = " + strCBeff);
//		
//		logger.info("strCBeffWithoutFace = " + strCBeffWithoutFace);
//		
		logger.info("encodedCBeff = " + encodedCBeff);

		addToBiometricMap("BioValue", encodedCBeff);

		addToBiometricMap("BioValueWithoutFace", encodedCBeffWithoutFace);

		return true;
	}
	
//	private static BIR getBirs(String cbeff) throws Exception {
//			try {
//				BIR birInfo = CbeffValidator.getBIRFromXML(cbeff.getBytes(StandardCharsets.UTF_8));
//				birInfo.setBirs(birInfo.getBirs().stream().filter(b -> b.getBdb() != null).toList());
//				return birInfo;
//			} catch (Exception ex) {
//				logger.error("Error while validating CBEFF", ex);
//			}
//		}
	
	private static BIR getBirs(String cbeff) throws Exception {
	    try {
	        BIR birInfo = CbeffValidator.getBIRFromXML(cbeff.getBytes(StandardCharsets.UTF_8));
	        birInfo.setBirs(birInfo.getBirs().stream()
	            .filter(b -> b.getBdb() != null)
	            .collect(Collectors.toList()));
	        return birInfo;
	    } catch (Exception ex) {
	        logger.error("Error while validating CBEFF", ex);
	        throw ex;  // Make sure to rethrow the exception to adhere to the method's signature
	    }
	}

	static String buildBirIris(String irisInfo, String irisName, String jtwSign, String payload, String qualityScore, String exception) throws ParserConfigurationException,
			FactoryConfigurationError, TransformerException, FileNotFoundException {
		String today = CommonUtil.getUTCDateTime(null);
		XMLBuilder builder = XMLBuilder.create("BIR").a(XMLNS, "http://standards.iso.org/iso-iec/19785/-3/ed-2/")
				.e(VERSION).e(MAJOR).t("1").up().e(MINOR).t("1").up().up().e(CBEFFVERSION).e(MAJOR).t("1").up().e(MINOR)
				.t("1").up().up().e(BIRINFO).e(INTEGRITY).t(FALSE).up().up().e(BDBINFO).e(FORMAT).e(ORGANIZATION)
				.t(MOSIP).up().e("Type").t("9").up().up().e(CREATIONDATE).t(today).up().e("Type").t("Iris").up()
				.e(SUBTYPE).t(irisName).up().e(LEVEL).t("Raw").up().e(PURPOSE).t(ENROLL).up().e(QUALITY).e(ALGORITHM)
				.e(ORGANIZATION).t("HMAC").up().e("Type").t(SHA_256).up().up().e(SCORE).t((int) Math.round(Double.parseDouble(qualityScore)) + "").up().up().up()
				.e("BDB").t(getBase64EncodedStringFromBase64URL(irisInfo)).up().up();
		if (jtwSign != null && payload != null) {
			jtwSign = Base64.getEncoder().encodeToString(jtwSign.getBytes());
			builder.e("SB").t(jtwSign).up().

					e(OTHERS).e(ENTRY).a("key", EXCEPTION).t(exception).up().e(ENTRY).a("key", RETRIES).t("1").up()
					.e(ENTRY).a("key", SDK_SCORE).t("0.0").up().e(ENTRY).a("key", FORCE_CAPTURED).t(FALSE).up().e(ENTRY)
					.a("key", PAYLOAD).t(payload).up().e(ENTRY).a("key", SPEC_VERSION).t("0.9.5").up().up();
		}
		return builder.asString(null);
	}
	
	static String getBase64EncodedStringFromBase64URL(String input) {
		byte[] decodedBytes = Base64.getUrlDecoder().decode(input);
//      String decodedBdb = new String(decodedBytes, StandardCharsets.UTF_8);

		return Base64.getEncoder().encodeToString(decodedBytes);
	}

	static String buildBirFinger(String fingerInfo, String fingerName, String jtwSign, String payload,
			String qualityScore, String exception) throws ParserConfigurationException, FactoryConfigurationError,
			TransformerException, FileNotFoundException {
		String today = CommonUtil.getUTCDateTime(null);
		XMLBuilder builder = null;
		String bdbKey = "BDB";
		
		builder = XMLBuilder.create("BIR").a(XMLNS, "http://standards.iso.org/iso-iec/19785/-3/ed-2/").e(VERSION)
				.e(MAJOR).t("1").up().e(MINOR).t("1").up().up().e(CBEFFVERSION).e(MAJOR).t("1").up().e(MINOR).t("1")
				.up().up().e(BIRINFO).e(INTEGRITY).t(FALSE).up().up().e(BDBINFO).e(FORMAT).e(ORGANIZATION).t(MOSIP).up()
				.e("Type").t("7").up().up().e(CREATIONDATE).t(today).up().e("Type").t("Finger").up().e(SUBTYPE)
				.t(fingerName).up().e(LEVEL).t("Raw").up().e(PURPOSE).t(ENROLL).up().e(QUALITY).e(ALGORITHM)
				.e(ORGANIZATION).t("HMAC").up().e("Type").t(SHA_256).up().up().e(SCORE).t((int) Math.round(Double.parseDouble(qualityScore)) + "").up().up().up()
				.e(bdbKey).t(getBase64EncodedStringFromBase64URL(fingerInfo)).up().up();
		if (jtwSign != null && payload != null) {
			jtwSign = Base64.getEncoder().encodeToString(jtwSign.getBytes());
			builder.e("SB").t(jtwSign).up().

					e(OTHERS).e(ENTRY).a("key", EXCEPTION).t(exception).up().e(ENTRY).a("key", RETRIES).t("1").up()
					.e(ENTRY).a("key", SDK_SCORE).t("0.0").up().e(ENTRY).a("key", FORCE_CAPTURED).t(FALSE).up().e(ENTRY)
					.a("key", PAYLOAD).t(payload).up().e(ENTRY).a("key", SPEC_VERSION).t("0.9.5").up().up();
		}
		return builder.asString(null);
	}

	static String buildBirFace(String faceInfo, String jtwSign, String payload, String qualityScore, String exception)
			throws ParserConfigurationException, FactoryConfigurationError, TransformerException,
			FileNotFoundException {
		String today = CommonUtil.getUTCDateTime(null);
		XMLBuilder builder = XMLBuilder.create("BIR").a(XMLNS, "http://standards.iso.org/iso-iec/19785/-3/ed-2/")
				.e(VERSION).e(MAJOR).t("1").up().e(MINOR).t("1").up().up().e(CBEFFVERSION).e(MAJOR).t("1").up().e(MINOR)
				.t("1").up().up().e(BIRINFO).e(INTEGRITY).t(FALSE).up().up().e(BDBINFO).e(FORMAT).e(ORGANIZATION)
				.t(MOSIP).up().e("Type").t("8").up().up().e(CREATIONDATE).t(today).up().e("Type").t("Face").up()
				.e(SUBTYPE).t("").up().e(LEVEL).t("Raw").up().e(PURPOSE).t(ENROLL).up().e(QUALITY).e(ALGORITHM)
				.e(ORGANIZATION).t("HMAC").up().e("Type").t(SHA_256).up().up().e(SCORE).t((int) Math.round(Double.parseDouble(qualityScore)) + "").up().up().up()
				.e("BDB").t(getBase64EncodedStringFromBase64URL(faceInfo)).up().up();
		if (jtwSign != null && payload != null) {
			jtwSign = Base64.getEncoder().encodeToString(jtwSign.getBytes());
			builder.e("SB").t(jtwSign).up().

					e(OTHERS).e(ENTRY).a("key", EXCEPTION).t(exception).up().e(ENTRY).a("key", RETRIES).t("1").up()
					.e(ENTRY).a("key", SDK_SCORE).t("0.0").up().e(ENTRY).a("key", FORCE_CAPTURED).t(FALSE).up().e(ENTRY)
					.a("key", PAYLOAD).t(payload).up().e(ENTRY).a("key", SPEC_VERSION).t("0.9.5").up().up();

		}
		return builder.asString(null);
	}

	static String buildBirExceptionPhoto(String faceInfo, String jtwSign, String payload, String qualityScore,
			String exception) throws ParserConfigurationException, FactoryConfigurationError, TransformerException,
			FileNotFoundException {
		String today = CommonUtil.getUTCDateTime(null);
		XMLBuilder builder = XMLBuilder.create("BIR").a(XMLNS, "http://standards.iso.org/iso-iec/19785/-3/ed-2/")
				.e(VERSION).e(MAJOR).t("1").up().e(MINOR).t("1").up().up().e(CBEFFVERSION).e(MAJOR).t("1").up().e(MINOR)
				.t("1").up().up().e(BIRINFO).e(INTEGRITY).t(FALSE).up().up().e(BDBINFO).e(FORMAT).e(ORGANIZATION)
				.t(MOSIP).up().e("Type").t("8").up().up().e(CREATIONDATE).t(today).up().e("Type").t("ExceptionPhoto")
				.up().e(SUBTYPE).t("").up().e(LEVEL).t("Raw").up().e(PURPOSE).t(ENROLL).up().e(QUALITY).e(ALGORITHM)
				.e(ORGANIZATION).t("HMAC").up().e("Type").t(SHA_256).up().up().e(SCORE).t((int) Math.round(Double.parseDouble(qualityScore)) + "").up().up().up()
				.e("BDB").t(faceInfo).up().up();
		if (jtwSign != null && payload != null) {
			jtwSign = Base64.getEncoder().encodeToString(jtwSign.getBytes());
			builder.e("SB").t(jtwSign).up().

					e(OTHERS).e(ENTRY).a("key", EXCEPTION).t(exception).up().e(ENTRY).a("key", RETRIES).t("1").up()
					.e(ENTRY).a("key", SDK_SCORE).t("0.0").up().e(ENTRY).a("key", FORCE_CAPTURED).t(FALSE).up().e(ENTRY)
					.a("key", PAYLOAD).t(payload).up().e(ENTRY).a("key", SPEC_VERSION).t("0.9.5").up().up();

		}
		return builder.asString(null);
	}

	public static List<BioModality> getModalitiesByType(List<BioModality> bioExceptions, String type) {
		List<BioModality> lst = new ArrayList<BioModality>();

		for (BioModality m : bioExceptions) {
			if (m.getType().equalsIgnoreCase(type)) {
				lst.add(m);
			}
		}
		return lst;
	}
	
	public static void setMDSscore(long port, String type, int qualityScore) {

		try {
			String requestBody = "{\"type\":\"" + type + "\",\"qualityScore\":\"" + qualityScore
					+ "\",\"fromIso\":false}";

			Response response = RestClient.post("http://127.0.0.1:" + port + "/admin/score", requestBody);
			logger.info(response.toString());

			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonNode = objectMapper.readTree(response.getBody().asString());
			// Extract the errorInfo field
			String errorInfo = jsonNode.get("errorInfo").asText();
			logger.info("errorInfo: " + errorInfo);
		} catch (Exception e) {
			logger.error("Issue with the Rest Assured MOCKMDS Score Request{}", e);
		}
	}
	
    public static String getKeysDirPath(String certsDir, String moduleName) {
      	String certsTargetDir = System.getProperty("java.io.tmpdir")+ File.separator + "AUTHCERTS";
      	
      	if (System.getProperty("os.name").toLowerCase().contains("windows") == false) {
      		certsTargetDir = "/home/mosip/authcerts";
      	}
      	
      	String certsModuleName = "IDA";
  		
  		
  		if (certsDir != null && certsDir.length() != 0){
      	   certsTargetDir = certsDir;
  		}
  		
  		if (moduleName != null && moduleName.length() != 0){
  		    certsModuleName = moduleName;
  		}
  		return certsTargetDir + File.separator + certsModuleName + "-IDA-" + System.getProperty("env.user")+ ".mosip.net";
  }

	public static MDSRCaptureModel regenBiometricViaMDS(ResidentBiometricModel resident, String mdsMode, int qualityScore)
			throws Exception {
		BiometricDataModel biodata = null;
		MDSRCaptureModel capture = null;
		String contextKey = "default";
		MDSClientInterface mds = null;
		String val;
		String mdsprofilePath = null;
		String profileName = null;
		int port = 0;
		List<String> filteredAttribs = resident.getFilteredBioAttribtures();
		List<BioModality> bioExceptions = resident.getBioExceptions();
		List<String> bioexceptionlist = new ArrayList<String>();

		try {
			
			Path p12path = Paths.get(getKeysDirPath("", BaseTestCase.certsForModule));
			
//			Path p12path = Paths.get("C:\\Users\\NANDHU~1\\AppData\\Local\\Temp\\AUTHCERTS\\IDA-api-internal.qa-inji.mosip.net");
			
			int maxLoopCount = 20;

			while (maxLoopCount > 0) {
				try {
					port = CentralizedMockSBI.startSBI(contextKey, mdsMode, "Biometric Device", p12path.toString());
				} catch (Exception e) {
					logger.error("Exception occured during startSBI " + contextKey, e);
				}
				if (port != 0) {
					logger.info(contextKey + ", Found the port " + contextKey + " port number is: " + port);
					break;
				}

				maxLoopCount--;
			}

			if (port == 0) {
				logger.error("Unable to find the port " + contextKey + " port number is: " + port);
				return null;
			}

			portmap.put("port_", port);

			mds = new MDSClient(port);
			mds.setProfile("Default", port, contextKey);

			// Change mockmds quality score

			HashMap<String, Integer> portAsPerKey = BiometricDataProvider.portmap;
			setMDSscore(portAsPerKey.get("port_"), "Biometric Device", qualityScore);
			logger.info(contextKey + ", mds score is changed to : " + qualityScore);
			
		} catch (Throwable t) {
			logger.error(" Port issue " + contextKey, t);
			t.getStackTrace();
			return null;
		}

		// Step 1 : Face get capture
		try {
//			if ((filteredAttribs != null && filteredAttribs.contains("face")) && biodata.getRawFaceData() != null) {

			List<MDSDevice> faceDevices = mds.getRegDeviceInfo(DataProviderConstants.MDS_DEVICE_TYPE_FACE);
			MDSDevice faceDevice = faceDevices.get(0);

			capture = mds.captureFromRegDevice(faceDevice, capture, DataProviderConstants.MDS_DEVICE_TYPE_FACE, null,
					60, faceDevice.getDeviceSubId().get(0), port, contextKey, null);
//			}
		}

		catch (Throwable t) {
			logger.error(" Face get capture fail" + contextKey, t);
			t.getStackTrace();
			return null;
		}

		// Step 2 : IRIS get capture
		try {

			List<MDSDevice> irisDevices = mds.getRegDeviceInfo(DataProviderConstants.MDS_DEVICE_TYPE_IRIS);
			MDSDevice irisDevice = irisDevices.get(0);

			capture = mds.captureFromRegDevice(irisDevice, capture, DataProviderConstants.MDS_DEVICE_TYPE_IRIS, null,
					60, irisDevice.getDeviceSubId().get(0), port, contextKey, null);

			if (irisDevice.getDeviceSubId().size() > 1) {

				capture = mds.captureFromRegDevice(irisDevice, capture, DataProviderConstants.MDS_DEVICE_TYPE_IRIS,
						null, 60, irisDevice.getDeviceSubId().get(1), port, contextKey, null);
			} 
			
		}
		catch (Throwable t) {
			logger.error(" IRIS get capture  fail" + contextKey, t);
			t.getStackTrace();
			return null;
		}

		// Step 3 : Finger get capture
		try {
			List<MDSDevice> fingerDevices = mds.getRegDeviceInfo(DataProviderConstants.MDS_DEVICE_TYPE_FINGER);
			MDSDevice fingerDevice = fingerDevices.get(0);

			for (int i = 0; i < fingerDevice.getDeviceSubId().size(); i++) {
				capture = mds.captureFromRegDevice(fingerDevice, capture, DataProviderConstants.MDS_DEVICE_TYPE_FINGER,
						null, 60, fingerDevice.getDeviceSubId().get(i), port, contextKey, null);
			}
		} catch (Throwable t) {
			logger.error("Finger get capture fail" + contextKey, t);
			t.getStackTrace();
			return null;
		}

		CentralizedMockSBI.stopSBI(contextKey);
		
		return capture;
	}

	public static String toCBEFFFromCapture(List<String> bioFilter, MDSRCaptureModel capture,
			List<BioModality> exceptionlist, boolean skipFace) throws Exception {

		String retXml = "";

		String mosipVersion = "1.2.1-SNAPSHOT";

		XMLBuilder builder = XMLBuilder.create("BIR").a(XMLNS, "http://standards.iso.org/iso-iec/19785/-3/ed-2/")
				.e(BIRINFO).e(INTEGRITY).t(FALSE).up().up();

		builder.getDocument().setXmlStandalone(true);

		List<String> bioSubType = new ArrayList<>();

		// Step 1: convert finger print
		try {
			List<MDSDeviceCaptureModel> lstFingerData = capture.getLstBiometrics()
					.get(DataProviderConstants.MDS_DEVICE_TYPE_FINGER);

			builder = xmlbuilderFinger(bioFilter, lstFingerData, bioSubType, builder, exceptionlist);

			if (exceptionlist != null && !exceptionlist.isEmpty()) {
				builder = xmlbuilderFingerExep(bioFilter, exceptionlist, bioSubType, builder);
			}
		} catch (Exception e) {
			logger.error("xmlbuilderFinger failed" + e.getMessage());
		}

		// Step 2: Add Face

		if (skipFace == false) {
			try {
				if (bioFilter.contains("face")) {

					List<MDSDeviceCaptureModel> lstFaceData = capture.getLstBiometrics()
							.get(DataProviderConstants.MDS_DEVICE_TYPE_FACE);
					bioSubType.add("face");

					addToBiometricMap("FaceBioValue", getBase64EncodedStringFromBase64URL(lstFaceData.get(0).getBioValue()));

					String faceXml = buildBirFace(lstFaceData.get(0).getBioValue(), lstFaceData.get(0).getSb(),
							lstFaceData.get(0).getPayload(), lstFaceData.get(0).getQualityScore(), FALSE);
					builder = builder.importXMLBuilder(XMLBuilder.parse(faceXml));

				}
			} catch (Exception e) {
				logger.error("buildBirFace failed");
				logger.error(e.getMessage());
			}
		}

		// Step 3: Add IRIS
		try {
			List<MDSDeviceCaptureModel> lstIrisData = capture.getLstBiometrics()
					.get(DataProviderConstants.MDS_DEVICE_TYPE_IRIS);

			builder = xmlbuilderIris(bioFilter, lstIrisData, bioSubType, builder, exceptionlist);

			if (exceptionlist != null && !exceptionlist.isEmpty()) {
				builder = xmlbuilderIrisExcep(bioFilter, exceptionlist, bioSubType, builder);
			}
		} catch (Exception e) {
			logger.error("xmlbuilderIris failed");
			logger.error(e.getMessage());
		}

		// Step 4: Add Face as an Exception photo

		try {
			if (exceptionlist != null && !exceptionlist.isEmpty()) {
				List<MDSDeviceCaptureModel> lstFaceData = capture.getLstBiometrics().get(EXCEPTION);
				bioSubType.add("exceptionphoto");
				String faceXml = buildBirExceptionPhoto(lstFaceData.get(1).getBioValue(), lstFaceData.get(1).getSb(),
						lstFaceData.get(1).getPayload(), lstFaceData.get(1).getQualityScore(), FALSE);
				builder = builder.importXMLBuilder(XMLBuilder.parse(faceXml));
			}
		} catch (Exception e) {
			logger.error("buildBirExceptionPhoto failed");
			logger.error(e.getMessage());
		}

//		// Print builder
//
//		if (mosipVersion != null && mosipVersion.startsWith("1.2") && !bioSubType.isEmpty()) {
//			builder.e(OTHERS).e("Key").t("CONFIGURED").up().e("Value")
//					.t(bioSubType.toString().substring(1, bioSubType.toString().length() - 1)).up().up();
//		}

		retXml = builder.asString(null);
		return retXml;
	}

	private static XMLBuilder xmlbuilderIris(List<String> bioFilter, List<MDSDeviceCaptureModel> lstIrisData,
			List<String> bioSubType, XMLBuilder builder, List<BioModality> exceptionlst)

	{
		List<String> listWithoutExceptions = bioFilter;
		if (exceptionlst != null && !exceptionlst.isEmpty()) {
			List<String> exceptions = exceptionlst.stream().map(BioModality::getSubType).collect(Collectors.toList());
			List<String> schemaName = new ArrayList<String>();
			for (String ex : exceptions) {
				schemaName.add(getschemaName(ex));
			}
			listWithoutExceptions = bioFilter.stream().filter(bioAttribute -> !schemaName.contains(bioAttribute))
					.collect(Collectors.toList());
		}
//		RestClient.logInfo(contextKey, "withoutExceptionList is: " + listWithoutExceptions);

		try {
			if (lstIrisData != null) {
				String irisXml = "";
				for (MDSDeviceCaptureModel cm : lstIrisData) {

					if (listWithoutExceptions.contains(LEFTEYE) && cm.getBioSubType().equals("Left")) {
						irisXml = buildBirIris(cm.getBioValue(), "Left", cm.getSb(), cm.getPayload(),
								cm.getQualityScore(), FALSE);
						builder = builder.importXMLBuilder(XMLBuilder.parse(irisXml));
						bioSubType.add("Left");
						addToBiometricMap("LeftIrisBioValue", getBase64EncodedStringFromBase64URL(cm.getBioValue()));
					}
					if (listWithoutExceptions.contains(RIGHTEYE) && cm.getBioSubType().equals(RIGHT)) {

						irisXml = buildBirIris(cm.getBioValue(), RIGHT, cm.getSb(), cm.getPayload(),
								cm.getQualityScore(), FALSE);
						builder = builder.importXMLBuilder(XMLBuilder.parse(irisXml));
						bioSubType.add(RIGHT);
						addToBiometricMap("RightIrisBioValue", getBase64EncodedStringFromBase64URL(cm.getBioValue()));
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		return builder;
	}

	private static XMLBuilder xmlbuilderIrisExcep(List<String> bioFilter, List<BioModality> lstIrisData,
			List<String> bioSubType, XMLBuilder builder) {
		try {
			if (lstIrisData != null) {
				for (BioModality finger : lstIrisData) {
					if (!finger.getType().equalsIgnoreCase("Iris"))
						continue;

					String strFingerXml = buildBirIris(finger.getType(), finger.getSubType(),
							Arrays.toString(new byte[0]), "", "0", "true");
					XMLBuilder fbuilder = XMLBuilder.parse(strFingerXml);
					builder = builder.importXMLBuilder(fbuilder);
				}

			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		return builder;
	}

	private static String getschemaName(String name) {
		// First check if it falls in all modaities
		for (int i = 0; i < 13; i++) {
			String displayFingerName = DataProviderConstants.displayFullName[i];
			if (displayFingerName.equalsIgnoreCase(name) == true)
				return DataProviderConstants.schemaNames[i];
		}

		// Other wise just return
		return name;
	}

	private static XMLBuilder xmlbuilderFinger(List<String> bioFilter, List<MDSDeviceCaptureModel> lstFingerData,
			List<String> bioSubType, XMLBuilder builder, List<BioModality> exceptionlst) {
		List<String> listWithoutExceptions = bioFilter;
		if (exceptionlst != null && !exceptionlst.isEmpty()) {
			List<String> exceptions = exceptionlst.stream().map(BioModality::getSubType).collect(Collectors.toList());
//			RestClient.logInfo(contextKey, "exceptions" + exceptions);
			List<String> schemaName = new ArrayList<String>();
			for (String ex : exceptions) {
				schemaName.add(getschemaName(ex));
			}
			listWithoutExceptions = bioFilter.stream().filter(bioAttribute -> !schemaName.contains(bioAttribute))
					.collect(Collectors.toList());
		}

		// getschemaName(BioModality::getSubType)

		int i = 0;
		String fingerData = null;

		try {
			if (lstFingerData != null) {
				for (String finger : listWithoutExceptions) {
					if (finger.toLowerCase().contains("eye") || finger.toLowerCase().equals("face"))
						continue;
					i = Arrays.asList(DataProviderConstants.schemaNames).indexOf(finger);
					String displayName = DataProviderConstants.displayFingerName[i];
					MDSDeviceCaptureModel currentCM = null;
					for (MDSDeviceCaptureModel cm : lstFingerData) {
						if (cm.getBioSubType().equals(displayName)) {
							fingerData = cm.getBioValue();
							bioSubType.add(finger);
							currentCM = cm;
							break;
						}
					}
//					RestClient.logInfo(contextKey, "fingerData is: " + fingerData);
					if (i >= 0 && fingerData != null && currentCM != null) {
						// Write the BDB values for all finger modalities in proper file.
						
						addToBiometricMap(DataProviderConstants.schemaNames[i], getBase64EncodedStringFromBase64URL(fingerData));
						String strFinger = DataProviderConstants.displayFingerName[i];
						String strFingerXml = buildBirFinger(fingerData, strFinger, currentCM.getSb(),
								currentCM.getPayload(), currentCM.getQualityScore(), FALSE);
						XMLBuilder fbuilder = XMLBuilder.parse(strFingerXml);
						builder = builder.importXMLBuilder(fbuilder);
					}

				}
			}

		} catch (Exception e1) {
			logger.error(e1.getMessage());
		}
		return builder;
	}

	private static XMLBuilder xmlbuilderFingerExep(List<String> bioFilter, List<BioModality> lstFingerData,
			List<String> bioSubType, XMLBuilder builder)
			throws ParserConfigurationException, FactoryConfigurationError, TransformerException, SAXException,
			IOException {

		if (lstFingerData != null) {
			for (BioModality finger : lstFingerData) {
				if (finger.getType().equalsIgnoreCase("iris") || finger.getType().equalsIgnoreCase("face"))
					continue;

				String strFingerXml = buildBirFinger(finger.getType(), finger.getSubType(),
						Arrays.toString(new byte[0]), "", "0", "true");
				XMLBuilder fbuilder = XMLBuilder.parse(strFingerXml);
				builder = builder.importXMLBuilder(fbuilder);
			}

		}

		return builder;
	}

	/*
	 * Construct CBEFF format XML file from biometric data
	 */
	public static String toCBEFF(List<String> bioFilter, BiometricDataModel biometricDataModel, String toFile,
			boolean genarateValidCbeff, String contextKey) throws Exception {
		String retXml = "";

		XMLBuilder builder = XMLBuilder.create("BIR").a(XMLNS, "http://standards.iso.org/iso-iec/19785/-3/ed-2/")
				.e(BIRINFO).e(INTEGRITY).t(FALSE).up().up();

		builder.getDocument().setXmlStandalone(true);

		// Step 1: convert finger print
		String[] fingerPrint = biometricDataModel.getFingerPrint();

		// get qualityScore
		String qualityScore = null;
		Hashtable<String, List<MDSDeviceCaptureModel>> capture = biometricDataModel.getCapture();
		Enumeration<List<MDSDeviceCaptureModel>> elements = capture.elements();
		while (elements.hasMoreElements()) {
			List<MDSDeviceCaptureModel> nextElement = elements.nextElement();
			qualityScore = nextElement.get(0).getQualityScore();
			break;
		}

		int i = 0;
		for (String finger : bioFilter) {
			if (finger.toLowerCase().contains("eye") || finger.toLowerCase().equals("face"))
				continue;
			i = Arrays.asList(DataProviderConstants.schemaNames).indexOf(finger);

			if (i >= 0) {
				String strFinger = DataProviderConstants.displayFingerName[i];
				String strFingerXml = buildBirFinger(fingerPrint[i], strFinger, null, null, qualityScore, FALSE);

				XMLBuilder fbuilder = XMLBuilder.parse(strFingerXml);
				builder = builder.importXMLBuilder(fbuilder);
			}

		}

		// Step 2: Add Face
		if (bioFilter.contains("Face")) {
			if (biometricDataModel.getEncodedPhoto() != null) {
				String faceXml = buildBirFace(biometricDataModel.getEncodedPhoto(), null, null, qualityScore, "true");
				builder = builder.importXMLBuilder(XMLBuilder.parse(faceXml));
			}
		}

		// Step 3: Add IRIS
		IrisDataModel irisInfo = biometricDataModel.getIris();
		if (irisInfo != null) {
			String irisXml = "";
			if (bioFilter.contains(LEFTEYE)) {
				irisXml = buildBirIris(irisInfo.getLeft(), "Left", null, null, qualityScore, "true");
				builder = builder.importXMLBuilder(XMLBuilder.parse(irisXml));
			}
			if (bioFilter.contains(RIGHTEYE)) {
				irisXml = buildBirIris(irisInfo.getRight(), RIGHT, null, null, qualityScore, "true");
				builder = builder.importXMLBuilder(XMLBuilder.parse(irisXml));
			}
		}

		if (toFile != null) {
			FileOutputStream fos = new FileOutputStream(toFile);
			PrintWriter writer = new PrintWriter(fos);
			builder.toWriter(true, writer, null);
			fos.close();
		}
		retXml = builder.asString(null);
		return retXml;
	}

	public static IrisDataModel loadIris(String filePath, String subModality, IrisDataModel im) throws Exception {

		IrisDataModel m = im;
		if (m == null)
			m = new IrisDataModel();
		String irisData = "";
		String irisHash = "";

		if (Files.exists(Paths.get(filePath))) {
			byte[] fdata = CommonUtil.read(filePath);
			irisData = Hex.encodeHexString(fdata);
			irisHash = CommonUtil.getHexEncodedHash(fdata);
			if (subModality.equals("left")) {
				m.setLeftHash(irisHash);
				m.setLeft(irisData);
			} else {
				m.setRightHash(irisHash);
				m.setRight(irisData);
			}
		}

		return m;
	}

}
