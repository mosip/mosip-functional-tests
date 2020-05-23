package io.mosip.authentication.partnerdemo.service.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.jaiimageio.jpeg2000.impl.J2KImageReader;

import io.mosip.kernel.core.bioapi.exception.BiometricException;
import io.mosip.kernel.core.bioapi.model.CompositeScore;
import io.mosip.kernel.core.bioapi.model.KeyValuePair;
import io.mosip.kernel.core.bioapi.model.QualityScore;
import io.mosip.kernel.core.bioapi.model.Score;
import io.mosip.kernel.core.bioapi.spi.IBioApi;
import io.mosip.kernel.core.cbeffutil.entity.BIR;
import io.mosip.kernel.core.util.CryptoUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;

/**
 * The Class BiometricValidation.
 *
 * @author Manoj SP
 * 
 *         This class is used to validate the Biometric SDK. It also contains
 *         functionality to convert from iso/jp2 to jpg.
 * 
 *         CheckQuality takes BIR as input. BIR input should have following data
 *         as mandatory: 1. bdb 2. bdbInfo - type, subtype, format
 *         (organization, type)
 * 
 *         match should provide input in below json structure:
 * 
 *         { "probe" : <BIR data>, "gallery" : <array of BIR data> }
 * 
 *         compositeMatch should provide input in below json structure:
 * 
 *         { "probe" : <array of BIR data>, "gallery" : <array of BIR data> }
 */
@RestController
@Api(tags = { "Biometric Validation" })
public class BiometricValidation implements IBioApi {

	/** The mapper. */
	@Autowired
	private ObjectMapper mapper;

	/** The provider. */
	@Autowired(required = false)
	private IBioApi provider;

	/** The rest. */
	RestTemplate rest = new RestTemplate();

