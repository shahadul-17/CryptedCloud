package com.cloud.crypted.server;

import java.io.File;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.cloud.crypted.server.core.DynamicResources;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootApplication
public class Application {
	
	public static final String DYNAMIC_RESOURCES_DIRECTORY_PATH = "." + File.separator + "dynamic-resources" + File.separator;
	public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	
	public static void main(String[] args) {
		/*
		 * 
		 * we could have placed this piece of code within a static block
		 * in DynamicResources.java class. but we are loading dynamic resources
		 * in main method because, we want to make sure that the application
		 * is not launched if failed...
		 * 
		 */
		if (!DynamicResources.load(DYNAMIC_RESOURCES_DIRECTORY_PATH)) {
			System.err.println("An error occurred while loading dynamic resources.");
			
			return;
		}
		
		SpringApplication.run(Application.class, args);
	}
	
}