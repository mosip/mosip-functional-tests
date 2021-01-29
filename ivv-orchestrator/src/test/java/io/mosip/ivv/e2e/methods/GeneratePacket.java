package io.mosip.ivv.e2e.methods;

import static org.testng.Assert.assertFalse;

import java.util.HashMap;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.testng.Reporter;

import io.mosip.authentication.fw.util.ReportUtil;
import io.mosip.authentication.fw.util.RestClient;
import io.mosip.ivv.core.base.StepInterface;
import io.mosip.ivv.orchestrator.BaseTestCaseUtil;
import io.restassured.response.Response;

public class GeneratePacket  extends BaseTestCaseUtil implements StepInterface {

	private String generatePacketUrl = "http://localhost:8888/makepacketandsync/";
	Logger logger = Logger.getLogger(GeneratePacket.class);

    @SuppressWarnings("static-access")
	@Override
    public void run() {
    	generatePacket(this.pridsAndRids);
    }

    public void generatePacket(HashMap<String, String> prids)
    {
    	for(String prid: prids.keySet())
    	{
    		Reporter.log("<b><u>"+"Check-Status"+ "</u></b>");
    		Reporter.log("<pre>" + ReportUtil.getTextAreaJsonMsgHtml("{Prid: "+prid +"}") + "</pre>");
    		long startTime = System.currentTimeMillis();
			logger.info(this.getClass().getSimpleName()+" starts at..."+startTime +" MilliSec");
    		Response response = RestClient.getRequest(generatePacketUrl+prid, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON);
    		long stopTime = System.currentTimeMillis();
			long elapsedTime = stopTime - startTime;
			logger.info("Time taken to execute "+ this.getClass().getSimpleName()+": " +elapsedTime +" MilliSec");
			Reporter.log("<b><u>"+"Time taken to execute "+ this.getClass().getSimpleName()+": " +elapsedTime +" MilliSec"+ "</u></b>");
    		logger.info("Response from packet Generator for PRID: "+prid+" "+response.asString());
    		Reporter.log("<b><u>Actual Response Content: </u></b>(EndPointUrl: " + generatePacketUrl + ") <pre>"
					+ ReportUtil.getTextAreaJsonMsgHtml(response.asString()) + "</pre>");
    		if(!response.asString().contains("Failed")) {
    			JSONObject res = new JSONObject(response.asString());
    			JSONObject responseJson = new JSONObject(res.get("response").toString());
    			pridsAndRids.put(prid, responseJson.get("registrationId").toString());
    		}
    		else {
    			logger.error("Failed in packet generation, not able to get RID with PRID: "+prid);
    			assertFalse(true, "Failed in Packet Generation and uploading with PRID: "+prid);
    		}
    	}
    }
    
}
