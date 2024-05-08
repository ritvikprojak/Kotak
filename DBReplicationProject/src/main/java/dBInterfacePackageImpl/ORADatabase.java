package dBInterfacePackageImpl;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.log4j.Logger;

import cripUtilityPackage.AES;
//import cripUtilityPackage.CripUtility;
import dBInterfacePackage.DBInterface;
import dBInterfacePackage.ReadServerDBInterface;

/*
 * 			ods = new OracleDataSource();
			ods.setURL("jdbc:oracle:thin:@//192.168.1.18:1521/XEPDB1"); // jdbc:oracle:thin@//[hostname]:[port]/[DB service name]
		    ods.setUser("system"); // [username]
		    ods.setPassword("Filenet8"); // [password]
		    Connection conn = ods.getConnection();////
		     
		    PreparedStatement stmt = conn.prepareStatement("SELECT 'Hello World!' FROM DUAL");
		    ResultSet rslt = stmt.executeQuery();
		    while (rslt.next()) {
		      System.out.println(rslt.getString(1));
		    }
*		    
*/

public class ORADatabase implements DBInterface, ReadServerDBInterface {

	final static Logger log = Logger.getLogger(ORADatabase.class.getName());

	public ResultSet fetchResultSet(final String tb, final Connection con, final ResourceBundle rBProps)
			throws SQLException {
		try {
			Statement sqlStatement = con.createStatement();
			// Columns to be Fetched from HRRM ResultSet
			String hRRMTableName = rBProps.getString("hRRMTableName");// "HR.HRRM";
			String pERSON_NUMBER_HRRM = rBProps.getString("pERSON_NUMBER_HRRM");// "PERSON_NUMBER";
			String rESPONSIBILITY_NAME_HRRM = rBProps.getString("rESPONSIBILITY_NAME_HRRM");// "RESPONSIBILITY_NAME";
			// Columns to be Fetched From CHRRM ResultSet
			String cHRRM_TableName = rBProps.getString("cHRRM_TableName");
			String eMPLOYEE_NUMBER_CHRRM = rBProps.getString("eMPLOYEE_NUMBER_CHRRM"); // This
																						// is
																						// Person
																						// Number
																						// from
																						// CHRRM
			String rESPONSIBILITY_NAME_CHRRM = rBProps.getString("rESPONSIBILITY_NAME_CHRRM");
			String lOB_CODE_CHRRM = rBProps.getString("lOB_CODE_CHRRM");
			String lOC_CODE_CHRMS = rBProps.getString("lOC_CODE_CHRMS");
			String cC_CODE_CHRRM = rBProps.getString("cC_CODE_CHRRM");
			String iNCLU_OR_EXCLU_CHRRM = rBProps.getString("iNCLU_OR_EXCLU_CHRRM");
			String aCTIVE = rBProps.getString("aCTIVE");
			String lAST_WORKING_DATE_CHRRM = rBProps.getString("lAST_WORKING_DATE_CHRRM");
			
			// Columns to be Fetched From HRMS ResultSet
			String hRMSTableName = rBProps.getString("hRMSTableName");// "HR.HRMS";
			String eMPLOYEE_NUMBER_HRMS = rBProps.getString("eMPLOYEE_NUMBER_HRMS");// "EMPLOYEE_NUMBER";
			// ########### Re-Constructing 25 Nov 2019 ################
			String cOMPANY_HRMS = rBProps.getString("cOMPANY_HRMS");// "COMPANY";
			String oRACLE_EMPLOYEE_NUMBER_HRMS = rBProps.getString("oRACLE_EMPLOYEE_NUMBER_HRMS");// "ORACLE_EMPLOYEE_NUMBER";
			// ########### Re-Constructing 25 Nov 2019 ################
			String lOB_CODE_HRMS = rBProps.getString("lOB_CODE_HRMS");// "LOB_CODE";
			String lOC_CODE_HRMS = rBProps.getString("lOC_CODE_HRMS");// "LOC_CODE";
			String cC_CODE_HRMS = rBProps.getString("cC_CODE_HRMS");// "CC_CODE";

			if (tb == "HRRM") {
				// System.out.println("SELECT "+pERSON_NUMBER_HRRM+",
				// "+rESPONSIBILITY_NAME_HRRM+" FROM "+hRRMTableName);
				ResultSet rs = sqlStatement.executeQuery(
						"SELECT " + pERSON_NUMBER_HRRM + ", " + rESPONSIBILITY_NAME_HRRM + " FROM " + hRRMTableName); // Change
																														// Schema
																														// &
																														// table
																														// Name
																														// of
																														// HRRM
																														// in
																														// SELECT
																														// QUERY
																														// According
																														// to
																														// Env
				isRSEmpty(rs);
				return rs;

			} else if (tb == "CHRRM") {
				// System.out.println("SELECT "+pERSON_NUMBER_HRRM+",
				// "+rESPONSIBILITY_NAME_HRRM+" FROM "+hRRMTableName);
				ResultSet rs = sqlStatement.executeQuery("SELECT " + eMPLOYEE_NUMBER_CHRRM + ", "
						+ rESPONSIBILITY_NAME_CHRRM + ", "+iNCLU_OR_EXCLU_CHRRM + " FROM " + cHRRM_TableName
						+ " WHERE "+lAST_WORKING_DATE_CHRRM+" >= LOCALTIMESTAMP AND "+ aCTIVE + " = "+"'"+"1"+"'"); // Change
																					// Schema
																					// &
																					// table
																					// Name
																					// of
																					// HRRM
																					// in
																					// SELECT
																					// QUERY
																					// According
																					// to
																					// Env
				isRSEmpty(rs);
				return rs;
			} else if (tb == "CHRRM2") {

				// ResultSet rs = sqlStatement
				// .executeQuery("SELECT "+eMPLOYEE_NUMBER_CHRRM+",
				// "+lOB_CODE_CHRRM+", "+cC_CODE_CHRRM+", "+lOC_CODE_CHRMS+"
				// FROM "+cHRRM_TableName); // Change Schema & table Name of
				// HRMS According to Env

				String strSQL = "SELECT DISTINCT x." + eMPLOYEE_NUMBER_CHRRM + "," + " ( SELECT " + lOB_CODE_CHRRM
						+ " FROM " + cHRRM_TableName + " a WHERE a." + eMPLOYEE_NUMBER_CHRRM + "=x."
						+ eMPLOYEE_NUMBER_CHRRM + " AND ROWNUM=1 )" + "," + " ( SELECT " + cC_CODE_CHRRM + " FROM "
						+ cHRRM_TableName + " a WHERE a." + eMPLOYEE_NUMBER_CHRRM + "=x." + eMPLOYEE_NUMBER_CHRRM
						+ " AND ROWNUM=1 )" + "," + " ( SELECT " + lOC_CODE_CHRMS + " FROM " + cHRRM_TableName
						+ " a WHERE a." + eMPLOYEE_NUMBER_CHRRM + "=x." + eMPLOYEE_NUMBER_CHRRM + " AND ROWNUM=1 )"
						+ " FROM " + cHRRM_TableName + " x";
				// System.out.println(strSQL);
				// System.out.println(strSQL);
				ResultSet rs1 = sqlStatement.executeQuery(strSQL);// executeQuery
																	// Ends
				isRSEmpty(rs1);
				return rs1;

			}
			// ########### Re-Constructing 25 Nov 2019 ################ (Also
			// changes in Main Method Line No 289)
			else if (tb == "HRMS") {

				String NEN = "SELECT CONCAT(" + cOMPANY_HRMS + ", " + oRACLE_EMPLOYEE_NUMBER_HRMS
						+ ") AS EMPLOYEE_NUMBER, " + lOB_CODE_HRMS + ", " + cC_CODE_HRMS + "," + lOC_CODE_HRMS
						+ " FROM " + hRMSTableName;// +" FETCH NEXT 1000 ROWS
													// ONLY";
				// System.out.println("NEN : "+NEN);
				// System.out.println("SELECT "+eMPLOYEE_NUMBER_HRMS+",
				// "+lOB_CODE_HRMS+", "+cC_CODE_HRMS+","+lOC_CODE_HRMS+" FROM
				// "+hRMSTableName);
				ResultSet rs = sqlStatement.executeQuery(NEN);
				// ResultSet rs = sqlStatement
				// .executeQuery("SELECT "+eMPLOYEE_NUMBER_HRMS+",
				// "+lOB_CODE_HRMS+", "+cC_CODE_HRMS+", "+lOC_CODE_HRMS+" FROM
				// "+hRMSTableName); // Change Schema & table Name of HRMS
				// According to Env
				isRSEmpty(rs);
				return rs;
			}
			// ########### Re-Constructing 25 Nov 2019 ################

			// ########### Included on 28 Nov 2019 ##########
			else if (tb == "pre_HRMS") {
				// System.out.println("SELECT "+eMPLOYEE_NUMBER_HRMS+",
				// "+cOMPANY_HRMS+", "+oRACLE_EMPLOYEE_NUMBER_HRMS+" FROM
				// "+hRMSTableName);
				ResultSet rs = sqlStatement.executeQuery("SELECT " + eMPLOYEE_NUMBER_HRMS + ", " + cOMPANY_HRMS + ", "
						+ oRACLE_EMPLOYEE_NUMBER_HRMS + " FROM " + hRMSTableName); // Change
																					// Schema
																					// &
																					// table
																					// Name
																					// of
																					// HRMS
																					// According
																					// to
																					// Env
				isRSEmpty(rs);
				return rs;
			} else if (tb == "pre_HRRM") {
				// System.out.println("SELECT "+pERSON_NUMBER_HRRM+" FROM
				// "+hRRMTableName);
				ResultSet rs = sqlStatement.executeQuery("SELECT " + pERSON_NUMBER_HRRM + " FROM " + hRRMTableName); // Change
																														// Schema
																														// &
																														// table
																														// Name
																														// of
																														// HRRM
																														// in
																														// SELECT
																														// QUERY
																														// According
																														// to
																														// Env
				isRSEmpty(rs);
				return rs;
			}
			// ########### Included on 28 Nov 2019 ##########
			else {
				if(log.isInfoEnabled())log.info(
						"!! tb Argument passed in fetchResultSet(String tb, Connection con) for Table Name did not Match !!");
				System.out.println(
						"!! tb Argument passed in fetchResultSet(String tb, Connection con) for Table Name did not Match !!");
			}
		} catch (Exception ex) {
			log.error("Error on Fetch Result Set: " + ex.fillInStackTrace());

		}
		ResultSet rs = null;
		return rs;

	}// fetchResultSet

