package com.cloud.crypted.client.core.models;

import java.util.LinkedList;
import java.util.List;

public class UserInformation {
	
	private long userID = 0L;
	
	private String cloudService = "";
	private String email = "";
	private String hashedPassphrase = "";
	private String encryptedPassphrase = "";
	private String encryptedPrivateKey = "";
	private String publicKey = "";
	
	private List<SecurityQuestionInformation> securityQuestionInformationList = null;
	// private Set<FileAccessInformation> fileAccessInformationSet = null;
	
	public UserInformation() { }
	
	public UserInformation(long userID, String cloudService, String email, String hashedPassphrase,
			String encryptedPassphrase, String encryptedPrivateKey, String publicKey,
			List<SecurityQuestionInformation> securityQuestionInformationList) {
		this.userID = userID;
		this.cloudService = cloudService;
		this.email = email;
		this.hashedPassphrase = hashedPassphrase;
		this.encryptedPassphrase = encryptedPassphrase;
		this.encryptedPrivateKey = encryptedPrivateKey;
		this.publicKey = publicKey;
		this.securityQuestionInformationList = securityQuestionInformationList;
	}
	
	public long getUserID() {
		return userID;
	}

	public void setUserID(long userID) {
		this.userID = userID;
	}

	public String getCloudService() {
		return cloudService;
	}

	public void setCloudService(String cloudService) {
		this.cloudService = cloudService;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getHashedPassphrase() {
		return hashedPassphrase;
	}

	public void setHashedPassphrase(String hashedPassphrase) {
		this.hashedPassphrase = hashedPassphrase;
	}

	public String getEncryptedPassphrase() {
		return encryptedPassphrase;
	}

	public void setEncryptedPassphrase(String encryptedPassphrase) {
		this.encryptedPassphrase = encryptedPassphrase;
	}

	public String getEncryptedPrivateKey() {
		return encryptedPrivateKey;
	}

	public void setEncryptedPrivateKey(String encryptedPrivateKey) {
		this.encryptedPrivateKey = encryptedPrivateKey;
	}

	public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}
	
	public List<SecurityQuestionInformation> getSecurityQuestionInformationList() {
		return securityQuestionInformationList;
	}
	
	public void setSecurityQuestionInformationList(List<SecurityQuestionInformation> securityQuestionInformationList) {
		this.securityQuestionInformationList = securityQuestionInformationList;
	}
	
	public void addSecurityQuestionInformation(String question, String hashedAnswer) {
		if (question == null || question.trim().isEmpty() || hashedAnswer == null || hashedAnswer.isEmpty()) {
			return;
		}
		
		if (securityQuestionInformationList == null) {
			securityQuestionInformationList = new LinkedList<SecurityQuestionInformation>();
		}
		
		securityQuestionInformationList.add(new SecurityQuestionInformation(question, hashedAnswer));
	}
	
	@Override
	public String toString() {
		return email;
	}
	
}