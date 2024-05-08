package cRUDUtilityClass;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.apache.log4j.Logger;

import cripUtilityPackage.AES;
import dDReplication.DBreplication;
import dDReplication.MasterProgram;
//import cripUtilityPackage.CripUtility;
import dDReplication.MasterSlaveDBreplication;
import emailSMTPPackage.EmailNotification;
import oracle.jdbc.internal.OraclePreparedStatement;

public class CopyTableContent {
	
	static ResourceBundle rBProps = MasterSlaveDBreplication.rBProps;
	
	
	final static Logger log = Logger.getLogger(CopyTableContent.class.getName());

	// This Method is Used in Application for hrupm.hrrm table
	public void copyHRRM2HRRM(Connection sourceCon, Connection targetCon) {
	
		
		try{
			PreparedStatement targetInsertSQL = targetCon
					.prepareStatement(
				
							"INSERT INTO "+rBProps.getString("hRRMTableName_TG") //hrupm.hrrm
							+ " VALUES (?,?,?,?,?,?,?)"

							);
			// Perform select / open Cursor
			Statement selectViewStm = sourceCon.createStatement();
			ResultSet sourceResultSet = selectViewStm.executeQuery("SELECT * FROM "+rBProps.getString("hRRMTableName_IN"));
			
			int currentBatchSize = 0;
			int mAXBATCHSIZE = 1000;
			while ( sourceResultSet.next()) {
				// Set into INSERT the values we SELECTED
				targetInsertSQL.setString(1, sourceResultSet.getString(rBProps.getString("pERSON_NUMBER_HRRM_TG")));
				targetInsertSQL.setString(2, sourceResultSet.getString(rBProps.getString("rESPONSIBILITY_NAME_HRRM_TG")));
				  targetInsertSQL.setNull(3, Types.VARCHAR);
				targetInsertSQL.setString(4, sourceResultSet.getString(rBProps.getString("nEW_LOB_HRRM_TG")));
				targetInsertSQL.setString(5, sourceResultSet.getString(rBProps.getString("nEW_LOC_HRRM_TG")));
				targetInsertSQL.setString(6, sourceResultSet.getString(rBProps.getString("nEW_cC_HRRM_TG")));
				  targetInsertSQL.setNull(7, Types.VARCHAR);
				//targetInsertSQL.setNull(60, Types.VARCHAR);
				targetInsertSQL.addBatch();
				
				if (++currentBatchSize % mAXBATCHSIZE == 0) {
					targetInsertSQL.executeBatch();
					//targetCon.commit();
				}// if Ends
			}// While Ends
			if (++currentBatchSize % mAXBATCHSIZE != 0) {
				targetInsertSQL.executeBatch();
				//targetCon.commit();
			}
			
		} catch (SQLException e) {
			log.error("SQLException : " + e.getMessage()) ;
			e.printStackTrace();
		}

	}
	
