package com.cloud.crypted.client.core.cryptography;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import com.cloud.crypted.client.core.Configuration;

public class AES {
	
	private Cipher cipher = null;
	
	public AES(char[] passphrase, InputStream inputStream, OutputStream outputStream) throws Exception {
		byte[] initializationVector;
		int mode;
		
		if (outputStream == null) {		// means we want decryption...
			initializationVector = readInitializationVector(inputStream);
			mode = Cipher.DECRYPT_MODE;
		} else {
			initializationVector = generateInitializationVector();
			mode = Cipher.ENCRYPT_MODE;
			
			outputStream.write(initializationVector);
			outputStream.flush();
		}
		
		SecretKey secretKey = generateSecretKey(initializationVector, passphrase);
		IvParameterSpec ivParameterSpec = new IvParameterSpec(initializationVector);
		
		cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(mode, secretKey, ivParameterSpec);
	}
	
	public byte[] process(byte[] buffer, int offset, int length) {
		return cipher.update(buffer, offset, length);
	}
	
	public byte[] clinch() throws Exception {
		return cipher.doFinal();
	}
	
	private static byte[] generateInitializationVector() {
		byte[] initializationVector = new byte[Integer.parseInt(Configuration.get("aes.secretKeyLength"))];
		
		new SecureRandom().nextBytes(initializationVector);
		
		return initializationVector;
	}
	
	private static SecretKey generateSecretKey(byte[] initializationVector, char[] password) throws Exception {
		PBEKeySpec pbeKeySpec = new PBEKeySpec(
			password, initializationVector,
			Integer.parseInt(Configuration.get("pbeKeySpec.iterationCount")), Integer.parseInt(Configuration.get("aes.secretKeyLength")) * Byte.SIZE
		);
		
		byte[] encodedSecretKey = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1").generateSecret(pbeKeySpec).getEncoded();
		
		return new SecretKeySpec(encodedSecretKey, "AES");
	}
	
	private static byte[] readInitializationVector(InputStream inputStream) throws Exception {
		byte[] initializationVector = new byte[Integer.parseInt(Configuration.get("aes.secretKeyLength"))];
		
		for (int i = 0; i < initializationVector.length; i++) {
			initializationVector[i] = (byte) inputStream.read();
		}
		
		return initializationVector;
	}
	
	public static String encrypt(char[] passphrase, String plainText) throws Exception {
		return encrypt(passphrase, plainText.getBytes());
	}
	
	public static String encrypt(char[] passphrase, byte[] bytesToEncrypt) throws Exception {
		byte[] initializationVector = generateInitializationVector();
		
		SecretKey secretKey = generateSecretKey(initializationVector, passphrase);
		IvParameterSpec ivParameterSpec = new IvParameterSpec(initializationVector);
		
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
		
		byte[] cipherTextByteArray = cipher.doFinal(bytesToEncrypt);
		byte[] encryptedByteArray = new byte[initializationVector.length + cipherTextByteArray.length];
		
		int index = 0;
		
		for (int i = 0; i < initializationVector.length; i++) {
			encryptedByteArray[index] = initializationVector[i];
			index++;
		}
		
		for (int i = 0; i < cipherTextByteArray.length; i++) {
			encryptedByteArray[index] = cipherTextByteArray[i];
			index++;
		}
		
		return Base64.getEncoder().encodeToString(encryptedByteArray);
	}
	
	public static String decrypt(char[] password, String cipherText) throws Exception {
		byte[] cipherTextByteArray = Base64.getDecoder().decode(cipherText);
		byte[] initializationVector = new byte[Integer.parseInt(Configuration.get("aes.secretKeyLength"))];
		byte[] cipherX = new byte[cipherTextByteArray.length - initializationVector.length];
		
		int index = 0;
		
		for (int i = 0; i < initializationVector.length; i++) {
			initializationVector[i] = cipherTextByteArray[index];
			index++;
		}
		
		for (int i = 0; i < cipherX.length; i++) {
			cipherX[i] = cipherTextByteArray[index];
			index++;
		}
		
		SecretKey secretKey = generateSecretKey(initializationVector, password);
		IvParameterSpec ivParameterSpec = new IvParameterSpec(initializationVector);
		
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
		
		return new String(cipher.doFinal(cipherX));
	}
	
}