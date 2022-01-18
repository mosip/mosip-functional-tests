package io.mosip.registrationProcessor.perf.util;

import java.util.*;

import org.hibernate.Session;

import io.mosip.registrationProcessor.perf.dto.RegCenterDetailDto;
import io.mosip.resgistrationProcessor.perf.dbaccess.DBUtil;
import io.mosip.resgistrationProcessor.perf.dbaccess.RegProcPerfDaoImpl;

public class RegCenterDetailFetcher {

	public RegCenterDetailFetcher regCenterDetailFetcher;

	public static List<RegCenterDetailDto> regCenterDetailList;
	private int count;

	public RegCenterDetailFetcher() {
	}





	public RegCenterDetailDto obtainNextData(List<RegCenterDetailDto> listOfCenters) {
		Random rand=new Random();
		int rand_next=rand.nextInt(10);
		RegCenterDetailDto regCenterDetailDto = new RegCenterDetailDto();
		
		regCenterDetailDto = listOfCenters.get(rand_next);
		
		return regCenterDetailDto;

	}


	
	public List<RegCenterDetailDto> getRegistratonCenters(Session session) {
		/*String CONFIG_FILE = "config.properties";
		new PropertiesUtil().loadProperties(CONFIG_FILE);
		Session session=new DBUtil().obtainSession();*/
		List<RegCenterDetailDto> listOfCenters= new RegProcPerfDaoImpl().fetchAllRegistrationCenterDetail(session);
		return listOfCenters;
	}
}