	// This Method is Used in Application
	public void copyHRMS2HRMS_HRRM(Connection sourceCon, Connection targetCon) throws SQLException {

		if(log.isInfoEnabled())log.info("Inside copyHRMS2HRMS_HRRM");
		try {
			
			// 60 Question Marks"?"
			PreparedStatement targetInsertSQL = targetCon
					.prepareStatement(
					
					"INSERT INTO "+rBProps.getString("hRMS_HRRMTableName") //HRUPM.HRMS_HRRM
							+ " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

					);
			// Perform select / open Cursor
			Statement selectViewStm = sourceCon.createStatement();
			ResultSet sourceResultSet = selectViewStm.executeQuery("SELECT * FROM "+rBProps.getString("hRMSTableName")); //APPS.KBANK_OUTSOURCED_V
			
			int currentBatchSize = 0;
			int mAXBATCHSIZE = 1000;
			while ( sourceResultSet.next()) {
				String dataTrace = "";
				// Set into INSERT the values we SELECTED
				//targetInsertSQL.setString(1, ((ResultSet) sourceResultSet).getString("EMPLOYEE_NUMBER"));
				targetInsertSQL.setString(1, sourceResultSet.getString("COMPANY")+sourceResultSet.getString("ORACLE_EMPLOYEE_NUMBER"));
				dataTrace = dataTrace + "," +sourceResultSet.getString("COMPANY");
				//targetInsertSQL.setInt(2, sourceResultSet.getInt("ORACLE_EMPLOYEE_NUMBER"));
				targetInsertSQL.setString(2, sourceResultSet.getString("ORACLE_EMPLOYEE_NUMBER"));
				dataTrace = dataTrace + "," +sourceResultSet.getString("ORACLE_EMPLOYEE_NUMBER");
				targetInsertSQL.setString(3, sourceResultSet.getString("COMPANY"));
				dataTrace = dataTrace + "," +sourceResultSet.getString("COMPANY");
				targetInsertSQL.setString(4, sourceResultSet.getString("PERSON_ID"));
				dataTrace = dataTrace + "," +sourceResultSet.getString("PERSON_ID");
				targetInsertSQL.setString(5, sourceResultSet.getString("ATTRIBUTE3"));
				dataTrace = dataTrace + "," +sourceResultSet.getString("ATTRIBUTE3");
				targetInsertSQL.setString(6, sourceResultSet.getString("PREFIX"));
				dataTrace = dataTrace + "," +sourceResultSet.getString("PREFIX");
				targetInsertSQL.setString(7, sourceResultSet.getString("FIRST_NAME"));
				dataTrace = dataTrace + "," +sourceResultSet.getString("FIRST_NAME");
				targetInsertSQL.setString(8, sourceResultSet.getString("MIDDLE_NAMES"));
				dataTrace = dataTrace + "," +sourceResultSet.getString("MIDDLE_NAMES");
				targetInsertSQL.setString(9, sourceResultSet.getString("LAST_NAME"));
				dataTrace = dataTrace + "," +sourceResultSet.getString("MIDDLE_NAMES");
				targetInsertSQL.setString(10, sourceResultSet.getString("LAST_NAME"));
				dataTrace = dataTrace + "," +sourceResultSet.getString("MIDDLE_NAMES");
				targetInsertSQL.setString(11, sourceResultSet.getString("GENDER"));
				dataTrace = dataTrace + "," +sourceResultSet.getString("GENDER");
				targetInsertSQL.setDate(12, sourceResultSet.getDate("DOB"));
				dataTrace = dataTrace + "," +sourceResultSet.getString("DOB");
				// MARITAL_STATUS is actually CHAR(10) BYTE
				targetInsertSQL.setString(13, sourceResultSet.getString("MARITAL_STATUS"));
				dataTrace = dataTrace + "," +sourceResultSet.getString("MARITAL_STATUS");
				targetInsertSQL.setString(14, null);
				dataTrace = dataTrace + "," +sourceResultSet.getString("PADDRESS1");
				targetInsertSQL.setString(15, null);
				dataTrace = dataTrace + "," +sourceResultSet.getString("PADDRESS2");
				targetInsertSQL.setString(16, null);
				dataTrace = dataTrace + "," +sourceResultSet.getString("PADDRESS3");
				targetInsertSQL.setString(17, sourceResultSet.getString("PCITY"));
				dataTrace = dataTrace + "," +sourceResultSet.getString("PCITY");
				// PPIN is actually a CHAR(20) BYTE)
				targetInsertSQL.setString(18, sourceResultSet.getString("PPIN"));
				dataTrace = dataTrace + "," +sourceResultSet.getString("PPIN");
				targetInsertSQL.setString(19, null);
				dataTrace = dataTrace + "," +sourceResultSet.getString("TADDRESS1");
				targetInsertSQL.setString(20, null);
				dataTrace = dataTrace + "," +sourceResultSet.getString("TADDRESS2");
				targetInsertSQL.setString(21, null);
				dataTrace = dataTrace + "," +sourceResultSet.getString("TADDRESS3");
				targetInsertSQL.setString(22, sourceResultSet.getString("TCITY"));
				dataTrace = dataTrace + "," +sourceResultSet.getString("TCITY");
				// TPIN is actually a CHAR(6) BYTE)
				targetInsertSQL.setString(23, sourceResultSet.getString("TPIN"));
				dataTrace = dataTrace + "," +sourceResultSet.getString("TPIN");
				targetInsertSQL.setString(24, sourceResultSet.getString("SUPERVISOR_EMP_NO"));
				dataTrace = dataTrace + "," +sourceResultSet.getString("SUPERVISOR_EMP_NO");
				targetInsertSQL.setString(25, sourceResultSet.getString("SUP_COMPANY"));
				dataTrace = dataTrace + "," +sourceResultSet.getString("SUP_COMPANY");
				targetInsertSQL.setDate(26, sourceResultSet.getDate("DOJ"));
				dataTrace = dataTrace + "," +sourceResultSet.getString("DOJ");
				targetInsertSQL.setString(27, sourceResultSet.getString("FATHER_HUSBAND_NAME"));
				dataTrace = dataTrace + "," +sourceResultSet.getString("FATHER_HUSBAND_NAME");
				targetInsertSQL.setString(28, sourceResultSet.getString("DIVISION"));
				dataTrace = dataTrace + "," +sourceResultSet.getString("DIVISION");
				targetInsertSQL.setString(29, sourceResultSet.getString("FUNCTION"));
				dataTrace = dataTrace + "," +sourceResultSet.getString("FUNCTION");
				targetInsertSQL.setBigDecimal(30, sourceResultSet.getBigDecimal("FUNCTION_ID"));
				dataTrace = dataTrace + "," +sourceResultSet.getString("FUNCTION_ID");
				targetInsertSQL.setBigDecimal(31, sourceResultSet.getBigDecimal("DESIGNATION_ID"));
				dataTrace = dataTrace + "," +sourceResultSet.getString("DESIGNATION_ID");
				// CHAR(10 BYTE)
				targetInsertSQL.setString(32, sourceResultSet.getString("PAYROLL_ID"));
				dataTrace = dataTrace + "," +sourceResultSet.getString("PAYROLL_ID");
				targetInsertSQL.setString(33, sourceResultSet.getString("DESIGNATION_LABEL_ID"));
				dataTrace = dataTrace + "," +sourceResultSet.getString("DESIGNATION_LABEL_ID");
				targetInsertSQL.setString(34, sourceResultSet.getString("ROLE"));
				dataTrace = dataTrace + "," +sourceResultSet.getString("ROLE");
				targetInsertSQL.setString(35, sourceResultSet.getString("LOC_CODE"));
				dataTrace = dataTrace + "," +sourceResultSet.getString("LOC_CODE");
				targetInsertSQL.setString(36, sourceResultSet.getString("LOC_CODE"));
				dataTrace = dataTrace + "," +sourceResultSet.getString("LOC_CODE");
				targetInsertSQL.setDate(37, sourceResultSet.getDate("DATE_EMPLOYEE_DATA_VERIFIED"));
				dataTrace = dataTrace + "," +sourceResultSet.getString("DATE_EMPLOYEE_DATA_VERIFIED");
				targetInsertSQL.setString(38, sourceResultSet.getString("LOB_CODE"));
				dataTrace = dataTrace + "," +sourceResultSet.getString("LOB_CODE");
				targetInsertSQL.setString(39, sourceResultSet.getString("LOB"));
				dataTrace = dataTrace + "," +sourceResultSet.getString("LOB");
				targetInsertSQL.setString(40, sourceResultSet.getString("CC_CODE"));
				dataTrace = dataTrace + "," +sourceResultSet.getString("CC_CODE");
				targetInsertSQL.setString(41, sourceResultSet.getString("CC_NAME"));
				dataTrace = dataTrace + "," +sourceResultSet.getString("CC_NAME");
				targetInsertSQL.setString(42, sourceResultSet.getString("CC_NAME"));
				dataTrace = dataTrace + "," +sourceResultSet.getString("CC_NAME");
				targetInsertSQL.setString(43, sourceResultSet.getString("DOMAIN_LOGIN_ID"));
				dataTrace = dataTrace + "," +sourceResultSet.getString("DOMAIN_LOGIN_ID");
				targetInsertSQL.setDate(44, sourceResultSet.getDate("LAST_WORKING_DATE"));
				dataTrace = dataTrace + "," +sourceResultSet.getString("LAST_WORKING_DATE");
				targetInsertSQL.setString(45, sourceResultSet.getString("RM_NAME"));
				dataTrace = dataTrace + "," +sourceResultSet.getString("RM_NAME");
				targetInsertSQL.setString(46, sourceResultSet.getString("RM_NAME"));
				dataTrace = dataTrace + "," +sourceResultSet.getString("RM_NAME");
				targetInsertSQL.setString(47, sourceResultSet.getString("RM_NAME"));
				dataTrace = dataTrace + "," +sourceResultSet.getString("RM_NAME");
				targetInsertSQL.setString(48, sourceResultSet.getString("KPO_CODE"));
				dataTrace = dataTrace + "," +sourceResultSet.getString("KPO_CODE");
				targetInsertSQL.setString(49, sourceResultSet.getString("SUPERVISOR_NAME"));
				dataTrace = dataTrace + "," +sourceResultSet.getString("SUPERVISOR_NAME");
				// Char(13 BYTE)
				targetInsertSQL.setString(50, sourceResultSet.getString("MOBILE_NUMBER"));
				dataTrace = dataTrace + "," +sourceResultSet.getString("MOBILE_NUMBER");
				targetInsertSQL.setDate(51, sourceResultSet.getDate("ASSIGNMENT_CHANGE_DATE"));
				dataTrace = dataTrace + "," +sourceResultSet.getString("ASSIGNMENT_CHANGE_DATE");
				targetInsertSQL.setString(52, sourceResultSet.getString("LOC_CODE_NEW"));
				dataTrace = dataTrace + "," +sourceResultSet.getString("LOC_CODE_NEW");
				targetInsertSQL.setString(53, sourceResultSet.getString("LOC_CODE_NEW"));
				dataTrace = dataTrace + "," +sourceResultSet.getString("LOC_CODE_NEW");
				targetInsertSQL.setString(54, sourceResultSet.getString("LOC_CODE_NEW"));
				dataTrace = dataTrace + "," +sourceResultSet.getString("LOC_CODE_NEW");
				targetInsertSQL.setString(55, sourceResultSet.getString("LOC_CODE_NEW"));
				dataTrace = dataTrace + "," +sourceResultSet.getString("LOC_CODE_NEW");
				// CHAR(10 BYTE)
				targetInsertSQL.setString(56, sourceResultSet.getString("PAN_NO"));
				dataTrace = dataTrace + "," +sourceResultSet.getString("PAN_NO");
				targetInsertSQL.setDate(57, sourceResultSet.getDate("DOJ_KOTAK_GROUP"));
				dataTrace = dataTrace + "," +sourceResultSet.getString("DOJ_KOTAK_GROUP");
				targetInsertSQL.setString(58, sourceResultSet.getString("RM_EMAIL"));
				dataTrace = dataTrace + "," +sourceResultSet.getString("RM_EMAIL");
				targetInsertSQL.setString(59, sourceResultSet.getString("GRADE_ID"));
				dataTrace = dataTrace + "," +sourceResultSet.getString("GRADE_ID");
				
				targetInsertSQL.setNull(60, Types.VARCHAR);
				targetInsertSQL.addBatch();
				if (++currentBatchSize % mAXBATCHSIZE == 0) {
					targetInsertSQL.executeBatch();
					//targetCon.commit();
				}// if Ends
			}// While Ends
			if (++currentBatchSize % mAXBATCHSIZE != 0) {
				targetInsertSQL.executeBatch();
				//targetCon.commit();
			}
		} catch (SQLException e) {
			if(log.isInfoEnabled())log.info("Inside copyHRMS2HRMS_HRRM method");
			log.error("SQL Exception : " + e.getMessage());
			e.printStackTrace();
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
		}
			

	}// method ends
	
