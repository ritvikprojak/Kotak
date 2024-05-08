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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.ListIterator;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Map.Entry;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import org.apache.log4j.Logger;
import activeDirectory.ActiveDirectoryApp;
import resourceBundlePackage.ResourceBundleClass;
import cRUDUtilityClass.UDBatchForBackupDB;
import dBInterfacePackageImpl.GettingHRRMCorrect;
import dBInterfacePackageImpl.ORADatabase;
import emailSMTPPackage.EmailNotification;

//MasterSlaveDBreplication.rBProps
public class MasterSlaveDBreplication {

	static {
		LocalDateTime startTime = LocalDateTime.now();
		String logFileName = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss").format(startTime);
		System.setProperty("current.date.time", logFileName);
	}

	final static Logger log = Logger.getLogger(MasterSlaveDBreplication.class.getName());
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
	public ArrayList<Map<String, String>> RunBusinessLogic(ArrayList<Map<String, Object>> rMMapList, String tableName) {

		// Columns to be Fetched from HRRM ResultSet
		///////////////// 29 Nov 2019 Start/////////////
		String pERSON_NUMBER_HRRM = rBProps.getString("pERSON_NUMBER_HRRM");
		// String pERSON_NUMBER_HRRM = rBProps.getString("hR_RM_TG");//"HR_RM";
		///////////////// 29 Nov 2019 Start/////////////
		String rESPONSIBILITY_NAME_HRRM = rBProps.getString("rESPONSIBILITY_NAME_HRRM");
		// Columns to be Fetched From HRMS ResultSet
		String eMPLOYEE_NUMBER_HRMS = rBProps.getString("eMPLOYEE_NUMBER_HRMS");
		String lOB_CODE_HRMS = rBProps.getString("lOB_CODE_HRMS");
		String lOC_CODE_HRMS = rBProps.getString("lOC_CODE_HRMS");
		String cC_CODE_HRMS = rBProps.getString("cC_CODE_HRMS");
		// String Incl_OR_Exclu = rBProps.getString("iNCLU_OR_EXCLU_CHRRM");
		if (tableName == "HRRM") {
			Iterator<Map<String, Object>> rMMapListItr1 = rMMapList.iterator();
			while (rMMapListItr1.hasNext()) {
				// for Each Entry of HR Employee Details (HRRM)
				String PERSON_NUMBER = null;
				String RESPONSIBILITY_NAME = null;
				for (Map.Entry<String, Object> hEMapListItr1Entry1 : rMMapListItr1.next().entrySet()) {
					if (hEMapListItr1Entry1.getKey().equals(pERSON_NUMBER_HRRM)) {
						PERSON_NUMBER = hEMapListItr1Entry1.getValue().toString();
						continue;
					} // if entry of PERSON_NUMBER, then continue to outer while
					else if (hEMapListItr1Entry1.getKey().equals(rESPONSIBILITY_NAME_HRRM)) {
						RESPONSIBILITY_NAME = hEMapListItr1Entry1.getValue().toString();
					}
					// if(log.isInfoEnabled())log.info(hEMapListItr1Entry1.getKey() + " : "+
					// hEMapListItr1Entry1.getValue());
					Iterator<Map<String, Object>> hRMSMapListItr2 = hRMSMapList.iterator();

					// if(log.isInfoEnabled())log.info(mDMapList);
					// if(log.isInfoEnabled())log.info(hEMapList);

					while (hRMSMapListItr2.hasNext()) {
						// for Each Entry of Master Details HRMS
						String LOB = null;
						String LOC = null;
						String CC = null;
						String EMP_ID = null;
						for (Map.Entry<String, Object> mDMapListItr2Entry2 : hRMSMapListItr2.next().entrySet()) {

							if (mDMapListItr2Entry2.getKey().equals(cC_CODE_HRMS)) {
								CC = (String) mDMapListItr2Entry2.getValue();
							} else if (mDMapListItr2Entry2.getKey().equals(lOC_CODE_HRMS)) {
								LOC = (String) mDMapListItr2Entry2.getValue();
							} else if (mDMapListItr2Entry2.getKey().equals(eMPLOYEE_NUMBER_HRMS)) {
								EMP_ID = mDMapListItr2Entry2.getValue().toString();
								// if(log.isInfoEnabled())log.info(" Working on Employee ID "+EMP_ID);
							} else if (mDMapListItr2Entry2.getKey().equals(lOB_CODE_HRMS)) {
								LOB = (String) mDMapListItr2Entry2.getValue();
							} // if else if ends
						} // inner for
							// if(log.isInfoEnabled())log.info("RESPONSIBILITY_NAME : "+
							// RESPONSIBILITY_NAME + " | " + LOB + "-" + LOC+
							// "-" +
							// CC + " : LOB-LOC-CC");
						boolean isOldEntry = false;
						// ##################### Main Business Logic
						// #####################
						if (RESPONSIBILITY_NAME.equals(LOB + "-" + LOC + "-" + CC)) {
							// if(log.isInfoEnabled())log.info("\t \t \t \t \t \t \t \t \t \t #This is
							// a
							// Match#");
							if (!sQLParametersList.isEmpty()) {
								ListIterator<Map<String, String>> sQLParametersListItr = sQLParametersList
										.listIterator();
								while (sQLParametersListItr.hasNext()) {
									Iterator<Entry<String, String>> entrySetItr = sQLParametersListItr.next().entrySet()
											.iterator();
									while (entrySetItr.hasNext()) {
										Entry<String, String> entry = entrySetItr.next();
										try {
											if (entry.getKey().equals("EMP_ID") && entry.getValue().equals(EMP_ID)) { // Checking
																														// if
																														// isOldEntry

												entry = entrySetItr.next();

												if (!entry.getValue().contains(PERSON_NUMBER)) {
													entry.setValue(entry.getValue() + "," + PERSON_NUMBER);
												}

												isOldEntry = true;
												// }
											} // if isOldEntry
										} catch (Exception ex) {
										}
									} // entrySetItr.hasNext()

								} // while sQLParametersList.hasNext()

							} // !sQLParametersList.isEmpty()

							// if This is the new Entry the Add the SQL
							// Parameters
							// in the List
							if (!isOldEntry) {
								Map<String, String> eachSQLParameter = new LinkedHashMap<String, String>();
								eachSQLParameter.put("EMP_ID", EMP_ID);
								eachSQLParameter.put("PERSON_NUMBER", PERSON_NUMBER);
								sQLParametersList.add(eachSQLParameter);
								// if(log.isInfoEnabled())log.info("Added : "+ "EMP_ID : "+EMP_ID+"
								// PERSON_NUMBER : "+PERSON_NUMBER+" to
								// eachSQLParameter");
							}
						}
						// ######### Main Business Logic Ends
						// #####################
					} // inner while
				} // outer for
			}
		} else if (tableName == "CHRRM") {
			String iNCLU_OR_EXCLU_CHRRM = rBProps.getString("iNCLU_OR_EXCLU_CHRRM");
			String wILD_CARD = rBProps.getString("wILD_CARD");
			Iterator<Map<String, Object>> rMMapListItr1 = rMMapList.iterator();
			while (rMMapListItr1.hasNext()) {

				// for Each Entry of HR Employee Details (HRRM)
				String PERSON_NUMBER = null;
				String RESPONSIBILITY_NAME = null;
				String INCLUSION_0R_EXCLUSION = null;
				for (Map.Entry<String, Object> hEMapListItr1Entry1 : rMMapListItr1.next().entrySet()) {
					if (hEMapListItr1Entry1.getKey().equals(pERSON_NUMBER_HRRM)) {
						PERSON_NUMBER = (String) hEMapListItr1Entry1.getValue();
					} // if entry of PERSON_NUMBER, then continue to outer while
					else if (hEMapListItr1Entry1.getKey().equals(rESPONSIBILITY_NAME_HRRM)) {
						RESPONSIBILITY_NAME = (String) hEMapListItr1Entry1.getValue();
					} else if (hEMapListItr1Entry1.getKey().equals(iNCLU_OR_EXCLU_CHRRM)) {
						INCLUSION_0R_EXCLUSION = (String) hEMapListItr1Entry1.getValue();
					}
				}
				// if(log.isInfoEnabled())log.info(hEMapListItr1Entry1.getKey() + " : "+
				// hEMapListItr1Entry1.getValue());
				Iterator<Map<String, Object>> hRMSMapListItr2 = hRMSMapList.iterator();

				if (RESPONSIBILITY_NAME.equals(wILD_CARD) && INCLUSION_0R_EXCLUSION.equals("I")) {
					while (hRMSMapListItr2.hasNext()) {
						String EMP_ID = null;
						for (Map.Entry<String, Object> mDMapListItr2Entry2 : hRMSMapListItr2.next().entrySet()) {

							if (mDMapListItr2Entry2.getKey().equals(eMPLOYEE_NUMBER_HRMS)) {
								EMP_ID = mDMapListItr2Entry2.getValue().toString();
							}
						}
						boolean isOldEntry = false;
						addSQLParameter(PERSON_NUMBER, EMP_ID, isOldEntry);

					}
				} else if (!RESPONSIBILITY_NAME.equals(wILD_CARD)) {

					// if(log.isInfoEnabled())log.info(mDMapList);
					// if(log.isInfoEnabled())log.info(hEMapList);

					while (hRMSMapListItr2.hasNext()) {

						// for Each Entry of Master Details HRMS
						String LOB = null;
						String LOC = null;
						String CC = null;
						String EMP_ID = null;
						for (Map.Entry<String, Object> mDMapListItr2Entry2 : hRMSMapListItr2.next().entrySet()) {

							if (mDMapListItr2Entry2.getKey().equals(cC_CODE_HRMS)) {
								CC = (String) mDMapListItr2Entry2.getValue();
							} else if (mDMapListItr2Entry2.getKey().equals(lOC_CODE_HRMS)) {
								LOC = (String) mDMapListItr2Entry2.getValue();
							} else if (mDMapListItr2Entry2.getKey().equals(eMPLOYEE_NUMBER_HRMS)) {
								EMP_ID = mDMapListItr2Entry2.getValue().toString();
								// if(log.isInfoEnabled())log.info(" Working on Employee ID
								// "+EMP_ID);
							} else if (mDMapListItr2Entry2.getKey().equals(lOB_CODE_HRMS)) {
								LOB = (String) mDMapListItr2Entry2.getValue();
							} // if else if ends
						} // inner for
						boolean isOldEntry = false;
						if ((RESPONSIBILITY_NAME.equals(LOB + "-" + LOC + "-" + CC)
								&& INCLUSION_0R_EXCLUSION.equals("I"))) {
							// if(log.isInfoEnabled())log.info("\t \t \t \t \t \t \t \t \t \t #This
							// is
							// a
							// Match#");

							addSQLParameter(PERSON_NUMBER, EMP_ID, isOldEntry);
						}
						// ######### Main Business Logic Ends
						// #####################
					}

				} // inner while
			} // outer for
		}
		// outer while ends
		return sQLParametersList;

	}// Method Business Logic() Ends