	public void isRSEmpty(ResultSet rs) {
		// Verifies if CASES(Rows) are Returned by SQL Query
		if (rs.equals(null)) {
			if(log.isInfoEnabled())log.info("rs.equals(null) : " + true);
			if(log.isInfoEnabled())log.info("!!! warn Msg  !!!", new Error(
					"QUERY RETURNED EMPTY RESULTSET. BAD 'SQLQUERY' PARAMETER IN CONFIG.PROPERTIES FILE AT /FS1/IBM/ICNAUTOACTIONS/CONFIG/CONFIG.PROPERTIES "));
			return;
		}
		if(log.isInfoEnabled())log.info("\t\tMessage:  SQLQUERY Returned ResultSet Row(s) from DB");

	}// isRSEmpty

	public void loadDriver() throws ClassNotFoundException {
		Class.forName("oracle.jdbc.OracleDriver");
		if(log.isInfoEnabled())log.info("Loaded the JDBC driver for: ORACLE");

	}// loadDriver

	public Connection loadConnection(final String env, final ResourceBundle rBProps) 
			throws SQLException, ClassNotFoundException {
		String url = null;
		String user = null;
		String password = null;
		Connection con = null;
		if(log.isInfoEnabled())log.info("ORL Server Env loadConnection STARTs");
		// ORADatabase.getSSLAccess();
		// pre-requsites for loading the Connections
		final String urlPrefix = "jdbc:oracle:thin:@";

		if (env == "STG") { //loading live view
			// jdbc:oracle:thin:@ // 192.168.1.18:1521/XEPDB1    

			String ipaddress = "//" + rBProps.getString("STG_ORA_URI") + "/" + rBProps.getString("STG_ORA_DBNAME"); // get
																													// databaseName
																													// &
																													// ip_port
																													// from
																													// config.properties
			url = urlPrefix + ipaddress;
			if(log.isInfoEnabled())log.info("ORA URL : " + url);
			user = rBProps.getString("STG_ORA_USERID");
			password = AES.decrypt(rBProps.getString("STG_ORA_PASSWORD"));
			// password =
			// CripUtility.decryptStr(rBProps.getString("STG_ORA_PASSWORD"));
//			 password = rBProps.getString("STG_ORA_PASSWORD");
			// System.out.println("url :"+url+" |user :"+user+" |password :
			// "+password);

		} else if (env == "SERVER") { //loading target db

			String ipaddress = "//" + rBProps.getString("ORA_URI") + "/" + rBProps.getString("ORA_DBNAME"); // get
																											// databaseName
																											// &
																											// ip_port
																											// from
																											// config.properties
			url = urlPrefix + ipaddress;
			if(log.isInfoEnabled())log.info("ORA URL : " + url); // get databaseName & ip_port from
											// config.properties
			user = rBProps.getString("ORA_USERID");
			// password =
			// CripUtility.decryptStr(rBProps.getString("ORA_PASSWORD"));
//			 password = rBProps.getString("ORA_PASSWORD");
			password = AES.decrypt(rBProps.getString("ORA_PASSWORD"));
		}

		try {
			Properties props = new Properties();
			props.getProperty("user", user);
			props.getProperty("password", password);
			props.getProperty("oracle.net.READ_TIMEOUT", "5000"); // 5000 milliseconds (5 seconds)
			
			con = DriverManager.getConnection(url, user, password);
			if(log.isInfoEnabled())log.info("Printing Connection Object for ORA : " + con);
			if(log.isInfoEnabled())log.info("ORA dataBase Now Accessed via user : " + user);
			if(log.isInfoEnabled())log.info("Auto-commit is : " + con.getAutoCommit());
		} finally {
			/*
			 * if(log.isInfoEnabled())log.info( "Logging from finally block of : "
			 * +SQLDatabase.class.getName() )
			 */ ;
		}
		if(log.isInfoEnabled())log.info("## loadConnection ENDs, Returning Connection Object ##");
		return con;
	}// loadConnection

