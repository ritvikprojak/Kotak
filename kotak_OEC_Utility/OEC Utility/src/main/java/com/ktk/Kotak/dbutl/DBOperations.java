package com.ktk.Kotak.dbutl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import com.filenet.api.core.Document;
import com.filenet.api.util.Id;
import com.ktk.Kotak.constants.Constants;
import com.ktk.Kotak.filenetops.FileNetDocumentOperations;
import com.ktk.Kotak.util.ResourceLoader;
import com.ktk.Kotak.vo.OECMigrationRecordDetail;

public class DBOperations {

	
	private static final Logger logger = Logger.getLogger(DBOperations.class);
	Connection connection = null;
	ResourceLoader loader = null;
	// to get currentLocation of the file
	public String fileExistInFolder;
	String fileName = null;

	public void executeBatch(int count) throws SQLException {

		loader = new ResourceLoader();
		String dbQuery = loader.getValue("dbQuery");
		String folder1Path = loader.getValue("folder1");
		String folder2Path = loader.getValue("folder2");

		logger.info("dbQuery:: " + dbQuery);
		logger.info("Folder1Path " + folder1Path);
		logger.info("Folder2Path " + folder2Path);

		DBConnectionUtils dbConn = new DBConnectionUtils();
		ResultSet rs = null;
		Statement stmt=null;
		try {
			// System.out.println(new Date());
			connection = dbConn.getConnection();
			//FilenetConnection conn=new FilenetConnection();
			//ObjectStore objectStore = conn.getObjectStore();
			// System.out.println(objectStore+" connection establish with CE");
			// System.out.println(new Date());
			if (connection != null) {
				
				connection.setAutoCommit(false);
				stmt = connection.createStatement();
				// String dbQuery="Select * from kotak_db.oec_migration_records";

				String sql = dbQuery + count;
				// System.out.println("dbQuery with count " + sql);
				logger.info("dbQuery with count " + sql);

				rs = stmt.executeQuery(sql);
				// System.out.println(new Date());
				while (rs.next()) {
					// System.out.println("calling fetchDetailsFromDB() method ");
					logger.info("calling fetchDetailsFromDB() method ");
					OECMigrationRecordDetail fetchedDetailsFromDB = fetchDetailsFromDB(rs);
					// System.out.println(new Date());
					String url = fetchedDetailsFromDB.getUrl();

					// System.out.println("URL : "+ url);
					logger.info("URL : "+ url);
					if (url != null) {
						 fileName = getFileNameFromUrl(url);
						// System.out.println(" FileName " + fileName);
						logger.info(" FileName ::" + fileName);
						Id docId = null;
						if (fileName != null) {
							logger.info("Inside fileName if loop begins ");
							fileExistInFolder = "";
							// System.out.println(new Date());
							boolean fileExist = checkFileInFolders(fileName);
							// System.out.println("file Exist " + fileExist);
							logger.info("file Exist " + fileExist);
							// System.out.println(new Date());
							if (fileExist) {
								// System.out.println("File exist "+fileExist);
								logger.info("File exist "+fileExist);
								// integrate rajasekhar code
								// calling FileNet APIs
								// System.out.println(new Date());
								FileNetDocumentOperations filenetDocOperation = new FileNetDocumentOperations();
								Document document = filenetDocOperation.createDocument(fetchedDetailsFromDB,
										fileExistInFolder, fileName);
								// System.out.println(new Date());
								boolean docUploadedFlag = false;
								if (document != null) {
									docId = document.getProperties().getIdValue("ID");
									// once doc is moved/migrated to filenet need to update same to db
									logger.info("Document ID:: "+docId);
									if (docId != null) {
										docUploadedFlag = true;
									}

									if (docUploadedFlag) {
										// System.out.println(new Date());
										logger.info("DocUploadedFlag:: "+docUploadedFlag);
										// System.out.println(new Date());
										dataBaseUpdate(fetchedDetailsFromDB.getCrn(), Constants.DocMigrated, docId);
										// System.out.println(new Date());
										moveMigratedFileToFolder(fileName);
										connection.commit();
										// System.out.println(new Date());
									} else {
										// System.out.println(new Date());
										logger.info("DocUploadedFlag:: "+docUploadedFlag);
										dataBaseUpdate(fetchedDetailsFromDB.getCrn(), Constants.DocMigrationFailed,
												docId);
										// System.out.println(new Date());
										connection.commit();
										// System.out.println(new Date());
									}
								}

							} else {

								// System.out.println("File not exist in the folder");
								logger.info("File not exist in the folder");
								dataBaseUpdate(fetchedDetailsFromDB.getCrn(), Constants.DocNotFound, docId);
								// System.out.println("Commiting connection");
								logger.info("Commiting connection");
								connection.commit();
								// System.out.println("connection commited");
								logger.info(" connection commited");
								// need to update in db that file not exist hwence status is not extracted
							}

						} else {
							// System.out.println("Unable to retreive fileName or FileName is not present");
							logger.info("Unable to retreive fileName or FileName is not present");
						}
					} else {
						// System.out.println("URL Not Present in db for CRN"+fetchedDetailsFromDB.getCrn());
						logger.info("URL Not Present in db for CRN"+fetchedDetailsFromDB.getCrn());
					}

				}

			}
			else {
				logger.info("Connection is null. Make sure filenet system is up and running");
			}
		} catch (SQLException e) {

			e.printStackTrace();
			// System.out.println("SQL Exception DBOperation " + e.getMessage());
			logger.error("SQL Exception DBOperation " + e.getMessage());
			connection.rollback();

		} catch (Exception e) {

			e.printStackTrace();
			// System.out.println("Exception in DBOperation " + e.getMessage());
			logger.error("Exception in DBOperation " + e.getMessage());
			connection.rollback();
		} finally {
			// System.out.println("Before Closing connection");
			logger.info("Before CLosing connection");
			//// System.out.println("Before Closing connection");
			stmt.close();
			connection.close();
			
			//// System.out.println("After Closing connection");
			logger.info("after closing connection");
			// System.out.println("After Closing connection");
		}

	}

