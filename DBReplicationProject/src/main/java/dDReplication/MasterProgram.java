package dDReplication;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import cRUDUtilityClass.UDBatchForBackupDB;
import dBInterfacePackageImpl.ORADatabase;
import resourceBundlePackage.ResourceBundleClass;

public class MasterProgram {
	
	static {
		LocalDateTime startTime = LocalDateTime.now();
		String logFileName = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss").format(startTime);
		System.setProperty("current.date.time", logFileName);
	}
	
	final static Logger log = Logger.getLogger(MasterProgram.class);
	static ResourceBundleClass propClass = new ResourceBundleClass();
	public final static ResourceBundle prop = propClass.getResourceBundle();
	
	public static ArrayList<Map<String, Object>> resultSetToMapList(ResultSet rs) throws SQLException {

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
	}
	
	public static void main(String[] args) {
		
		if(log.isInfoEnabled())log.info(" ");
		if(log.isInfoEnabled())log.info("###### Main Started");
		LocalDateTime startTime = LocalDateTime.now();
		
		Connection stagingConnection = null;
		Connection targetConnection = null;
		ORADatabase ora = new ORADatabase();
		try {
			stagingConnection = ora.loadConnection("STG", prop);
			targetConnection = ora.loadConnection("SERVER", prop);
			
			ora.offAutoCommit(stagingConnection);
			
//			ResultSet resultSet1 = ora.fetchResultSet("HRRM", stagingConnection, prop);
//			ArrayList<Map<String, Object>> hRRMMapList = new ArrayList<Map<String, Object>>();
//			hRRMMapList = resultSetToMapList(resultSet1);
			
			UDBatchForBackupDB uDBatchObj = new UDBatchForBackupDB();
			if(log.isInfoEnabled())log.info("hitting method fireTruncateNInsertDDL_forHRRM");
			uDBatchObj.fireTruncateNInsertDDL_forHRRM(stagingConnection, targetConnection, prop);
			if(log.isInfoEnabled())log.info("hitting method FireTruncNSnapHRMS_HRRM");
			uDBatchObj.FireTruncNSnapHRMS_HRRM(stagingConnection, targetConnection, prop);
			
			System.out.println("Insert Completed");
			if(log.isInfoEnabled())log.info("Insert Completed");
			System.out.println("Start Time - "+startTime.toString());
			if(log.isInfoEnabled())log.info("Start Time : " + startTime.toString());
			System.out.println("Finish Time - "+ LocalDateTime.now().toString());
			if(log.isInfoEnabled())log.info("Finish Time : " +  LocalDateTime.now().toString());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			log.error("ClassNotFound Exception : "  + e.getMessage()) ;
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			log.error("SQLException  : "  + e.getMessage()) ;
			e.printStackTrace();
		}
		
	}

}
