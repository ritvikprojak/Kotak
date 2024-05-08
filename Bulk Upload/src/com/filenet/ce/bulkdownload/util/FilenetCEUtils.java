package com.filenet.ce.bulkdownload.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.apache.poi.util.IOUtils;

import com.filenet.api.collection.ContentElementList;
import com.filenet.api.collection.IndependentObjectSet;
import com.filenet.api.constants.AutoClassify;
import com.filenet.api.constants.AutoUniqueName;
import com.filenet.api.constants.CheckinType;
import com.filenet.api.constants.DefineSecurityParentage;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.core.ContentTransfer;
import com.filenet.api.core.Document;
import com.filenet.api.core.Domain;
import com.filenet.api.core.Factory;
import com.filenet.api.core.ReferentialContainmentRelationship;
import com.filenet.api.events.DocumentClassificationAction;
import com.filenet.api.exception.EngineRuntimeException;
import com.filenet.api.core.Folder;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.property.Properties;
import com.filenet.api.property.Property;
import com.filenet.api.query.SearchSQL;
import com.filenet.api.query.SearchScope;
import com.filenet.api.util.Id;
import com.filenet.apiimpl.core.DocumentImpl;

public class FilenetCEUtils {

	final static Logger logger = Logger.getLogger(FilenetCEUtils.class);
	final static ResourceBundle rsbundle = ResourceData.getResourceBundle();



	public String downloadFiles(Domain domain, String cm_TARGETOS, LinkedList<String> headerList, LinkedList<String> rowList, String downloadPath) throws FileNotFoundException, IOException {

		ObjectStore objectStore = Factory.ObjectStore.fetchInstance(domain, cm_TARGETOS, null);

		if(logger.isInfoEnabled()){
			logger.info("Download file contents in ::"+ objectStore.get_DisplayName());
		} 

		/*
		 * Creating SQLQuery String
		 */
		String SQLQuery = "SELECT * FROM ";
		String folderName = "";
		if (headerList.get(0).toString().equalsIgnoreCase("Doc_Class")) {
			SQLQuery = SQLQuery + " " + rowList.get(0) + "  as t where ";
			folderName = rowList.get(0).toString() + "_"+ rowList.get(1).toString();
			for (int i = 1; i < rowList.size(); i++) {
				String symbolicName = headerList.get(i);
				String value = rowList.get(i);
				if (symbolicName != null && value != null) {
					SQLQuery = SQLQuery + " t.[" + symbolicName.trim() + "] = ";
					SQLQuery = SQLQuery + " '" + value.trim() + "' AND ";
				}
			}
		}

		int endIndex = SQLQuery.length() - 5;
		SQLQuery = SQLQuery.substring(0, endIndex);
		SearchSQL sqlObject = new SearchSQL(SQLQuery);
		if(logger.isInfoEnabled()){
			logger.info("SQL Query: " + sqlObject.toString());
		} 
		/*
		 * Creating SQLQuery String Ends
		 */
		// Sets the Search Scope Limited to Targeted(Given) OS
		SearchScope searchScope = new SearchScope(objectStore);

		// Firing SQL Query
		// RepositoryRowSet repositoryRowSet = searchScope.fetchRows(sqlObject,
		// null, null, new Boolean(true)); // Executes the content search
		IndependentObjectSet independentObjectSet = searchScope.fetchObjects(sqlObject, new Integer(10), null, new Boolean(true));
		int count = 0;
		if (!independentObjectSet.isEmpty()) {
			// if rowset NOT EMPTY
			if(logger.isInfoEnabled()){
				logger.info(" SQL Query Result set is not empty  for ::");
			}
			Iterator<?> iter = independentObjectSet.iterator();
			while (iter.hasNext()) {
				try {
					Document doc = (Document) iter.next();
					Properties properties = doc.getProperties();
					String ID = properties.getIdValue("ID").toString();
					System.out.println(ID);
					ContentElementList docContentList = doc.get_ContentElements();
					Iterator<?> contentItr = docContentList.iterator();
					while (contentItr.hasNext()) {
						ContentTransfer ct = (ContentTransfer) contentItr.next();
						if(logger.isInfoEnabled()){
							logger.info("Content file name "+Calendar.getInstance().getTimeInMillis()+"_"+ct.get_RetrievalName() + "\t Content type: " + ct.get_ContentType());
						}

						File directory = new File(downloadPath +File.separator+folderName);
						if (!directory.exists()) {
							directory.mkdir();
						}
						FileOutputStream fos = new FileOutputStream(downloadPath +File.separator+ folderName + File.separator+Calendar.getInstance().getTimeInMillis()+"_"+ ct.get_RetrievalName());
						InputStream stream = ct.accessContentStream();
						IOUtils.copy(stream, fos);
						fos.close();
						stream.close();
					}
					count++;
				} catch (Exception e) {
					logger.error("Exception ::"+e.getMessage());
					e.printStackTrace();
					return "0,Exception";
				}
			}
			if(logger.isInfoEnabled()){
				logger.info("Downloaded documents count is ::"+count);
				logger.info(" >> Documents downloaded successfully ::");
			}
			return count +","+ downloadPath +File.separator+ folderName;
		} else {
			// if rowset returns empty
			if(logger.isInfoEnabled()){
				logger.info(" SQL Query returned empty Result Set for this search criteria ");
				logger.info(" >> No documents documents found for this search criteria ::"+count);
			}
			return "0, N/A";
		}
	}