	private void moveMigratedFileToFolder(String fileName) {
		String destinationFolder = loader.getValue("destinationFolder");
		Path source = Paths.get(fileExistInFolder);
		// System.out.println("source path"+source);
		logger.info("source path"+source);
		String destinationFolderPath = destinationFolder + fileName;
		Path destination = Paths.get(destinationFolderPath);
		// System.out.println(destination + "destination path");
		logger.info("destination path"+destination);
		try {
			Files.move(source, destination);
			//Files.move(source, destination, REPLACE_EXISTING);
			// System.out.println("File moved successfully.");
			logger.info("File moved successfully.");
		} catch (IOException e) {
			System.err.println("Failed to move file: " + e.getMessage());
			logger.error("Failed to move file: " + e.getMessage());
		}

	}

	public void dataBaseUpdate(String crn, String docUpdateStatus, Id fDocId) throws SQLException {
		//to update date into table of required format
		String currentDate = getCurrentDate();
		logger.info("dataBaseUpdate method() begins ");
		if (docUpdateStatus.equalsIgnoreCase("DocMigrated")) {
			logger.info("docUpdateStatus:: "+docUpdateStatus);
			java.util.Date today = new java.util.Date();
			Timestamp ts = new java.sql.Timestamp(today.getTime());
			// System.out.println("Timestamp:: "+ts);
			logger.info("Timestamp:: "+ts);
			
			
			Id docId = fDocId;
			// System.out.println("currentDate:: "+currentDate);
			logger.info("currentDate:: "+currentDate);
			// System.out.println("docId:: "+docId);
			logger.info("docId:: "+docId);
			// System.out.println("CRN:: "+crn);
			logger.info("CRN:: "+crn);
			
			String updateSql = "UPDATE navreports.OEC_MIGRATION_RECORDS " + "SET MIGRATION_STATUS = '" + Constants.SUCCESS
					+ "', COMMENTS='Documents Migrated successfully'," + "DOCID='" + docId + "'," + "COMPLETED_TIME = TO_DATE('"+currentDate+"', 'DD-MON-YY') "
							+ "WHERE URL LIKE '%" + fileName + "%'";
			// System.out.println("Update SQL: "+updateSql);
			logger.info("Update SQL: "+updateSql);
			
			int rowsUpdated = 0;
			Statement stmt1 = null;
			try {
				logger.info("Before Creating Statement connection: "+connection);
				// System.out.println("Before Creating Statement connection: "+connection);
				stmt1 = connection.createStatement();
				logger.info("After Creating Statement connection: "+connection+"stmt1 "+stmt1);
				// System.out.println("After Creating Statement connection: "+connection+"stmt1 "+stmt1);
				rowsUpdated = stmt1.executeUpdate(updateSql);
				// System.out.println(rowsUpdated + "rows updated in db");
				logger.info(rowsUpdated + "rows updated in db");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				logger.error("Error occurred while updating DocMigrated ");
				e.printStackTrace();
				
			} 
			finally {
				// System.out.println("Inside finally block");
				logger.info("Inside finally block");
				stmt1.close();
				logger.info("Exiting finally block");
				// System.out.println("Exiting finally block");
			}

			
		} else if (docUpdateStatus.equalsIgnoreCase("DocNotFound")) {
			logger.info("docUpdateStatus:: "+docUpdateStatus);
			java.util.Date today = new java.util.Date();
			Timestamp ts = new java.sql.Timestamp(today.getTime());
			// System.out.println("Timestamp:: "+ts);
			logger.info("Timestamp:: "+ts);
			String docId = "";
			// System.out.println("currentDate:: "+currentDate);
			logger.info("currentDate:: "+currentDate);
			// System.out.println("docId:: "+docId);
			logger.info("docId:: "+docId);
			// System.out.println("CRN:: "+crn);
			logger.info("CRN:: "+crn);
			String updateSql = "UPDATE navreports.OEC_MIGRATION_RECORDS " + "SET MIGRATION_STATUS = '" + Constants.FAILURE
					+ "', COMMENTS='Documents Migration unsuccessfull'," + "DOCID='" + docId + "'," + "COMPLETED_TIME = TO_DATE('"+currentDate+"', 'DD-MON-YY') "
							+ "WHERE URL LIKE '%" + fileName + "%'";
			// System.out.println("Update SQL: "+updateSql);
			logger.info("Update SQL: "+updateSql);
			Statement stmt1 = null;
			
			int rowsUpdated = 0;
			try {
				logger.info("Before Creating Statement connection: "+connection);
				// System.out.println("Before Creating Statement connection: "+connection);
				stmt1 = connection.createStatement();
				logger.info("After Creating Statement connection: "+connection+"stmt1 "+stmt1);
				// System.out.println("After Creating Statement connection: "+connection+"stmt1 "+stmt1);
				rowsUpdated = stmt1.executeUpdate(updateSql);
				// System.out.println(rowsUpdated + "rows updated in db");
				logger.info(rowsUpdated + "rows updated in db");
			} catch (SQLException e) {
				logger.error("Error occurred while updating DocNotFailed ");
				// TODO Auto-generated catch block
				e.printStackTrace();
				//logger.error("Error occurred while updating DocNotFailed ");
			} finally {
				// System.out.println("Inside finally block");
				logger.info("Inside finally block");
				stmt1.close();
				logger.info("Exiting finally block");
				// System.out.println("Exiting finally block");
			}
			/*Statement stmt1 = connection.createStatement();
			logger.info("After Creating Statement connection: "+connection+"stmt1 "+stmt1);
			// System.out.println("After Creating Statement connection: "+connection+"stmt1 "+stmt1);
			rowsUpdated = stmt1.executeUpdate(updateSql);
			// System.out.println(rowsUpdated + "rows updated in db");
			logger.info(rowsUpdated + "rows updated in db");*/
		}

		else if (docUpdateStatus.equalsIgnoreCase("DocMigrationFailed")) {
			logger.info("docUpdateStatus:: "+docUpdateStatus);
			java.util.Date today = new java.util.Date();
			Timestamp ts = new java.sql.Timestamp(today.getTime());
			// System.out.println("Timestamp:: "+ts);
			logger.info("Timestamp:: "+ts);
			
			String docId = "";
			// System.out.println("currentDate:: "+currentDate);
			logger.info("currentDate:: "+currentDate);
			// System.out.println("docId:: "+docId);
			logger.info("docId:: "+docId);
			// System.out.println("CRN:: "+crn);
			logger.info("CRN:: "+crn);
			String updateSql = "UPDATE navreports.OEC_MIGRATION_RECORDS " + "SET MIGRATION_STATUS = '" + Constants.FAILURE
					+ "', COMMENTS='Documents Migration unsuccessfull'," + "DOCID='" + docId + "'," + "COMPLETED_TIME = TO_DATE('"+currentDate+"', 'DD-MON-YY') "
							+ "WHERE URL LIKE '%" + fileName + "%'";
			// System.out.println("Update SQL: "+updateSql);
			logger.info("Update SQL: "+updateSql);
			Statement stmt1 = null;
			int rowsUpdated = 0;
			try {
				logger.info("Before Creating Statement connection: "+connection);
				// System.out.println("Before Creating Statement connection: "+connection);
				stmt1 = connection.createStatement();
				logger.info("After Creating Statement connection: "+connection+"stmt1 "+stmt1);
				// System.out.println("After Creating Statement connection: "+connection+"stmt1 "+stmt1);
				rowsUpdated = stmt1.executeUpdate(updateSql);
				// System.out.println(rowsUpdated + "rows updated in db");
				logger.info(rowsUpdated + "rows updated in db");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				logger.error("Error occurred while updating DocMigrationFailed ");
				e.printStackTrace();
				
			}

			finally {
				// System.out.println("Inside finally block");
				logger.info("Inside finally block");
				stmt1.close();
				logger.info("Exiting finally block");
				// System.out.println("Exiting finally block");
			}

			
		}
		else if(docUpdateStatus.equalsIgnoreCase("fileNetClassNotFound")) {
			logger.info("docUpdateStatus:: "+docUpdateStatus);
			java.util.Date today = new java.util.Date();
			Timestamp ts = new java.sql.Timestamp(today.getTime());
			// System.out.println("Timestamp:: "+ts);
			logger.info("Timestamp:: "+ts);
			String docId = "";
			// System.out.println("currentDate:: "+currentDate);
			logger.info("currentDate:: "+currentDate);
			// System.out.println("docId:: "+docId);
			logger.info("docId:: "+docId);
			// System.out.println("CRN:: "+crn);
			logger.info("CRN:: "+crn);
			String updateSql = "UPDATE navreports.OEC_MIGRATION_RECORDS " + "SET MIGRATION_STATUS = '" + Constants.FAILURE
					+ "', COMMENTS='Documents Migration unsuccessfull'," + "DOCID='" + docId + "'," + "COMPLETED_TIME = TO_DATE('"+currentDate+"', 'DD-MON-YY') "
							+ "WHERE URL LIKE '%" + fileName + "%'";
			// System.out.println("Update SQL: "+updateSql);
			logger.info("Update SQL: "+updateSql);
			Statement stmt1 = null;
			int rowsUpdated = 0;
			try {
				stmt1 = connection.createStatement();
				rowsUpdated = stmt1.executeUpdate(updateSql);
				// System.out.println(rowsUpdated + "rows updated in db");
				logger.info(rowsUpdated + "rows updated in db");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logger.error("Error occurred while fetching filenet class ");
			}

			finally {
				stmt1.close();
			}
		}
		else if(docUpdateStatus.equalsIgnoreCase("fileNetClassNotFound")) {
			logger.info("docUpdateStatus:: "+docUpdateStatus);
			java.util.Date today = new java.util.Date();
			Timestamp ts = new java.sql.Timestamp(today.getTime());
			// System.out.println("Timestamp:: "+ts);
			logger.info("Timestamp:: "+ts);
			String docId = "";
			// System.out.println("currentDate:: "+currentDate);
			logger.info("currentDate:: "+currentDate);
			// System.out.println("docId:: "+docId);
			logger.info("docId:: "+docId);
			// System.out.println("CRN:: "+crn);
			logger.info("CRN:: "+crn);
			String updateSql = "UPDATE navreports.OEC_MIGRATION_RECORDS " + "SET MIGRATION_STATUS = '" + Constants.FAILURE
					+ "', COMMENTS='Documents Migration unsuccessfull'," + "DOCID='" + docId + "'," + "COMPLETED_TIME = TO_DATE('"+currentDate+"', 'DD-MON-YY') "
							+ "WHERE URL LIKE '%" + fileName + "%'";
			// System.out.println("Update SQL: "+updateSql);
			logger.info("Update SQL: "+updateSql);
			int rowsUpdated = 0;
			Statement stmt1 = null;
			try {
				stmt1 = connection.createStatement();
				rowsUpdated = stmt1.executeUpdate(updateSql);
				// System.out.println(rowsUpdated + "rows updated in db");
				logger.info(rowsUpdated + "rows updated in db");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logger.error("Error occurred while fetching filenet class ");
			}

			finally {
				stmt1.close();
			}
		}
		else if(docUpdateStatus.equalsIgnoreCase("objectStoreNotFound")) {
			logger.info("docUpdateStatus:: "+docUpdateStatus);
			java.util.Date today = new java.util.Date();
			Timestamp ts = new java.sql.Timestamp(today.getTime());
			// System.out.println("Timestamp:: "+ts);
			logger.info("Timestamp:: "+ts);
			String docId = "";
			// System.out.println("currentDate:: "+currentDate);
			logger.info("currentDate:: "+currentDate);
			// System.out.println("docId:: "+docId);
			logger.info("docId:: "+docId);
			// System.out.println("CRN:: "+crn);
			logger.info("CRN:: "+crn);
			String updateSql = "UPDATE navreports.OEC_MIGRATION_RECORDS " + "SET MIGRATION_STATUS = '" + Constants.FAILURE
					+ "', COMMENTS='Documents Migration unsuccessfull'," + "DOCID='" + docId + "'," + "COMPLETED_TIME = TO_DATE('"+currentDate+"', 'DD-MON-YY') "
							+ "WHERE URL LIKE '%" + fileName + "%'";
			// System.out.println("Update SQL: "+updateSql);
			logger.info("Update SQL: "+updateSql);
			int rowsUpdated = 0;
			Statement stmt1 = null;
			try {
				stmt1 = connection.createStatement();
				rowsUpdated = stmt1.executeUpdate(updateSql);
				// System.out.println(rowsUpdated + "rows updated in db");
				logger.info(rowsUpdated + "rows updated in db");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logger.error("Error occurred while fetching objectStore ");
			}

			finally {
				stmt1.close();
			}
		}
		

	}



