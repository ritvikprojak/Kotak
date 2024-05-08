package dBInterfacePackageImpl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import cripUtilityPackage.AES;
//import cripUtilityPackage.CripUtility;
import cRUDUtilityClass.UDBatchForBackupDB;
import dBInterfacePackage.DBInterface;

/*
 * This is the class of Database which is used as Backup DB or Slave DB Hence Functionality is Limited Only to Updating DB ie Upadate Query only
 */
public class DB2Database extends UDBatchForBackupDB implements DBInterface { //extends UDBatchForBackupDB implements ReadServerDBInterface
	
	final static Logger log = Logger.getLogger(DB2Database.class.getName());
	
	public DB2Database() {
		if(log.isInfoEnabled())log.info("DB2Database STARTs");
	}
    public void loadDriver() throws ClassNotFoundException {	
		    Class.forName("com.ibm.db2.jcc.DB2Driver");
		    if(log.isInfoEnabled())log.info("Loaded the JDBC driver for: DB2");
    }

	public Connection loadConnection(final String env, final ResourceBundle rBProps) throws SQLException {
		if(log.isInfoEnabled())log.info("DB2 loadConnection STARTs");
		// pre-requisites for loading the Connections
		final Connection con;
		final String urlPrefix = "jdbc:db2:";
		final String ipaddress = "//"+rBProps.getString("DB2_URI")+"/"+rBProps.getString("DB2_DBNAME") ; 
		final String url = urlPrefix+ipaddress;
		final String user= rBProps.getString("DB2_USERID");
	    if(log.isInfoEnabled())log.info(rBProps.getString("DB2_USERID"));
	    final String password=  AES.decrypt(rBProps.getString("DB2_PASSWORD"));                                  
		
		try{
			if(log.isInfoEnabled())log.info("DB2 URL : "+url);
			con=DriverManager.getConnection(url,user,password);
			if(log.isInfoEnabled())log.info("Printing Connection Object for DB2 : " +con);
			if(log.isInfoEnabled())log.info("DB2 dataBase Now Accessed via user : "+user);
			
			
		}finally {/*if(log.isInfoEnabled())log.info( "Logging from finally block of : "+SQLDatabase.class.getName() )*/ ;}
		if(log.isInfoEnabled())log.info("## loadConnection ENDs, Returning Connection Object ##");
		return con;
	}
	
	public void unloadConnection(Connection con) throws SQLException {
    	try {
			con.close();
			//if(log.isInfoEnabled())log.info("DB2 Connection unloaded");
		}finally{	
		}

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


