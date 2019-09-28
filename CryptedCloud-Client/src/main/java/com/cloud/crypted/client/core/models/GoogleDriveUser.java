package com.cloud.crypted.client.core.models;

import com.google.api.services.drive.model.User;

public class GoogleDriveUser {
	
	private String permissionID = "";
	private String firstName = "";
	private String lastName = "";
	private String email = "";
	
	public GoogleDriveUser(User user) {
		this.permissionID = user.getPermissionId();
		
		int lastIndexOfSpace = user.getDisplayName().lastIndexOf(' ');
		
		if (lastIndexOfSpace == -1) {
			this.firstName = user.getDisplayName();
		} else {
			this.firstName = user.getDisplayName().substring(0, lastIndexOfSpace);
			this.lastName = user.getDisplayName().substring(lastIndexOfSpace + 1);
		}
		
		this.email = user.getEmailAddress();
	}
	
	public String getPermissionID() {
		return permissionID;
	}
	
	public String getFirstName() {
		return firstName;
	}
	
	public String getLastName() {
		return lastName;
	}
	
	public String getEmail() {
		return email;
	}
	
	@Override
	public String toString() {
		return email;
	}
	
}