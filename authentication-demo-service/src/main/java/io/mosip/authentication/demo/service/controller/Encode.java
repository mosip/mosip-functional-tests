package io.mosip.authentication.demo.service.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

//import java.util.Base64;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.mosip.kernel.core.util.CryptoUtil;
import io.swagger.annotations.Api;

/**
 * @author ArunBose S
 * @author DineshKaruppiah
 * The Class Encode is used to encode the String.
 */
@RestController
@Api(tags = { "Encode" })
public class Encode {

	/**
	 * Encode.
	 *
	 * @param stringToEncode the string to encode
	 * @return the string
	 */
	@PostMapping(path = "/encodeBase64UrlSafe", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
	public String encodeBase64UrlSafe(@RequestBody String stringToEncode) {
		return CryptoUtil.encodeToURLSafeBase64(stringToEncode.getBytes(StandardCharsets.UTF_8));
	}
	
	@PostMapping(path = "/encodeBase64Plain", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
	public String encodeBase64Plain(@RequestBody String stringToEncode) {
		return  CryptoUtil.encodeToPlainBase64(stringToEncode.getBytes(StandardCharsets.UTF_8));
	}
	
	/**
	 * Encodes the contents of cpeff file.
	 *
	 * @param file the file
	 * @return the string
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@PostMapping(value = "/encodeToBase64UrlSafeFromFile", produces = MediaType.TEXT_PLAIN_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public String encodeToBase64UrlSafeFromFile(@RequestPart MultipartFile file) throws IOException {
		return CryptoUtil.encodeToURLSafeBase64(file.getBytes());
	}
	
	@PostMapping(value = "/encodeToBase64PlainFromFile", produces = MediaType.TEXT_PLAIN_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public String encodeToBase64PlainSafeFromFile(@RequestPart MultipartFile file) throws IOException {
		return CryptoUtil.encodeToPlainBase64(file.getBytes());
	}
}
