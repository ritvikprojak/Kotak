Build runnable jar file with 2. Option (package required libraries into jar)

#Caution : This Document should be referred by Developers Only#
###Prerequisites###
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
	

### Folder Structure ###
{YourBasePath}
└───DBTableMappingActions
	├───ReadMe.md
    ├───config
		├───config.properties
    ├───logs
		├───DBTableMappingActions
    └───runnablejar
		├───HRMappingUtility.bat				(ignore rest bat)
		├───HRMappingUtility.jar 				(ignore rest jar)
		├───AntScriptfor_HRMappingUtility.xml	(ignore rest xml)

###Setting logging path & Resource Bundle(config.properties) path ###

1.
Goto : /DBReplicationProject/src/main/java/log4j.properties
Change -> basePath : {YourBasePath}
Ex : ${basePath}${log}/DBTableMappingActions.log

Logging Path 	: {YourBasePath}HRMS_HRRM_Mapping Utility\DBTableMappingActions\logs\DBTableMappingActions.log
Properties Path : {YourBasePath}HRMS_HRRM_Mapping Utility\DBTableMappingActions\config\config.properties


2.
Goto : /DBReplicationProject/src/main/java/resourceBundlePackage/ResourceBundleClass.java
	unComment & input path String  -> //String basePath= "{{YourBasePath}}"; //reserved for Client Side Base Path


###Setting Environment Configuration###

3. 
Goto : basePath+"HRMS_HRRM_Mapping Utility/DBTableMappingActions/config/config.properties"
Set the following Values:-
#UAT#
#Oracle
#STG_ORA_URI={IPAddress:PORT}
#STG_ORA_DBNAME=NOTSET
#STG_ORA_USERID=NOTSET
#PASSWORD123
#STG_ORA_PASSWORD={Encrypted Password String Only}
#END

Ex :
#Oracle STG ENV
STG_ORA_URI=192.168.1.18:1521
STG_ORA_DBNAME=XEPDB1
STG_ORA_USERID=SYSTEM
#PASSWORD123
STG_ORA_PASSWORD=rlj34bd2FWQnTl9DufFAZQ==



###Setting Class File & SQL & Query Configuration###

4.
Goto : /DBReplicationProject/src/main/java/dDReplication/MasterSlaveDBreplication.java
Find the below sentence in this class & follow as mentioned
	1. Used STGENV_ORAcon Only if  if all 3 Tables (HRMS,HRRM & HRMS_STGENV) are in Same Environment & replace all "ORAcon" by "STGENV_ORAcon"
	2. comment this 2 line unload if all 3 Tables(HRMS,HRRM & HRMS_STGENV) are in Same Environment
	3. sET cOLOUMN FOR HRRM TABLE HERE
		1. PERSON_NUMBER 
		2. RESPONSIBILITY_NAME
	4. sET cOLOUMN FOR HRMS TABLE HERE
		1. CC_CODE
		2. LOC_CODE
		3. EMPLOYEE_NUMBER
		4. LOB1
	5. sET table for UPDATE SQL 
		1. sET UPDATE TABLE NAME HR.HRMS_STGENV
		2. sET COLUMN HR_PERSON_NUMBER  AS PER uPDATE sQL TABLE -> HR.HRMS_STGENV
		3. sET COLUMN EMPLOYEE_NUMBER NAME AS PER uPDATE sQL TABLE -> HR.HRMS_STGENV
	
5. 
Goto : /DBReplicationProject/src/main/java/dBInterfacePackageImpl/ORADatabase.java
	
	Set SCHEMA.TABLE & Columns Name as per Select Query Table 
	1. Change Schema & table Name of HRRM in SELECT QUERY According to Environment
	2. Change Schema & table Name of HRMS in SELECT QUERY According to Environment

5.1
### Build the Jar and Place it at : "HRMS_HRRM_Mapping Utility/DBTableMappingActions/runnable/" ###
### Tip: Build/Export runnable jar file with 2. Option (package required libraries into jar) ###

###Scheduling a Windows Task###

6.
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
		
DONE