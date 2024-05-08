package com.projak.DMS;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ResourceBundle;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletRequest;



//import com.filenet.ce.bulkdownload.util.ResourceData;
import com.ibm.ecm.extension.PluginLogger;
import com.ibm.ecm.extension.PluginResponseFilter;
import com.ibm.ecm.extension.PluginServiceCallbacks;
import com.ibm.ecm.json.JSONItemAttributesResponse;
import com.ibm.ecm.json.JSONViewoneBootstrapResponse;
import com.ibm.json.java.JSONObject;

public class ViewOneBootstrapResponseFilter extends PluginResponseFilter {

	private static final String[] filteredServices = new String[] { "/p8/getViewoneBootstrap" };
	
	@Override
	public String[] getFilteredServices() {
		return filteredServices;
	}

	@Override
	public void filter(String serverType, PluginServiceCallbacks callbacks, HttpServletRequest request, JSONObject jsonResponse) throws Exception {
//		System.out.println("\n\nv1BootstrapPlugin --> ViewOneBootstrapResponseFilter--> filter  Started");
		if ( jsonResponse instanceof JSONViewoneBootstrapResponse  ) {
//			System.out.println("\n\nv1BootstrapPlugin --> ViewOneBootstrapResponseFilter--> filter  jsonResponse");
			PluginLogger logger = callbacks.getLogger();
//			callbacks.
			JSONItemAttributesResponse jr = (JSONItemAttributesResponse) jsonResponse;

			JSONViewoneBootstrapResponse jvbr = (JSONViewoneBootstrapResponse)jsonResponse;
			ResourceBundle rs = getResourceBundle();
			String userUrl = rs.getString("CE_URI");
			String[] inputkey={"userId"};
			String user = callbacks.getUserId();
			String[] inputValue={user};
			String userResponse= sendingGetRequest(userUrl,inputkey,inputValue);
			boolean Print_Allowed = false;
			try{
				if ( null != userResponse )
				{
					JSONObject userList= JSONObject.parse(userResponse);
//					System.out.println("userList " + userList);
//					JSONObject newList= new JSONObject().parse(userList.get("GetUserDetailsWithAdUserwithApp").toString());
//					System.out.println("newList " + newList.get("APPLICATION_SECURITY_CLASS"));
//					int printSecurityLevel = Integer.parseInt(newList.get("FINACLE_WORK_CLASS").toString());
					//For Accomodating CSV Values for Printable Permission
					
					Print_Allowed = (Boolean) userList.get("Print_Allowed");
					
					
					if (Print_Allowed)
						jvbr.setViewOneParameter( "printButtons","true");
					else
						jvbr.setViewOneParameter( "printButtons","false");	
				}
				else 
				{
					System.out.println(this + "  \n\nv1BootstrapPlugin --> ViewOneBootstrapResponseFilter--> filter User response Is Null");
				}	
			}
			catch (NumberFormatException e){
				System.out.println("Either Finacle Work class is empty or not present in UPM Response ");
			}
			catch (Exception exc) {
					logger.logError(this, "filter", exc);
				}	
		}
	}
	
	
	
	 private static String sendingGetRequest(String urlString,String[] key,String[] value) throws Exception {
		  // By default it is GET request
		
		 StringBuffer response=null;
		try {
			String parameters="";
			  for(int i=0;i<key.length;i++){			
					if(i == 0){
						parameters="?"+key[i]+"="+value[i];
					}else{
						parameters=parameters+"&&"+key[i]+"="+value[i];
					}
					
				}
			  urlString=urlString+parameters;
			  URL url = new URL(urlString);
			  HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
			  con.setRequestMethod("GET");
			  con.setConnectTimeout(5000);
			  
			  // Reading response from input Stream
			  BufferedReader in = new BufferedReader(
			          new InputStreamReader(con.getInputStream()));
			  String output;
			  response = new StringBuffer();
			 
			  while ((output = in.readLine()) != null) {
			   response.append(output);
			  }
			  in.close();
		} catch (Exception e) {
			return "{}";
		}
		 
		  //printing result from response
		 
		return response.toString();
		 
		 }
	 
/*	 public static void main (String Args[]) throws Exception
	 {
//		 String userUrl="http://projakserver:9081/CCWebServices/GetUserDetailsWithAdUserwithApp/";
			
		 	ResourceBundle rs = getResourceBundle();
			String userUrl = rs.getString("CE_URI");
			String[] inputkey={"userId","appCode"};
			String user = "chall";//request.getParameter("");
			String[] inputValue={user,"DMS"};
			String userResponse= sendingGetRequest(userUrl,inputkey,inputValue);
			JSONObject userList= new JSONObject().parse(userResponse);
			System.out.println("userList " + userList);
			JSONObject newList= new JSONObject().parse(userList.get("GetUserDetailsWithAdUserwithApp").toString());
			System.out.println("newList " + newList.get("APPLICATION_SECURITY_CLASS"));
			System.out.println("The Integer Value is " + Integer.parseInt(newList.get("APPLICATION_SECURITY_CLASS").toString()));
		 
	 }*/
	 
	 	 
	 public static ResourceBundle getResourceBundle() 
	  {
		 ResourceBundle rsbundle = null;
		try {
			 rsbundle = ResourceBundle.getBundle("config");
			 return rsbundle;
			
		} catch (Exception e) {
			System.out.println("Property File Not Found "+e.fillInStackTrace());
			return null;
			
		}
		
	  }
	 public static void main (String [] args){
			String str = ViewOneBootstrapResponseFilter.getResourceBundle().getString("CE_URI");
			System.out.println(str);
		}
	 
	 
}
