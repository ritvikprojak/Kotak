package activeDirectory;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;
import org.apache.log4j.Logger;

import dBInterfacePackageImpl.ORADatabase;
import dDReplication.MasterSlaveDBreplication;

//
public class ActiveDirectoryApp {

	static Logger log = Logger.getLogger(ActiveDirectoryApp.class.getName());
	ResourceBundle rBProps = MasterSlaveDBreplication.rBProps;

	public void shuffleAdUsers(Connection oRAcon) throws SQLException {

		ActiveDirectoryApp aDAObj = new ActiveDirectoryApp();

		ResultSet removeRS = aDAObj.fetchViewResultSet(oRAcon, rBProps, "REMOVE");
		aDAObj.removeUserOnRole(resultSetToMapList(removeRS));
		while (removeRS.next()) {
			for (int i = 1; i < removeRS.getMetaData().getColumnCount() + 1; i++) {
				System.out.print(" " + removeRS.getMetaData().getColumnName(i) + "=" + removeRS.getObject(i));
			}
			System.out.println("");
		}

		ResultSet addRS = aDAObj.fetchViewResultSet(oRAcon, rBProps, "ADD");
		while (removeRS.next()) {
			for (int i = 1; i < addRS.getMetaData().getColumnCount() + 1; i++) {
				System.out.print(" " + addRS.getMetaData().getColumnName(i) + "=" + addRS.getObject(i));
			}
			System.out.println("");
		}

		aDAObj.addUserOnRole(resultSetToMapList(addRS));

	}

	public ResultSet fetchViewResultSet(final Connection con, final ResourceBundle rBProps, String taskCode)
			throws SQLException {
		Statement st = con.createStatement();
		// Columns to be Fetched from CHRRM ResultSet
		String HRMS_ROLESViewName = rBProps.getString("HRMS_ROLESViewName");//// VARCHAR2
		String v_EMPLOYEE_NUMBER = rBProps.getString("v_EMPLOYEE_NUMBER");// VARCHAR2
		String v_DOMAIN_LOGIN_ID = rBProps.getString("v_DOMAIN_LOGIN_ID");// VARCHAR2
		String v_ROLE = rBProps.getString("v_ROLE");// VARCHAR2
		String v_LAST_WORKING_DATE = rBProps.getString("v_LAST_WORKING_DATE");// DATE
		String v_ROLENAME = rBProps.getString("v_ROLENAME");// VARCHAR2
		String v_ISACTIVE = rBProps.getString("v_ISACTIVE");// NUMBER
		String v_END_DATE = rBProps.getString("v_END_DATE");// TIMESTAMP
		String v_COPY_ACCESS = rBProps.getString("v_COPY_ACCESS");// NUMBER
		String v_CREATE_ACCESS = rBProps.getString("v_CREATE_ACCESS");// NUMBER
		String v_DELETE_ACCESS = rBProps.getString("v_DELETE_ACCESS");// NUMBER
		String v_MODIFY_ACCESS = rBProps.getString("v_MODIFY_ACCESS");// NUMBER
		String v_PRINT_ACCESS = rBProps.getString("v_PRINT_ACCESS");// NUMBER
		String v_READ_ACCESS = rBProps.getString("v_READ_ACCESS");// NUMBER
		String v_COMPANY = rBProps.getString("v_COMPANY");

		String selectQuery = null;

		if (taskCode.equals("REMOVE")) {
			selectQuery = "SELECT " + v_DOMAIN_LOGIN_ID + ", " + v_COMPANY + " FROM " + HRMS_ROLESViewName + " WHERE ("
					+ v_DOMAIN_LOGIN_ID + " IS NOT NULL AND " + v_DOMAIN_LOGIN_ID + " !='DUMMY') AND (( " + v_END_DATE
					+ " <= LOCALTIMESTAMP OR " + v_LAST_WORKING_DATE + " <= LOCALTIMESTAMP ) OR ( " + v_ISACTIVE + " = '0' OR "
					+ v_ISACTIVE + " IS NULL))";
		} // removeUser IF Ends

		if (taskCode.equals("ADD")) {
			selectQuery = "SELECT " + v_EMPLOYEE_NUMBER + ", " + v_DOMAIN_LOGIN_ID + ", " + v_ROLE + ", "
					+ v_LAST_WORKING_DATE + ", " + v_ROLENAME + ", " + v_ISACTIVE + ", " + v_END_DATE + ", "
					+ v_READ_ACCESS + ", " + v_PRINT_ACCESS + ", " + v_COPY_ACCESS + ", " + v_MODIFY_ACCESS + ", "
					+ v_CREATE_ACCESS + ", " + v_DELETE_ACCESS + ", " + v_COMPANY + " FROM " + HRMS_ROLESViewName
					+ " WHERE " + "( " + v_DOMAIN_LOGIN_ID + " IS NOT NULL AND " + v_DOMAIN_LOGIN_ID
					+ " !='DUMMY' AND ( " + v_ISACTIVE + " = '1') AND ("
					+ v_END_DATE + " >= LOCALTIMESTAMP OR " + v_END_DATE + " IS NULL ) AND (" + v_LAST_WORKING_DATE
					+ " >= LOCALTIMESTAMP OR " + v_LAST_WORKING_DATE + " IS NULL ) )" ;
//					+ " OR " + "( "
//					+ v_DOMAIN_LOGIN_ID + " IS NOT NULL AND " + v_DOMAIN_LOGIN_ID + " !='DUMMY' AND ( "
//					+ v_IS_CUSTOM_ROLE + " IS NOT NULL OR " + v_IS_CUSTOM_ROLE + " = 1) AND (" + v_END_DATE
//					+ " >= LOCALTIMESTAMP OR " + v_END_DATE + " IS NULL ) AND (" + v_LAST_WORKING_DATE
//					+ " >= LOCALTIMESTAMP OR " + v_LAST_WORKING_DATE + " IS NULL ) )";
			// System.out.println("ADD : "+selectQuery);
		} // addUser IF Ends

		// System.out.println(selectQuery);

		ResultSet rs = st.executeQuery(selectQuery);
		isRSEmpty(rs);
		return rs;

	}// fetchViewResultSet Methods Ends Here

