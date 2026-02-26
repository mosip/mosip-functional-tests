package io.mosip.testrig.apirig.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.jdbc.Work;

import io.mosip.idrepository.core.constant.IdRepoErrorConstants;
import io.mosip.idrepository.core.exception.IdRepoAppUncheckedException;
import io.mosip.kernel.core.exception.ExceptionUtils;
import java.security.NoSuchAlgorithmException;
import io.mosip.kernel.core.util.HMACUtils2;
import io.mosip.testrig.apirig.dbaccess.DBManager;
import io.mosip.testrig.apirig.dto.TestCaseDTO;
import io.mosip.testrig.apirig.testrunner.BaseTestCase;
import io.mosip.testrig.apirig.testrunner.JsonPrecondtion;

import org.springframework.util.Assert;

public class GetCredentialTableStackTrace extends DBManager {

    private static final Logger logger = Logger.getLogger(GetCredentialTableStackTrace.class);

    private static final String HOST = ConfigManager.getproperty("db-server");
    private static final String IDREPO_DB_URL = "jdbc:postgresql://" + HOST + ":" + ConfigManager.getproperty("db-port") + "/mosip_idrepo";
    private static final String CREDENTIAL_DB_URL = "jdbc:postgresql://" + HOST + ":" + ConfigManager.getproperty("db-port") + "/mosip_credential";
    private static final String DB_USER =  "postgres";
    private static final String DB_PASSWORD =  ConfigManager.getproperty("postgres-password");
    private static final int SALT_KEY_LENGTH = 3;

    public static String getCredentialStatus(String uin) {
        return getRequestIdFromIdRepoCredentialRequestStatusTable(uin);
    }

    public static String getRequestIdFromIdRepoCredentialRequestStatusTable(String uin) {
        String query = "SELECT request_id FROM idrepo.credential_request_status "
                + "WHERE individual_id LIKE ? ORDER BY cr_dtimes DESC LIMIT 1";
        String likePattern = uin + "_%";
        try {
            String requestId = executeDBWithSelectQueries(IDREPO_DB_URL, DB_USER, DB_PASSWORD, "idrepo", query, likePattern);
            if (requestId == null || requestId.isEmpty()) {
                logger.warn("No request_id found for UIN: " + uin);
                return null;
            }
            return getStatusFromCredentialTransactionTable(requestId);
        } catch (AdminTestException e) {
            logger.error("Error retrieving request_id for UIN: " + uin, e);
            return null;
        }
    }

    public static String getStatusFromCredentialTransactionTable(String requestID) {
        String query = "SELECT status_code FROM credential.credential_transaction WHERE id = ?";
        try {
            String status = executeDBWithSelectQueries(CREDENTIAL_DB_URL, DB_USER, DB_PASSWORD, "credential", query, requestID);
            if (status == null || status.isEmpty()) {
                logger.warn("No status_code found for requestID: " + requestID);
                return null;
            }
            return status;
        } catch (AdminTestException e) {
            logger.error("Error retrieving status for requestID: " + requestID, e);
            return null;
        }
    }

    public static String executeDBWithSelectQueries(String dbURL, String dbUser, String dbPassword, String dbSchema,
            String query, String... parameters) throws AdminTestException {
        Session session = null;
        try {
            session = getDataBaseConnection(dbURL, dbUser, dbPassword, dbSchema);
            if (session == null) {
                throw new AdminTestException("Failed to obtain DB session.");
            }
            String result = executeSelectQuery(session, query, parameters);
            if (result == null || result.isEmpty()) {
                logger.warn("No result returned for query: " + query);
            } else {
                logger.info("Retrieved result: " + result);
            }
            return result;
        } catch (Exception e) {
            logger.error("Exception executing DB query", e);
            throw new AdminTestException("Exception executing DB query: " + e.getMessage());
        } finally {
            if (session != null) {
                try {
                    closeDataBaseConnection(session);
                } catch (Exception e) {
                    logger.warn("Failed to close DB session properly", e);
                }
            }
        }
    }

