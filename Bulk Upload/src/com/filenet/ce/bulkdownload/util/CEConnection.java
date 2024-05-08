/**
	IBM grants you a nonexclusive copyright license to use all programming code 
	examples from which you can generate similar function tailored to your own 
	specific needs.

	All sample code is provided by IBM for illustrative purposes only.
	These examples have not been thoroughly tested under all conditions.  IBM, 
	therefore cannot guarantee or imply reliability, serviceability, or function of 
	these programs.

	All Programs or code component contained herein are provided to you “AS IS “ 
	without any warranties of any kind.
	The implied warranties of non-infringement, merchantability and fitness for a 
	particular purpose are expressly disclaimed.

	© Copyright IBM Corporation 2007, ALL RIGHTS RESERVED.
 */

package com.filenet.ce.bulkdownload.util;

import java.net.Socket;
import java.security.cert.CertificateException;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;
import javax.security.auth.Subject;

import org.apache.log4j.Logger;

import com.filenet.api.collection.ObjectStoreSet;
import com.filenet.api.core.Connection;
import com.filenet.api.core.Domain;
import com.filenet.api.core.Factory;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.util.UserContext;

/**
 * This object represents the connection with the Content Engine. Once
 * connection is established it intializes Domain and ObjectStoreSet with
 * available Domain and ObjectStoreSet.
 * 
 */
public class CEConnection {
	final static Logger logger = Logger.getLogger(CEConnection.class);
	final static ResourceBundle rsbundle = ResourceData.getResourceBundle();
	public Connection con;
	private Domain dom;
	private String domainName;
	private ObjectStoreSet ost;
	private Vector<String> osnames;
	private boolean isConnected;
	private UserContext uc;
	private String cm_TARGETOS;

	/*
	 * constructor
	 */
	public CEConnection() {
		con = null;
		uc = UserContext.get();
		dom = null;
		domainName = null;
		ost = null;
		osnames = new Vector<String>();
		isConnected = false;
	}

	public String getCm_TARGETOS() {
		return cm_TARGETOS;
	}

	public void setCm_TARGETOS(String cm_TARGETOS) {
		this.cm_TARGETOS = cm_TARGETOS;
	}

	/*
	 * Establishes connection with Content Engine using supplied username,
	 * password, JAAS stanza and CE Uri.
	 */
	public void establishConnection(String userName, String password,
			String stanza, String uri) {
		if (logger.isInfoEnabled()) {
			logger.info("<< Connection Details CE URI ::" + uri
					+ "\t Stanza ::" + stanza + "\t Username ::" + userName
					+ "\t Password ::" + "******");
		}
		try {
			String sec = rsbundle.getString("sec");
			if (sec.equalsIgnoreCase("false")) {
				trustAllHosts();
			}
			con = Factory.Connection.getConnection(uri);
			Subject sub = UserContext.createSubject(con, userName, password,
					stanza);
			uc.pushSubject(sub);
			dom = fetchDomain();
			domainName = dom.get_Name();
			ost = getOSSet();
			isConnected = true;
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			ex.printStackTrace();
		}
	}

	/*
	 * Returns Domain object.
	 */
	public Domain fetchDomain() {
		dom = Factory.Domain.fetchInstance(con, null, null);
		return dom;
	}

	/*
	 * Returns ObjectStoreSet from Domain
	 */
	public ObjectStoreSet getOSSet() {
		ost = dom.get_ObjectStores();
		return ost;
	}

	/*
	 * Returns vector containing ObjectStore names from object stores available
	 * in ObjectStoreSet.
	 */
	public Vector<String> getOSNames() {
		if (osnames.isEmpty()) {
			Iterator<?> it = ost.iterator();
			while (it.hasNext()) {
				ObjectStore os = (ObjectStore) it.next();
				osnames.add(os.get_DisplayName());
			}
		}
		return osnames;
	}

	/*
	 * Checks whether connection has established with the Content Engine or not.
	 */
	public boolean isConnected() {
		return isConnected;
	}

	/*
	 * Returns ObjectStore object for supplied object store name.
	 */
	public ObjectStore fetchOS(String name) {
		ObjectStore os = Factory.ObjectStore.fetchInstance(dom, name, null);
		return os;
	}

	/*
	 * Returns the domain name.
	 */
	public String getDomainName() {
		return domainName;
	}

	public void trustAllHosts() {
		try {
			TrustManager[] trustAllCerts = new TrustManager[] { new X509ExtendedTrustManager() {
				@Override
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				@Override
				public void checkClientTrusted(
						java.security.cert.X509Certificate[] certs,
						String authType) {
				}

				@Override
				public void checkServerTrusted(
						java.security.cert.X509Certificate[] certs,
						String authType) {
				}

				@Override
				public void checkClientTrusted(
						java.security.cert.X509Certificate[] xcs,
						String string, Socket socket)
						throws CertificateException {

				}

				@Override
				public void checkServerTrusted(
						java.security.cert.X509Certificate[] xcs,
						String string, Socket socket)
						throws CertificateException {

				}

				@Override
				public void checkClientTrusted(
						java.security.cert.X509Certificate[] xcs,
						String string, SSLEngine ssle)
						throws CertificateException {

				}

				@Override
				public void checkServerTrusted(
						java.security.cert.X509Certificate[] xcs,
						String string, SSLEngine ssle)
						throws CertificateException {

				}

			} };

			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection
					.setDefaultSSLSocketFactory(sc.getSocketFactory());

			// Create all-trusting host name verifier
			HostnameVerifier allHostsValid = new HostnameVerifier() {
				@Override
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}

				@SuppressWarnings("unused")
				public boolean verify1(String arg0, SSLSession arg1) {
					// TODO Auto-generated method stub
					return false;
				}
			};
			// Install the all-trusting host verifier
			HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
