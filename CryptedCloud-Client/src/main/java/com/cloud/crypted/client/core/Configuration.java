package com.cloud.crypted.client.core;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.cloud.crypted.client.Application;

public class Configuration {
	
	private static Map<String, String> configuration = null;
	
	public static boolean loadConfiguration(String configurationFilePath) {
		if (configuration != null) {
			return true;
		}
		
		try {
			Map<?, ?> configuration = Application.OBJECT_MAPPER.readValue(new File(configurationFilePath), Map.class);
			Set<?> configurationKeySet = configuration.keySet();
			Configuration.configuration = new HashMap<String, String>(configuration.size());
			
			for (Object key : configurationKeySet) {
				if (key instanceof String) {
					Object value = configuration.get(key);
					
					if (value instanceof String) {
						Configuration.configuration.put(key.toString(), value.toString());
					}
				}
			}
			
			return true;
		} catch (Exception exception) {
			exception.printStackTrace();
			
			return false;
		}
	}
	
	public static String get(String configurationKey) {
		return configuration.get(configurationKey);
	}
	
}