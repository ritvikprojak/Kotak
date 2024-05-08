package com.mits.kotak.util;

import com.ibm.ecm.extension.PluginLogger;
import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class GeneratedCsvUtil {
  public String generateCSV(JSONArray jsonResponse, PluginLogger log) {
    String methodName = "generateCSV";
    boolean isDebugEnabled = log.isDebugLogged();
    log.logEntry(this, methodName);
    log.logDebug(this, "documentPermissionList  is", jsonResponse.toString());
    JSONObject jsonObject = null;
    StringBuffer resultSring = new StringBuffer();
    resultSring.append("");
    Set<String> docNameSet = new HashSet<String>();
    try {
      for (int i = 0; i < jsonResponse.size(); i++) {
        jsonObject = (JSONObject)jsonResponse.get(i);
        String docName = (String)jsonObject.get("Document Name");
        Set set1 = jsonObject.keySet();
        if (i == 0) {
          Iterator<String> iterator2 = set1.iterator();
          while (iterator2.hasNext()) {
            String object = iterator2.next();
            resultSring.append('"' + object.toString() + '"' + ",");
          } 
          resultSring.append("\n");
        } 
        if (docNameSet.size() == 0)
          docNameSet.add(docName); 
        if (!docNameSet.contains(docName)) {
          resultSring.append("\n");
          docNameSet.add(docName);
        } 
        Iterator<String> iterator1 = set1.iterator();
        while (iterator1.hasNext()) {
          String object = iterator1.next();
          if (object.toString().equalsIgnoreCase("ACTION DATE") || object.toString().equalsIgnoreCase("CREATED DATE") || object.toString().equalsIgnoreCase("MODIFIED DATE") || object.toString().equalsIgnoreCase("APPROVED DATE") || object.toString().equalsIgnoreCase("LAST LOGIN DATE")) {
            String str = (jsonObject.get(object) == null) ? "" : jsonObject.get(object).toString();
            resultSring.append("'" + str + ",");
            continue;
          } 
          String value = (jsonObject.get(object) == null) ? "" : jsonObject.get(object).toString();
          resultSring.append(value + ",");
        } 
        resultSring.append("\n");
        System.out.println(docNameSet.contains(docName));
      } 
      resultSring.deleteCharAt(resultSring.lastIndexOf(","));
      System.out.println("resultSring::::::::::85" + resultSring.toString());
    } catch (Exception e) {
      e.printStackTrace();
      log.logDebug(this, "Error", e.getMessage());
    } finally {
      jsonResponse = null;
      jsonObject = null;
      Iterator iterator = null;
      Set set = null;
    } 
    return resultSring.toString();
  }
}
