package io.mosip.testrig.apirig.utils;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import io.mosip.testrig.apirig.testrunner.BaseTestCase;


public class KeycloakUserManager {
	
	private static final Logger logger = Logger.getLogger(KeycloakUserManager.class);

	public static Properties propsKernel = getproperty(BaseTestCase.getGlobalResourcePath() + "/"+"config/Kernel.properties");
	public static Keycloak key = null;
	
	public static void closeKeycloakInstance() {
		if (key != null) {
			key.close();
			key = null;
		}
	}

	private static Keycloak getKeycloakInstance() {
		if (key != null)
			return key;
			try {
				String automationClientId = BaseTestCase.isTargetEnvLTS() ? ConfigManager.getAutomationClientId()
						: ConfigManager.getPmsClientId();
				key = KeycloakBuilder.builder().serverUrl(ConfigManager.getIAMUrl())
						.realm(ConfigManager.getIAMRealmId()).grantType(OAuth2Constants.CLIENT_CREDENTIALS)
						.clientId(automationClientId).clientSecret(ConfigManager.getAutomationClientSecret()).build();
				logger.info(ConfigManager.getIAMUrl());
				logger.info(key.toString() + key.realms());
			} catch (Exception e) {
				throw e;
			}
		return key;
	}

	public static Properties getproperty(String path) {
		Properties prop = new Properties();	
		FileInputStream inputStream = null;
		try {
			File file = new File(path);
			inputStream = new FileInputStream(file);
			prop.load(inputStream);
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e.getMessage());
		}finally {
			AdminTestUtil.closeInputStream(inputStream);
		}
		return prop;
	}

	public static void createUsers() {
		
		List<String> needsToBeCreatedUsers = List.of(ConfigManager.getIAMUsersToCreate().split(","));
		Keycloak keycloakInstance = getKeycloakInstance();
		for (String needsToBeCreatedUser : needsToBeCreatedUsers) {
			UserRepresentation user = new UserRepresentation();
			String moduleSpecificUser = null;
			if (needsToBeCreatedUser.equals("globaladmin")) {
				moduleSpecificUser = needsToBeCreatedUser;
			}
			else if(needsToBeCreatedUser.equals("masterdata-220005")){
				moduleSpecificUser = needsToBeCreatedUser;
				
			}
						
			else {
				moduleSpecificUser = BaseTestCase.currentModule +"-"+ needsToBeCreatedUser;
			}
			
			logger.info(moduleSpecificUser);
			user.setEnabled(true);
			user.setUsername(moduleSpecificUser);
			user.setFirstName(moduleSpecificUser);
			user.setLastName(moduleSpecificUser);
			user.setEmail(GlobalConstants.AUTOMATION + moduleSpecificUser + GlobalConstants.AUTOMATIONLABS);
			RealmResource realmResource = keycloakInstance.realm(ConfigManager.getIAMRealmId());
			UsersResource usersRessource = realmResource.users();
			Response response = null;
				response = usersRessource.create(user);
 				logger.info(response);
 				logger.info(String.format(GlobalConstants.REPSONSE, response.getStatus(), response.getStatusInfo()));
			if (response.getStatus()==409) {
				continue;
			}
			logger.info(response.getLocation());
			String userId = CreatedResponseUtil.getCreatedId(response);
			logger.info(String.format(GlobalConstants.USERCREATEDWITHUSERID, userId));

			CredentialRepresentation passwordCred = new CredentialRepresentation();
			
			passwordCred.setTemporary(false);
			passwordCred.setType(CredentialRepresentation.PASSWORD);
			
			passwordCred.setValue(GlobalConstants.MOSIP123);

			UserResource userResource = usersRessource.get(userId);

			userResource.resetPassword(passwordCred);

			List<RoleRepresentation> allRoles = realmResource.roles().list();
			List<RoleRepresentation> availableRoles = new ArrayList<>();
			List<String> toBeAssignedRoles = List.of(ConfigManager.getRolesForUser(needsToBeCreatedUser).split(","));
			for(String role : toBeAssignedRoles) {
				if(allRoles.stream().anyMatch((r->r.getName().equalsIgnoreCase(role)))){
					if (allRoles.stream().filter(r->r.getName().equals(role)).findFirst().isPresent())
							availableRoles.add(allRoles.stream().filter(r->r.getName().equals(role)).findFirst().get());
					else
						logger.info(String.format(GlobalConstants.ROLENOTFOUNDINKEYCLOAK, role));
				}else {
					logger.info(String.format(GlobalConstants.ROLENOTFOUNDINKEYCLOAK, role));
				}
			}
			userResource.roles().realmLevel() //
					.add((availableRoles.isEmpty() ? allRoles : availableRoles));
		}
	}

	
	public static void createKeyCloakUsers(String partnerId,String emailId, String userRole ) {
		List<String> needsToBeCreatedUsers = List.of(partnerId);
		Keycloak keycloakInstance = getKeycloakInstance();
		for (String needsToBeCreatedUser : needsToBeCreatedUsers) {
			UserRepresentation user = new UserRepresentation();
			user.setEnabled(true);
			user.setUsername(needsToBeCreatedUser);
			user.setFirstName(needsToBeCreatedUser);
			user.setLastName("");
			user.setEmail(emailId);
			RealmResource realmResource = keycloakInstance.realm(ConfigManager.getIAMRealmId());
			UsersResource usersRessource = realmResource.users();
			Response response = usersRessource.create(user);
			logger.info(response);
			logger.info(String.format(GlobalConstants.REPSONSE, response.getStatus(), response.getStatusInfo()));
			logger.info(response.getLocation());
			String userId = "";
			if (!response.getStatusInfo().equals(Response.Status.CONFLICT)){
				userId = CreatedResponseUtil.getCreatedId(response);
			}
			else {
				userId = getKeycloakUserID(needsToBeCreatedUser);
			}
			logger.info(String.format(GlobalConstants.USERCREATEDWITHUSERID, userId));

			CredentialRepresentation passwordCred = new CredentialRepresentation();
			
			passwordCred.setTemporary(false);
			passwordCred.setType(CredentialRepresentation.PASSWORD);
			
			passwordCred.setValue(GlobalConstants.MOSIP123);

			UserResource userResource = usersRessource.get(userId);

			userResource.resetPassword(passwordCred);

			List<RoleRepresentation> allRoles = realmResource.roles().list();
			List<RoleRepresentation> availableRoles = new ArrayList<>();
			List<String> toBeAssignedRoles = List.of(userRole);
			for(String role : toBeAssignedRoles) {
				if(allRoles.stream().anyMatch((r->r.getName().equalsIgnoreCase(role)))){
					if (allRoles.stream().filter(r->r.getName().equals(role)).findFirst().isPresent())
						availableRoles.add(allRoles.stream().filter(r->r.getName().equals(role)).findFirst().get());
				else
					logger.info(String.format(GlobalConstants.ROLENOTFOUNDINKEYCLOAK, role));
				}else {
					logger.info(String.format(GlobalConstants.ROLENOTFOUNDINKEYCLOAK, role));
				}
			}
			userResource.roles().realmLevel() //
					.add((availableRoles.isEmpty() ? allRoles : availableRoles));
		}
	}
	
	public static String getKeycloakUserID(String userName) {
		Keycloak keycloakInstance = getKeycloakInstance();
		RealmResource realmResource = keycloakInstance.realm(ConfigManager.getIAMRealmId());
		UsersResource usersRessource = realmResource.users();

		List<UserRepresentation> usersFromDB = usersRessource.search(userName);
		if (!usersFromDB.isEmpty()) {
			return usersFromDB.get(0).getId();
		} else {
			return "";
		}

	}
	
	public static void removeKeyCloakUser(String partnerId) {
		List<String> needsToBeRemovedUsers = List.of(partnerId);
		Keycloak keycloakInstance = getKeycloakInstance();
		for (String needsToBeRemovedUser : needsToBeRemovedUsers) {
			RealmResource realmResource = keycloakInstance.realm(ConfigManager.getIAMRealmId());
			UsersResource usersRessource = realmResource.users();

			List<UserRepresentation> usersFromDB = usersRessource.search(needsToBeRemovedUser);
			if (!usersFromDB.isEmpty()) {
				UserResource userResource = usersRessource.get(usersFromDB.get(0).getId());
				userResource.remove();
				logger.info(String.format(GlobalConstants.USERREMOVEDWITHNAME, needsToBeRemovedUser));
			} else {
				logger.info(String.format(GlobalConstants.USERNOTFOUNDWITHNAME, needsToBeRemovedUser));
			}

		}
	}
	
	public static void removeUser() {
		List<String> needsToBeRemovedUsers = List.of(ConfigManager.getIAMUsersToCreate().split(","));
		Keycloak keycloakInstance = getKeycloakInstance();
		for (String needsToBeRemovedUser : needsToBeRemovedUsers) {
			String moduleSpecificUserToBeRemoved = BaseTestCase.currentModule +"-"+ needsToBeRemovedUser;
			RealmResource realmResource = keycloakInstance.realm(ConfigManager.getIAMRealmId());
			UsersResource usersRessource = realmResource.users();

			List<UserRepresentation> usersFromDB = usersRessource.search(moduleSpecificUserToBeRemoved);
			if (!usersFromDB.isEmpty()) {
				UserResource userResource = usersRessource.get(usersFromDB.get(0).getId());
				userResource.remove();
				logger.info(String.format(GlobalConstants.USERREMOVEDWITHNAME, moduleSpecificUserToBeRemoved));
			} else {
				logger.info(String.format(GlobalConstants.USERNOTFOUNDWITHNAME, moduleSpecificUserToBeRemoved));
			}

		}
	}
	public static void removeVidUser() {
		List<String> needsToBeRemovedUsers = List.of(BaseTestCase.currentModule +"-"+propsKernel.getProperty("new_Resident_User"));
		Keycloak keycloakInstance = getKeycloakInstance();
		for (String needsToBeRemovedUser : needsToBeRemovedUsers) {
			RealmResource realmResource = keycloakInstance.realm(ConfigManager.getIAMRealmId());
			UsersResource usersRessource = realmResource.users();

			List<UserRepresentation> usersFromDB = usersRessource.search(needsToBeRemovedUser);
			if (!usersFromDB.isEmpty()) {
				UserResource userResource = usersRessource.get(usersFromDB.get(0).getId());
				userResource.remove();
				logger.info(String.format(GlobalConstants.USERREMOVEDWITHNAME, needsToBeRemovedUser));
			} else {
				logger.info(String.format(GlobalConstants.USERNOTFOUNDWITHNAME, needsToBeRemovedUser));
			}

		}
	}

	public static void createUsers(String userid, String pwd, String rolenum, Map<String, List<String>> map) {
		Keycloak keycloakInstance = getKeycloakInstance();
		UserRepresentation user = new UserRepresentation();
		user.setEnabled(true);
		user.setUsername(userid);
		user.setFirstName(userid);
		user.setLastName(userid);
		user.setEmail(GlobalConstants.AUTOMATION + userid + GlobalConstants.AUTOMATIONLABS);
		if (map != null)
			user.setAttributes(map);
		RealmResource realmResource = null;

		realmResource = keycloakInstance.realm(ConfigManager.getIAMRealmId());

		UsersResource usersRessource = realmResource.users();

		try (Response response = usersRessource.create(user)) {

			if (response.getStatus() == 409) {

			} else {

				logger.info(response);
				logger.info(String.format(GlobalConstants.REPSONSE, response.getStatus(), response.getStatusInfo()));
				logger.info(response.getLocation());
				String userId = CreatedResponseUtil.getCreatedId(response);
				logger.info(String.format(GlobalConstants.USERCREATEDWITHUSERID, userId));

				CredentialRepresentation passwordCred = new CredentialRepresentation();

				passwordCred.setTemporary(false);
				passwordCred.setType(CredentialRepresentation.PASSWORD);

				passwordCred.setValue(pwd);

				UserResource userResource = usersRessource.get(userId);
				userResource.resetPassword(passwordCred);

				List<RoleRepresentation> allRoles = realmResource.roles().list();
				List<RoleRepresentation> availableRoles = new ArrayList<>();
				List<String> toBeAssignedRoles = List.of(ConfigManager.getRolesForUser().split(","));
				for (String role : toBeAssignedRoles) {
					if (allRoles.stream().anyMatch((r -> r.getName().equalsIgnoreCase(role)))) {
						if (allRoles.stream().filter(r->r.getName().equals(role)).findFirst().isPresent())
							availableRoles.add(allRoles.stream().filter(r->r.getName().equals(role)).findFirst().get());
					else
						logger.info(String.format(GlobalConstants.ROLENOTFOUNDINKEYCLOAK, role));
					} else {
						logger.info(String.format(GlobalConstants.ROLENOTFOUNDINKEYCLOAK, role));
					}
				}
				userResource.roles().realmLevel() //
						.add((availableRoles.isEmpty() ? allRoles : availableRoles));

			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

	}
	
	public static void createUsersWithArg(String userid,String pwd, String rolenum) {
		Keycloak keycloakInstance = getKeycloakInstance();
			UserRepresentation user = new UserRepresentation(); 
			user.setEnabled(true);
			user.setUsername(userid);
			user.setFirstName(userid);
			user.setLastName(userid);
			user.setEmail(GlobalConstants.AUTOMATION + userid + GlobalConstants.AUTOMATIONLABS);
			RealmResource realmResource=null;
			 realmResource = keycloakInstance.realm(propsKernel.getProperty("keycloak.realm"));

			UsersResource usersRessource = realmResource.users();
			Response response = usersRessource.create(user);
			logger.info(response);
			logger.info(String.format(GlobalConstants.REPSONSE, response.getStatus(), response.getStatusInfo()));
			logger.info(response.getLocation());
			String userId = CreatedResponseUtil.getCreatedId(response);
			logger.info(String.format(GlobalConstants.USERCREATEDWITHUSERID, userId));

			CredentialRepresentation passwordCred = new CredentialRepresentation();
			
			passwordCred.setTemporary(false);
			passwordCred.setType(CredentialRepresentation.PASSWORD);
			
			passwordCred.setValue(pwd);

			UserResource userResource = usersRessource.get(userId);
			userResource.resetPassword(passwordCred);

			List<RoleRepresentation> allRoles = realmResource.roles().list();
			List<RoleRepresentation> availableRoles = new ArrayList<>();
			List<String> toBeAssignedRoles = List.of(propsKernel.getProperty(rolenum).split(","));
			for(String role : toBeAssignedRoles) {
				if(allRoles.stream().anyMatch((r->r.getName().equalsIgnoreCase(role)))){
					if (allRoles.stream().filter(r->r.getName().equals(role)).findFirst().isPresent())
						availableRoles.add(allRoles.stream().filter(r->r.getName().equals(role)).findFirst().get());
				else
					logger.info(String.format(GlobalConstants.ROLENOTFOUNDINKEYCLOAK, role));
				}else {
					logger.info(String.format(GlobalConstants.ROLENOTFOUNDINKEYCLOAK, role));
				}
			}
			userResource.roles().realmLevel() 
					.add((availableRoles.isEmpty() ? allRoles : availableRoles));
		
	}
	public static void createVidUsers(String userid,String pwd, String rolenum,Map<String, List<String>> map) {
		Keycloak keycloakInstance = getKeycloakInstance();
			UserRepresentation user = new UserRepresentation();
			
			String moduleSpecificUser = null;
			moduleSpecificUser = BaseTestCase.currentModule +"-"+userid;
			
			user.setEnabled(true);
			user.setUsername(moduleSpecificUser);
			user.setFirstName(moduleSpecificUser);
			user.setLastName(moduleSpecificUser);
			user.setEmail(GlobalConstants.AUTOMATION + moduleSpecificUser + GlobalConstants.AUTOMATIONLABS);
			if(map!=null)
		    user.setAttributes(map);
			RealmResource realmResource=null;
			 realmResource = keycloakInstance.realm(ConfigManager.getIAMRealmId());
			UsersResource usersRessource = realmResource.users();
			Response response = usersRessource.create(user);
			logger.info(response);
			logger.info(String.format(GlobalConstants.REPSONSE, response.getStatus(), response.getStatusInfo()));
			
			
			logger.info(response.getLocation());
			String userId = CreatedResponseUtil.getCreatedId(response);
			logger.info(String.format(GlobalConstants.USERCREATEDWITHUSERID, userId));

			CredentialRepresentation passwordCred = new CredentialRepresentation();
			
			passwordCred.setTemporary(false);
			passwordCred.setType(CredentialRepresentation.PASSWORD);
			
			passwordCred.setValue(GlobalConstants.MOSIP123);

			UserResource userResource = usersRessource.get(userId);
			userResource.resetPassword(passwordCred);

			List<RoleRepresentation> allRoles = realmResource.roles().list();
			List<RoleRepresentation> availableRoles = new ArrayList<>();
			List<String> toBeAssignedRoles = List.of(propsKernel.getProperty("roles."+userid).split(","));
			for(String role : toBeAssignedRoles) {
				if(allRoles.stream().anyMatch((r->r.getName().equalsIgnoreCase(role)))){
					if (allRoles.stream().filter(r->r.getName().equals(role)).findFirst().isPresent())
						availableRoles.add(allRoles.stream().filter(r->r.getName().equals(role)).findFirst().get());
				else
					logger.info(String.format(GlobalConstants.ROLENOTFOUNDINKEYCLOAK, role));
				}else {
					logger.info(String.format(GlobalConstants.ROLENOTFOUNDINKEYCLOAK, role));
				}
			}
			userResource.roles().realmLevel() //
					.add((availableRoles.isEmpty() ? allRoles : availableRoles));
		
	}
	public static void removeUser(String user) {
		Keycloak keycloakInstance = getKeycloakInstance();
			RealmResource realmResource = keycloakInstance.realm(ConfigManager.getIAMRealmId());
			UsersResource usersRessource = realmResource.users();
try {
			List<UserRepresentation> usersFromDB = usersRessource.search(user);
			if (!usersFromDB.isEmpty()) {
				UserResource userResource = usersRessource.get(usersFromDB.get(0).getId());
				userResource.remove();
				logger.info(String.format(GlobalConstants.USERREMOVEDWITHNAME, user));
			} else {
				logger.info(String.format(GlobalConstants.USERNOTFOUNDWITHNAME, user));
			}

}
catch(Exception e)
{
	logger.error(e.getMessage());
	
}
			
		
	}

}
