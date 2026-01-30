package io.mosip.testrig.apirig.id;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import io.mosip.testrig.apirig.utils.ConfigManager;

public class IdValidator {

	static {
		ConfigManager.init(new java.util.HashMap<>());
	}

	private static final List<String> RESTRICTED = ConfigManager.getListProperty("mosip.id.restricted-number");

	private static final List<String> NOT_START_WITH = ConfigManager.getListProperty("mosip.id.not-start-with");

	private static final int SEQUENCE_LIMIT = ConfigManager.getIntProperty("mosip.id.sequence-limit");

	private static final int REPEATING_LIMIT = ConfigManager.getIntProperty("mosip.id.repeating-limit");

	private static final int UIN_LENGTH = ConfigManager.getIntProperty("mosip.uin.length");

	private static final int VID_LENGTH = ConfigManager.getIntProperty("mosip.vid.length");

	private static final int PRID_LENGTH = ConfigManager.getIntProperty("mosip.prid.length");

	private static final int UIN_REPEAT_BLOCK = ConfigManager.getIntProperty("mosip.uin.repeating-block-limit");

	private static final int VID_REPEAT_BLOCK = ConfigManager.getIntProperty("mosip.vid.repeating-block-limit");

	private static final int PRID_REPEAT_BLOCK = ConfigManager.getIntProperty("mosip.prid.repeating-block-limit");

	private static final int DIGITS_GROUP_LIMIT = ConfigManager.getIntProperty("mosip.uin.digits-group-limit");

	private static final int CONJUGATIVE_EVEN_LIMIT = ConfigManager.getIntProperty("mosip.uin.conjugative-even-limit");

	private static final String[] CYCLIC_NUM = ConfigManager.getArrayProperty("mosip.uin.cyclic-num");

	private static final String SEQ_ASC = ConfigManager.getproperty("mosip.id.seq-asc");
	private static final String SEQ_DEC = ConfigManager.getproperty("mosip.id.seq-dec");

	private static final String SEQ_ASC_UIN = ConfigManager.getproperty("mosip.uin.seq-asc");
	private static final String SEQ_DEC_UIN = ConfigManager.getproperty("mosip.uin.seq-dec");

	private static final Pattern REPEATING_PATTERN = Pattern.compile("(\\d)\\d{0," + (REPEATING_LIMIT - 1) + "}\\1");

	private static final Pattern CONJUGATIVE_EVEN_PATTERN = Pattern.compile("[2468]{" + CONJUGATIVE_EVEN_LIMIT + "}");

	private IdValidator() {
	}

	public static boolean isValidUin(String uin) {
		return commonValidation(uin, UIN_LENGTH, UIN_REPEAT_BLOCK, SEQ_ASC_UIN, SEQ_DEC_UIN) && uinValidation(uin);
	}

	public static boolean isValidVid(String vid) {
		return commonValidation(vid, VID_LENGTH, VID_REPEAT_BLOCK, SEQ_ASC, SEQ_DEC);
	}

	public static boolean isValidPrid(String prid) {
		return commonValidation(prid, PRID_LENGTH, PRID_REPEAT_BLOCK, SEQ_ASC, SEQ_DEC);
	}

	private static boolean commonValidation(String id, int length, int repeatBlock, String ascSeq, String decSeq) {

		if (id == null || id.isBlank() || !id.matches("\\d+"))
			return false;

		Pattern repeatingBlockPattern = Pattern.compile("(\\d{" + repeatBlock + ",}).*?\\1");

		return !(id.length() != length || NOT_START_WITH.stream().anyMatch(id::startsWith)
				|| RESTRICTED.stream().anyMatch(id::contains) || sequenceFilter(id, ascSeq, decSeq)
				|| REPEATING_PATTERN.matcher(id).find() || repeatingBlockPattern.matcher(id).find());
	}

	private static boolean sequenceFilter(String id, String asc, String dec) {
		return IntStream.rangeClosed(0, id.length() - SEQUENCE_LIMIT).mapToObj(i -> id.substring(i, i + SEQUENCE_LIMIT))
				.anyMatch(sub -> asc.contains(sub) || dec.contains(sub));
	}

	private static boolean uinValidation(String id) {

		if (CONJUGATIVE_EVEN_PATTERN.matcher(id).find())
			return false;

		if (id.substring(0, DIGITS_GROUP_LIMIT).equals(id.substring(id.length() - DIGITS_GROUP_LIMIT)))
			return false;

		String reversedLast = new StringBuilder(id.substring(id.length() - DIGITS_GROUP_LIMIT)).reverse().toString();

		if (id.substring(0, DIGITS_GROUP_LIMIT).equals(reversedLast))
			return false;

		for (String cyclic : CYCLIC_NUM) {
			if (id.contains(cyclic))
				return false;
		}

		return true;
	}
}