package io.mosip.kernel.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import io.mosip.admin.fw.util.*;
import io.mosip.testrunner.MosipTestRunner;

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



public class KeycloakUserManager {
	private static final Logger logger = Logger.getLogger(KeycloakUserManager.class);
	public static Properties propsKernel = getproperty(MosipTestRunner.getResourcePath() + "/"+"config/Kernel.properties");

	private static Keycloak getKeycloakInstance() {
		return KeycloakBuilder.builder().serverUrl(propsKernel.getProperty("keycloak.url")).realm(propsKernel.getProperty("keycloak.realm"))
				.grantType(OAuth2Constants.CLIENT_CREDENTIALS).clientId(propsKernel.getProperty("automation.clientid")).clientSecret(propsKernel.getProperty("automation.clientid.secrectkey"))
				.build();
	}

	public static Properties getproperty(String path) {
		Properties prop = new Properties();
		
		try {
			File file = new File(path);
			prop.load(new FileInputStream(file));
		} catch (IOException e) {
			logger.error("Exception " + e.getMessage());
		}
		return prop;
	}

	public static void createUsers() {
		List<String> needsToBeCreatedUsers = List.of(propsKernel.getProperty("users.create").split(","));
		Keycloak keycloakInstance = getKeycloakInstance();
		for (String needsToBeCreatedUser : needsToBeCreatedUsers) {
			UserRepresentation user = new UserRepresentation();
			user.setEnabled(true);
			user.setUsername(needsToBeCreatedUser);
			user.setFirstName(needsToBeCreatedUser);
			user.setLastName(needsToBeCreatedUser);
			user.setEmail("automation" + needsToBeCreatedUser + "@automationlabs.com");
			// Get realm
			RealmResource realmResource = keycloakInstance.realm(propsKernel.getProperty("keycloak.realm"));
			UsersResource usersRessource = realmResource.users();
			// Create user (requires manage-users role)
			Response response = usersRessource.create(user);
			System.out.println(response);
			System.out.printf("Repsonse: %s %s%n", response.getStatus(), response.getStatusInfo());
			System.out.println(response.getLocation());
			String userId = CreatedResponseUtil.getCreatedId(response);
			System.out.printf("User created with userId: %s%n", userId);

			// Define password credential
			CredentialRepresentation passwordCred = new CredentialRepresentation();
			passwordCred.setTemporary(false);
			passwordCred.setType(CredentialRepresentation.PASSWORD);
			passwordCred.setValue("mosip123");

			UserResource userResource = usersRessource.get(userId);

			// Set password credential
			userResource.resetPassword(passwordCred);

			// Getting all the roles
			List<RoleRepresentation> testerRealmRoleList = realmResource.roles().list();
			// Assign realm role tester to user
			userResource.roles().realmLevel() //
					.add((testerRealmRoleList));
		}
	}

	public static void removeUser() {
		List<String> needsToBeRemovedUsers = List.of(propsKernel.getProperty("users.create").split(","));
		Keycloak keycloakInstance = getKeycloakInstance();
		for (String needsToBeRemovedUser : needsToBeRemovedUsers) {
			RealmResource realmResource = keycloakInstance.realm(propsKernel.getProperty("keycloak.realm"));
			UsersResource usersRessource = realmResource.users();
			List<UserRepresentation> usersFromDB = usersRessource.search(needsToBeRemovedUser);
			UserResource userResource = usersRessource.get(usersFromDB.get(0).getId());
			userResource.remove();
			System.out.printf("User removed with name: %s%n", needsToBeRemovedUser);
		}
	}
}
