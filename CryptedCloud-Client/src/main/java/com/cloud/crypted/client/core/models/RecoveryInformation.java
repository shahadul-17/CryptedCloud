package com.cloud.crypted.client.core.models;

public class RecoveryInformation {
	
	private long recoveryInformationID = 0L;
	
	private String question;
	
	private String hashedAnswer;
	
	public RecoveryInformation() { }
	
	public RecoveryInformation(String question, String hashedAnswer) {
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
	
	@Override
	public String toString() {
		return question;
	}
	
}