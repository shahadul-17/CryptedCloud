package com.cloud.crypted.server.core.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cloud.crypted.server.Application;
import com.cloud.crypted.server.core.DynamicResources;
import com.cloud.crypted.server.core.models.RecoveryInformation;
import com.cloud.crypted.server.core.models.User;
import com.cloud.crypted.server.core.repositories.UserRepository;
import com.cloud.crypted.server.core.utilities.MailUtilities;
import com.cloud.crypted.server.core.utilities.StringUtilities;

@Service
public class UserService {
	
	@Autowired
	private UserRepository userRepository;
	
	/**
	 * Checks whether this email is associated with
	 * any user account.
	 * 
	 * @param email
	 * @return Returns 'true' if the email is associated
	 * with a user account otherwise returns 'false'.
	 */
	public boolean exists(String email) {
		return userRepository.existsByEmail(email);
	}
	
	public List<?> get() {
		return userRepository.findAll();
	}
	
	public User getUserInstance(String email) {
		Optional<User> userContainer = userRepository.findByEmail(email);
		
		if (userContainer.isPresent()) {
			return userContainer.get();
		}
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public Map<?, ?> get(String email) {
		String errorMessage = null;
		Map<Object, Object> response = null;
		Optional<User> userContainer = userRepository.findByEmail(email);
		
		if (userContainer.isPresent()) {
			User user = userContainer.get();
			
			try {
				String userInformationJSONString = Application.OBJECT_MAPPER.writeValueAsString(user);
				response = Application.OBJECT_MAPPER.readValue(userInformationJSONString, Map.class);
			} catch (Exception exception) {
				exception.printStackTrace();
				
				errorMessage = DynamicResources.getErrorMessage("userDataRetrievalFailed");
			}
		} else {
			errorMessage = DynamicResources.getErrorMessage("userNotFound");
		}
		
		if (!StringUtilities.isNullOrEmpty(errorMessage)) {
			response = new HashMap<Object, Object>((int) DynamicResources.getConfiguration("collection.minimumCapacity"));
			response.put("errorMessage", errorMessage);
		}
		
		return response;
	}
	
	@SuppressWarnings("unchecked")
	public Map<?, ?> get(String email, String requesterEmail, String requesterName) {
		Map<?, ?> response = get(email);
		
		if (response.containsKey("errorMessage")) {
			String title = (String) DynamicResources.getConfiguration("title");
			String mailBody = String.format(
				DynamicResources.getEmailTemplate("userRegistrationRequest"),
				title,
				requesterEmail, requesterName, "Google Drive",
				title, title
			);
			
			String errorMessage;
			
			if (MailUtilities.sendMail(email,
					(String) DynamicResources.getConfiguration("mail.subject.userRegistrationRequest"), mailBody)) {
				errorMessage = DynamicResources.getErrorMessage("registrationRequestEmailSentSuccessfully");
			} else {
				errorMessage = DynamicResources.getErrorMessage("registrationRequestEmailSendingFailed");
			}
			
			((Map<String, String>) response).put("errorMessage", errorMessage);
		}
		
		return response;
	}
	
	@SuppressWarnings("unchecked")
	public Map<?, ?> post(String cloudService, String email, String hashedPassphrase,
			String encryptedPassphrase, String encryptedPrivateKey, String publicKey,
			List<RecoveryInformation> recoveryInformationList) {
		String errorMessage = null;
		Map<Object, Object> response = null;
		
		if (exists(email)) {
			errorMessage = DynamicResources.getErrorMessage("userAlreadyExists");
		} else {
			User user = new User(
				0, cloudService, email, hashedPassphrase,
				encryptedPassphrase, encryptedPrivateKey,
				publicKey, recoveryInformationList
			);
			
			user = userRepository.save(user);
			
			try {
				String userInformationJSONString = Application.OBJECT_MAPPER.writeValueAsString(user);
				response = Application.OBJECT_MAPPER.readValue(userInformationJSONString, Map.class);
			} catch (Exception exception) {
				exception.printStackTrace();
				
				errorMessage = DynamicResources.getErrorMessage("userInformationProcessingFailed");
			}
		}
		
		if (!StringUtilities.isNullOrEmpty(errorMessage)) {
			response = new HashMap<Object, Object>((int) DynamicResources.getConfiguration("collection.minimumCapacity"));
			response.put("errorMessage", errorMessage);
		}
		
		return response;
	}
	
	@SuppressWarnings("unchecked")
	public Map<?, ?> put(long userID, String cloudService, String email, String hashedPassphrase,
			String encryptedPassphrase, String encryptedPrivateKey, String publicKey,
			List<RecoveryInformation> recoveryInformationList) {
		String errorMessage = null;
		Map<Object, Object> response = null;
		
		if (exists(email)) {
			User user = null;
			
			if (Long.compare(userID, 0L) == 0) {
				Optional<User> userContainer = userRepository.findByEmail(email);
				
				if (userContainer.isPresent()) {
					user = userContainer.get();
					user.setCloudService(cloudService);
					user.setHashedPassphrase(hashedPassphrase);
					user.setEncryptedPassphrase(encryptedPassphrase);
					user.setEncryptedPrivateKey(encryptedPrivateKey);
					user.setPublicKey(publicKey);
					user.setRecoveryInformationList(recoveryInformationList);
				} else {
					// impossible scenario... :P :P
					// still just for safety...
					errorMessage = DynamicResources.getErrorMessage("invalidUserID");
				}
			} else {
				user = new User(
					userID, cloudService, email, hashedPassphrase,
					encryptedPassphrase, encryptedPrivateKey,
					publicKey, recoveryInformationList
				);
			}
			
			if (user != null) {
				user = userRepository.save(user);
				
				try {
					String userInformationJSONString = Application.OBJECT_MAPPER.writeValueAsString(user);
					response = Application.OBJECT_MAPPER.readValue(userInformationJSONString, Map.class);
				} catch (Exception exception) {
					exception.printStackTrace();
					
					errorMessage = DynamicResources.getErrorMessage("userInformationProcessingFailed");
				}
			}
		} else {
			errorMessage = DynamicResources.getErrorMessage("userNotFound");
		}
		
		if (!StringUtilities.isNullOrEmpty(errorMessage)) {
			response = new HashMap<Object, Object>((int) DynamicResources.getConfiguration("collection.minimumCapacity"));
			response.put("errorMessage", errorMessage);
		}
		
		return response;
	}
	
}