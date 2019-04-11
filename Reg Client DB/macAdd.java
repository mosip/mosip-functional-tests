package io.mosip.registration.dao;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class macAdd {
	public static void main(String[] args) throws IOException {

		System.out.println(getLinuxMacAddress());
	}

	private static String getLinuxMacAddress() throws IOException {
		String linuxMachineId = "";
		List<String> devices = new ArrayList<>();
		Pattern pattern = Pattern.compile("^ *(.*):");
		try (BufferedReader in = new BufferedReader(new FileReader("/proc/net/dev"))) {
			String line = null;
			while ((line = in.readLine()) != null) {
				Matcher m = pattern.matcher(line);
				if (m.find()) {
					devices.add(m.group(1));
				}
			}
			for (String device : devices) {
				try (FileReader reader1 = new FileReader("/sys/class/net/" + device + "/address")) {
					if (device.equals("eno1")) {
						BufferedReader in1 = new BufferedReader(reader1);
						linuxMachineId = in1.readLine();
						in1.close();
					}
				} catch (IOException e) {

				}
			}
		} catch (IOException e) {

		}
		return linuxMachineId;

	}
}
