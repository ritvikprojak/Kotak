package com.projak.kotak.event.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.net.ssl.HttpsURLConnection;

import com.filenet.api.collection.ContentElementList;
import com.filenet.api.core.ContentElement;
import com.filenet.api.core.Document;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.engine.EventActionHandler;
import com.filenet.api.events.ObjectChangeEvent;
import com.filenet.api.exception.EngineRuntimeException;
import com.filenet.api.util.Id;
import com.filenet.apiimpl.constants.Charsets;
import com.projak.kotak.event.action.exception.MimeException;

public class FileResctrictionHandler implements EventActionHandler {

	public void onEvent(ObjectChangeEvent arg0, Id arg1) throws EngineRuntimeException {

		Properties prop = getProperties();

		String text = null;

		String fileName = null;

		try {

			Document doc = (Document) arg0.get_SourceObject();

			doc.refresh();

			ContentElementList ce = doc.get_ContentElements();

			String mimeType = null;

			if (ce != null) {

				ContentElement c = (ContentElement) ce.get(0);

				mimeType = c.get_ContentType();

				if (mimeType == null) {
					mimeType = doc.get_MimeType();
				}

				fileName = c.getProperties().getStringValue("RetrievalName");

			} else {
				mimeType = doc.get_MimeType();
			}
			
		    System.out.println("File Name : " + fileName);

			System.out.println("Mime Type : " + mimeType);

			ObjectStore O = doc.getObjectStore();

			O.refresh();

			String OS = O.get_SymbolicName();

			System.out.println("OS : " + OS);

			String document = doc.getClassName();

			System.out.println("Document Class Name : " + document);

			String stringURL = "https://10.10.34.120:9443/ecm/mime/check";

			System.out.println("URL : " + stringURL);

			String input = "{\"OS\":\"" + OS + "\",\"document\":\"" + document

			+ "\",\"mimeType\":\"" + mimeType + "\"}";

			System.out.println("Input : " + input);

			URL url = new URL(stringURL);

			byte[] out = input.getBytes(Charsets.CHARSET_UTF_8);

			int length = out.length;

			HttpsURLConnection con = (HttpsURLConnection) url.openConnection();

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

		} catch (Exception e) {

			// TODO Auto-generated catch block

			e.printStackTrace();

			System.out.println(e.fillInStackTrace());

		}

		System.out.println("Text : " + text);
		
		String Extension = fileName.substring(fileName.lastIndexOf('.') + 1, fileName.length());
	    
		System.out.println("Ext :" + Extension);

		if (text != null) {

			if (text.equalsIgnoreCase("false")) {

				throw new MimeException(String.valueOf(Extension.toUpperCase()) + " is Not Allowed for the document");

			}

		}

	}

	public Properties getProperties() {

		Properties properties = new Properties();

		File file = new File("src/application.properties");

		FileInputStream fileInput = null;

		try {

			fileInput = new FileInputStream(file);

			properties.load(fileInput);

		} catch (IOException e) {

			// TODO Auto-generated catch block

			e.printStackTrace();

		}

		return properties;

	}

}