	/**
	 * Iso to JPG.
	 *
	 * @param file
	 *            the file
	 * @return the response entity
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@PostMapping(path = "/isoFileToJpg", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public ResponseEntity<InputStreamResource> isoFileToJPG(@RequestParam("ISO") MultipartFile file) throws IOException {
		byte[] inputFileBytes = file.getBytes();
		int index;
		for (index = 0; index < inputFileBytes.length; index++) {
			if ((char) inputFileBytes[index] == 'j' && (char) inputFileBytes[index + 1] == 'P') {
				break;
			}
		}
		return convertToJPG(Arrays.copyOfRange(inputFileBytes, index - 4, inputFileBytes.length),
				file.getOriginalFilename());
	}
	
	/**
	 * Encoded Iso to JPG.
	 *
	 * @param isoEncoded
	 *            the file
	 * @return the response entity
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@PostMapping(path = "/isoEncodedToJpg", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public ResponseEntity<InputStreamResource> isoEncodedToJPG(@RequestBody String isoEncoded) throws IOException {
		byte[] inputBytes = CryptoUtil.decodeBase64(isoEncoded);
		int index;
		for (index = 0; index < inputBytes.length; index++) {
			if ((char) inputBytes[index] == 'j' && (char) inputBytes[index + 1] == 'P') {
				break;
			}
		}
		return convertToJPG(Arrays.copyOfRange(inputBytes, index - 4, inputBytes.length),
				"image");
	}

	/**
	 * Jp 2 to JPG.
	 *
	 * @param file
	 *            the file
	 * @return the response entity
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@PostMapping(path = "/jp2ToJpg", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public ResponseEntity<InputStreamResource> jp2ToJPG(@RequestParam("JP2") MultipartFile file) throws IOException {
		return convertToJPG(file.getBytes(), file.getOriginalFilename());
	}
	
	/**
	 * Jp 2 to JPG.
	 *
	 * @param file
	 *            the file
	 * @return the response entity
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@PostMapping(path = "/jp2EncodedToJpg", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public ResponseEntity<InputStreamResource> jp2EncodedToJPG(@RequestBody String isoEncoded) throws IOException {
		return convertToJPG(CryptoUtil.decodeBase64(isoEncoded), "image");
	}

	/**
	 * Convert to JPG.
	 *
	 * @param jp2Data
	 *            the jp 2 data
	 * @param fileName
	 *            the file name
	 * @return the response entity
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private ResponseEntity<InputStreamResource> convertToJPG(byte[] jp2Data, String fileName) throws IOException {
		J2KImageReader j2kImageReader = new J2KImageReader(null);
		j2kImageReader.setInput(ImageIO.createImageInputStream(new ByteArrayInputStream(jp2Data)));
		ImageReadParam imageReadParam = j2kImageReader.getDefaultReadParam();
		BufferedImage image = j2kImageReader.read(0, imageReadParam);
		ByteArrayOutputStream imgBytes = new ByteArrayOutputStream();
		ImageIO.write(image, "JPG", imgBytes);
		byte[] jpgImg = imgBytes.toByteArray();
		InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(jpgImg));
		HttpHeaders headers = new HttpHeaders();
		headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
		headers.add("Pragma", "no-cache");
		headers.add("Content-Disposition", "attachment; filename=" + fileName + ".jpg");
		headers.add("Expires", "0");
		return ResponseEntity.ok().headers(headers).contentLength(jpgImg.length)
				.contentType(MediaType.parseMediaType("application/octet-stream")).body(resource);
	}

	/**
	 * Check quality.
	 *
	 * @param sample
	 *            the sample
	 * @return the quality score
	 * @throws BiometricException
	 *             the biometric exception
	 */
	@PostMapping(path = "/checkQuality", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public QualityScore checkQuality(@ApiParam("Requires bio.test.server.provider property to be set.\n"
			+ "BIR input should be as follows: \r\n {	\r\n"
			+ "		\"bdbInfo\": {\r\n" + "			\"type\": [\"FINGER\"],\r\n"
			+ "			\"subtype\": [\"Left\", \"Index\"],\r\n" + "			\"format\": {\r\n"
			+ "				\"organization\": \"257\",\r\n" + "				\"type\": \"7\"\r\n" + "			}\r\n"
			+ "		},\r\n" + "		\"bdb\": bdb data\r\n" + "}") @RequestBody BIR sample) throws BiometricException {
		return provider.checkQuality(sample, null);
	}

	/**
	 * Match.
	 *
	 * @param node
	 *            the node
	 * @return the score[]
	 * @throws BiometricException
	 *             the biometric exception
	 * @throws JsonParseException
	 *             the json parse exception
	 * @throws JsonMappingException
	 *             the json mapping exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@SuppressWarnings("unchecked")
	@PostMapping(path = "/match", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public Score[] match(
			@ApiParam("Requires bio.test.server.provider property to be set.\n"
					+ "match should provide input in below json structure:\r\n" + "{\r\n"
					+ " \"probe\" : BIR data,\r\n" + " \"gallery\" : array of BIR data\r\n"
					+ " }") @RequestBody ObjectNode node)
			throws BiometricException, JsonParseException, JsonMappingException, IOException {
		BIR probe = mapper.readValue(node.get("probe").toString(), BIR.class);
		BIR[] gallery = (BIR[]) ((List<BIR>) mapper.readValue(node.get("gallery").toString(),
				new TypeReference<List<BIR>>() {
				})).toArray(new BIR[] {});
		return provider.match(probe, gallery, null);
	}

	/**
	 * Composite match.
	 *
	 * @param node
	 *            the node
	 * @return the composite score
	 * @throws BiometricException
	 *             the biometric exception
	 * @throws JsonParseException
	 *             the json parse exception
	 * @throws JsonMappingException
	 *             the json mapping exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@SuppressWarnings("unchecked")
	@PostMapping(path = "/compositeMatch", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public CompositeScore compositeMatch(
			@ApiParam("Requires bio.test.server.provider property to be set.\n."
					+ "compositeMatch should provide input in below json structure:\r\n" + " { \r\n"
					+ " \"probe\" : array of BIR data,\r\n" + " \"gallery\" : array of BIR data\r\n"
					+ " }") @RequestBody ObjectNode node)
			throws BiometricException, JsonParseException, JsonMappingException, IOException {
		BIR[] probe = (BIR[]) ((List<BIR>) mapper.readValue(node.get("probe").toString(),
				new TypeReference<List<BIR>>() {
				})).toArray(new BIR[] {});
		BIR[] gallery = (BIR[]) ((List<BIR>) mapper.readValue(node.get("gallery").toString(),
				new TypeReference<List<BIR>>() {
				})).toArray(new BIR[] {});
		return provider.compositeMatch(probe, gallery, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.core.bioapi.spi.IBioApi#checkQuality(io.mosip.kernel.core.
	 * cbeffutil.entity.BIR, io.mosip.kernel.core.bioapi.model.KeyValuePair[])
	 */
	@Override
	public QualityScore checkQuality(BIR probe, KeyValuePair[] var2) throws BiometricException {
		return this.checkQuality(probe);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.core.bioapi.spi.IBioApi#compositeMatch(io.mosip.kernel.core.
	 * cbeffutil.entity.BIR[], io.mosip.kernel.core.cbeffutil.entity.BIR[],
	 * io.mosip.kernel.core.bioapi.model.KeyValuePair[])
	 */
	@Override
	public CompositeScore compositeMatch(BIR[] probe, BIR[] gallery, KeyValuePair[] var3) throws BiometricException {
		ObjectNode node = mapper.createObjectNode();
		try {
			node.set("probe", mapper.readTree(mapper.writeValueAsString(probe)));
			List<BIR> asList = Arrays.asList(gallery);
			node.set("gallery", mapper.readTree(mapper.writeValueAsString(asList)));
			return this.compositeMatch(node);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BiometricException("", e.getMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.core.bioapi.spi.IBioApi#match(io.mosip.kernel.core.cbeffutil.
	 * entity.BIR, io.mosip.kernel.core.cbeffutil.entity.BIR[],
	 * io.mosip.kernel.core.bioapi.model.KeyValuePair[])
	 */
	@Override
	public Score[] match(BIR probe, BIR[] gallery, KeyValuePair[] var3) throws BiometricException {
		ObjectNode node = mapper.createObjectNode();
		try {
			node.set("probe", mapper.readTree(mapper.writeValueAsString(Arrays.asList(probe))));
			node.set("gallery", mapper.readTree(mapper.writeValueAsString(Arrays.asList(gallery))));
			return this.match(node);
		} catch (IOException e) {
			e.printStackTrace();
			throw new BiometricException("", e.getMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.core.bioapi.spi.IBioApi#extractTemplate(io.mosip.kernel.core.
	 * cbeffutil.entity.BIR, io.mosip.kernel.core.bioapi.model.KeyValuePair[])
	 */
	@Override
	public BIR extractTemplate(BIR probe, KeyValuePair[] var2) throws BiometricException {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.core.bioapi.spi.IBioApi#segment(io.mosip.kernel.core.
	 * cbeffutil.entity.BIR, io.mosip.kernel.core.bioapi.model.KeyValuePair[])
	 */
	@Override
	public BIR[] segment(BIR var1, KeyValuePair[] var2) throws BiometricException {
		return null;
	}
}