	public String uploadFiles(Domain domain, String cm_TARGETOS, LinkedList<String> headerList, LinkedList<String> rowList, String downloadPath){

		ObjectStore objectStore = Factory.ObjectStore.fetchInstance(domain, cm_TARGETOS, null);
		Document doc = null;
		Id id = null;
		Folder fold = null;
		String Doc_Folder;
		ReferentialContainmentRelationship rcr = null;
		String returnString = null;
		boolean flag = false;
		if(logger.isInfoEnabled()){
			logger.info("Upload file contents in ::"+ objectStore.get_DisplayName());
		}
		int List_items = headerList.size();
		try{
			testloop:for(int i = 0;i<List_items;i++){
				
				Properties prop = null;
				

				switch(headerList.get(i).trim()){
				case "Document Classification" :
					logger.info(headerList.get(i).trim() +" " + rowList.get(i).trim() );
					try {
					if(headerList.get(i).trim() != null || headerList.get(i).trim()!="") {
//						if(Factory.Document.createInstance(objectStore, rowList.get(i).trim()).equals(null)) {
				doc = Factory.Document.createInstance(objectStore, rowList.get(i).trim());
					}
					else {
						flag = false;
					}
					}
					catch(Exception e) {
						returnString = "Failed, Document is Undefined";
						break testloop;
					}
//					}
//					catch (NullPointerException ex) {
//						System.err.println("Null Point Exception");
//					}
//					doc.save(RefreshMode.REFRESH);
					break;
				case "KDMS_EmployeeCode" :
					if(headerList.get(i).trim() != null || headerList.get(i).trim()!="") {
						prop = doc.getProperties();
						logger.info(headerList.get(i).trim() +" " + rowList.get(i).trim() );
						logger.info(" Is property present "+prop.isPropertyPresent(headerList.get(i).trim()));
						prop.putValue(headerList.get(i).trim(),rowList.get(i).trim());
//						doc.save(RefreshMode.REFRESH);
						flag = true;
					}
					else {
						flag = false;
					}
					break;
				case "Storage Path" :
					File File = new File(rowList.get(i).trim());
					logger.info(headerList.get(i).trim() +" " + rowList.get(i).trim() );
					String typearr = rowList.get(i).substring( rowList.get(i).lastIndexOf(".")+1, rowList.get(i).length());
					String type = null;
					if (typearr.equalsIgnoreCase("pdf")){
						type = "application/pdf";
					} else if(typearr.equalsIgnoreCase("jpeg")){
						type = "image/jpeg";
					}else if(typearr.equalsIgnoreCase("jpg")){
						type = "image/jpg";
					}else if(typearr.equalsIgnoreCase("tiff")){
						type = "image/tiff";
					}
					String []str = rowList.get(i).split("\\\\");
					System.out.println(str);
					String name = str[str.length-1];
					System.out.println(name);
					
					doc.getProperties().putValue("DocumentTitle", name);
					try {
						// First, add a ContentTransfer object.
						ContentTransfer ctObject = Factory.ContentTransfer.createInstance();
						FileInputStream fileIS = new FileInputStream(rowList.get(i).trim());
						ContentElementList contentList = Factory.ContentTransfer.createList();
						ctObject.setCaptureSource(fileIS);
						ctObject.set_ContentType(type);
						// Add ContentTransfer object to list.
						contentList.add(ctObject);
						doc.set_ContentElements(contentList);
						rcr = fold.file(doc, AutoUniqueName.AUTO_UNIQUE, null, DefineSecurityParentage.DEFINE_SECURITY_PARENTAGE);
//						doc.save(RefreshMode.REFRESH);
						flag=true;
					}

					catch (Exception e)
					{
						System.out.println(e.getMessage() );
						return returnString = "Failed, Path is Wrong";
//						returnString = "Failed, File Path Error";
					}
					doc.checkin(AutoClassify.DO_NOT_AUTO_CLASSIFY, CheckinType.MAJOR_VERSION);
//					doc.save(RefreshMode.REFRESH);
					
					
					break;
				case "KDMS_DocumentType" :
					if(headerList.get(i).trim() != null || headerList.get(i).trim()!="") {
						try{
						prop = doc.getProperties();
						logger.info(headerList.get(i).trim() +" " + rowList.get(i).trim() );
						logger.info(" Is property present "+prop.isPropertyPresent(headerList.get(i).trim()));
						prop.putValue(headerList.get(i).trim(),rowList.get(i).trim());
//						doc.save(RefreshMode.REFRESH);
						String Doc_Type = rowList.get(i).trim();
						Doc_Folder = rsbundle.getString("RootFolder") + Doc_Type;
						fold = Factory.Folder.fetchInstance(objectStore, Doc_Folder, null);
//						rcr = fold.file(doc, AutoUniqueName.AUTO_UNIQUE, null, DefineSecurityParentage.DEFINE_SECURITY_PARENTAGE);
						flag = true;
					}
						catch(Exception e){
							System.out.println(e.getMessage());
							return returnString = "Failed,Document type is wrong";
						}
					}
					else {
						flag = false;
					}
					
					
//					doc.checkin(AutoClassify.DO_NOT_AUTO_CLASSIFY, CheckinType.MAJOR_VERSION);
					
					break;
				case "KDMS_DocumentSubType" : 
					try{
					if(headerList.get(i).trim() != null || headerList.get(i).trim()!="") {
						prop = doc.getProperties();
						logger.info(headerList.get(i).trim() +" " + rowList.get(i).trim() );
						logger.info(" Is property present "+prop.isPropertyPresent(headerList.get(i).trim()));
						prop.putValue(headerList.get(i).trim(),rowList.get(i).trim());
//						doc.save(RefreshMode.REFRESH);
//						String Doc_Type = rowList.get(i).trim();
//						String Doc_SubType = rowList.get(i-1).trim();
//						String Doc_Folder = rsbundle.getString("RootFolder") + rsbundle.getString(Doc_SubType) + rsbundle.getString(Doc_Type);
//						Folder fold = Factory.Folder.fetchInstance(objectStore, Doc_Folder, null);
//						rcr = fold.file(doc, AutoUniqueName.AUTO_UNIQUE, null, DefineSecurityParentage.DEFINE_SECURITY_PARENTAGE);
						flag = true;
					}
					else {
						flag = false;
					}
					}
					catch(Exception e){
						System.out.println(e.getMessage());
						return returnString ="Failed,Document SubType is Wrong";
					}
					break;
//				case "Status" : 
//					returnString = "Sucess";
//					break;
//				case "Error Message" :
//					returnString = returnString + ", N/A";
//					break;
//				default:
//					returnString = "Failed, Data Is Incorrect";


				}
				
			}
			if(flag == true) {
				returnString = "Success, N/A";
			}


			if(doc!=null && rcr!=null) {
			doc.save(RefreshMode.REFRESH);
			rcr.save(RefreshMode.REFRESH);
			}
			
		}

//		catch (Exception e){
//			e.printStackTrace();
//			logger.info("Upload Failed due to \n" + e.fillInStackTrace());
//			returnString = "Failed,"+e.fillInStackTrace();
		
//		}
		catch(MissingResourceException msgex) {
			logger.info("Upload Failed Due to \n" + msgex.fillInStackTrace());
			returnString = "Failed, Data is incomplete or Incorrect";
		}
		catch(EngineRuntimeException engex) {
			logger.info("Upload failed due to \n" + engex.fillInStackTrace() );
			returnString = "Failed, Error in Document Subtype or Connection, Please Check";
		}
		
		catch(Exception e) {
			e.printStackTrace();
			logger.info("Upload Failed Due to \n" + e.fillInStackTrace());
			returnString = "Failed," + e.fillInStackTrace();
		}
		
		return returnString;
		
		
//		finally{
//			if(doc!=null && rcr!=null) {
//			doc.save(RefreshMode.REFRESH);
//			rcr.save(RefreshMode.REFRESH);
//			}
//			return returnString;
//		}







	}
}
