package com.cloud.crypted.client.core.utilities;

import java.io.File;

public final class FileUtilities {
	
	public static boolean createDirectory(String directoryLocation) {
		File file = new File(directoryLocation);
		
		if (file.exists() && file.isDirectory()) {
			return true;
		}
		
		try {
			file.mkdirs();
			
			return true;
		} catch (Exception exception) {
			exception.printStackTrace();
			
			return false;
		}
	}
	
}