package com.cloud.crypted.client.core.cryptography;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;

public class RSA {
	
	private PrivateKey privateKey;
	private PublicKey publicKey;
	
	private static final short KEY_SIZE = 2048;
	
	public RSA() throws Exception {
		generateKeyPair();
	}
	
	public RSA(String privateKey, String publicKey) throws Exception {
		loadKeyPair(privateKey, publicKey);
	}
	
	public PrivateKey getPrivateKey() {
		return privateKey;
	}
	
	public String getPrivateKeyString() {
		return Base64.getEncoder().encodeToString(privateKey.getEncoded());
	}
	
	public PublicKey getPublicKey() {
		return publicKey;
	}
	
	public String getPublicKeyString() {
		return Base64.getEncoder().encodeToString(publicKey.getEncoded());
	}
	
	private void generateKeyPair() throws Exception {
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
		keyPairGenerator.initialize(KEY_SIZE);
		
		KeyPair keyPair = keyPairGenerator.generateKeyPair();
		
		privateKey = keyPair.getPrivate();
		publicKey = keyPair.getPublic();
	}
	
	private void loadKeyPair(String privateKey, String publicKey) throws Exception {
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		
		if (privateKey != null && privateKey.length() != 0) {
			this.privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKey)));
		}
		
		if (publicKey != null && publicKey.length() != 0) {
			this.publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(publicKey)));
		}
	}
	
	public String encrypt(String text) throws Exception {
		Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        
        return Base64.getEncoder().encodeToString(cipher.doFinal(text.getBytes()));
	}
	
	public String decrypt(String text) throws Exception {
		Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        
        return new String(cipher.doFinal(Base64.getDecoder().decode(text)));
	}
	
}