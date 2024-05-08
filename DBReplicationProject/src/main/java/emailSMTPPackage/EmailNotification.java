package emailSMTPPackage;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;
import javax.mail.internet.MimeMultipart;

import org.apache.log4j.Logger;

//import cripUtilityPackage.CripUtility;
import dDReplication.MasterSlaveDBreplication;


public class EmailNotification {
	
	final static Logger log = Logger.getLogger(EmailNotification.class.getName());
	static Properties mailServerProperties;
	static Session getMailSession;
	static MimeMessage generateMailMessage;
	//ResourceBundleClass rBC = new ResourceBundleClass();
	//final ResourceBundle resourceBundle = rBC.getResourceBundle();
	ResourceBundle rBProps = MasterSlaveDBreplication.rBProps;
	String toList = rBProps.getString("toList") ;
	//String toList2 = rBProps.getString("toList2") ;
	String cCList = rBProps.getString("cCList") ;
	//String subjectStr = resourceBundle.getString("emailGreetingStr"); // Subject is Fetched Dynamically
	String emailGreetingStr = rBProps.getString("emailGreetingStr");// "Dear Admin,";
	String emailBodyString = rBProps.getString("emailBodyString"); //"Kindly Check the Logs";
	String emailSignature = rBProps.getString("emailSignature"); // "HRMS HRM DB Utility";
	String fromEmailHostServer = rBProps.getString("fromEmailHostServer"); //"10.10.19.56";
	String fromEmailHostPort = rBProps.getString("fromEmailHostPort"); // "25";
	String fromEmailId = rBProps.getString("fromEmailId");//"noreply_dms@kotak.com";
	String environment = rBProps.getString("Environment");
	//String fromEmailPassword = rBProps.getString("fromEmailPassword");// Default Id Do Not Require Password
	
	
	public String  generateAndSendEmail(String exceptionType) throws AddressException, MessagingException {
	//Step1
	//System.out.println("\n 1st ===> setup Mail Server Properties..");
		boolean isSuccess = false;
	try{
	mailServerProperties = System.getProperties();
	mailServerProperties.put("mail.smtp.host", fromEmailHostServer);
	mailServerProperties.put("mail.smtp.port", fromEmailHostPort); 
	mailServerProperties.put("mail.smtp.auth", "false");
	mailServerProperties.put("mail.smtp.starttls.enable", "false"); // by Default Value is False
	mailServerProperties.put("mail.smtp.connectiontimeout", "10000");
	mailServerProperties.put("mail.smtp.timeout", "10000");
	//System.out.println("Mail Server Properties have been setup successfully..");

	// Step2
	//System.out.println("\n\n 2nd ===> get Mail Session..");
	getMailSession = Session.getDefaultInstance(mailServerProperties, null);
	//getMailSession.setDebug(true); // Uncomment This While Debugging Email Related Issues
	try{
		generateMailMessage = new MimeMessage(getMailSession);
		//generateMailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(toList));
		generateMailMessage.setSender(new InternetAddress(fromEmailId));
	}
	catch(NullPointerException e){
		System.out.println("Caught Null Pointer");
		e.printStackTrace();
		System.out.println(e.fillInStackTrace());
	}
	//generateMailMessage = new MimeMessage(getMailSession);
	// For Customized Subject from config.properties File Use variable String : subjectStr
	
	
	String emailBody = null;
	
	if(exceptionType == "Success"){
		generateMailMessage.setSubject(environment+" : "+exceptionType+" in HRMS HRRM DB Utility execution"); 
		
		emailBody = 
							emailGreetingStr+"<br><br>"+
							"<font size='5'>"+
							"<b>"+exceptionType+"</b> on HRMS HRRM DB Utility Execution at <b>"+
							DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now())+"</b></font>"+
							"<br><br> Regards, <br>"+emailSignature+
							"<br><br><i>This is a System Generated Mail</i>";
		isSuccess = true;
		
		
	}else{
		generateMailMessage.setSubject(environment+" : "+exceptionType+" has occured in HRMS HRRM DB Utility"); 
		
		emailBody = 
							emailGreetingStr+"<br><br>"+
							"<font size='5'>"+
							"<b>"+exceptionType+"</b> has occured at <b>"+
							DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now())+
							"</b>, while running HRMS HRRM DB Utility.</b><br><br>"+
							"</font>"+
							"<b>"+emailBodyString+"</b>"+
							"<br><br> Regards, <br>"+emailSignature+
							"<br><br><i>This is a System Generated Mail</i>";
		
		
	}
	
	
	Multipart multipart = new MimeMultipart();
	// Adding Email Body
	BodyPart messageBodyPart = new MimeBodyPart();
    messageBodyPart.setContent(emailBody, "text/html");
    multipart.addBodyPart(messageBodyPart);
	//System.out.println("Mail Session has been created successfully..");
	
