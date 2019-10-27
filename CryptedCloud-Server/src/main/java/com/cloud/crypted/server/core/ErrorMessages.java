package com.cloud.crypted.server.core;

import java.util.Map;

import com.cloud.crypted.server.core.utilities.JSONUtilities;

public class ErrorMessages {
	
	private static Map<String, String> errorMessages = null;
	
	public static boolean load(String errorMessagesFilePath) {
		errorMessages = JSONUtilities.loadJSONData(errorMessagesFilePath);
		
		return errorMessages != null;
	}
	
	public static String get(String errorMessageKey) {
		return errorMessages.get(errorMessageKey);
	}
	
}