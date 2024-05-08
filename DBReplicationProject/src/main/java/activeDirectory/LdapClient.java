/**
 * 
 */
package activeDirectory;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.naming.Context;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.OperationNotSupportedException;
import javax.naming.ldap.LdapContext;

import org.apache.log4j.Logger;

import cripUtilityPackage.AES;
//import cripUtilityPackage.CripUtility;

import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;

class LdapClient {
	private static String userObjectClass = "(objectclass=user)";
	private static String groupObjectClass = "(objectclass=group)";
	static Logger log = Logger.getLogger(LdapClient.class.getName());

	static LdapContext GetLdapContext(String ldapURL, String adminUser, String ldapBindPassword, String trustStore,
			String trustStorePassword) throws Exception {
		LdapContext ctx = null;
		try {
			Hashtable<String, String> env = new Hashtable<String, String>();
			String principalName = adminUser;
			env.put(Context.PROVIDER_URL, ldapURL);
			env.put(Context.SECURITY_PRINCIPAL, principalName);
			env.put(Context.SECURITY_CREDENTIALS, AES.decrypt(ldapBindPassword));
			env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
			env.put(Context.SECURITY_AUTHENTICATION, "Simple");
			env.put("javax.net.ssl.trustStore", trustStore);
			env.put("javax.net.ssl.trustStorePassword", trustStorePassword);
			System.setProperty("javax.net.ssl.trustStore", trustStore);
			System.setProperty("javax.net.ssl.trustStorePassword", trustStorePassword);
			ctx = new InitialLdapContext(env, null);
		} catch (Exception ex) {
			log.error("Error in ldap Context: " + ex.fillInStackTrace());
			throw ex;
		}
		return ctx;
	}

	static Map<String, String> GetUserGroupsByUserName(String commonDN, String userName, LdapContext ctx)
			throws Exception {
		Map<String, String> userGroups = new HashMap<String, String>();
		try {
			NamingEnumeration<SearchResult> answer = ctx.search(commonDN,
					"(&" + userObjectClass + "(sAMAccountName=" + userName + "))",
					SearchQueries.GetUserGroupsByUserName());
			if (answer.hasMore()) {
				Attributes attrs = answer.next().getAttributes();
				userGroups = GetGroupsMap(attrs);
			} else {
				throw new Exception("User Name '" + userName + "' not found.");
			}
		} catch (Exception ex) {
			throw ex;
		} finally {
			ctx.close();
		}
		return userGroups;
	}

	static Map<String, String> GetAllUserGroups(String commonDN, LdapContext ctx) throws Exception {
		Map<String, String> allUserGroups = new HashMap<String, String>();
		try {
			NamingEnumeration<SearchResult> answer = ctx.search(commonDN, "(&" + groupObjectClass + ")",
					SearchQueries.GetAllUserGroups());
			while (answer.hasMore()) {
				Attributes attrs = answer.next().getAttributes();
				GetAllGroupsMap(attrs, allUserGroups);
			}
			ctx.close();
		} catch (Exception ex) {
			throw ex;
		} finally {
			ctx.close();
		}
		return allUserGroups;
	}


	static Boolean AddUserToGroup(String commonDN, LdapContext ctx, String groupName, String userName)
			throws Exception {
		Boolean userAdded = false;
		try {
			ModificationItem[] mods = new ModificationItem[1];
			Attribute mod0 = new BasicAttribute("member", GetUserDistinguishedName(commonDN, userName, ctx));
			mods[0] = new ModificationItem(DirContext.ADD_ATTRIBUTE, mod0);
			ctx.modifyAttributes(GetGroupDistinguishedName(commonDN, groupName, ctx), mods);
			userAdded = true;
		} catch (NameAlreadyBoundException nameEx) {
			if(log.isInfoEnabled())log.info(userName + " exists in " + groupName);
		} catch (NamingException ex) {
			log.error("Error: " + ex.fillInStackTrace());
			ex.printStackTrace();
		} finally {
			ctx.close();
		}
		return userAdded;
	}


