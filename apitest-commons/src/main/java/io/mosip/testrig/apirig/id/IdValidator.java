package io.mosip.testrig.apirig.id;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import io.mosip.testrig.apirig.utils.ConfigManager;

public class IdValidator {

    // ---------- UIN PROPERTIES----------
    private static final int UIN_LENGTH = ConfigManager.getIntProperty("mosip.kernel.uin.length");
    private static final int UIN_SEQUENCE_LIMIT = ConfigManager.getIntProperty("mosip.kernel.uin.length.sequence-limit");
    private static final int UIN_REPEAT_BLOCK = ConfigManager.getIntProperty("mosip.kernel.uin.length.repeating-block-limit");
    private static final int UIN_REPEATING_LIMIT = ConfigManager.getIntProperty("mosip.kernel.uin.length.repeating-limit");
    private static final int DIGITS_GROUP_LIMIT = ConfigManager.getIntProperty("mosip.kernel.uin.length.digits-limit");
    private static final int CONJUGATIVE_EVEN_LIMIT = ConfigManager.getIntProperty("mosip.kernel.uin.length.conjugative-even-digits-limit");

    private static final List<String> UIN_RESTRICTED =
            ConfigManager.getListProperty("mosip.kernel.uin.restricted-numbers");

    private static final List<String> UIN_NOT_START_WITH =
            ConfigManager.getListProperty("mosip.kernel.uin.not-start-with");

    // ---------- VID PROPERTIES----------
    private static final int VID_LENGTH = ConfigManager.getIntProperty("mosip.kernel.vid.length");
    private static final int VID_SEQUENCE_LIMIT = ConfigManager.getIntProperty("mosip.kernel.vid.length.sequence-limit");
    private static final int VID_REPEAT_BLOCK = ConfigManager.getIntProperty("mosip.kernel.vid.length.repeating-block-limit");
    private static final int VID_REPEATING_LIMIT = ConfigManager.getIntProperty("mosip.kernel.vid.length.repeating-limit");

    private static final List<String> VID_RESTRICTED =
            ConfigManager.getListProperty("mosip.kernel.vid.restricted-numbers");

    private static final List<String> VID_NOT_START_WITH =
            ConfigManager.getListProperty("mosip.kernel.vid.not-start-with");

    // ---------- PRID PROPERTIES ----------
    private static final int PRID_LENGTH = ConfigManager.getIntProperty("mosip.kernel.prid.length");
    private static final int PRID_SEQUENCE_LIMIT = ConfigManager.getIntProperty("mosip.kernel.prid.sequence-limit");
    private static final int PRID_REPEAT_BLOCK = ConfigManager.getIntProperty("mosip.kernel.prid.repeating-block-limit");
    private static final int PRID_REPEATING_LIMIT = ConfigManager.getIntProperty("mosip.kernel.prid.repeating-limit");

    private static final List<String> PRID_RESTRICTED =
            ConfigManager.getListProperty("mosip.kernel.prid.restricted-numbers");

    private static final List<String> PRID_NOT_START_WITH =
            ConfigManager.getListProperty("mosip.kernel.prid.not-start-with");

    private static final String SEQ_ASC = "0123456789";
    private static final String SEQ_DEC = "9876543210";

    private static final String SEQ_ASC_UIN = "01234567890123456789";
    private static final String SEQ_DEC_UIN = "98765432109876543210";

    private static final String[] CYCLIC_NUM = {
            "142857",
            "0588235294117647",
            "052631578947368421",
            "0434782608695652173913",
            "0344827586206896551724137931",
            "0212765957446808510638297872340425531914893617",
            "0169491525423728813559322033898305084745762711864406779661",
            "016393442622950819672131147540983606557377049180327868852459",
            "010309278350515463917525773195876288659793814432989690721649484536082474226804123711340206185567"
    };

    private static final Pattern UIN_REPEATING_PATTERN =
            Pattern.compile("(\\d)\\d{0," + (UIN_REPEATING_LIMIT - 1) + "}\\1");

    private static final Pattern CONJUGATIVE_EVEN_PATTERN =
            Pattern.compile("[2468]{" + CONJUGATIVE_EVEN_LIMIT + "}");

    private IdValidator() {
    }

    public static boolean isValidUin(String uin) {
        return commonValidation(uin, UIN_LENGTH, UIN_REPEAT_BLOCK, UIN_SEQUENCE_LIMIT,
                UIN_NOT_START_WITH, UIN_RESTRICTED, SEQ_ASC_UIN, SEQ_DEC_UIN, UIN_REPEATING_PATTERN)
                && uinValidation(uin);
    }

    public static boolean isValidVid(String vid) {
        Pattern vidRepeat = Pattern.compile("(\\d)\\d{0," + (VID_REPEATING_LIMIT - 1) + "}\\1");

        return commonValidation(vid, VID_LENGTH, VID_REPEAT_BLOCK, VID_SEQUENCE_LIMIT,
                VID_NOT_START_WITH, VID_RESTRICTED, SEQ_ASC, SEQ_DEC, vidRepeat);
    }

    public static boolean isValidPrid(String prid) {
        Pattern pridRepeat = Pattern.compile("(\\d)\\d{0," + (PRID_REPEATING_LIMIT - 1) + "}\\1");

        return commonValidation(prid, PRID_LENGTH, PRID_REPEAT_BLOCK, PRID_SEQUENCE_LIMIT,
                PRID_NOT_START_WITH, PRID_RESTRICTED, SEQ_ASC, SEQ_DEC, pridRepeat);
    }

    private static boolean commonValidation(String id, int length, int repeatBlock, int sequenceLimit,
                                            List<String> notStartWith, List<String> restricted,
                                            String ascSeq, String decSeq, Pattern repeatingPattern) {

        if (id == null || id.isBlank() || !id.matches("\\d+"))
            return false;

        Pattern repeatingBlockPattern = Pattern.compile("(\\d{" + repeatBlock + ",}).*?\\1");

        return !(id.length() != length
                || notStartWith.stream().anyMatch(id::startsWith)
                || restricted.stream().anyMatch(id::contains)
                || sequenceFilter(id, ascSeq, decSeq, sequenceLimit)
                || repeatingPattern.matcher(id).find()
                || repeatingBlockPattern.matcher(id).find());
    }

    private static boolean sequenceFilter(String id, String asc, String dec, int limit) {
        return IntStream.rangeClosed(0, id.length() - limit)
                .mapToObj(i -> id.substring(i, i + limit))
                .anyMatch(sub -> asc.contains(sub) || dec.contains(sub));
    }

    private static boolean uinValidation(String id) {

        if (CONJUGATIVE_EVEN_PATTERN.matcher(id).find())
            return false;

        if (id.substring(0, DIGITS_GROUP_LIMIT)
                .equals(id.substring(id.length() - DIGITS_GROUP_LIMIT)))
            return false;

        String reversedLast =
                new StringBuilder(id.substring(id.length() - DIGITS_GROUP_LIMIT)).reverse().toString();

        if (id.substring(0, DIGITS_GROUP_LIMIT).equals(reversedLast))
            return false;

        for (String cyclic : CYCLIC_NUM) {
            if (id.contains(cyclic))
                return false;
        }

        return true;
    }
}
