package mosip.helper;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

/**
 * This class used to move the image/file from one directory
 * 
 * 
 * @author Arjun chandramohan
 *
 */

public class MoveingFileToAnotherDirectory {
	public static void copyFile(String destinationLocation,String fileName) {
		File source = new File("src//main//resources//biometrics//"+fileName);
		//File source = new File("..\\TestDataGeneration\\Resources\\biometrics\\"+fileName);
		File dest = new File(destinationLocation+"\\"+fileName);
		try {
		    FileUtils.copyFile(source, dest);
		} catch (IOException e) {
		    e.printStackTrace();
		}
	}
}
