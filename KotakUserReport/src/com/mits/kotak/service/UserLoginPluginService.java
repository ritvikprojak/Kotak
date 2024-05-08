package com.mits.kotak.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import javax.naming.InitialContext;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import javax.naming.InitialContext;
import javax.naming.PartialResultException;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;
import com.ibm.json.java.JSON;
import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;

import com.filenet.api.core.Factory;
import com.filenet.api.security.User;
import com.ibm.ecm.extension.PluginLogger;
import com.ibm.ecm.extension.PluginService;
import com.ibm.ecm.extension.PluginServiceCallbacks;
import com.ibm.json.java.JSON;
import com.ibm.json.java.JSONArray;
import com.mits.kotak.constants.UserReportConstants;
import com.mits.kotak.util.ConfigLoader;


public class UserLoginPluginService extends PluginService {

	ResourceBundle resourceBundle = ConfigLoader.getBundle();
	 static String[] attrIDs = new String[] { "sAMAccountType", "givenname", "sn", "mail", "employeeID", "sAMAccountName", "title", "userAccountControl", "whenCreated", "whenChanged" };
	static {
		disableSslVerification();
		}

		private static void disableSslVerification() {

		try
		{

		// Create a trust manager that does not validate certificate chains
		TrustManager[]
		trustAllCerts = new TrustManager[] {new X509TrustManager() {

		public java.security.cert.X509Certificate[] getAcceptedIssuers() {

		return null;
		}

		public void checkClientTrusted(X509Certificate[] certs, String authType) {
		}

		public void checkServerTrusted(X509Certificate[] certs, String authType) {
		}
		}
		};


		// Install the all-trusting trust manager
		SSLContext
		sc = SSLContext.getInstance("SSL");

		sc.init(null, trustAllCerts, new java.security.SecureRandom());
		HttpsURLConnection.setDefaultSSLSocketFactory(
		sc.getSocketFactory());

		// Create all-trusting host name verifier
		HostnameVerifier
		allHostsValid = new HostnameVerifier() {

		public boolean verify(String hostname, SSLSession session) {

		return true;
		}
		};

		// Install the all-trusting host verifier
		HttpsURLConnection.setDefaultHostnameVerifier(
		allHostsValid);
		}
		catch (NoSuchAlgorithmException e) {

		e.printStackTrace();
		}
		catch (KeyManagementException e) {

		e.printStackTrace();
		}
		}
	
	
	@SuppressWarnings("resource")
	@Override
	public void execute(PluginServiceCallbacks callbacks, HttpServletRequest request, HttpServletResponse response)
			throws Exception {String methodName = "execute";
		    PluginLogger log = callbacks.getLogger();
		    boolean isDebugEnabled = log.isDebugLogged();
		    log.logEntry(this, methodName, (ServletRequest)request);
		    Connection sqlCon = null;
		    String ladpUser = null;
		    String ldapUserPwd = null;
		    String ladpURL = null;
		    InitialContext context = null;
		    DataSource datasource = null;
		    String userName = null;
		    String repositoryId = null;
		    PreparedStatement preparedStatement = null;
		    ResultSet resultData = null;
		    PrintWriter printWriter = null;
		    String localAddr = null;
		    try {
		      userName = request.getParameter("userName");
		      repositoryId = request.getParameter("repoId");
		      String auditType = "Login";
		      localAddr = request.getRemoteAddr();
		      JSONObject jsonObje = null;
		      String configurationString = callbacks.loadConfiguration();
		      com.filenet.api.core.Connection p8connection = callbacks.getP8Connection(repositoryId);
		      if (isDebugEnabled)
		        System.out.println("##" + configurationString); 
		      JSONObject jsonData = (JSONObject)JSON.parse(configurationString);
		      JSONArray array = (JSONArray)jsonData.get("configuration");
		      JSONObject kmblLDAPAdminUser = (JSONObject)array.get(0);
		      JSONObject kmblLDAPAdminPassword = (JSONObject)array.get(1);
		      JSONObject kmplLDAPAdminUser = (JSONObject)array.get(2);
		      JSONObject kmplLDAPAdminPassword = (JSONObject)array.get(3);
		      JSONObject kmblLDAPURL = (JSONObject)array.get(4);
		      JSONObject kmplLDAPURL = (JSONObject)array.get(5);
		      if (repositoryId.equalsIgnoreCase("KMBL")) {
		        ladpUser = kmblLDAPAdminUser.get("value").toString();
		        ldapUserPwd = kmblLDAPAdminPassword.get("value").toString();
		        ladpURL = kmblLDAPURL.get("value").toString();
		      } else if (repositoryId.equalsIgnoreCase("KMPL")) {
		        ladpUser = kmplLDAPAdminUser.get("value").toString();
		        ldapUserPwd = kmplLDAPAdminPassword.get("value").toString();
		        ladpURL = kmplLDAPURL.get("value").toString();
		      } 
		      JSONObject dataSorceObj = (JSONObject)array.get(6);
		      String dataSorce = dataSorceObj.get("value").toString();
		      System.out.println("service call value dataSorce  is::::::::" + dataSorce);
		      User user = Factory.User.fetchInstance(p8connection, userName, null);
		      System.out.println("get_DistinguishedName" + user.get_DistinguishedName());
		      printWriter = response.getWriter();
		      jsonObje = new JSONObject();
		      JSONObject userAttrData = retrieveUserAttributes(user.get_DistinguishedName(), ladpUser, ldapUserPwd, ladpURL);
		      System.out.println("result from LDAP" + userAttrData.toString());
		      JSONObject jsonResponse = (JSONObject)JSON.parse(userAttrData.toString());
		      System.out.println("jsonResponse isEmpty:"+jsonResponse.isEmpty());
		      jsonObje = new JSONObject();
		      context = new InitialContext();
		      datasource = (DataSource)context.lookup(dataSorce.trim());
		      sqlCon = datasource.getConnection();
		      System.out.println("sqlCon success");
		      insertUserDetails(localAddr, userName, auditType, repositoryId, sqlCon, preparedStatement, log);
		      preparedStatement = sqlCon.prepareStatement(this.resourceBundle.getString("selectLDAPUserProfileQuery").trim());
		      preparedStatement.setString(1, userName);
		      preparedStatement.setString(2, repositoryId);
		      resultData = preparedStatement.executeQuery();
		      System.out.println("query executed");
		      System.out.println("resultData:"+resultData);
		      if (!resultData.next() && jsonResponse != null) {
		        insertRecordAuditHistoryTable(jsonResponse, userName, sqlCon, preparedStatement, repositoryId, log);
		        jsonObje.put("inserted", "Audit inserted Successfully");
		      } else {
		        preparedStatement = sqlCon.prepareStatement(this.resourceBundle.getString("updateLDAPUserProfileQuery").trim());
		       System.out.println("preparedStatement:::"+preparedStatement);
		        String[] attrIDs = null;
		        try {
		          attrIDs = this.resourceBundle.getString("ldapAttr").trim().split(",");
		          System.out.println("attrIDs:"+attrIDs.length);
		          for (int i = 0; i < attrIDs.length; i++) {
		            if (jsonResponse.containsKey(attrIDs[i])) {
		              String value;
		              if (jsonResponse.get(attrIDs[i]) != null && !jsonResponse.get(attrIDs[i]).toString().trim().equalsIgnoreCase("")) {
		                value = jsonResponse.get(attrIDs[i]).toString();
		              } else {
		                value = "";
		              } 
		              if (i == 0) {
		                preparedStatement.setString(1, value);
		              } else {
		                preparedStatement.setString(i + 1, value);
		              } 
		              if (jsonResponse.containsKey("whenCreated")) {
		                String creDate = jsonResponse.get("whenCreated").toString();
		                String patternChange = "yyyyMMddhhmmss";
		                SimpleDateFormat simpleDateFormatChange = new SimpleDateFormat(patternChange);
		                Date date1 = simpleDateFormatChange.parse(creDate);
		                Timestamp timestamp = new Timestamp(date1.getTime());
		                System.out.println("sqlTimestamp object is" + timestamp);
		                preparedStatement.setTimestamp(9, timestamp);
		                preparedStatement.setTimestamp(11, timestamp);
		              } 
		              if (jsonResponse.containsKey("whenChanged")) {
		                String changeDate = jsonResponse.get("whenChanged").toString();
		                String patternChange = "yyyyMMddhhmmss";
		                SimpleDateFormat simpleDateFormatChange = new SimpleDateFormat(patternChange);
		                Date date1 = simpleDateFormatChange.parse(changeDate);
		                Timestamp timestamp = new Timestamp(date1.getTime());
		                System.out.println("sqlTimestamp object is" + timestamp);
		                preparedStatement.setTimestamp(10, timestamp);
		              } 
		              preparedStatement.setString(13, userName);
		              Date utilDate = new Date();
		              Timestamp sqlTimestamp = new Timestamp(utilDate.getTime());
		              System.out.println(sqlTimestamp + "sqlTimestampsqlTimestamp");
		              preparedStatement.setTimestamp(12, sqlTimestamp);
		              preparedStatement.setString(14, repositoryId);
		            } 
		          } 
		          int executeUpdate = preparedStatement.executeUpdate();
		          System.out.println(executeUpdate + " record updated");
		          jsonObje.put("inserted", "Audit Updated Successfully");
		        } catch (Exception ex) {
		          ex.printStackTrace();
		          System.out.println("error meassge 340" + ex.getMessage());
		        } 
		      } 
		      jsonObje.put("inserted", "Audit Updated Successfully");
		      printWriter.print(jsonObje);
		    } catch (Exception ex) {
		      ex.printStackTrace();
		    } finally {
		      try {
		        if (sqlCon != null)
		          sqlCon.close(); 
		        if (null != context)
		          context.close(); 
		        if (null != preparedStatement)
		          preparedStatement.close(); 
		        datasource = null;
		        printWriter = null;
		      } catch (Exception e) {
		        e.printStackTrace();
		      } 
		    } 
		    log.logExit(this, methodName, (ServletRequest)request);
}
	 public JSONObject retrieveUserAttributes(String userDN, String adminUserName, String adminPwd, String ldapURL) throws Exception {
		
		 
		 System.out.println("UserLoginPluginService.retrieveUserAttributes()");  
		 
		 System.out.println("userDN"+userDN);
		 System.out.println("adminusername"+adminUserName);
		 System.out.println("adminpassword"+adminPwd);
		 System.out.println("ldapurl"+ldapURL);
		 JSONObject retrieveUserAttributes = new JSONObject();
		    try {
		      Hashtable<Object, Object> env = new Hashtable<Object, Object>(11);
		      env.put("java.naming.factory.initial", "com.sun.jndi.ldap.LdapCtxFactory");
		     // env.put("java.naming.security.protocol", "ssl");
		      env.put("java.naming.security.authentication", "Simple");
		      env.put("java.naming.security.principal", adminUserName);
		      env.put("java.naming.security.credentials", adminPwd);
		      env.put("java.naming.provider.url", ldapURL);
		      System.out.println("before searchContext");
		    	  InitialDirContext searchContext = new InitialDirContext(env);
			      System.out.println("InitialDirContext");
			      System.out.println("NameSpace: "+searchContext.getNameInNamespace());
			      Attributes attributes = searchContext.getAttributes(userDN);
			      System.out.println("After searchContext");
		
				
			
		      
		      HashMap<Integer, String> userAccType = new HashMap<Integer, String>();
		      HashMap<Integer, String> samAccType = new HashMap<Integer, String>();
		      userAccType.put(Integer.valueOf(512), "Enabled Account");
		      userAccType.put(Integer.valueOf(514), "Disabled Account");
		      userAccType.put(Integer.valueOf(544), "Enabled, Password Not Required");
		      userAccType.put(Integer.valueOf(546), "Disabled Password Not Required");
		      userAccType.put(Integer.valueOf(66048), "Enabled Password Doesn't Expire");
		      userAccType.put(Integer.valueOf(66050), "Disabled Password Doesn't Expire");
		      userAccType.put(Integer.valueOf(66080), "Enabled Password Doesn't Expire & Not Required");
		      userAccType.put(Integer.valueOf(66082), "Disabled Password Doesn't Expire & Not Required");
		      userAccType.put(Integer.valueOf(262656), "Enabled Smartcard Required");
		      userAccType.put(Integer.valueOf(262658), "Disabled Smartcard Required");
		      userAccType.put(Integer.valueOf(262688), "Enabled Smartcard Required Password Not Required");
		      userAccType.put(Integer.valueOf(262690), "Disabled Smartcard Required Password Not Required");
		      userAccType.put(Integer.valueOf(328192), "Disabled Smartcard Required Password Doesn't Expire");
		      userAccType.put(Integer.valueOf(328194), "Enabled Smartcard Required Password Not Required");
		      userAccType.put(Integer.valueOf(328224), "Enabled Smartcard Required Password Doesn't Expire & Not Required");
		      userAccType.put(Integer.valueOf(328226), "Disabled Smartcard Required Password Doesn't Expire & Not Required");
		      samAccType.put(Integer.valueOf(268435456), "SAM_GROUP_OBJECT");
		      samAccType.put(Integer.valueOf(268435457), "SAM_NON_SECURITY_GROUP_OBJECT");
		      samAccType.put(Integer.valueOf(536870912), "SAM_ALIAS_OBJECT");
		      samAccType.put(Integer.valueOf(536870913), "SAM_NON_SECURITY_ALIAS_OBJECT");
		      samAccType.put(Integer.valueOf(805306368), "SAM_NORMAL_USER_ACCOUNT");
		      samAccType.put(Integer.valueOf(805306369), "SAM_MACHINE_ACCOUNT");
		      samAccType.put(Integer.valueOf(805306370), "SAM_TRUST_ACCOUNT");
		      samAccType.put(Integer.valueOf(1073741824), "SAM_APP_BASIC_GROUP");
		      samAccType.put(Integer.valueOf(1073741825), "SAM_APP_QUERY_GROUP");
		      samAccType.put(Integer.valueOf(2147483647), "SAM_ACCOUNT_TYPE_MAX");
		      System.out.println("Before for loop attrIDs");
		      for (String str : attrIDs) {
		    	  System.out.println("Inside for loop");
		        if (attributes.get(str) == null) {
		          retrieveUserAttributes.put(str, "");
		          System.out.println("attributes.get(str) == null exit");
		        } else {
		        	System.out.println("Inside else condition");
		          Object attributeValue = attributes.get(str).get();
		          for (Map.Entry<Integer, String> userAcc : userAccType.entrySet()) {
		        	  System.out.println("check userAccountControl");
		            if (!attributeValue.equals(attributes.get("userAccountControl").get()))
		              continue; 
		            System.out.println("85 user account type" + Integer.parseInt(attributeValue.toString()));
		            attributeValue = userAccType.get(Integer.valueOf(Integer.parseInt(attributeValue.toString())));
		          } 
		          for (Map.Entry<Integer, String> sAMAcc : samAccType.entrySet()) {
		        	  System.out.println("check sAMAccountType");
		            if (!attributeValue.equals(attributes.get("sAMAccountType").get()))
		              continue; 
		            System.out.println("94 sam account type" + Integer.parseInt(attributeValue.toString()));
		            attributeValue = samAccType.get(Integer.valueOf(Integer.parseInt(attributeValue.toString())));
		          } 
		          System.out.println("=====" + attributeValue);
		          retrieveUserAttributes.put(str, attributeValue);
		        } 
		      } 
		      searchContext.close();
		      System.out.println("searchContext closed");
		    } catch (Exception e) {
		    	System.out.println("Internal Server Error in Integration Service occuered"+e.getMessage());
		      retrieveUserAttributes.put("statusCode", "IS00001");
		      retrieveUserAttributes.put("message", "Internal Server Error in Integration Service");
		      throw e;
		    } 
		    return retrieveUserAttributes;
		  }
	  public void insertRecordAuditHistoryTable(JSONObject jsonResponse, String userName, Connection connection, PreparedStatement preparedStatement, String repositoryId, PluginLogger log) {
		  System.out.println("UserLoginPluginService.insertRecordAuditHistoryTable()");  
		  String methodName = "insertRecordAuditHistoryTable";
		    log.logEntry(this, methodName);
		    String[] attrIDs = null;
		    try {
		      attrIDs = this.resourceBundle.getString("ldapAttr").trim().split(",");
		      System.out.println("connection object is" + connection);
		      preparedStatement = connection.prepareStatement(this.resourceBundle.getString("insertLDAPUserProfileQuery").trim());
		      System.out.println("preparedStatement");
		      for (int i = 0; i < attrIDs.length; i++) {
		        if (jsonResponse.containsKey(attrIDs[i])) {
		          String value;
		          if (jsonResponse.get(attrIDs[i]) != null && !jsonResponse.get(attrIDs[i]).toString().trim().equalsIgnoreCase("")) {
		            value = jsonResponse.get(attrIDs[i]).toString();
		          } else {
		            value = "";
		          } 
		          if (i == 0) {
		            preparedStatement.setString(1, value);
		          } else {
		            preparedStatement.setString(i + 1, value);
		          } 
		          if (jsonResponse.containsKey("whenCreated")) {
		            String creDate = jsonResponse.get("whenCreated").toString();
		            String patternChange = "yyyyMMddhhmmss";
		            SimpleDateFormat simpleDateFormatChange = new SimpleDateFormat(patternChange);
		            Date date1 = simpleDateFormatChange.parse(creDate);
		            Timestamp timestamp = new Timestamp(date1.getTime());
		            System.out.println("sqlTimestamp object is" + timestamp);
		            preparedStatement.setTimestamp(9, timestamp);
		            preparedStatement.setTimestamp(11, timestamp);
		          } 
		          if (jsonResponse.containsKey("whenChanged")) {
		            String changeDate = jsonResponse.get("whenChanged").toString();
		            String patternChange = "yyyyMMddhhmmss";
		            SimpleDateFormat simpleDateFormatChange = new SimpleDateFormat(patternChange);
		            Date date1 = simpleDateFormatChange.parse(changeDate);
		            Timestamp timestamp = new Timestamp(date1.getTime());
		            System.out.println("sqlTimestamp object is" + timestamp);
		            preparedStatement.setTimestamp(10, timestamp);
		          } 
		          preparedStatement.setString(12, userName);
		          Date utilDate = new Date();
		          Timestamp sqlTimestamp = new Timestamp(utilDate.getTime());
		          System.out.println(sqlTimestamp + "sqlTimestampsqlTimestamp");
		          preparedStatement.setTimestamp(13, sqlTimestamp);
		          preparedStatement.setString(14, repositoryId);
		        } 
		      } 
		      int executeUpdate = preparedStatement.executeUpdate();
		      System.out.println(executeUpdate + " record updated");
		    } catch (Exception ex) {
		      ex.printStackTrace();
		      System.out.println("error meassge 340" + ex.getMessage());
		    } 
		    log.logExit(this, methodName);
		  }
		  
