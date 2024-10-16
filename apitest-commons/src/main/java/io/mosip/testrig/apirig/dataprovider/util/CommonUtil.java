package io.mosip.testrig.apirig.dataprovider.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.DatatypeConverter;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mifmif.common.regex.Generex;

public class CommonUtil {
	private static final Logger logger = LoggerFactory.getLogger(CommonUtil.class);
	private static SecureRandom rand = new SecureRandom();

	private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

	public static boolean isExists(List<String> missList, String categoryCode) {
		if (missList != null) {
			for (String s : missList) {
				if (s.equals(categoryCode))
					return true;
			}
		}
		return false;
	}

	public static String getJSONObjectAttribute(JSONObject obj, String attrName, String defValue) {
		if (obj.has(attrName))
			return obj.getString(attrName);
		return defValue;
	}

	public static String getHexEncodedHash(byte[] data) throws Exception {
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
			messageDigest.update(data);
			return DatatypeConverter.printHexBinary(messageDigest.digest()).toUpperCase();
		} catch (Exception ex) {
			throw new Exception("Invalid getHexEncodedHash " + ex.getMessage());
		}
	}

	public static String toCaptialize(String text) {
		return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
	}

	// generate count no of random numbers in range 0 - (max-1)
	public static int[] generateRandomNumbers(int count, int max, int min) {
		int[] rand_nums = new int[count];

		for (int i = 0; i < count; i++) {
			rand_nums[i] = rand.nextInt((max - min) + 1) + min;
		}

		return rand_nums;
	}

	public static String getUTCDateTime(LocalDateTime time) {
		String DATEFORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(DATEFORMAT);
		if (time == null) {
			time = LocalDateTime.now(TimeZone.getTimeZone("UTC").toZoneId());
		}
		String utcTime = time.format(dateFormat);
		return utcTime;
	}

	public static String genStringAsperRegex(String regex) throws Exception {
		if (Generex.isValidPattern(regex)) {

			Generex generex = new Generex(regex);

			String randomStr = generex.random();
			logger.info(randomStr);
			// Generate all String that matches the given Regex.
			boolean bFound = false;
			do {
				bFound = false;
				if (randomStr.startsWith("^")) {
					int idx = randomStr.indexOf("^");
					randomStr = randomStr.substring(idx + 1);
					bFound = true;
				}
				if (randomStr.endsWith("$")) {
					int idx = randomStr.indexOf("$");
					randomStr = randomStr.substring(0, idx);
					bFound = true;
				}
			} while (bFound);
			return randomStr;
		}
		throw new Exception("invalid regex");

	}

	public static String readFromJSONFile(String filePath) {

		StringBuilder builder = new StringBuilder();
		try (FileReader reader = new FileReader(filePath);) {

			char[] cbuf = new char[1024];
			int n = 0;
			while ((n = reader.read(cbuf)) > 0) {
				builder.append(new String(cbuf, 0, n));
			}
			reader.close();
		} catch (IOException e) {
		}

		return builder.toString();

	}

	public static void CopyRecursivly(Path sourceDirectory, Path targetDirectory) throws IOException {

		// Traverse the file tree and copy each file/directory.
		Files.walk(sourceDirectory).forEach(sourcePath -> {

			Path targetPath = targetDirectory.resolve(sourceDirectory.relativize(sourcePath));

			try {
				Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				logger.error(e.getMessage());
			}

		});
	}

	public static String generateRandomString(int len) {
		StringBuilder builder = new StringBuilder();
		String alphabet = "abcdefghijklmn opqrstuvwxyz_123456789";

		if (len == 0)
			len = 20;

		for (int i = 0; i < len; i++) {
			builder.append(alphabet.charAt(rand.nextInt(alphabet.length())));
		}
		return builder.toString();
	}

	public static List<File> listFiles(String dirPath) {

		List<File> lstFiles = new ArrayList<File>();

		File dir = new File(dirPath);
		File[] files = dir.listFiles();
		for (File f : files)
			lstFiles.add(f);

		return lstFiles;

	}

	public static String getSHAFromBytes(byte[] byteArray) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		return bytesToHex(md.digest(byteArray));
	}

	private static String bytesToHex(byte[] hash) {
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < hash.length; i++) {
			String hex = Integer.toHexString(0xff & hash[i]);
			if (hex.length() == 1) {
				hexString.append('0');
			}
			hexString.append(hex);
		}
		return hexString.toString();
	}

	public static String getSHA(String cbeffStr) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		return bytesToHex(md.digest(cbeffStr.getBytes(StandardCharsets.UTF_8)));
	}


	public static void saveToTemp(String data, String fileName) {
		try {
			CommonUtil.write(Paths.get("/temp/" + fileName), data.getBytes());
		} catch (IOException e) {
		}
	}

	public static Properties String2Props(String str) {
		Properties props = new Properties();
		String[] parts = str.split(",");
		for (String p : parts) {
			String[] v = p.split("=");
			if (v.length > 0) {
				props.put(v[0].trim(), v[1].trim());
			}
		}
		return props;
	}

	public static void copyFileWithBuffer(Path source, Path destination)  {
		try (BufferedInputStream in = new BufferedInputStream(Files.newInputStream(source));
				BufferedOutputStream out = new BufferedOutputStream(Files.newOutputStream(destination))) {
			byte[] buffer = new byte[8192]; // Adjust buffer size as needed
			int bytesRead;
			while ((bytesRead = in.read(buffer)) != -1) {
				out.write(buffer, 0, bytesRead);
			}
			// Flush the buffered output stream
			out.flush();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	public static void copyMultipartFileWithBuffer(MultipartFile sourceFile, Path destination) {
		try (InputStream inputStream = sourceFile.getInputStream();
				BufferedOutputStream outputStream = new BufferedOutputStream(Files.newOutputStream(destination))) {
			// Define buffer size
			byte[] buffer = new byte[8192];
			int bytesRead;
			// Read from the input stream and write to the output stream with buffering
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
			}
			// Flush the buffered output stream
			outputStream.flush();
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}
	
	
	public static void write(Path filePath, byte[] bytes) throws IOException {
		Files.write(filePath, bytes);
		
//		ObjectMapper mapper = new ObjectMapper();
//		try (OutputStream outputStream = new FileOutputStream(filePath.toString())) {
//			mapper.writeValue(outputStream, bytes);
//		}
	}
	
	
	public static void write( byte[] bytes,File file) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		try (OutputStream outputStream = new FileOutputStream(file)) {
			mapper.writeValue(outputStream, bytes);
		}
	}
	
	public static  byte[] read(String path)
	{
		 byte[] data = null;
		try {
			data = Files.readAllBytes(Paths.get(path));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		}
		 
		 return data;
	}
	
	
	public static void main(String[] args) throws Exception {
		String regex1 = "^|^0[5-7][0-9]{8}$";
		String regex2 = "^[a-zA-Zء-ي٠-٩ ]{5,47}$";
		String regex3 = "(^|^[A-Z]{2}[0-9]{1,6}$)|(^[A-Z]{1}[0-9]{1,7}$)";
		String regex4 = "^|^(?=.{0,10}$).*";

		String regex5 = "^(1869|18[7-9][0-9]|19[0-9][0-9]|20[0-9][0-9])/([0][1-9]|1[0-2])/([0][1-9]|[1-2][0-9]|3[01])$";

		String rex = regex5;
		String values = genStringAsperRegex(rex);
		Pattern p = Pattern.compile(rex);// . represents single character
		Matcher m = p.matcher(values);
		boolean b = m.matches();

	}
}
