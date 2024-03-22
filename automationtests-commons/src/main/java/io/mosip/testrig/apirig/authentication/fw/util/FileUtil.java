package io.mosip.testrig.apirig.authentication.fw.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import io.mosip.testrig.apirig.admin.fw.util.AdminTestUtil;
import io.mosip.testrig.apirig.authentication.fw.precon.JsonPrecondtion;
import io.mosip.testrig.apirig.global.utils.GlobalConstants;

/**
 * Class is to perform all file util such as create,read files
 * 
 * @author Vignesh
 *
 */
public class FileUtil{
	
	private static final Logger FILEUTILITY_LOGGER = Logger.getLogger(FileUtil.class);

	/**
	 * The method will get list of files in a folder
	 * 
	 * @param folder, Folder path
	 * @return list of files
	 */ 
	public static List<File> getFolders(File folder) {
		List<File> list = new ArrayList<File>();
		File[] listOfFolders = folder.listFiles();
		Arrays.sort(listOfFolders);
		for (int j = 0; j < listOfFolders.length; j++) {
			if (listOfFolders[j].isDirectory())
				list.add(listOfFolders[j]);
		}
		return list;
	}
	
	/**
	 * The method will get file path from folder using file name keywords
	 * 
	 * @param folder, Folder path
	 * @param keywordToFind, file keyword to find
	 * @return File
	 */
	public static File getFilePath(File folder, String keywordToFind) {
		File[] listOfFolders = folder.listFiles();
		for (int j = 0; j < listOfFolders.length; j++) {
			if (listOfFolders[j].getName().contains(keywordToFind))
				return listOfFolders[j].getAbsoluteFile();
		}
		return null;
	}
	/**
	 * The method will get file path from folder using file name keywords
	 * 
	 * @param folder, Folder path
	 * @param keywordToFind, file keyword to find
	 * @return File
	 */
	public static File getFilePathName(File folder, String keywordToFind) {
		File[] listOfFolders = folder.listFiles();
		for (int j = 0; j < listOfFolders.length; j++) {
			if (listOfFolders[j].getName().contains(keywordToFind))
				return listOfFolders[j].getAbsoluteFile();
		}
		return folder;
		
	}
	
	/**
	 * The method verify file present in list of files
	 * 
	 * @param listOfFiles
	 * @param keywordToFind
	 * @return True or False
	 */
	public static boolean verifyFilePresent(File[] listOfFiles, String keywordToFind) {
		for (int j = 0; j < listOfFiles.length; j++) {
			if (listOfFiles[j].getName().contains(keywordToFind))
				return true;
		}
		return false;
	}
	
	/**
	 * The method will create and write file
	 * 
	 * @param fileName, file name to used
	 * @param content, content to be written in file
	 * @return True or false
	 */
	public static boolean createAndWriteFile(String fileName, String content) {
		BufferedWriter bufferedWriter = null;
		boolean bReturn = false;
		try {
			Path path = Paths.get(AuthTestsUtil.getTestFolder().getAbsolutePath() + "/" + fileName);
			Charset charset = Charset.forName(StandardCharsets.UTF_8.name());
			bufferedWriter = Files.newBufferedWriter(path, charset);
			bufferedWriter.write(content);
			bReturn = true;
		} catch (Exception e) {
			FILEUTILITY_LOGGER.error(GlobalConstants.EXCEPTION_STRING_2 + e);
		}finally {
			AdminTestUtil.closeBufferedWriter(bufferedWriter);
		}
		return bReturn;
	}
	
	/**
	 * The method will write file
	 * 
	 * @param filePath, file path to use used
	 * @param content, Content to written in file
	 * @return true or false
	 */
	public static boolean writeFile(String filePath, String content) {
		BufferedWriter bufferedWriter = null;
		boolean bReturn = false;
		try {
			Path path = Paths.get(filePath);
			bufferedWriter = Files.newBufferedWriter(path, StandardCharsets.UTF_8);
			bufferedWriter.write(content);
			bReturn =  true;
		} catch (Exception e) {
			FILEUTILITY_LOGGER.error(GlobalConstants.EXCEPTION_STRING_2 + e);
		}finally {
			AdminTestUtil.closeBufferedWriter(bufferedWriter);
		}
		return bReturn;
	}
	
