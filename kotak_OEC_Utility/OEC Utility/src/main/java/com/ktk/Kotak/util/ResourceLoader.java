package com.ktk.Kotak.util;

import java.io.InputStream;
import java.util.Properties;

public class ResourceLoader {

private static Properties prop=new Properties();
	
	public ResourceLoader() {
		try {
			InputStream inputStream = getClass().getClassLoader().getResourceAsStream("application.properties");
			ResourceLoader.prop.load(inputStream);
		}
		catch(Exception e) {
			// System.out.println("Exception occured "+e.getLocalizedMessage());
		}
	}
	
	public ResourceLoader(String fileName) {
		try {
			InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
			ResourceLoader.prop.load(inputStream);
		}
		catch(Exception e) {
			// System.out.println("Exception occured "+e.getLocalizedMessage());
		}
	}
	
	public String getValue(String key) {
		if(!prop.containsKey(key)) {
			// System.out.println("key"+key+" not found in properties file");
			throw new IllegalArgumentException(key);
		}
		return prop.getProperty(key);
	}

	public String getString(String string) {
		// TODO Auto-generated method stub
		return null;
	}
}
