package com.cloud.crypted.server.core.utilities;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.cloud.crypted.server.core.DynamicResources;

public final class MailUtilities {
	
	public static boolean sendMail(String recipientEmail, String subject, String mailBody) {
		Properties properties = new Properties();
		properties.put("mail.smtp.host", DynamicResources.getConfiguration("mail.smtp.host"));
		properties.put("mail.smtp.port", (int) DynamicResources.getConfiguration("mail.smtp.port"));
		properties.put("mail.smtp.auth", DynamicResources.getConfiguration("mail.smtp.auth"));
		properties.put("mail.smtp.starttls.enable", DynamicResources.getConfiguration("mail.smtp.starttls.enable"));
		
		Session session = Session.getInstance(properties, new Authenticator() {
			
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(
					(String) DynamicResources.getConfiguration("mail.senderEmail"),
					(String) DynamicResources.getConfiguration("mail.senderPassword")
				);
			}
		});
		
		Message message = new MimeMessage(session);
		
		try {
			message.setFrom(new InternetAddress((String) DynamicResources.getConfiguration("mail.senderEmail"),
					(String) DynamicResources.getConfiguration("mail.senderName")));
			message.setRecipient(RecipientType.TO, new InternetAddress(recipientEmail));
			message.setSubject(subject);
            message.setContent(mailBody, "text/html; charset=utf-8");
            
            Transport.send(message);
            
            return true;
		} catch (Exception exception) {
			exception.printStackTrace();
			
			return false;
		}
	}
	
}