package com.cloud.crypted.server.core.models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotBlank;

@Entity
public class User {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long userID = 0L;
	
	@NotBlank
	@Column(nullable=false)
	private String cloudService = "";
	
	@NotBlank
	@Column(nullable=false, unique=true)
	private String email = "";
	
	@NotBlank
	@Column(nullable=false)
	private String hashedPassphrase = "";
	
	@Lob
	private String encryptedPassphrase = "";
	
	@NotBlank
	@Lob
	@Column(nullable=false)
	private String encryptedPrivateKey = "";
	
	@NotBlank
	@Lob
	@Column(nullable=false)
	private String publicKey = "";
	
	@OneToMany(mappedBy="user", cascade=CascadeType.ALL)
	private List<RecoveryInformation> recoveryInformationList = null;
	
	public User() { }
	
	public User(long userID, String cloudService, String email, String hashedPassphrase,
			String encryptedPassphrase, String encryptedPrivateKey, String publicKey,
			List<RecoveryInformation> recoveryInformationList) {
		this.userID = userID;
		this.cloudService = cloudService;
		this.email = email;
		this.hashedPassphrase = hashedPassphrase;
		this.encryptedPassphrase = encryptedPassphrase;
		this.encryptedPrivateKey = encryptedPrivateKey;
		this.publicKey = publicKey;
		this.recoveryInformationList = recoveryInformationList;
		
		if (this.recoveryInformationList != null) {
			for (RecoveryInformation securityQuestion : this.recoveryInformationList) {
				securityQuestion.setUser(this);
			}
		}
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
	
	public List<RecoveryInformation> getRecoveryInformationList() {
		return recoveryInformationList;
	}
	
	public void setRecoveryInformationList(List<RecoveryInformation> recoveryInformationList) {
		this.recoveryInformationList = recoveryInformationList;
	}
	
	@Override
	public String toString() {
		return email;
	}
	
}