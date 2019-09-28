package com.cloud.crypted.client.ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import com.cloud.crypted.client.core.Configuration;

public class SignInPanel extends JPanel implements MouseListener, ComponentListener {
	
	private static final long serialVersionUID = -8375992071633206289L;
	
	private JPanel contentPane;
	private JLabel labelErrorMessage;
	private JTextField textFieldEmail;
	private JPasswordField passphraseField;
	private JLabel labelForgotPassphrase;
	private JLabel labelCreateNewAccount;
	private JLabel labelSignInWithDifferentAccount;
	private JButton buttonSignIn;
	
	private ActionListener actionListener;
	
	public SignInPanel() throws Exception {
		initialize();
	}
	
	private void initialize() throws Exception {
		setLayout(null);
		addComponentListener(this);
		
		contentPane = new JPanel();
		contentPane.setLocation(0, 0);
		contentPane.setSize(545, 410);
		contentPane.setBackground(Color.WHITE);
		contentPane.setLayout(null);
		add(contentPane);
		
		JLabel labelCreateAccount = new JLabel("Sign in to your " + Configuration.get("title") + " account");
		labelCreateAccount.setHorizontalAlignment(SwingConstants.LEFT);
		labelCreateAccount.setFont(new Font("Tahoma", Font.PLAIN, 16));
		labelCreateAccount.setBounds(32, 56, 480, 25);
		contentPane.add(labelCreateAccount);
		
		labelErrorMessage = new JLabel();
		labelErrorMessage.setVisible(false);
		labelErrorMessage.setFont(new Font("Tahoma", Font.PLAIN, 14));
		labelErrorMessage.setBorder(new EmptyBorder(5, 5, 5, 5));
		labelErrorMessage.setBounds(72, 106, 400, 30);
		labelErrorMessage.setOpaque(true);
		labelErrorMessage.setBackground(new Color(255, 150, 150));
		contentPane.add(labelErrorMessage);
		
		JLabel labelEmail = new JLabel("Email");
		labelEmail.setFont(new Font("Tahoma", Font.PLAIN, 14));
		labelEmail.setBounds(72, 148, 100, 20);
		contentPane.add(labelEmail);
		
		textFieldEmail = new JTextField();
		textFieldEmail.setEditable(false);
		textFieldEmail.setBackground(Color.WHITE);
		textFieldEmail.setFont(new Font("Tahoma", Font.PLAIN, 14));
		textFieldEmail.setColumns(10);
		textFieldEmail.setBounds(72, 171, 400, 25);
		contentPane.add(textFieldEmail);
		
		JLabel labelPassphrase = new JLabel("Passphrase");
		labelPassphrase.setFont(new Font("Tahoma", Font.PLAIN, 14));
		labelPassphrase.setBounds(72, 206, 100, 20);
		contentPane.add(labelPassphrase);
		
		passphraseField = new JPasswordField("12345678");
		passphraseField.setFont(new Font("Tahoma", Font.PLAIN, 14));
		passphraseField.setColumns(10);
		passphraseField.setBounds(72, 229, 400, 25);
		contentPane.add(passphraseField);
		
		labelForgotPassphrase = new JLabel("<html><u>Forgot passphrase?</u></html>");
		labelForgotPassphrase.setForeground(Color.BLUE);
		labelForgotPassphrase.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		labelForgotPassphrase.setFont(new Font("Tahoma", Font.PLAIN, 16));
		labelForgotPassphrase.setBounds(72, 267, 245, 20);
		labelForgotPassphrase.addMouseListener(this);
		contentPane.add(labelForgotPassphrase);
		
		labelCreateNewAccount = new JLabel("<html><u>Create new " + Configuration.get("title") + " account</u></html>");
		labelCreateNewAccount.setForeground(Color.BLUE);
		labelCreateNewAccount.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		labelCreateNewAccount.setFont(new Font("Tahoma", Font.PLAIN, 16));
		labelCreateNewAccount.setBounds(72, 300, 245, 20);
		labelCreateNewAccount.addMouseListener(this);
		contentPane.add(labelCreateNewAccount);
		
		labelSignInWithDifferentAccount = new JLabel("<html><u>Sign in with a different google drive account</u></html>");
		labelSignInWithDifferentAccount.setForeground(Color.BLUE);
		labelSignInWithDifferentAccount.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		labelSignInWithDifferentAccount.setFont(new Font("Tahoma", Font.PLAIN, 16));
		labelSignInWithDifferentAccount.setBounds(72, 333, 350, 20);
		labelSignInWithDifferentAccount.addMouseListener(this);
		contentPane.add(labelSignInWithDifferentAccount);
		
		buttonSignIn = new JButton("Sign in");
		buttonSignIn.setFont(new Font("Tahoma", Font.PLAIN, 14));
		buttonSignIn.setBounds(372, 262, 100, 30);
		contentPane.add(buttonSignIn);
	}
	
	public void setErrorMessage(String errorMessage) {
		if (errorMessage == null || errorMessage.isEmpty()) {
			labelErrorMessage.setVisible(false);
			
			return;
		}
		
		labelErrorMessage.setText(errorMessage);
		labelErrorMessage.setVisible(true);
	}
	
	public void setEmail(String email) {
		textFieldEmail.setText(email);
	}
	
	public void setActionListener(ActionListener actionListener) {
		if (actionListener == null) {
			return;
		}
		
		this.actionListener = actionListener;
		
		buttonSignIn.addActionListener(actionListener);
	}
	
	public String getEmail() {
		return textFieldEmail.getText().trim();
	}
	
	public char[] getPassphrase() {
		return passphraseField.getPassword();
	}
	
	@Override
	public void mouseClicked(MouseEvent event) {
		if (actionListener == null) {
			return;
		}
		
		String command = "";
		
		if (event.getSource().equals(labelForgotPassphrase)) {
			command = "forgotPassphrase";
		} else if (event.getSource().equals(labelCreateNewAccount)) {
			command = "createNewAccount";
		} else if (event.getSource().equals(labelSignInWithDifferentAccount)) {
			command = "signInWithDifferentAccount";
		}
		
		actionListener.actionPerformed(new ActionEvent(event.getSource(), ActionEvent.ACTION_PERFORMED, command));
	}
	
	@Override
	public void componentResized(ComponentEvent event) {
		int x = (int) ((getWidth() / 2.0) - (contentPane.getWidth() / 2.0));
		int y = (int) ((getHeight() / 2.0) - (contentPane.getHeight() / 2.0));
		
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				contentPane.setLocation(x, y);
			}
		});
	}
	
	@Override
	public void mousePressed(MouseEvent event) { }

	@Override
	public void mouseReleased(MouseEvent event) { }

	@Override
	public void mouseEntered(MouseEvent event) { }

	@Override
	public void mouseExited(MouseEvent event) { }

	@Override
	public void componentMoved(ComponentEvent event) { }

	@Override
	public void componentShown(ComponentEvent event) { }

	@Override
	public void componentHidden(ComponentEvent event) { }
}