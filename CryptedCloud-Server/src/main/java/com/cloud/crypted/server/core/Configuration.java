package com.cloud.crypted.server.core;

import java.util.Map;

import com.cloud.crypted.server.core.utilities.JSONUtilities;

public class Configuration {
	
	private static Map<String, String> configuration = null;
	
	public static boolean load(String configurationFilePath) {
		configuration = JSONUtilities.loadJSONData(configurationFilePath);
		
		return configuration != null;
	}
	
	public static String get(String configurationKey) {
		return configuration.get(configurationKey);
	}
	
}