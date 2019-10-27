package com.cloud.crypted.server.core.models;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
public class FileAccess {
	
	@EmbeddedId
	private FileAccessID fileAccessID = null;
	
	@NotBlank
	@Column(nullable=false)
	private String userRole = "";
	
	@Lob
	@Column
	private String encryptedRandomKey = "";
	
	@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
	@ManyToOne(fetch=FetchType.LAZY)
    @MapsId("userid")
    @JoinColumn(name="userid")
	private User user = null;
	
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	@ManyToOne(fetch=FetchType.LAZY)
    @MapsId("fileid")
    @JoinColumn(name="fileid")
	private File file = null;
	
	public FileAccess() { }
	
	public FileAccess(FileAccessID fileAccessID) {
		this.fileAccessID = fileAccessID;
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
	
	public User getUser() {
		return user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
	
	public File getFile() {
		return file;
	}
	
	public void setFile(File file) {
		this.file = file;
	}
	
}