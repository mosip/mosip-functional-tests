package io.mosip.ivv.e2e.methods;

import static org.testng.Assert.assertFalse;

import java.io.File;
import java.util.HashMap;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.testng.Reporter;

import io.mosip.authentication.fw.util.FileUtil;
import io.mosip.authentication.fw.util.ReportUtil;
import io.mosip.authentication.fw.util.RestClient;
import io.mosip.ivv.core.base.StepInterface;
import io.mosip.ivv.orchestrator.BaseTestCaseUtil;
import io.mosip.ivv.orchestrator.TestResources;
import io.mosip.kernel.util.KernelAuthentication;
import io.mosip.service.BaseTestCase;
import io.restassured.response.Response;

public class GetIdentityByRid extends BaseTestCaseUtil implements StepInterface {

	private String getIdentityUrl = "/idrepository/v1/identity/idvid/";
	private String identitypath = "preReg/identity/";
	Logger logger = Logger.getLogger(GetIdentityByRid.class);
	KernelAuthentication kauth = new KernelAuthentication();
    @SuppressWarnings("static-access")
	@Override
    public void run() {
    	getIdentity(this.pridsAndRids);
    }

    public void getIdentity(HashMap<String, String> rids)
    {
    	uinReqIds.clear();
    	for(String rid: rids.values())
    	{
    		if(rid!=null) {
    			//rid = "6834197843"; this is to use uin instead of rid when Packet is not synced 
    			Reporter.log("<b><u>"+"GetIdentity By Rid"+ "</u></b>");
        		Reporter.log("<pre>" + ReportUtil.getTextAreaJsonMsgHtml("{Rid: "+rid +"}") + "</pre>");
        		long startTime = System.currentTimeMillis();
				logger.info(this.getClass().getSimpleName()+" starts at..."+startTime +" MilliSec");
        		Response response = RestClient.getRequestWithCookie(BaseTestCase.ApplnURI+getIdentityUrl+rid, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, "Authorization", kauth.getTokenByRole("regproc"));
        		long stopTime = System.currentTimeMillis();
				long elapsedTime = stopTime - startTime;
				logger.info("Time taken to execute "+ this.getClass().getSimpleName()+": " +elapsedTime +" MilliSec");
				Reporter.log("<b><u>"+"Time taken to execute "+ this.getClass().getSimpleName()+": " +elapsedTime +" MilliSec"+ "</u></b>");
				logger.info("Response from get Identity for RID: "+rid+" "+response.asString());
    		Reporter.log("<b><u>Actual Response Content: </u></b>(EndPointUrl: " + BaseTestCase.ApplnURI+getIdentityUrl+rid + ") <pre>"
					+ ReportUtil.getTextAreaJsonMsgHtml(response.asString()) + "</pre>");
    		JSONObject res = new JSONObject(response.asString());
    		if(!res.get("response").toString().equals("null"))
    		{
    			JSONObject respJson = new JSONObject(res.get("response").toString());
    			JSONObject identityJson = new JSONObject(respJson.get("identity").toString());
    			String uin = identityJson.get("UIN").toString();
    			uinReqIds.put(uin, null);
    			FileUtil.createFile(new File(TestResources.getResourcePath()+identitypath+uin+".json"), response.asString());
    		}else
    			{
    				logger.error("Issue while fetching identity for RID: "+rid+" Response: "+res.toString());
    				assertFalse(true, "Not able to Fetch identity for RID: "+rid);
    			}
    			
    		}
    	}
    }
    
}
