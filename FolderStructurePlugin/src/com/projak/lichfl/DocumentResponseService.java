package com.projak.lichfl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpHeaders;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

//import org.apache.http.HttpResponse;
//import org.apache.http.client.ClientProtocolException;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.impl.client.CloseableHttpClient;
//import org.apache.http.impl.client.HttpClients;


import com.ibm.ecm.extension.PluginResponseFilter;
import com.ibm.ecm.extension.PluginServiceCallbacks;
import com.ibm.ecm.json.JSONMessage;
import com.ibm.ecm.json.JSONResultSetResponse;
import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;

public class DocumentResponseService extends PluginResponseFilter {
	
	public String[] getFilteredServices() {
		return new String[] { "/p8/openItem" };
	}

	public void filter(String serverType, PluginServiceCallbacks callbacks,
			HttpServletRequest request, JSONObject jsonResponse)
			throws Exception {
		System.out.println("DocumentResponseService.filter()"+request.getRequestURI());
		String user = request.getRemoteUser();

		System.out.println("User - " + user);
		JSONArray criteriaArr = (JSONArray) jsonResponse.get("criterias");
		System.out.println("criteriaArr: "+criteriaArr);
		JSONObject choiceListObj = new JSONObject();
		choiceListObj.put("displayName", "ScanLocation");
		JSONArray choices = new JSONArray();
		System.out.println("criteriaArr size: "+criteriaArr.size());
		for (int i = 0; i < criteriaArr.size(); i++) {
			JSONObject object = (JSONObject) criteriaArr.get(i);
			String name = object.get("name").toString();
			
			if(name.equalsIgnoreCase("ScanLocation")) {
				System.out.println("inside for if");
				JSONObject resultsObj = getUserLocationDetails(user);
				JSONArray array = (JSONArray) resultsObj.get("attachLocation");
				//System.out.println("array: "+array.size());
				
				for(int j=0;j<array.size();j++) {
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("displayName", array.get(j).toString());
					jsonObject.put("value", array.get(j).toString());
					choices.add(jsonObject);
				}
				//System.out.println("Final Choices: "+choices);
				choiceListObj.put("choices", choices);
				object.put("choiceList", choiceListObj);
			}
			
			
		}
		//System.out.println("Final Criteria: "+criteriaArr);
		jsonResponse.put("criterias", criteriaArr);
		System.out.println("updated response: "+jsonResponse);
		
	}
	public static JSONObject getUserLocationDetails(String userId) throws ClientProtocolException, IOException {
		
		JSONObject responseJson = null;
		String bodyAsString = "";
		String url = "http://10.0.5.81:8080/edms_uat/getUserDetails?srNo="+userId;
		
		System.out.println("URL: "+url);
		
		try(CloseableHttpClient client = HttpClients.createDefault()){
			

			HttpGet httpGet = new HttpGet(url);

			JSONObject jobj = new JSONObject();

			try(CloseableHttpResponse response = client.execute(httpGet)){
				
				 bodyAsString = EntityUtils.toString(response.getEntity());

				System.out.println(bodyAsString);
				
				responseJson = JSONObject.parse(bodyAsString);

				client.close();
				
			}
			
		}

		return responseJson;
	}
	
	

}
