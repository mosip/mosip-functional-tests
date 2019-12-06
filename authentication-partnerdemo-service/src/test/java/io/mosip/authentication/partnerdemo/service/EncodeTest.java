package io.mosip.authentication.partnerdemo.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import io.mosip.authentication.partnerdemo.service.controller.Encode;

// TODO: Auto-generated Javadoc
/**
 * @author Arun Bose S The Class EncodeTest.
 */
@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class EncodeTest {

	/** The encode mock. */
	@InjectMocks
	private Encode encodeMock;

	/**
	 * Encode test.
	 */
	@Test
	public void encodeTest() {
		encodeMock.encode("SampleData");
	}

	/**
	 * Encode file test.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void encodeFileTest() throws IOException {
		encodeMock.encodeFile(new MultipartFile() {

			@Override
			public void transferTo(File dest) throws IOException, IllegalStateException {
				// TODO Auto-generated method stub

			}

			@Override
			public boolean isEmpty() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public long getSize() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public String getOriginalFilename() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public InputStream getInputStream() throws IOException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getContentType() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public byte[] getBytes() throws IOException {
				// TODO Auto-generated method stub
				return null;
			}
		});

	}

}
