package com.cloud.crypted.server.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.cloud.crypted.server.Application;

public class DynamicResources {
	
	private static Map<String, String> emailTemplates = null;
	private static Map<?, ?> configuration = null;
	private static Map<?, ?> errorMessages = null;
	
	private static String readEmailTemplate(File emailTemplateFile) throws Exception {
		InputStream inputStream = new FileInputStream(emailTemplateFile);
		
		byte[] buffer = new byte[(int) getConfiguration("stream.bufferLength")];
		int bytesRead = 0;
		
		StringBuilder emailTemplateBuilder = new StringBuilder((int) getConfiguration("collection.initialCapacity"));
		
		while ((bytesRead = inputStream.read(buffer, 0, buffer.length)) != -1) {
			emailTemplateBuilder.append(new String(buffer, 0, bytesRead));
		}
		
		inputStream.close();
		
		return emailTemplateBuilder.toString();
	}
	
	private static Map<String, String> readEmailTemplates(String emailTemplatesDirectoryPath) throws Exception {
		File emailTemplatesDirectory = new File(emailTemplatesDirectoryPath);
		
		if (emailTemplatesDirectory.exists() && emailTemplatesDirectory.isDirectory()) {
			File[] emailTemplateFiles = emailTemplatesDirectory.listFiles();
			Map<String, String> emailTemplates = new HashMap<String, String>((int) getConfiguration("collection.initialCapacity"));
			
			for (int i = 0; i < emailTemplateFiles.length; i++) {
				String emailTemplateName = emailTemplateFiles[i].getName();
				
				if (emailTemplateFiles[i].exists() && emailTemplateFiles[i].isFile() &&
						emailTemplateName.endsWith(".html")) {
					emailTemplateName = emailTemplateName.substring(0, emailTemplateName.lastIndexOf(".html"));
					
					emailTemplates.put(emailTemplateName, readEmailTemplate(emailTemplateFiles[i]));
				}
			}
			
			return emailTemplates;
		} else {
			throw new Exception("Email templates directory does not exist.");
		}
	}
	
	public static boolean load(String dynamicResourcesDirectoryPath) {
		try {
			configuration = Application.OBJECT_MAPPER.readValue(new File(dynamicResourcesDirectoryPath + "configuration.json"), Map.class);
			errorMessages = Application.OBJECT_MAPPER.readValue(new File(dynamicResourcesDirectoryPath + "errorMessages.json"), Map.class);
			emailTemplates = readEmailTemplates(dynamicResourcesDirectoryPath + "email-templates");
		} catch (Exception exception) {
			exception.printStackTrace();
			
			return false;
		}
		
		return true;
	}
	
	public static String getEmailTemplate(String emailTemplateKey) {
		return emailTemplates.get(emailTemplateKey);
	}
	
	public static Object getConfiguration(String configurationKey) {
		return configuration.get(configurationKey);
	}
	
	public static String getErrorMessage(String errorMessageKey) {
		return (String) errorMessages.get(errorMessageKey);
	}
	
}