	static Boolean RemoveUserFromGroup(String commonDN, LdapContext ctx, String groupName, String userName)
			throws Exception {
		Boolean userRemoved = false;
		try {
			ModificationItem[] mods = new ModificationItem[1];
			Attribute mod0 = new BasicAttribute("member", GetUserDistinguishedName(commonDN, userName, ctx));
			mods[0] = new ModificationItem(DirContext.REMOVE_ATTRIBUTE, mod0);
			ctx.modifyAttributes(GetGroupDistinguishedName(commonDN, groupName, ctx), mods);
			userRemoved = true;
		} catch (OperationNotSupportedException opEx) {
			if(log.isInfoEnabled())log.info(userName + " not exists in " + groupName);
		} catch (Exception e) {
			log.error("Error: " + e.fillInStackTrace());
		} finally {
			ctx.close();
		}
		return userRemoved;
	}

	/**
	 * Get User Distinguished Name
	 * 
	 * @throws Exception
	 */
	static String GetUserDistinguishedName(String commonDN, String userName, LdapContext ctx) throws Exception {
		String userDistinguishedName = "";
		try {
			NamingEnumeration<SearchResult> answer = ctx.search(commonDN,
					"(&" + userObjectClass + "(sAMAccountName=" + userName + "))",
					SearchQueries.GetDistinguishedName());
			if (answer.hasMore()) {
				Attributes attrs = answer.next().getAttributes();
				userDistinguishedName = GetUserNameValue(attrs);
			} else {
				if(log.isInfoEnabled())log.info("User Name '" + userName + "' not found.");
			}
		} catch (NameAlreadyBoundException ex) {
			log.error("User Name '" + userName + "'s is NameAlreadyBound.");
		} catch (Exception ex) {
			log.error("Error:" + ex.fillInStackTrace());
			ex.printStackTrace();
		}
		return userDistinguishedName;
	}

	/**
	 * Get Group Distinguished Name
	 * 
	 * @throws Exception
	 */
	private static String GetGroupDistinguishedName(String commonDN, String groupName, LdapContext ctx)
			throws Exception {
		String groupDistinguishedName = "";
		try {
			NamingEnumeration<SearchResult> answer = ctx.search(commonDN,
					"(&" + groupObjectClass + "(CN=" + groupName + "))", SearchQueries.GetDistinguishedName());
			if (answer.hasMore()) {
				Attributes attrs = answer.next().getAttributes();
				groupDistinguishedName = GetUserNameValue(attrs);
			} else {
				log.error("Group Name '" + groupName + "' not found.");
				throw new Exception("Group Name '" + groupName + "' not found.");
			}
		} catch (Exception ex) {
			log.error("Error:" + ex.fillInStackTrace());
			throw ex;
		}
		return groupDistinguishedName;
	}

	/**
	 * Get Single Value from attributes
	 * 
	 * @throws Exception
	 */
	private static String GetUserNameValue(Attributes attrs) throws Exception {
		String userName = "";
		if (attrs != null) {
			try {
				for (NamingEnumeration<?> ae = attrs.getAll(); ae.hasMore();) {
					Attribute attr = (Attribute) ae.next();
					for (NamingEnumeration<?> e = attr.getAll(); e.hasMore();) {
						userName = (String) e.next();
					}
				}
			} catch (Exception ex) {
				throw ex;
			}
		}
		return userName;
	}

	private static Map<String, String> GetGroupsMap(Attributes attrs) throws Exception {
		Map<String, String> userGroups = new HashMap<String, String>();
		if (attrs != null) {
			try {
				for (NamingEnumeration<?> ae = attrs.getAll(); ae.hasMore();) {
					Attribute attr = (Attribute) ae.next();
					for (NamingEnumeration<?> e = attr.getAll(); e.hasMore();) {
						String value = (String) e.next();
						userGroups.put((value.split(",")[0]).split("=")[1], value);
					}
				}
			} catch (Exception ex) {
				throw ex;
			}
		}
		return userGroups;
	}

	private static Map<String, String> GetAllGroupsMap(Attributes attrs, Map<String, String> userGroups)
			throws Exception {
		if (attrs != null) {
			try {
				for (NamingEnumeration<?> ae = attrs.getAll(); ae.hasMore();) {
					Attribute attr = (Attribute) ae.next();
					for (NamingEnumeration<?> e = attr.getAll(); e.hasMore();) {
						String value = (String) e.next();
						userGroups.put((value.split(",")[0]).split("=")[1], value);
					}
				}
			} catch (Exception ex) {
				throw ex;
			}
		}
		return userGroups;
	}
}
