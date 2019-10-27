package com.cloud.crypted.client.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import com.cloud.crypted.client.Application;
import com.cloud.crypted.client.core.Configuration;
import com.cloud.crypted.client.core.events.GoogleDriveListener;
import com.cloud.crypted.client.core.events.TaskListener;
import com.cloud.crypted.client.core.models.CloudFileInformation;
import com.cloud.crypted.client.core.models.RecoveryInformation;
import com.cloud.crypted.client.core.models.Task;
import com.cloud.crypted.client.core.models.UserInformation;
import com.cloud.crypted.client.core.services.BackgroundTask;
import com.cloud.crypted.client.core.services.GoogleDriveService;
import com.cloud.crypted.client.core.utilities.StringUtilities;
import com.cloud.crypted.client.ui.events.WebViewListener;

public class Frame extends JFrame implements GoogleDriveListener, TaskListener, WebViewListener,
											 ActionListener, WindowListener {
	
	private static final long serialVersionUID = -6343358214662738587L;
	
	private JPanel contentPane = null;
	
	private GIF gifLoading = null;
	private WebView webView = null;
	private SignInPanel signInPanel = null;
	private SignUpPanel signUpPanel = null;
	private AccountRecoveryPanel accountRecoveryPanel = null;
	private CloudServicePanel cloudServicePanel = null;
	
	private JPanel panelStatus = null;
	private JLabel labelStatus = null;
	private JProgressBar progressBarStatus = null;
	
	private static final Color DODGER_BLUE = new Color(30, 144, 255);
	private static final Color ORANGE_RED = new Color(255, 69, 0);
	
	private UserInformation userInformation = null;
	private GoogleDriveService googleDriveService = null;
	
	public Frame() throws Exception {
		initialize();
		authenticate();
	}
	
	private void initialize() throws Exception {
		setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/images/icon.png")));
		setTitle(Configuration.get("title") + " v" + Configuration.get("version"));
		setSize(1007, 700);
		setMinimumSize(getSize());
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		addWindowListener(this);
		
		contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		contentPane.setLayout(new BorderLayout());
		setContentPane(contentPane);
		
		gifLoading = new GIF("loading");
		contentPane.add(gifLoading, BorderLayout.CENTER);
		
		webView = new WebView();
		webView.addWebViewListener(this);
		
		cloudServicePanel = new CloudServicePanel();
		cloudServicePanel.addActionListener(this);
		
		signInPanel = new SignInPanel();
		signInPanel.setActionListener(this);
		
		signUpPanel = new SignUpPanel();
		signUpPanel.setActionListener(this);
		
		accountRecoveryPanel = new AccountRecoveryPanel();
		accountRecoveryPanel.setActionListener(this);
		
		panelStatus = new JPanel();
		panelStatus.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		panelStatus.setLayout(new BorderLayout());
		panelStatus.setBackground(DODGER_BLUE);
		contentPane.add(panelStatus, BorderLayout.SOUTH);
		
		labelStatus = new JLabel("Please wait...");
		labelStatus.setForeground(Color.WHITE);
		labelStatus.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		labelStatus.setHorizontalAlignment(SwingConstants.LEFT);
		panelStatus.add(labelStatus, BorderLayout.CENTER);
		
		progressBarStatus = new JProgressBar();
		progressBarStatus.setStringPainted(true);
		panelStatus.add(progressBarStatus, BorderLayout.EAST);
	}
	
	private void changeContentPaneComponent(Component component) {
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				Component[] components = contentPane.getComponents();
				
				for (int i = 0; i < components.length; i++) {
					Object constraints = ((BorderLayout) contentPane.getLayout()).getConstraints(components[i]);
					
					if (BorderLayout.CENTER.equals(constraints)) {
						contentPane.remove(components[i]);
					}
				}
				
				contentPane.add(component, BorderLayout.CENTER);
				contentPane.revalidate();
				contentPane.repaint();
			}
		});
	}
	
	private void setStatusText(boolean successStatus, String text) {
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				if (successStatus) {
					panelStatus.setBackground(DODGER_BLUE);
				} else {
					panelStatus.setBackground(ORANGE_RED);
				}
				
				labelStatus.setText(text);
			}
		});
	}
	
	private void setProgressBarStatusValue(int value) {
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				if (value == -1) {
					progressBarStatus.setStringPainted(false);
					progressBarStatus.setIndeterminate(true);
				} else {
					progressBarStatus.setStringPainted(true);
					progressBarStatus.setIndeterminate(false);
					progressBarStatus.setValue(value);
				}
			}
		});
	}
	
	private void authenticate() {
		Task authenticationTask = new BackgroundTask("authenticate");
		authenticationTask.setParameters(webView);
		authenticationTask.addTaskListener(this);
		
		Application.taskExecutor.addTask(authenticationTask);
	}
	
	private void signIn() {
		changeContentPaneComponent(gifLoading);
		setStatusText(true, "Please wait while we are signing you in...");
		setProgressBarStatusValue(-1);
		
		Task signInTask = new BackgroundTask("signIn");
		signInTask.setParameters(signInPanel.getEmail(), signInPanel.getPassphrase(), Application.CRYPTED_CLOUD_SERVICE);
		signInTask.addTaskListener(this);
		
		Application.taskExecutor.addTask(signInTask);
	}
	
	private void signInWithDifferentGoogleDriveAccount() {
		Task signInWithDifferentGoogleDriveAccountTask = new BackgroundTask("signInWithDifferentGoogleDriveAccount");
		signInWithDifferentGoogleDriveAccountTask.setParameters(webView);
		signInWithDifferentGoogleDriveAccountTask.addTaskListener(this);
		
		Application.taskExecutor.addTask(signInWithDifferentGoogleDriveAccountTask);
	}
	
	private void signUp() {
		changeContentPaneComponent(gifLoading);
		setStatusText(true, "Please wait while we are signing you up...");
		setProgressBarStatusValue(-1);
		
		Task signUpTask = new BackgroundTask("signUp");
		signUpTask.setParameters(signUpPanel.getEmail(),
			signUpPanel.getPassphrase(), signUpPanel.getRePassphrase(),
			signUpPanel.getSecurityQuestionAndAnswerArray(),
			Application.CRYPTED_CLOUD_SERVICE
		);
		signUpTask.addTaskListener(this);
		
		Application.taskExecutor.addTask(signUpTask);
	}
	
	private void retrieveUserInformation() {
		changeContentPaneComponent(gifLoading);
		setStatusText(true, "Please wait...");
		setProgressBarStatusValue(-1);
		
		Task userInformationRetrievalTask = new BackgroundTask("retrieveUserInformation");
		userInformationRetrievalTask.setParameters(googleDriveService.getGoogleDriveUser().getEmail(),
			Application.CRYPTED_CLOUD_SERVICE
		);
		userInformationRetrievalTask.addTaskListener(this);
		
		Application.taskExecutor.addTask(userInformationRetrievalTask);
	}
	
	private void accountRecoveryFirstPhase() {
		changeContentPaneComponent(gifLoading);
		setStatusText(true, "Please wait while your recovery information is validated...");
		setProgressBarStatusValue(-1);
		
		Task accountRecoveryTask = new BackgroundTask("accountRecoveryFirstPhase");
		accountRecoveryTask.setParameters(accountRecoveryPanel.getAnswers(), userInformation);
		accountRecoveryTask.addTaskListener(this);
		
		Application.taskExecutor.addTask(accountRecoveryTask);
	}
	
	private void accountRecoverySecondPhase() {
		changeContentPaneComponent(gifLoading);
		setStatusText(true, "Please wait while your recovery information is validated...");
		setProgressBarStatusValue(-1);
		
		Task accountRecoveryTask = new BackgroundTask("accountRecoverySecondPhase");
		accountRecoveryTask.setParameters(accountRecoveryPanel.getNewPassphrase(),
			accountRecoveryPanel.getRePassphrase(),
			accountRecoveryPanel.getAnswers(),
			userInformation, Application.CRYPTED_CLOUD_SERVICE
		);
		accountRecoveryTask.addTaskListener(this);
		
		Application.taskExecutor.addTask(accountRecoveryTask);
	}
	
	private void signOut() {
		Application.passphrase = null;
		
		changeContentPaneComponent(signInPanel);
		setStatusText(true, "You have been signed out.");
		setProgressBarStatusValue(0);
	}
	
	private void refresh() {
		cloudServicePanel.buttonsSetEnabled(false);
		setStatusText(true, "Retrieving file information list from cloud.");
		setProgressBarStatusValue(-1);
		
		Task refreshTask = new BackgroundTask("refresh");
		refreshTask.setParameters(googleDriveService);
		refreshTask.addTaskListener(this);
		
		Application.taskExecutor.addTask(refreshTask);
	}
	
	private void upload() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setMultiSelectionEnabled(false);
		
		if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();
			
			cloudServicePanel.buttonsSetEnabled(false);
			setStatusText(true, "Preparing to upload...");
			setProgressBarStatusValue(-1);
			
			Task uploadTask = new BackgroundTask("upload");
			uploadTask.setParameters(Application.passphrase, selectedFile, Application.CRYPTED_CLOUD_SERVICE, googleDriveService);
			uploadTask.addTaskListener(this);
			
			Application.taskExecutor.addTask(uploadTask);
		}
	}
	
	private void download() {
		CloudFileInformation cloudFileInformation = cloudServicePanel.getSelectedCloudFileInformation();
		
		if (cloudFileInformation == null) {
			return;
		}
		
		JFileChooser fileChooser = new JFileChooser("." + File.separator + "Downloads");
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			cloudServicePanel.buttonsSetEnabled(false);
			setStatusText(true, "Preparing to download...");
			setProgressBarStatusValue(-1);
			
			Task downloadTask = new BackgroundTask("download");
			downloadTask.setParameters(Application.passphrase,
				fileChooser.getSelectedFile().getAbsolutePath(),
				cloudFileInformation,
				Application.CRYPTED_CLOUD_SERVICE, googleDriveService
			);
			downloadTask.addTaskListener(this);
			
			Application.taskExecutor.addTask(downloadTask);
		}
	}
	
	private void share(String emailToShare, AccessControlManager accessControlManager) {
		cloudServicePanel.buttonsSetEnabled(false);
		accessControlManager.buttonsSetEnabled(false);
		setStatusText(true, "Preparing to share...");
		setProgressBarStatusValue(-1);
		
		Task sharingTask = new BackgroundTask("share");
		sharingTask.setParameters(googleDriveService.getGoogleDriveUser().getEmail(),
				emailToShare, accessControlManager,
				Application.CRYPTED_CLOUD_SERVICE, googleDriveService);
		sharingTask.addTaskListener(this);
		
		Application.taskExecutor.addTask(sharingTask);
	}
	
	private void delete() {
		CloudFileInformation cloudFileInformation = cloudServicePanel.getSelectedCloudFileInformation();
		
		if (cloudFileInformation == null) {
			return;
		}
		
		if (JOptionPane.showConfirmDialog(this,
				"Are you sure you want to delete \"" + cloudFileInformation.getName() + "\"?",
				"Delete - " + cloudFileInformation.getName(), JOptionPane.WARNING_MESSAGE) == JOptionPane.OK_OPTION) {
			cloudServicePanel.buttonsSetEnabled(false);
			setStatusText(true, "Preparing to delete \"" + cloudFileInformation.getName() + "\" from cloud.");
			setProgressBarStatusValue(-1);
			
			Task deletionTask = new BackgroundTask("delete");
			deletionTask.setParameters(cloudFileInformation, Application.CRYPTED_CLOUD_SERVICE, googleDriveService);
			deletionTask.addTaskListener(this);
			
			Application.taskExecutor.addTask(deletionTask);
		}
	}
	
	private void requestForOpeningAccessControlManager() {
		CloudFileInformation cloudFileInformation = cloudServicePanel.getSelectedCloudFileInformation();
		
		if (cloudFileInformation == null || AccessControlManager.isAlreadyShown(cloudFileInformation.getID())) {
			return;
		}
		
		cloudServicePanel.buttonsSetEnabled(false);
		setStatusText(true, "Preparing to open access control manager...");
		setProgressBarStatusValue(-1);
		
		Task openAccessControlManagerTask = new BackgroundTask("requestForOpeningAccessControlManager");
		openAccessControlManagerTask.setParameters(
			googleDriveService.getGoogleDriveUser().getEmail(),
			cloudServicePanel.getSelectedCloudFileInformation(),
			Application.CRYPTED_CLOUD_SERVICE
		);
		openAccessControlManagerTask.addTaskListener(this);
		
		Application.taskExecutor.addTask(openAccessControlManagerTask);
	}
	
	private void requestForRevokingFileAccess(String email, AccessControlManager accessControlManager) {
		cloudServicePanel.buttonsSetEnabled(false);
		accessControlManager.buttonsSetEnabled(false);
		
		setStatusText(true, "Preparing to revoke access...");
		setProgressBarStatusValue(-1);
		
		Task revokeAccessTask = new BackgroundTask("revokeAccess");
		revokeAccessTask.setParameters(
			googleDriveService.getGoogleDriveUser().getEmail(),
			email,
			accessControlManager,
			Application.CRYPTED_CLOUD_SERVICE,
			googleDriveService
		);
		revokeAccessTask.addTaskListener(this);
		
		Application.taskExecutor.addTask(revokeAccessTask);
	}
	
	private void openAccessControlManager(String[] emailAddresses, CloudFileInformation fileInformation) {
		setStatusText(true, "Access control information retrieved successfully...");
		setProgressBarStatusValue(100);
		cloudServicePanel.buttonsSetEnabled(true);
		
		AccessControlManager.show(emailAddresses, fileInformation, this);
	}
	
	private void refreshAccessControlInformation(AccessControlManager accessControlManager) {
		cloudServicePanel.buttonsSetEnabled(false);
		accessControlManager.buttonsSetEnabled(false);
		setStatusText(true, "Please wait while access control information is being refreshed...");
		setProgressBarStatusValue(-1);
		
		Task refreshAccessControlInformationTask = new BackgroundTask("refreshAccessControlInformation");
		refreshAccessControlInformationTask.setParameters(
			googleDriveService.getGoogleDriveUser().getEmail(),
			accessControlManager,
			Application.CRYPTED_CLOUD_SERVICE
		);
		refreshAccessControlInformationTask.addTaskListener(this);
		
		Application.taskExecutor.addTask(refreshAccessControlInformationTask);
	}
	
	private void refreshAccessControlInformation(String[] emailAddresses,
			AccessControlManager accessControlManager) {
		accessControlManager.setEmailListContent(emailAddresses);
		accessControlManager.buttonsSetEnabled(true);
		cloudServicePanel.buttonsSetEnabled(true);
		
		setStatusText(true, "Access control information refreshed successfully.");
		setProgressBarStatusValue(100);
	}
	
	private void fileAccessRevocationSucceeded(String[] emailAddresses,
			AccessControlManager accessControlManager) {
		accessControlManager.setEmailListContent(emailAddresses);
		accessControlManager.buttonsSetEnabled(true);
		cloudServicePanel.buttonsSetEnabled(true);
		
		setStatusText(true, "Successfully revoked access for the specified user.");
		setProgressBarStatusValue(100);
	}
	
	/*
	 * 
	 * GoogleDriveListener methods...
	 * 
	 */
	@Override
	public void fileInformationRetrievalSucceeded(List<CloudFileInformation> cloudFileInformationList) {
		cloudServicePanel.addFiles(cloudFileInformationList);
		setStatusText(true, "File list retrieved successfully.");
		setProgressBarStatusValue(100);
		cloudServicePanel.buttonsSetEnabled(true);
		changeContentPaneComponent(cloudServicePanel);
	}
	
	@Override
	public void fileInformationRetrievalFailed(Exception exception) {
		exception.printStackTrace();
		
		setStatusText(false, exception.getMessage());
		setProgressBarStatusValue(0);
		cloudServicePanel.buttonsSetEnabled(true);
		changeContentPaneComponent(cloudServicePanel);
	}
	
	@Override
	public void encryptionProgressChanged(int progress, String fileName) {
		setStatusText(true, "Encrypting \"" + fileName + "\"");
		setProgressBarStatusValue(progress);
	}

	@Override
	public void encryptionSucceeded(String fileName) {
		System.out.println("Encryption succeeded...");
	}

	@Override
	public void encryptionFailed(String fileName, Exception exception) {
		exception.printStackTrace();
		
		setStatusText(false, "An error occurred while encrypting \"" + fileName + "\".");
		setProgressBarStatusValue(0);
		cloudServicePanel.buttonsSetEnabled(true);
	}
	
	@Override
	public void uploadProgressChanged(int progress, String fileName) {
		setStatusText(true, "Uploading \"" + fileName + "\"");
		setProgressBarStatusValue(progress);
	}
	
	@Override
	public void uploadSucceeded(String fileName) {
		setStatusText(true, "File \"" + fileName + "\" uploaded successfully.");
		setProgressBarStatusValue(100);
		cloudServicePanel.buttonsSetEnabled(true);
		
		refresh();
	}

	@Override
	public void uploadFailed(String fileName, Exception exception) {
		exception.printStackTrace();
		
		setStatusText(false, "An error occurred while uploading \"" + fileName + "\".");
		setProgressBarStatusValue(0);
		cloudServicePanel.buttonsSetEnabled(true);
	}
	
	@Override
	public void downloadProgressChanged(int progress, String fileName) {
		setStatusText(true, "Downloading \"" + fileName + "\"");
		setProgressBarStatusValue(progress);
	}
	
	@Override
	public void downloadSucceeded(String cloudFileID, String fileName) {
		setStatusText(true, "File \"" + fileName + "\" downloaded successfully.");
		setProgressBarStatusValue(100);
		cloudServicePanel.buttonsSetEnabled(true);
	}

	@Override
	public void downloadFailed(String fileName, Exception exception) {
		exception.printStackTrace();
		
		setStatusText(false, "An error occurred while downloading \"" + fileName + "\".");
		setProgressBarStatusValue(0);
		cloudServicePanel.buttonsSetEnabled(true);
	}
	
	@Override
	public void sharingSucceeded(String cloudFileID, String fileName, String email) {
		System.out.println("File sharing succeeded.");
	}
	
	@Override
	public void sharingFailed(String fileName, String email, Exception exception) {
		exception.printStackTrace();
		
		setStatusText(false, "An error occurred while sharing \"" + fileName + "\" with \"" + email + "\".");
		setProgressBarStatusValue(0);
		cloudServicePanel.buttonsSetEnabled(true);
	}
	
	@Override
	public void deletionSucceeded(String cloudFileID, String fileName) {
		cloudServicePanel.removeCloudFile(cloudFileID);
		
		setStatusText(true, "File \"" + fileName + "\" deleted successfully.");
		setProgressBarStatusValue(100);
		cloudServicePanel.buttonsSetEnabled(true);
	}
	
	@Override
	public void deletionFailed(String fileName, Exception exception) {
		exception.printStackTrace();
		
		setStatusText(false, "An error occurred while deleting \"" + fileName + "\".");
		setProgressBarStatusValue(0);
		cloudServicePanel.buttonsSetEnabled(true);
	}
	
	/*
	 * 
	 * TaskListener methods...
	 * 
	 */
	@Override
	public void executionSucceeded(Task task, Object ... results) {
		if ("authenticate".equalsIgnoreCase(task.getName()) ||
				"signInWithDifferentGoogleDriveAccount".equalsIgnoreCase(task.getName())) {
			googleDriveService = (GoogleDriveService) results[0];
			googleDriveService.addGoogleDriveListener(this);
			
			setStatusText(true, "Successfully logged in to google drive service.");
			signInPanel.setEmail(googleDriveService.getGoogleDriveUser().getEmail());
			changeContentPaneComponent(signInPanel);
			setProgressBarStatusValue(100);
			cloudServicePanel.buttonsSetEnabled(true);
		} else if ("signIn".equalsIgnoreCase(task.getName())) {
			Application.passphrase = (char[]) task.getParameters()[1];
			
			signInPanel.setErrorMessage(null);
			setStatusText(true, "Successfully signed you in.");
			
			googleDriveService.retrieveCloudFileInformationList();
		} else if ("signUp".equalsIgnoreCase(task.getName())) {
			signUpPanel.setErrorMessage(null);
			signInPanel.setErrorMessage(null);
			
			setStatusText(true, Configuration.get("title") + " registration successful.");
			setProgressBarStatusValue(100);
			changeContentPaneComponent(signInPanel);
		} else if ("retrieveUserInformation".equalsIgnoreCase(task.getName())) {
			userInformation = (UserInformation) results[0];
			
			// FAILED DUE TO NO RECOVERY INFORMATION...
			if (userInformation.getRecoveryInformationList() == null ||
					userInformation.getRecoveryInformationList().size() == 0) {
				signInPanel.setErrorMessage("No account recovery information associated with your account.");
				
				setStatusText(false, "No account recovery information associated with your account.");
				setProgressBarStatusValue(0);
				changeContentPaneComponent(signInPanel);
				
				return;
			}
			
			String[] securityQuestions = new String[3];
			
			int i = 0;
			
			for (RecoveryInformation recoveryInformation : userInformation.getRecoveryInformationList()) {
				securityQuestions[i] = recoveryInformation.getQuestion();
				
				i++;
			}
			
			accountRecoveryPanel.setErrorMessage(null);
			accountRecoveryPanel.setEmail(googleDriveService.getGoogleDriveUser().getEmail());
			accountRecoveryPanel.setSecurityQuestions(securityQuestions);
			accountRecoveryPanel.clearAnswers();
			accountRecoveryPanel.setContentPane(0);
			
			changeContentPaneComponent(accountRecoveryPanel);
		} else if ("accountRecoveryFirstPhase".equalsIgnoreCase(task.getName())) {
			setStatusText(true, "Please enter a new passphrase...");
			setProgressBarStatusValue(-1);
			accountRecoveryPanel.setErrorMessage(null);
			accountRecoveryPanel.setContentPane(1);
			changeContentPaneComponent(accountRecoveryPanel);
		} else if ("accountRecoverySecondPhase".equalsIgnoreCase(task.getName())) {
			userInformation = null;
			
			setStatusText(true, "Account recovery succeeded.");
			setProgressBarStatusValue(100);
			changeContentPaneComponent(signInPanel);
		} else if ("refresh".equalsIgnoreCase(task.getName())) {
			System.out.println(task.getName() + " completed.");
		} else if ("upload".equalsIgnoreCase(task.getName())) {
			System.out.println(task.getName() + " completed.");
		} else if ("download".equalsIgnoreCase(task.getName())) {
			System.out.println(task.getName() + " completed.");
		} else if ("share".equalsIgnoreCase(task.getName())) {
			String fileName = (String) results[0];
			String email = (String) results[1];
			String[] emailAddresses = (String[]) results[2];
			AccessControlManager accessControlManager = (AccessControlManager) results[3];
			
			accessControlManager.setEmailListContent(emailAddresses);
			setStatusText(true, "Successfully shared \"" + fileName + "\" with \"" + email + "\".");
			setProgressBarStatusValue(100);
			cloudServicePanel.buttonsSetEnabled(true);
			accessControlManager.buttonsSetEnabled(true);
		} else if ("delete".equalsIgnoreCase(task.getName())) {
			System.out.println(task.getName() + " completed.");
		} else if ("requestForOpeningAccessControlManager".equalsIgnoreCase(task.getName())) {
			String[] emailAddresses = (String[]) results[0];
			CloudFileInformation fileInformation = (CloudFileInformation) results[1];
			
			openAccessControlManager(emailAddresses, fileInformation);
		} else if ("refreshAccessControlInformation".equalsIgnoreCase(task.getName())) {
			String[] emailAddresses = (String[]) results[0];
			AccessControlManager accessControlManager = (AccessControlManager) results[1];
			
			refreshAccessControlInformation(emailAddresses, accessControlManager);
		} else if ("revokeAccess".equalsIgnoreCase(task.getName())) {
			String[] emailAddresses = (String[]) results[0];
			AccessControlManager accessControlManager = (AccessControlManager) results[1];
			
			fileAccessRevocationSucceeded(emailAddresses, accessControlManager);
		}
	}

	@Override
	public void executionFailed(Task task, Exception exception, Object ... results) {
		exception.printStackTrace();
		
		if ("authenticate".equalsIgnoreCase(task.getName()) ||
				"signInWithDifferentGoogleDriveAccount".equalsIgnoreCase(task.getName())) {
			// changeContentPaneComponent(gifLoading);
			signInWithDifferentGoogleDriveAccount();
		} else if ("signIn".equalsIgnoreCase(task.getName())) {
			signInPanel.setErrorMessage(exception.getMessage());
			
			changeContentPaneComponent(signInPanel);
			setStatusText(false, "An error occurred while we were signing you in...");
			setProgressBarStatusValue(0);
		} else if ("signUp".equalsIgnoreCase(task.getName())) {
			signUpPanel.setErrorMessage(exception.getMessage());
			
			changeContentPaneComponent(signUpPanel);
			setStatusText(false, "An error occurred while we were signing you up...");
			setProgressBarStatusValue(0);
		} else if ("retrieveUserInformation".equalsIgnoreCase(task.getName())) {
			signInPanel.setErrorMessage(exception.getMessage());
			
			changeContentPaneComponent(signInPanel);
			setStatusText(false, "An error occurred...");
			setProgressBarStatusValue(0);
		} else if ("accountRecoveryFirstPhase".equalsIgnoreCase(task.getName())) {
			accountRecoveryPanel.setErrorMessage("An error occurred during recovery information validation.");
			accountRecoveryPanel.setContentPane(0);
			
			changeContentPaneComponent(accountRecoveryPanel);
			setStatusText(false, exception.getMessage());
			setProgressBarStatusValue(0);
		} else if ("accountRecoverySecondPhase".equalsIgnoreCase(task.getName())) {
			accountRecoveryPanel.setErrorMessage("An error occurred while setting your new passphrase.");
			accountRecoveryPanel.setContentPane(1);
			
			changeContentPaneComponent(accountRecoveryPanel);
			setStatusText(false, exception.getMessage());
			setProgressBarStatusValue(0);
		} else if ("refresh".equalsIgnoreCase(task.getName())) {
			setStatusText(false, exception.getMessage());
			setProgressBarStatusValue(0);
			cloudServicePanel.buttonsSetEnabled(true);
		} else if ("upload".equalsIgnoreCase(task.getName())) {
			setStatusText(false, exception.getMessage());
			setProgressBarStatusValue(0);
			cloudServicePanel.buttonsSetEnabled(true);
		} else if ("download".equalsIgnoreCase(task.getName())) {
			setStatusText(false, exception.getMessage());
			setProgressBarStatusValue(0);
			cloudServicePanel.buttonsSetEnabled(true);
		} else if ("share".equalsIgnoreCase(task.getName())) {
			AccessControlManager accessControlManager = (AccessControlManager) results[0];
			
			setStatusText(false, exception.getMessage());
			setProgressBarStatusValue(0);
			cloudServicePanel.buttonsSetEnabled(true);
			accessControlManager.buttonsSetEnabled(true);
		} else if ("delete".equalsIgnoreCase(task.getName())) {
			setStatusText(false, exception.getMessage());
			setProgressBarStatusValue(0);
			cloudServicePanel.buttonsSetEnabled(true);
		} else if ("requestForOpeningAccessControlManager".equalsIgnoreCase(task.getName())) {
			setStatusText(false, exception.getMessage());
			setProgressBarStatusValue(0);
			cloudServicePanel.buttonsSetEnabled(true);
		} else if ("refreshAccessControlInformation".equalsIgnoreCase(task.getName())) {
			AccessControlManager accessControlManager = (AccessControlManager) results[0];
			
			setStatusText(false, exception.getMessage());
			setProgressBarStatusValue(0);
			cloudServicePanel.buttonsSetEnabled(true);
			accessControlManager.buttonsSetEnabled(true);
		} else if ("revokeAccess".equalsIgnoreCase(task.getName())) {
			AccessControlManager accessControlManager = (AccessControlManager) results[0];
			
			setStatusText(false, exception.getMessage());
			setProgressBarStatusValue(0);
			cloudServicePanel.buttonsSetEnabled(true);
			accessControlManager.buttonsSetEnabled(true);
		}
	}
	
	/*
	 * 
	 * WebViewListener methods...
	 * 
	 */
	@Override
	public void urlRequested(String url) {
		setProgressBarStatusValue(-1);
		setStatusText(true, "Waiting for you to sign in...");
		changeContentPaneComponent(webView);
	}
	
	/*@Override
	public void locationChanged(String newLocation) {
		if (StringUtilities.isNullOrEmpty(newLocation)) {
			return;
		}
		
		newLocation = newLocation.toLowerCase();
		
		if (newLocation.startsWith("http://localhost:8888/callback")) {
			webView.release();		// releases additional resources occupied by WebView...
			changeContentPaneComponent(gifLoading);
		}
	}*/
	
	@Override
	public void documentContentChanged(String documentURL, String documentContent) {
		if (StringUtilities.isNullOrEmpty(documentURL)) {
			return;
		}
		
		documentURL = documentURL.toLowerCase();
		
		if (documentURL.startsWith("http://localhost:8888/callback")) {
			changeContentPaneComponent(gifLoading);
			
			if (!StringUtilities.isNullOrEmpty(documentContent) &&
					documentContent.contains("received verification code")) {
				System.err.println(documentContent);
			}
		}
	}
	
	/*
	 * 
	 * ActionListener methods...
	 * 
	 */
	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getSource() instanceof JLabel) {
			if ("forgotPassphrase".equalsIgnoreCase(event.getActionCommand())) {
				retrieveUserInformation();
				
				return;
			}
			
			Component component = null;
			
			if ("createNewAccount".equalsIgnoreCase(event.getActionCommand())) {
				component = signUpPanel;
				
				signUpPanel.setErrorMessage(null);
				signUpPanel.setFirstName(googleDriveService.getGoogleDriveUser().getFirstName());
				signUpPanel.setLastName(googleDriveService.getGoogleDriveUser().getLastName());
				signUpPanel.setEmail(googleDriveService.getGoogleDriveUser().getEmail());
			} else if ("signInWithDifferentAccount".equalsIgnoreCase(event.getActionCommand())) {
				component = gifLoading;
				
				signInWithDifferentGoogleDriveAccount();
			} else if ("signInInstead".equalsIgnoreCase(event.getActionCommand())) {
				userInformation = null;
				component = signInPanel;
				
				signInPanel.setErrorMessage(null);
				signInPanel.setEmail(googleDriveService.getGoogleDriveUser().getEmail());
			}
			
			changeContentPaneComponent(component);
		} else if (event.getSource() instanceof JButton) {
			JButton button = (JButton) event.getSource();
			
			if ("sign in".equalsIgnoreCase(button.getText())) {
				signIn();
			} else if ("sign up".equalsIgnoreCase(button.getText())) {
				signUp();
			} else if ("next".equalsIgnoreCase(button.getText())) {
				accountRecoveryFirstPhase();
			} else if ("finish".equalsIgnoreCase(button.getText())) {
				accountRecoverySecondPhase();
			}
		} else if (event.getSource() instanceof Button) {
			Button button = (Button) event.getSource();
			
			if ("refresh".equalsIgnoreCase(button.getText())) {
				refresh();
			} else if ("upload".equalsIgnoreCase(button.getText())) {
				upload();
			} else if ("download".equalsIgnoreCase(button.getText())) {
				download();
			} else if ("share".equalsIgnoreCase(button.getText())) {
				requestForOpeningAccessControlManager();
			} else if ("delete".equalsIgnoreCase(button.getText())) {
				delete();
			} else if ("sign out".equalsIgnoreCase(button.getText())) {
				signOut();
			}
		} /*else if (event.getSource() instanceof CloudFile) {
			if ("openSecurityManager".equalsIgnoreCase(event.getActionCommand()) &&
					!progressBarStatus.isIndeterminate()) {
				requestForOpeningSecurityManager();
			}
		}*/ else if (event.getSource() instanceof AccessControlManager) {
			AccessControlManager accessControlManager = (AccessControlManager) event.getSource();
			
			if ("refresh".equalsIgnoreCase(event.getActionCommand())) {
				refreshAccessControlInformation(accessControlManager);
			} else if (event.getActionCommand().startsWith("share")) {
				int indexOfHashCharacter = event.getActionCommand().indexOf('#');
				
				if (indexOfHashCharacter == -1) {
					return;
				}
				
				String email = event.getActionCommand().substring(indexOfHashCharacter + 1);
				
				share(email, accessControlManager);
			} else if (event.getActionCommand().startsWith("delete")) {
				int indexOfHashCharacter = event.getActionCommand().indexOf('#');
				
				if (indexOfHashCharacter == -1) {
					return;
				}
				
				String email = event.getActionCommand().substring(indexOfHashCharacter + 1);
				
				requestForRevokingFileAccess(email, accessControlManager);
			}
		}
	}
	
	/*
	 * 
	 * WindowListener methods...
	 * 
	 */
	@Override
	public void windowClosing(WindowEvent event) {
		Application.running = false;
	}
	
	/*
	 * Unused WindowListener methods...
	 */
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