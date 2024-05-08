package com.ktk.Kotak.filenetops;

import java.io.File;
import java.io.FileInputStream;
import java.util.Date;

import org.apache.log4j.Logger;

import com.filenet.api.collection.ContentElementList;
import com.filenet.api.constants.AutoClassify;
import com.filenet.api.constants.AutoUniqueName;
import com.filenet.api.constants.CheckinType;
import com.filenet.api.constants.DefineSecurityParentage;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.core.ContentTransfer;
import com.filenet.api.core.Document;
import com.filenet.api.core.DynamicReferentialContainmentRelationship;
import com.filenet.api.core.Factory;
import com.filenet.api.core.Folder;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.core.ReferentialContainmentRelationship;
import com.filenet.api.exception.EngineRuntimeException;
import com.filenet.api.property.Properties;
import com.ktk.Kotak.constants.Constants;
import com.ktk.Kotak.dbutl.DBOperations;
import com.ktk.Kotak.util.ResourceLoader;
import com.ktk.Kotak.vo.OECMigrationRecordDetail;



public class FileNetDocumentOperations {
	private static final Logger logger = Logger.getLogger(FileNetDocumentOperations.class);
	private ResourceLoader loader;
	public Document createDocument(OECMigrationRecordDetail fetchedDetailsFromDB, String fileExistInFolder, String fileName )
			
			throws Exception {
		// System.out.println(new Date());
		try {
			logger.info("Start of createDocument() method.");
			// System.out.println("Start of createDocument() method.");
			FilenetConnection conn=new FilenetConnection();
			loader=new ResourceLoader();
			ObjectStore objectStore = conn.getObjectStore();

			// String docClass = documentClass;
			String docFClass=loader.getValue("filenet.document.class");
			String docClass = docFClass;
			// System.out.println("docClass "+docFClass);
			logger.info("docClass :: "+docFClass);
			if(objectStore!=null) {
				if(docFClass!=null) {
					Document document = Factory.Document.createInstance(objectStore, docFClass);
					// System.out.println("document instance created "+document);
					logger.info("document instance created "+document);
					// System.out.println(new Date());
					mapDocumentProperties(document.getProperties(),fetchedDetailsFromDB, fileName);
					// System.out.println(new Date());
					addContentElements(document, fetchedDetailsFromDB, fileExistInFolder);
					// System.out.println(new Date());
					document.checkin(AutoClassify.DO_NOT_AUTO_CLASSIFY, CheckinType.MAJOR_VERSION);
					// System.out.println(new Date()+"after checkin");
					document.save(RefreshMode.REFRESH);
					// System.out.println("document with content and metadata saved into filenet system ");
					// System.out.println(new Date());
					logger.info("document with content and metadata saved into filenet system ");
					// System.out.println(new Date());
					fileDocumentInFolder(objectStore, document );
					// System.out.println(new Date());
					
					//logger.debug("Document successfully created with docId: {} in '{}' document class.", document.get_Id(),
					//		docClass);
					return document;
				}
				else {
					logger.info("Filenet class Not found "+docFClass);
					DBOperations dbOp=new DBOperations();
					dbOp.dataBaseUpdate(fetchedDetailsFromDB.getCrn(), Constants.FileNetClassNotFound, null);
				}
			}
			else {
				logger.info("objectStore not found "+objectStore);
				DBOperations dbOp=new DBOperations();
				dbOp.dataBaseUpdate(fetchedDetailsFromDB.getCrn(), Constants.ObjectStoreNotFound, null);
			}
			

		} catch (EngineRuntimeException exception) {
			logger.error("An exception occurred while  createing the  method."+exception.getMessage());
			
		} catch (Exception exception) {
			logger.error("An exception occurred in createDocument() method."+exception.getMessage());
		}
		return null;

		

	}
	
	private void addContentElements(Document document, OECMigrationRecordDetail fetchedDetailsFromDB, String fileExistInFolder) throws Exception {

		logger.info("Start of addContentElements() method.");
		File internalFile = new File(fileExistInFolder);
		FileInputStream fileIS = new FileInputStream(internalFile.getAbsolutePath());
		
		try {
			ContentTransfer ctObject = Factory.ContentTransfer.createInstance();
			ContentElementList contentList = Factory.ContentTransfer.createList();

			ctObject.setCaptureSource(fileIS);
			ctObject.set_RetrievalName(internalFile.getName());
			// System.out.println(internalFile.getName());
			contentList.add(ctObject);
			document.set_ContentElements(contentList);

			logger.info("Content element sucessfully added to the document.");
			logger.info("End of addContentElements() method.");

		} catch (Exception exception) {
			logger.error("An exception occurred in addContentElements() method."+exception.getMessage());
		}
	}
	
	private void mapDocumentProperties(Properties properties, 
			OECMigrationRecordDetail fetchedDetailsFromDB, String fileName) throws Exception {

		logger.info("Start of mapDocumentProperties() method.");
		try {	properties.putObjectValue(Constants.CRN,fetchedDetailsFromDB.getCrn());
		properties.putObjectValue(Constants.CUSTOMER_NAME,fetchedDetailsFromDB.getName());
		properties.putObjectValue(Constants.ACCOUNT_NUMBER,fetchedDetailsFromDB.getAccountNumber());
		properties.putObjectValue(Constants.BAR_CODE, fetchedDetailsFromDB.getFileBarCode());
		properties.putValue(Constants.OECDATE,fetchedDetailsFromDB.getOecDate());
		properties.putValue(Constants.DocType,fetchedDetailsFromDB.getDoc_type());
		properties.putObjectValue(Constants.DocumentTitle, fileName);
		} catch (Exception exception) {
			logger.error("An exception occurred in mapDocumentpropeties() method."+exception.getMessage());
		}
	}
	private void fileDocumentInFolder(ObjectStore objectStore, Document document) {
		logger.info("Start of fileDocumentInFolder() method.");
		// System.out.println(new Date());
	int trialCount = 0;
		while (trialCount <= 1) {
			try {
				// System.out.println(new Date());
				String fileNetFolderPath=loader.getValue("filenet.root.folderPath");
				// System.out.println(new Date());
				String folderPath = fileNetFolderPath;
				// System.out.println(new Date());
				// System.out.println("fileDocumentInFolder"+folderPath);
				logger.info("fileDocumentInFolder"+folderPath);
				// System.out.println(new Date());
				Folder folder = Factory.Folder.fetchInstance(objectStore, folderPath, null);
				// System.out.println(new Date());
				DynamicReferentialContainmentRelationship drcr = (DynamicReferentialContainmentRelationship) folder.file(document, AutoUniqueName.NOT_AUTO_UNIQUE, null,
						DefineSecurityParentage.DO_NOT_DEFINE_SECURITY_PARENTAGE);
				// System.out.println(new Date());
				drcr.save(RefreshMode.NO_REFRESH);
				// System.out.println("Document has been fileIn successfully");
				logger.info("Document has been fileIn successfully");

				break;
			} catch (EngineRuntimeException exception) {
				logger.error("An exception occurred in fileDocumentInFolder() method."+exception.getMessage());
			}
		}
	}

}
