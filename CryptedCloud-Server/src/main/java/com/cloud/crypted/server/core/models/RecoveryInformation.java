package com.cloud.crypted.server.core.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name="recovery_information")
public class RecoveryInformation {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long recoveryInformationID = 0L;
	
	@NotBlank
	@Column(nullable=false)
	private String question = "";
	
	@NotBlank
	@Column(nullable=false)
	private String hashedAnswer = "";
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="userid")
	private User user;
	
	public RecoveryInformation() { }
	
	public RecoveryInformation(long recoveryInformationID,
			String question, String hashedAnswer) {
		this.recoveryInformationID = recoveryInformationID;
		this.question = question;
		this.hashedAnswer = hashedAnswer;
	}
	
	public long getRecoveryInformationID() {
		return recoveryInformationID;
	}
	
	public void setRecoveryInformationID(long recoveryInformationID) {
		this.recoveryInformationID = recoveryInformationID;
	}
	
	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getHashedAnswer() {
		return hashedAnswer;
	}

	public void setHashedAnswer(String hashedAnswer) {
		this.hashedAnswer = hashedAnswer;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
	
	@Override
	public String toString() {
		return question;
	}
	
}