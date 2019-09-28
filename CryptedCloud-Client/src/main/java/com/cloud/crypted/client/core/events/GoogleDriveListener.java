package com.cloud.crypted.client.core.events;

import java.util.EventListener;
import java.util.List;

import com.cloud.crypted.client.core.models.CloudFileInformation;

public interface GoogleDriveListener extends EventListener {
	
	void fileInformationRetrievalSucceeded(List<CloudFileInformation> cloudFileInformationList);
	void fileInformationRetrievalFailed(Exception exception);
	
	void encryptionProgressChanged(int progress, String fileName);
	void encryptionSucceeded(String fileName);
	void encryptionFailed(String fileName, Exception exception);
	
	void uploadProgressChanged(int progress, String fileName);
	void uploadSucceeded(String fileName);
	void uploadFailed(String fileName, Exception exception);
	
	void downloadProgressChanged(int progress, String fileName);
	void downloadSucceeded(String cloudFileID, String fileName);
	void downloadFailed(String fileName, Exception exception);
	
	void sharingSucceeded(String cloudFileID, String fileName, String email);
	void sharingFailed(String fileName, String email, Exception exception);
	
	void deletionSucceeded(String cloudFileID, String fileName);
	void deletionFailed(String fileName, Exception exception);
	
}