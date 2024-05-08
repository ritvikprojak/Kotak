/**
 * 
 */
package com.abc.kotak.ldap;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

/**
 * This Class use for all LDAP operations 
 * @author Dilan
 *
 */
class LdapClient {
	/**
	 * Common Domain Names
	 */
	private static String commonDN = "OU=fncm,DC=KGPUAT,DC=COM";
	/**
	 * User Object Class
	 */
	private static String userObjectClass = "(objectclass=user)";
	//private static String userObjectClass = "(&(samAccountName={0})(objectClass=user))";
	/**
	 * Group Object Class
	 */
	private static String groupObjectClass = "(objectclass=group)";
	//private static String groupObjectClass = "(&(samAccountName={0})(objectClass=group))";
	/**
	 * Get LDAP context to search and modify
	 * @throws Exception
	 */
	static LdapContext GetLdapContext(String providerUri, String AdminUser, String adminPwd, String adminDN) throws Exception{
		LdapContext ctx = null;
		try{
			Hashtable<String, String> env = new Hashtable<String, String>();
			env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
			env.put(Context.SECURITY_AUTHENTICATION, "Simple");
			env.put(Context.SECURITY_PRINCIPAL, "cn="+AdminUser+","+adminDN);
			//System.out.println("admin password is >>> "+CripUtils.decryptStr(adminPwd));
			env.put(Context.SECURITY_CREDENTIALS, adminPwd);
			env.put(Context.PROVIDER_URL, providerUri);
			env.put("javax.net.ssl.trustStore", "C:\\Program Files\\Java\\jdk1.8.0_221\\jre\\lib\\security\\cacerts"); 
			env.put("javax.net.ssl.trustStorePassword", "changeit");   
			//env.put(Context.REFERRAL, "follow");
//			System.out.println(env.toString());
			ctx = new InitialLdapContext(env, null);
			System.out.println("ctx obtained is "+ctx);
		}
		catch(Exception ex){
			throw ex;
		}
		return ctx;
	}
	/**
	 * Get All user belongs User Groups by User Name
	 * @throws Exception 
	 */
	static Map<String, String> GetUserGroupsByUserName(String userName, LdapContext ctx) throws Exception{
		Map<String, String> userGroups = new HashMap<String, String>();
		try {
			NamingEnumeration<SearchResult> answer = ctx.search(commonDN, "(&"+userObjectClass+"(sAMAccountName=" + userName+"))", SearchQueries.GetUserGroupsByUserName());
           //(&(objectclass=user)(sAMAccountName="fncmadmin))
			if (answer.hasMore()) {
                Attributes attrs = answer.next().getAttributes();
                userGroups = GetGroupsMap(attrs);
            } else {
            	throw new Exception("User Name '" +userName+"' not found.");
            }
		} catch (Exception ex) {
			throw ex;
		}
		finally
		{
			ctx.close();
		}
		return userGroups;
	}
	
	/**
	 * Get All user belongs User Groups by User Name
	 * @throws Exception 
	 * added by saimadan
	 */
	/*
	static Map<String, String> GetUserByUserName(String userName, LdapContext ctx) throws Exception{
		Map<String, String> userGroups = new HashMap<String, String>();
		try {
			NamingEnumeration<SearchResult> answer = ctx.search(commonDN, "(&"+userObjectClass+"(sAMAccountName=" + userName+"))", SearchQueries.GetUserGroupsByUserName());
            if (answer.hasMore()) {
                Attributes attrs = answer.next().getAttributes();
                userGroups = GetGroupsMap(attrs);
            } else {
            	throw new Exception("User Name '" +userName+"' not found.");
            }
		} catch (Exception ex) {
			throw ex;
		}
		finally
		{
			ctx.close();
		}
		return userGroups;
	}*/
	
	/**
	 * Get All User Groups
	 * @throws Exception 
	 */
	static Map<String, String> GetAllUserGroups(LdapContext ctx) throws Exception{
		Map<String, String> allUserGroups = new HashMap<String, String>();
		try {
			NamingEnumeration<SearchResult> answer = ctx.search(commonDN, "(&"+groupObjectClass+")", SearchQueries.GetAllUserGroups());
			while(answer.hasMore())
            {
            	Attributes attrs = answer.next().getAttributes();
            	GetAllGroupsMap(attrs, allUserGroups);
            	
            }
            ctx.close();
		} catch (Exception ex) {
			throw ex;
		} 
		finally
		{
			ctx.close();
		}
		return allUserGroups;
	}
	
	/**
	 * Get All User 
	 * @throws Exception
	 * added by saimadan 
	 */
	static Map<String, String> GetAllUsers(LdapContext ctx) throws Exception{
		Map<String, String> allUserGroups = new HashMap<String, String>();
		try {
			NamingEnumeration<SearchResult> answer = ctx.search(commonDN, "(&"+userObjectClass+")", SearchQueries.GetAllUserGroups());
			while(answer.hasMore())
            {
            	Attributes attrs = answer.next().getAttributes();
            	GetAllGroupsMap(attrs, allUserGroups);
            	
            }
            ctx.close();
		} catch (Exception ex) {
			throw ex;
		} 
		finally
		{
			ctx.close();
		}
		return allUserGroups;
	}
	
