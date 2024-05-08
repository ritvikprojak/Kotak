package com.mits.kotak.util;

import com.filenet.api.admin.ClassDefinition;
import com.filenet.api.collection.AccessPermissionList;
import com.filenet.api.collection.ClassDefinitionSet;
import com.filenet.api.core.Factory;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.security.AccessPermission;
import com.ibm.ecm.extension.PluginLogger;
import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.Set;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

public class UserReportsUtil {
  ResourceBundle resourceBundle = ConfigLoader.getBundle();
  
  Connection connection = null;
  
  ResultSet reportData = null;
  
  private static final Set<String> classValues = new HashSet<String>(Arrays.asList(new String[] { 
          "CKYC", "WorkflowDefinition", "XMLPropertyMappingScript", "CodeModule", "ScenarioDefinition", "Simulation", "PublishTemplate", "PreferencesDocument", "Email", "FormData", 
          "FormTemplate", "FormPolicy", "EntryTemplate", "StoredSearch" }));
  
  public JSONArray searchLastLoginReport(String reportType, String repositoryId, PluginLogger log, String dataSource) {
    JSONArray jsonArray = new JSONArray();
    try {
      Context context = new InitialContext();
      DataSource dataSorce = (DataSource)context.lookup(dataSource.trim());
      this.connection = dataSorce.getConnection();
      log.logDebug(this, "DBConnection::::::::::::::", this.connection.toString());
      log.logDebug(this, "repositoryId::::::::::::::", repositoryId);
      PreparedStatement preparedStatement = this.connection.prepareStatement(this.resourceBundle.getString("UserLastLoginReportQuery"));
      preparedStatement.setString(1, repositoryId);
      this.reportData = preparedStatement.executeQuery();
      log.logDebug(this, "reportData:::::::::::::::::", this.reportData.toString());
      while (this.reportData.next()) {
        log.logDebug(this, "USER NAME::::::::::::", this.reportData.getString(1));
        log.logDebug(this, "Last Login Time::::::::::::", this.reportData.getString(2));
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("USER TYPE", this.reportData.getString(1));
        jsonObject.put("FIRSTNAME", this.reportData.getString(2));
        jsonObject.put("LASTNAME", this.reportData.getString(3));
        jsonObject.put("EMAIL ID", this.reportData.getString(4));
        jsonObject.put("EMPLOYEE ID", this.reportData.getString(5));
        jsonObject.put("WINDOWS ID", this.reportData.getString(6));
        jsonObject.put("ROLE TYPE", this.reportData.getString(7));
        jsonObject.put("STATUS", this.reportData.getString(8));
        jsonObject.put("CREATED DATE", this.reportData.getTimestamp(9).toString());
        jsonObject.put("MODIFIED DATE", this.reportData.getTimestamp(10).toString());
        jsonObject.put("APPROVED DATE", this.reportData.getTimestamp(11).toString());
        jsonObject.put("USER ID", this.reportData.getString(12));
        jsonObject.put("LAST LOGIN DATE", this.reportData.getTimestamp(13).toString());
        jsonObject.put("REPOSITORY ID", this.reportData.getString(14));
        jsonArray.add(jsonObject);
      } 
      log.logDebug(this, "jsonArray::::::::", jsonArray.toString());
    } catch (Exception e) {
      e.printStackTrace();
      log.logDebug(this, "Error", e.getMessage());
    } finally {
      try {
        this.connection.close();
      } catch (SQLException e) {
        e.printStackTrace();
      } 
      try {
        this.reportData.close();
      } catch (SQLException e) {
        e.printStackTrace();
      } 
    } 
    return jsonArray;
  }
  
