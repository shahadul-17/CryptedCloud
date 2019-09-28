package com.cloud.crypted.client.ui;

import java.awt.FlowLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

import com.cloud.crypted.client.core.Configuration;
import com.cloud.crypted.client.core.models.CloudFileInformation;
import com.cloud.crypted.client.core.utilities.StringUtilities;

public class CloudFileExplorer extends JPanel {
	
	private static final long serialVersionUID = 787801250684407410L;
	
	private Map<String, CloudFile> fileMap = null;
	
	private CloudFile selectedFile = null;
	
	public CloudFileExplorer() throws Exception {
		fileMap = new HashMap<String, CloudFile>(Integer.parseInt(Configuration.get("collection.initialCapacity")));
		
		initialize();
	}
	
	private void initialize() throws Exception {
		setLayout(new ExtendedFlowLayout(FlowLayout.LEFT, 5, 5));
	}
	
	public CloudFile getSelectedFile() {
		return selectedFile;
	}
	
	public void setSelectedFile(CloudFile selectedFile) {
		this.selectedFile = selectedFile;
	}
	
	public synchronized CloudFileInformation getCloudFileInformationByID(String cloudFileID) {
		if (StringUtilities.isNullOrEmpty(cloudFileID)) {
			return null;
		}
		
		CloudFile file = fileMap.get(cloudFileID);
		
		if (file == null) {
			return null;
		}
		
		return file.getCloudFileInformation();
	}
	
	public synchronized boolean addFile(CloudFileInformation fileInformation) {
		if (fileInformation == null) {
			return false;
		}
		
		CloudFile file = null;
		
		try {
			file = new CloudFile(fileInformation);
		} catch (Exception exception) {
			exception.printStackTrace();
			
			return false;
		}
		
		fileMap.put(fileInformation.getID(), file);
		add(file);
		
		return true;
	}
	
	public synchronized boolean removeFile(String cloudFileID) {
		if (StringUtilities.isNullOrEmpty(cloudFileID)) {
			return false;
		}
		
		CloudFile file = fileMap.remove(cloudFileID);
		
		if (file == null) {
			return false;
		}
		
		remove(file);
		
		return true;
	}
	
	public synchronized void removeFiles() {
		fileMap.clear();
		removeAll();
	}
	
}