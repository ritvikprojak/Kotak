package com.mits.kotak.service;

import com.ibm.ecm.extension.PluginService;
import com.ibm.ecm.extension.PluginServiceCallbacks;
import com.ibm.json.java.JSON;
import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;
import com.mits.kotak.util.ConfigLoader;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

public class KotakUserReportsCheckService extends PluginService {
  ResourceBundle resourceBundle = ConfigLoader.getBundle();
  
  public void execute(PluginServiceCallbacks callbacks, HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection connection = null;
    ResultSet reportData = null;
    try {
      PrintWriter out = response.getWriter();
      String startDate = request.getParameter("startDate");
      String endDate = request.getParameter("endDate");
      String repoID = request.getParameter("repoID");
      String patternChange = "yyyy-MM-dd hh:mm:ss";
      SimpleDateFormat simpleDateFormatChange = new SimpleDateFormat(patternChange);
      Date date1 = simpleDateFormatChange.parse(startDate);
      String pattern = "dd-MMM-yy";
      SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
      String date = simpleDateFormat.format(date1);
      date = date + "12.00.00.000 AM";
      Date date2 = simpleDateFormatChange.parse(endDate);
      String pattern1 = "dd-MMM-yy";
      SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat(pattern1);
      String date3 = simpleDateFormat1.format(date2);
      String date4 = date3 + " 11.59.59.000 PM";
      Context context = new InitialContext();
      String configurationString = callbacks.loadConfiguration();
      JSONObject jsonData = (JSONObject)JSON.parse(configurationString);
      JSONArray array = (JSONArray)jsonData.get("configuration");
      JSONObject dataSorceObj = (JSONObject)array.get(6);
      String dataSourceName = dataSorceObj.get("value").toString();
      System.out.println("DataSourc Name is" + dataSourceName);
      DataSource dataSorce = (DataSource)context.lookup(dataSourceName);
      connection = dataSorce.getConnection();
      System.out.println("connection" + connection);
      PreparedStatement preparedStatement = connection.prepareStatement(this.resourceBundle.getString("UserSignInSinOffQuery"));
      preparedStatement.setString(1, date);
      preparedStatement.setString(2, date4);
      preparedStatement.setString(3, repoID);
      System.out.println("query");
      reportData = preparedStatement.executeQuery();
      System.out.println("execute query");
      System.out.println("boolean condition" + reportData.isBeforeFirst());
      if (!reportData.isBeforeFirst()) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("key", "empty");
        out.print(jsonObject);
      } else {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("key", "full");
        out.print(jsonObject);
      } 
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("Error" + e.getMessage());
    } finally {
      try {
        connection.close();
        if (reportData != null)
          reportData.close(); 
      } catch (Exception e) {
        e.printStackTrace();
        System.out.println("Catch" + e.getMessage() + e);
      } 
    } 
  }
  
  public String getId() {
    return "KotakUserReportsCheckServiceId";
  }
}
