package com.cloud.crypted.server.core.models;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;

@Embeddable
public class FileAccessID implements Serializable {
	
	@Transient
	private static final long serialVersionUID = -8953939105524612148L;
	
	@Column
	private long userID = 0L;
	
	@Column
	private long fileID = 0L;
	
	public FileAccessID() { }
	
	public FileAccessID(long userID, long fileID) {
		this.userID = userID;
		this.fileID = fileID;
	}
	
	public long getUserID() {
		return userID;
	}
	
	public void setUserID(long userID) {
		this.userID = userID;
	}
	
	public long getFileID() {
		return fileID;
	}
	
	public void setFileID(long fileID) {
		this.fileID = fileID;
	}
	
	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		
		if (object instanceof FileAccessID) {
			FileAccessID fileAccessID = (FileAccessID) object;
			
			if (Long.compare(fileAccessID.userID, userID) == 0 &&
				Long.compare(fileAccessID.fileID, fileID) == 0) {
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(userID, fileID);
	}
	
}