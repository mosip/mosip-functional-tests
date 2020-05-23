package io.mosip.authentication.partnerdemo.service.controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.partnerdemo.service.helper.DBUtil;
import io.mosip.kernel.core.logger.spi.Logger;
import io.swagger.annotations.Api;

@RestController
@Api(tags = { "SQL Performance" })
public class SQLPerformanceController {

	/** The logger. */
	private static Logger logger = IdaLogger.getLogger(SQLPerformanceController.class);

	@Autowired
	private DBUtil dbUtil;

	@PostMapping(path = "/selectQueryWithTime", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> selectQueryWithTime(@RequestBody String sqlQuery,
			@RequestParam(name = "dbName", required = false, defaultValue=DBUtil.IDA) String dbName) {

		String jdbcUrl = dbUtil.getDbUrl(dbName);
		String username = dbUtil.getDbUser(dbName);
		String password = dbUtil.getDbPass(dbName);

		try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password);
				Statement stmt = conn.createStatement();
				) {
			LocalDateTime start = LocalDateTime.now();
			try(
				ResultSet resultSet = stmt.executeQuery(sqlQuery);) {
				LocalDateTime end = LocalDateTime.now();
				
				HashMap<String, Object> responseMap = new HashMap<>();
				List<Map<String, Object>> resultSetToList = resultSetToList(resultSet);
				responseMap.put("ResultSet", resultSetToList);
				responseMap.put("QueryTimeMilliSeconds", Duration.between(start, end).toMillis());
				return responseMap;
			}
		} catch (SQLException e) {
			logger.info("sessionID", "IDA", "selectQueryWithTime", "SQLException :" + e.getMessage());
			HashMap<String, Object> responseMap = new HashMap<>();
			responseMap.put("ErrorResult", e.getMessage());
			return responseMap;
		}
	}

	private List<Map<String, Object>> resultSetToList(ResultSet rs) throws SQLException {
		ResultSetMetaData md = rs.getMetaData();
		int columns = md.getColumnCount();
		List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
		while (rs.next()) {
			Map<String, Object> row = new HashMap<String, Object>(columns);
			for (int i = 1; i <= columns; ++i) {
				row.put(md.getColumnName(i), rs.getObject(i));
			}
			rows.add(row);
		}
		return rows;
	}

}
