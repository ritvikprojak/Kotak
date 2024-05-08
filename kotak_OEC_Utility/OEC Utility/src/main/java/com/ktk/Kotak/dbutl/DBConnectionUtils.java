package com.ktk.Kotak.dbutl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.ktk.Kotak.util.CripUtils;
import com.ktk.Kotak.util.ResourceLoader;

public class DBConnectionUtils {
	
	ResourceLoader loader=null;
	

	private static final Logger logger = Logger.getLogger(DBConnectionUtils.class);
	public DBConnectionUtils() {
		super();
		loader=new ResourceLoader();
		
	}

	public Connection getConnection() {
		Connection conn=null;
		String driver=loader.getValue("spring.datasource.driver-class-name");
		String dbUrl=loader.getValue("spring.datasource.url");
		String dbUserId=loader.getValue("spring.datasource.username");
		String dbPassword=loader.getValue("spring.datasource.password");
		
		try {
			dbUserId = CripUtils.decryptStr(dbUserId, "HU58YZ3CR9");
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			System.err.println("Error occured while decrypting dbUserID: "+dbUserId);
			logger.error("Error occured while decrypting dbUserID: "+dbUserId);
		}
		
		try {
			dbPassword = CripUtils.decryptStr(dbPassword, "HU58YZ3CR9");
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			System.err.println("Error occured while decrypting dbPassword: "+dbPassword);
			logger.error("Error occured while decrypting dbPassword: "+dbPassword);
			
		}
		
		logger.info("driver:: "+driver);
		logger.info("dbUrl:: "+dbUrl);
		logger.info("dbUserId:: "+dbUserId);
		logger.info("dbPassword:: "+dbPassword);
		
		try {
			Class.forName(driver);
			conn= DriverManager.getConnection(dbUrl, dbUserId, dbPassword);
			// System.out.println("DB Connection establish "+conn);
			logger.info("DB Connection establish "+conn);
			if(conn==null) {
				throw new Exception("DB Connection could not be established");
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			// System.out.println("Class Not Found Exception getConnection method"+e.getMessage());
			logger.error("Class Not Found Exception getConnection method"+e.getMessage());
		} catch (SQLException e) {
			e.printStackTrace();
			// System.out.println("SQL Exception getConnection method "+e.getMessage());
			logger.error("SQL Exception getConnection method "+e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			// System.out.println("Exception getConnection method"+e.getMessage());
			logger.error("Exception getConnection method"+e.getMessage());
		}
		return conn;
	}
	
}
