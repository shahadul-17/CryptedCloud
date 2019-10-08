package com.cloud.crypted.client.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import com.cloud.crypted.client.core.models.CloudFileInformation;

public class CloudServicePanel extends JPanel implements ActionListener, ComponentListener {
	
	private static final long serialVersionUID = -6575164711554236126L;
	
	private JPanel spacer;
	private CloudFileExplorer fileExplorer;
	private JScrollPane scrollPaneFileTable;
	
	private Button buttonRefresh;
	private Button buttonUpload;
	private Button buttonDownload;
	private Button buttonShare;
	private Button buttonDelete;
	private Button buttonSignOut;
	
	private List<ActionListener> actionListeners = null;
	
	public CloudServicePanel() throws Exception {
		initialize();
	}
	
	private void initialize() throws Exception {
		setLayout(new BorderLayout());
		addComponentListener(this);
		
		scrollPaneFileTable = new JScrollPane();
		scrollPaneFileTable.getVerticalScrollBar().setUnitIncrement(25);
		scrollPaneFileTable.getHorizontalScrollBar().setUnitIncrement(25);
		scrollPaneFileTable.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPaneFileTable.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		add(scrollPaneFileTable, BorderLayout.CENTER);
		
		fileExplorer = new CloudFileExplorer();
		fileExplorer.addActionListener(this);
		scrollPaneFileTable.setViewportView(fileExplorer);
		
		JPanel panelControls = new JPanel();
		panelControls.setBackground(Color.WHITE);
		((FlowLayout) panelControls.getLayout()).setAlignment(FlowLayout.LEFT);
		panelControls.setPreferredSize(new Dimension(168, 0));
		add(panelControls, BorderLayout.WEST);
		
		buttonRefresh = createButton("refresh.png", "Refresh", "Refresh");
		buttonRefresh.setEnabled(true);
		buttonRefresh.addActionListener(this);
		panelControls.add(buttonRefresh);
		
		buttonUpload = createButton("upload.png", "Upload", "Upload");
		buttonUpload.setEnabled(true);
		buttonUpload.addActionListener(this);
		panelControls.add(buttonUpload);
		
		buttonDownload = createButton("download.png", "Download", "Download");
		buttonDownload.setEnabled(true);
		buttonDownload.addActionListener(this);
		panelControls.add(buttonDownload);
		
		buttonShare = createButton("share.png", "Share", "Share");
		buttonShare.setEnabled(true);
		buttonShare.addActionListener(this);
		panelControls.add(buttonShare);
		
		buttonDelete = createButton("delete.png", "Delete", "Delete");
		buttonDelete.setEnabled(true);
		buttonDelete.addActionListener(this);
		panelControls.add(buttonDelete);
		
		spacer = new JPanel();
		spacer.setOpaque(false);
		panelControls.add(spacer);
		
		buttonSignOut = createButton("sign_out.png", "Sign out", "Sign out");
		buttonSignOut.setEnabled(true);
		buttonSignOut.addActionListener(this);
		panelControls.add(buttonSignOut);
	}
	
	public void buttonsSetEnabled(boolean enabled) {
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				buttonRefresh.setEnabled(enabled);
				buttonUpload.setEnabled(enabled);
				buttonDownload.setEnabled(enabled);
				buttonShare.setEnabled(enabled);
				buttonDelete.setEnabled(enabled);
				buttonSignOut.setEnabled(enabled);
			}
		});
	}
	
	public CloudFileInformation getCloudFileInformationByID(String cloudFileID) {
		return fileExplorer.getCloudFileInformationByID(cloudFileID);
	}
	
	public void removeCloudFile(String cloudFileID) {
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				fileExplorer.removeFile(cloudFileID);
			}
		});
	}
	
	public CloudFileInformation getSelectedCloudFileInformation() {
		CloudFile file = fileExplorer.getSelectedFile();
		
		if (file == null) {
			return null;
		}
		
		return file.getCloudFileInformation();
	}
	
	public void addFiles(List<CloudFileInformation> fileInformationList) {
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				for (CloudFileInformation fileInformation : fileInformationList) {
					fileExplorer.addFile(fileInformation);
				}
				
				buttonsSetEnabled(true);
			}
		});
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
	
	@Override
	public void actionPerformed(ActionEvent event) {
		if (actionListeners == null) {
			return;
		}
		
		for (ActionListener actionListener : actionListeners) {
			actionListener.actionPerformed(event);
		}
	}
	
	private static Button createButton(String iconName, String text, String toolTipText) {
		Button button = new Button();
		button.setIcon(iconName);
		button.setText(text);
		button.setToolTipText(toolTipText);
		
		return button;
	}

	@Override
	public void componentResized(ComponentEvent event) {
		int width = getSize().width;
		int height = getSize().height - buttonSignOut.getHeight() - 250;
		
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				spacer.setPreferredSize(new Dimension(width, height));
				spacer.getParent().revalidate();
				spacer.getParent().repaint();
			}
		});
	}

	@Override
	public void componentMoved(ComponentEvent event) { }

	@Override
	public void componentShown(ComponentEvent event) { }

	@Override
	public void componentHidden(ComponentEvent event) { }
	
}