package com.cloud.crypted.client.core.services;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.cloud.crypted.client.core.Configuration;
import com.cloud.crypted.client.core.cryptography.AES;
import com.cloud.crypted.client.core.events.GoogleDriveListener;
import com.cloud.crypted.client.core.models.CloudFileInformation;
import com.cloud.crypted.client.core.models.GoogleDriveUser;
import com.cloud.crypted.client.core.utilities.FileUtilities;
import com.cloud.crypted.client.core.utilities.Reflector;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp.Browser;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.googleapis.media.MediaHttpUploaderProgressListener;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files.Create;
import com.google.api.services.drive.Drive.Files.Get;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.Permission;

public class GoogleDriveService implements MediaHttpUploaderProgressListener {
	
	private GoogleDriveUser googleDriveUser = null;
	private Drive drive = null;
	private List<GoogleDriveListener> googleDriveListeners = null;
	
	private static final String APPLICATION_DATA_DIRECTORY_PATH = "application-data";
	private static final String CREDENTIALS_FILE_PATH = "/google-drive-api/credentials.json";
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	
	private static final List<String> SCOPES = new LinkedList<String>();
	
	static {
		SCOPES.add(DriveScopes.DRIVE);
	}
	
	public GoogleDriveService(Browser browser) throws Exception {
		NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
		
		drive = new Drive.Builder(
			httpTransport, JSON_FACTORY, getCredential(httpTransport, browser)
		).setApplicationName(Configuration.get("title")).build();
		
		googleDriveUser = new GoogleDriveUser(drive.about().get().setFields("*").execute().getUser());
	}
	
	private boolean hasGoogleDriveListener() {
		return googleDriveListeners != null & googleDriveListeners.size() != 0;
	}
	
	private void callGoogleDriveListenerMethod(String methodName, Object... arguments) {
		if (!hasGoogleDriveListener()) {
			return;
		}
		
		Method method = Reflector.getMethodByName(GoogleDriveListener.class, methodName, arguments);
		
		if (method == null) {
			System.err.println("Unable to locate the method \"" + methodName + "\".");
			
			return;
		}
		
		for (GoogleDriveListener googleDriveListener : googleDriveListeners) {
			Reflector.callMethod(googleDriveListener, method, arguments);
		}
	}
	
	public GoogleDriveUser getGoogleDriveUser() {
		return googleDriveUser;
	}
	
	public void addGoogleDriveListener(GoogleDriveListener googleDriveListener) {
		if (googleDriveListener == null) {
			return;
		}
		
		if (googleDriveListeners == null) {
			googleDriveListeners = new LinkedList<GoogleDriveListener>();
		}
		
		googleDriveListeners.add(googleDriveListener);
	}
	
	public void retrieveCloudFileInformationList() {
		if (googleDriveListeners == null || googleDriveListeners.isEmpty()) {
			return;
		}
		
		String nextPageToken = null;
		List<CloudFileInformation> cloudFileInformationList = new LinkedList<CloudFileInformation>();
		
		try {
			while ((nextPageToken = retrieveCloudFileInformationList(nextPageToken, drive, cloudFileInformationList)) != null) { }
			
			callGoogleDriveListenerMethod("fileInformationRetrievalSucceeded", cloudFileInformationList);
		} catch (Exception exception) {
			callGoogleDriveListenerMethod("fileInformationRetrievalFailed", exception);
		}
	}
	