	private static String getCurrentDate() {
		// System.out.println("Inside currentDate method");
		logger.info("Inside currentDate method");
		Date date = new Date();

        // Create a SimpleDateFormat object with the Oracle default date format
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy");

        // Format the date using the SimpleDateFormat
        String formattedDate = dateFormat.format(date);

        // Print the formatted date
        // System.out.println("Formatted Date: " + formattedDate);
        logger.info("Formatted Date: " + formattedDate);
		return formattedDate;
	}

	private boolean checkFileInFolders(String fileName) {
		logger.info(" checkFileInFolders begins ");
		loader = new ResourceLoader();

		String folder1Path = loader.getValue("folder1");
		String folder2Path = loader.getValue("folder2");
		
		logger.info(" folder1Path: " + folder1Path);
		logger.info(" folder2Path: " + folder2Path);
		if (folder1Path != null) {
			String filePath = folder1Path + fileName;
			// System.out.println("filePath"+filePath);
			logger.info("filePath"+filePath);
			File file = new File(filePath);
			boolean exists = file.exists();
			if (exists) {
				fileExistInFolder = filePath;
				return true;
			}
		}
		if (folder2Path != null) {
			String filePath = folder2Path + fileName;
			// System.out.println("filePath"+filePath);
			logger.info("filePath"+filePath);
			File file = new File(filePath);
			boolean exists = file.exists();
			if (exists) {
				fileExistInFolder = filePath;
				return true;
			}
		}
		logger.info(" checkFileInFolders ends ");
		return false;

	}

