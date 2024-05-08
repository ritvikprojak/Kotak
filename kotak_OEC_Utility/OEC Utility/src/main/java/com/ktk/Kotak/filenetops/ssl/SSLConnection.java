package com.ktk.Kotak.filenetops.ssl;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.apache.log4j.Logger;

import com.ktk.Kotak.dbutl.DBOperations;
import com.ktk.Kotak.util.ResourceLoader;

public class SSLConnection {

	private static final Logger logger = Logger.getLogger(SSLConnection.class);
	public static void setupSSL() throws Exception {
		// System.out.println("Inside setupSSL method begins ");
		logger.info("Inside setupSSL method begins");
         ResourceLoader loader = new ResourceLoader();
         
         String truststorePath = loader.getValue("truststorePath");
         String truststorePassword= loader.getValue("truststorePassword");
         
         
         // System.out.println("trust store path "+truststorePath);
 		logger.info("trust store path "+truststorePath);
 		
 		// System.out.println("trust store password "+truststorePassword);
 		logger.info("trust store password "+truststorePassword);
       // String truststorePath = "/path/to/truststore.jks"; // Replace with the path to your truststore
      //  String truststorePassword = "your_truststore_password"; // Replace with your truststore password

        KeyStore truststore = KeyStore.getInstance(KeyStore.getDefaultType());
        truststore.load(new FileInputStream(truststorePath), truststorePassword.toCharArray());

        // Create a TrustManagerFactory and initialize it with the truststore
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(truststore);

        // Get the default X509TrustManager
        X509TrustManager defaultTrustManager = (X509TrustManager) tmf.getTrustManagers()[0];

        // Create a custom X509TrustManager that trusts all certificates
        X509TrustManager customTrustManager = new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
                try {
                    defaultTrustManager.checkServerTrusted(certs, authType);
                } catch (CertificateException e) {
                    // Handle certificate verification errors here if needed
                    e.printStackTrace();
                }
            }
        };

        // Create an SSLContext and set the custom TrustManager
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, new TrustManager[]{customTrustManager}, null);

        // Set the custom SSLContext as the default SSLContext for HTTPS connections
        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
        
        // System.out.println("Inside setupSSL method ends ");
		logger.info("Inside setupSSL method ends");
    }
}
