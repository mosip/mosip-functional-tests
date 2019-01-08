package listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;


public class ExecutionListener implements TestExecutionListener{

//	public void testPlanExecutionStarted(TestPlan testPlan) {
//		// TODO Auto-generated method stub
//		TestExecutionListener.super.testPlanExecutionStarted(testPlan);
//	}
//
//	public void executionStarted(TestIdentifier testIdentifier) {
//		// TODO Auto-generated method stub
//		TestExecutionListener.super.executionStarted(testIdentifier);
//	}

	List<Map<String, Object>> testCases = new ArrayList<Map<String, Object>>();
	 
	public void executionFinished(TestIdentifier testIdentifier, TestExecutionResult
            testExecutionResult) {
        if (testIdentifier.isTest()) {
            System.out.println("Execution finished: " + testIdentifier.getDisplayName() + " " +
                    testExecutionResult.toString());
            String result = testExecutionResult.getStatus().toString();
            // Tesults requires result to be one of: [pass, fail, unknown]
            if (result == "SUCCESSFUL") {
                result = "pass";
            } else if (result == "FAILED") {
                result = "fail";
            } else {
                result = "unknown";
            }
            String reason = "";
            if (testExecutionResult.getThrowable().isPresent()) {
                reason = testExecutionResult.getThrowable().get().getMessage();
            }
            String suite = "";
            String separator = "class:";
            if (testIdentifier.getParentId().isPresent()) {
                suite = testIdentifier.getParentId().get();
                suite = suite.substring(suite.indexOf(separator) + separator.length(), suite.lastIndexOf("]"));
            }
            Map<String, Object> testCase = new HashMap<String, Object>();
            String name = testIdentifier.getDisplayName();
            if (name.indexOf("(") != -1) {
                name = name.substring(0, name.lastIndexOf("("));
            }
            testCase.put("name", name);
            testCase.put("result", result);
            testCase.put("suite", suite);
            testCase.put("desc", testIdentifier.getDisplayName());
            testCase.put("reason", reason);
            // (Optional) For uploading files:
            //List<String> files = new ArrayList<String>();
            //files.add("/path-to-files/test-name/img1.png");
            //files.add("/path-to-files/test-name/img2.png");
            //testCase.put("files", files);
            testCases.add(testCase);
        }
    }

	public void testPlanExecutionFinished(TestPlan testPlan) {
        // Map<String, Object> to hold your test results data.
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("target", "token");
        Map<String, Object> results = new HashMap<String, Object>();
        results.put("cases", testCases);
        data.put("results", results);
        System.out.println("Ab tak ni fata:  "+results);
    }
	
	
}
