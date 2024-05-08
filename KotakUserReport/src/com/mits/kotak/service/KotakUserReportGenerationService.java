package com.mits.kotak.service;

import com.ibm.ecm.extension.PluginLogger;
import com.ibm.ecm.extension.PluginService;
import com.ibm.ecm.extension.PluginServiceCallbacks;
import com.ibm.json.java.JSON;
import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;
import com.mits.kotak.util.ConfigLoader;
import com.mits.kotak.util.GeneratedCsvUtil;
import com.mits.kotak.util.UserReportsUtil;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class KotakUserReportGenerationService extends PluginService {
  private static String GENERATED_CSV_NAME = "User_LoginAction_Report.csv";
  
  private static String CSV_MIME_TYPE = "application/csv";
  
  ResourceBundle resourceBundle = ConfigLoader.getBundle();
  
  public void execute(PluginServiceCallbacks callbacks, HttpServletRequest request, HttpServletResponse response) throws Exception {
    String methodName = "execute";
    PluginLogger log = callbacks.getLogger();
    boolean isDebugEnabled = log.isDebugLogged();
    log.logEntry(this, methodName, (ServletRequest)request);
    Connection connection = null;
    ResultSet reportData = null;
    PrintWriter out = null;
    try {
      out = response.getWriter();
      String reportType = request.getParameter("reportType");
      System.out.println("reportType::::::::::::::" + reportType);
      String configurationString = callbacks.loadConfiguration();
      JSONObject jsonData = (JSONObject)JSON.parse(configurationString);
      JSONArray array = (JSONArray)jsonData.get("configuration");
      JSONObject dataSorceObj = (JSONObject)array.get(6);
      String dataSource = dataSorceObj.get("value").toString();
      String startDate = request.getParameter("startDate");
      String endDate = request.getParameter("endDate");
      System.out.println("startDate::::::::::::::" + startDate);
      System.out.println("endDate::::::::::::::" + endDate);
      String patternChange = "yyyy-MM-dd hh:mm:ss";
      SimpleDateFormat simpleDateFormatChange = new SimpleDateFormat(patternChange);
      Date date1 = simpleDateFormatChange.parse(startDate);
      String pattern = "dd-MMM-yy";
      SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
      String date = simpleDateFormat.format(date1);
      date = date + "12.00.00.000 AM";
      System.out.println("Date1" + date);
      Date date2 = simpleDateFormatChange.parse(endDate);
      String pattern1 = "dd-MMM-yy";
      SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat(pattern1);
      String date3 = simpleDateFormat1.format(date2);
      String date4 = date3 + " 11.59.59.000 PM";
      System.out.println("Date4" + date4);
      String repositoryId = request.getParameter("repoId");
      System.out.println("repositoryId" + repositoryId);
      UserReportsUtil userReportUtil = new UserReportsUtil();
      JSONArray searchSignInSignOffReportArray = userReportUtil.searchSignInSignOffReport(date, date4, repositoryId, log, dataSource);
      GeneratedCsvUtil generate = new GeneratedCsvUtil();
      String generateCsvString = generate.generateCSV(searchSignInSignOffReportArray, log);
      System.out.println("119 generateCsvString  " + generateCsvString);
      response.setContentType(CSV_MIME_TYPE);
      response.setHeader("Content-Disposition", "attachment; filename=" + GENERATED_CSV_NAME);
      String[] lines = generateCsvString.split(System.getProperty("line.separator"));
      for (int i = 0; i < lines.length; i++) {
        out.append(lines[i]);
        out.append("\n");
      } 
    } catch (Exception ex) {
      log.logError(this, methodName, (ServletRequest)request, ex);
      if (null != out)
        out.print("Exception Response from Server" + ex.getMessage()); 
    } 
  }
  
  public String getId() {
    return "KotakUserReportGenerationServiceId";
  }
}
