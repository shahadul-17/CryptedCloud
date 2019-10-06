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

public class SignUpPanel extends JPanel implements MouseListener, ComponentListener {
	
	private static final long serialVersionUID = 5722754165733218486L;
	
	private JPanel contentPane;
	private JLabel labelErrorMessage;
	private JTextField textFieldFirstName;
	private JTextField textFieldLastName;
	private JTextField textFieldEmail;
	private JPasswordField passphraseField;
	private JPasswordField rePassphraseField;
	private JTextField textFieldFirstSecurityQuestion;
	private JTextField textFieldFirstAnswer;
	private JTextField textFieldSecondSecurityQuestion;
	private JTextField textFieldSecondAnswer;
	private JTextField textFieldThirdSecurityQuestion;
	private JTextField textFieldThirdAnswer;
	private JLabel labelSignInInstead;
	private JButton buttonSignUp;
	
	private ActionListener actionListener;
	
	public SignUpPanel() throws Exception {
		initialize();
	}
	
	private void initialize() throws Exception {
		setLayout(null);
		addComponentListener(this);
		
		contentPane = new JPanel();
		contentPane.setLocation(0, 0);
		contentPane.setSize(565, 580);
		contentPane.setBackground(Color.WHITE);
		contentPane.setLayout(null);
		add(contentPane);
		
		JLabel labelCreateAccount = new JLabel("Create your " + Configuration.get("title") + " account");
		labelCreateAccount.setHorizontalAlignment(SwingConstants.LEFT);
		labelCreateAccount.setFont(new Font("Tahoma", Font.PLAIN, 16));
		labelCreateAccount.setBounds(22, 30, 520, 25);
		contentPane.add(labelCreateAccount);
		
		labelErrorMessage = new JLabel();
		labelErrorMessage.setVisible(false);
		labelErrorMessage.setFont(new Font("Tahoma", Font.PLAIN, 14));
		labelErrorMessage.setBorder(new EmptyBorder(5, 5, 5, 5));
		labelErrorMessage.setBounds(22, 60, 520, 30);
		labelErrorMessage.setOpaque(true);
		labelErrorMessage.setBackground(new Color(255, 150, 150));
		contentPane.add(labelErrorMessage);
		
		JLabel labelFirstName = new JLabel("First name");
		labelFirstName.setFont(new Font("Tahoma", Font.PLAIN, 14));
		labelFirstName.setBounds(22, 96, 100, 20);
		contentPane.add(labelFirstName);
		
		textFieldFirstName = new JTextField();
		textFieldFirstName.setEditable(false);
		textFieldFirstName.setBackground(Color.WHITE);
		textFieldFirstName.setFont(new Font("Tahoma", Font.PLAIN, 14));
		textFieldFirstName.setBounds(22, 119, 255, 25);
		contentPane.add(textFieldFirstName);
		
		JLabel labelLastName = new JLabel("Last name");
		labelLastName.setFont(new Font("Tahoma", Font.PLAIN, 14));
		labelLastName.setBounds(287, 96, 100, 20);
		contentPane.add(labelLastName);
		
		textFieldLastName = new JTextField();
		textFieldLastName.setEditable(false);
		textFieldLastName.setBackground(Color.WHITE);
		textFieldLastName.setFont(new Font("Tahoma", Font.PLAIN, 14));
		textFieldLastName.setBounds(287, 119, 255, 25);
		contentPane.add(textFieldLastName);
		
		JLabel labelEmail = new JLabel("Email");
		labelEmail.setFont(new Font("Tahoma", Font.PLAIN, 14));
		labelEmail.setBounds(22, 154, 100, 20);
		contentPane.add(labelEmail);
		
		textFieldEmail = new JTextField();
		textFieldEmail.setEditable(false);
		textFieldEmail.setBackground(Color.WHITE);
		textFieldEmail.setFont(new Font("Tahoma", Font.PLAIN, 14));
		textFieldEmail.setBounds(22, 177, 520, 25);
		contentPane.add(textFieldEmail);
		
		JLabel labelPassphrase = new JLabel("Passphrase");
		labelPassphrase.setFont(new Font("Tahoma", Font.PLAIN, 14));
		labelPassphrase.setBounds(22, 212, 100, 20);
		contentPane.add(labelPassphrase);
		
		passphraseField = new JPasswordField();
		passphraseField.setFont(new Font("Tahoma", Font.PLAIN, 14));
		passphraseField.setBounds(22, 235, 520, 25);
		contentPane.add(passphraseField);
		
		JLabel labelRePassphrase = new JLabel("Re-passphrase");
		labelRePassphrase.setFont(new Font("Tahoma", Font.PLAIN, 14));
		labelRePassphrase.setBounds(22, 270, 100, 20);
		contentPane.add(labelRePassphrase);
		
		rePassphraseField = new JPasswordField();
		rePassphraseField.setFont(new Font("Tahoma", Font.PLAIN, 14));
		rePassphraseField.setBounds(22, 293, 520, 25);
		contentPane.add(rePassphraseField);
		
		JLabel labelFirstSecurityQuestion = new JLabel("Security question 1");
		labelFirstSecurityQuestion.setFont(new Font("Tahoma", Font.PLAIN, 14));
		labelFirstSecurityQuestion.setBounds(22, 331, 195, 20);
		contentPane.add(labelFirstSecurityQuestion);
		
		textFieldFirstSecurityQuestion = new JTextField("What was the name of your first pet?");
		textFieldFirstSecurityQuestion.setFont(new Font("Tahoma", Font.PLAIN, 14));
		textFieldFirstSecurityQuestion.setBackground(Color.WHITE);
		textFieldFirstSecurityQuestion.setBounds(22, 354, 255, 25);
		contentPane.add(textFieldFirstSecurityQuestion);
		
		JLabel labelFirstAnswer = new JLabel("Answer 1");
		labelFirstAnswer.setFont(new Font("Tahoma", Font.PLAIN, 14));
		labelFirstAnswer.setBounds(287, 331, 100, 20);
		contentPane.add(labelFirstAnswer);
		
		textFieldFirstAnswer = new JTextField();
		textFieldFirstAnswer.setFont(new Font("Tahoma", Font.PLAIN, 14));
		textFieldFirstAnswer.setBackground(Color.WHITE);
		textFieldFirstAnswer.setBounds(287, 354, 255, 25);
		contentPane.add(textFieldFirstAnswer);
		
		JLabel labelSecondSecurityQuestion = new JLabel("Security question 2");
		labelSecondSecurityQuestion.setFont(new Font("Tahoma", Font.PLAIN, 14));
		labelSecondSecurityQuestion.setBounds(22, 392, 195, 20);
		contentPane.add(labelSecondSecurityQuestion);
		
		textFieldSecondSecurityQuestion = new JTextField("Where did your parents meet?");
		textFieldSecondSecurityQuestion.setFont(new Font("Tahoma", Font.PLAIN, 14));
		textFieldSecondSecurityQuestion.setBackground(Color.WHITE);
		textFieldSecondSecurityQuestion.setBounds(22, 415, 255, 25);
		contentPane.add(textFieldSecondSecurityQuestion);
		
		JLabel labelSecondAnswer = new JLabel("Answer 2");
		labelSecondAnswer.setFont(new Font("Tahoma", Font.PLAIN, 14));
		labelSecondAnswer.setBounds(287, 392, 100, 20);
		contentPane.add(labelSecondAnswer);
		
		textFieldSecondAnswer = new JTextField();
		textFieldSecondAnswer.setFont(new Font("Tahoma", Font.PLAIN, 14));
		textFieldSecondAnswer.setBackground(Color.WHITE);
		textFieldSecondAnswer.setBounds(287, 415, 255, 25);
		contentPane.add(textFieldSecondAnswer);
		
		JLabel labelThirdSecurityQuestion = new JLabel("Security question 3");
		labelThirdSecurityQuestion.setFont(new Font("Tahoma", Font.PLAIN, 14));
		labelThirdSecurityQuestion.setBounds(22, 453, 195, 20);
		contentPane.add(labelThirdSecurityQuestion);
		
		textFieldThirdSecurityQuestion = new JTextField("What primary school did you attend?");
		textFieldThirdSecurityQuestion.setFont(new Font("Tahoma", Font.PLAIN, 14));
		textFieldThirdSecurityQuestion.setBackground(Color.WHITE);
		textFieldThirdSecurityQuestion.setBounds(22, 476, 255, 25);
		contentPane.add(textFieldThirdSecurityQuestion);
		
		JLabel labelThirdAnswer = new JLabel("Answer 3");
		labelThirdAnswer.setFont(new Font("Tahoma", Font.PLAIN, 14));
		labelThirdAnswer.setBounds(287, 453, 100, 20);
		contentPane.add(labelThirdAnswer);
		
		textFieldThirdAnswer = new JTextField();
		textFieldThirdAnswer.setFont(new Font("Tahoma", Font.PLAIN, 14));
		textFieldThirdAnswer.setBackground(Color.WHITE);
		textFieldThirdAnswer.setBounds(287, 476, 255, 25);
		contentPane.add(textFieldThirdAnswer);
		
		labelSignInInstead = new JLabel("<html><u>Sign in instead</u></html>");
		labelSignInInstead.setForeground(Color.BLUE);
		labelSignInInstead.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		labelSignInInstead.setFont(new Font("Tahoma", Font.PLAIN, 16));
		labelSignInInstead.setBounds(22, 514, 150, 20);
		labelSignInInstead.addMouseListener(this);
		contentPane.add(labelSignInInstead);
		
		buttonSignUp = new JButton("Sign up");
		buttonSignUp.setFont(new Font("Tahoma", Font.PLAIN, 14));
		buttonSignUp.setBounds(442, 514, 100, 30);
		contentPane.add(buttonSignUp);
	}
	
