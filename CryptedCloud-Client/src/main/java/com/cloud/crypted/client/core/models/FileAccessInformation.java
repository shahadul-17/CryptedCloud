package com.cloud.crypted.client.core.models;

public class FileAccessInformation {
	
	private String email = "";
	private String cloudFileID = "";
	private String userRole = "";
	private String encryptedRandomKey = "";
	
	public FileAccessInformation() { }

	public FileAccessInformation(String email, String cloudFileID,
			String userRole, String encryptedRandomKey) {
		this.email = email;
		this.cloudFileID = cloudFileID;
		this.userRole = userRole;
		this.encryptedRandomKey = encryptedRandomKey;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCloudFileID() {
		return cloudFileID;
	}

	public void setCloudFileID(String cloudFileID) {
		this.cloudFileID = cloudFileID;
	}

	public String getUserRole() {
		return userRole;
	}

	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}

	public String getEncryptedRandomKey() {
		return encryptedRandomKey;
	}

	public void setEncryptedRandomKey(String encryptedRandomKey) {
		this.encryptedRandomKey = encryptedRandomKey;
	}
	
	@Override
	public String toString() {
		return cloudFileID + "@" + email;
	}
	
}