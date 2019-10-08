package com.cloud.crypted.client.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import com.cloud.crypted.client.core.models.CloudFileInformation;
import com.cloud.crypted.client.core.utilities.StringUtilities;

public class SecurityManager extends JFrame implements ActionListener, WindowListener {
	
	private static final long serialVersionUID = -5956231286705954846L;
	
	private JPanel contentPane;
	private JTextField textFieldFileName;
	private JList<String> emailList;
	private JButton buttonRefresh;
	private JButton buttonDelete;
	
	private CloudFileInformation cloudFileInformation;
	
	private List<ActionListener> actionListeners;
	private DefaultListModel<String> emailListModel;
	
	private static Map<String, SecurityManager> securityManagerMap = new HashMap<String, SecurityManager>();
	
	public SecurityManager() throws Exception {
		initialize();
	}
	
	private void initialize() throws Exception {
		setTitle("Security Manager");
		setSize(450, 250);
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		addWindowListener(this);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(0, 5, 5, 1));
		contentPane.setLayout(new BorderLayout());
		setContentPane(contentPane);
		
		JPanel panel = new JPanel();
		panel.setBorder(new EmptyBorder(5, 5, 5, 5));
		panel.setLayout(new BorderLayout(5, 0));
		contentPane.add(panel, BorderLayout.NORTH);
		
		JLabel labelFileName = new JLabel("File name");
		labelFileName.setFont(new Font("Tahoma", Font.PLAIN, 14));
		labelFileName.setHorizontalAlignment(SwingConstants.LEFT);
		panel.add(labelFileName, BorderLayout.WEST);
		
		textFieldFileName = new JTextField();
		textFieldFileName.setFont(new Font("Tahoma", Font.PLAIN, 14));
		textFieldFileName.setEditable(false);
		textFieldFileName.setBackground(Color.WHITE);
		panel.add(textFieldFileName, BorderLayout.CENTER);
		
		JScrollPane scrollPaneEmailList = new JScrollPane();
		contentPane.add(scrollPaneEmailList, BorderLayout.CENTER);
		
		emailListModel = new DefaultListModel<String>();
		
		emailList = new JList<String>(emailListModel);
		emailList.setFont(new Font("Tahoma", Font.PLAIN, 14));
		emailList.setFixedCellHeight(35);
		scrollPaneEmailList.setViewportView(emailList);
		
		JPanel panelControls = new JPanel();
		panelControls.setPreferredSize(new Dimension(75, 0));
		contentPane.add(panelControls, BorderLayout.EAST);
		
		buttonRefresh = new JButton();
		buttonRefresh.setIcon(new ImageIcon(getClass().getResource("/images/refresh.png")));
		buttonRefresh.setToolTipText("Refresh");
		buttonRefresh.addActionListener(this);
		panelControls.add(buttonRefresh);
		
		buttonDelete = new JButton();
		buttonDelete.setIcon(new ImageIcon(getClass().getResource("/images/delete.png")));
		buttonDelete.setToolTipText("Delete");
		buttonDelete.addActionListener(this);
		panelControls.add(buttonDelete);
	}
	
	public CloudFileInformation getCloudFileInformation() {
		return cloudFileInformation;
	}
	
	public void setCloudFileInformation(CloudFileInformation cloudFileInformation) {
		this.cloudFileInformation = cloudFileInformation;
	}
	
	public void buttonsSetEnabled(boolean enabled) {
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				buttonRefresh.setEnabled(enabled);
				buttonDelete.setEnabled(enabled);
			}
		});
	}
	
	public void setFileName(String fileName) {
		textFieldFileName.setText(fileName);
	}
	
	public void setEmailListContent(String[] emailListContent) {
		if (emailListContent == null) {
			return;
		}
		
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				emailListModel.clear();
				
				for (int i = 0; i < emailListContent.length; i++) {
					if (StringUtilities.isNullOrEmpty(emailListContent[i])) {
						continue;
					}
					
					emailListModel.addElement(emailListContent[i]);
				}
				
				contentPane.revalidate();
				contentPane.repaint();
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
		
		JButton button = (JButton) event.getSource();
		String actionCommand = "";
		
		if (button.equals(buttonRefresh)) {
			actionCommand = "refresh";
		} else if (button.equals(buttonDelete)) {
			if (emailList.getSelectedValue() == null) {
				return;
			}
			
			actionCommand = "delete#" + emailList.getSelectedValue();
		}
		
		event = new ActionEvent(this, event.getID(), actionCommand);
		
		for (ActionListener actionListener : actionListeners) {
			actionListener.actionPerformed(event);
		}
	}
	
	public static boolean isAlreadyShown(String cloudFileID) {
		return securityManagerMap.containsKey(cloudFileID);
	}
	
	public static SecurityManager show(String[] emailAddresses,
			CloudFileInformation fileInformation, Object parentComponent) {
		SecurityManager securityManager = null;
		
		try {
			securityManager = new SecurityManager();
		} catch (Exception exception) {
			exception.printStackTrace();
			
			return securityManager;
		}
		
		if (parentComponent instanceof JFrame) {
			securityManager.setIconImage(((JFrame) parentComponent).getIconImage());
		}
		
		securityManager.setFileName(fileInformation.getName());
		securityManager.setEmailListContent(emailAddresses);
		securityManager.setCloudFileInformation(fileInformation);
		securityManager.setLocationRelativeTo((JFrame) parentComponent);
		securityManager.addActionListener((ActionListener) parentComponent);
		securityManager.setVisible(true);
		
		securityManagerMap.put(fileInformation.getID(), securityManager);
		
		return securityManager;
	}

	@Override
	public void windowClosing(WindowEvent event) {
		securityManagerMap.remove(cloudFileInformation.getID());
	}
	
	@Override
	public void windowOpened(WindowEvent event) { }
	
	@Override
	public void windowClosed(WindowEvent event) { }

	@Override
	public void windowIconified(WindowEvent event) { }

	@Override
	public void windowDeiconified(WindowEvent event) { }

	@Override
	public void windowActivated(WindowEvent event) { }

	@Override
	public void windowDeactivated(WindowEvent event) { }
	
}