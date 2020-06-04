package io.mosip.authentication.partnerdemo.service.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.codec.binary.Base64;

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
	@PostMapping(path = "/encode", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
	public String encode(@RequestBody String stringToEncode) {
		return Base64.encodeBase64URLSafeString(stringToEncode.getBytes(StandardCharsets.UTF_8));
	}
	
	/**
	 * Encodes the contents of cpeff file.
	 *
	 * @param file the file
	 * @return the string
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@PostMapping(value = "/encodeFile", produces = MediaType.TEXT_PLAIN_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public String encodeFile(@RequestPart MultipartFile file) throws IOException {
		return CryptoUtil.encodeBase64(file.getBytes());
	}
}
