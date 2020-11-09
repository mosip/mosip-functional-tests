package io.mosip.device.register.util;

import java.io.*;
import java.security.*;
import java.security.cert.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import org.jose4j.jws.JsonWebSignature;
import org.jose4j.lang.JoseException;
public class JwtUtility {

	private static String signAlgorithm="RS256";
	
	public static X509Certificate getCertificate(Map<String, String> testDataMap) {
		try {
//			FileInputStream certfis = new FileInputStream(
//					new File(System.getProperty("user.dir") + "/files/keys/MosipTestCert.pem").getPath());
//			String cert = getFileContent(certfis, "UTF-8");

//			FileInputStream certfis = new FileInputStream(
//					new File(testDataMap.get("partner_certificate_name")).getPath());
			//String cert = getFileContent(certfis, "UTF-8");
			DeviceUtil d=new DeviceUtil();
			String cert=d.readCertificate("selfmosip.cer");
			cert = trimBeginEnd(cert);
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			return (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(Base64.getDecoder().decode(cert)));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	public static String getFileContent(FileInputStream fis, String encoding) throws IOException {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(fis, encoding))) {
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line);
				sb.append('\n');
			}
			return sb.toString();
		}
	}
	private static String trimBeginEnd(String pKey) {
		pKey = pKey.replaceAll("-*BEGIN([^-]*)-*(\r?\n)?", "");
		pKey = pKey.replaceAll("-*END([^-]*)-*(\r?\n)?", "");
		pKey = pKey.replaceAll("\\s", "");
		return pKey;
	}
	public PrivateKey getPrivateKey() {		
		try {
			File getKeyStorePath=DeviceUtil.getGlobalResourcePath("DeviceData/certificates/identity.p12");
			InputStream is=new FileInputStream(getKeyStorePath);
			
			KeyStore keystore = KeyStore.getInstance("PKCS12");
			keystore.load(is, "storepassword".toCharArray());
			is.close();
			PrivateKey key = (PrivateKey)keystore.getKey("notebook", "storepassword".toCharArray());
			return key;
//			FileInputStream pkeyfis = new FileInputStream(
//					new File(System.getProperty("user.dir") + "/files/keys/PrivateKey.pem").getPath());
//
//			String pKey = getFileContent(pkeyfis, "UTF-8");
//			DeviceUtil d=new DeviceUtil();
//			String pKey=d.readCertificate("private_key.pem");
			
//			pKey = trimBeginEnd(pKey);
		//	KeyFactory kf = KeyFactory.getInstance("RSA");
			//return kf.generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(pKey)));
			
//			String privateKeyPEM = pKey
//				      .replace("-----BEGIN RSA PRIVATE KEY-----", "")
//				      .replaceAll(System.lineSeparator(), "")
//				      .replace("-----END RSA PRIVATE KEY-----", "");
				 
//				    byte[] encoded = Base64.getDecoder().decode(pKey);
//				 
//				    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
//				    PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
//				    return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
			
//			KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
//			gen.initialize(2048);
//			KeyPair pair = gen.generateKeyPair();
//			return pair.getPrivate();
			
		} catch (Exception ex) {
			ex.printStackTrace();
			//throw new Exception("Failed to get private key");
		}
		return null;
	}
	
	public static String getJwt(byte[] data, PrivateKey privateKey, X509Certificate x509Certificate) {
		String jwsToken = null;
		JsonWebSignature jws = new JsonWebSignature();
		
		if(x509Certificate != null) {
			List<X509Certificate> certList = new ArrayList<>();
			certList.add(x509Certificate);
			X509Certificate[] certArray = certList.toArray(new X509Certificate[] {});
			jws.setCertificateChainHeaderValue(certArray);
		}
		
		jws.setPayloadBytes(data);
		jws.setAlgorithmHeaderValue(signAlgorithm);
		jws.setKey(privateKey);
		jws.setDoKeyValidation(false);
		try {
			jwsToken = jws.getCompactSerialization();
		} catch (JoseException e) {
			e.printStackTrace();
		}
		return jwsToken;
	}
}
