/**
 * 
 */
/**
 * @author sanket
 *
 */
package resourceBundlePackage;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import org.apache.log4j.Logger;

public class ResourceBundleClass{
	
	final Logger log = Logger.getLogger(ResourceBundleClass.class.getName());
	FileInputStream fis = null;
	ResourceBundle resourceBundle = null;
	//String basePath= "C:/Users/admin/Desktop/KotakKeFiles/";
	String basePath= "D:/DB_UTILITY/"; //reserved for Client Side Base Path
	String configPath = basePath+"HRMS_HRRM_Mapping Utility/DBTableMappingActions/config/config.properties"; // Windows OS path
//	String configPath="D:\\sanket\\workspace\\DBReplicationProject\\src\\main\\resources\\config.properties";
	//String sessionConfigPath = basePath+"HRMS_HRRM_Mapping Utility/DBTableMappingActions/config/sessionConfigPath.properties"; // Stores Session Details
	//String configPath = "/fs1/IBM/ICNAutoActions/config/config.properties"; // linux OS path example
	
	//Method Fetches Parameters from config.properties
	public ResourceBundle getResourceBundle() {

		
		try {
				fis = new FileInputStream(configPath); // Change the Path According to EVN
				resourceBundle = new PropertyResourceBundle(fis);
				//if(log.isInfoEnabled())log.info("Parameters Fetched via config.properties: "+resourceBundle.keySet().toString());
				fis.close();
			} catch (FileNotFoundException e) {
				if(log.isInfoEnabled())log.info("Property File Not Found "
						+ e.fillInStackTrace());
				if(log.isInfoEnabled())log.info(
						"FileNotFoundException warn Msg: ",
						new Error(
								"!!! CONFIG.PROPERTIES FILE NOT FOUND AT "+configPath+"  !!!"));
				fis = null;
				System.exit(0);
			} catch (IOException e) {
				if(log.isInfoEnabled())log.info("IOException Msg : " + e.fillInStackTrace());
				if(log.isInfoEnabled())log.info(
						"IOException warn Msg : ",
						new Error(
								"!!An IOException Occured somthing bad with config.properties file!!"));
				fis = null;
				System.exit(0);
			} finally {
				fis = null;
			}
			return resourceBundle;
	} // ResourceBundle

/*
	// Not Used/Called in project
	public void setLastSessionResourceBundle(String setWhat, String setValue){
		
		try{
			File sessionconfigFile = new File(sessionConfigPath);
			FileReader reader = new FileReader(sessionconfigFile);
			Properties props = new Properties(); 
			// load the properties file:
			props.load(reader);
			reader.close();
			if(setWhat=="isException"){
				//setValue="1";
				props.setProperty(setWhat, setValue);
			}
//				else if(setWhat=="nowDate"){
//				setValue= DateTimeFormatter.ofPattern("yyyy/MM/dd HH:MM:SS").format(LocalDateTime.now());
//				props.setProperty(setWhat, setValue);
//		}
			// Writing on sessionconfig.properties file
			FileWriter writer = new FileWriter(sessionConfigPath);
			props.store(writer,"These are the Last Session Details & HRMR_HRM Table will be restored by Data from HRMS_HRRM_snap");
			writer.close();
			
			//System.out.println("DONE");
		}
		catch (Exception e){
		    e.printStackTrace();
		}
	}//setResourceBundle()
	// Not Used/Called in project
	public String getLastSessionResourceBundle(String getWhat){
		try{
			File sessionconfigFile = new File(sessionConfigPath);
			FileReader reader = new FileReader(sessionconfigFile);
			Properties props = new Properties(); 
			props.load(reader);// load the properties file:
			reader.close();
			String isExceptionValue = props.getProperty(getWhat);
			return isExceptionValue;
		}catch (Exception e){
		    e.printStackTrace();
		}
		return null;
	}
*/		
//	public static void main(String[] args){
//		
//		ResourceBundleClass rB = new ResourceBundleClass();
//		rB.setResourceBundle("isException",null);
//		rB.setResourceBundle("nowDate",null);
//		
//	}
		
	
}//class
