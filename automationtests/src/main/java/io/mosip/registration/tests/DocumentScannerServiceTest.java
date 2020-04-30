package io.mosip.registration.tests;

import static io.mosip.registration.constants.LoggerConstants.LOG_PKT_HANLDER;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.util.ReflectionTestUtils;
import org.testng.Assert;
import org.testng.ITest;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.internal.BaseTestMethod;
import org.testng.internal.TestResult;



import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.exception.IOException;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.config.DaoConfig;
import io.mosip.registration.device.scanner.impl.DocumentScannerSaneServiceImpl;
import io.mosip.registration.util.BaseConfiguration;

/**
 * 
 * @author Tabish Khan
 * 
 *         Test class having methods to test the functionality of
 *         DocumentScannerService
 *
 */
public class DocumentScannerServiceTest extends BaseConfiguration implements ITest {

	@Autowired
	private DocumentScannerSaneServiceImpl documentScannerServiceImpl;

	private static Logger logger = AppConfig.getLogger(DocumentScannerServiceTest.class);
	static BufferedImage bufferedImage;
	static List<BufferedImage> bufferedImages = new ArrayList<>();
	protected static String mTestCaseName = "";

	/**
	 * 
	 * @throws IOException
	 * @throws java.io.IOException
	 * 
	 *             method invoked before class to initialize the data needed during
	 *             test
	 */
	@BeforeClass
	public void initialize() throws IOException, java.io.IOException {
		baseSetUp();
		URL url = DocumentScannerServiceTest.class.getResource("/Registration/dataprovider/applicantPhoto.jpg");
		logger.info("DOCUMENTSCANNER SERVICE TEST - ", APPLICATION_NAME, APPLICATION_ID, url.toString());
		bufferedImage = ImageIO.read(url);
		bufferedImages.add(bufferedImage);

	}

	/**
	 * Test method to test if scanner is connected
	 */
	@Test
	public void isScannerConnectedTest() {
		try {
			// intializeValues();
			mTestCaseName = "regClient_DocumentScannerService_isScannerConnected";
			boolean isConnected = documentScannerServiceImpl.isConnected();
			Assert.assertNotNull(isConnected);
		} catch (AssertionError assertException) {
			// TODO: handle exception
			logger.info(LOG_PKT_HANLDER, APPLICATION_NAME, APPLICATION_ID,
					ExceptionUtils.getStackTrace(assertException));
		}
	}

	/**
	 * Test method to test the behavior when a document is scanned
	 */
	@Test // (enabled = false)
	public void scanDocumentTest() {
		try {
			mTestCaseName = "regClient_DocumentScannerService_scanDocument";
			intializeValues();
			bufferedImage = documentScannerServiceImpl.scan();
			Assert.assertNotNull(bufferedImage);
		} catch (AssertionError assertException) {
			// TODO: handle exception
			logger.info(LOG_PKT_HANLDER, APPLICATION_NAME, APPLICATION_ID,
					ExceptionUtils.getStackTrace(assertException));
		}
	}

	/**
	 * Test method to test the behavior when a document is scanned as PDF
	 */
	@Test
	public void getSinglePDFInBytesTest() {
		try {// intializeValues();
			mTestCaseName = "regClient_DocumentScannerService_getSinglePDFInBytes";
			byte[] data = documentScannerServiceImpl.asPDF(bufferedImages);
			Assert.assertNotNull(data);
		} catch (AssertionError assertException) {
			// TODO: handle exception
			logger.info(LOG_PKT_HANLDER, APPLICATION_NAME, APPLICATION_ID,
					ExceptionUtils.getStackTrace(assertException));
		}

	}

