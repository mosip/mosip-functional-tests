import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.security.Key;
import java.security.KeyStore;
import java.util.Base64;
/**
 * Class to export private key from key store in PEM format.
 * This is used since keytool cannot be used for this purpose.
 * Referenced from https://security.stackexchange.com/a/114776/221987
 * 
 * Command Line: java ExportPrivateKey <path_to_keystore> JCEKS <keystore_password> â€œ<key_alias>â€� <key_password> <output_file_name>
 * 
 * Example: java ExportPrivateKey keystore.jks PKCS12 somepassword mosip.io somepassword PrivateKey.pem

 * @author Loganathan.Sekar
 *
 */


public class ExportPrivateKey
{
    private File keystoreFile;
    private String keyStoreType;
    private char[] keyStorePassword;
    private char[] keyPassword;
    private String alias;
    private File exportedFile;

    public void export() throws Exception {
        KeyStore keystore = KeyStore.getInstance(keyStoreType);
        keystore.load(new FileInputStream(keystoreFile), keyStorePassword);
        Key key = keystore.getKey(alias, keyPassword);
        String encoded = Base64.getEncoder().encodeToString(key.getEncoded());
        FileWriter fw = new FileWriter(exportedFile);
        fw.write("---BEGIN PRIVATE KEY---\n");
        fw.write(encoded);
        fw.write("\n");
        fw.write("---END PRIVATE KEY---");
        fw.close();
    }

    public static void main(String args[]) throws Exception {
        ExportPrivateKey export = new ExportPrivateKey();
        export.keystoreFile = new File(args[0]);
        export.keyStoreType = args[1];
        export.keyStorePassword = args[2].toCharArray();
        export.alias = args[3];
        export.keyPassword = args[4].toCharArray();
        export.exportedFile = new File(args[5]);
        export.export();
    }
}