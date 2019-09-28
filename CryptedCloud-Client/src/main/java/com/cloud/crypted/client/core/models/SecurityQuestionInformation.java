package com.cloud.crypted.client.core.models;

public class SecurityQuestionInformation {
	
	private long securityQuestionID = 0L;
	
	private String question;
	
	private String hashedAnswer;
	
	public SecurityQuestionInformation() { }
	
	public SecurityQuestionInformation(String question, String hashedAnswer) {
		this.question = question;
		this.hashedAnswer = hashedAnswer;
	}

	public long getSecurityQuestionID() {
		return securityQuestionID;
	}
	
	public void setSecurityQuestionID(long securityQuestionID) {
		this.securityQuestionID = securityQuestionID;
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