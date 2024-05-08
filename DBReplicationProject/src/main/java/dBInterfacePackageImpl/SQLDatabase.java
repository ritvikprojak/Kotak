package dBInterfacePackageImpl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import cripUtilityPackage.AES;
//import cripUtilityPackage.CripUtility;
import dBInterfacePackage.DBInterface;
import dBInterfacePackage.ReadServerDBInterface;
/*
 * This is the Class of Database used as Master Database Hence Functionality is limited only to Reading the Data ie SELECT SQLQUERY ONLY
 */
public class SQLDatabase implements DBInterface, ReadServerDBInterface {

	final static Logger log = Logger.getLogger(SQLDatabase.class.getName());
	
	public SQLDatabase(){
		if(log.isInfoEnabled())log.info("DB2Database STARTs");
	}
	public void loadDriver() throws ClassNotFoundException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		if(log.isInfoEnabled())log.info("Loaded the JDBC driver for: SQL");
	}
	public Connection loadConnection(final String env, final ResourceBundle rBProps) throws SQLException {
		if(log.isInfoEnabled())log.info("SQL loadConnection STARTs");
		// pre-requsites for loading the Connections
		final Connection con;
		final String urlPrefix = "jdbc:sqlserver:";
		String ipaddress = "//"+rBProps.getString("SQL_URI")+";databaseName="+rBProps.getString("DB2_DBNAME")+";" ; 
						// get databaseName & ip_port from config.properties
		final String url = urlPrefix + ipaddress;
		final String user = rBProps.getString("SQL_USERID");
		final String password =  AES.decrypt(rBProps.getString("SQL_PASSWORD"));
										
		try {
			if(log.isInfoEnabled())log.info("SQL URL : " + url);
			con = DriverManager.getConnection(url, user, password);
			if(log.isInfoEnabled())log.info("Printing Connection Object for SQL : " + con);
			if(log.isInfoEnabled())log.info("SQL dataBase Now Accessed via user : " + user);

		} finally {/*if(log.isInfoEnabled())log.info( "Logging from finally block of : "+SQLDatabase.class.getName() )*/ ;}
		if(log.isInfoEnabled())log.info("## loadConnection ENDs, Returning Connection Object ##");
		return con;
	}
	public void unloadConnection(Connection con) throws SQLException {
		try {
			con.close();
			if(log.isInfoEnabled())log.info("SQL Connection unloaded");
		} finally {
		}

	}
	public ResultSet fetchResultSet(String tb, Connection con, ResourceBundle rBProps)
			throws SQLException {

		Statement sqlStatement = con.createStatement();

		if (tb == "hE") { // get SQLStr from config.properties

			ResultSet rs = sqlStatement
					.executeQuery("SELECT PERSON_NUMBER,RESPONSIBILITY_NAME FROM KOTAK_UPM.dbo.HR_Employee_Relationship");
			isRSEmpty(rs);
			return rs;
			
		} else if (tb == "mD") { // get SQLStr from config.properties
			ResultSet rs = sqlStatement
					.executeQuery("SELECT EMP_ID,LOB,LOC,CC FROM KOTAK_UPM.dbo.Master_Details");
			isRSEmpty(rs);
			return rs;
		} else {
			// throw new
			// IllegalArgumentException​("tb Argument for Table Name did not Match");
		}
		ResultSet rs = null;
		return rs;
	}
	public void isRSEmpty(ResultSet rs) {

		// Verifies if CASES(Rows) are Returned by SQL Query
		if (rs.equals(null)) {
			if(log.isInfoEnabled())log.info("rs.equals(null) : " + true);
			if(log.isInfoEnabled())log.info(
					"!!! warn Msg  !!!",
					new Error(
							"QUERY RETURNED EMPTY RESULTSET. BAD 'SQLQUERY' PARAMETER IN CONFIG.PROPERTIES FILE AT /FS1/IBM/ICNAUTOACTIONS/CONFIG/CONFIG.PROPERTIES "));
			return;
		}
		if(log.isInfoEnabled())log.info("\t\tMessage:  SQLQUERY Returned ResultSet Row(s)");

	}
	public boolean getAutoCommit(Connection con) throws SQLException {
		return con.getAutoCommit();
	}
	public void offAutoCommit(Connection con) throws SQLException {
		con.setAutoCommit(false);
		
	}
	public void commit(Connection con) throws SQLException {
		con.commit();
		
	}
}// class ends
