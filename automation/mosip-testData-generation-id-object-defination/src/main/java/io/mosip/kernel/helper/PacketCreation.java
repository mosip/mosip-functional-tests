package io.mosip.kernel.helper;

import java.io.File;
/**
 * This class will create a folder locally when folder name/ address is passed
 * 
 * @author Arjun chandramohan
 *
 */
public class PacketCreation {
	public static void createFolder(String folderName) {
		File file = new File(folderName);
		file.mkdir();

	}
}
