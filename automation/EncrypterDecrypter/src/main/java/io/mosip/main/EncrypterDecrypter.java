package io.mosip.main;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import javax.ws.rs.core.MediaType;

import org.apache.commons.io.FileDeleteStrategy;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.moisp.Dto.CryptomanagerDto;
import io.moisp.Dto.CryptomanagerRequestDto;
import io.mosip.dto.DecrypterDto;
import io.mosip.entity.TokenGenerationEntity;
import io.mosip.util.ApplicationLibrary;
import io.mosip.util.CryptoUtil;
import io.mosip.util.HMACUtils;
import io.mosip.util.TokenGeneration;
import io.restassured.response.Response;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

public class EncrypterDecrypter {
	String validToken="";
	TokenGeneration generateToken=new TokenGeneration();
	TokenGenerationEntity tokenEntity=new TokenGenerationEntity();
	private static Logger logger = Logger.getLogger(EncrypterDecrypter.class);
	private String decrypterURL = "";
	private String encrypterURL = "";
	private String applicationId = "REGISTRATION";
	InputStream outstream = null;
	static ApplicationLibrary applnMethods=new ApplicationLibrary();
	public void generateHash(byte[] fileByte) {
		if (fileByte != null) {
			HMACUtils.update(fileByte);
		}	
	}
	public String getToken(String tokenType) {
		String tokenGenerationProperties=generateToken.readPropertyFile(tokenType);
		tokenEntity=generateToken.createTokenGeneratorDto(tokenGenerationProperties);
		String token=generateToken.getToken(tokenEntity);
		return token;
		}
	public File decryptFile(JSONObject decryptDto, String destinationPath, String fileName,String url,String token)
			throws IOException, ZipException, ParseException, org.json.simple.parser.ParseException {
		logger.info(destinationPath);
		destinationPath = destinationPath + "//TemporaryValidPackets";
		File folder = new File(destinationPath);
		folder.mkdirs();
		destinationPath = destinationPath + "//" + fileName;
		Response response = applnMethods.postRequestToDecrypt( url,decryptDto,MediaType.APPLICATION_JSON,MediaType.APPLICATION_JSON,token);
		JSONObject data = (JSONObject) new JSONParser().parse(response.asString());
		JSONObject responseObject = (JSONObject) data.get("response");
		byte[] decryptedPacket = CryptoUtil.decodeBase64(responseObject.get("data").toString());
		outstream = new ByteArrayInputStream(decryptedPacket);
		logger.info("Outstream is " + outstream);
		FileOutputStream fos = new FileOutputStream(destinationPath);
		fos.write(decryptedPacket);
		fos.close();
		outstream.close();
		ZipFile zipFile = new ZipFile(destinationPath);
		zipFile.extractAll(destinationPath.substring(0, destinationPath.lastIndexOf('.')));
		File extractedFile = new File(destinationPath.substring(0, destinationPath.lastIndexOf('.')));
		return extractedFile;
	}

