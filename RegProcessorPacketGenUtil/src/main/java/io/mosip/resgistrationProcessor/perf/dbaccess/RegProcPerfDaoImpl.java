package io.mosip.resgistrationProcessor.perf.dbaccess;

import java.util.*;

import javax.persistence.Query;

import org.hibernate.Session;

import io.mosip.registrationProcessor.perf.dto.RegCenterDetailDto;
import io.mosip.registrationProcessor.perf.entity.Location;
import io.mosip.registrationProcessor.perf.util.PropertiesUtil;

public class RegProcPerfDaoImpl {
	DBUtil dbUtil = new DBUtil();
	String CONFIG_FILE = "config.properties";
	PropertiesUtil prop = new PropertiesUtil(CONFIG_FILE);

	public List<Location> getCountry(Session session, String countryCode) {

		//Session session = dbUtil.obtainSession(prop);
		List<Location> locations = new ArrayList<>();
		Query q = session.createQuery("from Location where lang_code='eng' and hierarchy_level=0 and code='"+countryCode+"'");
		locations = q.getResultList();
		//System.out.println("Country fetched: " + locations.size());
		//session.close();
		return locations;
	}

	public List<Location> getLocations(String parentLocationCode, int hierarchyLevel, Session session) {
		List<Location> locations = new ArrayList<>();
		String query = "from Location where lang_code='eng' and hierarchy_level=" + hierarchyLevel
				+ " and parent_loc_code='" + parentLocationCode + "'";
		// Session session = dbUtil.obtainSession(prop);
		Query q = session.createQuery(query);
		locations = q.getResultList();
		//session.close();
		return locations;
	}

	public String getTranslatedLocation(String locationName, String toLangCode, int hierarchy_level, Session session) {
		String result = "";
		String fromLangCode = "eng";

		//System.out.println("Fetching " + locationName + " in " + toLangCode);
		String queryString = "select name from Location where lang_code='" + toLangCode
				+ "' and code in (SELECT code FROM Location where name='" + locationName + "' and lang_code='"
				+ fromLangCode + "' and hierarchy_level=" + hierarchy_level + ")";
		// Session session = dbUtil.obtainSession(prop);
		Query query = session.createQuery(queryString);

		result = (String) query.getSingleResult();
		//session.close();
		return result;
	}

	public List<RegCenterDetailDto> fetchAllRegistrationCenterDetail(Session session) {
		List<RegCenterDetailDto> regCenterList = new ArrayList<>();

		String queryString = "select t_user.registrationCenterUserID.regCenterId,t_user.registrationCenterUserID.userId,t_machine.registrationCenterMachinePk.machineId from  ";
		queryString += "RegistrationCenterUser t_user INNER JOIN RegistrationCenterMachine t_machine ";
		queryString += "on t_user.registrationCenterUserID.regCenterId=t_machine.registrationCenterMachinePk.regCenterId";
		queryString = "SELECT t.registrationCenterMachineUserID.cntrId, t.registrationCenterMachineUserID.machineId,";
		queryString += " t.registrationCenterMachineUserID.usrId from RegistrationCenterUserMachine t ";
		queryString += "where t.langCode ='eng' and t.isActive=true";

		// Session session = dbUtil.obtainSession(prop);
		Query query = session.createQuery(queryString);
		List<Object[]> results = query.getResultList();
		for (Object[] row : results) {
			// System.out.println(row[0] + " " + row[1] + " " + row[2]);
			RegCenterDetailDto obj = new RegCenterDetailDto();
			obj.setRegCenterId(row[0].toString());
			obj.setMachineId(row[1].toString());
			obj.setUserId(row[2].toString());
			regCenterList.add(obj);
		}
		//session.close();
		return regCenterList;
	}

}
