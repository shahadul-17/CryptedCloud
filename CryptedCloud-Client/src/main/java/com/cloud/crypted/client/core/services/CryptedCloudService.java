package com.cloud.crypted.client.core.services;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.cloud.crypted.client.Application;
import com.cloud.crypted.client.core.Configuration;
import com.cloud.crypted.client.core.models.FileAccessInformation;
import com.cloud.crypted.client.core.models.FileInformation;
import com.cloud.crypted.client.core.models.RecoveryInformation;
import com.cloud.crypted.client.core.models.UserInformation;
import com.cloud.crypted.client.core.utilities.StringUtilities;

/**
 * This class was written like sh*t.
 * Need to refactor the entire class.
 * 
 * @author Shahadul Alam
 *
 */
public class CryptedCloudService {
	
	public CryptedCloudService() { }
	
	public Object createAccount(UserInformation userInformation) {
		StringBuilder responseBuilder = new StringBuilder(Integer.parseInt(Configuration.get("collection.initialCapacity")));
		
		if (sendHTTPRequest("POST", Configuration.get("service.host") + "v" + Configuration.get("version") + '/' + "users", userInformation, responseBuilder) == 200) {
			Map<?, ?> response = null;
			
			try {
				response = Application.OBJECT_MAPPER.readValue(responseBuilder.toString(), Map.class);
			} catch (Exception exception) {
				exception.printStackTrace();
				
				return exception.getMessage();
			}
			
			Object errorMessage = response.get("errorMessage");
			
			if (errorMessage == null) {
				try {
					userInformation.setUserID((Integer) response.get("userID"));
				} catch (Exception exception) {
					exception.printStackTrace();
					
					userInformation.setUserID((Long) response.get("userID"));
				}
				
				userInformation.setCloudService((String) response.get("cloudService"));
				userInformation.setEmail((String) response.get("email"));
				userInformation.setHashedPassphrase((String) response.get("hashedPassphrase"));
				userInformation.setEncryptedPassphrase((String) response.get("encryptedPassphrase"));
				userInformation.setEncryptedPrivateKey((String) response.get("encryptedPrivateKey"));
				userInformation.setPublicKey((String) response.get("publicKey"));
				
				List<?> temporaryRecoveryInformationList = (List<?>) response.get("recoveryInformationList");
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
						recoveryInformationList.add((RecoveryInformation) recoveryInformation);
					}
				}
				
				userInformation.setRecoveryInformationList(recoveryInformationList);
				
				return userInformation;
			} else {
				return errorMessage;
			}
		}
		
		return "An error occurred while connecting to " + Configuration.get("title") + " server.";
	}
	
	public Object updateAccount(UserInformation userInformation) {
		StringBuilder responseBuilder = new StringBuilder(Integer.parseInt(Configuration.get("collection.initialCapacity")));
		
		if (sendHTTPRequest("PUT", Configuration.get("service.host") + "v" + Configuration.get("version") + '/' + "users", userInformation, responseBuilder) == 200) {
			Map<?, ?> response = null;
			
			try {
				response = Application.OBJECT_MAPPER.readValue(responseBuilder.toString(), Map.class);
			} catch (Exception exception) {
				exception.printStackTrace();
				
				return exception.getMessage();
			}
			
			Object errorMessage = response.get("errorMessage");
			
			if (errorMessage == null) {
				try {
					userInformation.setUserID((Integer) response.get("userID"));
				} catch (Exception exception) {
					exception.printStackTrace();
					
					userInformation.setUserID((Long) response.get("userID"));
				}
				
				userInformation.setCloudService((String) response.get("cloudService"));
				userInformation.setEmail((String) response.get("email"));
				userInformation.setHashedPassphrase((String) response.get("hashedPassphrase"));
				userInformation.setEncryptedPassphrase((String) response.get("encryptedPassphrase"));
				userInformation.setEncryptedPrivateKey((String) response.get("encryptedPrivateKey"));
				userInformation.setPublicKey((String) response.get("publicKey"));
				
				List<?> temporaryRecoveryInformationList = (List<?>) response.get("recoveryInformationList");
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
						recoveryInformationList.add((RecoveryInformation) recoveryInformation);
					}
				}
				
				userInformation.setRecoveryInformationList(recoveryInformationList);
				
				return userInformation;
			} else {
				return errorMessage;
			}
		}
		
		return "An error occurred while connecting to " + Configuration.get("title") + " server.";
	}
	
	public Object getUserInformation(String email) {
		return getUserInformation(email, false, null, null);
	}
	
	public Object getUserInformation(String email, boolean sendEmail, String requesterName, String requesterEmail) {
		StringBuilder responseBuilder = new StringBuilder(Integer.parseInt(Configuration.get("collection.initialCapacity")));
		
		String url = Configuration.get("service.host") + "v" + Configuration.get("version") + '/' + "users?email=" + email;
		
		if (sendEmail && !StringUtilities.isNullOrEmpty(requesterName) &&
				!StringUtilities.isNullOrEmpty(requesterEmail)) {
			url += "&sendEmail=" + sendEmail + "&requesterName=" + requesterName + "&requesterEmail=" + requesterEmail;
		}
		
		if (sendHTTPRequest("GET", url, (String) null, responseBuilder) == 200) {
			Map<?, ?> response = null;
			
			try {
				response = Application.OBJECT_MAPPER.readValue(responseBuilder.toString(), Map.class);
			} catch (Exception exception) {
				exception.printStackTrace();
				
				return exception.getMessage();
			}
			
			Object errorMessage = response.get("errorMessage");
			
			if (errorMessage == null) {
				UserInformation userInformation = new UserInformation();
				
				try {
					userInformation.setUserID((Integer) response.get("userID"));
				} catch (Exception exception) {
					exception.printStackTrace();
					
					userInformation.setUserID((Long) response.get("userID"));
				}
				
				userInformation.setCloudService((String) response.get("cloudService"));
				userInformation.setEmail((String) response.get("email"));
				userInformation.setHashedPassphrase((String) response.get("hashedPassphrase"));
				userInformation.setEncryptedPassphrase((String) response.get("encryptedPassphrase"));
				userInformation.setEncryptedPrivateKey((String) response.get("encryptedPrivateKey"));
				userInformation.setPublicKey((String) response.get("publicKey"));
				
				List<?> temporaryRecoveryInformationList = (List<?>) response.get("recoveryInformationList");
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
						recoveryInformationList.add((RecoveryInformation) recoveryInformation);
					}
				}
				
				userInformation.setRecoveryInformationList(recoveryInformationList);
				
				return userInformation;
			} else {
				return errorMessage;
			}
		}
		
		return "An error occurred while connecting to " + Configuration.get("title") + " server.";
	}
	
	public Object saveFileInformation(String email, String cloudFileID, String userRole, String encryptedRandomKey) {
		StringBuilder responseBuilder = new StringBuilder(Integer.parseInt(Configuration.get("collection.initialCapacity")));
		
		Map<String, String> fileInformation = new HashMap<String, String>(Integer.parseInt(Configuration.get("collection.initialCapacity")));
		fileInformation.put("email", email);
		fileInformation.put("cloudFileID", cloudFileID);
		fileInformation.put("userRole", userRole);
		fileInformation.put("encryptedRandomKey", encryptedRandomKey);
		
		if (sendHTTPRequest("POST", Configuration.get("service.host") + "v" + Configuration.get("version") + '/' + "files", fileInformation, responseBuilder) == 200) {
			Map<?, ?> response = null;
			
			try {
				response = Application.OBJECT_MAPPER.readValue(responseBuilder.toString(), Map.class);
			} catch (Exception exception) {
				exception.printStackTrace();
				
				return exception.getMessage();
			}
			
			Object errorMessage = response.get("errorMessage");
			
			if (errorMessage == null) {
				return response;
			} else {
				return errorMessage;
			}
		}
		
		return "An error occurred while connecting to " + Configuration.get("title") + " server.";
	}
	
	public boolean fileInformationExists(String cloudFileID) {
		StringBuilder responseBuilder = new StringBuilder(Integer.parseInt(Configuration.get("collection.initialCapacity")));
		
		if (sendHTTPRequest("GET", Configuration.get("service.host") + "v" + Configuration.get("version") +
				'/' + "files?cloudFileID=" + cloudFileID + "&ping=true", (String) null, responseBuilder) == 200) {
			return Boolean.parseBoolean(responseBuilder.toString());
		}
		
		return false;
	}
	
	public Object saveFileAccessInformation(FileAccessInformation fileAccessInformation) {
		StringBuilder responseBuilder = new StringBuilder(Integer.parseInt(Configuration.get("collection.initialCapacity")));
		
		if (sendHTTPRequest("POST", Configuration.get("service.host") + "v" + Configuration.get("version") + '/' + "files", fileAccessInformation, responseBuilder) == 200) {
			Map<?, ?> response = null;
			
			try {
				response = Application.OBJECT_MAPPER.readValue(responseBuilder.toString(), Map.class);
			} catch (Exception exception) {
				exception.printStackTrace();
				
				return exception.getMessage();
			}
			
			Object errorMessage = response.get("errorMessage");
			
			if (errorMessage == null) {
				fileAccessInformation = new FileAccessInformation();
				fileAccessInformation.setEmail((String) response.get("email"));
				fileAccessInformation.setCloudFileID((String) response.get("cloudFileID"));
				fileAccessInformation.setUserRole((String) response.get("userRole"));
				fileAccessInformation.setEncryptedRandomKey((String) response.get("encryptedRandomKey"));
				
				return fileAccessInformation;
			} else {
				return errorMessage;
			}
		}
		
		return "An error occurred while connecting to " + Configuration.get("title") + " server.";
	}
	
	public Object getFileAccessInformation(String email, String cloudFileID) {
		StringBuilder responseBuilder = new StringBuilder(Integer.parseInt(Configuration.get("collection.initialCapacity")));
		
		if (sendHTTPRequest("GET", Configuration.get("service.host") + "v" + Configuration.get("version") + '/' + "files?email=" + email + "&cloudFileID=" + cloudFileID, (String) null, responseBuilder) == 200) {
			Map<?, ?> response = null;
			
			try {
				response = Application.OBJECT_MAPPER.readValue(responseBuilder.toString(), Map.class);
			} catch (Exception exception) {
				exception.printStackTrace();
				
				return exception.getMessage();
			}
			
			Object errorMessage = response.get("errorMessage");
			
			if (errorMessage == null) {
				FileAccessInformation fileAccessInformation = new FileAccessInformation();
				fileAccessInformation.setEmail((String) response.get("email"));
				fileAccessInformation.setCloudFileID((String) response.get("cloudFileID"));
				fileAccessInformation.setUserRole((String) response.get("userRole"));
				fileAccessInformation.setEncryptedRandomKey((String) response.get("encryptedRandomKey"));
				
				
				
				return fileAccessInformation;
			} else {
				return errorMessage;
			}
		}
		
		return "An error occurred while connecting to " + Configuration.get("title") + " server.";
	}
	
	public Object deleteFileAccessInformation(String email, String cloudFileID) {
		StringBuilder responseBuilder = new StringBuilder(Integer.parseInt(Configuration.get("collection.initialCapacity")));
		
		if (sendHTTPRequest("DELETE", Configuration.get("service.host") + "v" + Configuration.get("version") + '/' + "files?email=" + email + "&cloudFileID=" + cloudFileID, (String) null, responseBuilder) == 200) {
			Map<?, ?> response = null;
			
			try {
				response = Application.OBJECT_MAPPER.readValue(responseBuilder.toString(), Map.class);
			} catch (Exception exception) {
				exception.printStackTrace();
				
				return exception.getMessage();
			}
			
			Object errorMessage = response.get("errorMessage");
			
			if (errorMessage == null) {
				return null;
			} else {
				return errorMessage;
			}
		}
		
		return "An error occurred while connecting to " + Configuration.get("title") + " server.";
	}
	
	public Object getFileAccessInformationListByEmail(String email) {
		StringBuilder responseBuilder = new StringBuilder(Integer.parseInt(Configuration.get("collection.initialCapacity")));
		
		if (sendHTTPRequest("GET", Configuration.get("service.host") + "v" + Configuration.get("version") + '/' + "files?email=" + email, (String) null, responseBuilder) == 200) {
			Map<?, ?> response = null;
			
			try {
				response = Application.OBJECT_MAPPER.readValue(responseBuilder.toString(), Map.class);
			} catch (Exception exception) {
				exception.printStackTrace();
				
				return exception.getMessage();
			}
			
			Object errorMessage = response.get("errorMessage");
			
			if (errorMessage == null) {
				List<?> temporaryFileAccessInformationList = (List<?>) response.get("fileAccessInformationList");
				List<FileAccessInformation> fileAccessInformationList = new LinkedList<FileAccessInformation>();
				
				for (Object fileAccessInformationObject : temporaryFileAccessInformationList) {
					Map<?, ?> fileAccessInformationMap = (Map<?, ?>) fileAccessInformationObject;
					
					FileAccessInformation fileAccessInformation = new FileAccessInformation();
					fileAccessInformation.setEmail((String) fileAccessInformationMap.get("email"));
					fileAccessInformation.setCloudFileID((String) fileAccessInformationMap.get("cloudFileID"));
					fileAccessInformation.setUserRole((String) fileAccessInformationMap.get("userRole"));
					fileAccessInformation.setEncryptedRandomKey((String) fileAccessInformationMap.get("encryptedRandomKey"));
					fileAccessInformationList.add(fileAccessInformation);
				}
				
				return fileAccessInformationList;
			} else {
				return errorMessage;
			}
		}
		
		return "An error occurred while connecting to " + Configuration.get("title") + " server.";
	}
	
	public Object getFileAccessInformationListByCloudFileID(String cloudFileID) {
		StringBuilder responseBuilder = new StringBuilder(Integer.parseInt(Configuration.get("collection.initialCapacity")));
		
		if (sendHTTPRequest("GET", Configuration.get("service.host") + "v" + Configuration.get("version") + '/' + "files?email=&cloudFileID=" + cloudFileID, (String) null, responseBuilder) == 200) {
			Map<?, ?> response = null;
			
			try {
				response = Application.OBJECT_MAPPER.readValue(responseBuilder.toString(), Map.class);
			} catch (Exception exception) {
				exception.printStackTrace();
				
				return exception.getMessage();
			}
			
			Object errorMessage = response.get("errorMessage");
			
			if (errorMessage == null) {
				List<?> temporaryFileAccessInformationList = (List<?>) response.get("fileAccessInformationList");
				List<FileAccessInformation> fileAccessInformationList = new LinkedList<FileAccessInformation>();
				
				for (Object fileAccessInformationObject : temporaryFileAccessInformationList) {
					Map<?, ?> fileAccessInformationMap = (Map<?, ?>) fileAccessInformationObject;
					
					FileAccessInformation fileAccessInformation = new FileAccessInformation();
					fileAccessInformation.setEmail((String) fileAccessInformationMap.get("email"));
					fileAccessInformation.setCloudFileID((String) fileAccessInformationMap.get("cloudFileID"));
					fileAccessInformation.setUserRole((String) fileAccessInformationMap.get("userRole"));
					fileAccessInformation.setEncryptedRandomKey((String) fileAccessInformationMap.get("encryptedRandomKey"));
					fileAccessInformationList.add(fileAccessInformation);
				}
				
				return fileAccessInformationList;
			} else {
				return errorMessage;
			}
		}
		
		return "An error occurred while connecting to " + Configuration.get("title") + " server.";
	}
	
	public Object deleteFileAccessInformationList(String cloudFileID) {
		StringBuilder responseBuilder = new StringBuilder(Integer.parseInt(Configuration.get("collection.initialCapacity")));
		
		if (sendHTTPRequest("DELETE", Configuration.get("service.host") + "v" + Configuration.get("version") + '/' + "files?cloudFileID=" + cloudFileID, (String) null, responseBuilder) == 200) {
			Map<?, ?> response = null;
			
			try {
				response = Application.OBJECT_MAPPER.readValue(responseBuilder.toString(), Map.class);
			} catch (Exception exception) {
				exception.printStackTrace();
				
				return exception.getMessage();
			}
			
			Object errorMessage = response.get("errorMessage");
			
			if (errorMessage == null) {
				return null;
			}
			
			return (String) errorMessage;
		}
		
		return "An error occurred while connecting to " + Configuration.get("title") + " server.";
	}
	
	private static int getHTTPResponseCode(HttpURLConnection httpURLConnection) {
		int responseCode = -1;
		
		try {
			responseCode = httpURLConnection.getResponseCode();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
		
		return responseCode;
	}
	
	private static byte validateUserInformation(UserInformation userInformation) {
		if (userInformation == null) {
			return -1;
		}
		
		if (userInformation.getEmail().isEmpty()) {
			return -3;
		}
		
		if (userInformation.getHashedPassphrase().isEmpty()) {
			return -4;
		}
		
		if (userInformation.getEncryptedPrivateKey().isEmpty()) {
			return -5;
		}
		
		if (userInformation.getPublicKey().isEmpty()) {
			return -5;
		}
		
		return 0;
	}
	
	private static int sendHTTPRequest(String method, String urlString, Object information, StringBuilder responseBuilder) {
		if (information instanceof UserInformation) {
			if (validateUserInformation((UserInformation) information) != 0) {
				return -1;
			}
		} else if (information instanceof FileInformation) {
			// validate method will be called...
		}
		
		String requestBody;
		
		try {
			requestBody = Application.OBJECT_MAPPER.writeValueAsString(information);
		} catch (Exception exception) {
			exception.printStackTrace();
			
			return -3;
		}
		
		return sendHTTPRequest(method, urlString, requestBody, responseBuilder);
	}
	
	private static int sendHTTPRequest(String method, String urlString, String requestBody, StringBuilder responseBuilder) {
		URL url = null;
		
		try {
			url = new URL(urlString);
		} catch (Exception exception) {
			exception.printStackTrace();
			
			return 400;
		}
		
		HttpURLConnection httpURLConnection = null;
		
		try {
			httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setRequestMethod(method);
		} catch (Exception exception) {
			exception.printStackTrace();
			
			return -2;
		}
		
		httpURLConnection.setDoInput(true);
		httpURLConnection.setInstanceFollowRedirects(true);
		httpURLConnection.setUseCaches(false);
		
		if ("POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method)) {
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setRequestProperty("Content-Type", "application/json");
			httpURLConnection.setRequestProperty("Content-Length", "" + requestBody.length());
			
			PrintWriter requestWriter = null;
			
			try {
				requestWriter = new PrintWriter(httpURLConnection.getOutputStream());
			} catch (Exception exception) {
				exception.printStackTrace();
				
				return getHTTPResponseCode(httpURLConnection);
			}
			
			requestWriter.print(requestBody);
			requestWriter.flush();
			requestWriter.close();
		}
		
		Scanner responseScanner = null;
		
		try {
			responseScanner = new Scanner(httpURLConnection.getInputStream());
		} catch (Exception exception) {
			exception.printStackTrace();
			
			return getHTTPResponseCode(httpURLConnection);
		}
		
		while (responseScanner.hasNextLine()) {
			if (responseBuilder == null) {
				System.out.println(responseScanner.nextLine());
			} else {
				responseBuilder.append(responseScanner.nextLine());
			}
		}
		
		responseScanner.close();
		
		return getHTTPResponseCode(httpURLConnection);
	}
	
}