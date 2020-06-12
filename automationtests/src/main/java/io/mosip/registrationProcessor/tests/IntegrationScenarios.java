package io.mosip.registrationProcessor.tests;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.parser.ParseException;
import org.testng.ITest;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.internal.BaseTestMethod;
import org.testng.internal.TestResult;

import io.mosip.dbaccess.RegProcTransactionDb;
import io.mosip.registrationProcessor.service.IntegMethods;
import io.mosip.registrationProcessor.util.DecryptPacket;
import io.mosip.registrationProcessor.util.EncryptData;
import io.mosip.registrationProcessor.util.RegProcApiRequests;
import io.mosip.service.BaseTestCase;
import net.lingala.zip4j.exception.ZipException;

/**
 * 
 * @author M1047227
 *
 */

public class IntegrationScenarios extends BaseTestCase implements ITest {
	RegProcTransactionDb transaction = new RegProcTransactionDb();
	EncryptData encryptData = new EncryptData();
	protected static String testCaseName = "";
	IntegMethods scenario = new IntegMethods();
	String moduleName = "RegProc";
	String apiName = "Integration";
	DecryptPacket decrypt = new DecryptPacket();

	@DataProvider(name = "IntegrationScenarios")
	public File[] getIntegrationScenarioPackets() {
		RegProcApiRequests apiRequests = new RegProcApiRequests();
		File file = new File(apiRequests.getResourcePath() + "regProc/IntegrationScenarios");
		File[] listOfPackets = file.listFiles();
		List<File> insideFiles = new ArrayList<File>();

		for (File file1 : listOfPackets) {
			insideFiles.add(file1);
		}

		File[] objArray = new File[insideFiles.size()];
		for (int i = 0; i < insideFiles.size(); i++) {
			objArray[i] = insideFiles.get(i);
			/*
			 * for(File packet:insideFiles.get(i).listFiles()) {
			 * if(packet.getName().contains(".zip")) { File
			 * f=scenario.decryptPacket(packet); scenario.updateRegId(f); File
			 * updatedFile=scenario.updateCheckSum(f); scenario.encryptFile(updatedFile);
			 * scenario.destroyTempFiles(packet); } }
			 */
		}

		return objArray;

	}

	@Test(dataProvider = "IntegrationScenarios")
	public void syncSmokepacketUploadSmoke(File[] listOfInvpackets)
			throws FileNotFoundException, IOException, ParseException {
		File file1 = new File(listOfInvpackets[0].getPath());
		File[] folder = file1.listFiles();
		for (File file : folder) {
			if(file.getName().toLowerCase().contains("extracted")) {
				
				for(File genPacket:file.listFiles()) {
			if (genPacket.getName().equals("generatedPacket")) {
				File[] listOfPackets = genPacket.listFiles();
				for (File packet : listOfPackets) {
					if (packet.getName().contains(".zip")) {
						boolean syncResponse = scenario.syncList(packet);
						if (syncResponse) {
							boolean uploadPacket = scenario.UploadPacket(packet);
							if (uploadPacket) {
								scenario.getStatus("ValidPacket");
							}
						}
					}
					String regId = packet.getName().substring(0, packet.getName().lastIndexOf('.'));
					try {
						Thread.sleep(60000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					List<String> status = transaction.readStatus(regId);
					/*
					 * if(status.size()==15) { Assert.assertTrue(true); } else Assert.fail();
					 */
				}
			}
			}
			}
		}

	}

	
	@BeforeMethod(alwaysRun = true)
	public void getTestCaseName(Method method, Object[] testdata, ITestContext ctx) {
		Object[] obj = (Object[]) testdata[0];
		String name = "";
		String stageName = "";
		if (obj[0] instanceof File) {
			File file = (File) obj[0];
			name = file.getName();
			stageName = file.getParentFile().getName();
		}
		testCaseName = moduleName + "_" + apiName + "_" + name.toString();

	}

	@AfterMethod(alwaysRun = true)
	public void setResultTestName(ITestResult result) {

		Field method;
		try {
			method = TestResult.class.getDeclaredField("m_method");
			method.setAccessible(true);
			method.set(result, result.getMethod().clone());
			BaseTestMethod baseTestMethod = (BaseTestMethod) result.getMethod();
			Field f = baseTestMethod.getClass().getSuperclass().getDeclaredField("m_methodName");
			f.setAccessible(true);
			f.set(baseTestMethod, IntegrationScenarios.testCaseName);
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			logger.error("Exception occurred in Sync class in setResultTestName method " + e);
		}

	}

	@Override
	public String getTestName() {
		return this.testCaseName;
	}

	@BeforeClass
	public void generateDupePackets() throws ZipException, IOException, java.text.ParseException {
		RegProcApiRequests apiRequests = new RegProcApiRequests();
		File file = new File(apiRequests.getResourcePath() + "regProc/IntegrationScenarios");
		File[] listOfPackets = file.listFiles();
		List<File> insideFiles = new ArrayList<File>();

		for (File file1 : listOfPackets) {
			
			File[] packets = file1.listFiles();
			String regId = "";
			for (File packet : packets) {
				if (packet.getName().contains(".zip")) {
					File[] zippedPacket = decrypt.unzipPacket(packet);
					for (File encryptedFile : zippedPacket) {
						if (encryptedFile.getName().contains(".zip")) {
							if (encryptedFile.getName().contains("_id")) {
								File decryptedPacket = scenario.decryptPacket(encryptedFile);
								String centerId = decryptedPacket.getName().substring(0, 5);
								String machineId = decryptedPacket.getName().substring(5, 10);
								regId = scenario.generateRegId(centerId, machineId, decryptedPacket.getName()
										.substring(0, decryptedPacket.getName().lastIndexOf("_")));

								scenario.updateRegId(decryptedPacket, regId);
								scenario.updateCheckSum(decryptedPacket);
								scenario.encryptFile(decryptedPacket, regId + "_id");
							}

						}
					}
					for (File encryptedPacket : zippedPacket) {
						if (encryptedPacket.getName().contains("_evidence")) {
							Path source = Paths.get(encryptedPacket.getAbsolutePath());
							Path newdir = Paths
									.get(encryptedPacket.getParent() + "/generatedPacket/" + regId + "_evidence.zip");

							Files.move(source, newdir);
						} else if (encryptedPacket.getName().contains("_optional")) {
							Path source = Paths.get(encryptedPacket.getAbsolutePath());
							Path newdir = Paths
									.get(encryptedPacket.getParent() + "/generatedPacket/" + regId + "_optional.zip");

							Files.move(source, newdir);
						}
					}
					
				}
			}
			File generatedFiles=new File(file1.getAbsolutePath()+"/ExtractedPacket/generatedPacket");
			decrypt.createZipFile(generatedFiles.getAbsolutePath()+"/"+regId,generatedFiles.listFiles());
		}

	}
}
