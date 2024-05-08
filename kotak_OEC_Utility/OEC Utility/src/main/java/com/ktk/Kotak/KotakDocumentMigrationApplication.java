package com.ktk.Kotak;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Properties;
import java.util.Scanner;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

import com.ktk.Kotak.dbutl.DBOperations;
import com.ktk.Kotak.util.ResourceLoader;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
public class KotakDocumentMigrationApplication {
	
	//private static Logger logger = LoggerFactory.getLogger(KotakDocumentMigrationApplication.class);
	public static void main(String[] args) throws IOException {
		//SpringApplication.run(KotakDocumentMigrationApplication.class, args);
		final Logger logger = Logger.getLogger(KotakDocumentMigrationApplication.class);
		
		ResourceLoader loader = new ResourceLoader();
		
		String logFilePath=loader.getValue("logFilePath");
		// System.out.println("logFilePath: "+logFilePath);
		 logger.info("logFilePath: "+logFilePath);
		
		Properties prop1 = new Properties();
		FileInputStream ip1 = new FileInputStream(logFilePath);
		prop1.load(ip1);
		PropertyConfigurator.configure(prop1);
         
        LocalDateTime startTime = LocalDateTime.now();
 		logger.info("## Progarm started at 		:"
 				+ DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(startTime) + " ##");
		
		Scanner sc= new Scanner(System.in);    //System.in is a standard input stream  
		System.out.print("Enter No of Documents to be extracted : ");  
		int count= sc.nextInt();  
		
		// System.out.println("no of documents to be extracted "+count);
		logger.info("no of documents to be extracted "+count);
		
		DBOperations dbOperations=new DBOperations();
		
		// System.out.println("Calling executeBatch method from main method");
		logger.info("Calling executeBatch method from main method");
		// System.out.println(new Date());
		try {
			dbOperations.executeBatch(count);
			
//			try {
//				emailNotification.generateAndSendEmail("Success",count);
//				logger.info("Success email send");
//			} catch (AddressException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//				logger.error("Error occured while sending email");
//			} catch (MessagingException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//				logger.error("Error occured while sending email");
//			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("Error while pushing TIF images to FileNet System");
			
//			try {
//				emailNotification.generateAndSendEmail("Failed",count);
//				logger.info("Failed email send");
//			} catch (AddressException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//				logger.error("Error occured while sending email");
//			} catch (MessagingException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//				logger.error("Error occured while sending email");
//			}
			
			LocalDateTime endTime = LocalDateTime.now();
			
			logger.info("## Progarm terminated at 	:"
					+ DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(endTime) + " ##");
			logger.error("An exception occurred in KotakDocumentMigrationApplication executeBatch() the  method."+e.getMessage());
		}
		finally {
			LocalDateTime endTime = LocalDateTime.now();
			
			logger.info("## Progarm terminated at 	:"
					+ DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(endTime) + " ##");
		}
	}

}