    //Adding Attachment
	MimeBodyPart mimeBodyPart = new MimeBodyPart();
	String logFileName = System.getProperty("current.date.time")+"_DBTableMappingActions.log";
	String basePath= "D:/DB_UTILITY/"; //reserved for Client Side Base Path
	//String configPath = "/fs1/IBM/ICNAutoActions/config/config.properties"; // linux OS path example
	String filename = basePath+"HRMS_HRRM_Mapping Utility/DBTableMappingActions/logs/"+logFileName;
	boolean flag = false;
	try{
		
	File f = new File(filename);
	if (f.exists() && !f.isDirectory())
	{
	DataSource source = new FileDataSource(filename);
	mimeBodyPart.setDataHandler(new DataHandler(source));
	mimeBodyPart.setFileName(logFileName);
	multipart.addBodyPart(mimeBodyPart); 
	}
	
	
	else{
		flag  = true ;
		}
			
	}
	
	catch(Exception e){
		flag  = true ;}
	
	
	if(flag){
		generateMailMessage.setContent(emailBody, "text/html"); // adds only Email Body, if Adding Log File Fails
	}else{
		generateMailMessage.setContent(multipart);// adds Logs File as Attachment, Along with Email Body
	}
	
	// Step3
	//System.out.println("\n\n 3rd ===> Get Session and Send mail");
	Transport transport = getMailSession.getTransport("smtp");
	transport.connect();
	//Address[] addressArray =  {new InternetAddress(toList),new InternetAddress(toList2)};
//	Address[] addressArray =  {new InternetAddress(toList)};
	Address[] addressArray = InternetAddress.parse(toList);
	Address[] ccAddressArray = InternetAddress.parse(cCList);
	generateMailMessage.setRecipients(RecipientType.TO, addressArray);
	generateMailMessage.setRecipients(RecipientType.CC, ccAddressArray);
	transport.sendMessage(generateMailMessage, generateMailMessage.getAllRecipients());
	transport.close();
	}
	catch(Exception ex){
		log.error(ex.fillInStackTrace());
		log.error("Error occuered while sending email.");
	}
	String isMailSentMsg = null;
	if(isSuccess){
		isMailSentMsg = "Success Email Sent To: "+toList+".";
	}else{
		isMailSentMsg = "Exception occurrence email notification has been sent To: "+toList+".";
	}
	return isMailSentMsg;
	}
	
///*	
//  public static void main(String[] args) throws InterruptedException, AddressException, MessagingException {
//		String isMailSentMsg = new EmailNotification().generateAndSendEmail("Success");
//		//System.out.println("\n\n ===> Your Java Program has just sent an Email successfully. Check your email..");
//		System.out.println(isMailSentMsg);
//	} //Main
}//Class

//*/
/*
 If you connect using SSL or TLS, you can send mail to anyone with smtp.gmail.com. 

Note: Before you start the configuration,we recommend you set up App passwords for the the desired account.
Learn more at Sign in using App Passwords and Manage a user's security settings. 

Connect to smtp.gmail.com on port 465,if you�re using SSL. (Connect on port 587 if you�re using TLS.)
Sign in with a Google username and password for authentication to connect with SSL or TLS.
Ensure that the username you use has cleared the CAPTCHA word verification test that appears when you first sign in.
 */

/*
javax.mail.Session class
javax.mail.Message class
javax.mail.internet.MimeMessage class
javax.mail.Address class
javax.mail.internet.InternetAddress class
javax.mail.Authenticator class
javax.mail.PasswordAuthentication class
javax.mail.Transport class
javax.mail.Store class
javax.mail.Folder class  
 */

 