	private void addSQLParameter(String PERSON_NUMBER, String EMP_ID, boolean isOldEntry) {
		
		if (!sQLParametersList.isEmpty()) {
			ListIterator<Map<String, String>> sQLParametersListItr = sQLParametersList.listIterator();
			while (sQLParametersListItr.hasNext()) {
				Iterator<Entry<String, String>> entrySetItr = sQLParametersListItr.next().entrySet().iterator();
				while (entrySetItr.hasNext()) {
					Entry<String, String> entry = entrySetItr.next();
					try {
						if (entry.getKey().equals("EMP_ID") && entry.getValue().equals(EMP_ID)) { // Checking
																									// if
																									// isOldEntry

							entry = entrySetItr.next();

							if (!Arrays.asList(entry.getValue().split(",")).contains(PERSON_NUMBER)) {
								entry.setValue(entry.getValue() + "," + PERSON_NUMBER);
							}

							isOldEntry = true;

						}
					} catch (Exception ex) {

					} // if isOldEntry
				} // entrySetItr.hasNext()

			} // while sQLParametersList.hasNext()

		} // !sQLParametersList.isEmpty()

		// if This is the new Entry the Add the SQL
		// Parameters
		// in the List
		if (!isOldEntry) {
			Map<String, String> eachSQLParameter = new LinkedHashMap<String, String>();
			eachSQLParameter.put("EMP_ID", EMP_ID);
			eachSQLParameter.put("PERSON_NUMBER", PERSON_NUMBER);
			sQLParametersList.add(eachSQLParameter);
			// if(log.isInfoEnabled())log.info("Added : "+ "EMP_ID : "+EMP_ID+"
			// PERSON_NUMBER : "+PERSON_NUMBER+" to
			// eachSQLParameter");
		}
	}

