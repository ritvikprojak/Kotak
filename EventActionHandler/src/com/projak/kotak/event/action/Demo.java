package com.projak.kotak.event.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.stream.Collectors;

import org.apache.tika.Tika;

import com.filenet.apiimpl.constants.Charsets;

public class Demo {

	public static void main(String[] args) throws Exception {
		
		
//		Properties prop = getProperties();
//		String text = null;
//		try {
//			String mimeType = "application/pdf";
//			String OS = "KMPL";
//			String document = "Document";
//			String stringURL = "http://localhost:8082/mime/check";
//			String input = "{\"OS\":\"" + OS + "\",\"document\":\"" + document
//					+ "\",\"mimeType\":\"" + mimeType + "\"}";
//			URL url = new URL(stringURL);
//			byte[] out = input.getBytes(Charsets.CHARSET_UTF_8);
//			int length = out.length;
//			HttpURLConnection con = (HttpURLConnection) url.openConnection();
//			con.setFixedLengthStreamingMode(length);
//			con.setRequestMethod("POST");
//			con.setConnectTimeout(5000);
//			con.setReadTimeout(5000);
//			con.addRequestProperty("Content-Type", "application/json");
//			con.setDoOutput(true);
//			try(OutputStream os = con.getOutputStream()) {
//			    os.write(out);
//			}
//			con.connect();
//			text = (String) new BufferedReader(new InputStreamReader(
//					con.getInputStream(), Charsets.CHARSET_UTF_8)).lines()
//					.collect(Collectors.joining("/n"));
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			System.out.println(e.fillInStackTrace());
//		}
//
//		if (text.equalsIgnoreCase("false")) {
//			System.out.println("Mime Type not allowed");
//			throw new Exception("Mime Type not allowed");
//
//		} else {
//			System.out.println("Mime Type allowed");
//		}

	}
	
	
	public static Properties getProperties(){
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
	
	
	public static String checkMime(){

		return null;
	}

}
