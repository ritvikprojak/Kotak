
#Caution : This Document should be referred by Developers Only#
##Prerequisites##
1. Decide & Set the location of "HRMS_HRRM_Mapping Utility" folder. This location is further referred as  : {YourBasePath}
	Ex: C:/UR/BasePath/{Place "HRMS_HRRM_Mapping Utility" folder here}
		YourBasePath = "C:/UR/BasePath/"

Use 'DESC SHCHEMA.TABLENAME'
2. For HRMS & HRRM table
	1. SHCHEMA.TABLENAME
	2. EQUIVALENT COLOUMN NAME IN QUERY : SELECT EMPLOYEE_NUMBER, LOB_CODE, CC_CODE,LOC_CODE FROM HR.HRMS
	3. EQUIVALENT COLOUMN NAME IN QUERY : SELECT PERSON_NUMBER, RESPONSIBILITY_NAME FROM HR.HRRM

3. For HRMS_HRRM_Mapping table
	1. SHCHEMA.TABLENAME
	2. EQUIVALENT COLOUMN NAME IN SQL : UPDATE HR.HRMS_HRRM SET HR_PERSON_NUMBER={HR.HRRM.PERSON_NUMBER.VALUE} WHERE EMPLOYEE_NUMBER ={HR.HRMS.EMPLOYEE_NUMBER.VALUE}
	

