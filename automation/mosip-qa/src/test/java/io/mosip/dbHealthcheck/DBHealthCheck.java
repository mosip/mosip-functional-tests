package io.mosip.dbHealthcheck;

import io.mosip.dbaccess.*;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;


public class DBHealthCheck {
	
	private static Logger logger = Logger.getLogger(DBHealthCheck.class);
	
	
	public void kernel_CheckDBConnectivity()
	{
	
	
		boolean dbpreregConnectivity=prereg_dbread.prereg_dbconnectivityCheck();
		Assert.assertEquals(dbpreregConnectivity, true);
		logger.info("Pre-Reg DB is Connected Successfully");
		
	
	}
	
	
	public static void prereg_preIDCheckDB(String preId)
	{
		boolean dbpreregPresence=prereg_dbread.prereg_dbDataPersistenceCheck(preId);
		try {
			if(dbpreregPresence)
			{
				Assert.assertTrue(dbpreregPresence);
			logger.info("Present in DB: " + dbpreregPresence );
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
				logger.info("Pre-Reg Id is not Present in DB");
		}
	}
	

}