	//this method is used in the application for hrupm.hrms_hrrm table
	public void copyHRMS_HRRMData(Connection sourceCon, Connection targetCon) throws SQLException {

		if(log.isInfoEnabled())log.info("Inside copyHRMS_HRRMData");
		try {
			
			// 60 Question Marks"?"
			PreparedStatement targetInsertSQL = targetCon
					.prepareStatement(
					
					"INSERT INTO "+rBProps.getString("hRMS_HRRMTableName") //HRUPM.HRMS_HRRM
							+ " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

					);
			// Perform select / open Cursor
			
			Statement selectViewStm = sourceCon.createStatement();
			
			String selectQuery = "SELECT * FROM "+rBProps.getString("hRMSTableName") + " ORDER BY ORACLE_EMPLOYEE_NUMBER"; //APPS.KBANK_OUTSOURCED_V
			
			if(log.isInfoEnabled())log.info("Query - " + selectQuery);
			
			ResultSet sourceResultSet = selectViewStm.executeQuery(selectQuery);
			
			int currentBatchSize = 0;
			int mAXBATCHSIZE = 1000;
			
			int count = 0;
			
			while ( sourceResultSet.next()) {
				
				if(log.isTraceEnabled())log.trace("Count - "+(++count)+" Domain Id - " +  sourceResultSet.getString("COMPANY")+sourceResultSet.getString("ORACLE_EMPLOYEE_NUMBER"));
				
				String dataTrace = "";
				// if(log.isTraceEnabled())log.trace
				// Set into INSERT the values we SELECTED
				//targetInsertSQL.setString(1, ((ResultSet) sourceResultSet).getString("EMPLOYEE_NUMBER"));
				targetInsertSQL.setString(1, sourceResultSet.getString("COMPANY")+sourceResultSet.getString("ORACLE_EMPLOYEE_NUMBER"));
				//targetInsertSQL.setInt(2, sourceResultSet.getInt("ORACLE_EMPLOYEE_NUMBER"));
				dataTrace = dataTrace + ", " +sourceResultSet.getString("COMPANY");
				targetInsertSQL.setString(2, sourceResultSet.getString("ORACLE_EMPLOYEE_NUMBER"));
				dataTrace = dataTrace + ", " +sourceResultSet.getString("ORACLE_EMPLOYEE_NUMBER");
				targetInsertSQL.setString(3, sourceResultSet.getString("COMPANY"));
				dataTrace = dataTrace + ", " +sourceResultSet.getString("COMPANY");
				targetInsertSQL.setString(4, sourceResultSet.getString("PERSON_ID"));
				dataTrace = dataTrace + ", " +sourceResultSet.getString("PERSON_ID");
				targetInsertSQL.setString(5, sourceResultSet.getString("ATTRIBUTE3"));
				dataTrace = dataTrace + ", " +sourceResultSet.getString("ATTRIBUTE3");
				targetInsertSQL.setString(6, sourceResultSet.getString("PREFIX"));
				dataTrace = dataTrace + ", " +sourceResultSet.getString("PREFIX");
				targetInsertSQL.setString(7, sourceResultSet.getString("FIRST_NAME"));
				dataTrace = dataTrace + ", " +sourceResultSet.getString("FIRST_NAME");
				targetInsertSQL.setString(8, sourceResultSet.getString("MIDDLE_NAMES"));
				dataTrace = dataTrace + ", " +sourceResultSet.getString("MIDDLE_NAMES");
				targetInsertSQL.setString(9, sourceResultSet.getString("LAST_NAME"));
				dataTrace = dataTrace + ", " +sourceResultSet.getString("LAST_NAME");
				targetInsertSQL.setString(10, sourceResultSet.getString("LAST_NAME"));
				dataTrace = dataTrace + ", " +sourceResultSet.getString("LAST_NAME");
				targetInsertSQL.setString(11, sourceResultSet.getString("GENDER"));
				dataTrace = dataTrace + ", " +sourceResultSet.getString("GENDER");
				targetInsertSQL.setDate(12, sourceResultSet.getDate("DOB"));
				dataTrace = dataTrace + ", " +sourceResultSet.getString("DOB");
				// MARITAL_STATUS is actually CHAR(10) BYTE
				targetInsertSQL.setString(13, sourceResultSet.getString("MARITAL_STATUS"));
				dataTrace = dataTrace + ", " +sourceResultSet.getString("MARITAL_STATUS");
				targetInsertSQL.setString(14, null);
				dataTrace = dataTrace + ", " +sourceResultSet.getString("PADDRESS1");
				targetInsertSQL.setString(15, null);
				dataTrace = dataTrace + ", " +sourceResultSet.getString("PADDRESS2");
				targetInsertSQL.setString(16, null);
				dataTrace = dataTrace + ", " +sourceResultSet.getString("PADDRESS3");
				targetInsertSQL.setString(17, null);
				dataTrace = dataTrace + ", " +sourceResultSet.getString("PCITY");
				// PPIN is actually a CHAR(20) BYTE)
				targetInsertSQL.setString(18, sourceResultSet.getString("PPIN"));
				dataTrace = dataTrace + ", " +sourceResultSet.getString("PPIN");
				targetInsertSQL.setString(19, null);
				
				dataTrace = dataTrace + ", " +sourceResultSet.getString("TADDRESS1");
				targetInsertSQL.setString(20, null);
				
				dataTrace = dataTrace + ", " +sourceResultSet.getString("TADDRESS2");
				targetInsertSQL.setString(21, null);
				dataTrace = dataTrace + ", " +sourceResultSet.getString("TADDRESS3");
				targetInsertSQL.setString(22, null);
				dataTrace = dataTrace + ", " +sourceResultSet.getString("TCITY");
				// TPIN is actually a CHAR(6) BYTE)
				targetInsertSQL.setString(23, sourceResultSet.getString("TPIN"));
				dataTrace = dataTrace + ", " +sourceResultSet.getString("TPIN");
				targetInsertSQL.setString(24, sourceResultSet.getString("SUPERVISOR_EMP_NO"));
				dataTrace = dataTrace + ", " +sourceResultSet.getString("SUPERVISOR_EMP_NO");
				targetInsertSQL.setString(25, sourceResultSet.getString("SUP_COMPANY"));
				dataTrace = dataTrace + ", " +sourceResultSet.getString("SUP_COMPANY");
				targetInsertSQL.setDate(26, sourceResultSet.getDate("DOJ"));
				dataTrace = dataTrace + ", " +sourceResultSet.getString("DOJ");
				targetInsertSQL.setString(27, sourceResultSet.getString("FATHER_HUSBAND_NAME"));
				dataTrace = dataTrace + ", " +sourceResultSet.getString("FATHER_HUSBAND_NAME");
				targetInsertSQL.setString(28, sourceResultSet.getString("DIVISION"));
				dataTrace = dataTrace + ", " +sourceResultSet.getString("DIVISION");
				targetInsertSQL.setString(29, sourceResultSet.getString("FUNCTION"));
				dataTrace = dataTrace + ", " +sourceResultSet.getString("FUNCTION");
				targetInsertSQL.setBigDecimal(30, sourceResultSet.getBigDecimal("FUNCTION_ID"));
				dataTrace = dataTrace + ", " +sourceResultSet.getBigDecimal("FUNCTION_ID");
				targetInsertSQL.setBigDecimal(31, sourceResultSet.getBigDecimal("DESIGNATION_ID"));
				dataTrace = dataTrace + ", " +sourceResultSet.getBigDecimal("DESIGNATION_ID");
				// CHAR(10 BYTE)
				targetInsertSQL.setString(32, sourceResultSet.getString("PAYROLL_ID"));
				dataTrace = dataTrace + ", " +sourceResultSet.getString("PAYROLL_ID");
				targetInsertSQL.setString(33, sourceResultSet.getString("DESIGNATION_LABEL_ID"));
				dataTrace = dataTrace + ", " +sourceResultSet.getString("DESIGNATION_LABEL_ID");
				targetInsertSQL.setString(34, sourceResultSet.getString("ROLE"));
				dataTrace = dataTrace + ", " +sourceResultSet.getString("ROLE");
				targetInsertSQL.setString(35, sourceResultSet.getString("LOC_CODE"));
				dataTrace = dataTrace + ", " +sourceResultSet.getString("LOC_CODE");
				targetInsertSQL.setString(36, sourceResultSet.getString("LOC_CODE"));
				dataTrace = dataTrace + ", " +sourceResultSet.getString("LOC_CODE");
				targetInsertSQL.setDate(37, sourceResultSet.getDate("DATE_EMPLOYEE_DATA_VERIFIED"));
				dataTrace = dataTrace + ", " +sourceResultSet.getString("DATE_EMPLOYEE_DATA_VERIFIED");
				targetInsertSQL.setString(38, sourceResultSet.getString("LOB_CODE"));
				dataTrace = dataTrace + ", " +sourceResultSet.getString("LOB_CODE");
				targetInsertSQL.setString(39, sourceResultSet.getString("LOB"));
				dataTrace = dataTrace + ", " +sourceResultSet.getString("LOB");
				targetInsertSQL.setString(40, sourceResultSet.getString("CC_CODE"));
				dataTrace = dataTrace + ", " +sourceResultSet.getString("CC_CODE");
				targetInsertSQL.setString(41, sourceResultSet.getString("CC_NAME"));
				dataTrace = dataTrace + ", " +sourceResultSet.getString("CC_NAME");
				targetInsertSQL.setString(42, sourceResultSet.getString("CC_NAME"));
				dataTrace = dataTrace + ", " +sourceResultSet.getString("CC_NAME");
				targetInsertSQL.setString(43, sourceResultSet.getString("DOMAIN_LOGIN_ID"));
				dataTrace = dataTrace + ", " +sourceResultSet.getString("DOMAIN_LOGIN_ID");
				targetInsertSQL.setDate(44, sourceResultSet.getDate("LAST_WORKING_DATE"));
				dataTrace = dataTrace + ", " +sourceResultSet.getString("LAST_WORKING_DATE");
				targetInsertSQL.setString(45, sourceResultSet.getString("RM_NAME"));
				dataTrace = dataTrace + ", " +sourceResultSet.getString("RM_NAME");
				targetInsertSQL.setString(46, sourceResultSet.getString("RM_NAME"));
				dataTrace = dataTrace + ", " +sourceResultSet.getString("RM_NAME");
				targetInsertSQL.setString(47, sourceResultSet.getString("RM_NAME"));
				dataTrace = dataTrace + ", " +sourceResultSet.getString("RM_NAME");
				targetInsertSQL.setString(48, sourceResultSet.getString("KPO_CODE"));
				dataTrace = dataTrace + ", " +sourceResultSet.getString("KPO_CODE");
				targetInsertSQL.setString(49, sourceResultSet.getString("SUPERVISOR_NAME"));
				dataTrace = dataTrace + ", " +sourceResultSet.getString("SUPERVISOR_NAME");
				// Char(13 BYTE)
				targetInsertSQL.setString(50, sourceResultSet.getString("MOBILE_NUMBER"));
				dataTrace = dataTrace + ", " +sourceResultSet.getString("MOBILE_NUMBER");
				targetInsertSQL.setDate(51, sourceResultSet.getDate("ASSIGNMENT_CHANGE_DATE"));
				dataTrace = dataTrace + ", " +sourceResultSet.getString("ASSIGNMENT_CHANGE_DATE");
				targetInsertSQL.setString(52, sourceResultSet.getString("LOC_CODE_NEW"));
				dataTrace = dataTrace + ", " +sourceResultSet.getString("LOC_CODE_NEW");
				targetInsertSQL.setString(53, sourceResultSet.getString("LOC_CODE_NEW"));
				dataTrace = dataTrace + ", " +sourceResultSet.getString("LOC_CODE_NEW");
				targetInsertSQL.setString(54, sourceResultSet.getString("LOC_CODE_NEW"));
				dataTrace = dataTrace + ", " +sourceResultSet.getString("LOC_CODE_NEW");
				targetInsertSQL.setString(55, sourceResultSet.getString("LOC_CODE_NEW"));
				dataTrace = dataTrace + ", " +sourceResultSet.getString("LOC_CODE_NEW");
				// CHAR(10 BYTE)
				targetInsertSQL.setString(56, sourceResultSet.getString("PAN_NO"));
				dataTrace = dataTrace + ", " +sourceResultSet.getString("PAN_NO");
				targetInsertSQL.setDate(57, sourceResultSet.getDate("DOJ_KOTAK_GROUP"));
				dataTrace = dataTrace + ", " +sourceResultSet.getString("DOJ_KOTAK_GROUP");
				targetInsertSQL.setString(58, sourceResultSet.getString("RM_EMAIL"));
				dataTrace = dataTrace + ", " +sourceResultSet.getString("RM_EMAIL");
				targetInsertSQL.setString(59, sourceResultSet.getString("GRADE_ID"));
				dataTrace = dataTrace + ", " +sourceResultSet.getString("GRADE_ID");
				String lob = sourceResultSet.getString("LOB_CODE");
				String loc = sourceResultSet.getString("LOC_CODE");
				String cc = sourceResultSet.getString("CC_CODE");
				String query = "Select * from "+rBProps.getString("hRRMTableName_TG")+ " where NEW_LOB='"+lob+"' and NEW_LOC='"+loc+"' and NEW_CC='"+cc+"'"; //HRUPM.HRRM
				Statement stat = targetCon.createStatement();
				ResultSet set = stat.executeQuery(query);
				
				ArrayList<Map<String, Object>> data = MasterProgram.resultSetToMapList(set);
				try{
					stat.close();
					stat = null;
				}catch(Exception e){
					log.error("Exception caught :" + e.getMessage());
				}
				
				String hrPerson = "";
				
				for (Map<String, Object> map : data) {
					String output = (String) map.get(rBProps.getString("pERSON_NUMBER_HRRM_TG"));
					if(output != null && !output.isEmpty()) {
						hrPerson = hrPerson + output+" ,";
					}
				}
				
				//HRUPM.CUST_ROLES_USR_MAPPING
				query = "Select PERSON_NUMBER from "+rBProps.getString("cHRRM_TableName")+" where lob_code='"+lob+"' and loc_code='"+loc+"' and cc_code = '"+cc+"' and last_working_date >= LOCALTIMESTAMP and is_active = '1' and inclusion_ecxlusion = 'I'";
				
				
				stat = targetCon.createStatement();
				
				set = stat.executeQuery(query);
				
				data = MasterProgram.resultSetToMapList(set);
				
				try{
					stat.close();
					stat = null;
				}catch(Exception e){
					log.error("Exception  : " + e.getMessage());
				}
				
				for (Map<String, Object> map : data) {
					String output = (String) map.get(rBProps.getString("eMPLOYEE_NUMBER_CHRRM"));
					if(output != null && !output.isEmpty()) {
						hrPerson = hrPerson + output+" ,";
					}
				}
				
				query = "Select PERSON_NUMBER from "+rBProps.getString("cHRRM_TableName")+" where lob_code <> '"+lob+"' and loc_code <> '"+loc+"' and cc_code <> '"+cc+"' and last_working_date >= LOCALTIMESTAMP and is_active = '1' and inclusion_ecxlusion = 'E'";
				
				
				stat = targetCon.createStatement();
				
				set = stat.executeQuery(query);
				
				data = MasterProgram.resultSetToMapList(set);
				
				try{
					stat.close();
					stat = null;
				}catch(Exception e){
					log.error("Exception  : " + e.getMessage());
				}
				
				for (Map<String, Object> map : data) {
					String output = (String) map.get(rBProps.getString("eMPLOYEE_NUMBER_CHRRM"));
					if(output != null && !output.isEmpty()) {
						hrPerson = hrPerson + output+" ,";
					}
				}
				
				query = "Select PERSON_NUMBER from "+rBProps.getString("cHRRM_TableName")+" where LOB_CODE = '0000' and LOC_CODE = '0000' and CC_CODE = '0000' and last_working_date >= LOCALTIMESTAMP and is_active = '1'";
				
				stat = targetCon.createStatement();
				
				set = stat.executeQuery(query);
				
				data = MasterProgram.resultSetToMapList(set);
				
				try{
					stat.close();
					stat = null;
				}catch(Exception e){
					
					log.error("Exception : " + e.getMessage());
				}
				
				for (Map<String, Object> map : data) {
					String output = (String) map.get(rBProps.getString("eMPLOYEE_NUMBER_CHRRM"));
					if(output != null && !output.isEmpty()) {
						hrPerson = hrPerson + output+" ,";
					}
				}
				
				if(!hrPerson.isEmpty()){
					hrPerson = hrPerson.substring(0, hrPerson.length()-2);
					//System.out.println("HRPERSON - "+hrPerson);
					if(log.isTraceEnabled())log.trace("HRPERSON - "+hrPerson);
				}else{
				//	System.out.println("HRPERSON - "+hrPerson);
					if(log.isTraceEnabled())log.trace("HRPERSON - "+hrPerson);
				}
				
				
				
				targetInsertSQL.setString(60, hrPerson);
				
				if(log.isTraceEnabled())log.trace("Insert Data - " + dataTrace);
				
				
				
				targetInsertSQL.addBatch();
				
				if(log.isTraceEnabled())log.trace("Insert Query -"+ targetInsertSQL.unwrap(OraclePreparedStatement.class));
				if (++currentBatchSize % mAXBATCHSIZE == 0) {
					
					try{
						targetInsertSQL.executeBatch();
					}catch(Exception e){
						log.error("Exception : " + e.getMessage());
						e.printStackTrace();
					}
					
					//targetCon.commit();
				}// if Ends
			}// While Ends
			if (++currentBatchSize % mAXBATCHSIZE != 0) {
				
				try{
					targetInsertSQL.executeBatch();
				}catch(Exception e){
					log.error("Exception : " + e.getMessage());
					e.printStackTrace();
				}
				
				//targetCon.commit();
			}
		} 
		
		catch (SQLException e) {
			if(log.isInfoEnabled())log.info("Inside copyHRMS_HRRMData method");
			log.error("SQL Exception : " + e);
			e.printStackTrace();
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
		}

	}// method ends

}