    public static String executeSelectQuery(Session session, String query, String... parameters) throws AdminTestException {
        final StringBuilder resultHolder = new StringBuilder();
        try {
            session.doWork(new Work() {
                @Override
                public void execute(Connection connection) throws SQLException {
                    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                        for (int i = 0; i < parameters.length; i++) {
                            pstmt.setString(i + 1, parameters[i]);
                        }
                        try (ResultSet resultSet = pstmt.executeQuery()) {
                            if (resultSet.next()) {
                                resultHolder.append(resultSet.getString(1));
                            }
                        }
                    }
                }
            });
        } catch (Exception e) {
            logger.error("Failed to execute query: " + query, e);
            throw new AdminTestException("Failed to execute query: " + query);
        }
        return resultHolder.toString();
    }
    
	public static String getCredentialDetails(String uin, TestCaseDTO testCaseDTO) {

		try {

			if (uin == null || uin.isEmpty()) {
				return buildBox(null, null, "UIN is null or empty");
			}

			String rid = null;
			String record;

			if (isOverrideEnabled()) {
				
				String endpoint = BaseTestCase.ApplnURI + "/idrepository/v1/identity/rid/" + uin;
				rid = JsonPrecondtion.getValueFromJson(
						RestClient.getRequestWithCookie(endpoint, MediaType.APPLICATION_JSON,
								MediaType.APPLICATION_JSON, GlobalConstants.AUTHORIZATION,
								new KernelAuthentication().getTokenByRole(testCaseDTO.getRole())).asString(),
						"response.rid");

				if (rid == null || rid.isEmpty()) {
					return buildBox(uin, null, "RID not found");
				}
				String requestId = rid;
				if (requestId == null || requestId.isEmpty()) {
					return buildBox(uin, null, "No requestId found");
				}
				record = getStatusFromCredentialTransactionTable(requestId);
			} else {
				int prefix = generateThreeDigitPrefix(uin, SALT_KEY_LENGTH);
				logger.info(prefix);
				record = getRequestIdFromIdRepoCredentialRequestStatusTable(String.valueOf(prefix));
			}

			if (record == null || record.isEmpty()) {
				return buildBox(uin, rid, null);
			}
			return buildBox(uin, rid, record);

		} catch (Exception e) {
			return "<b>Credential Details Failed:</b> " + e.getMessage();
		}
	}

	private static boolean isOverrideEnabled() {

		String value = BaseTestCase.getValueFromActuators(ConfigManager.getproperty("actuatorIdrepoDataEndpoint"),
				"bootstrapProperties-overrides",
				"mosip.idrepo.credential.request.enable.convention.based.id.idrepo.override");
		return value != null && Boolean.parseBoolean(value.trim());
	}

	private static int generateThreeDigitPrefix(String uin, int substrigLen) {
		Assert.isTrue(substrigLen > 0, "divisor should be positive integer");

		try {
			String idPlainHash = HMACUtils2.digestAsPlainText(uin.getBytes());
			int hexToDecimal = getSubstrinInt(idPlainHash, substrigLen, 16);
			String decimalStr = String.valueOf(hexToDecimal);
			return getSubstrinInt(decimalStr, substrigLen, 10);
		} catch (NoSuchAlgorithmException e) {
			throw new IdRepoAppUncheckedException(IdRepoErrorConstants.UNKNOWN_ERROR, e);
		}
	}
	
	private static int getSubstrinInt(String idvid, int substrigLen, int radix) {
		String hexSubstring = getSubstring(idvid, substrigLen);
		return Integer.parseInt(hexSubstring, radix);
	}
	
	private static String getSubstring(String string, int substrigLen) {
		int length = string.length();
		return length > substrigLen ? string.substring(length - substrigLen) : string;
	}

	// HTML Report box
	private static String buildBox(String uin, String rid, String status) {

		return "<div style='border:2px solid #e74c3c;padding:12px;" + "border-radius:8px;background:#fff5f5'>" +
				"<h3 style='color:#c0392b;'>Credential Details</h3>" +
				"<b>UIN :</b> " + safe(uin) + "<br>" + "<b>RID :</b> " + safe(rid) + "<br>"
				+ "<b>Credential Status :</b> " + safe(status) + "<br>" +
				"</div><br>";
	}

	private static String safe(String value) {
		return value == null ? "Not Found" : value;
	}
    
}