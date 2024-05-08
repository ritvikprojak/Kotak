package com.mits.kotak.service;

import com.filenet.api.core.Connection;
import com.filenet.api.core.Domain;
import com.filenet.api.core.Factory;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.util.UserContext;
import com.ibm.ecm.extension.PluginLogger;
import com.ibm.ecm.extension.PluginService;
import com.ibm.ecm.extension.PluginServiceCallbacks;
import com.ibm.json.java.JSON;
import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;
import com.mits.kotak.util.GeneratedCsvUtil;
import com.mits.kotak.util.UserReportsUtil;
import java.io.PrintWriter;
import javax.security.auth.Subject;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class KotakDocumentSecurityReportsService extends PluginService {
  private static String GENERATED_CSV_NAME = "Group_Privilege_Report.csv";
  
  private static String CSV_MIME_TYPE = "application/csv";
  
  public void execute(PluginServiceCallbacks callbacks, HttpServletRequest request, HttpServletResponse response) throws Exception {
    String methodName = "execute";
    PluginLogger log = callbacks.getLogger();
    boolean isDebugEnabled = log.isDebugLogged();
    log.logEntry(this, methodName, (ServletRequest)request);
    ObjectStore p8ObjectStore = null;
    String repositoryId = null;
    String objstore = null;
    Subject subject = null;
    UserContext userContext = null;
    JSONArray jsonArray = null;
    PrintWriter out = response.getWriter();
    try {
      String docName = request.getParameter("docName");
      log.logDebug(this, "docname is", docName);
      repositoryId = request.getParameter("repositId");
      log.logDebug(this, "req ", repositoryId);
      subject = callbacks.getP8Subject(repositoryId);
      userContext = UserContext.get();
      String configurationString = callbacks.loadConfiguration();
      JSONObject jsonData = (JSONObject)JSON.parse(configurationString);
      JSONArray array = (JSONArray)jsonData.get("configuration");
      JSONObject mtomurl = (JSONObject)array.get(7);
      String uri = mtomurl.get("value").toString();
      System.out.println("connect URI" + uri);
      JSONObject domainname = (JSONObject)array.get(8);
      String domain = domainname.get("value").toString();
      JSONObject admusername = (JSONObject)array.get(9);
      JSONObject adminpwd = (JSONObject)array.get(10);
      String username = admusername.get("value").toString();
      String pass = adminpwd.get("value").toString();
      Connection con = Factory.Connection.getConnection(uri);
      subject = UserContext.createSubject(con, username, pass, "FileNetP8WSI");
      userContext.pushSubject(subject);
      Domain domin = Factory.Domain.fetchInstance(con, domain, null);
      ObjectStore objectStore = Factory.ObjectStore.fetchInstance(domin, repositoryId, null);
      UserReportsUtil usrReportsUtil = new UserReportsUtil();
      JSONArray documentPermissionList = usrReportsUtil.documentPermissionList(objectStore, docName, log);
      log.logDebug(this, "documentPermissionList  is", documentPermissionList.toString());
      GeneratedCsvUtil generate = new GeneratedCsvUtil();
      String generateCsvString = generate.generateCSV(documentPermissionList, log);
      StringBuffer resultSring = new StringBuffer();
      resultSring.append(generateCsvString);
      resultSring.append("\n");
      resultSring.append("\n");
      resultSring.append("\n");
      resultSring.append("\n");
      resultSring.append("\n");
      resultSring.append("IBM FileNet P8 permission,Explanation");
      resultSring.append("\n");
      resultSring.append("CHANGE_STATE,Check out a document & edit content and metadata & and checkin");
      resultSring.append("\n");
      resultSring.append("MAJOR_VERSION,Check out a document & edit content and metadata & and checkin");
      resultSring.append("\n");
      resultSring.append("MINOR_VERSION,Check out a document & edit content and metadata & and checkin");
      resultSring.append("\n");
      resultSring.append("WRITE,Check out a document & edit content and metadata & and checkin");
      resultSring.append("\n");
      resultSring.append("DELETE,Delete a document or folder");
      resultSring.append("\n");
      resultSring.append("READ,ViewProperties and View metadata on a document or folder");
      resultSring.append("\n");
      resultSring.append("VIEW,ViewContent and View content on a document");
      resultSring.append("\n");
      resultSring.append("CREATE_CHILD,Add a subfolder or document to a folder");
      resultSring.append("\n");
      resultSring.append("WRITE_ACL,Change access rights on a document or folder");
      resultSring.append("\n");
      resultSring.append("READ_ACL,User Can read access rights on a document or folder");
      resultSring.append("\n");
      resultSring.append("RESERVED12,Deploy is deprecated & incompatibility from release to release. (not currently used)");
      resultSring.append("\n");
      resultSring.append("RESERVED13,Archive is deprecated & incompatibility from release to release. (not currently used)");
      resultSring.append("\n");
      resultSring.append("LINK,Specifies that the user or group is granted or denied permission to link to an object.");
      resultSring.append("\n");
      resultSring.append("UNLINK,Specifies that the user or group is granted or denied permission to unlink to an object.");
      response.setContentType(CSV_MIME_TYPE);
      response.setHeader("Content-Disposition", "attachment; filename=" + GENERATED_CSV_NAME);
      String[] lines = resultSring.toString().split(System.getProperty("line.separator"));
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
    return "KotakDocumentSecurityReportsServiceId";
  }
}
