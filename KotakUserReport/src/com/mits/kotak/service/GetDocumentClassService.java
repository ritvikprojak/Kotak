package com.mits.kotak.service;

import com.filenet.api.admin.ClassDefinition;
import com.filenet.api.collection.ClassDefinitionSet;
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
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.security.auth.Subject;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class GetDocumentClassService extends PluginService {
  private static final Set<String> classValues = new HashSet<String>(Arrays.asList(new String[] { 
          "CKYC", "WorkflowDefinition", "XMLPropertyMappingScript", "CodeModule", "ScenarioDefinition", "Simulation", "PublishTemplate", "PreferencesDocument", "Email", "FormData", 
          "FormTemplate", "FormPolicy", "EntryTemplate", "StoredSearch" }));
  
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
      String req = request.getParameter("reqtype");
      repositoryId = request.getParameter("objStore");
      System.out.println("reqreqreqreqreqreq " + req);
      System.out.println("repositoryId::::::::::::::" + repositoryId);
      userContext = UserContext.get();
      p8ObjectStore = callbacks.getP8ObjectStore(repositoryId);
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
      System.out.println("admin username  " + username + " admin password  " + pass + "@#$TGS^$%@*");
      Connection con = Factory.Connection.getConnection(uri);
      subject = UserContext.createSubject(con, username, pass, "FileNetP8WSI");
      userContext.pushSubject(subject);
      Domain domin = Factory.Domain.fetchInstance(con, domain, null);
      ObjectStore objectStore = Factory.ObjectStore.fetchInstance(domin, repositoryId, null);
      objstore = objectStore.get_DisplayName();
      System.out.println("objstore name is" + objstore);
      ClassDefinition objClassDef = Factory.ClassDefinition.fetchInstance(objectStore, "Document", null);
      ClassDefinitionSet classdefinationset = objClassDef.get_ImmediateSubclassDefinitions();
      Iterator<ClassDefinition> iterateclasses = classdefinationset.iterator();
      jsonArray = new JSONArray();
      JSONObject jsonObject = null;
      jsonObject = new JSONObject();
      jsonObject.put("name", "All Classes");
      jsonArray.add(jsonObject);
      while (iterateclasses.hasNext()) {
        ClassDefinition objClassDefNew = iterateclasses.next();
        log.logDebug(this, " all documentclasses::::::::::::::", objClassDefNew.get_SymbolicName());
        if (!objClassDef.get_IsSystemOwned().booleanValue() || (!objClassDef.get_IsHidden().booleanValue() && !classValues.contains(objClassDefNew.get_SymbolicName()))) {
          String documentclasses = objClassDefNew.get_SymbolicName();
          log.logDebug(this, "documentclasses::::::::::::::", documentclasses);
          jsonObject = new JSONObject();
          jsonObject.put("name", objClassDefNew.get_SymbolicName());
          jsonObject.put("Id", objClassDefNew.get_Id().toString());
          jsonArray.add(jsonObject);
        } 
      } 
      log.logDebug(this, "jsonArray::::::::::::::", jsonArray.toString());
      out.println(jsonArray);
    } catch (Exception exception) {
      exception.printStackTrace();
    } finally {}
  }
  
  public String getId() {
    return "GetDocumentClassServiceId";
  }
}