	/**
	 * The method will create and write file for IDREPO
	 * 
	 * @param fileName, file name to be used
	 * @param content, content to be used
	 * @return true or false
	 */
	public static boolean createAndWriteFileForIdRepo(String fileName, String content) {
		FILEUTILITY_LOGGER.info("Retrieved identity Response from get Request is: "+content);
		BufferedWriter bufferedWriter = null;
		boolean bReturn = false;
		try {
			Path path = Paths
					.get(new File(RunConfigUtil.getResourcePath() + RunConfigUtil.objRunConfig.getStoreUINDataPath() + "/" + fileName)
							.getAbsolutePath());
			if (!path.toFile().exists()) {
				Charset charset = Charset.forName(StandardCharsets.UTF_8.name());
				createFile(path.toFile(), "");
				bufferedWriter = Files.newBufferedWriter(path, charset);
				bufferedWriter.write(JsonPrecondtion.convertJsonContentToXml(content));
			}
			bReturn =  true;
		} catch (Exception e) {
			FILEUTILITY_LOGGER.error(GlobalConstants.EXCEPTION_STRING_2 + e);
		}finally {
			AdminTestUtil.closeBufferedWriter(bufferedWriter);
		}
		return bReturn;
	}	
	
	/**
	 * The method will check file exist for idrepo
	 * 
	 * @param fileName, file name to check
	 * @return true or false
	 */
	public static boolean checkFileExistForIdRepo(String fileName) {
		Path path = Paths.get(new File(RunConfigUtil.getResourcePath() + RunConfigUtil.objRunConfig.getStoreUINDataPath()
				+ "/" + fileName).getAbsolutePath());
		return path.toFile().exists();
	}

	/**
	 * The method will write output using file outputstream
	 * 
	 * @param filePath
	 * @param content
	 */
	public void writeOutput(String filePath, String content) {
		FileOutputStream outputStream = null;
		OutputStreamWriter outputStreamWriter = null;
		try {
			outputStream = new FileOutputStream(filePath);
			outputStreamWriter = new OutputStreamWriter(outputStream, "UTF8");
			outputStreamWriter.write(content);
		} catch (IOException e) {
			FILEUTILITY_LOGGER.error("Exception : " + e.getMessage());
		}finally {
			AdminTestUtil.closeOutputStream(outputStream);
			AdminTestUtil.closeOutputStreamWriter(outputStreamWriter);
		}
	}

	/**
	 * The method will read input from file path
	 * 
	 * @param filePath
	 * @return String, content from file
	 */
	public static String readInput(String filePath) {
		StringBuffer buffer = new StringBuffer();
		FileInputStream inputStream = null;
		BufferedReader bufferedReader = null;
		try {
			inputStream = new FileInputStream(filePath);
			InputStreamReader isr = new InputStreamReader(inputStream, "UTF8");
			bufferedReader = new BufferedReader(isr);
			int ch;
			while ((ch = bufferedReader.read()) > -1) {
				buffer.append((char) ch);
			}
		} catch (IOException e) {
			FILEUTILITY_LOGGER.error("Exception : " + e.getMessage());
		}finally {
			AdminTestUtil.closeInputStream(inputStream);
			AdminTestUtil.closeBufferedReader(bufferedReader);
		}
		return buffer.toString();
	}
	  
	/**
	 * The method will create file either with dummy data or some data
	 * 
	 * @param fileName
	 * @param content
	 * @return true or false
	 */
	public static boolean createFile(File fileName, String content) {
		FileOutputStream outputStream = null;
		try {
			makeDir(fileName.getParentFile().toString());
			outputStream = new FileOutputStream(fileName);
			outputStream.write(content.getBytes());
			return true;
		} catch (Exception e) {
			FILEUTILITY_LOGGER.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return false;
		}finally {
			AdminTestUtil.closeOutputStream(outputStream);
		}
	}
	
	/**
	 * The method will male new directory
	 * 
	 * @param path
	 * @return true or false
	 */
	public static boolean makeDir(String path) {
		File file = new File(path);
		return file.mkdirs();
	}
	
	/**
	 * The method will get file from list of files
	 * 
	 * @param listOfFiles
	 * @param keywordToFind
	 * @return File
	 */ 
	public static File getFileFromList(File[] listOfFiles, String keywordToFind) {
		for (int j = 0; j < listOfFiles.length; j++) {
			if (listOfFiles[j].getName().contains(keywordToFind)) {
				return listOfFiles[j];
			}
		}
		return null;
	}
	
	public static boolean deleteDirectory(File dir) {
        if (dir.isDirectory()) {
            File[] children = dir.listFiles();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDirectory(children[i]);
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
	}
	
	public static String getFileContent(FileInputStream fis, String encoding) throws IOException {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(fis, encoding))) {
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line);
				sb.append('\n');
			}
			return sb.toString();
		}
	}

}
