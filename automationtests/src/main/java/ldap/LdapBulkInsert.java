/*package io.mosip.ldap;

import java.util.ArrayList;
import java.util.List;

import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.ldap.LdapName;

import org.apache.directory.api.ldap.model.constants.LdapSecurityConstants;
import org.apache.directory.api.ldap.model.exception.LdapInvalidDnException;
import org.apache.directory.api.ldap.model.password.PasswordUtil;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.pool.factory.PoolingContextSource;

*//**
 * Hello world!
 *
 *//*
public class LdapBulkInsert {
	private static LdapTemplate ldapTemplate;
	private static String url = "ldap://13.71.126.139:10389";
	private static String user = "uid=admin,ou=system";
	private static String password = "secret";

	private static PoolingContextSource getContextSource(String url, String user, String password) {
		LdapContextSource contextSource = new LdapContextSource();
		contextSource.setUrl(url);
		contextSource.setUserDn(user);
		contextSource.setPassword(password);
		contextSource.afterPropertiesSet();
		PoolingContextSource pooledContextSource = new PoolingContextSource();
		pooledContextSource.setContextSource(contextSource);

		return pooledContextSource;
	}

	private static Name createUserName(String userName) throws LdapInvalidDnException, InvalidNameException {
		return new LdapName("uid=" + userName + ",ou=people,c=morocco");
	}

	private static Name createRoleName(String role) throws LdapInvalidDnException, InvalidNameException {
		return new LdapName("cn=" + role + ",ou=roles,c=morocco");
	}

	public static void main(String[] args) throws LdapInvalidDnException, InvalidNameException {
		ldapTemplate = new LdapTemplate(getContextSource(url, user, password));
		
		for (int i = 9000; i <= 9001; i++) {
			User user = new User();
			user.setCn("pt_user_admin"+i);
			user.setSn("pt_user_admin"+i);
			user.setFirstName("pt_user_admin"+i);
			user.setLastName("pt_user_admin"+i);
			user.setMobile("7891029312");
			user.setMail("pt_user_admin"+i+"@mindtree.com");
			user.setPassword("mosip");
			user.setUid("pt_user_admin"+i);
			user.setIsActive("true");
			user.setRid("27841452330002620190527095023");
			user.setGenderCode("MLE");
			user.setDob("13-02-1996");
			Name userNameLdap = createUserName(user.getUid());
			//Name userNameLdap = ldapTemplate.find("uid=" + "pt_user_admin"+i + ",ou=people,c=morocco", String.class);
			ldapTemplate.bind(userNameLdap, null, buildUserAttributes(user));
			ModificationItem[] mods = new ModificationItem[1];
			mods[0] = new ModificationItem(DirContext.ADD_ATTRIBUTE,
					new BasicAttribute(LdapConstants.ROLE_OCCUPANT, userNameLdap.toString()));
			ldapTemplate.modifyAttributes(createRoleName("REGISTRATION_PROCESSOR"), mods);
			ldapTemplate.modifyAttributes(createRoleName("ID_AUTHENTICATION"), mods);
			ldapTemplate.modifyAttributes(createRoleName("REGISTRATION_ADMIN"), mods);
			ldapTemplate.modifyAttributes(createRoleName("REGISTRATION_SUPERVISOR"), mods);
			ldapTemplate.modifyAttributes(createRoleName("REGISTRATION_OFFICER"), mods);
			System.out.println("user inserted "+user.getUid());
		}
	}

	private static Attributes buildUserAttributes(User user) {
		List<Attribute> attributes = new ArrayList<>();
		attributes.add(new BasicAttribute(LdapConstants.CN, user.getUid()));
		attributes.add(new BasicAttribute(LdapConstants.SN, user.getUid()));
		attributes.add(new BasicAttribute(LdapConstants.MAIL, user.getMail()));
		attributes.add(new BasicAttribute(LdapConstants.MOBILE, user.getMobile()));
		attributes.add(new BasicAttribute(LdapConstants.DOB, user.getDob()));
		attributes.add(new BasicAttribute(LdapConstants.FIRST_NAME, user.getFirstName()));
		attributes.add(new BasicAttribute(LdapConstants.LAST_NAME, user.getLastName()));
		attributes.add(new BasicAttribute(LdapConstants.GENDER_CODE, user.getGenderCode()));
		attributes.add(new BasicAttribute(LdapConstants.IS_ACTIVE, LdapConstants.TRUE));
		byte[] newUserPassword = PasswordUtil.createStoragePassword(user.getPassword().getBytes(),
				LdapSecurityConstants.HASH_METHOD_SSHA256);
		attributes.add(new BasicAttribute(LdapConstants.USER_PASSWORD, newUserPassword));
		Attribute oc = new BasicAttribute(LdapConstants.OBJECT_CLASS);
		oc.add(LdapConstants.INET_ORG_PERSON);
		oc.add(LdapConstants.ORGANIZATIONAL_PERSON);
		oc.add(LdapConstants.PERSON);
		oc.add(LdapConstants.TOP);
		oc.add(LdapConstants.USER_DETAILS);
		attributes.add(oc);

		BasicAttributes entry = new BasicAttributes();
		attributes.parallelStream().forEach(entry::put);
		return entry;
	}
}
*/