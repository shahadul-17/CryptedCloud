package com.cloud.crypted.server.core.controllers;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cloud.crypted.server.core.ErrorMessages;
import com.cloud.crypted.server.core.services.FileService;
import com.cloud.crypted.server.core.utilities.RequestIDProvider;
import com.cloud.crypted.server.core.utilities.StringUtilities;

@RestController
@RequestMapping(path="files")
public class FileController {
	
	@Autowired
	private FileService fileService;
	
	@RequestMapping(path="/get")
	public Object get(@RequestParam(required=true) Map<String, String> requestParameters) {
		String requestID = requestParameters.get("requestID");
		
		if (StringUtilities.isNullOrEmpty(requestID) ||
				!RequestIDProvider.contains(requestID)) {
			RequestIDProvider.remove(requestID);
			
			Map<String, String> response = new HashMap<String, String>();
			response.put("errorMessage", ErrorMessages.get("invalidRequestID"));
			
			return response;
		}
		
		RequestIDProvider.remove(requestID);
		
		String email = requestParameters.get("email");
		String cloudFileID = requestParameters.get("cloudFileID");
		
		if (email == null) {
			if (StringUtilities.isNullOrEmpty(cloudFileID)) {
				return fileService.get();
			}
			
			Boolean ping = Boolean.parseBoolean(requestParameters.get("ping"));
			
			if (ping != null && ping) {
				return fileService.exists(cloudFileID);
			}
			
			return fileService.get(cloudFileID);
		} else {
			return fileService.get(email, cloudFileID);
		}
	}
	
	@RequestMapping(path="/post")
	public Object post(@RequestParam(required=true) Map<String, Object> requestParameters,
			HttpSession httpSession) {
		String requestID = (String) requestParameters.get("requestID");
		
		if (StringUtilities.isNullOrEmpty(requestID) ||
				!RequestIDProvider.contains(requestID)) {
			RequestIDProvider.remove(requestID);
			
			Map<String, String> response = new HashMap<String, String>();
			response.put("errorMessage", ErrorMessages.get("invalidRequestID"));
			
			return response;
		}
		
		RequestIDProvider.remove(requestID);
		
		// basically we are passing the request body from 
		// ApplicationController to UserController through session...
		@SuppressWarnings("unchecked")
		Map<String, Object> requestBody = (Map<String, Object>) httpSession.getAttribute(requestID);
		httpSession.removeAttribute(requestID);
		
		String email = (String) requestBody.get("email");
		String cloudFileID = (String) requestBody.get("cloudFileID");
		String userRole = (String) requestBody.get("userRole");
		String encryptedRandomKey = (String) requestBody.get("encryptedRandomKey");
		
		return fileService.post(email, cloudFileID, userRole, encryptedRandomKey);
	}
	
	@RequestMapping(path="/delete")
	public Object delete(@RequestParam(required=true) Map<String, String> requestParameters) {
		String requestID = (String) requestParameters.get("requestID");
		
		if (StringUtilities.isNullOrEmpty(requestID) ||
				!RequestIDProvider.contains(requestID)) {
			RequestIDProvider.remove(requestID);
			
			Map<String, String> response = new HashMap<String, String>();
			response.put("errorMessage", ErrorMessages.get("invalidRequestID"));
			
			return response;
		}
		
		RequestIDProvider.remove(requestID);
		
		String email = requestParameters.get("email");
		String cloudFileID = requestParameters.get("cloudFileID");
		
		return fileService.delete(email, cloudFileID);
	}
	
}