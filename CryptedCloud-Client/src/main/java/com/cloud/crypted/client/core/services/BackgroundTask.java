package com.cloud.crypted.client.core.services;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.cloud.crypted.client.Application;
import com.cloud.crypted.client.core.Configuration;
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
import com.cloud.crypted.client.ui.AccessControlManager;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp.Browser;

public class BackgroundTask implements Task {
	
	private String name = null;
	private Object[] parameters = null;
	private Object returnValue = null;
	
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
	
	private void callTaskListener(boolean succeeded, Object ... results) {
		if (taskListeners == null || taskListeners.isEmpty()) {
			return;
		}
		
		for (TaskListener taskListener : taskListeners) {
			if (succeeded) {
				taskListener.executionSucceeded(this, results);
			} else {
				if (results.length > 1 && results[1] != null) {
					taskListener.executionFailed(this, (Exception) results[0], results[1]);
				} else {
					taskListener.executionFailed(this, (Exception) results[0]);
				}
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
		
		callTaskListener(true, (Object) null);
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
		
		callTaskListener(true, (Object) null);
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
		
		callTaskListener(true, (Object) null);
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
			if (StringUtilities.isNullOrEmpty(answers[i])) {
				continue;
			}
			
			answers[i] = answers[i].trim().toLowerCase();
			passphrase = AES.decrypt(answers[i].toCharArray(), passphrase);
		}
		
		String newHashedPassphrase = BCrypt.hashpw(new String(newPassphrase), BCrypt.gensalt());
		String newEncryptedPassphrase = new String(newPassphrase);
		
		for (int i = 0; i < answers.length; i++) {
			if (StringUtilities.isNullOrEmpty(answers[i])) {
				continue;
			}
			
			newEncryptedPassphrase = AES.encrypt(answers[i].toCharArray(), newEncryptedPassphrase);
		}
		
		userInformation.setHashedPassphrase(newHashedPassphrase);
		userInformation.setEncryptedPassphrase(newEncryptedPassphrase);
		userInformation.setEncryptedPrivateKey(AES.encrypt(newPassphrase, AES.decrypt(passphrase.toCharArray(), userInformation.getEncryptedPrivateKey())));
		
		Object returnValue = cryptedCloudService.updateAccount(userInformation);		// saving user information...
		
		if (returnValue instanceof String) {
			throw new Exception((String) returnValue);
		}
		
		// update file info...
		returnValue = cryptedCloudService.getFileAccessInformationSetByEmail(userInformation.getEmail());
		
		if (returnValue instanceof String) {
			throw new Exception((String) returnValue);
		}
		
		int encryptedRandomKeySignatureLength = Integer.parseInt(Configuration.get("encryptedRandomKey.signature.length"));
		
		@SuppressWarnings("unchecked")
		Set<FileAccessInformation> fileAccessInformationSet = (Set<FileAccessInformation>) returnValue;
		
		for (FileAccessInformation fileAccessInformation : fileAccessInformationSet) {
			String encryptedRandomKeySignature = fileAccessInformation.getEncryptedRandomKey().substring(0, encryptedRandomKeySignatureLength);
			String encryptedRandomKeyWithoutSignature = fileAccessInformation.getEncryptedRandomKey().substring(encryptedRandomKeySignatureLength);
			
			if (Configuration.get("encryptedRandomKey.signature.aes").equals(encryptedRandomKeySignature)) {
				fileAccessInformation.setEncryptedRandomKey(
					encryptedRandomKeySignature +
					AES.encrypt(newPassphrase,
						AES.decrypt(passphrase.toCharArray(),
							encryptedRandomKeyWithoutSignature
						)
					)
				);
				
				returnValue = cryptedCloudService.saveFileAccessInformation(fileAccessInformation);
				
				if (returnValue instanceof String) {
					throw new Exception((String) returnValue);
				}
			}
		}
		
		callTaskListener(true, (Object) null);
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
		
		String encryptedRandomKey = Configuration.get("encryptedRandomKey.signature.aes")
				+ AES.encrypt(passphrase, new String(randomKey));
		FileInformation fileInformation = (FileInformation) returnValue;
		FileAccessInformation fileAccessInformation = new FileAccessInformation(
			googleDriveService.getGoogleDriveUser().getEmail(),
			fileInformation.getCloudFileID(), "owner", encryptedRandomKey
		);
		
		returnValue = cryptedCloudService.saveFileAccessInformation(fileAccessInformation);
		
		if (returnValue instanceof String) {
			throw new Exception((String) returnValue);
		}
		
		callTaskListener(true, (Object) null);
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
			
			int encryptedRandomKeySignatureLength = Integer.parseInt(Configuration.get("encryptedRandomKey.signature.length"));
			
			String encryptedRandomKeySignature = fileAccessInformation.getEncryptedRandomKey().substring(0, encryptedRandomKeySignatureLength);
			String encryptedRandomKeyWithoutSignature = fileAccessInformation.getEncryptedRandomKey().substring(encryptedRandomKeySignatureLength);
			
			char[] randomKey = null;
			
			if ("owner".equalsIgnoreCase(fileAccessInformation.getUserRole())) {
				randomKey = AES.decrypt(passphrase, encryptedRandomKeyWithoutSignature).toCharArray();
			} else if ("writer".equalsIgnoreCase(fileAccessInformation.getUserRole())) {
				if (Configuration.get("encryptedRandomKey.signature.rsa").equalsIgnoreCase(encryptedRandomKeySignature)) {
					String privateKey = AES.decrypt(passphrase, userInformation.getEncryptedPrivateKey());
					String temporaryRandomKey = new RSA(privateKey, "").decrypt(encryptedRandomKeyWithoutSignature);
					
					randomKey = temporaryRandomKey.toCharArray();
					
					// during first download, changing encryption from 'RSA' to 'AES'...
					fileAccessInformation.setEncryptedRandomKey(
						Configuration.get("encryptedRandomKey.signature.aes") +
						AES.encrypt(passphrase, temporaryRandomKey)
					);
					
					returnValue = cryptedCloudService.saveFileAccessInformation(fileAccessInformation);
					
					if (returnValue instanceof String) {
						System.err.println("An internal error occurred: " + (String) returnValue);
					}
				} else if (Configuration.get("encryptedRandomKey.signature.aes").equalsIgnoreCase(encryptedRandomKeySignature)) {
					randomKey = AES.decrypt(passphrase, encryptedRandomKeyWithoutSignature).toCharArray();
				} else {
					throw new Exception("Unable to identify the signature of the encrypted random key.");
				}
			} else {
				throw new Exception("Unable to identify the user role: '" + fileAccessInformation.getUserRole() + "'");
			}
			
			googleDriveService.download(randomKey, downloadLocation, cloudFileInformation);
		} else {
			// it is a regular file... just download without decryption...
			googleDriveService.download(null, downloadLocation, cloudFileInformation);
		}
		
		callTaskListener(true, (Object) null);
	}
	