	private String getFileNameFromUrl(String url) {
		// System.out.println(" Inside getFileNameFromUrl() method" + url);
		logger.info(" Inside getFileNameFromUrl() method" + url);
		int n = url.lastIndexOf("/");
		String fileName = url.substring(n + 1);
		// System.out.println(" fileName extracting from url "+fileName);
		logger.info(" fileName extracting from url "+fileName);
		return fileName;
	}

	private OECMigrationRecordDetail fetchDetailsFromDB(ResultSet rs) {
		OECMigrationRecordDetail record = new OECMigrationRecordDetail();
		try {

			// System.out.println("Inside rs");
			//int id = rs.getInt("id");
			String CRN = rs.getString("CRN");
			String name = rs.getString("NAME");
			String accountNumber = rs.getString("ACCOUNTNO");
			Timestamp oecDate = rs.getTimestamp("OECDATE");
			String fileBarCode = rs.getString("FILEBARCODE");
			String doc_type = rs.getString("DOC_TYPE");
			String url = rs.getString("URL");

			/*
			 * // System.out.println("CRN "+CRN);
			 * // System.out.println("accountNumber "+accountNumber);
			 * // System.out.println("oecDate "+oecDate); // System.out.println("name "+name);
			 * // System.out.println("fileBarCode "+fileBarCode);
			 * // System.out.println("doc_type "+doc_type); // System.out.println("id "+id);
			 * // System.out.println("url "+url);
			 */

			//record.setId(id);
			record.setOecDate(oecDate);
			record.setCrn(CRN);
			record.setName(name);
			record.setAccountNumber(accountNumber);
			record.setFileBarCode(fileBarCode);
			record.setDoc_type(doc_type);
			record.setUrl(url);

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return record;

	}
	
//	public static void main(String[] args) {
//		String fileName = "KBSI1111002323.TIF";
//		String docId = "{721e94c3-a76d-46ac-bd80-24fa683d3320}";
//		String currentDate = "";
//		String updateSql = "UPDATE navreports.OEC_MIGRATION_RECORDS " + "SET MIGRATION_STATUS = '" + Constants.SUCCESS
//				+ "', COMMENTS='Documents Migrated successfully'," + "DOCID='" + docId + "'," + "COMPLETED_TIME = TO_DATE('"+currentDate+"', 'DD-MON-YY') "
//						+ "WHERE URL LIKE '%" + fileName + "%'";
//		System.out.println("SQL query: "+updateSql);
//	}

}
