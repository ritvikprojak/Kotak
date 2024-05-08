package activeDirectory;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.naming.ldap.LdapContext;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.log4j.Logger;

import dDReplication.MasterSlaveDBreplication;

public class UpmLdapEvents {

	static Logger log = Logger.getLogger(UpmLdapEvents.class.getName());
	static ResourceBundle rBProps = MasterSlaveDBreplication.rBProps;
	static String ldapURL = null;
	static String adminUser = null;
	static String ldapBindPassword = null;
	static String commonDN = null;
	static String trustStore = rBProps.getString("javax.net.ssl.trustStore");
	static String trustStorePassword = rBProps.getString("javax.net.ssl.trustStorePassword");
	String allowedGroup = rBProps.getString("allowedGroup");
	String allowedAdminGroup = rBProps.getString("allowedAdminGroup");

	String userLoggedIn = null;

	public void setLdap(String company) {
		ldapURL = rBProps.getString(company + ".application.ldap.ldapURL");
		adminUser = rBProps.getString(company + ".application.ldap.ldapBindId");
		ldapBindPassword = rBProps.getString(company + ".application.ldap.ldapBindPassword");
		commonDN = rBProps.getString(company + ".application.ldap.BaseDN");
	}

	public void AddUserToGroup(String company, String groupName, String userName) {
		try {
			setLdap(company);
			LdapContext ldapContext = LdapClient.GetLdapContext(ldapURL, adminUser, ldapBindPassword, trustStore,
					trustStorePassword);
			LdapClient.AddUserToGroup(commonDN, ldapContext, groupName, userName);
		} catch (Exception e) {

			log.error("Error: " + e.fillInStackTrace());
			e.printStackTrace();
			
		}
	}

	public void RemoveUserFromGroup(String company, String groupName, String userName) {
		try {
			setLdap(company);
			LdapContext ldapContext = LdapClient.GetLdapContext(ldapURL, adminUser, ldapBindPassword, trustStore,
					trustStorePassword);
			LdapClient.RemoveUserFromGroup(commonDN, ldapContext, groupName, userName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
