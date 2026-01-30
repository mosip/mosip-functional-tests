package io.mosip.testrig.apirig.id;

import java.security.SecureRandom;

import io.mosip.testrig.apirig.utils.ConfigManager;

public class IdGenerator {

	private static final SecureRandom RANDOM = new SecureRandom();

	private IdGenerator() {
	}

	public static String generateValidUin() {
		return generateId(ConfigManager.getIntProperty("mosip.uin.length") - 1, Type.UIN, true);
	}

	public static String generateValidVid() {
		return generateId(ConfigManager.getIntProperty("mosip.vid.length") - 1, Type.VID, true);
	}

	public static String generateValidPrid() {
		return generateId(ConfigManager.getIntProperty("mosip.prid.length") - 1, Type.PRID, true);
	}

	public static String generateInvalidUin() {
		return generateId(ConfigManager.getIntProperty("mosip.uin.length") - 1, Type.UIN, false);
	}

	public static String generateInvalidVid() {
		return generateId(ConfigManager.getIntProperty("mosip.vid.length") - 1, Type.VID, false);
	}

	public static String generateInvalidPrid() {
		return generateId(ConfigManager.getIntProperty("mosip.prid.length") - 1, Type.PRID, false);
	}

	private enum Type {
		UIN, VID, PRID
	}

	private static String generateId(int baseLength, Type type, boolean valid) {
		int attempts = 0;
		final int MAX_ATTEMPTS = 1000;

		while (attempts < MAX_ATTEMPTS) {
			String base = randomDigits(baseLength);
			String checksum = CheckSumUtil.generateChecksumDigit(base);
			String id = base + (valid ? checksum : differentDigit(checksum));

			boolean isValid = CheckSumUtil.validateChecksum(id) && validate(type, id);
			if ((valid && isValid) || (!valid && !isValid)) {
				return id;
			}

			attempts++;
		}

		throw new IllegalStateException("Unable to generate " + (valid ? "valid" : "invalid") + " " + type + " after "
				+ MAX_ATTEMPTS + " attempts");
	}

	private static boolean validate(Type type, String id) {

		switch (type) {
		case UIN:
			return IdValidator.isValidUin(id);
		case VID:
			return IdValidator.isValidVid(id);
		case PRID:
			return IdValidator.isValidPrid(id);
		default:
			return false;
		}
	}

	private static String randomDigits(int length) {

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < length; i++) {
			sb.append(RANDOM.nextInt(10));
		}

		return sb.toString();
	}

	private static String differentDigit(String correct) {

		int digit;
		do {
			digit = RANDOM.nextInt(10);
		} while (String.valueOf(digit).equals(correct));

		return String.valueOf(digit);
	}
}