##Folder Structure ##
{YourBasePath}
└───DBTableMappingActions
	├───ReadMe2.md [Developer's Guide for Getting Started]
    ├───config
		├───config.properties [Environment Setting]
		├───sessionConfigPath.properties[Records the Incidence of Exception in Last Session ]
    ├───logs
		├───DBTableMappingActions
    └───runnablejar
		├───HRMappingUtility.bat				(ignore rest of the .bat)
		├───HRMappingUtility.jar 				(ignore rest of the .jar)
		├───AntScript.xml	(ignore rest of the .xml)

##Setting logging path & Resource Bundle(config.properties) path ##

#1.
Goto : /DBReplicationProject/src/main/java/log4j.properties
Change -> basePath : {YourBasePath}
Ex : ${basePath}${log}/DBTableMappingActions.log

Logging Path 	: {YourBasePath}HRMS_HRRM_Mapping Utility\DBTableMappingActions\logs\DBTableMappingActions.log
Properties Path : {YourBasePath}HRMS_HRRM_Mapping Utility\DBTableMappingActions\config\config.properties


#2.
Goto : /DBReplicationProject/src/main/java/resourceBundlePackage/ResourceBundleClass.java
	unComment & input path String  -> //String basePath= "{{YourBasePath}}"; //reserved for Client Side Base Path


##Setting Environment Configuration##

#3. 

## Setting  SQL & Query Configuration in config.properties ##


###Oracle SERVER ENV [Used for Firing UPADATE ,TRUNCATE, ALTER DDL on HRMS_HRRM Table]
ORA_URI=192.168.1.105:1521
ORA_DBNAME=XEPDB1
ORA_USERID=SYSTEM
#Filenet8
ORA_PASSWORD=rlj34bd2FWQnTl9DufFAZQ==

###Oracle STG ENV [Used for Firing SELECT Query on HRMS & HRRM Table]
STG_ORA_URI=192.168.1.105:1521
STG_ORA_DBNAME=XEPDB1
STG_ORA_USERID=SYSTEM
#Filenet8
STG_ORA_PASSWORD=rlj34bd2FWQnTl9DufFAZQ==

#END

#4.

# *******************COMMON CONFIG ACROSS ALL ENVIRONMENT*******************

#Table Name for CHRRM
cHRRM_TableName=HR.CUST_ROLES_USR_MAPPING
eMPLOYEE_NUMBER_CHRRM = PERSON_NUMBER
lOB_CODE_CHRRM = LOB_CODE
lOC_CODE_CHRMS = LOC_CODE
cC_CODE_CHRRM = CC_CODE
rESPONSIBILITY_NAME_CHRRM = RESPONSIBILITY_NAME
dOMAIN_LOGIN_ID_CHRRM = DOMAIN_ID
rOLE_CHRRM = ROLE_NAME

#Columns to be Fetched from HRRM ResultSet
hRRMTableName = HR.HRRM
pERSON_NUMBER_HRRM = PERSON_NUMBER
lOB_CODE_HRRM = NEW_LOB
lOC_CODE_HRRM = NEW_LOC
cC_CODE_HRRM = NEW_CC
rESPONSIBILITY_NAME_HRRM = RESPONSIBILITY_NAME

#Columns to be Fetched From HRMS ResultSet
hRMSTableName = HR.HRMS
eMPLOYEE_NUMBER_HRMS = EMPLOYEE_NUMBER
lOB_CODE_HRMS = LOB_CODE
lOC_CODE_HRMS = LOC_CODE
cC_CODE_HRMS = CC_CODE

#Table & Columns Name From HRMS_HRRM for UPDATE DML
hRMS_HRRMTableName = HR.HRMS_HRRM2
hRMS_HRRM_PERSON_NUMBERColumnName = HR_PERSON_NUMBER
hRMS_HRRM_EMPLOYEE_NUMBERColumnName = EMPLOYEE_NUMBER
hRMS_HRRM_ROLE = ROLE
hRMS_HRRM_DOMAIN_LOGIN_ID = DOMAIN_LOGIN_ID

#HRMS_ROLES View Name & Columns Name
HRMS_ROLESViewName = hr.HRMS_ROLES_view
v_EMPLOYEE_NUMBER = EMPLOYEE_NUMBER
v_DOMAIN_LOGIN_ID = DOMAIN_LOGIN_ID
v_ROLE = ROLE
v_LAST_WORKING_DATE = LAST_WORKING_DATE
v_ROLENAME = ROLENAME
v_IS_CUSTOM_ROLE = IS_CUSTOM_ROLE
v_END_DATE = END_DATE
v_COPY_ACCESS = COPY_ACCESS
v_CREATE_ACCESS = CREATE_ACCESS
v_DELETE_ACCESS = DELETE_ACCESS
v_MODIFY_ACCESS = MODIFY_ACCESS
v_PRINT_ACCESS = PRINT_ACCESS
v_READ_ACCESS = READ_ACCESS

#END


5.

#Build the Jar and Place it at : "{U/R/BaseFolder}/HRMS_HRRM_Mapping Utility/DBTableMappingActions/runnable/" ###

###Tip: Build/Export runnable jar file with 2. Option (package required libraries into jar) ###
###Important: Name Output Jar File as "HRMappingUtility.jar" [Naming this wrong .will make .BAT File to Go Wrong]


#Scheduling a Windows Task#
6.
Prerequisite : Copy the Path of HRMappingUtility.bat 
Goto: Start -> Search for "Task Scheduler" & Open it.

	Right Click on "Task Scheduler Library" & Click "Enable All Task History"
	Right Click on "Task Scheduler Library" & Click "Create Basic Task..."
		1. Name : 		HRMS_HRRM_MappingUtility
		2. Description : "{YourBasePath}HRMS_HRRM_Mapping Utility\DBTableMappingActions\runnablejar\HRMappingUtility.bat"
		Next
		
		3. When do you want to the task to start? : Select "Daily"
		Next
		4. Start {Select Day} {Select Time to Start}
		5. Recur every : {1} days
		Next
		
		6. What action do you want the task to perform? : Select "Start a program"
		Next
		7. Program/script: "{YourBasePath}HRMS_HRRM_Mapping Utility\DBTableMappingActions\runnablejar\HRMappingUtility.bat"
		Next
		8. Check the box : "Open the properties dialog when I click finish" 
		9. Click Finish.
		
		10. In General tab -> Security options
			Select Radio Button : "Run whether user is logged on or not"
			Check Box 			: "Run with highest privileges"
			
		11. (Optional)For Fine-tuning Task, Explore tab :Triggers | Actions | Conditions | Settings | History
		12. Click OK
		
#DONE