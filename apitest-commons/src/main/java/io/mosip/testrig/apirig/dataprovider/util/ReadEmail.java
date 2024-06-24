package io.mosip.testrig.apirig.dataprovider.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMultipart;

import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReadEmail {
	private static final Logger logger = LoggerFactory.getLogger(ReadEmail.class);
	public static String FROM_MATH ="info@mosip.io";
	public static String FROM_MATH1 ="Alok Tiwari <alok.tiwari@technoforte.co.in>";
	public static String gmailPOPHost = "pop.gmail.com";
	static String mailStoreType = "pop3";  
	static String username= "sanath.test.mosip@gmail.com";  
	static String password= "";//change accordingly  
	static String regexpattern = "\\d+";
	public static String messageSubject="Requesting the additional details for progressing on the application of UIN";
	
	public static List<String> getOtps(){
		List<String> otps = new ArrayList<String>();
		
		List<String> mails =  receiveEmail(gmailPOPHost, mailStoreType, username, password);  
		for(String s: mails) {
		
			 Pattern pattern = Pattern.compile(regexpattern);
			 Matcher matcher = pattern.matcher(s);
			 if (matcher.find())
			 {
				 logger.info(matcher.group());
				 otps.add(matcher.group());
			 }
		 }
		return otps;
	}
	
	public static List<String> getadditionalInfoReqIds(){
		List<String> additionalInfoReqIds = new ArrayList<String>();
		gmailPOPHost = "pop.gmail.com";
		mailStoreType = "pop3";  
		username= "alok1.test.mosip@gmail.com";  
		password= "";//change accordingly  
		String keyWord = "AdditionalInfoRequestId";
		
		List<String> mails = receiveEmail(gmailPOPHost, mailStoreType, username, password);
		for (String s : mails) {
			int position = s.indexOf(keyWord);
			String additionalInfoReqId = s.substring(position).split(" ")[1];
			logger.info(additionalInfoReqId);
			additionalInfoReqIds.add(additionalInfoReqId);
		}
		return additionalInfoReqIds;
	}
	
	 public static List<String> receiveEmail(String pop3Host, String storeType,  
	  String user, String password) {  

			List<String> mailMessage = new ArrayList<String>();
			try {
				// 1) get the session object
				Properties properties = new Properties();
				properties.put("mail.pop3.host", pop3Host);
				properties.put("mail.pop3.port", "995");
				properties.put("mail.pop3.starttls.enable", "true");
				properties.put("mail.pop3.ssl.enable", "true");

				Session emailSession = Session.getInstance(properties);

				

				// 2) create the POP3 store object and connect with the pop server
				Store emailStore = emailSession.getStore(storeType);
				emailStore.connect(user, password);

				// 3) create the folder object and open it
				Folder emailFolder = emailStore.getFolder("INBOX");
				emailFolder.open(Folder.READ_WRITE);

				// 4) retrieve the messages from the folder in an array and print it
				Message[] messages = emailFolder.getMessages();
				for (int i = 0; i < messages.length; i++) {
					Message message = messages[i];
					if (!message.getSubject().toLowerCase().contains("uin"))
						continue;
					logger.info("---------------------------------");
					logger.info("Email Number {}" , (i + 1));
					logger.info("Subject: {}" , message.getSubject());
					logger.info("From: {}" , message.getFrom()[0]);
					MimeMultipart content = (MimeMultipart) message.getContent();
					String bodyMsg = getTextFromMimeMultipart(content);
					
					if (message.getFrom()[0].toString().toLowerCase().contains("mosip")) {
						mailMessage.add(bodyMsg);
						message.setFlag(Flags.Flag.DELETED, true);
					}

					// 5) close the store and folder objects
					emailFolder.close(false);
					emailStore.close();
				}
			} catch (MessagingException | IOException e) {
				logger.error(e.getMessage());
			}

			return mailMessage;
		}
	  
	 public static void main(String[] args) {  
		 getadditionalInfoReqIds();
		 List<String> otps = getOtps();
		 for(String ss: otps) {
			 logger.info(ss);
		 }	  
	 }  
	 private static String getTextFromMimeMultipart( MimeMultipart mimeMultipart)  throws MessagingException, IOException{
		 String result = "";
		 int count = mimeMultipart.getCount();
		 for (int i = 0; i < count; i++) {
		        BodyPart bodyPart = mimeMultipart.getBodyPart(i);
		        if (bodyPart.isMimeType("text/plain")) {
		            result = result + "\n" + bodyPart.getContent();
		            break; // without break same text appears twice in my tests
		        } else if (bodyPart.isMimeType("text/html")) {
		            String html = (String) bodyPart.getContent();
		            result = result + "\n" + Jsoup.parse(html).text();
		        } else if (bodyPart.getContent() instanceof MimeMultipart){
		            result = result + getTextFromMimeMultipart((MimeMultipart)bodyPart.getContent());
		        }
		 }
		 return result;
	}

}
