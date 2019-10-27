package com.cloud.crypted.server.core.utilities;

import java.io.File;

public final class FileUtilities {
	
	public static boolean directoryExists(String directoryLocation) {
		File file = new File(directoryLocation);
		
		if (file.exists() && file.isDirectory()) {
			return true;
		}
		
		return false;
	}
	
	public static boolean createDirectory(String directoryLocation) {
		if (directoryExists(directoryLocation)) {
			return true;
		}
		
		File file = new File(directoryLocation);
		
		try {
			file.mkdirs();
			
			return true;
		} catch (Exception exception) {
			exception.printStackTrace();
			
			return false;
		}
	}
	
}