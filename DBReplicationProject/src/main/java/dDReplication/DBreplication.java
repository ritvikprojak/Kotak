package dDReplication;

/*
 *catch( ClassNotFoundException e ){
	e.printStackTrace();
	if(log.isInfoEnabled())log.info("ClassNotFoundException Occurred : "+e.getLocalizedMessage()+" Check if the jar in Built Path OR Class.forName String is Correct");
	System.out.println("ClassNotFoundException Occurred : "+e.getLocalizedMessage()+" Check if the jar in Built Path OR Class.forName String is Correct");
} 
 */
//Tip : //ArrayList<Map<String, String>> sQLParametersList = new ArrayList<Map<String, String>>(); Is Used to Store passed Parameter for Update SQL 
/* https://sanket_projak@bitbucket.org/kotakbank/hrdmsrepo.git
GIT Directory : C:\Users\NewKotakGit\.git
Working Directory : C:\Users\NewKotakGit
https://github.github.com/training-kit/downloads/github-git-cheat-sheet/
 */
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.apache.log4j.Logger;

import activeDirectory.ActiveDirectoryApp;
import cRUDUtilityClass.UDBatchForBackupDB;
import dBInterfacePackageImpl.ORADatabase;
import emailSMTPPackage.EmailNotification;
import resourceBundlePackage.ResourceBundleClass;

//MasterSlaveDBreplication.rBProps
public class DBreplication {

	static {
		LocalDateTime startTime = LocalDateTime.now();
		String logFileName = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss").format(startTime);
		System.setProperty("current.date.time", logFileName);
	}

	final static Logger log = Logger.getLogger(DBreplication.class.getName());
	static ResourceBundleClass rBC = new ResourceBundleClass();
	public final static ResourceBundle rBProps = rBC.getResourceBundle();
	static ArrayList<Map<String, Object>> hRMSMapList = new ArrayList<Map<String, Object>>();
	static ArrayList<Map<String, String>> sQLParametersList = new ArrayList<Map<String, String>>();

	// This Method Converts resultset to MapList
	public ArrayList<Map<String, Object>> resultSetToMapList(ResultSet rs) throws SQLException {

		ArrayList<Map<String, Object>> MapList = new ArrayList<Map<String, Object>>();

		if (rs != null) {
			ResultSetMetaData resultSet1Meta = rs.getMetaData();
			int numColumns = resultSet1Meta.getColumnCount();
			while (rs.next()) {
				Map<String, Object> eachRow = new HashMap<String, Object>();
				for (int i = 1; i <= numColumns; ++i) {
					String key = resultSet1Meta.getColumnName(i);
					Object value = rs.getObject(i);
					eachRow.put(key, value);
				} // for
				MapList.add(eachRow);
			} // while
		} // if
		return MapList;
	}// resultSetToMapList Method Ends

	// This method Runs Business Logic & Returns the parameter's List Required
	// for Update DML

