package com.mindtree.mosip.qamill.utility;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.json.simple.JSONObject;

public class FileClient {
	public void writeToFile(String fileNameWithPath, String fileContent) {
		try {
			System.out.println("Entering FileClient::writeToFile");
			System.out.println("FileNameWithPath: " + fileNameWithPath);
			File file = new File(fileNameWithPath);
			if (!file.exists()) {
				file.getParentFile().mkdirs();
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file, false);
			if (null != fileContent) {
				// fw.write(jsonObject.toJSONString());
				fw.write(fileContent);
				fw.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("FileClient::writeToFile -- Failed to write: " + e.getMessage());
		}
		System.out.println("Leaving FileClient::writeToFile");
	}

	public ArrayList<String> readFromCsvFile(String fileNameWithPath) {
		System.out.println("Entering FileClient::readFromCsvFile");
		System.out.println("FileNameWithPath: " + fileNameWithPath);
		ArrayList<String> fileContent = new ArrayList<String>();
		try {
			System.out.println(fileNameWithPath);
			File file = new File(fileNameWithPath);
			if (!file.exists()) {
				return null;
			}
			BufferedReader br = new BufferedReader(new FileReader(fileNameWithPath));
			String line = null;
			while ((line = br.readLine()) != null) {
				fileContent.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		//System.out.println("fileContent: " + fileContent.toString());
		System.out.println("Leaving FileClient::readFromCsvFile");
		return fileContent;
	}

	public String readFromFile(String fileNameWithPath) {
		System.out.println("Entering FileClient::readFromCsvFile");
		System.out.println("FileNameWithPath: " + fileNameWithPath);
		String fileContent = "";
		try {
			System.out.println(fileNameWithPath);
			File file = new File(fileNameWithPath);
			if (!file.exists()) {
				return null;
			}
			StringBuilder buf = new StringBuilder();
			BufferedReader br = new BufferedReader(new FileReader(fileNameWithPath));
			String line = null;
			while ((line = br.readLine()) != null) {
				buf.append(line);
			}
			fileContent = buf.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//System.out.println("fileContent: " + fileContent.toString());
		System.out.println("Leaving FileClient::readFromFile");
		return fileContent;
	}

	public Map<String, String> getMosipTestFrameworkProperties() {
		System.out.println("Entering FileClient::getMosipTestFrameworkProperties");
		String mosipPropertyFilePath = System.getenv("MosipTestFrameworkRT");
		System.out.println("MosipTestFramework PropertyFile Path: " + mosipPropertyFilePath);
		Map<String, String> mosipTestFrameworkConf = new HashMap<String, String>();

		Properties p = new Properties();
		try {
			p.load(new BufferedInputStream(
					new FileInputStream(mosipPropertyFilePath + File.separator + "MosipTestFrameworkConf.properties")));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (String key : p.stringPropertyNames()) {
			String value = p.getProperty(key);
			//System.out.println(key + " => " + value);
			mosipTestFrameworkConf.put(key, value);
		}
		
		System.out.println("Leaving FileClient::getMosipTestFrameworkProperties");
		return mosipTestFrameworkConf;
	}
	
	public void purgeDirectory(String dir) {
		File file = new File(dir);
		if (file.exists()) {
			purgeDirectory(file);
		}		
	}
	private void purgeDirectory(File dir) {
	    for (File file: dir.listFiles()) {
	        if (file.isDirectory())
	            purgeDirectory(file);
	        file.delete();
	    }
	}
}