	public File extractFromDecryptedPacket(String destinationPath, String fileName) {
		String temporaryPath = destinationPath + "//" + fileName;
		destinationPath = destinationPath + "//TemporaryValidPackets";
		File folder = new File(destinationPath);
		folder.mkdirs();
		destinationPath = destinationPath + "//" + fileName;
		try {
			FileUtils.copyFile(new File(temporaryPath), new File(destinationPath));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		ZipFile zipFile = null;
		try {
			zipFile = new ZipFile(destinationPath);
		} catch (ZipException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			zipFile.extractAll(destinationPath.substring(0, destinationPath.lastIndexOf('.')));
		} catch (ZipException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		File extractedFile = new File(destinationPath.substring(0, destinationPath.lastIndexOf('.')));
		return extractedFile;
	}

	public void encryptFile(File f, String sourcePath, String destinationPath, String fileName,String url,String token)
			throws ZipException, FileNotFoundException, IOException, org.json.simple.parser.ParseException {
		
		File folder = new File(destinationPath);
		folder.mkdirs();
		org.zeroturnaround.zip.ZipUtil.pack(new File(sourcePath + "/" + f.getName().substring(0,f.getName().lastIndexOf("."))),
				new File(destinationPath + "/" + fileName + ".zip"));
		File file1 = new File(destinationPath + "/" + fileName + ".zip");
	    JSONObject decryptedFileBody = new JSONObject();
		decryptedFileBody = generateCryptographicDataEncryption(file1);
		Response response = applnMethods.postRequestToDecrypt(url,decryptedFileBody,MediaType.APPLICATION_JSON,MediaType.APPLICATION_JSON,token);
		JSONObject data = (JSONObject) new JSONParser().parse(response.asString());
		JSONObject responseObject = (JSONObject) data.get("response");
		// String encryptedPacketString=
		// CryptoUtil.encodeBase64(data.get("data").toString().getBytes());
		byte[] encryptedPacket = responseObject.get("data").toString().getBytes();
		outstream = new ByteArrayInputStream(encryptedPacket);
		logger.info("Outstream is " + outstream);
		FileOutputStream fos = new FileOutputStream(destinationPath + "/" + fileName + ".zip");
		fos.write(encryptedPacket);
		fos.close();
		outstream.close();

	}

	public void destroyFiles(String filePath) throws IOException {
		logger.info("Destroying Files");
		filePath = filePath + "//TemporaryValidPackets";
		File file = new File(filePath);
		File[] listOfFiles = file.listFiles();

		for (File f : listOfFiles) {
			if (f.isFile()) {
				try {
					FileDeleteStrategy.FORCE.delete(f);
					logger.info("File Was Deleted");
				} catch (Exception e) {
					logger.info(f.getName() + " Was Not Deleted");
					e.printStackTrace();
				}
			} else if (f.isDirectory()) {
				try {
					FileUtils.deleteDirectory(f);
					logger.info("Folder Was Deleted");
				} catch (Exception e) {
					logger.info(f.getName() + "  Was Not Deleted");
				}
			}
		}
		try {
			FileUtils.deleteDirectory(file);
			logger.info("Decrypted File Was Deleted");
		} catch (Exception e) {
			logger.info("Decrypted File Has Some Files In It");
		}
	}

	/*
	 * public void revertPacketToValid(String filePath) throws
	 * FileNotFoundException, IOException { File zipFile = new
	 * File(filePath+".zip"); JSONObject cryptographicRequest=new JSONObject();
	 * cryptographicRequest=generateCryptographicDataEncryption(zipFile); Response
	 * response=applnMethods.postRequestToDecrypt(cryptographicRequest,
	 * encrypterURL); try { JSONObject data= (JSONObject) new
	 * JSONParser().parse(response.asString()); byte[] encryptedPacket =
	 * CryptoUtil.decodeBase64(data.get("data").toString()); outstream = new
	 * ByteArrayInputStream(encryptedPacket); logger.info("Outstream is "+
	 * outstream); FileOutputStream fos= new FileOutputStream(filePath+".zip");
	 * fos.write(encryptedPacket); fos.close(); outstream.close(); } catch
	 * (ParseException e) { // TODO Auto-generated catch block e.printStackTrace();
	 * } }
	 */
	@SuppressWarnings("unchecked")
	public JSONObject generateCryptographicData(File file) {
		JSONObject cryptographicRequest = new JSONObject();
		JSONObject decryptionRequest = new JSONObject();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd'T'HHmmssSSS");
		InputStream encryptedPacket = null;
		DecrypterDto decrypterDto = new DecrypterDto();
		CryptomanagerRequestDto cryptoRequest = new CryptomanagerRequestDto();
		CryptomanagerDto request = new CryptomanagerDto();
		String centerId = file.getName().substring(0, 5);
		String machineId = file.getName().substring(5, 10);
		try {
			encryptedPacket = new FileInputStream(file);
			byte[] fileInBytes = FileUtils.readFileToByteArray(file);
			// String encryptedPacketString=
			// Base64.getEncoder().encodeToString(fileInBytes);
			String encryptedPacketString = IOUtils.toString(encryptedPacket, "UTF-8");
			encryptedPacketString = encryptedPacketString.replaceAll("\\s+", "");
			String registrationId = file.getName().substring(0, file.getName().lastIndexOf('.'));
			String packetCreatedDateTime = registrationId.substring(registrationId.length() - 14);
			int n = 100 + new Random().nextInt(900);
			String milliseconds = String.valueOf(n);
			encryptedPacket.close();
			Date date = formatter.parse(packetCreatedDateTime.substring(0, 8) + "T"
					+ packetCreatedDateTime.substring(packetCreatedDateTime.length() - 6) + milliseconds);
			LocalDateTime ldt = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
			Date currentDate = new Date();
			LocalDateTime requestTime = LocalDateTime.ofInstant(currentDate.toInstant(), ZoneId.systemDefault());

			decrypterDto.setApplicationId(applicationId);
			decrypterDto.setReferenceId(centerId + "_" + machineId);
			decrypterDto.setData(encryptedPacketString);
			decrypterDto.setTimeStamp(ldt);
			request.setRequesttime(requestTime);
			ObjectMapper mapper = new ObjectMapper();
			mapper.registerModule(new JavaTimeModule());
			cryptographicRequest.put("applicationId", applicationId);
			cryptographicRequest.put("data", encryptedPacketString);
			cryptographicRequest.put("referenceId", centerId + "_" + machineId);
			cryptographicRequest.put("timeStamp", decrypterDto.getTimeStamp().atOffset(ZoneOffset.UTC).toString());
			decryptionRequest.put("id", "");
			decryptionRequest.put("metadata", "");
			decryptionRequest.put("request", cryptographicRequest);
			decryptionRequest.put("requesttime", request.getRequesttime().atOffset(ZoneOffset.UTC).toString());
			decryptionRequest.put("version", "");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			logger.info("Could Not ");
		} catch (IOException e) {

			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return decryptionRequest;
	}

	@SuppressWarnings("unchecked")
	public JSONObject generateCryptographicDataEncryption(File file) {
		JSONObject encryptRequest = new JSONObject();
		CryptomanagerDto request = new CryptomanagerDto();
		JSONObject cryptographicRequest = new JSONObject();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd'T'HHmmssSSS");
		InputStream encryptedPacket = null;
		DecrypterDto decrypterDto = new DecrypterDto();
		String centerId = file.getName().substring(0, 5);
		try {
			encryptedPacket = new FileInputStream(file);
			byte[] fileInBytes = FileUtils.readFileToByteArray(file);
			String encryptedPacketString = Base64.getEncoder().encodeToString(fileInBytes);
			// String encryptedPacketString = IOUtils.toString(encryptedPacket, "UTF-8");
			encryptedPacketString = encryptedPacketString.replaceAll("\\s+", "");
			String registrationId = file.getName().substring(0, file.getName().lastIndexOf('.'));
			String packetCreatedDateTime = registrationId.substring(registrationId.length() - 14);
			int n = 100 + new Random().nextInt(900);
			String milliseconds = String.valueOf(n);
			encryptedPacket.close();
			Date date = formatter.parse(packetCreatedDateTime.substring(0, 8) + "T"
					+ packetCreatedDateTime.substring(packetCreatedDateTime.length() - 6) + milliseconds);
			LocalDateTime ldt = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
			Date currentDate = new Date();
			LocalDateTime requestTime = LocalDateTime.ofInstant(currentDate.toInstant(), ZoneId.systemDefault());
			decrypterDto.setApplicationId(applicationId);
			decrypterDto.setReferenceId(centerId);
			decrypterDto.setData(encryptedPacketString);
			decrypterDto.setTimeStamp(ldt);
			request.setRequesttime(requestTime);
			ObjectMapper mapper = new ObjectMapper();
			mapper.registerModule(new JavaTimeModule());
			cryptographicRequest.put("applicationId", applicationId);
			cryptographicRequest.put("data", encryptedPacketString);
			cryptographicRequest.put("referenceId", centerId);
			cryptographicRequest.put("timeStamp", decrypterDto.getTimeStamp().atOffset(ZoneOffset.UTC).toString());
			encryptRequest.put("id", "");
			encryptRequest.put("metadata", "");
			encryptRequest.put("request", cryptographicRequest);
			encryptRequest.put("requesttime", request.getRequesttime().atOffset(ZoneOffset.UTC).toString());
			encryptRequest.put("version", "");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {

			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return encryptRequest;
	}

	public byte[] generateCheckSum(File[] listOfFiles) throws FileNotFoundException, IOException, ParseException, org.json.simple.parser.ParseException {
		JSONArray hashSequence1;
		byte[] hashCodeGenerated = null;
		for (File f : listOfFiles) {
			if (f.getName().contains("packet_meta_info.json")) {
				FileReader metaFileReader = new FileReader(f.getPath());
				JSONObject objectData = (JSONObject) new JSONParser().parse(metaFileReader);
				JSONObject identity = (JSONObject) objectData.get("identity");
				metaFileReader.close();
				hashSequence1 = (JSONArray) identity.get("hashSequence1");
				logger.info("hashSequence1....... : " + hashSequence1);
				for (Object obj : hashSequence1) {
					JSONObject label = (JSONObject) obj;
					logger.info("obj : " + label.get("label"));
					if (label.get("label").equals("applicantBiometricSequence")) {
						@SuppressWarnings("unchecked")
						List<String> docs = (List<String>) label.get("value");
						logger.info("list of documents :: " + docs);
						generateBiometricsHash(docs, listOfFiles);
					} else if (label.get("label").equals("introducerBiometricSequence")) {
						@SuppressWarnings("unchecked")
						List<String> docs = (List<String>) label.get("value");
						logger.info("list of documents :: " + docs);
						generateBiometricsHash(docs, listOfFiles);
					} else if (label.get("label").equals("applicantDemographicSequence")) {
						@SuppressWarnings("unchecked")
						List<String> docs = (List<String>) label.get("value");
						logger.info("list of documents :: " + docs);
						generateDemographicsHash(docs, listOfFiles);
					}
				}
				hashCodeGenerated = HMACUtils.digestAsPlainText(HMACUtils.updatedHash()).getBytes();
			}
		}
		return hashCodeGenerated;
	}

	private void generateBiometricsHash(List<String> docs, File[] listOfFiles) {
		byte[] fileByte = null;
		for (File file : listOfFiles) {
			if (file.getName().equalsIgnoreCase("Biometric")) {
				File[] demographicFiles = file.listFiles();
				for (File demoFiles : demographicFiles) {
					for (String fileName : docs) {
						if (fileName.equals(demoFiles.getName().substring(0, demoFiles.getName().lastIndexOf('.')))) {
							try {
								FileInputStream inputStream = new FileInputStream(demoFiles);
								fileByte = IOUtils.toByteArray(inputStream);
								generateHash(fileByte);
								inputStream.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		}
	}

	private void generateDemographicsHash(List<String> docs, File[] listOfFiles) {
		byte[] fileByte = null;
		for (File file : listOfFiles) {
			if (file.getName().equalsIgnoreCase("Demographic")) {
				File[] demographicFiles = file.listFiles();
				for (File demoFiles : demographicFiles) {
					for (String fileName : docs) {
						if (fileName.equals(demoFiles.getName().substring(0, demoFiles.getName().lastIndexOf('.')))) {
							try {
								FileInputStream inputStream = new FileInputStream(demoFiles);
								fileByte = IOUtils.toByteArray(inputStream);
								generateHash(fileByte);
								inputStream.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		}
	}

	@Test
	public void getpackts() throws IOException, ZipException, ParseException, org.json.simple.parser.ParseException {
		EncrypterDecrypter encryptDecrypt = new EncrypterDecrypter();
		Properties prop=new Properties();
		String propertyFilePath = System.getProperty("user.dir") + "/src/config/Apis.properties";
	 	FileReader reader;
		try {
			reader = new FileReader(new File(propertyFilePath));
			prop.load(reader);
		} catch ( IOException e) {
			logger.error("Propert File Was Not Found",e);
		}
		String packetPath=prop.getProperty("packetPath");
		File file = new File(packetPath);
		File[] listOfPackets = file.listFiles();
		for (File f : listOfPackets) {
			if (f.getName().contains(".zip")) {
				JSONObject jsonObject = encryptDecrypt.generateCryptographicData(f);
				encryptDecrypt.decryptFile(jsonObject,
						packetPath, f.getName(),decrypterURL,validToken);
			}
		}

	}
	/*@Test
	public void encryptPacket() throws IOException, ZipException, ParseException, org.json.simple.parser.ParseException {
		EncrypterDecrypter encryptDecrypt = new EncrypterDecrypter();
		Properties prop=new Properties();
		String propertyFilePath = System.getProperty("user.dir") + "/src/config/Apis.properties";
	 	FileReader reader;
		try {
			reader = new FileReader(new File(propertyFilePath));
			prop.load(reader);
		} catch ( IOException e) {
			logger.error("Propert File Was Not Found",e);
		}
		String packetPath=prop.getProperty("packetPath");
		File file = new File(packetPath);
		File[] listOfPackets = file.listFiles();
		for (File f : listOfPackets) {
			if (f.getName().contains(".zip")) {
				JSONObject jsonObject = encryptDecrypt.generateCryptographicDataEncryption(f);
				encryptDecrypt.encryptFile(f,packetPath,packetPath+"/encyptedFile",f.getName().substring(0,f.getName().lastIndexOf(".")),encrypterURL,validToken);
			}
		}
	}*/
	@BeforeClass
	public void getToken() {
		validToken=getToken("syncTokenGenerationFilePath");
		
		Properties prop=new Properties();
		String propertyFilePath = System.getProperty("user.dir") + "/src/config/Apis.properties";
	 	FileReader reader;
		try {
			reader = new FileReader(new File(propertyFilePath));
			prop.load(reader);
		} catch ( IOException e) {
			logger.error("Propert File Was Not Found",e);
		}
		 encrypterURL=prop.getProperty("encryptUrl");
		 decrypterURL=prop.getProperty("decryptUrl");
	}
}
