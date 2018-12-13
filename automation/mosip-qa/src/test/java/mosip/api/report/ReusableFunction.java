package mosip.api.report;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.eviware.soapui.impl.wsdl.WsdlProject;
import com.eviware.soapui.model.testsuite.TestCase;
import com.eviware.soapui.model.testsuite.TestSuite;
public class ReusableFunction {
	static Set<String> uniqueData = new HashSet<String>();
	/**
	 * To Run A Custom Test Type in 1 Test Suite 
	 * @param project
	 * @param testSuiteName
	 * @param testType
	 * @return
	 */
	public static List<Object[]> runCustomTestCases(WsdlProject project, String testSuiteName, String testType) {
		TestSuite selectedSuite = null;
		List<Object[]> customTestCases = new ArrayList<Object[]>();
		for(TestSuite ts : project.getTestSuiteList()){
			if(ts.getName().equals(testSuiteName)){
				selectedSuite = ts;
				break;	
			}
		}
		Object[] init = {"init", selectedSuite};
		customTestCases.add(init);
		for(TestCase tc: selectedSuite.getTestCaseList()){	
		if(tc.getName().toString().toLowerCase().contains(testType)) {
			
			Object [] dataSource = {tc.getName(), selectedSuite};
			
			customTestCases.add(dataSource);
		}
		}
		return customTestCases;
	}
	/**
	 * To Run 1 Test Type Across Project With All Test Suites 
	 * @param project
	 * @param testType
	 * @return
	 */
	public static List<Object[]> runCustomTestCasesInProject(WsdlProject project,String testType) {
		List<Object[]> customTestCases = new ArrayList<Object[]>();
		for(TestSuite ts : project.getTestSuiteList()){	
			for(TestCase tc: ts.getTestCaseList()){
				if(tc.getName().toString().toLowerCase().contains(testType)) {
					if(!uniqueData.contains(ts.getName())){
						uniqueData.add(ts.getName());
						Object[] init = {"init", ts};
						customTestCases.add(init);
					}
					Object [] dataSource = {tc.getName(), ts};
					customTestCases.add(dataSource);
				}
				}
		}
		return customTestCases;
	}
}
