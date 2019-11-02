package com.cloud.crypted.server.core.controllers;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cloud.crypted.server.core.DynamicResources;
import com.cloud.crypted.server.core.models.RecoveryInformation;
import com.cloud.crypted.server.core.services.UserService;
import com.cloud.crypted.server.core.utilities.RequestIDProvider;
import com.cloud.crypted.server.core.utilities.StringUtilities;

@RestController
@RequestMapping(path="users")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@RequestMapping(path="/get")
	public Object get(@RequestParam(required=true) Map<String, String> requestParameters) {
		String requestID = requestParameters.get("requestID");
		
		if (StringUtilities.isNullOrEmpty(requestID) ||
				!RequestIDProvider.contains(requestID)) {
			RequestIDProvider.remove(requestID);
			
			Map<String, String> response = new HashMap<String, String>();
			response.put("errorMessage", DynamicResources.getErrorMessage("invalidRequestID"));
			
			return response;
		}
		
		RequestIDProvider.remove(requestID);
		
		String email = requestParameters.get("email");
		
		if (StringUtilities.isNullOrEmpty(email)) {
			return userService.get();
		}
		
		String requesterEmail = requestParameters.get("requesterEmail");
		String requesterName = requestParameters.get("requesterName");
		
		return userService.get(email, requesterEmail, requesterName);
	}
	
	@RequestMapping(path="/post")
	public Object post(@RequestParam(required=true) Map<String, Object> requestParameters,
			HttpSession httpSession) {
		String requestID = (String) requestParameters.get("requestID");
		
		if (StringUtilities.isNullOrEmpty(requestID) ||
				!RequestIDProvider.contains(requestID)) {
			RequestIDProvider.remove(requestID);
			
			Map<String, String> response = new HashMap<String, String>();
			response.put("errorMessage", DynamicResources.getErrorMessage("invalidRequestID"));
			
			return response;
		}
		
		RequestIDProvider.remove(requestID);
		
		// basically we are passing the request body from 
		// ApplicationController to UserController through session...
		@SuppressWarnings("unchecked")
		Map<String, Object> requestBody = (Map<String, Object>) httpSession.getAttribute(requestID);
		httpSession.removeAttribute(requestID);
		
		printMap(requestParameters);
		printMap(requestBody);
		
		String email = (String) requestBody.get("email");
		String cloudService = (String) requestBody.get("cloudService");
		String hashedPassphrase = (String) requestBody.get("hashedPassphrase");
		String encryptedPassphrase = (String) requestBody.get("encryptedPassphrase");
		String encryptedPrivateKey = (String) requestBody.get("encryptedPrivateKey");
		String publicKey = (String) requestBody.get("publicKey");
		List<?> temporaryRecoveryInformationList = (List<?>) requestBody.get("recoveryInformationList");
		List<RecoveryInformation> recoveryInformationList = null;
		
		if (temporaryRecoveryInformationList != null && temporaryRecoveryInformationList.size() != 0) {
			recoveryInformationList = new LinkedList<RecoveryInformation>();
			
			for (Object recoveryInformationObject : temporaryRecoveryInformationList) {
				if (recoveryInformationObject == null) {
					continue;
				}
				
				Map<?, ?> recoveryInformationMap = (Map<?, ?>) recoveryInformationObject;
				
				RecoveryInformation recoveryInformation = new RecoveryInformation();
				Object recoveryInformationIDObject = recoveryInformationMap.get("recoveryInformationID");
				
				long recoveryInformationID = 0L;
				
				if (recoveryInformationIDObject instanceof Integer) {
					recoveryInformationID = (Integer) recoveryInformationIDObject;
				} else if (recoveryInformationIDObject instanceof Long) {
					recoveryInformationID = (Long) recoveryInformationIDObject;
				}
				
				recoveryInformation.setRecoveryInformationID(recoveryInformationID);
				recoveryInformation.setQuestion((String) recoveryInformationMap.get("question"));
				recoveryInformation.setHashedAnswer((String) recoveryInformationMap.get("hashedAnswer"));
				recoveryInformationList.add(recoveryInformation);
			}
		}
		
		return userService.post(cloudService, email,
			hashedPassphrase, encryptedPassphrase,
			encryptedPrivateKey, publicKey,
			recoveryInformationList
		);
	}
	
	@RequestMapping(path="/put")
	public Object put(@RequestParam(required=true) Map<String, Object> requestParameters,
			HttpSession httpSession) {
		String requestID = (String) requestParameters.get("requestID");
		
		if (StringUtilities.isNullOrEmpty(requestID) ||
				!RequestIDProvider.contains(requestID)) {
			RequestIDProvider.remove(requestID);
			
			Map<String, String> response = new HashMap<String, String>();
			response.put("errorMessage", DynamicResources.getErrorMessage("invalidRequestID"));
			
			return response;
		}
		
		RequestIDProvider.remove(requestID);
		
		// basically we are passing the request body from 
		// ApplicationController to UserController through session...
		@SuppressWarnings("unchecked")
		Map<String, Object> requestBody = (Map<String, Object>) httpSession.getAttribute(requestID);
		httpSession.removeAttribute(requestID);
		
		printMap(requestParameters);
		printMap(requestBody);
		
		long userID;
		
		try {
			userID = (Integer) requestBody.get("userID");
		} catch (Exception exception) {
			exception.printStackTrace();
			
			userID = (Long) requestBody.get("userID");
		}
		
		String email = (String) requestBody.get("email");
		String cloudService = (String) requestBody.get("cloudService");
		String hashedPassphrase = (String) requestBody.get("hashedPassphrase");
		String encryptedPassphrase = (String) requestBody.get("encryptedPassphrase");
		String encryptedPrivateKey = (String) requestBody.get("encryptedPrivateKey");
		String publicKey = (String) requestBody.get("publicKey");
		List<?> temporaryRecoveryInformationList = (List<?>) requestBody.get("recoveryInformationList");
		List<RecoveryInformation> recoveryInformationList = null;
		
		if (temporaryRecoveryInformationList != null && temporaryRecoveryInformationList.size() != 0) {
			recoveryInformationList = new LinkedList<RecoveryInformation>();
			
			for (Object recoveryInformationObject : temporaryRecoveryInformationList) {
				if (recoveryInformationObject == null) {
					continue;
				}
				
				Map<?, ?> recoveryInformationMap = (Map<?, ?>) recoveryInformationObject;
				
				RecoveryInformation recoveryInformation = new RecoveryInformation();
				Object recoveryInformationIDObject = recoveryInformationMap.get("recoveryInformationID");
				
				long recoveryInformationID = 0L;
				
				if (recoveryInformationIDObject instanceof Integer) {
					recoveryInformationID = (Integer) recoveryInformationIDObject;
				} else if (recoveryInformationIDObject instanceof Long) {
					recoveryInformationID = (Long) recoveryInformationIDObject;
				}
				
				recoveryInformation.setRecoveryInformationID(recoveryInformationID);
				recoveryInformation.setQuestion((String) recoveryInformationMap.get("question"));
				recoveryInformation.setHashedAnswer((String) recoveryInformationMap.get("hashedAnswer"));
				recoveryInformationList.add(recoveryInformation);
			}
		}
		
		return userService.put(userID, cloudService, email,
			hashedPassphrase, encryptedPassphrase,
			encryptedPrivateKey, publicKey,
			recoveryInformationList
		);
	}
	
	private static void printMap(Map<?, ?> map) {
		int i = 1;
		
		Set<?> keySet = map.keySet();
		
		for (Object key : keySet) {
			System.out.println("[" + i + "] " + key + ": " + map.get(key));
			
			i++;
		}
	}
	
}