	public static void main(String[] args) throws SQLException, ClassNotFoundException {
		// final Logger log =
		// Logger.getLogger(MasterSlaveDBreplication.class.getName());
		if(log.isInfoEnabled())log.info(" ");
		if(log.isInfoEnabled())log.info("###################### Main STARTs ######################");
		if(log.isTraceEnabled())log.trace("###################### TRACE STARTs ######################");

		LocalDateTime startTime = LocalDateTime.now();
		System.out.println("## Progarm started at 		:"
				+ DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(startTime) + " ##");

		Connection oRAcon_STG = null;
		ORADatabase oRAObj = new ORADatabase();
		
		oRAcon_STG = oRAObj.loadConnection("STG", rBProps); // Loading Server, loading live view
															// Env

		Connection oRAcon = null;
		oRAcon = oRAObj.loadConnection("SERVER", rBProps); // Loading STG Env, loading target db

		String hRMS_HRRMTableName = rBProps.getString("hRMS_HRRMTableName");
		String hRRMTableName_TG = rBProps.getString("hRRMTableName_TG");
		// ########### Re-Constructing 25 Nov 2019 ################
		try {
			if(log.isInfoEnabled())log.info("Updated Auto-commit is : " + oRAObj.getAutoCommit(oRAcon));

				UDBatchForBackupDB uDBatchObj = new UDBatchForBackupDB(); 
				uDBatchObj.fireTruncateNInsertDDL_forHRRM(oRAcon_STG, oRAcon, rBProps); // deleted the older hrrm_snap and created a new hrrm_snap with the help of apps.kbank.hrrm
				oRAObj.commit(oRAcon); // commiting hrupm.hrrm
				if(log.isInfoEnabled())log.info("");
				if(log.isInfoEnabled())log.info("##################### Ending with Updating HRRM Data #####################");
				if(log.isInfoEnabled())log.info("##################### Starting with Updating HRMS_HRRM Data #####################");
				if(log.isInfoEnabled())log.info("");

				uDBatchObj.FireTruncNSnapHRMS_HRRM(oRAcon_STG, oRAcon, rBProps);// Call
																				// Method
																				// to
																				// load
																				// Fresh
																				// Data
				// ## Getting Custom Roles in HRMS ##
				int isCustomUpdated = new ORADatabase().updateHRMSwithCHRRM(oRAcon, rBProps);
				if(log.isInfoEnabled())log.info("Total Custom Role Entries updated in HRMS Table is : " + isCustomUpdated);
				// ## Copying Fresh Data to HRMS_HRRM From HRMS BeFore Updating
				// HR_PERSON_NUMBER COLUMN
				oRAObj.unloadConnection(oRAcon_STG);

			if(log.isInfoEnabled())log.info("############ Starting With AdUsers Shuffling ############");
			ActiveDirectoryApp aDAObj = new ActiveDirectoryApp();

			aDAObj.shuffleAdUsers(oRAcon);

			if(log.isInfoEnabled())log.info("############ Ending With AdUsers Shuffling ############");
			oRAObj.unloadConnection(oRAcon);
			if(log.isInfoEnabled())log.info("## unloaded HRUPM DB connection ##");

			try {
					if(log.isInfoEnabled()){
						log.info(new EmailNotification().generateAndSendEmail("Success"));
					}
			    }	
			catch (AddressException e2) {
				e2.printStackTrace();
				if(log.isInfoEnabled())log.info(e2.getLocalizedMessage());
			} catch (MessagingException e2) {
				e2.printStackTrace();
				if(log.isInfoEnabled())log.info(e2.getLocalizedMessage());
			}

			if(log.isInfoEnabled())log.info("###################### Main ENDs ######################");
			LocalDateTime endTime = LocalDateTime.now();
			
			System.out.println("## Progarm terminated at 	:"
					+ DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(endTime) + " ##");
			System.out.println("## For More Info Kindly See Logs File ###");
		} 
		catch (NullPointerException e) {// Catch #4
			// ## Restoring HRMS_HRRM Table As Before From HRMS_HRRM_snap ##
			Statement restoreDBStat = oRAcon.createStatement(); // Initializing
																// Statement
																// Class for
																// Storing DML
																// String
			// try{ restoreDBStat.executeUpdate("ALTER TABLE
			// "+hRMS_HRRMTableName+" ADD "+hRMS_HRRM_PERSON_NUMBERColumnName+"
			// VARCHAR2(150)");}catch(SQLException
			// e1){if(log.isInfoEnabled())log.info("HR_PERSON_NUMBER Already Exists in HRMS_HRRM");}
			try {
				restoreDBStat.addBatch("TRUNCATE TABLE " + hRMS_HRRMTableName);
				restoreDBStat.addBatch("INSERT INTO " + hRMS_HRRMTableName + " SELECT * FROM HRMS_HRRM_snap");
				restoreDBStat.addBatch("TRUNCATE TABLE " + hRRMTableName_TG);
				restoreDBStat.addBatch("INSERT INTO " + hRRMTableName_TG + " SELECT * FROM HRRM_snap");
				restoreDBStat.executeBatch();
				oRAcon.commit();
			} catch (Exception ex) {
				log.error("Error:" + ex.fillInStackTrace());
			}
			if(log.isInfoEnabled())log.info("#  Changes Rolled Back in DB (if Any) #");
			// ## Restoring HRMS_HRRM Table As Before From HRMS_HRRM_snap ##
			if(log.isInfoEnabled())log.info("NullPointerException Occurred : " + e.getLocalizedMessage());
			// ## Email Notification Sending Business ##
			try {
				if(log.isInfoEnabled()){
					log.info(new EmailNotification().generateAndSendEmail("Success"));
				}
			} catch (AddressException e2) {
				e2.printStackTrace();
				if(log.isInfoEnabled())log.info(e2.getLocalizedMessage());
			} catch (MessagingException e2) {
				e2.printStackTrace();
				if(log.isInfoEnabled())log.info(e2.getLocalizedMessage());
			}
			// ## Email Notification Sending Business ##

			e.printStackTrace();
			log.error("NullPointerException Occurred : " + e.getLocalizedMessage());
		} catch (SQLSyntaxErrorException e) {// Catch #1
			// ## Restoring HRMS_HRRM Table As Before From HRMS_HRRM_snap ##
			e.printStackTrace();
			Statement restoreDBStat = oRAcon.createStatement(); // Initializing
																// Statement
																// Class for
																// Storing DML
																// String
			// try{ restoreDBStat.executeUpdate("ALTER TABLE
			// "+hRMS_HRRMTableName+" ADD "+hRMS_HRRM_PERSON_NUMBERColumnName+"
			// VARCHAR2(150)");}catch(SQLException
			// e1){if(log.isInfoEnabled())log.info("HR_PERSON_NUMBER Already Exists in HRMS_HRRM");}
			try {
				restoreDBStat.addBatch("TRUNCATE TABLE " + hRMS_HRRMTableName);
				restoreDBStat.addBatch("INSERT INTO " + hRMS_HRRMTableName + " SELECT * FROM HRMS_HRRM_snap");
				restoreDBStat.addBatch("TRUNCATE TABLE " + hRRMTableName_TG);
				restoreDBStat.addBatch("INSERT INTO " + hRRMTableName_TG + " SELECT * FROM HRRM_snap");
				restoreDBStat.executeBatch();
				oRAcon.commit();
			} catch (Exception ex) {
				log.error("Error:" + ex.fillInStackTrace());
			}
			// ## Restoring HRMS_HRRM Table As Before From HRMS_HRRM_snap ##
			if(log.isInfoEnabled())log.info("#  Changes Rolled Back in DB (if Any) #");
			if(log.isInfoEnabled())log.info("SQLException Occurred : " + e.getLocalizedMessage());
			// ## Email Notification Sending Business ##
			try {
				if(log.isInfoEnabled()){
					log.info(new EmailNotification().generateAndSendEmail("SQLSyntaxErrorException"));
				}
			} catch (AddressException e2) {
				e2.printStackTrace();
				if(log.isInfoEnabled())log.info(e2.getLocalizedMessage());
			} catch (MessagingException e2) {
				e2.printStackTrace();
				if(log.isInfoEnabled())log.info(e2.getLocalizedMessage());
			}
			// ## Email Notification Sending Business ##
			e.printStackTrace();

			log.error("Exception Occured : " + e.fillInStackTrace());
			try {
				oRAcon.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
				log.error("Exception Occured : " + e1.fillInStackTrace());
			}

		} catch (SQLException e) {// Catch #2
			e.printStackTrace();
			// ## Restoring HRMS_HRRM Table As Before From HRMS_HRRM_snap ##
			Statement restoreDBStat = oRAcon.createStatement(); // Initializing
																// Statement
																// Class for
																// Storing DML
																// String
			// try{ restoreDBStat.executeUpdate("ALTER TABLE
			// "+hRMS_HRRMTableName+" ADD "+hRMS_HRRM_PERSON_NUMBERColumnName+"
			// VARCHAR2(150)");}catch(SQLException
			// e1){if(log.isInfoEnabled())log.info("HR_PERSON_NUMBER Already Exists in HRMS_HRRM");}
			try {
				restoreDBStat.addBatch("TRUNCATE TABLE " + hRMS_HRRMTableName);
				restoreDBStat.addBatch("INSERT INTO " + hRMS_HRRMTableName + " SELECT * FROM HRMS_HRRM_snap");
				restoreDBStat.addBatch("TRUNCATE TABLE " + hRRMTableName_TG);
				restoreDBStat.addBatch("INSERT INTO " + hRRMTableName_TG + " SELECT * FROM HRRM_snap");
				restoreDBStat.executeBatch();
				oRAcon.commit();
			} catch (Exception ex) {
				log.error("Error:" + ex.fillInStackTrace());
			}
			// ## Restoring HRMS_HRRM Table As Before From HRMS_HRRM_snap ##
			if(log.isInfoEnabled())log.info("#  Changes Rolled Back in DB (if Any) #");
			log.error("SQLException Occured : " + e.fillInStackTrace());
			// ## Email Notification Sending Business ##
			try {
				if(log.isInfoEnabled()){
					log.info(new EmailNotification().generateAndSendEmail("SQLException"));
				}
			} catch (AddressException e2) {
				e2.printStackTrace();
				if(log.isInfoEnabled())log.info(e2.getLocalizedMessage());
			} catch (MessagingException e2) {
				e2.printStackTrace();
				if(log.isInfoEnabled())log.info(e2.getLocalizedMessage());
			}
			// ## Email Notification Sending Business ##

			log.error("Exception Occured : " + e.fillInStackTrace());
			try {
				oRAcon.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
				log.error("Exception Occured : " + e1.fillInStackTrace());
			}

		} catch (Exception e) {// Catch #3

			// ## Restoring HRMS_HRRM Table As Before From HRMS_HRRM_snap ##
			e.printStackTrace();
			Statement restoreDBStat = oRAcon.createStatement(); // Initializing
																// Statement
																// Class for
																// Storing DML
																// String
			// try{ restoreDBStat.executeUpdate("ALTER TABLE
			// "+hRMS_HRRMTableName+" ADD "+hRMS_HRRM_PERSON_NUMBERColumnName+"
			// VARCHAR2(150)");}catch(SQLException
			// e1){if(log.isInfoEnabled())log.info("HR_PERSON_NUMBER Already Exists in HRMS_HRRM");}
			try {
				restoreDBStat.addBatch("TRUNCATE TABLE " + hRMS_HRRMTableName);
				restoreDBStat.addBatch("INSERT INTO " + hRMS_HRRMTableName + " SELECT * FROM HRMS_HRRM_snap");
				restoreDBStat.addBatch("TRUNCATE TABLE " + hRRMTableName_TG);
				restoreDBStat.addBatch("INSERT INTO " + hRRMTableName_TG + " SELECT * FROM HRRM_snap");
				restoreDBStat.executeBatch();
				oRAcon.commit();
			} catch (Exception ex) {
				log.error("Error:" + ex.fillInStackTrace());
			}
			// ## Restoring HRMS_HRRM Table As Before From HRMS_HRRM_snap ##
			if(log.isInfoEnabled())log.info("#  Changes Rolled Back in DB (if Any) #");
			log.error("Exception Occured : " + e.fillInStackTrace());
			// ## Email Notification Sending Business ##
			try {
				if(log.isInfoEnabled()){
					log.info(new EmailNotification().generateAndSendEmail("Exception"));
				}
			} catch (AddressException e2) {
				e2.printStackTrace();
				if(log.isInfoEnabled())log.info(e2.getLocalizedMessage());
			} catch (MessagingException e2) {
				e2.printStackTrace();
				if(log.isInfoEnabled())log.info(e2.getLocalizedMessage());
			}
			// ## Email Notification Sending Business ##
			e.printStackTrace();

			log.error("Exception Occured : " + e.fillInStackTrace());
			try {
				oRAcon.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
				log.error("Exception Occured : " + e1.fillInStackTrace());
			}

		}

		// */
	}// main method

}// main class
