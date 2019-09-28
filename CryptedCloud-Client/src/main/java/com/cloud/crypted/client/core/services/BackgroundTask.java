package com.cloud.crypted.client.core.services;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import com.cloud.crypted.client.Application;
import com.cloud.crypted.client.core.cryptography.AES;
import com.cloud.crypted.client.core.cryptography.BCrypt;
import com.cloud.crypted.client.core.cryptography.RSA;
import com.cloud.crypted.client.core.events.TaskListener;
import com.cloud.crypted.client.core.models.CloudFileInformation;
import com.cloud.crypted.client.core.models.FileAccessInformation;
import com.cloud.crypted.client.core.models.FileInformation;
import com.cloud.crypted.client.core.models.SecurityQuestionInformation;
import com.cloud.crypted.client.core.models.Task;
import com.cloud.crypted.client.core.models.UserInformation;
import com.cloud.crypted.client.core.utilities.RandomKeyGenerator;
import com.cloud.crypted.client.core.utilities.StringUtilities;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp.Browser;

public class BackgroundTask implements Task {
	
	private String name = null;
	private Object[] parameters = null;
	
	private List<TaskListener> taskListeners = null;
	
	public BackgroundTask(String name) {
		this.name = name;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public Object[] getParameters() {
		return parameters;
	}
	
	@Override
	public void setParameters(Object... parameters) {
		this.parameters = parameters;
	}
	
	@Override
	public void addTaskListener(TaskListener taskListener) {
		if (taskListener == null) {
			return;
		}
		
		if (taskListeners == null) {
			taskListeners = new LinkedList<TaskListener>();
		}
		
		taskListeners.add(taskListener);
	}
	
	private void callTaskListener(boolean succeeded, Object result) {
		if (taskListeners == null || taskListeners.isEmpty()) {
			return;
		}
		
		for (TaskListener taskListener : taskListeners) {
			if (succeeded) {
				taskListener.executionSucceeded(this, result);
			} else {
				taskListener.executionFailed(this, (Exception) result);
			}
		}
	}
	
	private void authenticate() throws Exception {
		callTaskListener(true, new GoogleDriveService((Browser) parameters[0]));
	}
	
	private void signIn() throws Exception {
		char[] passphrase = (char[]) parameters[1];
		
		String email = (String) parameters[0];
		CryptedCloudService cryptedCloudService = (CryptedCloudService) parameters[2];
		
		Object returnValue = cryptedCloudService.getUserInformation(email);
		
		if (returnValue instanceof String) {
			throw new Exception((String) returnValue);
		}
		
		UserInformation userInformation = (UserInformation) returnValue;
		
		if (!BCrypt.checkpw(new String(passphrase), userInformation.getHashedPassphrase())) {
			throw new Exception("Incorrect passphrase provided.");
		}
		
		callTaskListener(true, null);
	}
	
	private char[] retrievePassphrase(String email, CryptedCloudService cryptedCloudService) throws Exception {
		String[] ans = { "tom", "dhaka" };
		Object returnValue = cryptedCloudService.getUserInformation(email);
		
		if (returnValue instanceof String) {
			throw new Exception((String) returnValue);
		}
		
		UserInformation userInformation = (UserInformation) returnValue;
		String passphrase = userInformation.getEncryptedPassphrase();
		
		for (int i = ans.length - 1; i > -1; i--) {
			passphrase = AES.decrypt(ans[i].toCharArray(), passphrase);
		}
		
		System.out.println(passphrase);
		
		return passphrase.toCharArray();
	}
	
	private void signInWithDifferentGoogleDriveAccount() throws Exception {
		GoogleDriveService.removeCredential();
		authenticate();
	}
 	
	private void signUp() throws Exception {
		char[] passphrase = (char[]) parameters[1];
		char[] rePassphrase = (char[]) parameters[2];
		
		String email = (String) parameters[0];
		String[] securityQuestionAndAnswerArray = (String[]) parameters[3];
		CryptedCloudService cryptedCloudService = (CryptedCloudService) parameters[4];
		
		UserInformation userInformation = getUserInformation(email, passphrase, rePassphrase, securityQuestionAndAnswerArray);
		
		if (userInformation == null) {
			throw new Exception("Invalid registration data provided.");
		}
		
		Object returnValue = cryptedCloudService.createAccount(userInformation);
		
		if (returnValue instanceof String) {
			throw new Exception((String) returnValue);
		}
		
		callTaskListener(true, null);
	}
	
	private void retrieveUserInformation() throws Exception {
		String email = (String) parameters[0];
		CryptedCloudService cryptedCloudService = (CryptedCloudService) parameters[1];
		
		Object returnValue = cryptedCloudService.getUserInformation(email);
		
		if (returnValue instanceof String) {
			throw new Exception((String) returnValue);
		}
		
		callTaskListener(true, returnValue);
	}
	
	private void accountRecoveryFirstPhase() throws Exception {
		String[] answers = (String[]) parameters[0];
		UserInformation userInformation = (UserInformation) parameters[1];
		
		List<SecurityQuestionInformation> securityQuestionInformationList = userInformation.getSecurityQuestionInformationList();
		
		int i = 0;
		
		for (SecurityQuestionInformation securityQuestionInformation : securityQuestionInformationList) {
			answers[i] = answers[i].trim().toLowerCase();
			
			if (!BCrypt.checkpw(answers[i], securityQuestionInformation.getHashedAnswer())) {
				throw new Exception("Incorrect recovery information provided.");
			}
			
			i++;
		}
		
		callTaskListener(true, null);
	}
	
	private void accountRecoverySecondPhase() throws Exception {
		char[] newPassphrase = (char[]) parameters[0];
		char[] rePassphrase = (char[]) parameters[1];
		
		if (!validatePassphrase(newPassphrase, rePassphrase)) {
			throw new Exception("Passphrase must be atleast 8 characters long and both 'New passphrase' and 'Re-passphrase' must match.");
		}
		
		String[] answers = (String[]) parameters[2];
		UserInformation userInformation = (UserInformation) parameters[3];
		CryptedCloudService cryptedCloudService = (CryptedCloudService) parameters[4];
		
		String passphrase = userInformation.getEncryptedPassphrase();
		
		for (int i = answers.length - 1; i > -1; i--) {
			System.out.println(i + "" + answers[i]);
			
			if (StringUtilities.isNullOrEmpty(answers[i])) {
				continue;
			}
			
			answers[i] = answers[i].trim().toLowerCase();
			passphrase = AES.decrypt(answers[i].toCharArray(), passphrase);
		}
		
		System.out.println("++++++++++ PASSS = " + passphrase);
		
		callTaskListener(true, null);
	}
	
	private void refresh() throws Exception {
		GoogleDriveService googleDriveService = (GoogleDriveService) parameters[0];
		googleDriveService.retrieveCloudFileInformationList();
	}
	
	private void upload() throws Exception {
		char[] passphrase = (char[]) parameters[0];
		
		File localFile = (File) parameters[1];
		CryptedCloudService cryptedCloudService = (CryptedCloudService) parameters[2];
		GoogleDriveService googleDriveService = (GoogleDriveService) parameters[3];
		
		char[] randomKey = RandomKeyGenerator.generate();
		
		String cloudFileID = googleDriveService.upload(randomKey, localFile);
		
		if (cloudFileID.isEmpty()) {
			/*
			 * exceptions are handled in GoogleDriveService upload() method...
			 * no need to throw exception here... so, just 'return;'
			 */
			return;
		}
		
		Object returnValue = cryptedCloudService.saveFileInformation(new FileInformation(0L, cloudFileID));
		
		if (returnValue instanceof String) {
			throw new Exception((String) returnValue);
		}
		
		String encryptedRandomKey = AES.encrypt(passphrase, new String(randomKey));
		FileInformation fileInformation = (FileInformation) returnValue;
		FileAccessInformation fileAccessInformation = new FileAccessInformation(
			googleDriveService.getGoogleDriveUser().getEmail(),
			fileInformation.getCloudFileID(), "owner", encryptedRandomKey
		);
		
		returnValue = cryptedCloudService.saveFileAccessInformation(fileAccessInformation);
		
		if (returnValue instanceof String) {
			throw new Exception((String) returnValue);
		}
		
		callTaskListener(true, null);
	}
	
	private void download() throws Exception {
		char[] passphrase = (char[]) parameters[0];
		
		String downloadLocation = (String) parameters[1];
		CloudFileInformation cloudFileInformation = (CloudFileInformation) parameters[2];
		CryptedCloudService cryptedCloudService = (CryptedCloudService) parameters[3];
		GoogleDriveService googleDriveService = (GoogleDriveService) parameters[4];
		
		boolean fileInformationExists = cryptedCloudService.fileInformationExists(cloudFileInformation.getID());
		
		if (fileInformationExists) {
			Object returnValue = cryptedCloudService.getUserInformation(googleDriveService.getGoogleDriveUser().getEmail());
			
			if (returnValue instanceof String) {
				throw new Exception((String) returnValue);
			}
			
			UserInformation userInformation = (UserInformation) returnValue;
			returnValue = cryptedCloudService.getFileAccessInformation(googleDriveService.getGoogleDriveUser().getEmail(), cloudFileInformation.getID());
			
			if (returnValue instanceof String) {
				throw new Exception((String) returnValue);
			}
			
			FileAccessInformation fileAccessInformation = (FileAccessInformation) returnValue;
			
			char[] randomKey = null;
			
			if ("owner".equalsIgnoreCase(fileAccessInformation.getUserRole())) {
				randomKey = AES.decrypt(passphrase, fileAccessInformation.getEncryptedRandomKey()).toCharArray();
			} else if ("writer".equalsIgnoreCase(fileAccessInformation.getUserRole())) {
				String privateKey = AES.decrypt(passphrase, userInformation.getEncryptedPrivateKey());
				randomKey = new RSA(privateKey, "").decrypt(fileAccessInformation.getEncryptedRandomKey()).toCharArray();
			} else {
				throw new Exception("Unable to identify the user role: '" + fileAccessInformation.getUserRole() + "'");
			}
			
			googleDriveService.download(randomKey, downloadLocation, cloudFileInformation);
		} else {
			// it is a regular file... just download without decryption...
			googleDriveService.download(null, downloadLocation, cloudFileInformation);
		}
		
		callTaskListener(true, null);
	}
	
	private void share() throws Exception {
		String email = (String) parameters[0];		// email of the user want to share with...
		CloudFileInformation cloudFileInformation = (CloudFileInformation) parameters[1];
		CryptedCloudService cryptedCloudService = (CryptedCloudService) parameters[2];
		GoogleDriveService googleDriveService = (GoogleDriveService) parameters[3];
		
		boolean fileInformationExists = cryptedCloudService.fileInformationExists(cloudFileInformation.getID());
		
		if (fileInformationExists) {	// if the file exists in SFADE database, then it is encrypted...
			// getting file access information of currently logged in user...
			Object returnValue = cryptedCloudService.getFileAccessInformation(googleDriveService.getGoogleDriveUser().getEmail(), cloudFileInformation.getID());
			
			if (returnValue instanceof String) {
				throw new Exception((String) returnValue);
			}
			
			FileAccessInformation fileAccessInformation = (FileAccessInformation) returnValue;
			
			// if user is the owner of this file, then he/she is allowed to share...
			if ("owner".equalsIgnoreCase(fileAccessInformation.getUserRole())) {
				// getting information of user with whom the file will be shared...
				returnValue = cryptedCloudService.getUserInformation(email, true,
					googleDriveService.getGoogleDriveUser().getFirstName(),
					googleDriveService.getGoogleDriveUser().getEmail()
				);
				
				if (returnValue instanceof String) {
					throw new Exception((String) returnValue);
				}
				
				UserInformation userInformation = (UserInformation) returnValue;
				
				// decrypting random key using file owner's password...
				String randomKey = AES.decrypt(Application.passphrase, fileAccessInformation.getEncryptedRandomKey());
				
				// re-encrypting randomKey using the public key of the user with whom the file will be shared...
				String encryptedRandomKey = new RSA("", userInformation.getPublicKey()).encrypt(randomKey);
				
				// saving new file access...
				cryptedCloudService.saveFileAccessInformation(new FileAccessInformation(
					userInformation.getEmail(), fileAccessInformation.getCloudFileID(),
					"writer", encryptedRandomKey
				));
			} else {
				throw new Exception("You do not have permission to share \"" + cloudFileInformation.getName() + "\"");
			}
		}
		
		googleDriveService.share(email, cloudFileInformation);
		callTaskListener(true, null);
	}
	
	private void delete() throws Exception {
		CloudFileInformation cloudFileInformation = (CloudFileInformation) parameters[0];
		CryptedCloudService cryptedCloudService = (CryptedCloudService) parameters[1];
		GoogleDriveService googleDriveService = (GoogleDriveService) parameters[2];
		
		boolean fileInformationExists = cryptedCloudService.fileInformationExists(cloudFileInformation.getID());
		
		if (fileInformationExists) {
			Object returnValue = cryptedCloudService.getFileAccessInformation(googleDriveService.getGoogleDriveUser().getEmail(), cloudFileInformation.getID());
			
			if (returnValue instanceof String) {
				throw new Exception((String) returnValue);
			}
			
			FileAccessInformation fileAccessInformation = (FileAccessInformation) returnValue;
			
			if ("owner".equalsIgnoreCase(fileAccessInformation.getUserRole())) {
				returnValue = cryptedCloudService.deleteFileAccessInformationSet(fileAccessInformation.getCloudFileID());
				
				if (returnValue instanceof String) {
					throw new Exception((String) returnValue);
				}
			} else {
				throw new Exception("You do not have permission to delete \"" + cloudFileInformation.getName() + "\"");
			}
		}
		
		googleDriveService.delete(cloudFileInformation);
		callTaskListener(true, null);
	}
	
	@Override
	public void execute() {
		try {
			if ("authenticate".equalsIgnoreCase(name)) {
				authenticate();
			} else if ("signIn".equalsIgnoreCase(name)) {
				signIn();
			} else if ("signInWithDifferentGoogleDriveAccount".equalsIgnoreCase(name)) {
				signInWithDifferentGoogleDriveAccount();
			} else if ("signUp".equalsIgnoreCase(name)) {
				signUp();
			} else if ("retrieveUserInformation".equalsIgnoreCase(name)) {
				retrieveUserInformation();
			} else if ("accountRecoveryFirstPhase".equalsIgnoreCase(name)) {
				accountRecoveryFirstPhase();
			} else if ("accountRecoverySecondPhase".equalsIgnoreCase(name)) {
				accountRecoverySecondPhase();
			} else if ("refresh".equalsIgnoreCase(name)) {
				refresh();
			} else if ("upload".equalsIgnoreCase(name)) {
				upload();
			} else if ("download".equalsIgnoreCase(name)) {
				download();
			} else if ("share".equalsIgnoreCase(name)) {
				share();
			} else if ("delete".equalsIgnoreCase(name)) {
				delete();
			}
		} catch (Exception exception) {
			callTaskListener(false, exception);
		}
	}
	
	private static boolean validatePassphrase(char[] passphrase, char[] rePassphrase) {
		if (passphrase.length < 8) {
			return false;
		}
		
		if (passphrase.length != rePassphrase.length) {
			return false;
		}
		
		for (int i = 0; i < passphrase.length; i++) {
			if (passphrase[i] != rePassphrase[i]) {
				return false;
			}
		}
		
		return true;
	}

	private static UserInformation getUserInformation(String email, char[] passphrase,
			char[] rePassphrase, String[] securityQuestionAndAnswerArray) {
		if (validatePassphrase(passphrase, rePassphrase)) {
			UserInformation userInformation = new UserInformation();
			userInformation.setCloudService("google-drive");
			userInformation.setEmail(email);
			userInformation.setHashedPassphrase(BCrypt.hashpw(new String(passphrase), BCrypt.gensalt()));
			
			String encryptedPassphrase = new String(passphrase);
			
			for (int i = 0; i < securityQuestionAndAnswerArray.length; i += 2) {
				securityQuestionAndAnswerArray[i] = securityQuestionAndAnswerArray[i].trim();
				securityQuestionAndAnswerArray[i + 1] = securityQuestionAndAnswerArray[i + 1].trim().toLowerCase();
				
				if (securityQuestionAndAnswerArray[i].isEmpty() || securityQuestionAndAnswerArray[i + 1].isEmpty()) {
					continue;
				}
				
				try {
					encryptedPassphrase = AES.encrypt(securityQuestionAndAnswerArray[i + 1].toCharArray(), encryptedPassphrase);
				} catch (Exception exception) {
					exception.printStackTrace();
					
					return null;
				}
				
				userInformation.addSecurityQuestionInformation(securityQuestionAndAnswerArray[i], BCrypt.hashpw(securityQuestionAndAnswerArray[i + 1], BCrypt.gensalt()));
			}
			
			userInformation.setEncryptedPassphrase(encryptedPassphrase);
			
			RSA rsa = null;
			
			try {
				rsa = new RSA();
			} catch (Exception exception) {
				exception.printStackTrace();
				
				return null;
			}
			
			try {
				userInformation.setEncryptedPrivateKey(AES.encrypt(passphrase, rsa.getPrivateKeyString()));
			} catch (Exception exception) {
				exception.printStackTrace();
				
				return null;
			}
			
			userInformation.setPublicKey(rsa.getPublicKeyString());
			
			return userInformation;
		}
		
		return null;
	}
	
}