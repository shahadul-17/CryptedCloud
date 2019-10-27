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

import com.cloud.crypted.server.core.Configuration;

public final class MailUtilities {
	
	public static boolean sendMail(String recipientEmail, String subject, String mailBody) {
		Properties properties = new Properties();
		properties.put("mail.smtp.host", Configuration.get("mail.smtp.host"));
		properties.put("mail.smtp.port", Integer.parseInt(Configuration.get("mail.smtp.port")));
		properties.put("mail.smtp.auth", Configuration.get("mail.smtp.auth"));
		properties.put("mail.smtp.starttls.enable", Configuration.get("mail.smtp.starttls.enable"));
		
		Session session = Session.getInstance(properties, new Authenticator() {
			
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(
					Configuration.get("mail.senderEmail"),
					Configuration.get("mail.senderPassword")
				);
			}
		});
		
		Message message = new MimeMessage(session);
		
		try {
			message.setFrom(new InternetAddress(Configuration.get("mail.senderEmail"),
					Configuration.get("mail.senderName")));
			message.setRecipient(RecipientType.TO, new InternetAddress(recipientEmail));
			message.setSubject(subject);
            message.setText(mailBody);
            
            Transport.send(message);
            
            return true;
		} catch (Exception exception) {
			exception.printStackTrace();
			
			return false;
		}
	}
	
}