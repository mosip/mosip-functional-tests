package io.mosip.testrig.apirig.utils;

import java.math.BigInteger;

import io.mosip.kernel.core.util.HMACUtils;

public class TokenIDGenerator {


 

    public String generateTokenID(String uin, String partnerCode, String uinSalt, String partnerCodeSalt, int tokenIDLength) {
        String uinHash = HMACUtils.digestAsPlainText(HMACUtils.generateHash((uin + uinSalt).getBytes()));
        String hash = HMACUtils
                .digestAsPlainText(HMACUtils.generateHash((partnerCodeSalt + partnerCode + uinHash).getBytes()));
        return new BigInteger(hash.getBytes()).toString().substring(0, tokenIDLength);
    }
}
