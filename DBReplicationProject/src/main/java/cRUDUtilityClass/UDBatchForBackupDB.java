/**
 * Implement this Interface in Update & Delete Queries are to be Fired. Normally it is to be Implemented on Remote Backup Server
 */
package cRUDUtilityClass;



import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLSyntaxErrorException;
import java.sql.Statement;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

 public class UDBatchForBackupDB {
	
	final static Logger log = Logger.getLogger(UDBatchForBackupDB.class.getName());
	
	
	// Method to Execute Update BatchStatement Only returns int[] to SQL Success Array 
	public int[] fireUpdateSQL(Statement statObj) throws SQLException,SQLSyntaxErrorException{
		if(log.isInfoEnabled())log.info("## Firing Update SQL Batch ##");
		return statObj.executeBatch();
	}
	
	
	// Method to Execute Truncate DDL & Insert DDL on HRMS_HRRM for Loading Fresh data from HRMS
	public void fireTruncateNInsertDDL(Connection oRAcon_STG, Connection oRAcon, ResourceBundle rBProps) throws SQLException,SQLSyntaxErrorException {
		
		Statement truncatedDataNCopyFreshData_DBStat = oRAcon.createStatement();
		// Table & Column Name from HRMS_HRRM Table for DDL & DML 
		String hRMS_HRRMTableName = rBProps.getString("hRMS_HRRMTableName");
		String tableName = hRMS_HRRMTableName.substring(hRMS_HRRMTableName.indexOf('.')+1, hRMS_HRRMTableName.length());
		String hRMS_HRRM_PERSON_NUMBERColumnName = rBProps.getString("hRMS_HRRM_PERSON_NUMBERColumnName");
		//String hRMSTableName= rBProps.getString("hRMSTableName");
		
			//## Checking if HRMS_HRRM_snap Exists & Deletes Old to Create New Before Truncating HRMS_HRRM 
			boolean isSnapPresent = false;
			try { isSnapPresent = truncatedDataNCopyFreshData_DBStat.executeQuery("SELECT * FROM HRMS_HRRM_snap") != null; }catch(SQLException e){if(log.isInfoEnabled())log.info("HRMS_HRRM_snap does NOT exists");}
			if( isSnapPresent ){
				truncatedDataNCopyFreshData_DBStat.executeUpdate("DROP MATERIALIZED VIEW HRMS_HRRM_snap");
				if(log.isInfoEnabled())log.info("HRMS_HRRM_snap was Dropped Now");
			}
			truncatedDataNCopyFreshData_DBStat.executeUpdate("CREATE SNAPSHOT HRMS_HRRM_snap AS SELECT * FROM "+hRMS_HRRMTableName);
			if(log.isInfoEnabled())log.info("Backed Up Existing Data to HRMS_HRRM_snap");
			//## Checking if HRMS_HRRM_snap Exists & Deletes Old to Create New Before Truncating HRMS_HRRM
		
		
		//## Checking if HR_PERSON_NUMBER Exists in HRMS_HRRM & Adds if Not ##
		String SQL = "SELECT 1 FROM DUAL WHERE EXISTS (SELECT 1 FROM all_tab_columns WHERE table_name = '"+tableName+"' AND column_name = '"+hRMS_HRRM_PERSON_NUMBERColumnName+"')";
		//System.out.println(SQL);
		int SQLAnswer = 0;
		try{SQLAnswer = truncatedDataNCopyFreshData_DBStat.executeUpdate(SQL);}catch(SQLException e){};
		if(SQLAnswer==0){//HRMS_HRRM2 Has HR_PERSON_NUMBER Column
			truncatedDataNCopyFreshData_DBStat.executeUpdate("ALTER TABLE "+hRMS_HRRMTableName+" ADD "+hRMS_HRRM_PERSON_NUMBERColumnName+" VARCHAR2(150)");
		}
		//## Checking if HR_PERSON_NUMBER Exists in HRMS_HRRM & Adds if Not ##
		
		//## Copying Fresh Data to HRMS_HRRM From HRMS BeFore Updating HR_PERSON_NUMBER COLUMN 
		//truncatedDataNCopyFreshData_DBStat.addBatch("ALTER TABLE "+hRMS_HRRMTableName+" DROP COLUMN "+hRMS_HRRM_PERSON_NUMBERColumnName);
		truncatedDataNCopyFreshData_DBStat.addBatch("TRUNCATE TABLE "+hRMS_HRRMTableName);
		truncatedDataNCopyFreshData_DBStat.executeBatch();
		//truncatedDataNCopyFreshData_DBStat.execute("INSERT INTO "+hRMS_HRRMTableName+" SELECT * FROM "+hRMSTableName);
		new CopyTableContent().copyHRMS2HRMS_HRRM(oRAcon_STG, oRAcon);
		//truncatedDataNCopyFreshData_DBStat.execute("ALTER TABLE "+hRMS_HRRMTableName+" ADD "+hRMS_HRRM_PERSON_NUMBERColumnName+" VARCHAR2(150)");
		oRAcon.commit();
	}
	
	public void FireTruncNSnapHRMS_HRRM(Connection oRAcon_STG, Connection oRAcon, ResourceBundle rBProps) throws SQLException,SQLSyntaxErrorException {
		
		Statement truncatedDataNCopyFreshData_DBStat = oRAcon.createStatement();
		// Table & Column Name from HRMS_HRRM Table for DDL & DML 
		String hRMS_HRRMTableName = rBProps.getString("hRMS_HRRMTableName"); //HRUPM.HRMS_HRRM
		String tableName = hRMS_HRRMTableName.substring(hRMS_HRRMTableName.indexOf('.')+1, hRMS_HRRMTableName.length());
		String hRMS_HRRM_PERSON_NUMBERColumnName = rBProps.getString("hRMS_HRRM_PERSON_NUMBERColumnName"); //HR_PERSON_NUMBER
		//String hRMSTableName= rBProps.getString("hRMSTableName");
		
			//## Checking if HRMS_HRRM_snap Exists & Deletes Old to Create New Before Truncating HRMS_HRRM 
			boolean isSnapPresent = false;
			try { isSnapPresent = truncatedDataNCopyFreshData_DBStat.executeQuery("SELECT * FROM HRMS_HRRM_snap") != null; }catch(SQLException e){if(log.isInfoEnabled())log.info("HRMS_HRRM_snap does NOT exists");}
			if( isSnapPresent ){
				truncatedDataNCopyFreshData_DBStat.executeUpdate("DROP MATERIALIZED VIEW HRMS_HRRM_snap");
				if(log.isInfoEnabled())log.info("HRMS_HRRM_snap was Dropped Now");
			}
			truncatedDataNCopyFreshData_DBStat.executeUpdate("CREATE SNAPSHOT HRMS_HRRM_snap AS SELECT * FROM "+hRMS_HRRMTableName);
			if(log.isInfoEnabled())log.info("Backed Up Existing Data to HRMS_HRRM_snap");
			//## Checking if HRMS_HRRM_snap Exists & Deletes Old to Create New Before Truncating HRMS_HRRM
		
		
		//## Checking if HR_PERSON_NUMBER Exists in HRMS_HRRM & Adds if Not ##
		String SQL = "SELECT 1 FROM DUAL WHERE EXISTS (SELECT 1 FROM all_tab_columns WHERE table_name = '"+tableName+"' AND column_name = '"+hRMS_HRRM_PERSON_NUMBERColumnName+"')";
		//System.out.println(SQL);
		int SQLAnswer = 0;
		try{SQLAnswer = truncatedDataNCopyFreshData_DBStat.executeUpdate(SQL);}catch(SQLException e){};
		if(SQLAnswer==0){//HRMS_HRRM2 Has HR_PERSON_NUMBER Column
			truncatedDataNCopyFreshData_DBStat.executeUpdate("ALTER TABLE "+hRMS_HRRMTableName+" ADD "+hRMS_HRRM_PERSON_NUMBERColumnName+" VARCHAR2(150)");
		}
		//## Checking if HR_PERSON_NUMBER Exists in HRMS_HRRM & Adds if Not ##
		
		//## Copying Fresh Data to HRMS_HRRM From HRMS BeFore Updating HR_PERSON_NUMBER COLUMN 
		//truncatedDataNCopyFreshData_DBStat.addBatch("ALTER TABLE "+hRMS_HRRMTableName+" DROP COLUMN "+hRMS_HRRM_PERSON_NUMBERColumnName);
		truncatedDataNCopyFreshData_DBStat.addBatch("TRUNCATE TABLE "+hRMS_HRRMTableName); // deleting the rows data from hrupm.hrms_hrrm
		truncatedDataNCopyFreshData_DBStat.executeBatch();
		//truncatedDataNCopyFreshData_DBStat.execute("INSERT INTO "+hRMS_HRRMTableName+" SELECT * FROM "+hRMSTableName);
		new CopyTableContent().copyHRMS_HRRMData(oRAcon_STG, oRAcon);
		//truncatedDataNCopyFreshData_DBStat.execute("ALTER TABLE "+hRMS_HRRMTableName+" ADD "+hRMS_HRRM_PERSON_NUMBERColumnName+" VARCHAR2(150)");
		oRAcon.commit();
	}

	
	public void fireTruncateNInsertDDL_forHRRM(Connection oRAcon_STG, Connection oRAcon, ResourceBundle rBProps) throws SQLException,SQLSyntaxErrorException {
		
		Statement truncatedDataNCopyFreshData_DBStat = oRAcon.createStatement();
		// Table & Column Name from HRMS_HRRM Table for DDL & DML 
		String hRRMTableName = rBProps.getString("hRRMTableName_TG"); //hrupm.hrrm
		
		
		
			//## Checking if HRMS_HRRM_snap Exists & Deletes Old to Create New Before Truncating HRMS_HRRM 
			boolean isSnapPresent = false;
			try { isSnapPresent = truncatedDataNCopyFreshData_DBStat.executeQuery("SELECT * FROM HRRM_snap") != null; }
			catch(SQLException e){log.error("HRMS_HRRM_snap does NOT exists" +  e.getMessage());}
			if( isSnapPresent ){
				truncatedDataNCopyFreshData_DBStat.executeUpdate("DROP MATERIALIZED VIEW HRRM_snap");
				if(log.isInfoEnabled())log.info("HRRM_snap was Dropped Now");
			}
			truncatedDataNCopyFreshData_DBStat.executeUpdate("CREATE SNAPSHOT HRRM_snap AS SELECT * FROM "+hRRMTableName);
			if(log.isInfoEnabled())log.info("Backed Up Existing Data to HRRM_snap");
			//## Checking if HRMS_HRRM_snap Exists & Deletes Old to Create New Before Truncating HRMS_HRRM
		
		
		
		
		//## Copying Fresh Data to HRMS_HRRM From HRMS BeFore Updating HR_PERSON_NUMBER COLUMN 
		
		truncatedDataNCopyFreshData_DBStat.addBatch("TRUNCATE TABLE "+hRRMTableName);
		truncatedDataNCopyFreshData_DBStat.executeBatch();
		
		new CopyTableContent().copyHRRM2HRRM(oRAcon_STG, oRAcon);
		
		oRAcon.commit(); //hrupm.hrrm commit
	}
	
	
	/*
	*SELECT 1 
   FROM dual
   WHERE EXISTS (   SELECT 1 
                    FROM all_tab_columns 
                    WHERE table_name = 'HRMS_HRRM2' 
                    AND column_name = 'HR_PERSON_NUMBER'
                );
                
	*
	/*

	 * "INSERT INTO HR.HRMS_HRRM2" +
				"(" +
					"EMPLOYEE_NUMBER," +
					"ORACLE_EMPLOYEE_NUMBER," +
					"COMPANY," +
					"PERSON_ID," +
					"ATTRIBUTE3," +
					"PREFIX," +
					"FIRST_NAME," +
					"MIDDLE_NAMES," +
					"LAST_NAME," +
					"EMAIL_ADDRESS," +
					"GENDER," +
					"DOB," +
					"MARITAL_STATUS," +
					"PADDRESS1," +
					"PADDRESS2," +
					"PADDRESS3," +
					"PCITY," +
					"PPIN," +
					"TADDRESS1," +
					"TADDRESS2," +
					"TADDRESS3," +
					"TCITY," +
					"TPIN," +
					"SUPERVISOR_EMP_NO," +
					"SUP_COMPANY," +
					"DOJ," +
					"FATHER_HUSBAND_NAME," +
					"DIVISION," +
					"FUNCTION," +
					"FUNCTION_ID," +
					"DESIGNATION_ID," +
					"PAYROLL_ID," +
					"DESIGNATION_LABEL_ID," +
					"ROLE," +
					"LOC_CODE," +
					"LOCATION_NAME," +
					"DATE_EMPLOYEE_DATA_VERIFIED," +
					"LOB_CODE," +
					"LOB," +
					"CC_CODE," +
					"CC_NAME," +
					"CATEGORY," +
					"DOMAIN_LOGIN_ID," +
					"LAST_WORKING_DATE," +
					"RM_NAME," +
					"SUPERVISOR_NO," +
					"SOURCE," +
					"KPO_CODE," +
					"SUPERVISOR_NAME," +
					"MOBILE_NUMBER," +
					"ASSIGNMENT_CHANGE_DATE," +
					"LOC_CODE_NEW," +
					"ING_EMP_TYPE," +
					"SEGMENT," +
					"ROLE_ID," +
					"PAN_NO," +
					"DOJ_KOTAK_GROUP," +
					"RM_EMAIL," +
					"GRADE_ID" +
				")" +
				"SELECT" + 
					"EMPLOYEE_NUMBER," +
					"ORACLE_EMPLOYEE_NUMBER," +
					"COMPANY," +
					"PERSON_ID," +
					"ATTRIBUTE3," +
					"PREFIX," +
					"FIRST_NAME," +
					"MIDDLE_NAMES," +
					"LAST_NAME," +
					"EMAIL_ADDRESS," +
					"GENDER," +
					"DOB," +
					"MARITAL_STATUS," +
					"PADDRESS1," +
					"PADDRESS2," +
					"PADDRESS3," +
					"PCITY," +
					"PPIN," +
					"TADDRESS1," +
					"TADDRESS2," +
					"TADDRESS3," +
					"TCITY," +
					"TPIN," +
					"SUPERVISOR_EMP_NO," +
					"SUP_COMPANY," +
					"DOJ," +
					"FATHER_HUSBAND_NAME," +
					"DIVISION," +
					"FUNCTION," +
					"FUNCTION_ID," +
					"DESIGNATION_ID," +
					"PAYROLL_ID," +
					"DESIGNATION_LABEL_ID," +
					"ROLE," +
					"LOC_CODE," +
					"LOCATION_NAME," +
					"DATE_EMPLOYEE_DATA_VERIFIED," +
					"LOB_CODE," +
					"LOB," +
					"CC_CODE," +
					"CC_NAME," +
					"CATEGORY," +
					"DOMAIN_LOGIN_ID," +
					"LAST_WORKING_DATE," +
					"RM_NAME," +
					"SUPERVISOR_NO," +
					"SOURCE," +
					"KPO_CODE," +
					"SUPERVISOR_NAME," +
					"MOBILE_NUMBER," +
					"ASSIGNMENT_CHANGE_DATE," +
					"LOC_CODE_NEW," +
					"ING_EMP_TYPE," +
					"SEGMENT," +
					"ROLE_ID," +
					"PAN_NO," +
					"DOJ_KOTAK_GROUP," +
					"RM_EMAIL," +
					"GRADE_ID" +
				"FROM HR.HRMS "
				; 
	 */


	public void executeHRRMUpdate(Connection dbCon,ResourceBundle rBProps) throws SQLException{
		//#Columns Name for CHRRM
		String cHRRM_TableName= rBProps.getString("cHRRM_TableName");//HR.CUST_ROLES_USR_MAPPING
		String eMPLOYEE_NUMBER_CHRRM = rBProps.getString("eMPLOYEE_NUMBER_CHRRM");//PERSON_NUMBER
		String lOB_CODE_CHRRM = rBProps.getString("lOB_CODE_CHRRM");//NEW_LOB
		String lOC_CODE_CHRMS = rBProps.getString("lOC_CODE_CHRMS");//NEW_LOC
		String cC_CODE_CHRRM = rBProps.getString("cC_CODE_CHRRM");//NEW_CC
		String rESPONSIBILITY_NAME_CHRRM = rBProps.getString("rESPONSIBILITY_NAME_CHRRM");//RESPONSIBILITY_NAME
		//#Columns Name for HRRM 
		String hRRMTableName=rBProps.getString("hRRMTableName");//HR.HRRM
		String pERSON_NUMBER_HRRM = rBProps.getString("pERSON_NUMBER_HRRM");//PERSON_NUMBER
		String lOB_CODE_HRRM = rBProps.getString("lOB_CODE_HRRM");//NEW_LOB
		String lOC_CODE_HRRM = rBProps.getString("lOC_CODE_HRRM");//NEW_LOC
		String cC_CODE_HRRM = rBProps.getString("cC_CODE_HRRM");//NEW_CC
		String rESPONSIBILITY_NAME_HRRM = rBProps.getString("rESPONSIBILITY_NAME_HRRM");//RESPONSIBILITY_NAME
		String DML = 
		"INSERT INTO "+hRRMTableName+"( "+pERSON_NUMBER_HRRM+", "+lOB_CODE_HRRM+", "+lOC_CODE_HRRM+", "+cC_CODE_HRRM+", "+rESPONSIBILITY_NAME_CHRRM+" )"+
		" SELECT "+eMPLOYEE_NUMBER_CHRRM+", "+lOB_CODE_CHRRM+", "+lOC_CODE_CHRMS+", "+cC_CODE_CHRRM+", "+rESPONSIBILITY_NAME_HRRM+
		" FROM "+cHRRM_TableName;
		
		//System.out.println(DML); INSERT INTO TABLENAME (C1,C2) SELECT C1,C2 FROM SOURCETABLE
		Statement st = dbCon.createStatement();
		try{
			st.execute(DML);
		}catch(SQLIntegrityConstraintViolationException e){
			if(log.isInfoEnabled())log.info("It Seems that, the Custom Role Table Has Old/Repeated Entry Present : "+e.getLocalizedMessage());
			//e.printStackTrace();
		}catch(Exception e){
			if(log.isInfoEnabled())log.info(e.getLocalizedMessage());
			e.printStackTrace();
		}
		dbCon.commit();
		if(log.isInfoEnabled())log.info("Included Custom Roles in HRRM");
	}
	
	// Alternate & Easy way of Truncate & Insert Data in HRMS_HRRM But Does not Work with JAVA ORACLE API
		public void fireTruncateNInsertDDL2(Connection oRAcon) throws SQLException {
				
				Statement truncateDataNCopyFreshData_DBStat = oRAcon.createStatement();
				String hRMS_HRRMTableName ="HR.HRMS_HRRM2";// Change as Per Production Evn
				String hRMSTableName= "HR.HRMS"; // Change as Per Production Evn
				truncateDataNCopyFreshData_DBStat.executeUpdate("TRUNCATE TABLE "+hRMS_HRRMTableName);
				truncateDataNCopyFreshData_DBStat.execute(
						"INSERT INTO "+hRMS_HRRMTableName+
						"(" +
							"EMPLOYEE_NUMBER," +
							"ORACLE_EMPLOYEE_NUMBER," +
							"COMPANY," +
							"PERSON_ID," +
							"ATTRIBUTE3," +
							"PREFIX," +
							"FIRST_NAME," +
							"MIDDLE_NAMES," +
							"LAST_NAME," +
							"EMAIL_ADDRESS," +
							"GENDER," +
							"DOB," +
							"MARITAL_STATUS," +
							"PADDRESS1," +
							"PADDRESS2," +
							"PADDRESS3," +
							"PCITY," +
							"PPIN," +
							"TADDRESS1," +
							"TADDRESS2," +
							"TADDRESS3," +
							"TCITY," +
							"TPIN," +
							"SUPERVISOR_EMP_NO," +
							"SUP_COMPANY," +
							"DOJ," +
							"FATHER_HUSBAND_NAME," +
							"DIVISION," +
							"FUNCTION," +
							"FUNCTION_ID," +
							"DESIGNATION_ID," +
							"PAYROLL_ID," +
							"DESIGNATION_LABEL_ID," +
							"ROLE," +
							"LOC_CODE," +
							"LOCATION_NAME," +
							"DATE_EMPLOYEE_DATA_VERIFIED," +
							"LOB_CODE," +
							"LOB," +
							"CC_CODE," +
							"CC_NAME," +
							"CATEGORY," +
							"DOMAIN_LOGIN_ID," +
							"LAST_WORKING_DATE," +
							"RM_NAME," +
							"SUPERVISOR_NO," +
							"SOURCE," +
							"KPO_CODE," +
							"SUPERVISOR_NAME," +
							"MOBILE_NUMBER," +
							"ASSIGNMENT_CHANGE_DATE," +
							"LOC_CODE_NEW," +
							"ING_EMP_TYPE," +
							"SEGMENT," +
							"ROLE_ID," +
							"PAN_NO," +
							"DOJ_KOTAK_GROUP," +
							"RM_EMAIL," +
							"GRADE_ID" +
						") " + // ") VALUES(" +
						"SELECT" + 
							"EMPLOYEE_NUMBER," +
							"ORACLE_EMPLOYEE_NUMBER," +
							"COMPANY," +
							"PERSON_ID," +
							"ATTRIBUTE3," +
							"PREFIX," +
							"FIRST_NAME," +
							"MIDDLE_NAMES," +
							"LAST_NAME," +
							"EMAIL_ADDRESS," +
							"GENDER," +
							"DOB," +
							"MARITAL_STATUS," +
							"PADDRESS1," +
							"PADDRESS2," +
							"PADDRESS3," +
							"PCITY," +
							"PPIN," +
							"TADDRESS1," +
							"TADDRESS2," +
							"TADDRESS3," +
							"TCITY," +
							"TPIN," +
							"SUPERVISOR_EMP_NO," +
							"SUP_COMPANY," +
							"DOJ," +
							"FATHER_HUSBAND_NAME," +
							"DIVISION," +
							"FUNCTION," +
							"FUNCTION_ID," +
							"DESIGNATION_ID," +
							"PAYROLL_ID," +
							"DESIGNATION_LABEL_ID," +
							"ROLE," +
							"LOC_CODE," +
							"LOCATION_NAME," +
							"DATE_EMPLOYEE_DATA_VERIFIED," +
							"LOB_CODE," +
							"LOB," +
							"CC_CODE," +
							"CC_NAME," +
							"CATEGORY," +
							"DOMAIN_LOGIN_ID," +
							"LAST_WORKING_DATE," +
							"RM_NAME," +
							"SUPERVISOR_NO," +
							"SOURCE," +
							"KPO_CODE," +
							"SUPERVISOR_NAME," +
							"MOBILE_NUMBER," +
							"ASSIGNMENT_CHANGE_DATE," +
							"LOC_CODE_NEW," +
							"ING_EMP_TYPE," +
							"SEGMENT," +
							"ROLE_ID," +
							"PAN_NO," +
							"DOJ_KOTAK_GROUP," +
							"RM_EMAIL," +
							"GRADE_ID" +
						"FROM "+hRMSTableName // +
					//	")"  // ")," 
					); // truncateDataNCopyFreshData_DBStat.execute() Ends
				
			}//method ends
	
}//class
