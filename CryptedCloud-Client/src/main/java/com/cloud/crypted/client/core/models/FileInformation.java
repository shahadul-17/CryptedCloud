package com.cloud.crypted.client.core.models;

public class FileInformation {

	private long fileID = 0L;
	
	private String cloudFileID = "";
	
	public FileInformation() { }

	public FileInformation(long fileID, String cloudFileID) {
		this.fileID = fileID;
		this.cloudFileID = cloudFileID;
	}
	
	public long getFileID() {
		return fileID;
	}

	public void setFileID(long fileID) {
		this.fileID = fileID;
	}

	public String getCloudFileID() {
		return cloudFileID;
	}

	public void setCloudFileID(String cloudFileID) {
		this.cloudFileID = cloudFileID;
	}
	
	@Override
	public String toString() {
		return cloudFileID;
	}
	
}