	public String upload(char[] passphrase, File localFile) {
		com.google.api.services.drive.model.File file = new com.google.api.services.drive.model.File();
		file.setName(localFile.getName());
		
		File encryptedFile = null;
		
		if (passphrase != null) {
			if (!FileUtilities.createDirectory(Configuration.get("temporaryFilePath"))) {
				callGoogleDriveListenerMethod("uploadFailed", localFile.getName(),
						new Exception("An error occurred while attempting to create temporary directory."));
				
				return "";
			}
			
			try {
				encryptedFile = new File(Configuration.get("temporaryFilePath") + File.separator + localFile.getName());
				
				InputStream inputStream = new FileInputStream(localFile);
				OutputStream outputStream = new FileOutputStream(encryptedFile);
				AES aes = new AES(passphrase, inputStream, outputStream);
				
				byte[] buffer = new byte[Integer.parseInt(Configuration.get("stream.bufferLength"))];
				int bytesRead = 0;
				int progress = 0;
				long totalBytesRead = 0L;
				
				while ((bytesRead = inputStream.read(buffer, 0, buffer.length)) > -1) {
					outputStream.write(aes.process(buffer, 0, bytesRead));
					outputStream.flush();
					
					if (googleDriveListeners == null || googleDriveListeners.isEmpty()) {
						continue;
					}
					
					totalBytesRead += bytesRead;
					progress = (int) (((double) totalBytesRead / localFile.length()) * 100);
					
					callGoogleDriveListenerMethod("encryptionProgressChanged", progress, localFile.getName());
				}
				
				outputStream.write(aes.clinch());
				outputStream.flush();
				outputStream.close();
				
				callGoogleDriveListenerMethod("encryptionSucceeded", localFile.getName());
			} catch (Exception exception) {
				callGoogleDriveListenerMethod("encryptionFailed", localFile.getName(), exception);
				
				return "";
			}
		}
		
		FileContent fileContent = null;
		
		if (passphrase == null) {
			fileContent = new FileContent("*/*", localFile);
		} else {
			fileContent = new FileContent("*/*", encryptedFile);
		}
		
		Create create = null;
		
		try {
			create = drive.files().create(file, fileContent);
		} catch (Exception exception) {
			callGoogleDriveListenerMethod("uploadFailed", localFile.getName(), exception);
			
			return "";
		}
		
		MediaHttpUploader uploader = create.getMediaHttpUploader();
		uploader.setDirectUploadEnabled(false);
		uploader.setChunkSize(MediaHttpUploader.MINIMUM_CHUNK_SIZE);
		uploader.setProgressListener(this);
		
		try {
			file = create.execute();
		} catch (Exception exception) {
			callGoogleDriveListenerMethod("uploadFailed", localFile.getName(), exception);
			
			return "";
		}
		
		return file.getId();
	}
	
	@Override
	public void progressChanged(MediaHttpUploader uploader) throws IOException {
		String fileName = null;
		HttpContent httpContent = uploader.getMetadata();
		
		if (httpContent != null) {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream(1024);
			httpContent.writeTo(outputStream);
			String metadataContent = outputStream.toString();
			outputStream.flush();
			outputStream.close();
			
			Map<?, ?> metadataMap = new ObjectMapper().readValue(metadataContent, Map.class);
			fileName = (String) metadataMap.get("name");
		}
		
		if (fileName == null) {
			fileName = "";
		}
		
		switch (uploader.getUploadState()) {
		case MEDIA_IN_PROGRESS:
			callGoogleDriveListenerMethod("uploadProgressChanged", (int) (uploader.getProgress() * 100), fileName);
			
			break;
		case MEDIA_COMPLETE:
			callGoogleDriveListenerMethod("uploadSucceeded", fileName);
			
			break;
		case NOT_STARTED:
		case INITIATION_STARTED:
		case INITIATION_COMPLETE:
		default:
			break;
		}
	}
	
	public void download(char[] passphrase, String downloadLocation,
			CloudFileInformation cloudFileInformation) {
		if (!FileUtilities.createDirectory(downloadLocation)) {
			callGoogleDriveListenerMethod("downloadFailed", cloudFileInformation.getName(),
					new Exception("An error occurred while attempting to create selected directory."));
			
			return;
		}
		
		try {
			Get get = drive.files().get(cloudFileInformation.getID());
			InputStream inputStream = get.executeMediaAsInputStream();
			OutputStream outputStream = new FileOutputStream(downloadLocation + java.io.File.separator + cloudFileInformation.getName());
			AES aes = null;
			
			if (passphrase != null) {
				aes = new AES(passphrase, inputStream, null);
			}
			
			byte[] buffer = new byte[Integer.parseInt(Configuration.get("stream.bufferLength"))];
			int bytesRead = 0;
			int progress = 0;
			long totalBytesRead = 0L;
			
			while ((bytesRead = inputStream.read(buffer, 0, buffer.length)) > -1) {
				if (aes == null) {
					outputStream.write(buffer, 0, bytesRead);
					outputStream.flush();
				} else {
					outputStream.write(aes.process(buffer, 0, bytesRead));
					outputStream.flush();
				}
				
				if (googleDriveListeners == null || googleDriveListeners.isEmpty()) {
					continue;
				}
				
				totalBytesRead += bytesRead;
				progress = (int) (((double) totalBytesRead / cloudFileInformation.getSize()) * 100);
				
				callGoogleDriveListenerMethod("downloadProgressChanged", progress, cloudFileInformation.getName());
			}
			
			if (aes != null) {
				outputStream.write(aes.clinch());
				outputStream.flush();
			}
			
			inputStream.close();
			outputStream.close();
			
			callGoogleDriveListenerMethod("downloadSucceeded", cloudFileInformation.getID(), cloudFileInformation.getName());
		} catch (Exception exception) {
			callGoogleDriveListenerMethod("downloadFailed", cloudFileInformation.getName(), exception);
		}
	}
	
