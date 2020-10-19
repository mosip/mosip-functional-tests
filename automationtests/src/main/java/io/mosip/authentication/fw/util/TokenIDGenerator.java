package io.mosip.authentication.fw.util;

import java.math.BigInteger;



 

import io.mosip.kernel.core.util.HMACUtils;

public class TokenIDGenerator {


    private String uinSalt = "zHuDEAbmbxiUbUShgy6pwUhKh9DE0EZn9kQDKPPKbWscGajMwf";

    private int tokenIDLength = 36;

    private String partnerCodeSalt = "yS8w5Wb6vhIKdf1msi4LYTJks7mqkbmITk2O63Iq8h0bkRlD0d";
    
    public String authPartnerID = "792112";

 

    public String generateTokenID(String uin, String partnerCode) {
        String uinHash = HMACUtils.digestAsPlainText(HMACUtils.generateHash((uin + uinSalt).getBytes()));
        String hash = HMACUtils
                .digestAsPlainText(HMACUtils.generateHash((partnerCodeSalt + partnerCode + uinHash).getBytes()));
        return new BigInteger(hash.getBytes()).toString().substring(0, tokenIDLength);
    }

	/*
	 * public static void main(String[] args) { TokenIDGenerator a = new
	 * TokenIDGenerator(); String uin = "5734728510"; String prid = "1873299273";
	 * String t = a.generateTokenID(uin, a.authPartnerID); System.err.println(t);
	 * System.err.println(a.generateTokenID(t, prid));
	 * //System.err.println(a.generateTokenID(uin, prid)); }
	 */
}