		  public void insertUserDetails(String localAddr, String userId, String auditType, String repositoryId, Connection connection, PreparedStatement preparedStatement, PluginLogger log) {
		    String methodName = "insertUserDetails";
		    log.logEntry(this, methodName);
		    try {
		      preparedStatement = connection.prepareStatement(this.resourceBundle.getString("insertUserActionAuditLogQuery").trim());
		      Date utilDate = new Date();
		      Timestamp sqlTimestamp = new Timestamp(utilDate.getTime());
		      System.out.println(sqlTimestamp + "sqlTimestamp");
		      preparedStatement.setString(1, userId);
		      preparedStatement.setString(2, localAddr);
		      preparedStatement.setString(3, auditType);
		      preparedStatement.setTimestamp(4, sqlTimestamp);
		      preparedStatement.setString(5, repositoryId);
		      int status = preparedStatement.executeUpdate();
		      System.out.println(status + "  records inserted successfully AuditTABLE");
		    } catch (SQLException e) {
		      e.printStackTrace();
		    } 
		    log.logExit(this, methodName);
		  }
	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return "UserLoginPluginService";
	}
	
	public static void main(String args[]){
		UserLoginPluginService user =  new UserLoginPluginService();
		 
		try {
			user.retrieveUserAttributes("CN=devcediradm,DC=KGPUAT,DC=COM" , "devcediradm", "Dell@123", "ldap://10.10.19.55:389");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
