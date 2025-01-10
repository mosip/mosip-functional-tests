package io.mosip.testrig.apirig.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.bouncycastle.operator.OperatorCreationException;
import org.jose4j.lang.JoseException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.testng.Reporter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Verify;
import com.ibm.icu.text.Transliterator;

import io.mosip.authentication.core.spi.indauth.match.MatchType;
import io.mosip.testrig.apirig.dto.CertificateChainResponseDto;
import io.mosip.testrig.apirig.dto.OutputValidationDto;
import io.mosip.testrig.apirig.testrunner.BaseTestCase;
import io.mosip.testrig.apirig.testrunner.JsonPrecondtion;
import io.restassured.response.Response;

/**
 * Class to hold dependency method for ida tests automation
 * 
 * @author Vignesh
 *
 */
public class AuthTestsUtil extends BaseTestCase {

	private static final Logger IDASCRIPT_LOGGER = Logger.getLogger(AuthTestsUtil.class);
	private static String testCaseName;
	private static int testCaseId;
	private static File testFolder;
	private static File demoAppBatchFilePath;
	public static final String AUTHORIZATHION_COOKIENAME = GlobalConstants.AUTHORIZATION;
	public static final String authHeaderValue = "Some String";
	protected static String responseJsonToVerifyDigtalSignature;
	protected static String responseDigitalSignatureValue;
	protected static String responseDigitalSignatureKey = "response-signature";
	protected static String logFileName = "id-auth.log";
	
	public AuthTestsUtil() {
        mapper = new ObjectMapper();
    }

    private ObjectMapper mapper;
    private static final String PIN = "pin";

    private static final String BIO = "bio";

    private static final String DEMO = "demo";

    private static final String OTP = "otp";
    private static final String BEGIN_CERTIFICATE = "-----BEGIN CERTIFICATE-----";
    private static final String END_CERTIFICATE = "-----END CERTIFICATE-----";

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    private static final String TIMESTAMP = "timestamp";

    private static final String ID = "id";

    private static final String AUTH_TYPE = "authType";

    private static final String SECONDARY_LANG_CODE = "secondaryLangCode";

    private static final String TXN = "txn";

    private static final String VER = "ver";

    private static final String ENV = "env";

    private static final String DOMAIN_URI = "domainUri";

    private static final String IDA_API_VERSION = "ida.api.version";

    private static final String MOSIP_ENV = "Staging";

    private static final String MOSIP_DOMAINURI = "mosip.base.url";
    public static final String BIOMETRICS = "biometrics";

    private static final String IDA_AUTH_REQUEST_TEMPLATE = "ida.authRequest.template";

    private static final String DATE_TIME = "dateTime";

    private static final String TRANSACTION_ID = "transactionId";

    private static final String IDENTITY = "Identity";

    private static final String TEMPLATE = "Template";

	/**
	 * The method will get current test execution folder
	 * 
	 * @return File
	 */
	public static File getTestFolder() {
		return testFolder;
	}

	/**
	 * The method will set current test execution folder
	 * 
	 * @param testFolder
	 */
	public static void setTestFolder(File testFolder) {
		AuthTestsUtil.testFolder = testFolder;
	}

	/**
	 * The method will get current test execution test case name
	 * 
	 * @return String, test case name
	 */
	public static String getTestCaseName() {
		return testCaseName;
	}

	/**
	 * The method will set test case name for current test execution
	 * 
	 * @param testCaseName
	 */
	public static void setTestCaseName(String testCaseName) {
		AuthTestsUtil.testCaseName = testCaseName;
	}

	/**
	 * The method will get current execution of test case id
	 * 
	 * @return Integer
	 */
	public static int getTestCaseId() {
		return testCaseId;
	}

	/**
	 * The method will set current execution of test case id
	 * 
	 * @param testCaseId
	 */
	public static void setTestCaseId(int testCaseId) {
		AuthTestsUtil.testCaseId = testCaseId;
	}
	
    public void clearKeys(String certsDir, String moduleName, String targetEnv) throws IOException {
        KeyMgrUtility keyMgrUtil = new KeyMgrUtility();
        keyMgrUtil.deleteFile(new File(keyMgrUtil.getKeysDirPath(certsDir, moduleName, targetEnv).toString()));
    }
    
    public CertificateChainResponseDto generatePartnerKeys(
            PartnerTypes partnerType, String partnerName, boolean keyFileNameByPartnerName, String certsDir, String moduleName, String targetEnv) throws UnrecoverableEntryException, CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, OperatorCreationException {
        KeyMgrUtility keyMgrUtil = new KeyMgrUtility();
        return keyMgrUtil.getPartnerCertificates(partnerType, keyMgrUtil.getKeysDirPath(certsDir, moduleName, targetEnv), partnerName,
                keyFileNameByPartnerName);
    }
    
    public String updatePartnerCertificate(
            PartnerTypes partnerType, String partnerName, boolean keyFileNameByPartnerName, Map<String, String> requestData, String certsDir,
            String moduleName, String targetEnv) throws CertificateException,
            IOException, NoSuchAlgorithmException, UnrecoverableEntryException, KeyStoreException {
        KeyMgrUtility keyMgrUtil = new KeyMgrUtility();
        String certificateData = requestData.get("certData");
        String filePrepend = partnerType.getFilePrepend();

        X509Certificate x509Cert = (X509Certificate) keyMgrUtil.convertToCertificate(certificateData);
        System.out.println("certificateType: " + partnerType.toString());
        System.out.println("filePrepend: " + filePrepend);
        boolean isUpdated = keyMgrUtil.updatePartnerCertificate(filePrepend, x509Cert, keyMgrUtil.getKeysDirPath(certsDir, moduleName, targetEnv),
                partnerName, keyFileNameByPartnerName);
        return isUpdated ? "Update Success" : "Update Failed";
    }
    
