package com.cloud.crypted.client;

import java.io.File;

import javax.swing.JOptionPane;

import com.cloud.crypted.client.core.Configuration;
import com.cloud.crypted.client.core.services.CryptedCloudService;
import com.cloud.crypted.client.core.services.TaskExecutor;
import com.cloud.crypted.client.ui.Frame;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Application {
	
	public static volatile boolean running = false;
	public static char[] passphrase = null;
	
	private static final String APPLICATION_DATA_DIRECTORY = "." + File.separator + "application-data" + File.separator;
	
	public static TaskExecutor taskExecutor = null;
	
	public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	public static final CryptedCloudService CRYPTED_CLOUD_SERVICE = new CryptedCloudService();
	
	public static void main(String[] args) {
		if (!Configuration.loadConfiguration(APPLICATION_DATA_DIRECTORY + "configuration.json")) {
			JOptionPane.showMessageDialog(null, "Failed to load configuration.", "Error", JOptionPane.ERROR_MESSAGE);
			
			return;
		}
		
		running = true;
		
		taskExecutor = new TaskExecutor();
		taskExecutor.start();
		
		Frame frame = null;
		
		try {
			frame = new Frame();
		} catch (Exception exception) {
			running = false;
			
			exception.printStackTrace();
			JOptionPane.showMessageDialog(null, "An error occurred while loading the UI.", "Error", JOptionPane.ERROR_MESSAGE);
			
			return;
		}
		
		frame.setVisible(true);
	}
	
}