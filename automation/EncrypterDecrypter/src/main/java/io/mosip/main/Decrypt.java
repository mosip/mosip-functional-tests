package io.mosip.main;

import java.util.List;

import org.testng.TestListenerAdapter;
import org.testng.TestNG;
import org.testng.collections.Lists;

public class Decrypt {
	public static void main(String[] args) {
		 TestListenerAdapter tla = new TestListenerAdapter();
		    TestNG testng = new TestNG();
		    List<String> suites = Lists.newArrayList();
		    String pathToXml=System.getProperty("user.dir")+"/src/test/resources/testNg.xml";
		    suites.add(pathToXml);
		    testng.setTestSuites(suites);
		    testng.run();
	}
	
}