/* Refer this if Select SQL Needs the Colunm from HRMS
 * 							+" ( "
							+ "EMPLOYEE_NUMBER,"
							+ "ORACLE_EMPLOYEE_NUMBER,"
							+ "COMPANY,"
							+ "PERSON_ID,"
							+ "ATTRIBUTE3,"
							+ "PREFIX,"
							+ "FIRST_NAME,"
							+ "MIDDLE_NAMES,"
							+ "LAST_NAME,"
							+ "EMAIL_ADDRESS,"
							+ "GENDER,"
							+ "DOB,"
							+ "MARITAL_STATUS,"
							+ "PADDRESS1,"
							+ "PADDRESS2,"
							+ "PADDRESS3,"
							+ "PCITY,"
							+ "PPIN,"
							+ "TADDRESS1,"
							+ "TADDRESS2,"
							+ "TADDRESS3,"
							+ "TCITY,"
							+ "TPIN,"
							+ "SUPERVISOR_EMP_NO,"
							+ "SUP_COMPANY,"
							+ "DOJ,"
							+ "FATHER_HUSBAND_NAME,"
							+ "DIVISION,"
							+ "FUNCTION,"
							+ "FUNCTION_ID,"
							+ "DESIGNATION_ID,"
							+ "PAYROLL_ID,"
							+ "DESIGNATION_LABEL_ID,"
							+ "ROLE,"
							+ "LOC_CODE,"
							+ "LOCATION_NAME,"
							+ "DATE_EMPLOYEE_DATA_VERIFIED,"
							+ "LOB_CODE,"
							+ "LOB,"
							+ "CC_CODE,"
							+ "CC_NAME,"
							+ "CATEGORY,"
							+ "DOMAIN_LOGIN_ID,"
							+ "LAST_WORKING_DATE,"
							+ "RM_NAME,"
							+ "SUPERVISOR_NO,"
							+ "SOURCE,"
							+ "KPO_CODE,"
							+ "SUPERVISOR_NAME,"
							+ "MOBILE_NUMBER,"
							+ "ASSIGNMENT_CHANGE_DATE,"
							+ "LOC_CODE_NEW,"
							+ "ING_EMP_TYPE,"
							+ "SEGMENT,"
							+ "ROLE_ID,"
							+ "PAN_NO,"
							+ "DOJ_KOTAK_GROUP,"
							+ "RM_EMAIL,"
							+ "GRADE_ID"
							+" ) "
 */