	/**
	 * Add User to User Group
	 * @throws Exception 
	 */
	static Boolean AddUserToGroup(LdapContext ctx, String groupName, String userName) throws Exception{
		Boolean userAdded = false;
		try {
			ModificationItem[] mods = new ModificationItem[1];
			Attribute mod0 = new BasicAttribute("member", GetUserDistinguishedName(userName, ctx));
			mods[0] = new ModificationItem(DirContext.ADD_ATTRIBUTE, mod0);	
			ctx.modifyAttributes(GetGroupDistinguishedName(groupName, ctx), mods);
			userAdded = true;
		} catch (NamingException ex) {
			throw ex;
		}
		finally
		{
			ctx.close();
		}
		return userAdded;
	}
	/**
	 * Remove User from User Group
	 * @throws Exception 
	 */
	static Boolean RemoveUserFromGroup(LdapContext ctx, String groupName, String userName) throws Exception{
		Boolean userRemoved = false;
		try {
			ModificationItem[] mods = new ModificationItem[1];
			Attribute mod0 = new BasicAttribute("member", GetUserDistinguishedName(userName, ctx));
			mods[0] = new ModificationItem(DirContext.REMOVE_ATTRIBUTE, mod0);	
			ctx.modifyAttributes(GetGroupDistinguishedName(groupName, ctx), mods);
			userRemoved = true;
		} catch (NamingException ex) {
			throw ex;
		}
		finally
		{
			ctx.close();
		}
		return userRemoved;
	}
	/**
	 * Get User Distinguished Name
	 * @throws Exception 
	 */
	static String GetUserDistinguishedName(String userName, LdapContext ctx) throws Exception{
		String userDistinguishedName = "";
		try {
			NamingEnumeration<SearchResult> answer = ctx.search(commonDN, "(&"+userObjectClass+"(sAMAccountName=" + userName+"))", SearchQueries.GetDistinguishedName());
			if (answer.hasMore()) {
                Attributes attrs = answer.next().getAttributes();
                userDistinguishedName = GetUserNameValue(attrs);
            } else {
            	throw new Exception("User Name '" +userName+"' not found.");
            }
		} catch (Exception ex) {
			throw ex;
		} 
		return userDistinguishedName;
	}
	
	
	
	
	/**
	 * Get Group Distinguished Name
	 * @throws Exception 
	 */
	private static String GetGroupDistinguishedName(String groupName, LdapContext ctx) throws Exception{
		String groupDistinguishedName = "";
		try {
			NamingEnumeration<SearchResult> answer = ctx.search(commonDN, "(&"+groupObjectClass+"(CN=" + groupName+"))", SearchQueries.GetDistinguishedName());
			if (answer.hasMore()) {
                Attributes attrs = answer.next().getAttributes();
                groupDistinguishedName = GetUserNameValue(attrs);
            } else {
            	throw new Exception("Group Name '" +groupName+"' not found.");
            }
		} catch (Exception ex) {
			throw ex;
		} 
		return groupDistinguishedName;
	}
	/**Get Single Value from attributes
	 * @throws Exception 
	 */
	private static String GetUserNameValue(Attributes attrs) throws Exception{
		String userName = "";
		if(attrs != null)
		{
			try {
				for(NamingEnumeration<?> ae = attrs.getAll(); ae.hasMore();)
				{
					Attribute attr = (Attribute) ae.next();					
					for(NamingEnumeration<?> e = attr.getAll(); e.hasMore();)
					{
						userName =  (String) e.next();
					}
				}
			} catch (Exception ex) {
				throw ex;
			}
		}
		return userName;
	} 
	/**Set all user groups to map
	 * @throws Exception 
	 */
	private static Map<String, String> GetGroupsMap(Attributes attrs) throws Exception
	{
		Map<String, String> userGroups = new HashMap<String, String>();
		if(attrs != null)
		{
			try {
				for(NamingEnumeration<?> ae = attrs.getAll(); ae.hasMore();)
				{
					Attribute attr = (Attribute) ae.next();
			
					for(NamingEnumeration<?> e = attr.getAll(); e.hasMore();)
					{
						String value = (String) e.next();
						System.out.println(" UserGroups are "+value);
						userGroups.put((value.split(",")[0]).split("=")[1], value);
					}
				}
			} catch (Exception ex) {
				throw ex;
			}
		}
		return userGroups;
	}
	/**Set all user groups to map
	 * @throws Exception 
	 */
	private static Map GetAllGroupsMap(Attributes attrs, Map<String, String> userGroups ) throws Exception
	{
		if(attrs != null)
		{
			try {
				for(NamingEnumeration<?> ae = attrs.getAll(); ae.hasMore();)
				{
					Attribute attr = (Attribute) ae.next();
			
					for(NamingEnumeration<?> e = attr.getAll(); e.hasMore();)
					{
						String value = (String) e.next();
						System.out.println(" Groups are "+value);
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
