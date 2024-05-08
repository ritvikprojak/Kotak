package com.abc.kotak.ldap;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.naming.ldap.LdapContext;

import org.springframework.stereotype.Service;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;


@Service
public class UpmLdapEvents {
	//private static Logger log =LoggerFactory.getLogger(UpmLdapEvents.class);
	//String providerUri = "ldap://172.20.8.43:389";
	
	//tring AdminUser = "fnp8admin";
	//String adminPwd = "CtaX#j7p8";
	
	//String AdminUser = "appmgr";
	//String adminDN = "DC=BANKOFCEYLON,DC=LOCAL";
	//String AdminUser = "upmuser1";
	//String adminDN = "OU=TEST,OU=DMS,DC=BANKOFCEYLON,DC=LOCAL";
	//String adminPwd = "boc@123";
	
	
	//static String AdminUser = "pf203860 PWC. Karunaratne";
	//String AdminUser = "PF161852";
	//String adminPwd = "pf161852";
	//String AdminUser = "PF161852";
	//String adminPwd = "pf161852";
	//String adminDN ="OU=Test,OU=DMS,DC=BANKOFCEYLON,DC=LOCAL";
	//String adminDN = "OU=Cluster Admins,OU=t4s,DC=BANKOFCEYLON,DC=LOCAL";
	//String adminDN ="OU=None IT Admins,DC=BANKOFCEYLON,DC=LOCAL";
	//String adminDN ="OU=Groups,OU=DMS,DC=BANKOFCEYLON,DC=LOCAL";
	//String adminDN ="OU=Cluster Admins,OU=t4s,DC=BANKOFCEYLON,DC=LOCAL";
	//String adminDN ="CN=Remote Desktop Users,CN=Builtin,DC=BANKOFCEYLON,DC=LOCAL";
	
	
	
	//String AdminUser = "PF111222";
	//String adminPwd = "pf111222";
	//String adminDN="OU=TEST,OU=DMS,DC=BANKOFCEYLON,DC=LOCAL";
	//String adminDN ="OU=Groups,OU=DMS,DC=BANKOFCEYLON,DC=LOCAL";
	//String adminDN ="OU=None IT Admins,DC=BANKOFCEYLON,DC=LOCAL";
	//String adminDN ="OU=Cluster Admins,OU=t4s,DC=BANKOFCEYLON,DC=LOCAL";
	//String adminDN="CN=Remote Desktop Users,CN=Builtin,DC=BANKOFCEYLON,DC=LOCAL"

	
	
	/*static String AdminUser = "CreditAssistance1";
	static String adminPwd = "boc@123";
	static String adminDN = "OU=TEST,OU=DMS,DC=BANKOFCEYLON,DC=LOCAL";*/
	//
	
	/*static ResourceBundle configMsgBundle = ResourceBundle.getBundle("config");
	static String providerUri = configMsgBundle.getString("providerUri");
	static String AdminUser = configMsgBundle.getString("AdminUser");
	static String adminPwd = configMsgBundle.getString("adminPwd");
	static String adminDN = configMsgBundle.getString("adminDN");
	String allowedGroup = configMsgBundle.getString("allowedGroup");
	String allowedAdminGroup = configMsgBundle.getString("allowedAdminGroup");
	String userLoggedIn = null;*/
	
