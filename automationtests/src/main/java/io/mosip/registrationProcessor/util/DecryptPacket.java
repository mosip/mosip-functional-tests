package io.mosip.registrationProcessor.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

public class DecryptPacket {
public File[] unzipPacket(File packet) throws ZipException {
	System.out.println(packet.getAbsolutePath());
	File tempPath=new File(packet.getParent()+"/ExtractedPacket");
	tempPath.mkdir();
	ZipFile zip=new ZipFile(packet.getAbsolutePath());
	zip.extractAll(tempPath.getAbsolutePath());
	return tempPath.listFiles();
	
}

public void createZipFile(String regId,File[] listOfFiles) throws IOException {
	byte[] buffer = new byte[1024];
	 FileOutputStream fos = new FileOutputStream(regId+".zip");
	 
     ZipOutputStream zos = new ZipOutputStream(fos);
     for(File file:listOfFiles) {
    	 FileInputStream fis = new FileInputStream(file);
    	 zos.putNextEntry(new ZipEntry(file.getName()));
    	 int length;
    	 
         while ((length = fis.read(buffer)) > 0) {
             zos.write(buffer, 0, length);
         }
         zos.closeEntry();
         fis.close();
         Path fileToDeletePath = Paths.get(file.getAbsolutePath());
         Files.delete(fileToDeletePath);
     }
     zos.close();
     fos.close();
			
}

public static void main(String[] args) throws ZipException {
	DecryptPacket decryptPacket=new DecryptPacket();
	File file=new File("src\\main\\resources\\regProc\\Packets\\ValidPackets\\packteForInvalidPackets\\10003100220000120200605063933.zip");
	decryptPacket.unzipPacket(file);
}
}
