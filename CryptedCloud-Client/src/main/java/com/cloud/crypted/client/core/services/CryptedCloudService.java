package com.cloud.crypted.client.core.services;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import com.cloud.crypted.client.Application;
import com.cloud.crypted.client.core.Configuration;
import com.cloud.crypted.client.core.models.FileAccessInformation;
import com.cloud.crypted.client.core.models.FileInformation;
import com.cloud.crypted.client.core.models.SecurityQuestionInformation;
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
		
		if (sendHTTPRequest("POST", Configuration.get("service.host") + Configuration.get("title") + "/v" + Configuration.get("version") + '/' + "users", userInformation, responseBuilder) == 200) {
			Map<?, ?> response = null;
			
			try {
				response = Application.OBJECT_MAPPER.readValue(responseBuilder.toString(), Map.class);
			} catch (Exception exception) {
				exception.printStackTrace();
				
				return exception.getMessage();
			}
			
			Object errorMessage = response.get("error");
			
			if (errorMessage == null) {
				Map<?, ?> userInformationMap = (Map<?, ?>) response.get("userInformation");
				
				try {
					userInformation.setUserID((Integer) userInformationMap.get("userID"));
				} catch (Exception exception) {
					exception.printStackTrace();
					
					userInformation.setUserID((Long) userInformationMap.get("userID"));
				}
				
				userInformation.setCloudService((String) userInformationMap.get("cloudService"));
				userInformation.setEmail((String) userInformationMap.get("email"));
				userInformation.setHashedPassphrase((String) userInformationMap.get("hashedPassphrase"));
				userInformation.setEncryptedPassphrase((String) userInformationMap.get("encryptedPassphrase"));
				userInformation.setEncryptedPrivateKey((String) userInformationMap.get("encryptedPrivateKey"));
				userInformation.setPublicKey((String) userInformationMap.get("publicKey"));
				
				List<?> temporarySecurityQuestionInformationList = (List<?>) userInformationMap.get("securityQuestionInformationList");
				List<SecurityQuestionInformation> securityQuestionInformationList = null;
				
				if (temporarySecurityQuestionInformationList != null && temporarySecurityQuestionInformationList.size() != 0) {
					securityQuestionInformationList = new LinkedList<SecurityQuestionInformation>();
					
					for (Object securityQuestionInformationObject : temporarySecurityQuestionInformationList) {
						if (securityQuestionInformationObject == null) {
							continue;
						}
						
						Map<?, ?> securityQuestionInformationMap = (Map<?, ?>) securityQuestionInformationObject;
						SecurityQuestionInformation securityQuestionInformation = new SecurityQuestionInformation();
						Object securityQuestionIDObject = securityQuestionInformationMap.get("securityQuestionID");
						
						long securityQuestionID = 0L;
						
						if (securityQuestionIDObject instanceof Integer) {
							securityQuestionID = (Integer) securityQuestionIDObject;
						} else if (securityQuestionIDObject instanceof Long) {
							securityQuestionID = (Long) securityQuestionIDObject;
						}
						
						securityQuestionInformation.setSecurityQuestionID(securityQuestionID);
						securityQuestionInformation.setQuestion((String) securityQuestionInformationMap.get("question"));
						securityQuestionInformation.setHashedAnswer((String) securityQuestionInformationMap.get("hashedAnswer"));
						securityQuestionInformationList.add((SecurityQuestionInformation) securityQuestionInformation);
					}
				}
				
				userInformation.setSecurityQuestionInformationList(securityQuestionInformationList);
				
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
			
			Object errorMessage = response.get("error");
			
			if (errorMessage == null) {
				Map<?, ?> userInformationMap = (Map<?, ?>) response.get("userInformation");
				
				try {
					userInformation.setUserID((Integer) userInformationMap.get("userID"));
				} catch (Exception exception) {
					exception.printStackTrace();
					
					userInformation.setUserID((Long) userInformationMap.get("userID"));
				}
				
				userInformation.setCloudService((String) userInformationMap.get("cloudService"));
				userInformation.setEmail((String) userInformationMap.get("email"));
				userInformation.setHashedPassphrase((String) userInformationMap.get("hashedPassphrase"));
				userInformation.setEncryptedPassphrase((String) userInformationMap.get("encryptedPassphrase"));
				userInformation.setEncryptedPrivateKey((String) userInformationMap.get("encryptedPrivateKey"));
				userInformation.setPublicKey((String) userInformationMap.get("publicKey"));
				
				List<?> temporarySecurityQuestionInformationList = (List<?>) userInformationMap.get("securityQuestionInformationList");
				List<SecurityQuestionInformation> securityQuestionInformationList = null;
				
				if (temporarySecurityQuestionInformationList != null && temporarySecurityQuestionInformationList.size() != 0) {
					securityQuestionInformationList = new LinkedList<SecurityQuestionInformation>();
					
					for (Object securityQuestionInformationObject : temporarySecurityQuestionInformationList) {
						if (securityQuestionInformationObject == null) {
							continue;
						}
						
						Map<?, ?> securityQuestionInformationMap = (Map<?, ?>) securityQuestionInformationObject;
						SecurityQuestionInformation securityQuestionInformation = new SecurityQuestionInformation();
						Object securityQuestionIDObject = securityQuestionInformationMap.get("securityQuestionID");
						
						long securityQuestionID = 0L;
						
						if (securityQuestionIDObject instanceof Integer) {
							securityQuestionID = (Integer) securityQuestionIDObject;
						} else if (securityQuestionIDObject instanceof Long) {
							securityQuestionID = (Long) securityQuestionIDObject;
						}
						
						securityQuestionInformation.setSecurityQuestionID(securityQuestionID);
						securityQuestionInformation.setQuestion((String) securityQuestionInformationMap.get("question"));
						securityQuestionInformation.setHashedAnswer((String) securityQuestionInformationMap.get("hashedAnswer"));
						securityQuestionInformationList.add((SecurityQuestionInformation) securityQuestionInformation);
					}
				}
				
				userInformation.setSecurityQuestionInformationList(securityQuestionInformationList);
				
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
			
			Object errorMessage = response.get("error");
			
			if (errorMessage == null) {
				Map<?, ?> userInformationMap = (Map<?, ?>) response.get("userInformation");
				UserInformation userInformation = new UserInformation();
				
				try {
					userInformation.setUserID((Integer) userInformationMap.get("userID"));
				} catch (Exception exception) {
					exception.printStackTrace();
					
					userInformation.setUserID((Long) userInformationMap.get("userID"));
				}
				
				userInformation.setCloudService((String) userInformationMap.get("cloudService"));
				userInformation.setEmail((String) userInformationMap.get("email"));
				userInformation.setHashedPassphrase((String) userInformationMap.get("hashedPassphrase"));
				userInformation.setEncryptedPassphrase((String) userInformationMap.get("encryptedPassphrase"));
				userInformation.setEncryptedPrivateKey((String) userInformationMap.get("encryptedPrivateKey"));
				userInformation.setPublicKey((String) userInformationMap.get("publicKey"));
				
				List<?> temporarySecurityQuestionInformationList = (List<?>) userInformationMap.get("securityQuestionInformationList");
				
				System.out.println(temporarySecurityQuestionInformationList);
				
				List<SecurityQuestionInformation> securityQuestionInformationList = null;
				
				if (temporarySecurityQuestionInformationList != null && temporarySecurityQuestionInformationList.size() != 0) {
					securityQuestionInformationList = new LinkedList<SecurityQuestionInformation>();
					
					for (Object securityQuestionInformationObject : temporarySecurityQuestionInformationList) {
						if (securityQuestionInformationObject == null) {
							continue;
						}
						
						Map<?, ?> securityQuestionInformationMap = (Map<?, ?>) securityQuestionInformationObject;
						SecurityQuestionInformation securityQuestionInformation = new SecurityQuestionInformation();
						Object securityQuestionIDObject = securityQuestionInformationMap.get("securityQuestionID");
						
						long securityQuestionID = 0L;
						
						if (securityQuestionIDObject instanceof Integer) {
							securityQuestionID = (Integer) securityQuestionIDObject;
						} else if (securityQuestionIDObject instanceof Long) {
							securityQuestionID = (Long) securityQuestionIDObject;
						}
						
						securityQuestionInformation.setSecurityQuestionID(securityQuestionID);
						securityQuestionInformation.setQuestion((String) securityQuestionInformationMap.get("question"));
						securityQuestionInformation.setHashedAnswer((String) securityQuestionInformationMap.get("hashedAnswer"));
						securityQuestionInformationList.add((SecurityQuestionInformation) securityQuestionInformation);
					}
				}
				
				userInformation.setSecurityQuestionInformationList(securityQuestionInformationList);
				
				return userInformation;
			} else {
				return errorMessage;
			}
		}
		
		return "An error occurred while connecting to " + Configuration.get("title") + " server.";
	}
	
	public Object saveFileInformation(FileInformation fileInformation) {
		StringBuilder responseBuilder = new StringBuilder(Integer.parseInt(Configuration.get("collection.initialCapacity")));
		
		if (sendHTTPRequest("POST", Configuration.get("service.host") + "v" + Configuration.get("version") + '/' + "files", fileInformation, responseBuilder) == 200) {
			Map<?, ?> response = null;
			
			try {
				response = Application.OBJECT_MAPPER.readValue(responseBuilder.toString(), Map.class);
			} catch (Exception exception) {
				exception.printStackTrace();
				
				return exception.getMessage();
			}
			
			Object errorMessage = response.get("error");
			
			if (errorMessage == null) {
				Map<?, ?> fileInformationMap = (Map<?, ?>) response.get("fileInformation");
				fileInformation = new FileInformation();
				
				try {
					fileInformation.setFileID((Integer) fileInformationMap.get("fileID"));
				} catch (Exception exception) {
					exception.printStackTrace();
					
					fileInformation.setFileID((Long) fileInformationMap.get("fileID"));
				}
				
				fileInformation.setCloudFileID((String) fileInformationMap.get("cloudFileID"));
				
				return fileInformation;
			} else {
				return errorMessage;
			}
		}
		
		return "An error occurred while connecting to " + Configuration.get("title") + " server.";
	}
	
	public boolean fileInformationExists(String cloudFileID) {
		StringBuilder responseBuilder = new StringBuilder(Integer.parseInt(Configuration.get("collection.initialCapacity")));
		
		if (sendHTTPRequest("GET", Configuration.get("service.host") + "v" + Configuration.get("version") + '/' + "files?cloudFileID=" + cloudFileID + "&ping=true", (String) null, responseBuilder) == 200) {
			Map<?, ?> response = null;
			
			try {
				response = Application.OBJECT_MAPPER.readValue(responseBuilder.toString(), Map.class);
			} catch (Exception exception) {
				exception.printStackTrace();
				
				return false;
			}
			
			return (Boolean) response.get("ping");
		}
		
		return false;
	}
	
	public Object getFileInformation(String cloudFileID) {
		StringBuilder responseBuilder = new StringBuilder(Integer.parseInt(Configuration.get("collection.initialCapacity")));
		
		if (sendHTTPRequest("GET", Configuration.get("service.host") + "v" + Configuration.get("version") + '/' + "files?cloudFileID=" + cloudFileID, (String) null, responseBuilder) == 200) {
			Map<?, ?> response = null;
			
			try {
				response = Application.OBJECT_MAPPER.readValue(responseBuilder.toString(), Map.class);
			} catch (Exception exception) {
				exception.printStackTrace();
				
				return exception.getMessage();
			}
			
			Object errorMessage = response.get("error");
			
			if (errorMessage == null) {
				Map<?, ?> fileInformationMap = (Map<?, ?>) response.get("fileInformation");
				FileInformation fileInformation = new FileInformation();
				
				try {
					fileInformation.setFileID((Integer) fileInformationMap.get("fileID"));
				} catch (Exception exception) {
					exception.printStackTrace();
					
					fileInformation.setFileID((Long) fileInformationMap.get("fileID"));
				}
				
				fileInformation.setCloudFileID((String) fileInformationMap.get("cloudFileID"));
				
				return fileInformation;
			} else {
				return errorMessage;
			}
		}
		
		return "An error occurred while connecting to " + Configuration.get("title") + " server.";
	}
	
	public Object saveFileAccessInformation(FileAccessInformation fileAccessInformation) {
		StringBuilder responseBuilder = new StringBuilder(Integer.parseInt(Configuration.get("collection.initialCapacity")));
		
		if (sendHTTPRequest("POST", Configuration.get("service.host") + "v" + Configuration.get("version") + '/' + "fileAccessSet", fileAccessInformation, responseBuilder) == 200) {
			Map<?, ?> response = null;
			
			try {
				response = Application.OBJECT_MAPPER.readValue(responseBuilder.toString(), Map.class);
			} catch (Exception exception) {
				exception.printStackTrace();
				
				return exception.getMessage();
			}
			
			Object errorMessage = response.get("error");
			
			if (errorMessage == null) {
				Map<?, ?> fileInformationMap = (Map<?, ?>) response.get("fileAccessInformation");
				
				fileAccessInformation = new FileAccessInformation();
				fileAccessInformation.setEmail((String) fileInformationMap.get("email"));
				fileAccessInformation.setCloudFileID((String) fileInformationMap.get("cloudFileID"));
				fileAccessInformation.setUserRole((String) fileInformationMap.get("userRole"));
				fileAccessInformation.setEncryptedRandomKey((String) fileInformationMap.get("encryptedRandomKey"));
				
				return fileAccessInformation;
			} else {
				return errorMessage;
			}
		}
		
		return "An error occurred while connecting to " + Configuration.get("title") + " server.";
	}
	
	public Object getFileAccessInformation(String email, String cloudFileID) {
		StringBuilder responseBuilder = new StringBuilder(Integer.parseInt(Configuration.get("collection.initialCapacity")));
		
		if (sendHTTPRequest("GET", Configuration.get("service.host") + "v" + Configuration.get("version") + '/' + "fileAccessSet?email=" + email + "&cloudFileID=" + cloudFileID, (String) null, responseBuilder) == 200) {
			Map<?, ?> response = null;
			
			try {
				response = Application.OBJECT_MAPPER.readValue(responseBuilder.toString(), Map.class);
			} catch (Exception exception) {
				exception.printStackTrace();
				
				return exception.getMessage();
			}
			
			Object errorMessage = response.get("error");
			
			if (errorMessage == null) {
				Map<?, ?> fileInformationMap = (Map<?, ?>) response.get("fileAccessInformation");
				
				FileAccessInformation fileAccessInformation = new FileAccessInformation();
				fileAccessInformation.setEmail((String) fileInformationMap.get("email"));
				fileAccessInformation.setCloudFileID((String) fileInformationMap.get("cloudFileID"));
				fileAccessInformation.setUserRole((String) fileInformationMap.get("userRole"));
				fileAccessInformation.setEncryptedRandomKey((String) fileInformationMap.get("encryptedRandomKey"));
				
				return fileAccessInformation;
			} else {
				return errorMessage;
			}
		}
		
		return "An error occurred while connecting to " + Configuration.get("title") + " server.";
	}
	
	public Object getFileAccessInformationSetByEmail(String email) {
		StringBuilder responseBuilder = new StringBuilder(Integer.parseInt(Configuration.get("collection.initialCapacity")));
		
		if (sendHTTPRequest("GET", Configuration.get("service.host") + "v" + Configuration.get("version") + '/' + "fileAccessSet?email=" + email, (String) null, responseBuilder) == 200) {
			Map<?, ?> response = null;
			
			try {
				response = Application.OBJECT_MAPPER.readValue(responseBuilder.toString(), Map.class);
			} catch (Exception exception) {
				exception.printStackTrace();
				
				return exception.getMessage();
			}
			
			Object errorMessage = response.get("error");
			
			if (errorMessage == null) {
				List<?> fileAccessInformationList = (List<?>) response.get("fileAccessInformationSet");
				Set<FileAccessInformation> fileAccessInformationSet = new HashSet<FileAccessInformation>(fileAccessInformationList.size());
				
				for (Object fileAccessInformationObject : fileAccessInformationList) {
					Map<?, ?> fileAccessInformationMap = (Map<?, ?>) fileAccessInformationObject;
					
					FileAccessInformation fileAccessInformation = new FileAccessInformation();
					fileAccessInformation.setEmail((String) fileAccessInformationMap.get("email"));
					fileAccessInformation.setCloudFileID((String) fileAccessInformationMap.get("cloudFileID"));
					fileAccessInformation.setUserRole((String) fileAccessInformationMap.get("userRole"));
					fileAccessInformation.setEncryptedRandomKey((String) fileAccessInformationMap.get("encryptedRandomKey"));
					fileAccessInformationSet.add(fileAccessInformation);
				}
				
				return fileAccessInformationSet;
			} else {
				return errorMessage;
			}
		}
		
		return "An error occurred while connecting to " + Configuration.get("title") + " server.";
	}
	
	public Object getFileAccessInformationSetByCloudFileID(String cloudFileID) {
		StringBuilder responseBuilder = new StringBuilder(Integer.parseInt(Configuration.get("collection.initialCapacity")));
		
		if (sendHTTPRequest("GET", Configuration.get("service.host") + "fileAccessSet?cloudFileID=" + cloudFileID, (String) null, responseBuilder) == 200) {
			Map<?, ?> response = null;
			
			try {
				response = Application.OBJECT_MAPPER.readValue(responseBuilder.toString(), Map.class);
			} catch (Exception exception) {
				exception.printStackTrace();
				
				return exception.getMessage();
			}
			
			Object errorMessage = response.get("error");
			
			if (errorMessage == null) {
				List<?> fileAccessInformationList = (List<?>) response.get("fileAccessInformationSet");
				Set<FileAccessInformation> fileAccessInformationSet = new HashSet<FileAccessInformation>(fileAccessInformationList.size());
				
				for (Object fileAccessInformationObject : fileAccessInformationList) {
					Map<?, ?> fileAccessInformationMap = (Map<?, ?>) fileAccessInformationObject;
					
					FileAccessInformation fileAccessInformation = new FileAccessInformation();
					fileAccessInformation.setEmail((String) fileAccessInformationMap.get("email"));
					fileAccessInformation.setCloudFileID((String) fileAccessInformationMap.get("cloudFileID"));
					fileAccessInformation.setUserRole((String) fileAccessInformationMap.get("userRole"));
					fileAccessInformation.setEncryptedRandomKey((String) fileAccessInformationMap.get("encryptedRandomKey"));
					fileAccessInformationSet.add(fileAccessInformation);
				}
				
				return fileAccessInformationSet;
			} else {
				return errorMessage;
			}
		}
		
		return "An error occurred while connecting to " + Configuration.get("title") + " server.";
	}
	
	public Object deleteFileAccessInformationSet(String cloudFileID) {
		StringBuilder responseBuilder = new StringBuilder(Integer.parseInt(Configuration.get("collection.initialCapacity")));
		
		if (sendHTTPRequest("DELETE", Configuration.get("service.host") + "v" + Configuration.get("version") + '/' + "fileAccessSet?cloudFileID=" + cloudFileID, (String) null, responseBuilder) == 200) {
			Map<?, ?> response = null;
			
			try {
				response = Application.OBJECT_MAPPER.readValue(responseBuilder.toString(), Map.class);
			} catch (Exception exception) {
				exception.printStackTrace();
				
				return exception.getMessage();
			}
			
			Object errorMessage = response.get("error");
			
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
		httpURLConnection.setInstanceFollowRedirects(false);
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