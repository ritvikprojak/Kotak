package com.mits.kotak.service;

import com.ibm.ecm.extension.PluginLogger;
import com.ibm.ecm.extension.PluginService;
import com.ibm.ecm.extension.PluginServiceCallbacks;
import com.ibm.json.java.JSON;
import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;
import com.mits.kotak.util.GeneratedCsvUtil;
import com.mits.kotak.util.UserReportsUtil;
import java.io.PrintWriter;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class KotakLastUserReportGenerationService extends PluginService {
  private static String GENERATED_CSV_NAME = "Last_Login_Report.csv";
  
  private static String CSV_MIME_TYPE = "application/csv";
  
  public void execute(PluginServiceCallbacks callbacks, HttpServletRequest request, HttpServletResponse response) throws Exception {
    String methodName = "execute";
    PluginLogger log = callbacks.getLogger();
    boolean isDebugEnabled = log.isDebugLogged();
    if (isDebugEnabled)
      log.logEntry(this, methodName, (ServletRequest)request); 
    PrintWriter out = null;
    try {
      out = response.getWriter();
      String reportType = request.getParameter("reqtype");
      String repositoryId = request.getParameter("repoId");
      log.logDebug(this, "repositoryId::::::::::::::", repositoryId);
      String configurationString = callbacks.loadConfiguration();
      JSONObject jsonData = (JSONObject)JSON.parse(configurationString);
      JSONArray array = (JSONArray)jsonData.get("configuration");
      JSONObject dataSorceObj = (JSONObject)array.get(6);
      String dataSource = dataSorceObj.get("value").toString();
      UserReportsUtil lastLogJsonArray = new UserReportsUtil();
      JSONArray resultsArray = lastLogJsonArray.searchLastLoginReport(reportType, repositoryId, log, dataSource);
      GeneratedCsvUtil generate = new GeneratedCsvUtil();
      String generateCsvString = generate.generateCSV(resultsArray, log);
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
    return "KotakLastUserReportGenerationServiceId";
  }
}
