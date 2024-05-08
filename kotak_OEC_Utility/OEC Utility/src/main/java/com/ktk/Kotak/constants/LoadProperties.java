package com.ktk.Kotak.constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class LoadProperties {
	
	static Properties properties = new Properties();

	
	public  LoadProperties() {


		File file = new File("/opt/IBM/CEEVENT/MigrationUtil.properties");

		FileInputStream fileInput = null;

		try {

			fileInput = new FileInputStream(file);

			properties.load(fileInput);

		} catch (IOException e) {

			// TODO Auto-generated catch block

			e.printStackTrace();

		}


	}
	public static String getValue(String keyName)
	{
		 
		return properties.getProperty(keyName);
	}

}
