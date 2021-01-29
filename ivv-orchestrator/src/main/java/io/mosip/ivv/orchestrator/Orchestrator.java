package io.mosip.ivv.orchestrator;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import org.apache.commons.collections4.CollectionUtils;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.SkipException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.mosip.ivv.core.base.StepInterface;
import io.mosip.ivv.core.dtos.ParserInputDTO;
import io.mosip.ivv.core.dtos.RegistrationUser;
import io.mosip.ivv.core.dtos.Scenario;
import io.mosip.ivv.core.dtos.Store;
import io.mosip.ivv.core.exceptions.RigInternalError;
import io.mosip.ivv.core.utils.Utils;
import io.mosip.ivv.dg.DataGenerator;
import io.mosip.ivv.parser.Parser;

public class Orchestrator {
	private static ExtentHtmlReporter htmlReporter;
	public static ExtentReports extent;
	private Properties properties;
	private HashMap<String, String> packages = new HashMap<String, String>() {
		{
			put("e2e", "io.mosip.ivv.e2e.methods");
			put("pr", "io.mosip.ivv.preregistration.methods");
			put("rc", "io.mosip.ivv.registration.methods");
			put("rp", "io.mosip.ivv.regprocessor.methods");
			put("kr", "io.mosip.ivv.kernel.methods");
			put("ia", "io.mosip.ivv.ida.methods");
			put("mt", "io.mosip.ivv.mutators.methods");
		}
	};

	@BeforeSuite
	public void beforeSuite() {
		this.properties = Utils.getProperties(TestRunner.getGlobalResourcePath()+"/config/config.properties");
		this.configToSystemProperties();
		Utils.setupLogger(System.getProperty("user.dir") + this.properties.getProperty("ivv._path.auditlog"));
		/* setting exentreport */
		htmlReporter = new ExtentHtmlReporter(
				System.getProperty("user.dir") + this.properties.getProperty("ivv._path.reports"));
		extent = new ExtentReports();
		extent.attachReporter(htmlReporter);
	}

	@BeforeTest
	public static void create_proxy_server() {

	}

	@AfterSuite
	public void afterSuite() {
		extent.flush();
	}

	@DataProvider(name = "ScenarioDataProvider", parallel = false)
	public static Object[][] dataProvider() throws RigInternalError {
		String configFile =TestRunner.getGlobalResourcePath()+"/config/config.properties";
		Properties properties = Utils.getProperties(configFile);
		
		ParserInputDTO parserInputDTO = new ParserInputDTO();
		parserInputDTO.setConfigProperties(properties);
		parserInputDTO.setDocumentsFolder(TestRunner.getGlobalResourcePath()+"/"+properties.getProperty("ivv.path.documents.folder"));
		//System.out.println(":Path:"+TestRunner.getGlobalResourcePath()+"/"+properties.getProperty("ivv.path.documents.folder"));
		parserInputDTO.setBiometricsFolder(TestRunner.getGlobalResourcePath()+"/"+properties.getProperty("ivv.path.biometrics.folder"));
		parserInputDTO.setPersonaSheet(TestRunner.getGlobalResourcePath()+"/"+properties.getProperty("ivv.path.persona.sheet"));
		parserInputDTO.setScenarioSheet(TestRunner.getGlobalResourcePath()+"/"+properties.getProperty("ivv.path.scenario.sheet"));
		parserInputDTO.setRcSheet(TestRunner.getGlobalResourcePath()+"/"+properties.getProperty("ivv.path.rcpersona.sheet"));
		parserInputDTO.setPartnerSheet(TestRunner.getGlobalResourcePath()+"/"+properties.getProperty("ivv.path.partner.sheet"));
		parserInputDTO.setIdObjectSchema(TestRunner.getGlobalResourcePath()+"/"+properties.getProperty("ivv.path.idobject"));
		parserInputDTO.setDocumentsSheet(TestRunner.getGlobalResourcePath()+"/"+properties.getProperty("ivv.path.documents.sheet"));
		parserInputDTO.setBiometricsSheet(TestRunner.getGlobalResourcePath()+"/"+properties.getProperty("ivv.path.biometrics.sheet"));
		parserInputDTO.setGlobalsSheet(TestRunner.getGlobalResourcePath()+"/"+properties.getProperty("ivv.path.globals.sheet"));
		parserInputDTO.setConfigsSheet(TestRunner.getGlobalResourcePath()+"/"+properties.getProperty("ivv.path.configs.sheet"));

		Parser parser = new Parser(parserInputDTO);
		DataGenerator dg = new DataGenerator();
		ArrayList<Scenario> scenarios = new ArrayList<>();
		try {
			scenarios = dg.prepareScenarios(parser.getScenarios(), parser.getPersonas());
		} catch (RigInternalError rigInternalError) {
			rigInternalError.printStackTrace();
		}

		for (int i = 0; i < scenarios.size(); i++) {
			scenarios.get(i).setRegistrationUsers(parser.getRCUsers());
			scenarios.get(i).setPartners(parser.getPartners());
		}
		HashMap<String, String> configs = parser.getConfigs();
		HashMap<String, String> globals = parser.getGlobals();
		ArrayList<RegistrationUser> rcUsers = parser.getRCUsers();
		Object[][] dataArray = new Object[scenarios.size()][5];
		for (int i = 0; i < scenarios.size(); i++) {
			dataArray[i][0] = i;
			dataArray[i][1] = scenarios.get(i);
			dataArray[i][2] = configs;
			dataArray[i][3] = globals;
			dataArray[i][4] = properties;
		}
		return dataArray;
	}