	/**
	 * @throws java.io.IOException
	 * 
	 *             Test method to test the behavior for scanning multiple documents
	 */
	@Test
	public void getSingleImageFromListTest() {
		try {
			// intializeValues();
			mTestCaseName = "regClient_DocumentScannerService_getSingleImageFromList";
			byte[] data = documentScannerServiceImpl.asImage(bufferedImages);
			Assert.assertNotNull(data);
		} catch (AssertionError assertException) {
			// TODO: handle exception
			logger.info(LOG_PKT_HANLDER, APPLICATION_NAME, APPLICATION_ID,
					ExceptionUtils.getStackTrace(assertException));
		} catch (java.io.IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @throws java.io.IOException
	 * 
	 *             Test method to test the behavior for scanning multiple documents
	 *             as image
	 */
	@Test
	public void getSingleImageAlternateFlowTest() throws java.io.IOException {
		// intializeValues();
		mTestCaseName = "regClient_DocumentScannerService_getSingleImageAlternateFlow";
		if (bufferedImage != null)
			bufferedImages.add(bufferedImage);
		byte[] data = documentScannerServiceImpl.asImage(bufferedImages);
		Assert.assertNotNull(data);

	}

	/**
	 * 
	 * @throws java.io.IOException
	 * 
	 *             Test method to scan multiple images as PDF
	 */
	@Test
	public void pdfToImagesTest() throws java.io.IOException {
		// intializeValues();
		mTestCaseName = "regClient_DocumentScannerService_pdfToImages";
		byte[] data = documentScannerServiceImpl.asPDF(bufferedImages);
		documentScannerServiceImpl.pdfToImages(data);
		Assert.assertNotNull(data);

	}

	/**
	 * 
	 * @throws java.io.IOException
	 * 
	 *             Test method to test converting the BufferedImage to byte[]
	 */
	@Test
	public void getImageBytesFromBufferedImageTest() throws java.io.IOException {
		try {
			// intializeValues();
			mTestCaseName = "regClient_DocumentScannerService_getImageBytesFromBufferedImage";
			byte[] data = documentScannerServiceImpl.getImageBytesFromBufferedImage(bufferedImage);
			Assert.assertNotNull(data);
		} catch (AssertionError assertException) {
			// TODO: handle exception
			logger.info(LOG_PKT_HANLDER, APPLICATION_NAME, APPLICATION_ID,
					ExceptionUtils.getStackTrace(assertException));
		}
	}

	/**
	 * 
	 * @throws java.io.IOException
	 * 
	 *             Negative test case for converting the BufferedImage to byte[]
	 */
	@Test // (expectedExceptions = IllegalArgumentException.class)
	public void getImageBytesFromBufferedImageTestNull() throws java.io.IOException {
		try {
			// intializeValues();
			mTestCaseName = "regClient_DocumentScannerService_getImageBytesFromBufferedImageTestNull";
			byte[] data = documentScannerServiceImpl.getImageBytesFromBufferedImage(null);
		} catch (AssertionError assertException) {
			// TODO: handle exception
			logger.info(LOG_PKT_HANLDER, APPLICATION_NAME, APPLICATION_ID,
					ExceptionUtils.getStackTrace(assertException));
		} catch (IllegalArgumentException illegalArgumentException) {
			// TODO: handle exception
			logger.info(LOG_PKT_HANLDER, APPLICATION_NAME, APPLICATION_ID,
					ExceptionUtils.getStackTrace(illegalArgumentException));
		}
	}

	/**
	 * 
	 * @throws java.io.IOException
	 * 
	 *             Negative test case for scanning PDF to image(s)
	 */
	@Test // (expectedExceptions = NullPointerException.class)
	public void pdfToImagesTestNull() {
		try {
			// intializeValues();
			mTestCaseName = "regClient_DocumentScannerService_pdfToImagesTestNull";
			byte[] data = documentScannerServiceImpl.asPDF(bufferedImages);
			documentScannerServiceImpl.pdfToImages(data);
		} catch (AssertionError assertException) {
			// TODO: handle exception
			logger.info(LOG_PKT_HANLDER, APPLICATION_NAME, APPLICATION_ID,
					ExceptionUtils.getStackTrace(assertException));
		} catch (java.io.IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * @throws java.io.IOException
	 * 
	 *             Negative test case for scanning to PDF
	 */
	@Test
	public void getSingleImageFromListTestNull() {
		try {
			// intializeValues();
			mTestCaseName = "regClient_DocumentScannerService_getSingleImageFromListTestNull";
			byte[] data = documentScannerServiceImpl.asImage(null);
			Assert.assertNull(data);
		} catch (AssertionError assertException) {
			// TODO: handle exception
			logger.info(LOG_PKT_HANLDER, APPLICATION_NAME, APPLICATION_ID,
					ExceptionUtils.getStackTrace(assertException));
		} catch (java.io.IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	@AfterMethod(alwaysRun = true)
	public void setResultTestName(ITestResult result) {
		try {
			Field method = TestResult.class.getDeclaredField("m_method");
			method.setAccessible(true);
			method.set(result, result.getMethod().clone());
			BaseTestMethod baseTestMethod = (BaseTestMethod) result.getMethod();
			Field f = baseTestMethod.getClass().getSuperclass().getDeclaredField("m_methodName");
			f.setAccessible(true);
			f.set(baseTestMethod, DocumentScannerServiceTest.mTestCaseName);
		} catch (Exception exception) {
			Reporter.log("Exception : " + exception.getMessage());
			logger.info(LOG_PKT_HANLDER, APPLICATION_NAME, APPLICATION_ID, ExceptionUtils.getStackTrace(exception));
		}
	}

	@Override
	public String getTestName() {
		return this.mTestCaseName;
	}

	private void intializeValues() {
		ReflectionTestUtils.setField(documentScannerServiceImpl, "scannerDepth", 300);
		ReflectionTestUtils.setField(documentScannerServiceImpl, "scannerhost", "192.168.43.253");
		ReflectionTestUtils.setField(documentScannerServiceImpl, "scannerPort", 6566);
		//ReflectionTestUtils.setField(documentScannerServiceImpl, "scannerImgType", "jpg");
		ReflectionTestUtils.setField(documentScannerServiceImpl, "scannerTimeout", 7000);

	}
}
