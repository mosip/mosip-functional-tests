import Util.AuthUtil;
import helper.PartnerTypes;
import org.bouncycastle.operator.OperatorCreationException;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

public class UnitTests {
    public static void main(String[] args) throws UnrecoverableEntryException, CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, OperatorCreationException {
        AuthUtil authUtil = new AuthUtil();
        authUtil.generatePartnerKeys(PartnerTypes.FTM,"mosip_ftmorg1715061369420" ,false,null,"IDA","api-internal.qa-inji.mosip.net");
    }
}