	@BeforeMethod
	public void beforeMethod(Method method) {

	}

	@Test(dataProvider = "ScenarioDataProvider")
	private void run(int i, Scenario scenario, HashMap<String, String> configs, HashMap<String, String> globals,
			Properties properties) throws SQLException {
		String tags = System.getProperty("ivv.tags");
		if (tags == null || tags.isEmpty()) {
			Utils.auditLog.info("Running Scenario #" + scenario.getId());
		} else if (!matchTags(tags, scenario.getTags())) {
			Utils.auditLog.info("Skipping Scenario #" + scenario.getId());
			throw new SkipException("Skipping Scenario #" + scenario.getId());
		}
		ObjectMapper mapper = new ObjectMapper();
		try {
			String stepsAsString = mapper.writeValueAsString(scenario.getSteps());
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		Utils.auditLog.info("");
		Utils.auditLog.info("-- *** Scenario " + scenario.getId() + ": " + scenario.getDescription() + " *** --");
		ExtentTest extentTest = extent.createTest("Scenario_" + scenario.getId() + ": " + scenario.getDescription());
		Store store = new Store();
		store.setConfigs(configs);
		store.setGlobals(globals);
		store.setPersona(scenario.getPersona());
		store.setRegistrationUsers(scenario.getRegistrationUsers());
		store.setPartners(scenario.getPartners());
		store.setProperties(this.properties);
		for (Scenario.Step step : scenario.getSteps()) {
			Utils.auditLog.info("");
			String identifier = "> #[Test Step: " + step.getName() + "] [module: " + step.getModule() + "] [variant: "
					+ step.getVariant() + "]";
			Utils.auditLog.info(identifier);
			try {
				extentTest.info(identifier + " - running");
				// extentTest.info("parameters: "+step.getParameters().toString());
				StepInterface st = getInstanceOf(step);
				st.setExtentInstance(extentTest);
				st.setSystemProperties(properties);
				st.setState(store);
				st.setStep(step);
				st.setup();
				st.validateStep();
				st.run();
				st.assertHttpStatus();
				if (st.hasError()) {
					extentTest.fail(identifier + " - failed");
					Assert.assertFalse(st.hasError());
				}
				if (st.getErrorsForAssert().size() > 0) {
					st.errorHandler();
					if (st.hasError()) {
						extentTest.fail(identifier + " - failed");
						Assert.assertFalse(st.hasError());
					}
				} else {
					st.assertNoError();
					if (st.hasError()) {
						extentTest.fail(identifier + " - failed");
						Assert.assertFalse(st.hasError());
					}
				}
				store = st.getState();
				if (st.hasError()) {
					extentTest.fail(identifier + " - failed");
					Assert.assertFalse(st.hasError());
				} else {
					extentTest.pass(identifier + " - passed");
				}
			} catch (ClassNotFoundException e) {
				extentTest.error(identifier + " - ClassNotFoundException --> " + e.toString());
				e.printStackTrace();
				Assert.assertTrue(false);
				return;
			} catch (IllegalAccessException e) {
				extentTest.error(identifier + " - IllegalAccessException --> " + e.toString());
				e.printStackTrace();
				Assert.assertTrue(false);
				return;
			} catch (InstantiationException e) {
				extentTest.error(identifier + " - InstantiationException --> " + e.toString());
				e.printStackTrace();
				Assert.assertTrue(false);
				return;
			} catch (RigInternalError e) {
				extentTest.error(identifier + " - RigInternalError --> " + e.toString());
				e.printStackTrace();
				Assert.assertTrue(false);
				return;
			} catch (RuntimeException e) {
				extentTest.error(identifier + " - RuntimeException --> " + e.toString());
				e.printStackTrace();
				Assert.assertTrue(false);
				return;
			}
		}
	}


	private String getPackage(Scenario.Step step) {
		String pack = packages.get(step.getModule().toString());
		return pack;
	}

	@AfterMethod
	public void afterMethod(ITestResult result) {

	}

	@SuppressWarnings("deprecation")
	public StepInterface getInstanceOf(Scenario.Step step)
			throws ClassNotFoundException, IllegalAccessException, InstantiationException {
		String className = getPackage(step) + "." + step.getName().substring(0, 1).toUpperCase()
				+ step.getName().substring(1);
		return (StepInterface) Class.forName(className).newInstance();
	}

	private void configToSystemProperties() {
		Set<String> keys = this.properties.stringPropertyNames();
		for (String key : keys) {
			System.setProperty(key, this.properties.getProperty(key));
		}
	}

	private static Boolean matchTags(String systemTags, ArrayList<String> scenarioTags) {
		List<String> sys = Arrays.asList(systemTags.split(","));
		return CollectionUtils.containsAny(sys, scenarioTags);
	}

}
