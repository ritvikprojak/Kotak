package dBInterfacePackageImpl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.ListIterator;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import cRUDUtilityClass.UDBatchForBackupDB;
import dDReplication.MasterSlaveDBreplication;
import resourceBundlePackage.ResourceBundleClass;


public class GettingHRRMCorrect {

	final static Logger log = Logger.getLogger(GettingHRRMCorrect.class.getName());
	static ResourceBundleClass rBC = new ResourceBundleClass();
	public final static ResourceBundle rBProps = rBC.getResourceBundle();
	static ArrayList<Map<String, String>> sQLParametersList = new ArrayList<Map<String, String>>();
	
	
	// This method Runs pre_Business Logic  & Returns the parameter's List Required for Update DML in HRRM
	public ArrayList<Map<String,String>> pre_RunBusinessLogic(
			ArrayList<Map<String, Object>> rMMapList,
			ArrayList<Map<String, Object>> hRMSMapList
			){
				//Columns to be Fetched from HRRM ResultSet
				String pERSON_NUMBER_HRRM = rBProps.getString("pERSON_NUMBER_HRRM");
				
				// Columns to be Fetched From HRMS ResultSet
				String eMPLOYEE_NUMBER_HRMS = rBProps.getString("eMPLOYEE_NUMBER_HRMS");
				String cOMPANY_HRMS = rBProps.getString("cOMPANY_HRMS");//"COMPANY";
				String oRACLE_EMPLOYEE_NUMBER_HRMS = rBProps.getString("oRACLE_EMPLOYEE_NUMBER_HRMS");//"ORACLE_EMPLOYEE_NUMBER";
						
		
				
		Iterator<Map<String, Object>> rMMapListItr1 = rMMapList.iterator();
		while (rMMapListItr1.hasNext()) {
		// for Each Entry of HR Employee Details (HRRM)
			String PERSON_NUMBER = null;
			
			for (Map.Entry<String, Object> hEMapListItr1Entry1 : rMMapListItr1.next().entrySet()) {
			
				if (hEMapListItr1Entry1.getKey().equals(pERSON_NUMBER_HRRM)) {
					PERSON_NUMBER = hEMapListItr1Entry1.getValue().toString();
				}
				}
				//if(log.isInfoEnabled())log.info(hEMapListItr1Entry1.getKey() + " : "+ hEMapListItr1Entry1.getValue());
				Iterator<Map<String, Object>> hRMSMapListItr2 = hRMSMapList.iterator();
				
				//if(log.isInfoEnabled())log.info(mDMapList);
				//if(log.isInfoEnabled())log.info(hEMapList);
				
				
				while (hRMSMapListItr2.hasNext()) {
				// for Each Entry of Master Details HRMS 
					Object oEN = null;
					String cOMPANY = null;
					String EMP_ID = null;
					for (Map.Entry<String, Object> mDMapListItr2Entry2 : hRMSMapListItr2.next().entrySet()) {
						
						if (mDMapListItr2Entry2.getKey().equals(oRACLE_EMPLOYEE_NUMBER_HRMS)) {
							oEN =  (Object) mDMapListItr2Entry2.getValue();
						}  else if (mDMapListItr2Entry2.getKey().equals(eMPLOYEE_NUMBER_HRMS)) {
							EMP_ID = mDMapListItr2Entry2.getValue().toString();
							//if(log.isInfoEnabled())log.info(" Working on Employee ID "+EMP_ID);
						} else if (mDMapListItr2Entry2.getKey().equals(cOMPANY_HRMS)) {
							cOMPANY = (String) mDMapListItr2Entry2.getValue();
						}// if else if ends
					}// inner for
					//if(log.isInfoEnabled())log.info("PERSON_NUMBER : "+PERSON_NUMBER+ " | " +EMP_ID+ " : EMP_ID");
					
					// ##################### Main Business Logic #####################
					if (PERSON_NUMBER.equals(EMP_ID)) {

							Map<String, String> eachSQLParameter = new LinkedHashMap<String, String>();
							eachSQLParameter.put( "EMP_ID",EMP_ID );
							eachSQLParameter.put( "PERSON_NUMBER",cOMPANY+oEN );
							sQLParametersList.add( eachSQLParameter );
							//if(log.isInfoEnabled())log.info("Added : "+ "EMP_ID : "+EMP_ID+" PERSON_NUMBER : "+PERSON_NUMBER+" to eachSQLParameter");
							
					}
					// ######### Main Business Logic Ends #####################
				
			}// outer for
		}// outer while ends
		return sQLParametersList;
		
	}// Method Business Logic() Ends
	
