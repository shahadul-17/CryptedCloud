package com.cloud.crypted.server.core.services;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cloud.crypted.server.Application;
import com.cloud.crypted.server.core.DynamicResources;
import com.cloud.crypted.server.core.models.File;
import com.cloud.crypted.server.core.models.FileAccess;
import com.cloud.crypted.server.core.models.FileAccessID;
import com.cloud.crypted.server.core.models.User;
import com.cloud.crypted.server.core.repositories.FileAccessRepository;
import com.cloud.crypted.server.core.repositories.FileRepository;
import com.cloud.crypted.server.core.utilities.StringUtilities;

@Service
public class FileService {
	
	@Autowired
	private FileRepository fileRepository;
	
	@Autowired
	private FileAccessRepository fileAccessRepository;
	
	@Autowired
	private UserService userService;
	
	public boolean exists(String cloudFileID) {
		return fileRepository.existsByCloudFileID(cloudFileID);
	}
	
	public boolean exists(String email, String cloudFileID) {
		User user = userService.getUserInstance(email);
		File file = getFileInstance(cloudFileID);
		
		if (user == null || file == null) {
			return false;
		}
		
		FileAccessID fileAccessID = new FileAccessID(
			user.getUserID(), file.getFileID()
		);
		
		return fileAccessRepository.existsById(fileAccessID);
	}
	
	public File getFileInstance(String cloudFileID) {
		Optional<File> fileContainer = fileRepository.findByCloudFileID(cloudFileID);
		
		if (fileContainer.isPresent()) {
			return fileContainer.get();
		}
		
		return null;
	}
	
	public List<?> get() {
		return fileRepository.findAll();
	}
	
	@SuppressWarnings("unchecked")
	public Map<?, ?> get(String cloudFileID) {
		String errorMessage = null;
		Map<Object, Object> response = null;
		Optional<File> fileContainer = fileRepository.findByCloudFileID(cloudFileID);
		
		if (fileContainer.isPresent()) {
			File file = fileContainer.get();
			
			try {
				String fileInformationJSONString = Application.OBJECT_MAPPER.writeValueAsString(file);
				response = Application.OBJECT_MAPPER.readValue(fileInformationJSONString, Map.class);
			} catch (Exception exception) {
				exception.printStackTrace();
				
				errorMessage = DynamicResources.getErrorMessage("fileInformationProcessingFailed");
			}
		} else {
			errorMessage = DynamicResources.getErrorMessage("fileNotFound");
		}
		
		if (!StringUtilities.isNullOrEmpty(errorMessage)) {
			response = new HashMap<Object, Object>(
					(int) DynamicResources.getConfiguration("collection.minimumCapacity"));
			response.put("errorMessage", errorMessage);
		}
		
		return response;
	}
	
	public Map<?, ?> get(String email, String cloudFileID) {
		String errorMessage = null;
		User user = userService.getUserInstance(email);
		File file = getFileInstance(cloudFileID);
		Map<Object, Object> response = new HashMap<Object, Object>(
				(int) DynamicResources.getConfiguration("collection.initialCapacity"));
		
		if (!StringUtilities.isNullOrEmpty(email) && !StringUtilities.isNullOrEmpty(cloudFileID)) {
			if (user == null) {
				errorMessage = DynamicResources.getErrorMessage("userNotFound");
			}
			
			if (file == null) {
				errorMessage = DynamicResources.getErrorMessage("fileNotFound");
			}
			
			if (StringUtilities.isNullOrEmpty(errorMessage)) {
				FileAccessID fileAccessID = new FileAccessID(
					user.getUserID(), file.getFileID()
				);
				
				Optional<FileAccess> fileAccessContainer = fileAccessRepository.findById(fileAccessID);
				
				if (fileAccessContainer.isPresent()) {
					FileAccess fileAccess = fileAccessContainer.get();
					
					try {
						response.put("userRole", fileAccess.getUserRole());
						response.put("encryptedRandomKey", fileAccess.getEncryptedRandomKey());
						response.put("email", fileAccess.getUser().getEmail());
						response.put("cloudFileID", fileAccess.getFile().getCloudFileID());
						
						return response;
					} catch (Exception exception) {
						exception.printStackTrace();
						
						errorMessage = DynamicResources.getErrorMessage("fileAccessInformationProcessingFailed");
					}
				} else {
					errorMessage = DynamicResources.getErrorMessage("fileNotFound");
				}
			}
		} else if (StringUtilities.isNullOrEmpty(email) && !StringUtilities.isNullOrEmpty(cloudFileID)) {		//delete file access list by cloud file id...
			if (file == null) {
				errorMessage = DynamicResources.getErrorMessage("fileNotFound");
			} else {
				response.put("fileAccessInformationList", populateFileAccessInformationList(
						fileAccessRepository.findByFileAccessIDFileID(file.getFileID())));
			}
		} else if (!StringUtilities.isNullOrEmpty(email) && StringUtilities.isNullOrEmpty(cloudFileID)) {		// delete  file access list by email...
			if (user == null) {
				errorMessage = DynamicResources.getErrorMessage("userNotFound");
			} else {
				response.put("fileAccessInformationList", populateFileAccessInformationList(
						fileAccessRepository.findByFileAccessIDUserID(user.getUserID())));
			}
		} else {
			errorMessage = DynamicResources.getErrorMessage("invalidRequest");
		}
		
		if (!StringUtilities.isNullOrEmpty(errorMessage)) {
			response.put("errorMessage", errorMessage);
		}
		
		return response;
	}
	
