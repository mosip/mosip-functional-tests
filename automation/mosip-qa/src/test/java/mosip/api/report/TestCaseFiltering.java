package mosip.api.report;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.xmlbeans.XmlException;

import com.eviware.soapui.impl.wsdl.WsdlProject;
import com.eviware.soapui.impl.wsdl.WsdlTestSuite;
import com.eviware.soapui.support.SoapUIException;

public class TestCaseFiltering {
/*public static void main(String[] args) throws IOException, XmlException, SoapUIException {
	  WsdlTestSuite ts;
		 String soapuiProjectPath = "D:\\MOSIP\\Test_Projects\\soapProjects\\v1\\IDA-soapui-project.xml";
		 WsdlProject project = new WsdlProject(soapuiProjectPath);
			ts = project.getTestSuiteByName("otpGenerator");
		List<com.eviware.soapui.impl.wsdl.testcase.WsdlTestCase> filteredTestCases =	(List) project.getTestSuiteByName("otpGenerator").getTestCaseList().stream().filter(i->i.getName().contains("smoke")).collect(Collectors.toList());
		System.out.println(filteredTestCases.get(0).getName());
}*/
}
