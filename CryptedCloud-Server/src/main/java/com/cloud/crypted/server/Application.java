package com.cloud.crypted.server;

import java.io.File;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.cloud.crypted.server.core.Configuration;
import com.cloud.crypted.server.core.ErrorMessages;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootApplication
public class Application {
	
	public static final String APPLICATION_DATA_DIRECTORY = "." + File.separator + "application-data" + File.separator;
	public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	
	public static void main(String[] args) {
		/*
		 * 
		 * we could have placed this piece of code within a static block
		 * in Configuration.java class. but we are loading configuration
		 * in main method because, we want to make sure that the application
		 * is not launched if failed...
		 * 
		 */
		if (!Configuration.load(APPLICATION_DATA_DIRECTORY + "configuration.json")) {
			System.err.println("An error occurred while loading configuration.");
			
			return;
		}
		
		if (!ErrorMessages.load(APPLICATION_DATA_DIRECTORY + "errorMessages.json")) {
			System.err.println("An error occurred while loading error messages.");
			
			return;
		}
		
		SpringApplication.run(Application.class, args);
	}
	
}