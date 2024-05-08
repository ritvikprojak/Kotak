package emailSMTPPackage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import dDReplication.MasterSlaveDBreplication;

/**
 * @author Sanket
 * 
 */

public class DefaultSMTP {

	static Properties mailServerProperties;
	static Session getMailSession;
	static MimeMessage generateMailMessage;

	// Main Method
	public static void main(String args[]) throws AddressException, MessagingException {
		generateAndSendEmail("SampleException");
		System.out.println("\n\n ===> Your Java Program has just sent an Email successfully. Check your email..");
	}

	public static void generateAndSendEmail(String exceptionType) throws AddressException, MessagingException {

		ResourceBundle rBProps = MasterSlaveDBreplication.rBProps;
		String toList = rBProps.getString("toList");
		String emailGreetingStr = rBProps.getString("emailGreetingStr");// "Dear Admin,";
		String emailBodyString = rBProps.getString("emailBodyString"); // "Kindly Check the Logs";
		String emailSignature = rBProps.getString("emailSignature"); // "HRMS HRM DB Utility";
		String fromEmailHostServer = rBProps.getString("fromEmailHostServer"); // "10.10.19.56";
		String fromEmailHostPort = rBProps.getString("fromEmailHostPort"); // "25";
		String fromEmailId = rBProps.getString("fromEmailId");// "noreply_dms@kotak.com";

		// Step1
		System.out.println("\n 1st ===> setup Mail Server Properties..");
		mailServerProperties = System.getProperties();
		mailServerProperties.put("mail.smtp.host", fromEmailHostServer);
		mailServerProperties.put("mail.smtp.port", fromEmailHostPort);
		mailServerProperties.put("mail.smtp.auth", "false");
		mailServerProperties.put("mail.smtp.starttls.enable", "false");
		mailServerProperties.put("mail.smtp.connectiontimeout", "10000");
		mailServerProperties.put("mail.smtp.timeout", "10000");

		System.out.println("Mail Server Properties have been setup successfully..");

		// Step2
		System.out.println("\n\n 2nd ===> get Mail Session..");
		getMailSession = Session.getDefaultInstance(mailServerProperties, null);
		getMailSession.setDebug(true);
		generateMailMessage = new MimeMessage(getMailSession);
		// generateMailMessage.addRecipient(Message.RecipientType.TO, new
		// InternetAddress("sanket.gawali@kotak.com"));
		generateMailMessage.setSender(new InternetAddress(fromEmailId));
		System.out.println("generateMailMessage.getSender().toString() :" + generateMailMessage.getSender().toString());
		System.out.println("generateMailMessage.getSender().toString() :" + generateMailMessage.getSender().toString());
		generateMailMessage.setSubject(exceptionType + " has occured in HRMS HRRM DB Utility");

		String emailBody = emailGreetingStr + "<br><br>" + "<font size='5'>" + "<b>" + exceptionType
				+ "</b> has occured at <b>"
				+ DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now())
				+ "</b>, while running HRMS HRRM DB Utility.</b><br><br>" + "</font>" + "<b>" + emailBodyString + "</b>"
				+ "<br><br> Regards, <br>" + emailSignature + "<br><br><i>This is a System Generated Mail</i>";

		Multipart multipart = new MimeMultipart();
// Adding Email Body
		BodyPart messageBodyPart = new MimeBodyPart();
		messageBodyPart.setContent(emailBody, "text/html");
		multipart.addBodyPart(messageBodyPart);
//System.out.println("Mail Session has been created successfully..");

//Adding Attachment
		MimeBodyPart mimeBodyPart = new MimeBodyPart();
		String logFileName = System.getProperty("current.date.time") + "_DBTableMappingActions.log";
		String basePath = "D:/DB_UTILITY/"; // reserved for Client Side Base Path
//String configPath = "/fs1/IBM/ICNAutoActions/config/config.properties"; // linux OS path example
		String filename = basePath + "HRMS_HRRM_Mapping Utility/DBTableMappingActions/logs/" + logFileName;
		DataSource source = new FileDataSource(filename);
		mimeBodyPart.setDataHandler(new DataHandler(source));
		mimeBodyPart.setFileName(logFileName);

		boolean flag = false;
		try {
			multipart.addBodyPart(mimeBodyPart);
		} catch (Exception e) {
			flag = true;
		}
		if (flag) {
			generateMailMessage.setContent(emailBody, "text/html"); // adds only Email Body, if Adding Log File Fails
		} else {
			generateMailMessage.setContent(multipart);// adds Logs File as Attachment, Along with Email Body
		}

		generateMailMessage.setContent(emailBody, "text/html");
		System.out.println("Mail Session has been created successfully..");
		// Transport.send(generateMailMessage);

		// Step3
		System.out.println("\n\n 3rd ===> Get Session and Send mail");
		Transport transport = getMailSession.getTransport("smtp");

		// Enter your correct gmail UserID and Password
		// if you have 2FA enabled then provide App Specific Password
		// transport.connect("SANKET-PC", "<----- Your GMAIL ID ----->", "<----- Your
		// GMAIL PASSWORD ----->");
		transport.connect();

		try {
			// generateMailMessage.saveChanges();
			// Transport.send(generateMailMessage);
			// transport.sendMessage(generateMailMessage,
			// generateMailMessage.getAllRecipients());
			Address[] addressArray = { new InternetAddress(toList) };
			transport.sendMessage(generateMailMessage, addressArray);
		} catch (Exception e) { // SMTPSendFailed
			e.printStackTrace();
		} finally {
			// transport.close();
		}
	}
}