	public void isRSEmpty(ResultSet rs) {
		// Verifies if CASES(Rows) are Returned by SQL Query
		if (rs.equals(null)) {
			if(log.isInfoEnabled())log.info("rs.equals(null) : " + true);
			if(log.isInfoEnabled())log.info("!!! warn Msg  !!!", new Error(
					"QUERY RETURNED EMPTY RESULTSET. BAD 'SQLQUERY' PARAMETER IN CONFIG.PROPERTIES FILE AT /FS1/IBM/ICNAUTOACTIONS/CONFIG/CONFIG.PROPERTIES "));
			return;
		}
		// if(log.isInfoEnabled())log.info("\t\tMessage: SQLQUERY Returned ResultSet Row(s) from DB");

	}// isRSEmpty

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

	public void removeUserOnRole(ArrayList<Map<String, Object>> MapList) {

		String v_DOMAIN_LOGIN_ID = rBProps.getString("v_DOMAIN_LOGIN_ID");
		String v_COMPANY = rBProps.getString("v_COMPANY");

		UpmLdapEvents upmLdap = new UpmLdapEvents();

		Iterator<Map<String, Object>> mapListItr = MapList.iterator();
		while (mapListItr.hasNext()) {
			// String vDOMAIN_LOGIN_ID = null;
			String adUserId = null;
			String company = null;

			for (Map.Entry<String, Object> mapListItrEntry : mapListItr.next().entrySet()) {
				if (mapListItrEntry.getKey().equals(v_DOMAIN_LOGIN_ID)) {
					adUserId = (String) mapListItrEntry.getValue();
				} // if else if ends
				else if (mapListItrEntry.getKey().equals(v_COMPANY)) {
					company = (String) mapListItrEntry.getValue();
				}
			} // for ends

			// if(log.isInfoEnabled())log.info("Removing Aduser "+adUserId+" from All Groups");
			try {
				upmLdap.RemoveUserFromGroup(company, "FN_HR_DOWNLOAD", adUserId);
			} catch (Exception e) {
				if(log.isInfoEnabled())log.info(adUserId + " AdUser was not part of DOWNLOAD Group");
			}
			try {
				upmLdap.RemoveUserFromGroup(company, "FN_HR_CREATE", adUserId);
			} catch (Exception e) {
				if(log.isInfoEnabled())log.info(adUserId + " AdUser was not part of CREATE Group");
			}
			try {
				upmLdap.RemoveUserFromGroup(company, "FN_HR_MODIFY", adUserId);
			} catch (Exception e) {
				if(log.isInfoEnabled())log.info(adUserId + " AdUser was not part of MODIFY Group");
			}
			try {
				upmLdap.RemoveUserFromGroup(company, "FN_HR_DELETE", adUserId);
			} catch (Exception e) {
				if(log.isInfoEnabled())log.info(adUserId + " AdUser was not part of DELETE Group");
			}
			try {
				upmLdap.RemoveUserFromGroup(company, "FN_HR_PRINT", adUserId);
			} catch (Exception e) {
				if(log.isInfoEnabled())log.info(adUserId + " AdUser was not part of PRINT Group");
			}
			try {
				upmLdap.RemoveUserFromGroup(company, "FN_HR_VIEW", adUserId);
			} catch (Exception e) {
				if(log.isInfoEnabled())log.info(adUserId + " AdUser was not part of VIEW Group");
			}
			try {
				upmLdap.RemoveUserFromGroup(company, "HR_USERS", adUserId);
			} catch (Exception e) {
				if(log.isInfoEnabled())log.info(adUserId + " AdUser was not part of HR_USERS Group");
			}
		} // while (MapListItr.hasNext()) ends
	}// removeUserOnRole() Method ends

