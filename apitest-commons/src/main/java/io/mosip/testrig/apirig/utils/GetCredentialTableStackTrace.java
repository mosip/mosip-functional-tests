package io.mosip.testrig.apirig.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.jdbc.Work;

import io.mosip.testrig.apirig.dbaccess.DBManager;

public class GetCredentialTableStackTrace extends DBManager {

    private static final Logger logger = Logger.getLogger(GetCredentialTableStackTrace.class);

    private static final String HOST = ConfigManager.getproperty("db-server");
    private static final String IDREPO_DB_URL = "jdbc:postgresql://" + HOST + ":" + ConfigManager.getproperty("db-port") + "/mosip_idrepo";
    private static final String CREDENTIAL_DB_URL = "jdbc:postgresql://" + HOST + ":" + ConfigManager.getproperty("db-port") + "/mosip_credential";
    private static final String DB_USER =  "postgres";
    private static final String DB_PASSWORD =  ConfigManager.getproperty("postgres-password");

    public static String getCredentialStatus(String uin) {
        return getRequestIdFromIdRepoCredentialRequestStatusTable(uin);
    }

    public static String getRequestIdFromIdRepoCredentialRequestStatusTable(String uin) {
        String query = "SELECT request_id FROM idrepo.credential_request_status "
                + "WHERE individual_id LIKE ? ORDER BY cr_dtimes DESC";
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
}