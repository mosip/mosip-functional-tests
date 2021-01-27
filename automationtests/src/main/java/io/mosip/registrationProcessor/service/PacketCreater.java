package io.mosip.registrationProcessor.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.json.simple.parser.ParseException;
import org.testng.Assert;

import com.google.gson.Gson;

import io.mosip.commons.packet.dto.PacketInfo;
import io.mosip.dbaccess.PreregDB;
import io.mosip.dbentity.TokenGenerationEntity;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.registrationProcessor.regpacket.dto.PhilIdentityObject;
import io.mosip.registrationProcessor.regpacket.dto.RegProcIdDto;
import io.mosip.registrationProcessor.regpacket.metainfo.dto.FieldValueArray;
import io.mosip.registrationProcessor.regpacket.metainfo.dto.Identity;
import io.mosip.registrationProcessor.regpacket.metainfo.dto.PacketMetaInfo;
import io.mosip.registrationProcessor.util.RegProcApiRequests;
import io.mosip.testrunner.MosipTestRunner;
import io.mosip.util.HMACUtils;
import io.mosip.util.TokenGeneration;

public class PacketCreater {

	private String SOURCE = "REGISTRATION_CLIENT";
	private String PROCESS = "NEW";
	private String SUB_FOLDER;
//	private String OLD_REGID;
//	private String NEW_REGID;
//	private String EXISTING_PACKET_DIR;

	private String TEMP_PATH_SECTION = File.separator + "temp";

	private static Logger logger = Logger.getLogger(PacketCreater.class);


	private PacketDemoDataUtil packetDataUtil;

	RegProcApiRequests apiRequests = new RegProcApiRequests();

	private PacketCreateHelper helper;

	public PacketCreater() {
		this.SUB_FOLDER = SOURCE + File.separator + PROCESS;
		packetDataUtil = new PacketDemoDataUtil();

	}

	public String createPacketFromExistingPacket(String parentDir, String existingPacketPath) throws Exception {
		PropertiesUtil prop = new PropertiesUtil();
		String created_packet = "";
		System.out.println("parent dir is " + parentDir);
		// A file separator char is already added in end of parentDir variable's value.
		String EXISTING_PACKET_DIR = parentDir + existingPacketPath;
		System.out.println("existingPacketDir :- " + EXISTING_PACKET_DIR);

		File packetParentDir = new File(EXISTING_PACKET_DIR);
		String OLD_REGID = "";
		String fileToCopy = null;
		for (File file : packetParentDir.listFiles()) {
			if (file.getName().length() == 29) {
				fileToCopy = file.getAbsolutePath();
				OLD_REGID = file.getName();
			}
		}

		System.out.println("fileToCOpy " + fileToCopy);
		System.out.println("oldRegId :- " + OLD_REGID);
		System.out.println();
		PacketCreateHelper helper = new PacketCreateHelper(OLD_REGID, EXISTING_PACKET_DIR);
		String centerId = OLD_REGID.substring(0, 5);
		String machineId = OLD_REGID.substring(5, 10);
		centerId = "10010";
		machineId = "10029";
		String userId = "110010";
		String NEW_REGID = "";
		try {
			NEW_REGID = helper.generateRegId(centerId, machineId);

			helper.emptyTempLocation();
			helper.copyFolderContentsToModify(new File(fileToCopy + File.separator + this.SUB_FOLDER));

			System.out.println("folder contents copied");
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		}

		/*
		 * tempDir is the location to modify content of file and zip those to generate a
		 * zipped packet
		 */
		String tempDir = EXISTING_PACKET_DIR + TEMP_PATH_SECTION;
		String destPacketPath = tempDir + File.separator + NEW_REGID + File.separator + this.SUB_FOLDER;

		helper.modifyDemographicAndPacketMetaInfoFile(destPacketPath, centerId, machineId, userId, prop);

		helper.computeChecksumForIdentityPacket();
		helper.computeChecksumForEvidencePacket();
		helper.computeChecksumForOptionalPacket();

		String authToken = helper.getToken("getStatusTokenGenerationFilePath");
		boolean tokenStatus = apiRequests.validateToken(authToken);
		while (!tokenStatus) {
			authToken = helper.getToken("getStatusTokenGenerationFilePath");
			tokenStatus = apiRequests.validateToken(authToken);
		}

		String parentFolder = "";

		helper.zipPacketContents(tempDir, authToken);
		created_packet = tempDir + File.separator + NEW_REGID + ".zip";
		
		return created_packet;
	}

}
