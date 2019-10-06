package com.cloud.crypted.client.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.cloud.crypted.client.core.models.CloudFileInformation;

public class CloudFile extends JPanel implements MouseListener {
	
	private static final long serialVersionUID = -2971375579042948715L;
	
	private JLabel labelIcon;
	private JLabel labelFileName;
	
	private CloudFileInformation fileInformation;
	
	private static final Color DODGER_BLUE = new Color(30, 144, 255);
	
	public CloudFile(CloudFileInformation fileInformation) throws Exception {
		this.fileInformation = fileInformation;
		
		initialize();
	}
	
	private void initialize() throws Exception {
		setBackground(Color.WHITE);
		setLayout(new BorderLayout());
		setSize(128, 128);
		setMinimumSize(getSize());
		setMaximumSize(getSize());
		setPreferredSize(getSize());
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		addMouseListener(this);
		setToolTipText();
		
		labelIcon = new JLabel();
		labelIcon.setHorizontalAlignment(SwingConstants.CENTER);
		labelIcon.setIcon(fileInformation.getIcon());
		add(labelIcon, BorderLayout.CENTER);
		
		labelFileName = new JLabel();
		labelFileName.setBorder(new EmptyBorder(0, 5, 0, 5));
		labelFileName.setHorizontalAlignment(SwingConstants.CENTER);
		labelFileName.setText(fileInformation.getName());
		labelFileName.setPreferredSize(new Dimension(0, 32));
		add(labelFileName, BorderLayout.SOUTH);
	}
	
	private void setToolTipText() {
		StringBuilder rowData = new StringBuilder(1024);
		rowData.append("<html>");
		rowData.append(fileInformation.getName());
		rowData.append("<br>");
		rowData.append("Size: ");
		rowData.append(fileInformation.getSizeString());
		rowData.append("<br>");
		rowData.append("Last modified: ");
		rowData.append(fileInformation.getModifiedTimeString());
		rowData.append("</html>");
		
		setToolTipText(rowData.toString());
	}
	
	public void updateCloudFileInformation(CloudFileInformation fileInformation) {
		this.fileInformation.setName(fileInformation.getName());
		this.fileInformation.setSize(fileInformation.getSize());
		this.fileInformation.setModifiedTime(fileInformation.getModifiedTime());
		this.fileInformation.setIcon(fileInformation.getIconURL());
		
		labelIcon.setIcon(fileInformation.getIcon());
		labelFileName.setText(fileInformation.getName());
		setToolTipText();
	}
	
	public CloudFileInformation getCloudFileInformation() {
		return fileInformation;
	}

	@Override
	public void setForeground(Color foregroundColor) {
		super.setForeground(foregroundColor);
		
		if (labelFileName == null) {
			return;
		}
		
		labelFileName.setForeground(foregroundColor);
	}
	
	@Override
	public void mouseClicked(MouseEvent event) {
		CloudFileExplorer fileExplorer = (CloudFileExplorer) getParent();
		
		if (DODGER_BLUE.equals(getBackground())) {
			setBackground(Color.LIGHT_GRAY);
			setForeground(Color.BLACK);
			
			if (fileExplorer != null) {
				fileExplorer.setSelectedFile(null);
			}
		} else {
			setBackground(DODGER_BLUE);
			setForeground(Color.WHITE);
			
			if (fileExplorer != null) {
				CloudFile previouslySelectedFile = fileExplorer.getSelectedFile();
				
				if (previouslySelectedFile != null) {
					previouslySelectedFile.setBackground(Color.WHITE);
					previouslySelectedFile.setForeground(Color.BLACK);
				}
				
				fileExplorer.setSelectedFile(this);
			}
		}
	}
	
	@Override
	public void mouseEntered(MouseEvent event) {
		if (!DODGER_BLUE.equals(getBackground())) {
			setBackground(Color.LIGHT_GRAY);
		}
	}

	@Override
	public void mouseExited(MouseEvent event) {
		if (!DODGER_BLUE.equals(getBackground())) {
			setBackground(Color.WHITE);
		}
	}
	
	@Override
	public void mousePressed(MouseEvent event) { }
	
	@Override
	public void mouseReleased(MouseEvent event) { }
	
}