	public void setErrorMessage(String errorMessage) {
		if (errorMessage == null || errorMessage.isEmpty()) {
			labelErrorMessage.setVisible(false);
			
			return;
		}
		
		labelErrorMessage.setText(errorMessage);
		labelErrorMessage.setVisible(true);
	}
	
	public void setFirstName(String firstName) {
		textFieldFirstName.setText(firstName);
	}
	
	public void setLastName(String lastName) {
		textFieldLastName.setText(lastName);
	}
	
	public String getEmail() {
		return textFieldEmail.getText().trim();
	}
	
	public void setEmail(String email) {
		textFieldEmail.setText(email);
	}
	
	public char[] getPassphrase() {
		return passphraseField.getPassword();
	}
	
	public char[] getRePassphrase() {
		return rePassphraseField.getPassword();
	}
	
	public String[] getSecurityQuestionAndAnswerArray() {
		return new String[] {
			textFieldFirstSecurityQuestion.getText(), textFieldFirstAnswer.getText(),
			textFieldSecondSecurityQuestion.getText(), textFieldSecondAnswer.getText(),
			textFieldThirdSecurityQuestion.getText(), textFieldThirdAnswer.getText()
		};
	}
	
	public void setActionListener(ActionListener actionListener) {
		if (actionListener == null) {
			return;
		}
		
		this.actionListener = actionListener;
		
		buttonSignUp.addActionListener(actionListener);
	}
	
	@Override
	public void mouseClicked(MouseEvent event) {
		if (actionListener == null) {
			return;
		}
		
		actionListener.actionPerformed(new ActionEvent(event.getSource(), ActionEvent.ACTION_PERFORMED, "signInInstead"));
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