	private void share() throws Exception {
		String email = (String) parameters[0];		// current user's email address...		
		String emailToShare = (String) parameters[1];		// email of the user want to share with...
		AccessControlManager accessControlManager = (AccessControlManager) parameters[2];
		CryptedCloudService cryptedCloudService = (CryptedCloudService) parameters[3];
		GoogleDriveService googleDriveService = (GoogleDriveService) parameters[4];
		
		CloudFileInformation cloudFileInformation = accessControlManager.getCloudFileInformation();
		
		returnValue = accessControlManager;		// if error occurs, this value will be passed...
		
		if (email.equalsIgnoreCase(emailToShare)) {
			throw new Exception("Sorry, you cannot share file(s) with yourself.");
		}
		
		boolean fileInformationExists = cryptedCloudService.fileInformationExists(cloudFileInformation.getID());
		
		String[] emailAddresses = null;
		
		if (fileInformationExists) {	// if the file exists in CryptedCloud database, then it is encrypted...
			// getting file access information of currently logged in user...
			Object returnValue = cryptedCloudService.getFileAccessInformation(googleDriveService.getGoogleDriveUser().getEmail(), cloudFileInformation.getID());
			
			if (returnValue instanceof String) {
				throw new Exception((String) returnValue);
			}
			
			FileAccessInformation fileAccessInformation = (FileAccessInformation) returnValue;
			
			// if user is the owner of this file, then he/she is allowed to share...
			if ("owner".equalsIgnoreCase(fileAccessInformation.getUserRole())) {
				// getting information of user with whom the file will be shared...
				returnValue = cryptedCloudService.getUserInformation(emailToShare, true,
					googleDriveService.getGoogleDriveUser().getFirstName(),
					googleDriveService.getGoogleDriveUser().getEmail()
				);
				
				if (returnValue instanceof String) {
					throw new Exception((String) returnValue);
				}
				
				UserInformation userInformation = (UserInformation) returnValue;
				
				// decrypting random key using file owner's password...
				String randomKey = AES.decrypt(Application.passphrase,
					fileAccessInformation.getEncryptedRandomKey().substring(
						Integer.parseInt(Configuration.get("encryptedRandomKey.signature.length"))
					)
				);
				
				// re-encrypting randomKey using the public key of the user with whom the file will be shared...
				String encryptedRandomKey = Configuration.get("encryptedRandomKey.signature.rsa") +
						new RSA("", userInformation.getPublicKey()).encrypt(randomKey);
				
				// saving new file access...
				cryptedCloudService.saveFileAccessInformation(new FileAccessInformation(
					userInformation.getEmail(), fileAccessInformation.getCloudFileID(),
					"writer", encryptedRandomKey
				));
				
				emailAddresses = retrieveEmailAddressesFromFileInformation(email,
						accessControlManager.getCloudFileInformation(), cryptedCloudService);
			} else {
				throw new Exception("You do not have permission to share \"" + cloudFileInformation.getName() + "\"");
			}
		}
		
		googleDriveService.share(emailToShare, cloudFileInformation);
		callTaskListener(true, cloudFileInformation.getName(), emailToShare, emailAddresses, accessControlManager);
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
				returnValue = cryptedCloudService.deleteFileAccessInformation(
					googleDriveService.getGoogleDriveUser().getEmail(),
					fileAccessInformation.getCloudFileID()
				);
				
				if (returnValue instanceof String) {
					throw new Exception((String) returnValue);
				}
			}
		}
		
		googleDriveService.delete(cloudFileInformation);
		callTaskListener(true, (Object) null);
	}
	
	private String[] retrieveEmailAddressesFromFileInformation(String email,
			CloudFileInformation fileInformation,
			CryptedCloudService cryptedCloudService) throws Exception {
		Object returnValue = cryptedCloudService.getFileAccessInformationSetByCloudFileID(fileInformation.getID());
		
		if (returnValue instanceof String) {
			throw new Exception((String) returnValue);
		}
		
		@SuppressWarnings("unchecked")
		Set<FileAccessInformation> fileAccessInformationSet = (Set<FileAccessInformation>) returnValue;
		String[] emailAddresses = new String[fileAccessInformationSet.size()];
		
		boolean isOwner = false;
		int i = 0;
		
		for (FileAccessInformation fileAccessInformation : fileAccessInformationSet) {
			if (email.equalsIgnoreCase(fileAccessInformation.getEmail())) {
				if (email.equalsIgnoreCase(fileAccessInformation.getEmail()) &&
						"owner".equalsIgnoreCase(fileAccessInformation.getUserRole())) {
					isOwner = true;
				}
			} else {
				emailAddresses[i] = fileAccessInformation.getEmail();
				
				i++;
			}
		}
		
		if (!isOwner) {
			throw new Exception("Sorry, you do not have access to view the security information of the selected file.");
		}
		
		return emailAddresses;
	}
	
	private void requestForOpeningAccessControlManager() throws Exception {
		String email = (String) parameters[0];
		CloudFileInformation fileInformation = (CloudFileInformation) parameters[1];
		CryptedCloudService cryptedCloudService = (CryptedCloudService) parameters[2];
		
		String[] emailAddresses = retrieveEmailAddressesFromFileInformation(email, fileInformation, cryptedCloudService);
		
		callTaskListener(true, emailAddresses, fileInformation);
	}
	
	private void refreshAccessControlInformation() throws Exception {
		String email = (String) parameters[0];
		AccessControlManager accessControlManager = (AccessControlManager) parameters[1];
		CryptedCloudService cryptedCloudService = (CryptedCloudService) parameters[2];
		
		returnValue = accessControlManager;		// if error occurs, this value will be passed...
		
		String[] emailAddresses = retrieveEmailAddressesFromFileInformation(email,
				accessControlManager.getCloudFileInformation(), cryptedCloudService);
		
		callTaskListener(true, emailAddresses, accessControlManager);
	}
	
	private void revokeAccess() throws Exception {
		String email = (String) parameters[0];				// current user's email address...
		String emailToRevokeAccess = (String) parameters[1];		// email of the user whose file access needs to be revoked...
		AccessControlManager accessControlManager = (AccessControlManager) parameters[2];
		CryptedCloudService cryptedCloudService = (CryptedCloudService) parameters[3];
		// GoogleDriveService googleDriveService = (GoogleDriveService) parameters[4];		// this will be needed later...
		
		returnValue = accessControlManager;		// if error occurs, this value will be passed...
		
		Object returnValue = cryptedCloudService.deleteFileAccessInformation(emailToRevokeAccess,
				accessControlManager.getCloudFileInformation().getID());
		
		if (returnValue instanceof String) {
			throw new Exception((String) returnValue);
		}
		
		String[] emailAddresses = retrieveEmailAddressesFromFileInformation(email,
				accessControlManager.getCloudFileInformation(), cryptedCloudService);
		
		callTaskListener(true, emailAddresses, accessControlManager);
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
			} else if ("requestForOpeningAccessControlManager".equalsIgnoreCase(name)) {
				requestForOpeningAccessControlManager();
			} else if ("refreshAccessControlInformation".equalsIgnoreCase(name)) {
				refreshAccessControlInformation();
			} else if ("revokeAccess".equalsIgnoreCase(name)) {
				revokeAccess();
			}
		} catch (Exception exception) {
			callTaskListener(false, exception, returnValue);
			
			returnValue = null;
		}
	}
	
	private static boolean validatePassphrase(char[] passphrase) {
		if (passphrase.length < 8) {
			return false;
		}
		
		return true;
	}
	
	private static boolean validatePassphrase(char[] passphrase, char[] rePassphrase) {
		if (!validatePassphrase(passphrase)) {
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
			
			String encryptedPassphrase = "";
			
			for (int i = 0; i < securityQuestionAndAnswerArray.length; i += 2) {
				securityQuestionAndAnswerArray[i] = securityQuestionAndAnswerArray[i].trim();
				securityQuestionAndAnswerArray[i + 1] = securityQuestionAndAnswerArray[i + 1].trim().toLowerCase();
				
				if (securityQuestionAndAnswerArray[i].isEmpty() || securityQuestionAndAnswerArray[i + 1].isEmpty()) {
					continue;
				}
				
				if (encryptedPassphrase.isEmpty()) {
					encryptedPassphrase = new String(passphrase);
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