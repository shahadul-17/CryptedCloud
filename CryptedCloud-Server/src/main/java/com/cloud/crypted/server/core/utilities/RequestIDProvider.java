package com.cloud.crypted.server.core.utilities;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import com.cloud.crypted.server.core.DynamicResources;

public class RequestIDProvider {
	
	private static final Random RANDOM = new Random(System.currentTimeMillis());
	
	private static final Set<String> REQUEST_IDs = new HashSet<String>(
			(int) DynamicResources.getConfiguration("collection.initialCapacity"));
	
	private static int generateRandomIntegerInRange(int lowerBound, int upperBound) {
		return RANDOM.nextInt((upperBound - lowerBound) + 1) + lowerBound;
	}
	
	public static synchronized String generateID() {
		String requestID = null;
		
		do {
			int length = generateRandomIntegerInRange((int) DynamicResources.getConfiguration("requestID.length.minimum"),
					(int) DynamicResources.getConfiguration("requestID.length.maximum"));
			char[] randomCharacters = new char[length];
			
			for (int i = 0; i < length; i++) {
				randomCharacters[i] = (char) generateRandomIntegerInRange((int) DynamicResources.getConfiguration("requestID.character.lowerBound"),
						(int) DynamicResources.getConfiguration("requestID.character.upperBound"));
			}
			
			requestID = new String(randomCharacters);
		} while (REQUEST_IDs.contains(requestID));
		
		REQUEST_IDs.add(requestID);
		
		return requestID;
	}
	
	public static synchronized boolean contains(String requestID) {
		return REQUEST_IDs.contains(requestID);
	}
	
	public static synchronized void remove(String requestID) {
		REQUEST_IDs.remove(requestID);
	}
	
}