	@SuppressWarnings("unchecked")
	public Map<?, ?> post(String email, String cloudFileID, String userRole, String encryptedRandomKey) {
		String errorMessage = null;
		Map<Object, Object> response = null;
		File file = null;
		
		if (!exists(cloudFileID)) {
			file = new File(0, cloudFileID);
			file = fileRepository.save(file);
		}
		
		if (StringUtilities.isNullOrEmpty(errorMessage)) {
			if (exists(email, cloudFileID)) {
				errorMessage = DynamicResources.getErrorMessage("alreadyHasFileAccess");
			} else {
				User user = userService.getUserInstance(email);
				
				if (user == null) {
					errorMessage = DynamicResources.getErrorMessage("userNotFound");
				}
				
				if (file == null) {
					file = getFileInstance(cloudFileID);
				}
				
				FileAccessID fileAccessID = new FileAccessID(
					user.getUserID(), file.getFileID()
				);
				
				FileAccess fileAccess = new FileAccess(fileAccessID);
				fileAccess.setUserRole(userRole);
				fileAccess.setEncryptedRandomKey(encryptedRandomKey);
				fileAccess.setUser(user);
				fileAccess.setFile(file);
				
				fileAccess = fileAccessRepository.save(fileAccess);
				
				try {
					String fileInformationJSONString = Application.OBJECT_MAPPER.writeValueAsString(fileAccess);
					response = Application.OBJECT_MAPPER.readValue(fileInformationJSONString, Map.class);
				} catch (Exception exception) {
					exception.printStackTrace();
					
					errorMessage = DynamicResources.getErrorMessage("fileInformationProcessingFailed");
				}
			}
		}
		
		if (!StringUtilities.isNullOrEmpty(errorMessage)) {
			response = new HashMap<Object, Object>((int) DynamicResources.getConfiguration("collection.minimumCapacity"));
			response.put("errorMessage", errorMessage);
		}
		
		return response;
	}
	
	public void delete(String cloudFileID) {
		fileRepository.deleteByCloudFileID(cloudFileID);
	}
	
	@Transactional
	public Map<?, ?> delete(String email, String cloudFileID) {
		User user = userService.getUserInstance(email);
		File file = getFileInstance(cloudFileID);
		Map<Object, Object> response = new HashMap<Object, Object>((int) DynamicResources.getConfiguration("collection.minimumCapacity"));
		
		if (!StringUtilities.isNullOrEmpty(email) && !StringUtilities.isNullOrEmpty(cloudFileID)) {
			String errorMessage = null;
			
			if (user == null) {
				errorMessage = DynamicResources.getErrorMessage("userNotFound");
			}
			
			if (file == null) {
				errorMessage = DynamicResources.getErrorMessage("fileNotFound");
			}
			
			if (StringUtilities.isNullOrEmpty(errorMessage)) {
				FileAccessID fileAccessID = new FileAccessID(
					user.getUserID(), file.getFileID()
				);
				
				fileAccessRepository.deleteById(fileAccessID);
			} else {
				response.put("errorMessage", errorMessage);
			}
		} else if (StringUtilities.isNullOrEmpty(email) && !StringUtilities.isNullOrEmpty(cloudFileID)) {		//delete file access list by cloud file id...
			if (file == null) {
				response.put("errorMessage", DynamicResources.getErrorMessage("fileNotFound"));
			} else {
				fileAccessRepository.deleteByFileAccessIDFileID(file.getFileID());
				delete(cloudFileID);
			}
		} else if (!StringUtilities.isNullOrEmpty(email) && StringUtilities.isNullOrEmpty(cloudFileID)) {		// delete  file access list by email...
			if (user == null) {
				response.put("errorMessage", DynamicResources.getErrorMessage("userNotFound"));
			} else {
				fileAccessRepository.deleteByFileAccessIDUserID(user.getUserID());
			}
		} else {
			response.put("errorMessage", DynamicResources.getErrorMessage("invalidRequest"));
		}
		
		return response;
	}
	
	private static List<Map<String, String>> populateFileAccessInformationList(
			List<FileAccess> fileAccessList) {
		List<Map<String, String>> fileAccessInformationList = new LinkedList<Map<String,String>>();
		
		for (FileAccess fileAccess : fileAccessList) {
			Map<String, String> fileAccessInformation =
					new HashMap<String, String>((int) DynamicResources.getConfiguration("collection.initialCapacity"));
			fileAccessInformation.put("userRole", fileAccess.getUserRole());
			fileAccessInformation.put("encryptedRandomKey", fileAccess.getEncryptedRandomKey());
			fileAccessInformation.put("email", fileAccess.getUser().getEmail());
			fileAccessInformation.put("cloudFileID", fileAccess.getFile().getCloudFileID());
			fileAccessInformationList.add(fileAccessInformation);
		}
		
		return fileAccessInformationList;
	}

}