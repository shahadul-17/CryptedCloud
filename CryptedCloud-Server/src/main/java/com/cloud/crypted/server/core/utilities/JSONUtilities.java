package com.cloud.crypted.server.core.utilities;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.cloud.crypted.server.Application;

public final class JSONUtilities {
	
	public static Map<String, String> loadJSONData(String filePath) {
		try {
			Map<?, ?> configuration = Application.OBJECT_MAPPER.readValue(new File(filePath), Map.class);
			Set<?> configurationKeySet = configuration.keySet();
			Map<String, String> jsonDataMap = new HashMap<String, String>(configuration.size());
			
			for (Object key : configurationKeySet) {
				if (key instanceof String) {
					Object value = configuration.get(key);
					
					if (value instanceof String) {
						jsonDataMap.put(key.toString(), value.toString());
					}
				}
			}
			
			return jsonDataMap;
		} catch (Exception exception) {
			exception.printStackTrace();
			
			return null;
		}
	}
	
}