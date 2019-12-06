package io.mosip.authentication.partnerdemo.service.controller;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.core.util.CryptoUtil;

/**
 * 
 * The Class Decode is used to decode the String.
 * @author Arun Bose S
 */
@RestController
public class Decode {

	/**
	 * Decode.
	 *
	 * @param stringToDecode the string to decode
	 * @return the string
	 */
	@PostMapping(path = "/decode", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public String decode(@RequestBody String stringToDecode) {
		return new String(Base64.getDecoder().decode(stringToDecode), StandardCharsets.UTF_8);
	}
	
	/**
	 *Encodes the contents of cpeff file.
	 *
	 * @param stringToDecode the string to decode
	 * @param fileName the file name
	 * @return the response entity
	 */
	@PostMapping(path = "/decodeFile", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public ResponseEntity<InputStreamResource> decodeToFile(@RequestBody String stringToDecode,
			@RequestParam String fileName) {
		byte[] decodedFileData = CryptoUtil.decodeBase64(stringToDecode);
		HttpHeaders headers = new HttpHeaders();
		headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
		headers.add("Pragma", "no-cache");
		headers.add("Content-Disposition", "attachment; filename=" + fileName);
		headers.add("Expires", "0");
		InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(decodedFileData));
		return ResponseEntity.ok().headers(headers).contentLength(decodedFileData.length)
				.contentType(MediaType.parseMediaType("application/octet-stream")).body(resource);
	}
}