	public void unloadConnection(Connection con) throws SQLException {
		try {
			con.close();
			 if(log.isInfoEnabled())log.info("DB Connection unloaded");
		} finally {
		}
	}// unloadConnection

	public boolean getAutoCommit(Connection con) throws SQLException {
		return con.getAutoCommit();
	}// getAutoCommit

	public void offAutoCommit(Connection con) throws SQLException {
		con.setAutoCommit(false);
	}// offAutoCommit

	public void commit(Connection con) throws SQLException {
		con.commit();
	}// commit

	public int updateHRMSwithCHRRM(Connection oRAcon,ResourceBundle rBProps) throws SQLException {
		int isSucess=0;
		try{
		String hRMS_HRRMTableName = rBProps.getString("hRMS_HRRMTableName");
		String hRMS_HRRM_EMPLOYEE_NUMBERColumnName = rBProps.getString("hRMS_HRRM_EMPLOYEE_NUMBERColumnName");
		String hRMS_HRRM_ROLE = rBProps.getString("hRMS_HRRM_ROLE");
		String hRMS_HRRM_DOMAIN_LOGIN_ID = rBProps.getString("hRMS_HRRM_DOMAIN_LOGIN_ID");
		String hRMS_HRRM_END_DATE = rBProps.getString("hRMS_HRRM_END_DATE");
		String hRMS_HRRM_COMPANY = rBProps.getString("hRMS_HRRM_COMPANY");
		
		
		String cHRRM_TableName= rBProps.getString("cHRRM_TableName");
		String eMPLOYEE_NUMBER_CHRRM = rBProps.getString("eMPLOYEE_NUMBER_CHRRM");
		String dOMAIN_LOGIN_ID_CHRRM  = rBProps.getString("dOMAIN_LOGIN_ID_CHRRM");
		String rOLE_CHRRM = rBProps.getString("rOLE_CHRRM");
		String lAST_WORKING_DATE_CHRRM = rBProps.getString("lAST_WORKING_DATE_CHRRM");
		String aCTIVE = rBProps.getString("aCTIVE");
		
		String dEFAULT_COMPANY = rBProps.getString("defaultCompany");
		
//		String DML = 
//				"INSERT INTO "+hRMS_HRRMTableName+" ( "+hRMS_HRRM_EMPLOYEE_NUMBERColumnName+","+hRMS_HRRM_DOMAIN_LOGIN_ID+","+hRMS_HRRM_ROLE+","+hRMS_HRRM_END_DATE+" )"
//						+" SELECT DISTINCT x."+eMPLOYEE_NUMBER_CHRRM+" AS "+hRMS_HRRM_EMPLOYEE_NUMBERColumnName+","
//						+" ( SELECT "+dOMAIN_LOGIN_ID_CHRRM+" FROM "+cHRRM_TableName+" a WHERE a."+eMPLOYEE_NUMBER_CHRRM+"=x."+eMPLOYEE_NUMBER_CHRRM+" AND ROWNUM=1 ) AS "+hRMS_HRRM_DOMAIN_LOGIN_ID+","
//						+" ( SELECT "+rOLE_CHRRM+" FROM "+cHRRM_TableName+" a WHERE a."+eMPLOYEE_NUMBER_CHRRM+"=x."+eMPLOYEE_NUMBER_CHRRM+" AND ROWNUM=1 ) AS "+hRMS_HRRM_ROLE
//				+" FROM "+cHRRM_TableName+" x ORDER BY "+hRMS_HRRM_EMPLOYEE_NUMBERColumnName;
		
		String DML = 
				"INSERT INTO "+hRMS_HRRMTableName+" ( "+hRMS_HRRM_DOMAIN_LOGIN_ID+","+hRMS_HRRM_EMPLOYEE_NUMBERColumnName+","+hRMS_HRRM_ROLE+","+hRMS_HRRM_END_DATE+","+hRMS_HRRM_COMPANY+" )"
				+" SELECT DISTINCT T."+dOMAIN_LOGIN_ID_CHRRM+",T."+eMPLOYEE_NUMBER_CHRRM+",T."+rOLE_CHRRM+",T."+lAST_WORKING_DATE_CHRRM+","+"'"+dEFAULT_COMPANY+"'" +"FROM "+cHRRM_TableName+" T "+
				"WHERE NOT EXISTS ( SELECT "+hRMS_HRRM_DOMAIN_LOGIN_ID+" FROM "+hRMS_HRRMTableName+" T2 WHERE T2."+hRMS_HRRM_DOMAIN_LOGIN_ID+" = T."+dOMAIN_LOGIN_ID_CHRRM+" )"
				+" AND T."+aCTIVE+" = "+"'"+"1"+"'";
		
		
//		Insert Into Hrms_Hrrm(Domain_Login_Id,Employee_Number) 
//		select Distinct T.Domain_Id , T.Person_Number
//		From Cust_Roles_Usr_Mapping T 
//		where not Exists (Select Domain_Login_Id from Hrms_Hrrm t2 where T2.Domain_Login_Id = T.Domain_Id)
		
		try {
			Statement st = oRAcon.createStatement();
			//System.out.println(DML);
			return st.executeUpdate(DML);
		} catch (SQLException e) {
			if(log.isInfoEnabled())log.info("Something Gone Wrong While Updating HRMS Table with Custom Users Entry Kindly check Custom Role Entries");
			//throw e;
		}
		}catch(Exception ex){
			log.error("Error: "+ex.fillInStackTrace());
		}
	return isSucess;
	}

	// public static void main(String[] args) throws SQLException,
	// ClassNotFoundException{
	// new ORADatabase().loadDriver();
	// new ORADatabase().updateHRMSwithCustomAdUserId(new
	// ORADatabase().loadConnection("STG", rBProps), rBProps);
	// }
	public static void getSSLAccess() {

		// sec = true

		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkClientTrusted(X509Certificate[] certs, String authType) {
			}

			public void checkServerTrusted(X509Certificate[] certs, String authType) {
			}

		} };

		SSLContext sc;
		try {
			sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

			// Create all-trusting host name verifier
			HostnameVerifier allHostsValid = new HostnameVerifier() {
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			};
			// Install the all-trusting host verifier
			HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
		} catch (NoSuchAlgorithmException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/*
		 * end of the fix
		 */

	}

}// class
