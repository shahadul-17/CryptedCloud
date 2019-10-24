package com.cloud.crypted.client.core.utilities;

import java.util.Random;

import com.cloud.crypted.client.core.Configuration;

public class RandomKeyGenerator {
	
	private static final Random RANDOM = new Random(System.currentTimeMillis());
	
	private static int generateRandomIntegerInRange(int lowerBound, int upperBound) {
		return RANDOM.nextInt((upperBound - lowerBound) + 1) + lowerBound;
	}
	
	public static char[] generate() {
		int length = generateRandomIntegerInRange(Integer.parseInt(Configuration.get("randomKey.length.minimum")),
				Integer.parseInt(Configuration.get("randomKey.length.maximum")));
		char[] randomKey = new char[length];
		
		for (int i = 0; i < length; i++) {
			randomKey[i] = (char) generateRandomIntegerInRange(Integer.parseInt(Configuration.get("randomKey.character.lowerBound")),
					Integer.parseInt(Configuration.get("randomKey.character.upperBound")));
		}
		
		return randomKey;
	}
	
}