	static ResourceBundle configMsgBundle = ResourceBundle.getBundle("application");
	static String providerUri = configMsgBundle.getString("application.ldap.ldapURL");
	static String AdminUser = configMsgBundle.getString("application.rest.adminUser");
	static String adminPwd = configMsgBundle.getString("application.ldap.ldapBindPassword");
	static String adminDN = configMsgBundle.getString("application.ldap.ldapBindDN.baseDN");
	String allowedGroup = configMsgBundle.getString("allowedGroup");
	String allowedAdminGroup = configMsgBundle.getString("allowedAdminGroup");
	String userLoggedIn = null;
	
	
	public String GetUserGroupsByUserName(String userName){
		try {
			LdapContext ldapContext = LdapClient.GetLdapContext(providerUri, AdminUser, adminPwd, adminDN);
			Map<String,String> userMap = LdapClient.GetUserGroupsByUserName(userName, ldapContext);
			Set keySet = userMap.keySet();
			Iterator iter = keySet.iterator();
			while(iter.hasNext())
			{
				String key = (String)iter.next();
				String value = userMap.get(key);
				//log.debug("key is "+key+" value is "+value);
				if(allowedGroup.contains(key))
				{
					userLoggedIn="Manager";
					//log.debug("Group Logged in is "+allowedGroup);
				}
				if(allowedAdminGroup.contains(key))
				{
					userLoggedIn="Admin";
					//log.debug("Group Logged in is "+allowedAdminGroup);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return userLoggedIn;
	}
	public void AddUserToGroup(String groupName, String userName){
		try {
			LdapContext ldapContext = LdapClient.GetLdapContext(providerUri, AdminUser, adminPwd, adminDN);
			LdapClient.AddUserToGroup(ldapContext, groupName, userName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void RemoveUserFromGroup(String groupName, String userName){
		try {
			LdapContext ldapContext = LdapClient.GetLdapContext(providerUri, AdminUser, adminPwd, adminDN);
			System.out.println(providerUri+" "+ AdminUser+" "+ adminPwd+" "+ adminDN);
			
			LdapClient.RemoveUserFromGroup(ldapContext, groupName, userName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public Map<String, String> GetAllUserGroups(){
		Map<String, String> allGroups = null;
		try {
			LdapContext ldapContext = LdapClient.GetLdapContext(providerUri, AdminUser, adminPwd, adminDN);
			allGroups = LdapClient.GetAllUserGroups(ldapContext);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return allGroups;
	}
	
	public static Map<String, String> GetAllUsers(){
		Map<String, String> allGroups = null;
		try {
			LdapContext ldapContext = LdapClient.GetLdapContext(providerUri, AdminUser, adminPwd, adminDN);
			allGroups = LdapClient.GetAllUsers(ldapContext);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return allGroups;
	}
	
	public void updateRole(String existingRole, String newRole,String userName)
	{
		existingRole = existingRole+"_Test";
		//existingRole = existingRole;
		//log.debug("existingRole is "+existingRole);
		newRole = newRole+"_Test";
		//newRole = newRole;
		//log.debug("newRole is "+newRole);
		/*Map<String, String> allGroups = GetAllUserGroups();
		Set keySet = allGroups.keySet();
		Iterator iter = keySet.iterator();
		String existingRoleGroup=null,newRoleGroup=null;
		while(iter.hasNext())
		{
			String key = (String) iter.next();
			if(key.equalsIgnoreCase(existingRole))
			{
				existingRoleGroup = allGroups.get(key);
				log.debug(" existingRoleGroup is "+existingRoleGroup);
			}
			else if(key.equalsIgnoreCase(newRole))
			{
				newRoleGroup = allGroups.get(key);
				log.debug(" newRoleGroup is "+newRoleGroup);
			}
			
			
		}
		if(null!=existingRoleGroup && null!=newRoleGroup)*/
		{
			RemoveUserFromGroup(existingRole, userName);
			AddUserToGroup(newRole, userName);
		}
		
	}
	
	
	public String userAuthentication(String userName,String password) throws Exception
	{
		boolean flag = false,allowedFlag =false;
		String loggedInUser = null;
		String responseMessage = null;
		String userDistinguishedName = getUserDistinguishedName(userName);
		try 
		{
			if(null!=userDistinguishedName)
			{
				
				int comaIndexOf = userDistinguishedName.indexOf(",");
				if(comaIndexOf!=-1)
				{
					String adUserName = userDistinguishedName.substring(0,comaIndexOf);
					//log.debug("UserDN is "+adUserName);
					adUserName = adUserName.split("=")[1];
					//log.debug("adUserName is "+adUserName);
					userDistinguishedName = userDistinguishedName.substring(++comaIndexOf);
					//log.debug("userDistinguishedName is "+userDistinguishedName);
					LdapContext ldapContext = LdapClient.GetLdapContext(providerUri, adUserName, password, userDistinguishedName);
					//log.debug("ldapContext obtained "+ldapContext);
					responseMessage = "User Logged in successfully";
					flag = true;
					userLoggedIn = GetUserGroupsByUserName(userName);
					if(!allowedFlag)
						flag = false;
				}
			}			
			else
			{
				responseMessage = "User doesn't exists, please check username and password";
				flag = false;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			
			/*throw e;*/
		}
		return userLoggedIn;
	}
	
	public String getUserDistinguishedName(String userName)
	{
		String userDistinguishedName = null;
		LdapContext ldapContext;
		try {
			ldapContext = LdapClient.GetLdapContext(providerUri, AdminUser, adminPwd, adminDN);
			userDistinguishedName = LdapClient.GetUserDistinguishedName(userName,ldapContext);
			//log.debug("userDistinguishedName is "+userDistinguishedName);
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return userDistinguishedName;
	}
	
	public String findUserByUserName(String userName)
	{
		String userDistinguishedName = null;
		LdapContext ldapContext;
		try {
			ldapContext = LdapClient.GetLdapContext(providerUri, AdminUser, adminPwd, adminDN);
			userDistinguishedName = LdapClient.GetUserDistinguishedName(userName,ldapContext);
			//log.debug("userDistinguishedName is "+userDistinguishedName);
			System.out.println("userDistinguishedName is "+userDistinguishedName);
			userDistinguishedName  = userDistinguishedName.split(",")[0].split("=")[1];
			//log.debug("Name is "+userDistinguishedName);
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return userDistinguishedName;
	}
//	public static void main(String a[]) throws Exception
//	{
//		UpmLdapEvents ldapEvents = new UpmLdapEvents();
//		//ldapEvents.findUserByUserName("PF161852");
//		//ldapEvents.findUserByUserName("PF164418");
//		//ldapEvents.findUserByUserName("psmall");
//		//ldapEvents.GetUserGroupsByUserName("aniket");
//		//ldapEvents.userAuthentication("p8admin","Filenet8");
//		//GetAllUsers();
//		//ldapEvents.GetAllUserGroups();
//		//ldapEvents.RemoveUserFromGroup("UPMadmin", "PF161852");
//		ldapEvents.AddUserToGroup("FN_HR_DOWNLOAD","capturer_1");
//		//ldapEvents.GetUserGroupsByUserName("p8admin");
//		//ldapEvents.AddUserToGroup("AreaManager","PF161852");
//		//GetUserGroupsByUserName("pf203860");
//		//ldapEvents.GetUserGroupsByUserName("PF161852");
//		
//		
//		try {
//			//ldapEvents.userAuthentication("rlcmanager1","boc@123");
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
}
