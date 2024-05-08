package com.mits.kotak.service;

import com.ibm.ecm.extension.PluginLogger;
import com.ibm.ecm.extension.PluginService;
import com.ibm.ecm.extension.PluginServiceCallbacks;
import com.ibm.json.java.JSON;
import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;
import com.mits.kotak.util.ConfigLoader;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.ResourceBundle;
import javax.naming.InitialContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

public class UserLogoffPluginService extends PluginService {
  ResourceBundle resourceBundle = ConfigLoader.getBundle();
  
  public void execute(PluginServiceCallbacks callbacks, HttpServletRequest request, HttpServletResponse response) throws Exception {
    PrintWriter printWriter = null;
    JSONObject object = null;
    String auditType = null;
    String localAddr = null;
    String userId = null;
    String repositoryId = null;
    String methodName = "execute";
    PluginLogger log = callbacks.getLogger();
    boolean isDebugEnabled = log.isDebugLogged();
    log.logEntry(this, methodName, (ServletRequest)request);
    try {
      String configurationString = callbacks.loadConfiguration();
      JSONObject jsonData = (JSONObject)JSON.parse(configurationString);
      JSONArray array = (JSONArray)jsonData.get("configuration");
      JSONObject dataSorceObj = (JSONObject)array.get(6);
      String dataSorce = dataSorceObj.get("value").toString();
      printWriter = response.getWriter();
      auditType = request.getParameter("userAction");
      userId = request.getParameter("userName");
      repositoryId = request.getParameter("repositoryid");
      System.out.println("auditType" + auditType);
      System.out.println("userId" + userId);
      localAddr = request.getRemoteAddr();
      object = new JSONObject();
      if (auditType.equalsIgnoreCase("Logoff")) {
        insertUserDetails(localAddr, userId, auditType, repositoryId, log, dataSorce);
        object.put("inserted", "Audit inserted Successfully");
        printWriter.println(object);
      } 
    } catch (Exception exception) {
      System.out.println("UserLogoff action" + exception.getMessage());
    } finally {
      if (printWriter != null)
        printWriter.close(); 
      auditType = null;
    } 
    log.logExit(this, methodName);
  }
  
  public void insertUserDetails(String localAddr, String userId, String auditType, String repositoryId, PluginLogger log, String dataSource) {
    String methodName = "insertUserDetails";
    log.logEntry(this, methodName);
    Connection connection = null;
    InitialContext context = null;
    DataSource datasource = null;
    PreparedStatement preparedStatement = null;
    try {
      context = new InitialContext();
      datasource = (DataSource)context.lookup(dataSource.trim());
      connection = datasource.getConnection();
      System.out.println("connection object is" + connection.toString());
      preparedStatement = connection.prepareStatement(this.resourceBundle.getString("insertUserActionAuditLogQuery").trim());
      Date utilDate = new Date();
      Timestamp sqlTimestamp = new Timestamp(utilDate.getTime());
      System.out.println(sqlTimestamp.toString() + "sqlTimestampsqlTimestamp");
      preparedStatement.setString(1, userId);
      preparedStatement.setString(2, localAddr);
      preparedStatement.setString(3, auditType);
      preparedStatement.setTimestamp(4, sqlTimestamp);
      preparedStatement.setString(5, repositoryId);
      int status = preparedStatement.executeUpdate();
      System.out.println("##" + status + "  records inserted successfully AuditTABLE");
    } catch (Exception exception) {
      exception.printStackTrace();
    } finally {
      try {
        if (null != preparedStatement)
          preparedStatement.close(); 
        if (connection != null)
          connection.close(); 
        if (null != context)
          context.close(); 
      } catch (Exception e) {
        e.printStackTrace();
      } 
      connection = null;
      context = null;
      preparedStatement = null;
      datasource = null;
    } 
    log.logExit(this, methodName);
  }
  
  public String getId() {
    return "UserLogoffPluginService";
  }
}
