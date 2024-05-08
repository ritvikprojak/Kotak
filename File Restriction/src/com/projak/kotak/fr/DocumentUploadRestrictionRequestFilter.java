package com.projak.kotak.fr;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.stream.Collectors;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;
import javax.servlet.http.HttpServletRequest;

import com.filenet.api.core.ObjectStore;
import com.filenet.apiimpl.constants.Charsets;
import com.ibm.ecm.extension.PluginRequestFilter;
import com.ibm.ecm.extension.PluginServiceCallbacks;
import com.ibm.ecm.json.JSONMessage;
import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONArtifact;
import com.ibm.json.java.JSONObject;

public class DocumentUploadRestrictionRequestFilter extends PluginRequestFilter {

	public String[] getFilteredServices() {
		return new String[] { "/p8/addItem", "/p8/checkIn" };
	}

	public JSONObject filter(PluginServiceCallbacks callbacks,
			HttpServletRequest request, JSONArtifact jsonRequest)
			throws Exception {
		String repositoryId = request.getParameter("repositoryId");
		System.out.println("Header Info: " + request.getHeader("Content-Type"));
		String template_name = request.getParameter("template_name");
		System.out.println("Template name" + template_name);
		System.out.println("repositoryId " + repositoryId);
		System.out.println("asdf" + request.getParameter("Content-Type"));
		String mimeType = request.getParameter("mimetype");
		String filename = request.getParameter("parm_part_filename");
		System.out.println("MIME type " + mimeType);
		System.out.println("FileName: " + filename);
		String extension = null;
		
		if (filename != null && filename.substring(filename.lastIndexOf('.') + 1, filename.length()) != null){
		      extension = filename.substring(filename.lastIndexOf('.') + 1, filename.length()); 
		}
		
		if(mimeType == null || mimeType == "application/octet-stream"){
			Path p = Paths.get(filename);
			mimeType = Files.probeContentType(p);
//			System.out.println(Files.probeContentType(p));
		}
		
		ObjectStore p8ObjectStore = callbacks.getP8ObjectStore(repositoryId);
		p8ObjectStore.refresh();
		String OS = p8ObjectStore.get_SymbolicName();
		trustAllHosts();
		String stringURL = "https://10.10.34.120:9443/ecm/mime/check";

		System.out.println("URL : " + stringURL);

		String input = "{\"OS\":\"" + OS + "\",\"document\":\"" + template_name

		+ "\",\"mimeType\":\"" + mimeType + "\"}";

		System.out.println("Input : " + input);

		String text = null;

		HttpsURLConnection con = null;

		try {

			URL url = new URL(stringURL);

			byte[] out = input.getBytes(Charsets.CHARSET_UTF_8);

			int length = out.length;

			con = (HttpsURLConnection) url.openConnection();

			con.setFixedLengthStreamingMode(length);

			con.setRequestMethod("POST");

			con.setConnectTimeout(5000);

			con.setReadTimeout(5000);

			con.addRequestProperty("Content-Type", "application/json");

			con.setDoOutput(true);

			try (OutputStream os = con.getOutputStream()) {

				os.write(out);

			}

			con.connect();

			text = (String) new BufferedReader(new InputStreamReader(

			con.getInputStream(), Charsets.CHARSET_UTF_8)).lines()

			.collect(Collectors.joining("/n"));

		} catch (Exception ex) {

			System.out.println(ex.fillInStackTrace());

		} finally {

			if (con != null) {
				con.disconnect();
			}

		}

		if (text.equalsIgnoreCase("false")) {
			JSONObject json = null;
			JSONArray jsonMessages = new JSONArray();
		      JSONMessage errorMessage = null;
		      if (extension != null) {
		        errorMessage = new JSONMessage(10001, "Invalid file type: " + extension.toUpperCase(), "Only valid file type are allowed for upload.", "Please choose a valid file type.", "", "");
		      } else {
		        errorMessage = new JSONMessage(10001, "Invalid file : " + filename, "Only valid file type are allowed for upload.", "Please choose a valid file type.", "", "");
		      } 
		      jsonMessages.add(errorMessage);
			if (jsonRequest != null) {
				json = (JSONObject) ((JSONArray) jsonRequest).get(0);

				json.put("errors", jsonMessages);

				return json;
			} else {
				return null;
			}

		} else {
			return null;
		}
	}
	
	public static void trustAllHosts() {
	    try {
	      TrustManager[] trustAllCerts = { new X509ExtendedTrustManager() {
			
			@Override
			public X509Certificate[] getAcceptedIssuers() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public void checkServerTrusted(X509Certificate[] arg0, String arg1)
					throws CertificateException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void checkClientTrusted(X509Certificate[] arg0, String arg1)
					throws CertificateException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void checkServerTrusted(X509Certificate[] arg0, String arg1,
					SSLEngine arg2) throws CertificateException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void checkServerTrusted(X509Certificate[] arg0, String arg1,
					Socket arg2) throws CertificateException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void checkClientTrusted(X509Certificate[] arg0, String arg1,
					SSLEngine arg2) throws CertificateException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void checkClientTrusted(X509Certificate[] arg0, String arg1,
					Socket arg2) throws CertificateException {
				// TODO Auto-generated method stub
				
			}
		} };
	      SSLContext sc = SSLContext.getInstance("SSL");
	      sc.init(null, trustAllCerts, new SecureRandom());
	      HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	      HostnameVerifier allHostsValid = new HostnameVerifier() {
			
			@Override
			public boolean verify(String arg0, SSLSession arg1) {
				// TODO Auto-generated method stub
				return true;
			}
			
			public boolean verify1(String arg0, SSLSession arg1) {
	            return false;
	          }
		};
	      HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
	    } catch (Exception exception) {}
	  }
	
}