	public static void main(String[] args) throws SQLException, ClassNotFoundException {
		// final Logger log =
		// Logger.getLogger(MasterSlaveDBreplication.class.getName());
		if(log.isInfoEnabled())log.info(" ");
		if(log.isInfoEnabled())log.info("###################### Main STARTs ######################");
		LocalDateTime startTime = LocalDateTime.now();
		ArrayList<String> updateSQLStoreLL = new ArrayList<String>(); // Stores
																		// Update
																		// Query
																		// Parameters
		String updateSQL = null; // String Used for building SQL Query Str

		Connection oRAcon_STG = null;
		ORADatabase oRAObj = new ORADatabase();
		oRAcon_STG = oRAObj.loadConnection("STG", rBProps); // Loading Server
															// Env

		Connection oRAcon = null;
		oRAcon = oRAObj.loadConnection("SERVER", rBProps); // Loading STG Env
		Statement updateDBStat = oRAcon.createStatement(); // Initializing
															// Statement Class
															// for Storing
															// updateSQL String
		// !!!!!!!!!!!!!!!!!!Warning!!!!!!!!!!!!!!
		// Used STGENV_ORAcon Only if if all 3 Tables(HRMS,HRRM & HRMS_HRRM) are
		// in Same Environment & replace all "ORAcon" by "STGENV_ORAcon"

		String hRMS_HRRMTableName = rBProps.getString("hRMS_HRRMTableName");
		String hRRMTableName_TG = rBProps.getString("hRRMTableName_TG");
		String hRMS_HRRM_PERSON_NUMBERColumnName = rBProps.getString("hRMS_HRRM_PERSON_NUMBERColumnName");
		// String hRMS_HRRM_EMPLOYEE_NUMBERColumnName =
		// rBProps.getString("hRMS_HRRM_EMPLOYEE_NUMBERColumnName");
		// ########### Re-Constructing 25 Nov 2019 ################
		String hRMS_HRRM_COMPANY = rBProps.getString("cOMPANY_HRMS");// "COMPANY";
		String hRMS_HRRM_ORACLE_EMPLOYEE_NUMBER_HRMS = rBProps.getString("oRACLE_EMPLOYEE_NUMBER_HRMS");// "ORACLE_EMPLOYEE_NUMBER";
		// ########### Re-Constructing 25 Nov 2019 ################
		try {

			// oRAObj.offAutoCommit(oRAcon_STG); // Setting AutoCommit Off for
			// Server Evn
			// if(log.isInfoEnabled())log.info("Updated Auto-commit is :
			// "+oRAObj.getAutoCommit(oRAcon_STG)); // Getting AutoCommit Off
			// for SERVER Evn

			oRAObj.offAutoCommit(oRAcon); // Setting AutoCommit Off for STG Evn
			if(log.isInfoEnabled())log.info("Updated Auto-commit is : " + oRAObj.getAutoCommit(oRAcon)); // Getting
																					// AutoCommit
																					// Off
																					// for
																					// STG
																					// Evn

			MasterSlaveDBreplication mainClassObj = new MasterSlaveDBreplication();

			// SQL for HRRMs | Tip: This is the view located in ORAcon_STG(LIVE
			// Env)
			ResultSet resultSet1 = oRAObj.fetchResultSet("HRRM", oRAcon_STG, rBProps);
			ArrayList<Map<String, Object>> hRRMMapList = new ArrayList<Map<String, Object>>();
			hRRMMapList = mainClassObj.resultSetToMapList(resultSet1);

			// SQL for HRMS | Tip: This is the view located in ORAcon_STG(LIVE
			// Env)
			ResultSet resultSet2 = oRAObj.fetchResultSet("HRMS", oRAcon_STG, rBProps);
			hRMSMapList = mainClassObj.resultSetToMapList(resultSet2);

			// SQL for CHRRM | Tip: This is the table located in ORAcon(Server
			// Env)
			ResultSet resultSet4 = oRAObj.fetchResultSet("CHRRM2", oRAcon, rBProps);// CHRRM2
			// Adding Unique Person Number Entries from CHRRM to HRMS's MapList
			System.out.println(hRMSMapList.size());
			hRMSMapList.addAll(mainClassObj.resultSetToMapList(resultSet4));
			System.out.println(hRMSMapList.size());

			// oRAObj.unloadConnection(oRAcon_STG); //
			// !!!!!!!!!!!!!!Warning!!!!!!!!!
			// if(log.isInfoEnabled())log.info("## unloaded Staging ENV connection ## "); // Staging
			// Environment means Environment Which Holds View

			// ################### Getting Custom Roles in HRMS
			// ###################
			// int isCustomUpdated = oRAObj.updateHRMSwithCHRRM(oRAcon,
			// rBProps);
			// oRAcon.commit();
			// if(log.isInfoEnabled())log.info("Total Custom Role Entries updated in HRMS Table is :
			// "+isCustomUpdated);
			// ################### Getting Custom Roles in HRMS
			// ###################

			// SQL for CumtomRoleHRRM | Tip: This is the table located in
			// oRAcon(SERVER Env)
			ResultSet resultSet3 = oRAObj.fetchResultSet("CHRRM", oRAcon, rBProps);
			ArrayList<Map<String, Object>> cHRRMMapList = new ArrayList<Map<String, Object>>();
			cHRRMMapList = mainClassObj.resultSetToMapList(resultSet3);

			// ################## Calling Business Logic
			// ################################
			if(log.isInfoEnabled())log.info("##################### Starting with Applying Business Logic #####################");
			if(log.isInfoEnabled())log.info("");

			if(log.isInfoEnabled())log.info("### Starting with Comparing Entries of 'HRRM' & 'HRMS' Table ###");
			mainClassObj.RunBusinessLogic(hRRMMapList, "HRRM");

			// if(log.isInfoEnabled())log.info("Fresh Entries Found From HRRM v/s HRMS:
			// "+sQLParametersListLocal.size());
			if(log.isInfoEnabled())log.info("### Ending with Comparing Entries of 'HRRM' & 'HRMS' Table ###");

			if(log.isInfoEnabled())log.info("### Starting with Comparing Entries of 'Custom Role HRRM' & 'HRMS' Table ###");
			mainClassObj.RunBusinessLogic(cHRRMMapList, "CHRRM");
			// if(log.isInfoEnabled())log.info("Fresh Entries Found From CHRRM v/s HRMS:
			// "+sQLParametersListLocal.size());
			if(log.isInfoEnabled())log.info("### Ending  Comparing Entries of 'Custom Role HRRM' & 'HRMS' Table ###");

			if(log.isInfoEnabled())log.info("");
			if(log.isInfoEnabled())log.info("##################### Ending with Applying Business Logic #####################");
			// ################## Calling Business Logic
			// ################################

			// ##################### Starting Building Update SQLs
			// #####################

			if (!sQLParametersList.isEmpty()) {
				if(log.isInfoEnabled())log.info("Total No. of Update SQL Entries Found: " + sQLParametersList.size());
				String sQLParameter_EMP_ID_2 = null;
				String sQLParameter_PERSON_NUMBER_2 = null;
				ListIterator<Map<String, String>> sQLParametersListItr2 = sQLParametersList.listIterator();

				while (sQLParametersListItr2.hasNext()) {
					for (Map.Entry<String, String> sQLParametersListEachEntry : (((LinkedHashMap<String, String>) sQLParametersListItr2
							.next()).entrySet())) {

						if (sQLParametersListEachEntry.getKey().equals("EMP_ID")) {
							sQLParameter_EMP_ID_2 = sQLParametersListEachEntry.getValue();
						} else if (sQLParametersListEachEntry.getKey().equals("PERSON_NUMBER")) {
							sQLParameter_PERSON_NUMBER_2 = sQLParametersListEachEntry.getValue();
						}
					} // for sQLParametersListEachEntry

					// ########### Re-Constructing 25 Nov 2019
					// ################(Also changes in ORADatbase Line No 104)
					// updateSQL = "UPDATE "+hRMS_HRRMTableName+" SET
					// "+hRMS_HRRM_PERSON_NUMBERColumnName+" = '"+
					// sQLParameter_PERSON_NUMBER_2+
					// "' WHERE "+hRMS_HRRM_EMPLOYEE_NUMBERColumnName+" = '"+
					// sQLParameter_EMP_ID_2 + "'";
					updateSQL = "UPDATE " + hRMS_HRRMTableName + " SET " + hRMS_HRRM_PERSON_NUMBERColumnName + " = '"
							+ sQLParameter_PERSON_NUMBER_2 + "' WHERE CONCAT(" + hRMS_HRRM_COMPANY + ", "
							+ hRMS_HRRM_ORACLE_EMPLOYEE_NUMBER_HRMS + ") = '" + sQLParameter_EMP_ID_2 + "'";
							// ########### Re-Constructing 25 Nov 2019
							// ################

					// if(log.isInfoEnabled())log.info("updateSQL Added to StatementBatch :
					// "+updateSQL);
					updateSQLStoreLL.add(updateSQL);
					updateDBStat.addBatch(updateSQL);
				} // sQLParametersListItr2.hasNext()

				UDBatchForBackupDB uDBatchObj = new UDBatchForBackupDB(); // Created
																			// UDBatchForBackupDB()
																			// Instance
																			// to
																			// call
																			// Methods
																			// from
																			// UDBatchForBackupDB
																			// Class

				// ###################### Updating HRRM_TG with Fresh Data Daily
				// Starts #########################
				if(log.isInfoEnabled())log.info("##################### Starting with Updating HRRM Data #####################");
				if(log.isInfoEnabled())log.info("");
				// ############ Blind Copying Data into HRRM From LIVE View Env
				// #########################
				uDBatchObj.fireTruncateNInsertDDL_forHRRM(oRAcon_STG, oRAcon, rBProps);// Call
																						// Method
																						// to
																						// load
																						// Fresh
																						// Data
				oRAObj.commit(oRAcon); // Committing changes in Server ENV
				// ########### Blind Copying Data into HRRM From LIVE View Env
				// #########################

				// ########### Starting with Correcting Person_Number AS
				// CONCAT(COMPANY+OEN) HRRM_TG Table ##############
				// new GettingHRRMCorrect().getHRRMPersonNumberRight(oRAcon_STG,
				// oRAcon);
				// oRAObj.commit(oRAcon); // Committing changes in Server ENV
				// ########### Ending with Correcting Person_Number AS
				// CONCAT(COMPANY+OEN) HRRM_TG Table ##############
				if(log.isInfoEnabled())log.info("");
				if(log.isInfoEnabled())log.info("##################### Ending with Updating HRRM Data #####################");
				// ###################### Updating HRRM_TG with Fresh Data Daily
				// Ends #########################

				// ###################### Updating HRMS_HRRM with Fresh Data
				// Daily #########################
				if(log.isInfoEnabled())log.info("##################### Starting with Updating HRMS_HRRM Data #####################");
				if(log.isInfoEnabled())log.info("");

				uDBatchObj.fireTruncateNInsertDDL(oRAcon_STG, oRAcon, rBProps);// Call
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
				if(log.isInfoEnabled())log.info("## unloaded LIVE VIEW connection ## "); // Staging
																	// Environment
																	// means
																	// Environment
																	// Which
																	// Holds
																	// View
				// ###################### Updating HRMS_HRRM with Fresh Data
				// Daily #########################

				// ###################### Executing Batch Statement
				// ########################################
				int[] updateSQLSucessArray = uDBatchObj.fireUpdateSQL(updateDBStat);

				oRAObj.commit(oRAcon);
				// Committing changes in Server ENV
				if(log.isInfoEnabled())log.info("# Committed changes in DB #");
				if(log.isInfoEnabled())log.info("");
				if(log.isInfoEnabled())log.info("##################### Ending with Updating HRMS_HRRM Data #####################");
				// ###################### Executing Batch Ends
				// #############################################
				if(log.isInfoEnabled())log.info("");
				// ########### Un-Comment This to Get the Details Fired Queries
				// ###################
				/// *
				if(log.isInfoEnabled())log.info("###################### Summary of Update Batch Statement ############################");
				int success = 0;
				for (int index = 0; index < updateSQLSucessArray.length; ++index) {
					if (updateSQLSucessArray[index] == 1) {
						success++;
					} else {
						if(log.isInfoEnabled())log.info("Failed :" + updateSQLStoreLL.get(index));
					}
					// if(log.isInfoEnabled())log.info( ( updateSQLSucessArray[index]==1 ?
					// "success":"failed" )+" : "+updateSQLStoreLL.get(index) );
				}

				if(log.isInfoEnabled())log.info("Total Success Count: " + success);
				if(log.isInfoEnabled())log.info(
						"###################### Summary of Update Batch Statement(s) Ends ############################");
				// */
			} // !sQLParametersList.isEmpty()
			else {
				if(log.isInfoEnabled())log.info("## No Update Entries Found ##");
			}

			// ##################### Ending Building Update SQLs
			// #####################

			// ##################### Shuffling AdUsers #####################
			if(log.isInfoEnabled())log.info("############ Starting With AdUsers Shuffling ############");
			ActiveDirectoryApp aDAObj = new ActiveDirectoryApp();

			aDAObj.shuffleAdUsers(oRAcon);

			if(log.isInfoEnabled())log.info("############ Ending With AdUsers Shuffling ############");
			// ##################### Shuffling AdUsers #####################

			// ##################### Closing STG Connection
			// #####################
			oRAObj.unloadConnection(oRAcon);
			if(log.isInfoEnabled())log.info("## unloaded HRUPM DB connection ##");
			// ##################### Closing STG Connection
			// #####################

			try {
				if(log.isInfoEnabled())log.info(new EmailNotification().generateAndSendEmail("Success"));
			} catch (AddressException e2) {
				e2.printStackTrace();
				if(log.isInfoEnabled())log.info(e2.getLocalizedMessage());
			} catch (MessagingException e2) {
				e2.printStackTrace();
				if(log.isInfoEnabled())log.info(e2.getLocalizedMessage());
			}

			if(log.isInfoEnabled())log.info("###################### Main ENDs ######################");
			LocalDateTime endTime = LocalDateTime.now();
			System.out.println("## Progarm started at 		:"
					+ DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(startTime) + " ##");
			System.out.println("## Progarm terminated at 	:"
					+ DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(endTime) + " ##");
			System.out.println("## For More Info Kindly See Logs File ###");
		} catch (NullPointerException e) {// Catch #4
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
				if(log.isInfoEnabled())log.info(new EmailNotification().generateAndSendEmail("NullPointerException"));
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
				if(log.isInfoEnabled())log.info(new EmailNotification().generateAndSendEmail("SQLSyntaxErrorException"));
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
				if(log.isInfoEnabled())log.info(new EmailNotification().generateAndSendEmail("SQLException"));
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
				if(log.isInfoEnabled())log.info(new EmailNotification().generateAndSendEmail("Exception"));
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
