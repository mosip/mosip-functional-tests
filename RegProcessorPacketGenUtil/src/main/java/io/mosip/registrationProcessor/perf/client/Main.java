package io.mosip.registrationProcessor.perf.client;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.hibernate.Session;

import io.mosip.dbentity.TokenGenerationEntity;
import io.mosip.dbentity.UsernamePwdTokenEntity;
import io.mosip.registrationProcessor.perf.service.SyncRequestCreater;
import io.mosip.registrationProcessor.perf.service.TestDataGenerator;
import io.mosip.registrationProcessor.perf.util.PropertiesUtil;
import io.mosip.registrationProcessor.perf.util.TokenGeneration;
import io.mosip.resgistrationProcessor.perf.dbaccess.DBUtil;

public class Main {
	private static Logger logger = Logger.getLogger(Main.class);

	public Main() {
	}

	public static void main(String[] args) {

		if (args.length != 1) {
			System.err.println("Receives only one parameter");
			System.out.println(
					"packet_gen - To generate packets, for multiple new packets or child packet with parent RID");
			System.out.println("new_packet - To generate a new packet");
			System.out.println("sync_data - To generate sync data(test data to reg proc sync API)");
			System.out.println("update_packet - to update packet, must provide a processed reg id in properties file");
			System.out.println("test_data - To generate test data for idrepo create identity API");
			System.out.println("sync_data_update - To generate sync data for update packet flow");
			System.out.println("lost_uin - To generate a packet for lost UIN");
			System.out.println("lost_uin_sync - To generate sync data for lost UIN");
			System.out.println("decrypt_packet - To decrypt an encrypted packet");
			System.out.println(
					"sync_data_multiple - To create sync data for multi kind packets by reading from a checksum log file");
			System.out.println("packet_upload - To sync and upload packets");
		} else {
			String mode = args[0];
			switch (mode) {
			case "packet_gen":
				/*
				 * Generates packets and outputs packet file path and checksum to a file. It
				 * will also generates a file having a list of registration IDs
				 */
				try {
					createNewPackets();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				break;

			case "new_packet":

				createSingleNewPacket();

				break;

			case "sync_data":
				/*
				 * Generates a test data file to be input to apache jmeter script to sync
				 * regsitration packets
				 */
				generateSyncData();
				break;
			case "test_data": // To generate test data for ID Repo create API this data is used for IDRepo and
								// IDA
				try {
					generateTestData();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				break;
			case "update_packet":

				updatePacket();

				break;
			case "sync_data_update":
				generateSyncDataForUpdatePacket();
				break;
			case "lost_uin":
				generatePacketForLostUIN();
				break;
			case "lost_uin_sync":
				generateSyncDataForLostUIN();
				break;
			case "sync_data_multiple":
				generateSyncDataForGenericPackets();
				break;
			case "packet_upload":
				uploadSyncedPacket();
				break;
			case "decrypt_packet":
				decryptPacket();
				break;
			default:
				System.out.println("Receives either of below parameters:");
				System.out.println("packet_gen: To generate packets");
				System.out.println(
						"sync_data: To generate sync data in a CSV file by using the checksums.txt file of generated packets");
				System.out.println("test_data: To generate test data in a CSV file");
			}

		}

	}

	private static void uploadSyncedPacket() {
		PacketUploadClient client = new PacketUploadClient();
		client.uploadPacket();
	}

	private static void generateSyncDataForGenericPackets() {

		SyncRequestCreater syncRequest = new SyncRequestCreater("GENERIC");

		String CONFIG_FILE = "config.properties";
		PropertiesUtil prop = new PropertiesUtil(CONFIG_FILE);
		syncRequest.createSyncRequestMaster(prop);

	}

	private static void createNewPackets() throws InterruptedException {
		String CONFIG_FILE = "config.properties";
		PropertiesUtil prop = new PropertiesUtil(CONFIG_FILE);
		File parentFile = new File(prop.PARENT_FOLDER);
//		File[] filesToDelete = parentFile.listFiles();
//		for (File f : filesToDelete) {
//			try {
//				FileUtils.forceDelete(f);
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
		String authToken = "";
		try {
			authToken = generateCommonAuthTokenForAllThreads();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println("authToken:- " + authToken);
		PacketCreationClient packetCreationClient = new PacketCreationClient(authToken);
		System.out.println("Threads objects creating");
		List<Thread> listOfThreadsForPacketCreation = new ArrayList<>();

		for (int i = 0; i < Integer.parseInt(prop.NUMBER_OF_THREADS_PACKET_CREATION); i++) {
			listOfThreadsForPacketCreation.add(new Thread(packetCreationClient));
		}
		logger.info("Starting the threads");
		listOfThreadsForPacketCreation.forEach(thread -> {
			thread.start();
		});
		logger.info("Terminating the threads");
		listOfThreadsForPacketCreation.forEach(thread -> {
			try {
				thread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});

	}

	private static void createSingleNewPacket() {

		NewPacketClient client = new NewPacketClient();
		try {
			client.createNewPacket();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static void decryptPacket() {

		DecryptPacketClient client = new DecryptPacketClient();
		try {
			client.decryptPacket();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static void generateSyncDataForLostUIN() {
		SyncRequestCreater syncRequest = new SyncRequestCreater("LOST");

		String CONFIG_FILE = "config.properties";
		PropertiesUtil prop = new PropertiesUtil(CONFIG_FILE);
		syncRequest.createSyncRequestMaster(prop);
	}

	private static void generatePacketForLostUIN() {
		LostPacketClient client = new LostPacketClient();
		client.createLostPacket();
	}

	private static void generateSyncData() {

		SyncRequestCreater syncRequest = new SyncRequestCreater("NEW");

		String CONFIG_FILE = "config.properties";
		PropertiesUtil prop = new PropertiesUtil(CONFIG_FILE);
		syncRequest.createSyncRequestMaster(prop);
	}

	private static void generateSyncDataForUpdatePacket() {
		SyncRequestCreater syncRequest = new SyncRequestCreater("UPDATE");

		String CONFIG_FILE = "config.properties";
		PropertiesUtil prop = new PropertiesUtil(CONFIG_FILE);
		syncRequest.createSyncRequestMaster(prop);
	}

	private static void updatePacket() {

		UpdatePacketClient client = new UpdatePacketClient();
		client.updatePacket();

	}

	private static void generateTestData() throws InterruptedException {

		TestDataClient testDataClient = new TestDataClient();

		String CONFIG_FILE = "config.properties";
		PropertiesUtil prop = new PropertiesUtil(CONFIG_FILE);
		List<Thread> listOfThreadsForTestData = new ArrayList<>();

		for (int i = 0; i < Integer.parseInt(prop.NUMBER_OF_THREADS_TEST_DATA); i++) {
			listOfThreadsForTestData.add(new Thread(testDataClient));
		}
		logger.info("Starting the threads");
		System.out.println("start");
		listOfThreadsForTestData.forEach(thread -> {
			thread.start();
		});
		logger.info("Terminating the threads");
		listOfThreadsForTestData.forEach(thread -> {
			try {
				thread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});

	}

	private static String generateCommonAuthTokenForAllThreads() throws IOException {
		String CONFIG_FILE = "config.properties";

		PropertiesUtil prop = new PropertiesUtil(CONFIG_FILE);
		TokenGeneration generateToken = new TokenGeneration();

		String TOKEN_TYPE = "syncTokenGenerationFilePath";
		String tokenGenerationFilePath = generateToken.readPropertyFile(TOKEN_TYPE);
		String token = "";
		if (prop.AUTH_TYPE_CLIENTID_SECRETKEY) {
			TokenGenerationEntity tokenEntity = new TokenGenerationEntity();
			tokenEntity = generateToken.createTokenGeneratorDtoForClientIdSecretKey(tokenGenerationFilePath);
			token = generateToken.getAuthTokenForClientIdSecretKey(tokenEntity, prop);
		} else {
			UsernamePwdTokenEntity tokenEntity1 = generateToken
					.createTokenGeneratorDtoForUserIdPassword(tokenGenerationFilePath);
			token = generateToken.getAuthTokenForUsernamePassword(tokenEntity1, prop);

		}

		return token;
	}

}