  public JSONArray searchSignInSignOffReport(String fromDate, String todate, String repositoryId, PluginLogger log, String dataSource) {
    JSONArray jsonArray = new JSONArray();
    try {
      Context context = new InitialContext();
      DataSource dataSorce = (DataSource)context.lookup(dataSource.trim());
      this.connection = dataSorce.getConnection();
      System.out.println("DBConnection::::::::::::::" + this.connection.toString());
      System.out.println("repositoryId::::::::::::::" + repositoryId);
      PreparedStatement preparedStatement = this.connection.prepareStatement(this.resourceBundle.getString("UserSignInSinOffQuery"));
      preparedStatement.setString(1, fromDate);
      preparedStatement.setString(2, todate);
      preparedStatement.setString(3, repositoryId);
      this.reportData = preparedStatement.executeQuery();
      System.out.println("reportData:::::::::::::::::" + this.reportData.toString());
      while (this.reportData.next()) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("USER TYPE", this.reportData.getString(1));
        jsonObject.put("FIRSTNAME", this.reportData.getString(2));
        jsonObject.put("LASTNAME", this.reportData.getString(3));
        jsonObject.put("EMAIL ID", this.reportData.getString(4));
        jsonObject.put("EMPLOYEE ID", this.reportData.getString(5));
        jsonObject.put("WINDOWS ID", this.reportData.getString(6));
        jsonObject.put("ROLE TYPE", this.reportData.getString(7));
        jsonObject.put("ACTION TYPE", this.reportData.getString(8));
        jsonObject.put("ACTION DATE", this.reportData.getTimestamp(9).toString());
        jsonObject.put("OBJECT STORE", this.reportData.getString(10));
        jsonArray.add(jsonObject);
      } 
      log.logDebug(this, "jsonArray::::::::", jsonArray.toString());
    } catch (Exception e) {
      e.printStackTrace();
      log.logDebug(this, "Error", e.getMessage());
    } finally {
      try {
        this.connection.close();
      } catch (SQLException e) {
        e.printStackTrace();
        System.out.println("error1" + e.getMessage() + e);
      } 
      try {
        this.reportData.close();
      } catch (SQLException e) {
        e.printStackTrace();
        System.out.println("error2" + e.getMessage() + e);
      } 
    } 
    return jsonArray;
  }
  
  public JSONArray documentPermissionList(ObjectStore objStore, String docClassName, PluginLogger log) {
    System.out.println("entered in to utilList");
    if (docClassName.equalsIgnoreCase("All Classes")) {
      System.out.println(docClassName + "230 doc class name");
      JSONArray jSONArray = new JSONArray();
      ClassDefinition objClassDef = Factory.ClassDefinition.fetchInstance(objStore, "Document", null);
      ClassDefinitionSet classdefinationset = objClassDef.get_ImmediateSubclassDefinitions();
      Iterator<ClassDefinition> iterateclasses = classdefinationset.iterator();
      while (iterateclasses.hasNext()) {
        ClassDefinition objClassDefNew = iterateclasses.next();
        log.logDebug(this, " all documentclasses::::::::::::::", objClassDefNew.get_SymbolicName());
        System.out.println("each doc class name 245" + objClassDefNew.get_SymbolicName());
        if (!objClassDef.get_IsSystemOwned().booleanValue() || (!objClassDef.get_IsHidden().booleanValue() && !classValues.contains(objClassDefNew.get_SymbolicName()))) {
          String accessName = "";
          StringBuffer accessMasks = null;
          try {
            String documentclasses = objClassDefNew.get_SymbolicName();
            ClassDefinition document = Factory.ClassDefinition.fetchInstance(objStore, documentclasses, null);
            AccessPermissionList permissions1 = document.get_DefaultInstancePermissions();
            Iterator<AccessPermission> it1 = permissions1.iterator();
            while (it1.hasNext()) {
              accessMasks = new StringBuffer();
              AccessPermission permission = it1.next();
              Integer get_AccessMask = permission.get_AccessMask();
              log.logDebug(this, "Mask Value is::", get_AccessMask.toString());
              log.logDebug(this, "Grantee Name=", permission.get_GranteeName());
              if ((get_AccessMask.intValue() & 0x2000000) == 33554432) {
                accessMasks.append("ADD_MARKING");
                accessMasks.append(";");
              } 
              if ((get_AccessMask.intValue() & 0x400) == 1024) {
                accessMasks.append("CHANGE_STATE");
                accessMasks.append(";");
              } 
              if ((get_AccessMask.intValue() & 0x100000) == 1048576) {
                accessMasks.append("CONNECT");
                accessMasks.append(";");
              } 
              if ((get_AccessMask.intValue() & 0x200) == 512) {
                accessMasks.append("CREATE_CHILD");
                accessMasks.append(";");
              } 
              if ((get_AccessMask.intValue() & 0x100) == 256) {
                accessMasks.append("CREATE_INSTANCE");
                accessMasks.append(";");
              } 
              if ((get_AccessMask.intValue() & 0x10000) == 65536) {
                accessMasks.append("DELETE");
                accessMasks.append(";");
              } 
              if ((get_AccessMask.intValue() & 0x10) == 16) {
                accessMasks.append("LINK");
                accessMasks.append(";");
              } 
              if ((get_AccessMask.intValue() & 0x4) == 4) {
                accessMasks.append("MAJOR_VERSION");
                accessMasks.append(";");
              } 
              if ((get_AccessMask.intValue() & 0x40) == 64) {
                accessMasks.append("MINOR_VERSION");
                accessMasks.append(";");
              } 
              if ((get_AccessMask.intValue() & 0x400000) == 4194304) {
                accessMasks.append("MODIFY_OBJECTS");
                accessMasks.append(";");
              } 
              if ((get_AccessMask.intValue() & 0x10000000) == 268435456) {
                accessMasks.append("PRIVILEGED_WRITE");
                accessMasks.append(";");
              } 
              if ((get_AccessMask.intValue() & 0x800) == 2048) {
                accessMasks.append("PUBLISH");
                accessMasks.append(";");
              } 
              if ((get_AccessMask.intValue() & 0x20000) == 131072) {
                accessMasks.append("READ_ACL");
                accessMasks.append(";");
              } 
              if ((get_AccessMask.intValue() & 0x1) == 1) {
                accessMasks.append("View All Properties");
                accessMasks.append(";");
              } 
              if ((get_AccessMask.intValue() & 0x4000000) == 67108864) {
                accessMasks.append("REMOVE_MARKING");
                accessMasks.append(";");
              } 
              if ((get_AccessMask.intValue() & 0x800000) == 8388608) {
                accessMasks.append("REMOVE_OBJECTS");
                accessMasks.append(";");
              } 
              if ((get_AccessMask.intValue() & 0x1000) == 4096) {
                accessMasks.append("RESERVED12");
                accessMasks.append(";");
              } 
              if ((get_AccessMask.intValue() & 0x2000) == 8192) {
                accessMasks.append("RESERVED13");
                accessMasks.append(";");
              } 
              if ((get_AccessMask.intValue() & 0x200000) == 2097152) {
                accessMasks.append("STORE_OBJECTS");
                accessMasks.append(";");
              } 
              if ((get_AccessMask.intValue() & 0x20) == 32) {
                accessMasks.append("UNLINK");
                accessMasks.append(";");
              } 
              if ((get_AccessMask.intValue() & 0x8000000) == 134217728) {
                accessMasks.append("USE_MARKING");
                accessMasks.append(";");
              } 
              if ((get_AccessMask.intValue() & 0x80) == 128) {
                accessMasks.append("VIEW_CONTENT");
                accessMasks.append(";");
              } 
              if ((get_AccessMask.intValue() & 0x40000000) == 1073741824) {
                accessMasks.append("VIEW_RECOVERABLE_OBJECTS");
                accessMasks.append(";");
              } 
              if ((get_AccessMask.intValue() & 0x40000) == 262144) {
                accessMasks.append("WRITE_ACL");
                accessMasks.append(";");
              } 
              if ((get_AccessMask.intValue() & 0x1000000) == 16777216) {
                accessMasks.append("WRITE_ANY_OWNER");
                accessMasks.append(";");
              } 
              if (permission.get_GranteeName().contains("@")) {
                accessName = permission.get_GranteeName().substring(0, permission.get_GranteeName().indexOf("@"));
              } else if (permission.get_GranteeName().contains("#")) {
                accessName = permission.get_GranteeName().replace("#", "");
              } 
              accessMasks.deleteCharAt(accessMasks.toString().lastIndexOf(";"));
              JSONObject jsonObject = new JSONObject();
              jsonObject.put("Document Name", objClassDefNew.get_SymbolicName());
              jsonObject.put("Grantee Name", accessName);
              jsonObject.put("Access Permissions", accessMasks.toString());
              jSONArray.add(jsonObject);
            } 
          } catch (Exception e) {
            e.printStackTrace();
          } finally {
            accessMasks = null;
          } 
        } 
      } 
      return jSONArray;
    } 
    JSONArray jsonArray = new JSONArray();
    System.out.println("in other classes ");
    try {
      ClassDefinition document = Factory.ClassDefinition.fetchInstance(objStore, docClassName, null);
      System.out.println(docClassName + "docClass name 443");
      log.logDebug(this, "document class name::::::::", document.get_Name());
      String symbolicName = document.get_SymbolicName();
      AccessPermissionList permissions1 = document.get_DefaultInstancePermissions();
      Iterator<AccessPermission> it1 = permissions1.iterator();
      String accessName = "";
      StringBuffer accessMasks = null;
      while (it1.hasNext()) {
        accessMasks = new StringBuffer();
        AccessPermission permission = it1.next();
        Integer get_AccessMask = permission.get_AccessMask();
        log.logDebug(this, "Mask Value is::", get_AccessMask.toString());
        log.logDebug(this, "Grantee Name=", permission.get_GranteeName());
        if ((get_AccessMask.intValue() & 0x2000000) == 33554432) {
          accessMasks.append("ADD_MARKING");
          accessMasks.append(";");
        } 
        if ((get_AccessMask.intValue() & 0x400) == 1024) {
          accessMasks.append("CHANGE_STATE");
          accessMasks.append(";");
        } 
        if ((get_AccessMask.intValue() & 0x100000) == 1048576) {
          accessMasks.append("CONNECT");
          accessMasks.append(";");
        } 
        if ((get_AccessMask.intValue() & 0x200) == 512) {
          accessMasks.append("CREATE_CHILD");
          accessMasks.append(";");
        } 
        if ((get_AccessMask.intValue() & 0x100) == 256) {
          accessMasks.append("CREATE_INSTANCE");
          accessMasks.append(";");
        } 
        if ((get_AccessMask.intValue() & 0x10000) == 65536) {
          accessMasks.append("DELETE");
          accessMasks.append(";");
        } 
        if ((get_AccessMask.intValue() & 0x10) == 16) {
          accessMasks.append("LINK");
          accessMasks.append(";");
        } 
        if ((get_AccessMask.intValue() & 0x4) == 4) {
          accessMasks.append("MAJOR_VERSION");
          accessMasks.append(";");
        } 
        if ((get_AccessMask.intValue() & 0x40) == 64) {
          accessMasks.append("MINOR_VERSION");
          accessMasks.append(";");
        } 
        if ((get_AccessMask.intValue() & 0x400000) == 4194304) {
          accessMasks.append("MODIFY_OBJECTS");
          accessMasks.append(";");
        } 
        if ((get_AccessMask.intValue() & 0x10000000) == 268435456) {
          accessMasks.append("PRIVILEGED_WRITE");
          accessMasks.append(";");
        } 
        if ((get_AccessMask.intValue() & 0x800) == 2048) {
          accessMasks.append("PUBLISH");
          accessMasks.append(";");
        } 
        if ((get_AccessMask.intValue() & 0x20000) == 131072) {
          accessMasks.append("READ_ACL");
          accessMasks.append(";");
        } 
        if ((get_AccessMask.intValue() & 0x1) == 1) {
          accessMasks.append("View All Properties");
          accessMasks.append(";");
        } 
        if ((get_AccessMask.intValue() & 0x4000000) == 67108864) {
          accessMasks.append("REMOVE_MARKING");
          accessMasks.append(";");
        } 
        if ((get_AccessMask.intValue() & 0x800000) == 8388608) {
          accessMasks.append("REMOVE_OBJECTS");
          accessMasks.append(";");
        } 
        if ((get_AccessMask.intValue() & 0x1000) == 4096) {
          accessMasks.append("RESERVED12");
          accessMasks.append(";");
        } 
        if ((get_AccessMask.intValue() & 0x2000) == 8192) {
          accessMasks.append("RESERVED13");
          accessMasks.append(";");
        } 
        if ((get_AccessMask.intValue() & 0x200000) == 2097152) {
          accessMasks.append("STORE_OBJECTS");
          accessMasks.append(";");
        } 
        if ((get_AccessMask.intValue() & 0x20) == 32) {
          accessMasks.append("UNLINK");
          accessMasks.append(";");
        } 
        if ((get_AccessMask.intValue() & 0x8000000) == 134217728) {
          accessMasks.append("USE_MARKING");
          accessMasks.append(";");
        } 
        if ((get_AccessMask.intValue() & 0x80) == 128) {
          accessMasks.append("VIEW_CONTENT");
          accessMasks.append(";");
        } 
        if ((get_AccessMask.intValue() & 0x40000000) == 1073741824) {
          accessMasks.append("VIEW_RECOVERABLE_OBJECTS");
          accessMasks.append(";");
        } 
        if ((get_AccessMask.intValue() & 0x40000) == 262144) {
          accessMasks.append("WRITE_ACL");
          accessMasks.append(";");
        } 
        if ((get_AccessMask.intValue() & 0x1000000) == 16777216) {
          accessMasks.append("WRITE_ANY_OWNER");
          accessMasks.append(";");
        } 
        if (permission.get_GranteeName().contains("@")) {
          accessName = permission.get_GranteeName().substring(0, permission.get_GranteeName().indexOf("@"));
        } else if (permission.get_GranteeName().contains("#")) {
          accessName = permission.get_GranteeName().replace("#", "");
        } 
        accessMasks.deleteCharAt(accessMasks.toString().lastIndexOf(";"));
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Document Name", symbolicName);
        jsonObject.put("Grantee Name", accessName);
        jsonObject.put("Access Permissions", accessMasks.toString());
        jsonArray.add(jsonObject);
      } 
      System.out.println("618" + jsonArray.toString());
      log.logDebug(this, "jsonArray::::::::::::::", jsonArray.toString());
    } catch (Exception ex) {
      ex.printStackTrace();
    } 
    return jsonArray;
  }
}
