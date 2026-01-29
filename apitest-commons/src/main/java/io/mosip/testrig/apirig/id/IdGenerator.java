package io.mosip.testrig.apirig.id;

import java.security.SecureRandom;

public class IdGenerator {
	
	    private static final SecureRandom RANDOM = new SecureRandom();

	    private IdGenerator() {}

	    public static String generateValidUin() {
	        return generateValid(9, Type.UIN);
	    }

	    public static String generateValidVid() {
	        return generateValid(15, Type.VID);
	    }

	    public static String generateValidPrid() {
	        return generateValid(13, Type.PRID);
	    }

	    public static String generateInvalidUin() {
	        return generateInvalid(9, Type.UIN);
	    }

	    public static String generateInvalidVid() {
	        return generateInvalid(15, Type.VID);
	    }

	    public static String generateInvalidPrid() {
	        return generateInvalid(13, Type.PRID);
	    }

	    private enum Type { UIN, VID, PRID }

	    private static String generateValid(int baseLength, Type type) {

	        while (true) {

	            String base = randomDigits(baseLength);
	            String checksum = CheckSumUtil.generateChecksumDigit(base);
	            String id = base + checksum;

	            if (validate(type, id) && CheckSumUtil.validateChecksum(id)) {
	                return id;
	            }
	        }
	    }

	    private static String generateInvalid(int baseLength, Type type) {

	        while (true) {

	            String base = randomDigits(baseLength);
	            String correct = CheckSumUtil.generateChecksumDigit(base);
	            String wrong = differentDigit(correct);
	            String id = base + wrong;

	            if (validate(type, id) && !CheckSumUtil.validateChecksum(id)) {
	                return id;
	            }
	        }
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