	// Call this Method(Before MainBussiness Logic) from Main Method to Get your HRRM Table Corrected
	public void getHRRMPersonNumberRight(Connection oRAcon_STG,Connection oRAcon  ) throws SQLException{
		
		ArrayList<String> updateSQLStoreLL = new ArrayList<String>();
		String pre_UpdateSQL=null; // String Used for building SQL Query Str
		Statement updateDBStat = oRAcon.createStatement(); // Initializing Statement Class for Storing updateSQL String
		MasterSlaveDBreplication mainClassObj =  new MasterSlaveDBreplication();
		ORADatabase oRAObj = new ORADatabase();
		
		// SQL for HRRMs | Tip: This is the view located in ORAcon(Server Env)
		ResultSet pre_ResultSet1 = oRAObj.fetchResultSet("pre_HRRM", oRAcon_STG, rBProps); 
		ArrayList<Map<String, Object>> pre_hRRMMapList = new ArrayList<Map<String, Object>>();
		pre_hRRMMapList =  mainClassObj.resultSetToMapList(pre_ResultSet1);
		
		// SQL for HRMS | Tip: This is the view located in ORAcon(STG Env)
		ResultSet pre_ResultSet2 = oRAObj.fetchResultSet("pre_HRMS", oRAcon_STG, rBProps);
		ArrayList<Map<String, Object>> pre_hRMSMapList = new ArrayList<Map<String, Object>>();
		pre_hRMSMapList =  mainClassObj.resultSetToMapList(pre_ResultSet2);
		
		sQLParametersList = new GettingHRRMCorrect().pre_RunBusinessLogic(pre_hRRMMapList,pre_hRMSMapList);
		System.out.println("sQLParametersList :"+sQLParametersList);
		

		if(!sQLParametersList.isEmpty()){
			
			System.out.println( "Total No. of Update SQL Entries Found FOR HRRM_IN Table : "+sQLParametersList.size() );
			String sQLParameter_EMP_ID_2=null;
			String sQLParameter_PERSON_NUMBER_2=null;
			ListIterator<Map<String, String>> sQLParametersListItr2 = sQLParametersList.listIterator();
			
			while( sQLParametersListItr2.hasNext() ) {
				for ( Map.Entry<String,String> sQLParametersListEachEntry : ( ( (LinkedHashMap<String,String>) sQLParametersListItr2.next() ).entrySet()) ) {
					
					if(sQLParametersListEachEntry.getKey().equals("EMP_ID")) {
						sQLParameter_EMP_ID_2 = sQLParametersListEachEntry.getValue();
					}else if( sQLParametersListEachEntry.getKey().equals("PERSON_NUMBER") ) {
						sQLParameter_PERSON_NUMBER_2 = sQLParametersListEachEntry.getValue();
					}
				}// for sQLParametersListEachEntry	
				
				String hRRMTableName_TG = rBProps.getString("hRRMTableName_TG");//"HRUPM.HRRM";
				String pERSON_NUMBER_HRRM_TG = rBProps.getString("pERSON_NUMBER_HRRM_TG");//"PERSON_NUMBER";
				
				//////////////////////// 29 Nov 2019 Start//////////////////////////////////
				String hR_RM_HRRM_TG = rBProps.getString("hR_RM_TG");//"HR_RM";
				pre_UpdateSQL = "UPDATE "+hRRMTableName_TG+" SET "+hR_RM_HRRM_TG+" = '"+ sQLParameter_PERSON_NUMBER_2+
						"' WHERE "+pERSON_NUMBER_HRRM_TG+" = '"+ sQLParameter_EMP_ID_2 + "'";
				
//				//////////////////////29 Nov 2019 End//////////////////////////////////
					
				//pre_UpdateSQL = "UPDATE "+hRRMTableName_TG+" SET "+pERSON_NUMBER_HRRM_TG +" = '"+ sQLParameter_PERSON_NUMBER_2+
				//			"' WHERE "+pERSON_NUMBER_HRRM_TG+" = '"+ sQLParameter_EMP_ID_2 + "'";
				
																  
				//pre_UpdateSQL = "UPDATE "+hRRMTableName_TG+" SET "+pERSON_NUMBER_HRRM_TG +" = '"+ sQLParameter_EMP_ID_2+
				//		"' WHERE "+pERSON_NUMBER_HRRM_TG+" = '"+ sQLParameter_PERSON_NUMBER_2 + "'";
					
				
					
					//if(log.isInfoEnabled())log.info("updateSQL Added to StatementBatch : "+pre_UpdateSQL);
				
					updateSQLStoreLL.add(pre_UpdateSQL);
					updateDBStat.addBatch(pre_UpdateSQL);
			}//sQLParametersListItr2.hasNext()	
	}else{
		if(log.isInfoEnabled())log.info("## No Update Entries Found for HRRM_TG ##");
	}
		
		
		
		UDBatchForBackupDB uDBatchObj = new  UDBatchForBackupDB(); // Created UDBatchForBackupDB() Instance to call Methods from UDBatchForBackupDB Class
		//###################### Executing Batch Statement ########################################
		
		int[] updateSQLSucessArray = uDBatchObj.fireUpdateSQL(updateDBStat);
		
		//###################### Executing Batch Ends #############################################
		
		//########### Un-Comment This to Get the Details Fired Queries ###################
			
		for (int index = 0; index < updateSQLSucessArray.length; ++index) {
			System.out.println( ( updateSQLSucessArray[index]==1 ? "success":"failed" )+" : "+updateSQLStoreLL.get(index) );
		}		
			
	}
	
	public static void main(String[] args) {
		
		Connection oRAcon_STG = null;
		Connection oRAcon = null;
		ORADatabase oRAObj = new ORADatabase();
	
		try {
		
			oRAcon_STG = oRAObj.loadConnection("STG",rBProps);
			oRAcon = oRAObj.loadConnection("SERVER",rBProps); // Loading STG Env
			new GettingHRRMCorrect().getHRRMPersonNumberRight(oRAcon_STG,oRAcon);
			oRAObj.commit(oRAcon); // Committing changes in Server ENV
			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // Loading Server Env 
 catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