    public String uploadIDACertificate(
            CertificateTypes certificateType,
            Map<String, String> requestData,
            String certsDir,
            String moduleName,
            String targetEnv)
            throws CertificateException, IOException {
        KeyMgrUtility keyMgrUtil = new KeyMgrUtility();

        String certificateData = requestData.get("certData");
        String fileName = certificateType.getFileName();
        System.out.println("certificateType: " + certificateType.toString());
        System.out.println("FileName: " + fileName);

        X509Certificate x509Cert = (X509Certificate) keyMgrUtil.convertToCertificate(certificateData);
        Base64.Encoder base64Encoder = Base64.getMimeEncoder(64, LINE_SEPARATOR.getBytes());
        byte[] certificateBytes = x509Cert.getEncoded();
        String encodedCertificateData = new String(base64Encoder.encode(certificateBytes));
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(BEGIN_CERTIFICATE);
        strBuilder.append(LINE_SEPARATOR);
        strBuilder.append(encodedCertificateData);
        strBuilder.append(LINE_SEPARATOR);
        strBuilder.append(END_CERTIFICATE);
        String certificateStr = strBuilder.toString();

        String keysDirPath = keyMgrUtil.getKeysDirPath(certsDir, moduleName, targetEnv);

        Path parentPath = Paths.get(keysDirPath + "/" + fileName).getParent();
        if (parentPath != null && !Files.exists(parentPath)) {
            Files.createDirectories(parentPath);
        }

        boolean isErrored = false;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(keysDirPath + "/" + fileName))) {
            writer.write(certificateStr);
            writer.flush();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            isErrored = true;
        }

        return isErrored ? "Upload Failed" : "Upload Success";
    }
    
    private String digest(byte[] hash) throws NoSuchAlgorithmException {
        return DatatypeConverter.printHexBinary(hash).toUpperCase();
    }
    
    public byte[] getCertificateThumbprint(Certificate cert) throws CertificateEncodingException {
        return DigestUtils.sha256(cert.getEncoded());
    }
    
    private void getAuthTypeMap(String reqAuth, Map<String, Object> reqValues, Map<String, Object> request) {
        String[] reqAuthArr;
        if (reqAuth == null) {
            BiFunction<String, String, Optional<String>> authTypeMapFunction = (key, authType) -> Optional
                    .ofNullable(request).filter(map -> map.containsKey(key)).map(map -> authType);
            reqAuthArr = Stream
                    .of(authTypeMapFunction.apply("demographics", "demo"), authTypeMapFunction.apply(BIOMETRICS, "bio"),
                            authTypeMapFunction.apply("otp", "otp"), authTypeMapFunction.apply("staticPin", "pin"))
                    .filter(Optional::isPresent).map(Optional::get).toArray(size -> new String[size]);
        } else {
            reqAuth = reqAuth.trim();
            if (reqAuth.contains(",")) {
                reqAuthArr = reqAuth.split(",");
            } else {
                reqAuthArr = new String[]{reqAuth};
            }
        }

        for (String authType : reqAuthArr) {
            authTypeSelectionMap(reqValues, authType);
        }
    }
    
    private void authTypeSelectionMap(Map<String, Object> reqValues, String authType) {

        if (authType.equalsIgnoreCase(MatchType.Category.OTP.getType())) {
            reqValues.put(OTP, true);
        } else if (authType.equalsIgnoreCase(MatchType.Category.DEMO.getType())) {
            reqValues.put(DEMO, true);
        } else if (authType.equalsIgnoreCase(MatchType.Category.BIO.getType())) {
            reqValues.put(BIO, true);
        } else if (authType.equalsIgnoreCase(MatchType.Category.SPIN.getType())) {
            reqValues.put("pin", true);
        }
    }
    
    private void applyRecursively(Object obj, String key, String value) {
        if (obj instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) obj;
            Optional<String> matchingKey = map.keySet().stream().filter(k -> k.equalsIgnoreCase(key)).findFirst();
            if (matchingKey.isPresent()) {
                map.put(matchingKey.get(), value);
            }

            for (Object val : map.values()) {
                applyRecursively(val, key, value);
            }
        } else if (obj instanceof List) {
            List<?> list = (List<?>) obj;
            for (Object object : list) {
                applyRecursively(object, key, value);
            }
        }
    }
    
    public String signRequest(
            PartnerTypes partnerType,
            String partnerName,
            boolean keyFileNameByPartnerName,
            String request,
            String certsDir,
            String moduleName,
            String targetEnv)
            throws JoseException, NoSuchAlgorithmException, UnrecoverableEntryException, KeyStoreException,
            CertificateException, IOException, OperatorCreationException {
        KeyMgrUtility keyMgrUtil = new KeyMgrUtility();
        JWSSignAndVerifyController jWSSignAndVerifyController = new JWSSignAndVerifyController();
        return jWSSignAndVerifyController.sign(request, false,
                true, false, null, keyMgrUtil.getKeysDirPath(certsDir, moduleName, targetEnv), partnerType, partnerName, keyFileNameByPartnerName);
    }

	/**
	 * The method will post request and generate output file
	 * 
	 * @param listOfFiles
	 * @param urlPath
	 * @param keywordToFind
	 * @param generateOutputFileKeyword
	 * @param code
	 * @return true or false
	 */
	protected boolean postRequestAndGenerateOuputFile(File[] listOfFiles, String urlPath, String keywordToFind,
			String generateOutputFileKeyword, int code) {
		boolean bReturn = true;
		for (int j = 0; j < listOfFiles.length; j++) {
			if (listOfFiles[j].getName().contains(keywordToFind)) {
				try (FileOutputStream outputStream = new FileOutputStream(
						listOfFiles[j].getParentFile() + "/" + generateOutputFileKeyword + ".json")) {
					Response response = null;
					String responseJson = "";
					if (code == 0)
						response = postRequestWithAuthHeader(listOfFiles[j].getAbsolutePath(), urlPath,
								AUTHORIZATHION_COOKIENAME, authHeaderValue);
					else
						response = postRequestWithAuthHeader(listOfFiles[j].getAbsolutePath(), urlPath, code,
								AUTHORIZATHION_COOKIENAME, authHeaderValue);
					responseJson = response.asString();
					responseJsonToVerifyDigtalSignature = responseJson;
					responseDigitalSignatureValue = response.getHeader(responseDigitalSignatureKey);
					GlobalMethods.reportResponse(response.getHeaders().asList().toString(), urlPath, response);
					responseJson = JsonPrecondtion.toPrettyFormat(responseJson);
					outputStream.write(responseJson.getBytes());
				} catch (Exception e) {
					bReturn = false;
					IDASCRIPT_LOGGER.error(GlobalConstants.EXCEPTION_STRING_2 + e.getMessage());
					break;
				}
			}
		}
		return bReturn;
	}

	/**
	 * The method will post request and generate output file
	 * 
	 * @param listOfFiles
	 * @param urlPath
	 * @param keywordToFind
	 * @param generateOutputFileKeyword
	 * @param code
	 * @return true or false
	 */
	protected boolean postRequestAndGenerateOuputFileForIntenalAuth(File[] listOfFiles, String urlPath,
			String keywordToFind, String generateOutputFileKeyword, String cookieName, String cookieValue, int code) {
		boolean bReturn = true;
		for (int j = 0; j < listOfFiles.length; j++) {
			if (listOfFiles[j].getName().contains(keywordToFind)) {
				try (FileOutputStream outputStream = new FileOutputStream(
						listOfFiles[j].getParentFile() + "/" + generateOutputFileKeyword + ".json")) {

					Response response;
					if (code == 0)
						response = postRequestWithCookieAndHeader(listOfFiles[j].getAbsolutePath(), urlPath, cookieName,
								cookieValue, AUTHORIZATHION_COOKIENAME, authHeaderValue);
					else
						response = postRequestWithCookieAndHeader(listOfFiles[j].getAbsolutePath(), urlPath, code,
								cookieName, cookieValue, AUTHORIZATHION_COOKIENAME, authHeaderValue);
					responseJsonToVerifyDigtalSignature = response.asString();
					responseDigitalSignatureValue = response.getHeader(responseDigitalSignatureKey);
					GlobalMethods.reportResponse(response.getHeaders().asList().toString(), urlPath, response);
					outputStream.write(response.asString().getBytes());
				} catch (Exception e) {
					bReturn = false;
					IDASCRIPT_LOGGER.error(GlobalConstants.EXCEPTION_STRING_2 + e.getMessage());
					break;
				}
			}
		}
		return bReturn;
	}

	/**
	 * The method will post request and generate output file with return repose
	 * 
	 * @param listOfFiles
	 * @param urlPath
	 * @param keywordToFind
	 * @param generateOutputFileKeyword
	 * @param code
	 * @return String , response for post request
	 */
	protected String postRequestAndGenerateOuputFileWithResponse(File[] listOfFiles, String urlPath,
			String keywordToFind, String generateOutputFileKeyword, String cookieName, String cookieValue, int code) {
		FileOutputStream outputStream = null;
		try {
			for (int j = 0; j < listOfFiles.length; j++) {
				if (listOfFiles[j].getName().contains(keywordToFind)) {
					outputStream = new FileOutputStream(
							listOfFiles[j].getParentFile() + "/" + generateOutputFileKeyword + ".json");
					Response responseJson;
					if (code == 0)
						responseJson = postRequestWithCookie(listOfFiles[j].getAbsolutePath(), urlPath, cookieName,
								cookieValue);
					else
						responseJson = postRequestWithCookie(listOfFiles[j].getAbsolutePath(), urlPath, code,
								cookieName, cookieValue);
					GlobalMethods.reportResponse(responseJson.getHeaders().asList().toString(), urlPath, responseJson);
					outputStream.write(responseJson.asString().getBytes());
					return responseJson.asString();
				}
			}
			return "NoResponse";
		} catch (Exception e) {
			IDASCRIPT_LOGGER.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return e.getMessage();
		} finally {
			AdminTestUtil.closeOutputStream(outputStream);
		}
	}

	protected Response postRequestWithCookie(String filename, String url, int expCode, String cookieName,
			String cookieValue) {
		Response response = null;
		try(FileReader fr = new FileReader(filename)) {
			JSONObject objectData = (JSONObject) new JSONParser().parse(fr);
			response = RestClient.postRequestWithCookie(url, objectData.toJSONString(), MediaType.APPLICATION_JSON,
					MediaType.APPLICATION_JSON, cookieName, cookieValue);
			Map<String, List<OutputValidationDto>> objMap = new HashMap<>();
			List<OutputValidationDto> objList = new ArrayList<OutputValidationDto>();
			objList.add(verifyStatusCode(response, expCode));
			objMap.put("Status Code", objList);
			Reporter.log(ReportUtil.getOutputValidationReport(objMap));
			Verify.verify(OutputValidationUtil.publishOutputResult(objMap));
			return response;
		} catch (Exception e) {
			IDASCRIPT_LOGGER.error("Exception: " + e);
			return response;
		}
	}

	protected Response postRequestWithCookieAndHeader(String filename, String url, int expCode, String cookieName,
			String cookieValue, String authHeaderName, String authHeaderValue) {
		Response response = null;
		try(FileReader fr = new FileReader(filename)) {
			JSONObject objectData = (JSONObject) new JSONParser().parse(fr);
			response = RestClient.postRequestWithCookieAndHeader(url, objectData.toJSONString(),
					MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, cookieName, cookieValue, authHeaderName,
					authHeaderValue);
			Map<String, List<OutputValidationDto>> objMap = new HashMap<>();
			List<OutputValidationDto> objList = new ArrayList<OutputValidationDto>();
			objList.add(verifyStatusCode(response, expCode));
			objMap.put("Status Code", objList);
			Reporter.log(ReportUtil.getOutputValidationReport(objMap));
			Verify.verify(OutputValidationUtil.publishOutputResult(objMap));
			return response;
		} catch (Exception e) {
			IDASCRIPT_LOGGER.error("Exception: " + e);
			return response;
		}
	}

	/**
	 * The method will post request and generate output file for UIN generation
	 * 
	 * @param listOfFiles
	 * @param urlPath
	 * @param keywordToFind
	 * @param generateOutputFileKeyword
	 * @param code
	 * @return true or false
	 */
	protected boolean postRequestAndGenerateOuputFileForUINGeneration(File[] listOfFiles, String urlPath,
			String keywordToFind, String generateOutputFileKeyword, String cookieName, String cookieValue, int code) {
		FileOutputStream outputStream = null;
		try {
			for (int j = 0; j < listOfFiles.length; j++) {
				if (listOfFiles[j].getName().contains(keywordToFind)) {
					outputStream = new FileOutputStream(
							listOfFiles[j].getParentFile() + "/" + generateOutputFileKeyword + ".json");
					Response responseJson;
					if (code == 0)
						responseJson = postRequestWithCookie(listOfFiles[j].getAbsolutePath(), urlPath, cookieName,
								cookieValue);
					else
						responseJson = postRequestWithCookie(listOfFiles[j].getAbsolutePath(), urlPath, code,
								cookieName, cookieValue);
					if (responseJson.asString().contains("Invalid UIN")) {
						return false;
					} else {
						GlobalMethods.reportResponse(responseJson.getHeaders().asList().toString(), urlPath, responseJson);
						outputStream.write(responseJson.asString().getBytes());
						return true;
					}
				}
			}
			return false;
		} catch (Exception e) {
			IDASCRIPT_LOGGER.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return false;
		} finally {
			AdminTestUtil.closeOutputStream(outputStream);
		}
	}

	/**
	 * The method will post request and generate output file for UIN update
	 * 
	 * @param listOfFiles
	 * @param urlPath
	 * @param keywordToFind
	 * @param generateOutputFileKeyword
	 * @param code
	 * @return true or false
	 */
	protected boolean postRequestAndGenerateOuputFileForUINUpdate(File[] listOfFiles, String urlPath,
			String keywordToFind, String generateOutputFileKeyword, String cookieName, String cookieValue, int code) {
		FileOutputStream outputStream = null;
		try {
			for (int j = 0; j < listOfFiles.length; j++) {
				if (listOfFiles[j].getName().contains(keywordToFind)) {
					outputStream = new FileOutputStream(
							listOfFiles[j].getParentFile() + "/" + generateOutputFileKeyword + ".json");
					String responseJson = "";
					if (code == 0)
						responseJson = patchRequestWithCookie(listOfFiles[j].getAbsolutePath(), urlPath, cookieName,
								cookieValue).asString();
					Reporter.log("<b><u>Actual Patch Response Content: </u></b>(EndPointUrl: " + urlPath + ") <pre>"
							+ ReportUtil.getTextAreaJsonMsgHtml(responseJson) + "</pre>");
					outputStream.write(responseJson.getBytes());
					return true;
				}
			}
			return false;
		} catch (Exception e) {
			IDASCRIPT_LOGGER.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return false;
		} finally {
			AdminTestUtil.closeOutputStream(outputStream);
		}
	}

	protected Response postRequestWithAuthHeader(String filename, String url, String authHeaderName,
			String authHeaderValue) {
		try(FileReader fr = new FileReader(filename)) {
			JSONObject objectData = (JSONObject) new JSONParser().parse(fr);
			return RestClient.postRequestWithAuthHeader(url, objectData.toJSONString(), MediaType.APPLICATION_JSON,
					MediaType.APPLICATION_JSON, authHeaderName, authHeaderValue);
		} catch (Exception e) {
			IDASCRIPT_LOGGER.error("Exception: " + e);
			return null;
		}
	}

	/**
	 * The method will help to patch request
	 * 
	 * @param filename
	 * @param url
	 * @return String, Reponse for request
	 */
	protected String patchRequest(String filename, String url) {
		try(FileReader fr = new FileReader(filename)) {
			JSONObject objectData = (JSONObject) new JSONParser().parse(fr);
			return RestClient.patchRequest(url, objectData.toJSONString(), MediaType.APPLICATION_JSON,
					MediaType.APPLICATION_JSON).asString();
		} catch (Exception e) {
			IDASCRIPT_LOGGER.error("Exception: " + e);
			return e.toString();
		}
	}

	/**
	 * The method will help to patch request and verify status code
	 * 
	 * @param filename
	 * @param url
	 * @param expCode
	 * @return
	 */
	protected String patchRequest(String filename, String url, int expCode) {
		String responseString = "";
		try(FileReader fr = new FileReader(filename)) {
			Response response = null;
			JSONObject objectData = (JSONObject) new JSONParser().parse(fr);
			response = RestClient.patchRequest(url, objectData.toJSONString(), MediaType.APPLICATION_JSON,
					MediaType.APPLICATION_JSON);
			Map<String, List<OutputValidationDto>> objMap = new HashMap<>();
			List<OutputValidationDto> objList = new ArrayList<OutputValidationDto>();
			objList.add(verifyStatusCode(response, expCode));
			objMap.put("Status Code", objList);
			Reporter.log(ReportUtil.getOutputValidationReport(objMap));
			Verify.verify(OutputValidationUtil.publishOutputResult(objMap));
			responseString = response.asString();
		} catch (Exception e) {
			IDASCRIPT_LOGGER.error("Exception: " + e);
		}
		return responseString;
	}

	/**
	 * The method perform status code verification
	 * 
	 * @param response
	 * @param expCode
	 * @return object of Output Validation
	 */
	private OutputValidationDto verifyStatusCode(Response response, int expCode) {
		OutputValidationDto objOpDto = new OutputValidationDto();
		if (response.statusCode() == expCode) {
			objOpDto.setFieldHierarchy("STATUS CODE");
			objOpDto.setFieldName("STATUS CODE");
			objOpDto.setActualValue(String.valueOf(response.statusCode()));
			objOpDto.setExpValue(String.valueOf(expCode));
			objOpDto.setStatus("PASS");
		} else {
			objOpDto.setFieldHierarchy("STATUS CODE");
			objOpDto.setFieldName("STATUS CODE");
			objOpDto.setActualValue(String.valueOf(response.statusCode()));
			objOpDto.setExpValue(String.valueOf(expCode));
			objOpDto.setStatus(GlobalConstants.FAIL_STRING);
		}
		return objOpDto;
	}

	protected Response postRequestWithAuthHeader(String filename, String url, int expCode, String authHeaderName,
			String authHeaderValue) {
		Response response = null;
		try(FileReader fr = new FileReader(filename)) {
			JSONObject objectData = (JSONObject) new JSONParser().parse(fr);
			response = RestClient.postRequestWithAuthHeader(url, objectData.toJSONString(), MediaType.APPLICATION_JSON,
					MediaType.APPLICATION_JSON, authHeaderName, authHeaderValue);
			Map<String, List<OutputValidationDto>> objMap = new HashMap<>();
			List<OutputValidationDto> objList = new ArrayList<OutputValidationDto>();
			objList.add(verifyStatusCode(response, expCode));
			objMap.put("Status Code", objList);
			Reporter.log(ReportUtil.getOutputValidationReport(objMap));
			Verify.verify(OutputValidationUtil.publishOutputResult(objMap));
			return response;
		} catch (Exception e) {
			IDASCRIPT_LOGGER.error("Exception: " + e);
			return response;
		}
	}

	/**
	 * The method will get response for url and type
	 * 
	 * @param url,  endpoint url
	 * @param type, BIO,DEMO,ALL
	 * @return String, Response
	 */
	protected static String getResponseWithCookie(String url, String cookieName, String cookieValue) {
		try {
			return RestClient.getRequestWithCookie(url, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON,
					cookieName, cookieValue).asString();
		} catch (Exception e) {
			IDASCRIPT_LOGGER.error("Exception: " + e);
			return e.toString();
		}
	}

	/**
	 * The method will get response for url
	 * 
	 * @param url
	 * @return String, Response
	 */
	protected static String getResponse(String url) {
		try {
			return RestClient.getRequest(url, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON).asString();
		} catch (Exception e) {
			IDASCRIPT_LOGGER.error("Exception: " + e);
			return e.toString();
		}
	}

	/**
	 * The method will get response for url and type
	 * 
	 * @param type, BIO,DEMO,ALL
	 * @return String, Response
	 */
	protected static String getResponseWithCookieForIdaUinGenerator(String url, String cookieName) {
		try {
			return RestClient.getRequestWithCookie(url, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON,
					cookieName,
					getAuthorizationCookie(getCookieRequestFilePathForUinGenerator(), getCookieUrlPath(), cookieName))
					.asString();
		} catch (Exception e) {
			IDASCRIPT_LOGGER.error("Exception: " + e);
			return e.toString();
		}
	}

	/**
	 * The method will get response for url and type
	 * 
	 * @param url,  endpoint url
	 * @param type, BIO,DEMO,ALL
	 * @return String, Response
	 */
	protected static String getResponseWithCookieForIdRepoUinGenerator(String url, String cookieName) {
		try {
			return RestClient.getRequestWithCookie(url, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON,
					cookieName, getAuthorizationCookie(AuthTestsUtil.getCookieRequestFilePathForUinGenerator(),
							getCookieUrlPath(), cookieName))
					.asString();
		} catch (Exception e) {
			IDASCRIPT_LOGGER.error("Exception: " + e);
			return e.toString();
		}
	}

	/**
	 * The method will get encoded data from file from list of files
	 * 
	 * @param listOfFiles   , List of files
	 * @param keywordToFind , keyword to find from list
	 * @return String, encoded data
	 */
	protected String getEnodedData(File[] listOfFiles, String keywordToFind) {
		for (int j = 0; j < listOfFiles.length; j++) {
			if (listOfFiles[j].getName().contains(keywordToFind)) {
				return EncryptDecrptUtil.getEncode(listOfFiles[j].getAbsolutePath());
			}
		}
		return null;
	}

	/**
	 * The method will get decoded data from file from list of files
	 * 
	 * @param listOfFiles   , List of file
	 * @param keywordToFind , keyword to find from list
	 * @return String, decoded data
	 */
	protected String getDecodedData(File[] listOfFiles, String keywordToFind) {
		for (int j = 0; j < listOfFiles.length; j++) {
			if (listOfFiles[j].getName().contains(keywordToFind)) {
				return EncryptDecrptUtil.getEncode(listOfFiles[j].getAbsolutePath());
			}
		}
		return null;
	}

	/**
	 * The method will get decoded data from string content
	 * 
	 * @param content
	 * @return String, decoded data
	 */
	protected static String getDecodedData(String content) {
		return EncryptDecrptUtil.getDecodeFromStr(content);
	}

	/**
	 * The method will display content in file
	 * 
	 * @param listOfFiles
	 * @param keywordToFind
	 */
	protected void displayContentInFile(File[] listOfFiles, String keywordToFind) {
		try {
			for (int j = 0; j < listOfFiles.length; j++) {
				if (listOfFiles[j].getName().contains(keywordToFind)) {
					String responseJson = getContentFromFile(listOfFiles[j]);
					GlobalMethods.reportRequest(null, responseJson);
				}
			}
		} catch (Exception e) {
			IDASCRIPT_LOGGER.info("Exception : " + e);
		}
	}

	/**
	 * The method will get content from file
	 * 
	 * @param listOfFiles
	 * @param keywordToFind
	 * @return String, content in file
	 */
	protected String getContentFromFile(File[] listOfFiles, String keywordToFind) {
		try {
			for (int j = 0; j < listOfFiles.length; j++) {
				if (listOfFiles[j].getName().contains(keywordToFind)) {
					return getContentFromFile(listOfFiles[j]);
				}
			}
		} catch (Exception e) {
			IDASCRIPT_LOGGER.info("Exception : " + e);
			return e.getMessage();
		}
		return null;
	}

	/**
	 * The method will get content from file
	 * 
	 * @param file
	 * @return String, content in file
	 * @throws IOException
	 */
	@SuppressWarnings("deprecation")
	public static String getContentFromFile(File file) throws IOException {
		try {
			return FileUtils.readFileToString(file.getAbsoluteFile());
		} catch (Exception e) {
			IDASCRIPT_LOGGER.error("Exception: " + e.getMessage());
			return e.getMessage();
		}
	}

	/**
	 * The method will modify json request
	 * 
	 * @param listOfFiles
	 * @param fieldvalue
	 * @param propFileName
	 * @param keywordinFile
	 * @return true or false
	 */

	/**
	 * The method get encryptedsessionkey, request and hmac value
	 * 
	 * @param listOfFiles
	 * @param keywordToFind
	 * @return map
	 */
	protected Map<String, String> getEncryptKeyvalue(File[] listOfFiles, String keywordToFind) {
		for (int j = 0; j < listOfFiles.length; j++) {
			if (listOfFiles[j].getName().contains(keywordToFind)) {
				return EncryptDecrptUtil.getEncryptSessionKeyValue(listOfFiles[j].getAbsolutePath());
			}
		}
		return null;
	}

	/**
	 * The method get internal encryptedsessionkey, request and hmac value
	 * 
	 * @param listOfFiles
	 * @param keywordToFind
	 * @return map
	 */
	protected Map<String, String> getInternalEncryptKeyvalue(File[] listOfFiles, String keywordToFind) {
		for (int j = 0; j < listOfFiles.length; j++) {
			if (listOfFiles[j].getName().contains(keywordToFind)) {
				return EncryptDecrptUtil.getInternalEncryptSessionKeyValue(listOfFiles[j].getAbsolutePath());
			}
		}
		return null;
	}

	/**
	 * To display current execution of test case log
	 * 
	 * @param testCaseName
	 * @param testCaseNumber
	 */
	protected void displayLog(File testCaseName, int testCaseNumber) {
		IDASCRIPT_LOGGER.info(
				"**************************************************************************************************************************************");
		IDASCRIPT_LOGGER.info("*          Test Case Id: " + testCaseNumber);
		IDASCRIPT_LOGGER.info("*          Test Case Name: " + testCaseName.getName());
		IDASCRIPT_LOGGER.info(
				"**************************************************************************************************************************************");
	}

	/**
	 * The method get property value for the key
	 * 
	 * @param key
	 * @return string
	 */
	public static String getPropertyValue(String key) {
		return getRunConfigData().getProperty(key);
	}

	/**
	 * The method get env config details
	 * 
	 * @return properties
	 */
	private static Properties getRunConfigData() {
		Properties prop = new Properties();
		InputStream input = null;
		FileInputStream inputStream = null;
		try {
			RunConfigUtil.objRunConfig.setUserDirectory();
			inputStream = new FileInputStream(
					new File(RunConfigUtil.getResourcePath() + "/ida/TestData/RunConfig/envRunConfig.properties")
							.getAbsolutePath());
			input = inputStream;
			prop.load(input);
			input.close();
			return prop;
		} catch (Exception e) {
			IDASCRIPT_LOGGER.error("Exception: " + e.getMessage());
			return prop;
		} finally {
			AdminTestUtil.closeInputStream(inputStream);
		}
	}

	/**
	 * The method get otp value from database
	 * 
	 * @param inputFilePath
	 * @param mappingFileName
	 * @param otpMappingFieldName
	 * @return String , OTP Value
	 */
	

	/**
	 * The method retrieve value from json
	 * 
	 * @param listOfFiles
	 * @param mappingFileName
	 * @param mappingFieldName
	 * @param keywordinFile
	 * @return String
	 */
	public String getValueFromJson(File[] listOfFiles, String mappingFileName, String mappingFieldName,
			String keywordinFile) {
		for (int j = 0; j < listOfFiles.length; j++) {
			if (listOfFiles[j].getName().contains(keywordinFile)) {
				return JsonPrecondtion.getValueFromJson(listOfFiles[j].getAbsolutePath(), mappingFileName,
						mappingFieldName);
			}
		}
		return "No Value Found From Json, Check mapping field or file name and input json file";
	}

	/**
	 * The method to wait for period of time
	 * 
	 * @param time
	 */
	public static void wait(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			IDASCRIPT_LOGGER.info("Exception :" + e);
			Thread.currentThread().interrupt();
		}
	}

	/**
	 * The method will perform language converter
	 * 
	 * @param inputString
	 * @param langSourceCode
	 * @param langDestCode
	 * @return String
	 */
	public static String languageConverter(String inputString, String langSourceCode, String langDestCode) {
		final String language_translation_code = langSourceCode + "-" + langDestCode;
		Transliterator input = Transliterator.getInstance(language_translation_code);
		String transliteratedString = input.transliterate(inputString);
		return transliteratedString;
	}

	/**
	 * Create generated UIN number and its test case name in property file
	 * 
	 * @param filePath
	 */
	public static void generateMappingDic(String filePath, Map<String, String> map) {
		Properties prop = new Properties();
		FileOutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(filePath);
			for (Entry<String, String> entry : map.entrySet()) {
				prop.setProperty(entry.getKey(), entry.getValue());
			}
			prop.store(outputStream, null);
		} catch (Exception e) {
			IDASCRIPT_LOGGER.error("Excpetion in storing the data in propertyFile" + e.getMessage());
		} finally {
			AdminTestUtil.closeOutputStream(outputStream);
		}
	}

	/**
	 * The method will update the existing mapping dictionary in property file
	 * 
	 * @param filePath
	 * @param map
	 */
	public static void updateMappingDic(String filePath, Map<String, String> map) {
		FileInputStream inputStream = null;
		FileOutputStream outputStream = null;
		try {
			inputStream = new FileInputStream(filePath);
			Properties props = new Properties();
			props.load(inputStream);
			outputStream = new FileOutputStream(filePath);
			for (Entry<String, String> entry : map.entrySet()) {
				props.setProperty(entry.getKey(), entry.getValue());
			}
			props.store(outputStream, null);
		} catch (Exception e) {
			IDASCRIPT_LOGGER.error("Exception in updating the property file" + e.getMessage());
		} finally {
			AdminTestUtil.closeInputStream(inputStream);
			AdminTestUtil.closeOutputStream(outputStream);
		}
	}

	/**
	 * The method will update mapping dictionary for email notification
	 * 
	 * @param filePath
	 * @param map
	 */
	public void updateMappingDicForEmailOtpNotification(String filePath, Map<String, String> map) {
		FileInputStream inputStream = null;
		FileOutputStream outputStream = null;
		OutputStreamWriter outputStreamWriter = null;
		try {
			inputStream = new FileInputStream(filePath);
			Properties props = new Properties();
			props.load(inputStream);
			for (Entry<String, String> entry : map.entrySet()) {
				if (entry.getValue().contains("$otp$")) {
					String value = entry.getValue().replace("$otp$", map.get("email.otp"));
					props.setProperty(entry.getKey(), value);
				} else
					props.setProperty(entry.getKey(), entry.getValue());
			}
			outputStream = new FileOutputStream(filePath);
			outputStreamWriter = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
			props.store(outputStreamWriter, null);
		} catch (Exception e) {
			IDASCRIPT_LOGGER.error("Exception in updating the property file" + e.getMessage());
		} finally {
			AdminTestUtil.closeInputStream(inputStream);
			AdminTestUtil.closeOutputStream(outputStream);
			AdminTestUtil.closeOutputStreamWriter(outputStreamWriter);
		}
	}

	/**
	 * The method will get value from property file
	 * 
	 * @param filepath
	 * @param key
	 * @return string
	 */
	public static String getValueFromPropertyFile(String filepath, String key) {
		Properties prop = new Properties();
		FileInputStream inputStream = null;
		try {
			inputStream = new FileInputStream(filepath);
			prop.load(inputStream);
			return prop.getProperty(key);
		} catch (Exception e) {
			IDASCRIPT_LOGGER.error("Exception: " + e.getMessage());
			return e.getMessage();
		} finally {
			AdminTestUtil.closeInputStream(inputStream);
		}
	}

	/**
	 * The method will get property from file path
	 * 
	 * @param filepath
	 * @return properties
	 */
	public static Properties getPropertyFromFilePath(String filepath) {
		Properties prop = new Properties();
		FileInputStream inputStream = null;
		try {
			inputStream = new FileInputStream(filepath);
			prop.load(inputStream);

		} catch (Exception e) {
			IDASCRIPT_LOGGER.error("Exception: " + e.getMessage());
		} finally {
			AdminTestUtil.closeInputStream(inputStream);
		}
		return prop;
	}

	/**
	 * The method will get property from relative file path
	 * 
	 * @param path
	 * @return properties
	 */
	public static Properties getPropertyFromRelativeFilePath(String path) {
		Properties prop = new Properties();
		FileInputStream inputStream = null;
		try {
			inputStream = new FileInputStream(new File(RunConfigUtil.getResourcePath() + path).getAbsolutePath());
			prop.load(inputStream);
		} catch (Exception e) {
			IDASCRIPT_LOGGER.error("Exception occured in fetching data property file " + e.getMessage());

		} finally {
			AdminTestUtil.closeInputStream(inputStream);
		}
		return prop;
	}

	/**
	 * The method will get property as map
	 * 
	 * @param filepath
	 * @return map
	 */
	public static Map<String, String> getPropertyAsMap(String filepath) {
		Properties prop = new Properties();
		FileInputStream inputStream = null;
		Map<String, String> map = new HashMap<>();
		try {
			inputStream = new FileInputStream(filepath);
			prop.load(inputStream);
			for (String key : prop.stringPropertyNames()) {
				map.put(key, prop.getProperty(key));
			}
		} catch (Exception e) {
			IDASCRIPT_LOGGER.error("Exception: " + e.getMessage());
		} finally {
			AdminTestUtil.closeInputStream(inputStream);
		}
		return map;
	}

	public static void initiateAuthTest() {

		copyAuthTestResource();

	}

	/**
	 * The method will change the file permission
	 * 
	 * @param path
	 */
	private static void changeFilePermissionInLinux(Path path) {
		try {
			Set<PosixFilePermission> perms = Files.readAttributes(path, PosixFileAttributes.class).permissions();
			perms.add(PosixFilePermission.OWNER_WRITE);
			perms.add(PosixFilePermission.OWNER_READ);
			perms.add(PosixFilePermission.OWNER_EXECUTE);
			perms.add(PosixFilePermission.GROUP_WRITE);
			perms.add(PosixFilePermission.GROUP_READ);
			perms.add(PosixFilePermission.GROUP_EXECUTE);
			Files.setPosixFilePermissions(path, perms);
		} catch (Exception e) {
			IDASCRIPT_LOGGER.error("Exception in change the file permission:" + e.getMessage());
		}
	}

	private static File fileDemoAppJarPath;

	/**
	 * The method use to add partnerID and License key in endpoint url
	 * 
	 * @param file
	 * @return PartnerID and LicenseKey
	 */
	public String getExtendedUrl(File file) {
		if (file.exists()) {
			Map<String, String> urlProperty = getPropertyAsMap(file.getAbsolutePath());
			if (urlProperty.containsKey("partnerIDMispLK")) {
				return "/" + urlProperty.get("partnerIDMispLK");
			} else if (urlProperty.containsKey("partnerID") && urlProperty.containsKey("mispLK")
					&& urlProperty.containsKey(GlobalConstants.APIKEY)) {
				return "/" + urlProperty.get("mispLK") + "/" + urlProperty.get("partnerID") + "/"
						+ urlProperty.get(GlobalConstants.APIKEY);
			}
		} else
			return "";
		return "NO Value Found in TestData";
	}

	/**
	 * The method will get current dempApp version from pom file
	 * 
	 * @return version of demoApp as string
	 */
	public static String getDemoAppVersion() {
		String expression = "//dependency/artifactId[text()='authentication-partnerdemo-service']//following::version";
		return RunConfigUtil.getdemoAppVersion();
	}

	public File getFile(File[] listOfFiles, String keywordToFind) {
		for (int j = 0; j < listOfFiles.length; j++) {
			if (listOfFiles[j].getName().contains(keywordToFind)) {
				return listOfFiles[j];
			}
		}
		return null;
	}



	/**
	 * The method returns run config path
	 */
	public String getRunConfigFile() {
		return RunConfigUtil.getGlobalResourcePath() + "/ida/TestData/RunConfig/runConfiguration.properties";
	}

	/**
	 * The method return test data path from config file
	 * 
	 * @param className
	 * @param index
	 * @return string
	 */
	public String getTestDataPath(String className, int index) {
		return getPropertyAsMap(new File(getRunConfigFile()).getAbsolutePath())
				.get(className + ".testDataPath[" + index + "]");
	}

	/**
	 * The method will return test data file name from config file
	 * 
	 * @param className
	 * @param index
	 * @return string
	 */
	public String getTestDataFileName(String className, int index) {
		return getPropertyAsMap(new File(getRunConfigFile()).getAbsolutePath())
				.get(className + ".testDataFileName[" + index + "]");
	}

	protected static String getAuthorizationCookie(String filename, String urlPath, String cookieName) {
		String authorizationCookie = "";
		try {
			String json = getContentFromFile(new File(filename));
			json = json.replace("$TIMESTAMPZ$", AdminTestUtil.generateCurrentUTCTimeStamp());
			JSONObject objectData = (JSONObject) new JSONParser().parse(json);
			authorizationCookie = RestClient.getCookie(urlPath, objectData.toJSONString(), MediaType.APPLICATION_JSON,
					MediaType.APPLICATION_JSON, cookieName);
		} catch (Exception e) {
			IDASCRIPT_LOGGER.error("Exception Occured :" + e.getMessage());
		}
		return authorizationCookie;
	}

	protected Response postRequestWithCookie(String filename, String url, String cookieName, String cookieValue) {
		try(FileReader fr = new FileReader(filename)) {
			JSONObject objectData = (JSONObject) new JSONParser().parse(fr);
			return RestClient.postRequestWithCookie(url, objectData.toJSONString(), MediaType.APPLICATION_JSON,
					MediaType.APPLICATION_JSON, cookieName, cookieValue);
		} catch (Exception e) {
			IDASCRIPT_LOGGER.error("Exception: " + e);
			return null;
		}
	}

	protected Response postRequestWithCookieAndHeader(String filename, String url, String cookieName,
			String cookieValue, String authHeaderName, String authHeaderValue) {
		try(FileReader fr = new FileReader(filename)) {
			JSONObject objectData = (JSONObject) new JSONParser().parse(fr);
			return RestClient.postRequestWithCookieAndHeader(url, objectData.toJSONString(), MediaType.APPLICATION_JSON,
					MediaType.APPLICATION_JSON, cookieName, cookieValue, authHeaderName, authHeaderValue);
		} catch (Exception e) {
			IDASCRIPT_LOGGER.error("Exception: " + e);
			return null;
		}
	}

	protected String postRequestWithCookie(String filename, String url, String cookieName, String cookieValue,
			int code) {
		try(FileReader fr = new FileReader(filename)) {
			JSONObject objectData = (JSONObject) new JSONParser().parse(fr);
			return RestClient.postRequestWithCookie(url, objectData.toJSONString(), MediaType.APPLICATION_JSON,
					MediaType.APPLICATION_JSON, cookieName, cookieValue).asString();
		} catch (Exception e) {
			IDASCRIPT_LOGGER.error("Exception: " + e);
			return e.toString();
		}
	}

	protected static String postStrContentRequestWithCookie(String content, String url, String cookieName,
			String cookieValue) {
		try {
			return RestClient.postRequestWithCookie(url, content, MediaType.APPLICATION_JSON,
					MediaType.APPLICATION_JSON, cookieName, cookieValue).asString();
		} catch (Exception e) {
			IDASCRIPT_LOGGER.error("Exception: " + e);
			return e.toString();
		}
	}

	protected static String getCookieRequestFilePath() {
		return RunConfigUtil.getResourcePath() + "ida/TestData/Security/GetCookie/" + RunConfigUtil.getRunEvironment()
				+ ".getCookieRequest.json";
	}

	protected static String getCookieRequestFilePathForUinGenerator() {
		return RunConfigUtil.getResourcePath() + "ida/TestData/Security/GetCookie/" + RunConfigUtil.getRunEvironment()
				+ ".getCookieForUinGenerator.json";
	}

	protected static String getCookieRequestFilePathForInternalAuth() {
		return RunConfigUtil.getResourcePath() + "ida/TestData/Security/GetCookie/" + RunConfigUtil.getRunEvironment()
				+ ".getCookieForInternalAuth.json";
	}

	protected static String getCookieRequestFilePathForResidentAuth() {
		return RunConfigUtil.getResourcePath() + "ida/TestData/Security/GetCookie/" + RunConfigUtil.getRunEvironment()
				+ ".residentServiceCredential.json";
	}

	protected Response patchRequestWithCookie(String filename, String url, String cookieName, String cookieValue) {
		try(FileReader fr = new FileReader(filename)) {
			JSONObject objectData = (JSONObject) new JSONParser().parse(fr);
			return RestClient.patchRequestWithCookie(url, objectData.toJSONString(), MediaType.APPLICATION_JSON,
					MediaType.APPLICATION_JSON, cookieName, cookieValue);
		} catch (Exception e) {
			IDASCRIPT_LOGGER.error("Exception: " + e);
			return null;
		}
	}

	protected Response patchRequestWithCookie(String filename, String url, int expCode, String cookieName,
			String cookieValue) {
		Response response = null;
		try(FileReader fr = new FileReader(filename)) {
			JSONObject objectData = (JSONObject) new JSONParser().parse(fr);
			response = RestClient.patchRequestWithCookie(url, objectData.toJSONString(), MediaType.APPLICATION_JSON,
					MediaType.APPLICATION_JSON, cookieName, cookieValue);
			Map<String, List<OutputValidationDto>> objMap = new HashMap<>();
			List<OutputValidationDto> objList = new ArrayList<OutputValidationDto>();
			objList.add(verifyStatusCode(response, expCode));
			objMap.put("Status Code", objList);
			Reporter.log(ReportUtil.getOutputValidationReport(objMap));
			Verify.verify(OutputValidationUtil.publishOutputResult(objMap));
			return response;
		} catch (Exception e) {
			IDASCRIPT_LOGGER.error("Exception: " + e);
			return response;
		}
	}

	protected static String getCookieUrlPath() {
		return RunConfigUtil.objRunConfig.getEndPointUrl() + RunConfigUtil.objRunConfig.getClientidsecretkey();
	}

	public static String getVidRequestContent() {
		try {
			return getContentFromFile(new File(
					RunConfigUtil.getResourcePath() + "ida/VIDData/VIDGeneration/VIDGenerate_smoke/vid-request.json"));
		} catch (Exception e) {
			IDASCRIPT_LOGGER.error("Exception Occured in getting the VID request file" + e.getMessage());
			return e.getMessage();
		}
	}

	public static String getVidRequestContentTemplate() {
		try {
			return getContentFromFile(new File(
					RunConfigUtil.getResourcePath() + "ida/TestData/VIDData/VIDGeneration/input/vid-request.json"));
		} catch (Exception e) {
			IDASCRIPT_LOGGER.error("Exception Occured in getting the VID request file" + e.getMessage());
			return e.getMessage();
		}
	}

	/**
	 * The method will post request and generate output file for VID generation
	 * 
	 * @param listOfFiles
	 * @param urlPath
	 * @param keywordToFind
	 * @param generateOutputFileKeyword
	 * @param code
	 * @return true or false
	 */
	public static String postRequestAndGetResponseForVIDGeneration(String content, String urlPath, String cookieName,
			String cookieValue) {
		try {
			return postStrContentRequestWithCookie(content, urlPath, cookieName, cookieValue);
		} catch (Exception e) {
			IDASCRIPT_LOGGER.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return e.getMessage();
		}
	}

	protected boolean verifyResponseUsingDigitalSignature(String resonseContent, String digitalSignature) {
		String dgPath = RunConfigUtil.objRunConfig.getValidateSignaturePath().replace("$signature$", digitalSignature);
		String signatureApiPath = RunConfigUtil.objRunConfig.getEncryptUtilBaseUrl() + dgPath;
		Response response = RestClient.postRequest(signatureApiPath, resonseContent, MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JSON);
		if (response.asString().contains("success"))
			return true;
		else
			return false;
	}

	public static void copyAuthTestResource() {
		AdminTestUtil.copymoduleSpecificAndConfigFile("ida");
	}

	public static void removeOldMosipTempTestResource() {
		File authTestFile = new File(RunConfigUtil.getGlobalResourcePath() + "/" + RunConfigUtil.resourceFolderName);
		if (authTestFile.exists())
			if (FileUtil.deleteDirectory(authTestFile))
				IDASCRIPT_LOGGER.info("Old " + RunConfigUtil.resourceFolderName + " folder successfully deleted!!");
			else
				IDASCRIPT_LOGGER.error("Old " + RunConfigUtil.resourceFolderName + " folder not deleted.");
	}

	/**
	 * The method will get request and generate output file with return repose
	 * 
	 * @param listOfFiles
	 * @param urlPath
	 * @param keywordToFind
	 * @param generateOutputFileKeyword
	 * @param code
	 * @return String , response for post request
	 */
	protected String getRequestAndGenerateOuputFileWithResponse(String parentFile, String urlPath,
			String generateOutputFileKeyword, String cookieName, String cookieValue) {
		FileOutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(parentFile + "/" + generateOutputFileKeyword + ".json");
			String responseJson = getResponseWithCookie(urlPath, cookieName, cookieValue);
			GlobalMethods.reportResponse(null, urlPath, responseJson, true);
			outputStream.write(responseJson.getBytes());
			return responseJson;
		} catch (Exception e) {
			IDASCRIPT_LOGGER.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return e.getMessage();
		} finally {
			AdminTestUtil.closeOutputStream(outputStream);
		}
	}

	public static int getNumberOfTimeWordPresentInString(String data, String keyword) {
		int i = 0;
		Pattern p = Pattern.compile(keyword);
		Matcher m = p.matcher(data);
		while (m.find()) {
			i++;
		}
		return i;
	}

	protected String putRequestWithparm(String filename, String url, String cookieName, String cookieValue) {
		try(FileReader fr = new FileReader(filename)) {
			JSONObject objectData = (JSONObject) new JSONParser().parse(fr);
			return RestClient.putRequestWithParm(url, objectData, MediaType.APPLICATION_JSON,
					MediaType.APPLICATION_JSON, cookieName, cookieValue).asString();
		} catch (Exception e) {
			IDASCRIPT_LOGGER.error("Exception: " + e);
			return e.toString();
		}
	}

	protected String putRequestWithCookie(String filename, String url, String cookieName, String cookieValue) {
		try(FileReader fr = new FileReader(filename)) {
			JSONObject objectData = (JSONObject) new JSONParser().parse(fr);
			return RestClient.putRequestWithCookie(url, objectData.toJSONString(), MediaType.APPLICATION_JSON,
					MediaType.APPLICATION_JSON, cookieName, cookieValue).asString();
		} catch (Exception e) {
			IDASCRIPT_LOGGER.error("Exception: " + e);
			return e.toString();
		}
	}

	// Added by Admin Test Team
	protected Response getRequestWithPathParm(String filename, String url, String cookieName, String cookieValue) {
		try(FileReader fr = new FileReader(filename)) {
			JSONObject objectData = (JSONObject) new JSONParser().parse(fr);
			return RestClient.getRequestWithCookieAndPathParm(url, objectData, MediaType.APPLICATION_JSON,
					MediaType.APPLICATION_JSON, cookieName, cookieValue);
		} catch (Exception e) {
			IDASCRIPT_LOGGER.error("Exception: " + e);
			return null;
		}
	}

	protected Response getRequestWithQueryParm(String filename, String url, String cookieName, String cookieValue) {
		try(FileReader fr = new FileReader(filename)) {
			JSONObject objectData = (JSONObject) new JSONParser().parse(fr);
			return RestClient.getRequestWithCookieAndQueryParm(url, objectData, MediaType.APPLICATION_JSON,
					MediaType.APPLICATION_JSON, cookieName, cookieValue);
		} catch (Exception e) {
			IDASCRIPT_LOGGER.error("Exception: " + e);
			return null;
		}
	}

	protected boolean patchRequestAndGenerateOuputFileForIntenalAuth(File[] listOfFiles, String urlPath,
			String keywordToFind, String generateOutputFileKeyword, String cookieName, String cookieValue, int code) {
		boolean bReturn = true;
		for (int j = 0; j < listOfFiles.length; j++) {
			if (listOfFiles[j].getName().contains(keywordToFind)) {
				try (FileOutputStream outputStream = new FileOutputStream(
						listOfFiles[j].getParentFile() + "/" + generateOutputFileKeyword + ".json")) {
					Response response;
					if (code == 0)
						response = patchRequestWithCookie(listOfFiles[j].getAbsolutePath(), urlPath, cookieName,
								cookieValue);
					else
						response = patchRequestWithCookie(listOfFiles[j].getAbsolutePath(), urlPath, code, cookieName,
								cookieValue);
					responseJsonToVerifyDigtalSignature = response.asString();
					responseDigitalSignatureValue = response.getHeader(responseDigitalSignatureKey);
					GlobalMethods.reportResponse(response.getHeaders().asList().toString(), urlPath, response);
					outputStream.write(response.asString().getBytes());
				} catch (Exception e) {
					bReturn = false;
					IDASCRIPT_LOGGER.error(GlobalConstants.EXCEPTION_STRING_2 + e.getMessage());
					break;
				}
			}
		}
		return bReturn;
	}

	protected Response postRequestAndGenerateOuputFileAndReturnResponse(File[] listOfFiles, String urlPath,
			String keywordToFind, String generateOutputFileKeyword, String cookieName, String cookieValue, int code) {
		Response response = null;
		for (int j = 0; j < listOfFiles.length; j++) {
			if (listOfFiles[j].getName().contains(keywordToFind)) {
				try (FileOutputStream outputStream = new FileOutputStream(
						listOfFiles[j].getParentFile() + "/" + generateOutputFileKeyword + ".json")) {

					if (code == 0)
						response = postRequestWithCookie(listOfFiles[j].getAbsolutePath(), urlPath, cookieName,
								cookieValue);
					else
						response = postRequestWithCookie(listOfFiles[j].getAbsolutePath(), urlPath, code, cookieName,
								cookieValue);
					responseJsonToVerifyDigtalSignature = response.asString();
					responseDigitalSignatureValue = response.getHeader(responseDigitalSignatureKey);
					GlobalMethods.reportResponse(response.getHeaders().asList().toString(), urlPath, response);
					outputStream.write(response.asString().getBytes());
				} catch (Exception e) {
					IDASCRIPT_LOGGER.error(GlobalConstants.EXCEPTION_STRING_2 + e);
					response = null;
					break;
				}
			}
		}

		return response;
	}


	protected Response deleteRequestWithPathParm(String filename, String url, String cookieName, String cookieValue) {
		try(FileReader fr = new FileReader(filename)) {
			JSONObject objectData = (JSONObject) new JSONParser().parse(fr);
			return RestClient.deleteRequestWithCookieAndPathParm(url, objectData, MediaType.APPLICATION_JSON,
					MediaType.APPLICATION_JSON, cookieName, cookieValue);
		} catch (Exception e) {
			IDASCRIPT_LOGGER.error("Exception: " + e);
			return null;
		}
	}

	protected String putRequestWithQueryparm(String filename, String url, String cookieName, String cookieValue) {
		try(FileReader fr = new FileReader(filename)) {
			JSONObject objectData = (JSONObject) new JSONParser().parse(fr);
			return RestClient.putRequestWithQueryParm(url, objectData, MediaType.APPLICATION_JSON,
					MediaType.APPLICATION_JSON, cookieName, cookieValue).asString();
		} catch (Exception e) {
			IDASCRIPT_LOGGER.error("Exception: " + e);
			return e.toString();
		}
	}

	protected String putRequestWithParameter(JSONObject objectData, String url, String cookieName, String cookieValue) {
		try {
			return RestClient.putRequestWithCookie(url, objectData.toJSONString(), MediaType.APPLICATION_JSON,
					MediaType.APPLICATION_JSON, cookieName, cookieValue).asString();
		} catch (Exception e) {
			IDASCRIPT_LOGGER.error("Exception: " + e);
			return e.toString();
		}
	}

	protected Response patchRequestWithParameter(JSONObject objectData, String url, String cookieName,
			String cookieValue) {
		try {
			return RestClient.patchRequestWithCookie(url, objectData.toJSONString(), MediaType.APPLICATION_JSON,
					MediaType.APPLICATION_JSON, cookieName, cookieValue);
		} catch (Exception e) {
			IDASCRIPT_LOGGER.error("Exception: " + e);
			return null;
		}
	}

	protected Response postRequestWithCookieforPublishPolicy(JSONObject objectData, String url, String cookieName,
			String cookieValue) {
		try {
			return RestClient.postRequestWithCookie(url, objectData.toJSONString(), MediaType.APPLICATION_JSON,
					MediaType.APPLICATION_JSON, cookieName, cookieValue);
		} catch (Exception e) {
			IDASCRIPT_LOGGER.error("Exception: " + e);
			return null;
		}
	}

}
