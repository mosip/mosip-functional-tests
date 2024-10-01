package io.mosip.testrig.apirig.testrunner;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Class to initiate mosip api test execution
 * 
 * @author Vignesh
 *
 */
public class MosipTestRunner {
	private static final Logger LOGGER = Logger.getLogger(MosipTestRunner.class);

	public static String jarUrl = MosipTestRunner.class.getProtectionDomain().getCodeSource().getLocation().getPath();
	public static List<String> languageList = new ArrayList<>();

	public static void main(String[] arg) {}
}