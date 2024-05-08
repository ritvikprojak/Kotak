package com.ktk.Kotak.filenetops;



import javax.security.auth.Subject;

import org.apache.log4j.Logger;

import com.filenet.api.core.Connection;
import com.filenet.api.core.Domain;
import com.filenet.api.core.Factory;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.util.UserContext;
import com.ktk.Kotak.filenetops.ssl.SSLConnection;
import com.ktk.Kotak.util.ResourceLoader;



public class FilenetConnection {

	private static final Logger logger = Logger.getLogger(FilenetConnection.class);
	private String ceUrl;

	private String objectStoreName;

	private String domain;

	private String username;

	private String password;
	
	private ResourceLoader loader;


	public FilenetConnection() {
		super();
		loader=new ResourceLoader();
	}

	
	public Connection getConnection()
	{
		
		
			logger.info("Inside new Connection");
			// System.out.println("Inside new Connection");
			ceUrl=loader.getValue("filenet.url");
			logger.info("CE URL "+ ceUrl);
			// System.out.println("CE URL "+ ceUrl);
			username=loader.getValue("filenet.user.name");
			logger.info("Username "+username);
			// System.out.println("Username "+username);
			password=loader.getValue("filenet.password");
			//logger.info("Password "+password);
			//// System.out.println("Password "+password);
			
			String initialContext=loader.getValue("initialContext");
			// System.out.println("Initial context "+initialContext);
			logger.info("Initial context "+initialContext);
			
			/*String trustStore=loader.getValue("javax.net.ssl.trustStore");
			// System.out.println("javax.net.ssl.trustStore "+trustStore);
			logger.info("javax.net.ssl.trustStore "+trustStore);
			
			String trustStorePassword=loader.getValue("javax.net.ssl.trustStorePassword");
			// System.out.println("javax.net.ssl.trustStorePassword "+trustStorePassword);
			logger.info("javax.net.ssl.trustStorePassword "+trustStorePassword);*/
			
			System.setProperty("java.naming.factory.initial", initialContext);
			
			try {
				logger.info("Before calling setupSSL method @@@@@ ");
				SSLConnection.setupSSL();
				logger.info("After calling setupSSL method @@@@@ ");
				Connection connInstance = Factory.Connection.getConnection(ceUrl);
				logger.info("Connection Instance "+connInstance);
				// System.out.println("Connection Instance "+connInstance);
				Subject subject = UserContext.createSubject(connInstance, username,password, null);
				logger.info("Subject "+subject);
				// System.out.println("Subject "+subject);
				UserContext.get().pushSubject(subject);
				
				return connInstance;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logger.info("Exception while making SSL connection: "+e.getMessage());
			}
			return null;
				
	}

	public ObjectStore getObjectStore() {
		logger.info("Start of getObjectStore() method.");
		// System.out.println("Start of getObjectStore() method.");
		try {
			domain=loader.getValue("filenet.domain");
			objectStoreName=loader.getValue("filenet.object.store");
			logger.info("Domain ."+domain);
			// System.out.println("Domain ."+domain);
			logger.info("objectStoreName "+ objectStoreName);
			// System.out.println("objectStoreName "+ objectStoreName);
			Domain domInstance = Factory.Domain.fetchInstance(getConnection(),	domain, null);
			// System.out.println("DomInstance ."+domInstance.get_Name());
			logger.info("DomInstance ."+domInstance.get_Name());
			
			ObjectStore objectStore = Factory.ObjectStore.fetchInstance(domInstance,objectStoreName, null);
			// System.out.println("objectStore ."+objectStore.get_Name());
			logger.info("objectStore ."+objectStore.get_Name());
			// System.out.println("ObjectStore instance: {} successfully fetched."+  objectStore.get_Name());
			//logger.debug("ObjectStore instance: {} successfully fetched.",  objectStore.get_Name());
			//logger.debug("End of getObjectStore() method. ");

			return objectStore;

		} catch (Exception exception) {
			logger.error("Error occurred while Fetching the Objectstore/Domain."+exception);
			throw exception;
		}
	}

	public void destroyConnection()
	{
	
			logger.info("Inside destroying connection");
			UserContext.get().popSubject();
		
	}

}
