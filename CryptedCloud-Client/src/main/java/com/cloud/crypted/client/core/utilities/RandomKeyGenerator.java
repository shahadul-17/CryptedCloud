package com.cloud.crypted.client.core.utilities;

import java.util.Random;

public class RandomKeyGenerator {
	
	private static final int MINIMUM_LENGTH = 64;
	private static final int MAXIMUM_LENGTH = 128;
	private static final int MINIMUM_ASCII_VALUE = 33;
	private static final int MAXIMUM_ASCII_VALUE = 126;
	
	private static final Random RANDOM = new Random(System.currentTimeMillis());
	
	private static int generateRandomIntegerInRange(int lowerBound, int upperBound) {
		return RANDOM.nextInt((upperBound - lowerBound) + 1) + lowerBound;
	}
	
	public static char[] generate() {
		int length = generateRandomIntegerInRange(MINIMUM_LENGTH, MAXIMUM_LENGTH);
		char[] randomKey = new char[length];
		
		for (int i = 0; i < length; i++) {
			randomKey[i] = (char) generateRandomIntegerInRange(MINIMUM_ASCII_VALUE, MAXIMUM_ASCII_VALUE);
		}
		
		return randomKey;
	}
	
}