package com.cloud.crypted.client.ui;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import com.cloud.crypted.client.core.Configuration;
import com.cloud.crypted.client.core.models.CloudFileInformation;
import com.cloud.crypted.client.core.utilities.StringUtilities;

public class CloudFileExplorer extends JPanel implements ActionListener {
	
	private static final long serialVersionUID = 787801250684407410L;
	
	private CloudFile selectedFile = null;
	
	private List<ActionListener> actionListeners;
	private Map<String, CloudFile> fileMap = null;
	
	private static final Color DODGER_BLUE = new Color(30, 144, 255);
	
	public CloudFileExplorer() throws Exception {
		fileMap = new HashMap<String, CloudFile>(Integer.parseInt(Configuration.get("collection.initialCapacity")));
		
		initialize();
	}
	
	private void initialize() throws Exception {
		setLayout(new ExtendedFlowLayout(FlowLayout.LEFT, 5, 5));
	}
	
	public void addActionListener(ActionListener actionListener) {
		if (actionListener == null) {
			return;
		}
		
		if (actionListeners == null) {
			actionListeners = new LinkedList<ActionListener>();
		}
		
		actionListeners.add(actionListener);
	}
	
	public CloudFile getSelectedFile() {
		return selectedFile;
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
		
		CloudFile file = fileMap.get(fileInformation.getID());
		
		if (file == null) {
			try {
				file = new CloudFile(fileInformation);
			} catch (Exception exception) {
				exception.printStackTrace();
				
				return false;
			}
			
			fileMap.put(fileInformation.getID(), file);
		} else {
			file.updateCloudFileInformation(fileInformation);
		}
		
		file.removeActionListener(this);
		file.addActionListener(this);
		
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

	@Override
	public void actionPerformed(ActionEvent event) {
		if ("rightClick".equalsIgnoreCase(event.getActionCommand())) {
			return;
		}
		
		CloudFile cloudFile = (CloudFile) event.getSource();
		
		if (DODGER_BLUE.equals(cloudFile.getBackground())) {
			selectedFile = null;
			
			cloudFile.setBackground(Color.LIGHT_GRAY);
			cloudFile.setForeground(Color.BLACK);
		} else {
			cloudFile.setBackground(DODGER_BLUE);
			cloudFile.setForeground(Color.WHITE);
			
			if (selectedFile != null) {
				selectedFile.setBackground(Color.WHITE);
				selectedFile.setForeground(Color.BLACK);
			}
			
			selectedFile = cloudFile;
			
			/*if (actionListeners != null && "rightClick".equalsIgnoreCase(event.getActionCommand())) {
				event = new ActionEvent(cloudFile, event.getID(), "openSecurityManager");
				
				for (ActionListener actionListener : actionListeners) {
					actionListener.actionPerformed(event);
				}
			}*/
		}
	}
	
}