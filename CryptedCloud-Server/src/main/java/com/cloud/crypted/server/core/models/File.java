package com.cloud.crypted.server.core.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;

@Entity
public class File {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long fileID = 0L;
	
	@NotBlank
	@Column(nullable=false)
	private String cloudFileID = "";
	
	public File() { }

	public File(long fileID, String cloudFileID) {
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