	public void share(String email, CloudFileInformation cloudFileInformation) {
		Permission permission = new Permission();
		permission.setType("user");
		// owner - organizer - fileOrganizer - writer - commenter - reader
		permission.setRole("writer");
		permission.setEmailAddress(email);
		
		BatchRequest batchRequest = drive.batch();
		
		try {
			drive.permissions().create(cloudFileInformation.getID(), permission).setFields("id").queue(batchRequest, new PermissionCallback());
			batchRequest.execute();
			
			callGoogleDriveListenerMethod("sharingSucceeded", cloudFileInformation.getID(), cloudFileInformation.getName(), email);
		} catch (Exception exception) {
			callGoogleDriveListenerMethod("sharingFailed", exception, email);
		}
	}
	
	public void delete(CloudFileInformation cloudFileInformation) {
		try {
			drive.files().delete(cloudFileInformation.getID()).execute();
			
			callGoogleDriveListenerMethod("deletionSucceeded", cloudFileInformation.getID(), cloudFileInformation.getName());
		} catch (Exception exception) {
			callGoogleDriveListenerMethod("deletionFailed", cloudFileInformation.getName(), exception);
		}
	}
	
	public static void removeCredential() throws Exception {
		new File(APPLICATION_DATA_DIRECTORY_PATH + File.separator + "StoredCredential").delete();
	}
	
	private static Credential getCredential(NetHttpTransport netHttpTransport, Browser browser) throws Exception {
		InputStream inputStream = GoogleDriveService.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
		
		if (inputStream == null) {
			throw new FileNotFoundException("Unable to locate the file: " + CREDENTIALS_FILE_PATH);
		}
		
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
			JSON_FACTORY, new InputStreamReader(inputStream)
		);
		
		GoogleAuthorizationCodeFlow authorizationCodeFlow = new GoogleAuthorizationCodeFlow.Builder(
			netHttpTransport, JSON_FACTORY, clientSecrets, SCOPES
		).setDataStoreFactory(new FileDataStoreFactory(
			new File(APPLICATION_DATA_DIRECTORY_PATH))
		).setAccessType("offline").build();
		
		LocalServerReceiver localServerReceiver = new LocalServerReceiver.Builder().setPort(8888).build();
		AuthorizationCodeInstalledApp authorizationCodeInstalledApp = null;
		
		if (browser == null) {
			authorizationCodeInstalledApp = new AuthorizationCodeInstalledApp(authorizationCodeFlow, localServerReceiver);
		} else {
			authorizationCodeInstalledApp = new AuthorizationCodeInstalledApp(authorizationCodeFlow, localServerReceiver, browser);
		}
		
        return authorizationCodeInstalledApp.authorize("user");
	}
	
	private static String retrieveCloudFileInformationList(String pageToken, Drive drive,
			List<CloudFileInformation> cloudFileInformationList) throws Exception {
		com.google.api.services.drive.Drive.Files.List list = drive.files().list().setQ("mimeType != 'application/vnd.google-apps.folder'");
		
		if (pageToken != null && !"".equals(pageToken)) {
			list = list.setPageToken(pageToken);
		}
		
		FileList fileList = list.setFields("nextPageToken, files(id, name, mimeType, iconLink, modifiedTime, size)").execute();
		List<com.google.api.services.drive.model.File> files = fileList.getFiles();
		
		if (files == null) {
			return null;
		}
		
		for (com.google.api.services.drive.model.File file : files) {
			CloudFileInformation cloudFileInformation = new CloudFileInformation();
			
			if (file.getSize() == null) {
				cloudFileInformation.setSize(0L);
			} else {
				cloudFileInformation.setSize(file.getSize());
			}
			
			cloudFileInformation.setID(file.getId());
			cloudFileInformation.setName(file.getName());
			cloudFileInformation.setIcon(file.getIconLink());
			cloudFileInformation.setModifiedTime(file.getModifiedTime().getValue());
			cloudFileInformationList.add(cloudFileInformation);
		}
		
		return fileList.getNextPageToken();
	}
	
}