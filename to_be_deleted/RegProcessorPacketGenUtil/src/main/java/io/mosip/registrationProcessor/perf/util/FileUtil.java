package io.mosip.registrationProcessor.perf.util;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.mosip.registrationProcessor.perf.dto.RegPacketSyncDto;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileUtil {

	public List<String> readLinesOfFile(String filePath) throws IOException {
		List<String> result = new ArrayList<>();
		try (Stream<String> lines = Files.lines(Paths.get(filePath))) {
			result = lines.collect(Collectors.toList());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public void logSyncDataToFile(RegPacketSyncDto syncDto, String filePath) {
		try (FileWriter f = new FileWriter(filePath, true);
				BufferedWriter b = new BufferedWriter(f);
				PrintWriter p = new PrintWriter(b);) {

			p.println(syncDto.getRegId() + "," + syncDto.getSyncData() + "," + syncDto.getPacketPath() + ","
					+ syncDto.getReferenceId());

		} catch (IOException i) {
			i.printStackTrace();
		}
	}

	public List<String> readLinesFromFile(String filepath) {
		List<String> lines = new ArrayList<>();
		BufferedReader b = null;
		try {
			File file = new File(filepath);
			b = new BufferedReader(new FileReader(file));

			String line = "";

			System.out.println("Reading file using Buffered Reader");

			while ((line = b.readLine()) != null) {
				lines.add(line);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				b.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return lines;
	}

	public InputStream getInputStream(String filePath) throws Exception {
		try {
			InputStream iStream = new FileInputStream(new File(filePath));
			return iStream;
		} catch (FileNotFoundException e) {
			throw new Exception(e);
		}

	}

}
