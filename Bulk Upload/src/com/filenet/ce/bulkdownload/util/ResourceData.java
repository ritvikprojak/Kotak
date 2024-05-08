package com.filenet.ce.bulkdownload.util;

import java.util.ResourceBundle;

public class ResourceData {

	public static ResourceBundle getResourceBundle() 
	  {
		 ResourceBundle rsbundle = null;
//		 FileInputStream fis = null;
		try {
//			 fis= new FileInputStream("C:\\Users\\qcc\\git\\dms\\UploadSearchDocument\\src\\Resources\\config.properties");
//			 fis= new FileInputStream("/fs1/IBM/CC/Resources/config.properties");
//			 fis= new FileInputStream("Resource.config.properties");
			 rsbundle = ResourceBundle.getBundle("Resource.config");
			 
//			 fis= new FileInputStream("ComplianceConfig.properties");
//			rsbundle = new PropertyResourceBundle(fis);
//			fis.close();
			
		} /*catch (FileNotFoundException e) {
			System.out.println("Property File Not Found "+e.fillInStackTrace());
			
		} catch (IOException e) {
			System.out.println("Property File Not Found "+e.fillInStackTrace());
		}*/
		catch(Exception e){
			e.printStackTrace();
			
			System.out.println(e.fillInStackTrace());
			
		}
		finally{
			
			return rsbundle;
			
		}
//		return rsbundle;
	  }
	
	public static void main (String [] args){
		String str = ResourceData.getResourceBundle().getString("RootFolder");
		System.out.println(str);
	}
	
	
}