	public void addUserOnRole(ArrayList<Map<String, Object>> MapList) {
		// System.out.println("Inside addUserOnRole");
		// AdUserId Details
		String v_DOMAIN_LOGIN_ID = rBProps.getString("v_DOMAIN_LOGIN_ID");
		String v_COPY_ACCESS = rBProps.getString("v_COPY_ACCESS");
		String v_CREATE_ACCESS = rBProps.getString("v_CREATE_ACCESS");
		String v_DELETE_ACCESS = rBProps.getString("v_DELETE_ACCESS");
		String v_MODIFY_ACCESS = rBProps.getString("v_MODIFY_ACCESS");
		String v_PRINT_ACCESS = rBProps.getString("v_PRINT_ACCESS");
		String v_READ_ACCESS = rBProps.getString("v_READ_ACCESS");
		String v_COMPANY = rBProps.getString("v_COMPANY");
		UpmLdapEvents upmLdap = new UpmLdapEvents();

		Iterator<Map<String, Object>> mapListItr = MapList.iterator();
		while (mapListItr.hasNext()) {
			String adUserId = null;// vDOMAIN_LOGIN_ID
			BigDecimal vCOPY_ACCESS = null;
			BigDecimal vCREATE_ACCESS = null;
			BigDecimal vDELETE_ACCESS = null;
			BigDecimal vMODIFY_ACCESS = null;
			BigDecimal vPRINT_ACCESS = null;
			BigDecimal vREAD_ACCESS = null;
			String company = null;

			// for Each Entry of Master Details HRMS
			for (Map.Entry<String, Object> mapListItrEntry : mapListItr.next().entrySet()) {
				// System.out.println("Inside for of addUserOnRole");
				if (mapListItrEntry.getKey().equals(v_DOMAIN_LOGIN_ID)) {
					adUserId = (String) mapListItrEntry.getValue();
				} else if (mapListItrEntry.getKey().equals(v_COPY_ACCESS)) {
					vCOPY_ACCESS = (BigDecimal) mapListItrEntry.getValue();
				} else if (mapListItrEntry.getKey().equals(v_CREATE_ACCESS)) {
					vCREATE_ACCESS = (BigDecimal) mapListItrEntry.getValue();
				} else if (mapListItrEntry.getKey().equals(v_DELETE_ACCESS)) {
					vDELETE_ACCESS = (BigDecimal) mapListItrEntry.getValue();
				} else if (mapListItrEntry.getKey().equals(v_MODIFY_ACCESS)) {
					vMODIFY_ACCESS = (BigDecimal) mapListItrEntry.getValue();
				} else if (mapListItrEntry.getKey().equals(v_PRINT_ACCESS)) {
					vPRINT_ACCESS = (BigDecimal) mapListItrEntry.getValue();
				} else if (mapListItrEntry.getKey().equals(v_READ_ACCESS)) {
					vREAD_ACCESS = (BigDecimal) mapListItrEntry.getValue();
				} else if (mapListItrEntry.getKey().equals(v_COMPANY)) {
					company = (String) mapListItrEntry.getValue();
				}
			} // for ends

			if(log.isInfoEnabled())log.info("Adding AdUser " + adUserId + " in Various Groups.");
			if (null != vCOPY_ACCESS) {
				if (vCOPY_ACCESS.equals(new BigDecimal(1))) {
					upmLdap.AddUserToGroup(company, "FN_HR_DOWNLOAD", adUserId);
				}else {
					upmLdap.RemoveUserFromGroup(company, "FN_HR_DOWNLOAD", adUserId);
				}
			}

			if (null != vCREATE_ACCESS) {
				if (vCREATE_ACCESS.equals(new BigDecimal(1))) {
					upmLdap.AddUserToGroup(company, "FN_HR_CREATE", adUserId);
				} else {
					upmLdap.RemoveUserFromGroup(company, "FN_HR_CREATE", adUserId);
				}
			}

			if (null != vMODIFY_ACCESS) {
				if (vMODIFY_ACCESS.equals(new BigDecimal(1))) {
					upmLdap.AddUserToGroup(company, "FN_HR_MODIFY", adUserId);
				}
				else{
					upmLdap.RemoveUserFromGroup(company, "FN_HR_MODIFY", adUserId);
				}
			}

			if (null != vDELETE_ACCESS) {
				if (vDELETE_ACCESS.equals(new BigDecimal(1))) {
					upmLdap.AddUserToGroup(company, "FN_HR_DELETE", adUserId);
				}
				else{
					upmLdap.RemoveUserFromGroup(company, "FN_HR_DELETE", adUserId);
				}
			}

			if (null != vPRINT_ACCESS) {
				if (vPRINT_ACCESS.equals(new BigDecimal(1))) {
					upmLdap.AddUserToGroup(company, "FN_HR_PRINT", adUserId);
				}
				else{
					upmLdap.RemoveUserFromGroup(company, "FN_HR_PRINT", adUserId);
				}
			}

			if (null != vREAD_ACCESS) {
				if (vREAD_ACCESS.equals(new BigDecimal(1))) {
					upmLdap.AddUserToGroup(company, "FN_HR_VIEW", adUserId);
				}
				else{
					upmLdap.RemoveUserFromGroup(company, "FN_HR_VIEW", adUserId);
				}
				
			}
			upmLdap.AddUserToGroup(company, "HR_USERS", adUserId);
		} // while (cHRRMMapListItr.hasNext()) ends
	}// addUserOnRole() Method End

	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		// ## Already Part of Main Methods in dBReplication
		ORADatabase oraDBObj = new ORADatabase();
		oraDBObj.loadDriver();
		ResourceBundle rBProps = MasterSlaveDBreplication.rBProps;
		Connection oRAcon = oraDBObj.loadConnection("SERVER", rBProps);
		// ## Already Part of Main Methods in dBReplication

		ActiveDirectoryApp aDAObj = new ActiveDirectoryApp();
		aDAObj.shuffleAdUsers(oRAcon);
	}// main

}// class ends
