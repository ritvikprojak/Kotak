package com.ktk.Kotak.constants;

import com.ktk.Kotak.util.ResourceLoader;

public class Constants {
	
	static ResourceLoader loader=new ResourceLoader();




	public static String ObjectStoreNotFound="objectStoreNotFound";

	
	 
	
	public static final String CRN = "CRN";

	public static final String CUSTOMER_NAME = "CustomerName";

	public static final String ACCOUNT_NUMBER = "Account_Number";

	public static final String BAR_CODE = "Barcode";	

	public static final String OECDATE = "OECDate";
	
	public static final String DocType = "Document_Type";
	
	public static final String DocMigrated = "DocMigrated";
	
	public static final String DocNotFound = "DocNotFound";
	
	public static final String DocMigrationFailed = "DocMigrationFailed";
	
	public static final String DocumentTitle = "DocumentTitle";
	
	public static final String SqlExceptionGetConnection = "SqlExceptionGetConnection";
	
	public static final String SUCCESS = "SUCCESS";
	
	public static final String FAILURE = "FAIL";
	
	//public static final String DOC_CLASS = LoadProperties.getValue("filenet.documentclass");
	
	public static final String DOC_CLASS = loader.getValue("filenet.documentclass");
	
	public static final String FolderPath = loader.getValue("filenet.root.folderPath");
	
	
	public static  final String  ceUrl=loader.getValue("filenet.url");

	public static final String objectStoreName=loader.getValue("filenet.objectstore");

	public static final String domain=loader.getValue("filenet.domain");

	public static final String username=loader.getValue("filenet.username");

	public static final String password=loader.getValue("filenet.password");




	public static final String ConnectionNotEstablish = "Make sure Filenet is up and running";




	public static final String FileNetClassNotFound = "fileNetClassNotFound";
	
	




}
