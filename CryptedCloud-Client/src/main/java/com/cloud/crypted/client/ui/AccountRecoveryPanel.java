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
import com.cloud.crypted.client.core.utilities.StringUtilities;

public class AccountRecoveryPanel extends JPanel implements MouseListener, ComponentListener {
	
	private static final long serialVersionUID = 5722754165733218486L;
	
	private JPanel[] contentPanes;
	private JLabel labelErrorMessage;
	private JTextField textFieldEmail;
	private JPasswordField newPassphraseField;
	private JPasswordField rePassphraseField;
	private JTextField[] textFieldSecurityQuestions;
	private JTextField[] textFieldAnswers;
	private JLabel labelSignInInstead;
	private JButton buttonNext;
	private JButton buttonFinish;
	
	private ActionListener actionListener;
	
	public AccountRecoveryPanel() throws Exception {
		initialize();
	}
	
	private void initialize() throws Exception {
		setLayout(null);
		addComponentListener(this);
		
		contentPanes = new JPanel[2];
		
		for (int i = 0; i < contentPanes.length; i++) {
			contentPanes[i] = new JPanel();
			contentPanes[i].setLocation(0, 0);
			contentPanes[i].setSize(565, 410);
			contentPanes[i].setBackground(Color.WHITE);
			contentPanes[i].setLayout(null);
		}
		
		add(contentPanes[0]);
		
		JLabel labelRecoverAccount = new JLabel("Recover your " + Configuration.get("title") + " account");
		labelRecoverAccount.setHorizontalAlignment(SwingConstants.LEFT);
		labelRecoverAccount.setFont(new Font("Tahoma", Font.PLAIN, 16));
		labelRecoverAccount.setBounds(22, 30, 520, 25);
		contentPanes[0].add(labelRecoverAccount);
		
		labelErrorMessage = new JLabel();
		labelErrorMessage.setVisible(false);
		labelErrorMessage.setFont(new Font("Tahoma", Font.PLAIN, 14));
		labelErrorMessage.setBorder(new EmptyBorder(5, 5, 5, 5));
		labelErrorMessage.setBounds(22, 60, 520, 30);
		labelErrorMessage.setOpaque(true);
		labelErrorMessage.setBackground(new Color(255, 150, 150));
		contentPanes[0].add(labelErrorMessage);
		
		JLabel labelEmail = new JLabel("Email");
		labelEmail.setFont(new Font("Tahoma", Font.PLAIN, 14));
		labelEmail.setBounds(22, 103, 100, 20);
		contentPanes[0].add(labelEmail);
		
		textFieldEmail = new JTextField();
		textFieldEmail.setEditable(false);
		textFieldEmail.setBackground(Color.WHITE);
		textFieldEmail.setFont(new Font("Tahoma", Font.PLAIN, 14));
		textFieldEmail.setColumns(10);
		textFieldEmail.setBounds(22, 126, 520, 25);
		contentPanes[0].add(textFieldEmail);
		
		JLabel labelFirstSecurityQuestion = new JLabel("Security question 1");
		labelFirstSecurityQuestion.setFont(new Font("Tahoma", Font.PLAIN, 14));
		labelFirstSecurityQuestion.setBounds(22, 164, 195, 20);
		contentPanes[0].add(labelFirstSecurityQuestion);
		
		textFieldSecurityQuestions = new JTextField[3];
		
		textFieldSecurityQuestions[0] = new JTextField();
		textFieldSecurityQuestions[0].setEditable(false);
		textFieldSecurityQuestions[0].setFont(new Font("Tahoma", Font.PLAIN, 14));
		textFieldSecurityQuestions[0].setBackground(Color.WHITE);
		textFieldSecurityQuestions[0].setBounds(22, 187, 255, 25);
		contentPanes[0].add(textFieldSecurityQuestions[0]);
		
		JLabel labelFirstAnswer = new JLabel("Answer 1");
		labelFirstAnswer.setFont(new Font("Tahoma", Font.PLAIN, 14));
		labelFirstAnswer.setBounds(287, 164, 100, 20);
		contentPanes[0].add(labelFirstAnswer);
		
		textFieldAnswers = new JTextField[3];
		
		textFieldAnswers[0] = new JTextField();
		textFieldAnswers[0].setFont(new Font("Tahoma", Font.PLAIN, 14));
		textFieldAnswers[0].setColumns(10);
		textFieldAnswers[0].setBackground(Color.WHITE);
		textFieldAnswers[0].setBounds(287, 187, 255, 25);
		contentPanes[0].add(textFieldAnswers[0]);
		
		JLabel labelSecondSecurityQuestion = new JLabel("Security question 2");
		labelSecondSecurityQuestion.setFont(new Font("Tahoma", Font.PLAIN, 14));
		labelSecondSecurityQuestion.setBounds(22, 225, 195, 20);
		contentPanes[0].add(labelSecondSecurityQuestion);
		
		textFieldSecurityQuestions[1] = new JTextField();
		textFieldSecurityQuestions[1].setEditable(false);
		textFieldSecurityQuestions[1].setFont(new Font("Tahoma", Font.PLAIN, 14));
		textFieldSecurityQuestions[1].setBackground(Color.WHITE);
		textFieldSecurityQuestions[1].setBounds(22, 248, 255, 25);
		contentPanes[0].add(textFieldSecurityQuestions[1]);
		
		JLabel labelSecondAnswer = new JLabel("Answer 2");
		labelSecondAnswer.setFont(new Font("Tahoma", Font.PLAIN, 14));
		labelSecondAnswer.setBounds(287, 225, 100, 20);
		contentPanes[0].add(labelSecondAnswer);
		
		textFieldAnswers[1] = new JTextField();
		textFieldAnswers[1].setFont(new Font("Tahoma", Font.PLAIN, 14));
		textFieldAnswers[1].setColumns(10);
		textFieldAnswers[1].setBackground(Color.WHITE);
		textFieldAnswers[1].setBounds(287, 248, 255, 25);
		contentPanes[0].add(textFieldAnswers[1]);
		
		JLabel labelThirdSecurityQuestion = new JLabel("Security question 3");
		labelThirdSecurityQuestion.setFont(new Font("Tahoma", Font.PLAIN, 14));
		labelThirdSecurityQuestion.setBounds(22, 286, 195, 20);
		contentPanes[0].add(labelThirdSecurityQuestion);
		
		textFieldSecurityQuestions[2] = new JTextField();
		textFieldSecurityQuestions[2].setEditable(false);
		textFieldSecurityQuestions[2].setFont(new Font("Tahoma", Font.PLAIN, 14));
		textFieldSecurityQuestions[2].setBackground(Color.WHITE);
		textFieldSecurityQuestions[2].setBounds(22, 309, 255, 25);
		contentPanes[0].add(textFieldSecurityQuestions[2]);
		
		JLabel labelThirdAnswer = new JLabel("Answer 3");
		labelThirdAnswer.setFont(new Font("Tahoma", Font.PLAIN, 14));
		labelThirdAnswer.setBounds(287, 286, 100, 20);
		contentPanes[0].add(labelThirdAnswer);
		
		textFieldAnswers[2] = new JTextField();
		textFieldAnswers[2].setFont(new Font("Tahoma", Font.PLAIN, 14));
		textFieldAnswers[2].setColumns(10);
		textFieldAnswers[2].setBackground(Color.WHITE);
		textFieldAnswers[2].setBounds(287, 309, 255, 25);
		contentPanes[0].add(textFieldAnswers[2]);
		
		labelSignInInstead = new JLabel("<html><u>Sign in instead</u></html>");
		labelSignInInstead.setForeground(Color.BLUE);
		labelSignInInstead.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		labelSignInInstead.setFont(new Font("Tahoma", Font.PLAIN, 16));
		labelSignInInstead.setBounds(22, 347, 150, 20);
		labelSignInInstead.addMouseListener(this);
		contentPanes[0].add(labelSignInInstead);
		
		buttonNext = new JButton("Next");
		buttonNext.setFont(new Font("Tahoma", Font.PLAIN, 14));
		buttonNext.setBounds(442, 347, 100, 30);
		contentPanes[0].add(buttonNext);
		
		JLabel labelEnterNewPassphrase = new JLabel("Enter new passphrase for your " + Configuration.get("title") + " account");
		labelEnterNewPassphrase.setHorizontalAlignment(SwingConstants.LEFT);
		labelEnterNewPassphrase.setFont(new Font("Tahoma", Font.PLAIN, 16));
		labelEnterNewPassphrase.setBounds(22, 30, 520, 25);
		contentPanes[1].add(labelEnterNewPassphrase);
		
		JLabel labelNewPassphrase = new JLabel("New passphrase");
		labelNewPassphrase.setFont(new Font("Tahoma", Font.PLAIN, 14));
		labelNewPassphrase.setBounds(22, 150, 110, 20);
		contentPanes[1].add(labelNewPassphrase);
		
		newPassphraseField = new JPasswordField();
		newPassphraseField.setFont(new Font("Tahoma", Font.PLAIN, 14));
		newPassphraseField.setColumns(10);
		newPassphraseField.setBounds(22, 173, 520, 25);
		contentPanes[1].add(newPassphraseField);
		
		JLabel labelRePassphrase = new JLabel("Re-passphrase");
		labelRePassphrase.setFont(new Font("Tahoma", Font.PLAIN, 14));
		labelRePassphrase.setBounds(22, 208, 100, 20);
		contentPanes[1].add(labelRePassphrase);
		
		rePassphraseField = new JPasswordField();
		rePassphraseField.setFont(new Font("Tahoma", Font.PLAIN, 14));
		rePassphraseField.setColumns(10);
		rePassphraseField.setBounds(22, 231, 520, 25);
		contentPanes[1].add(rePassphraseField);
		
		buttonFinish = new JButton("Finish");
		buttonFinish.setFont(new Font("Tahoma", Font.PLAIN, 14));
		buttonFinish.setBounds(442, 347, 100, 30);
		contentPanes[1].add(buttonFinish);
	}
	
	public void setContentPane(int index) {
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				removeAll();
				add(contentPanes[index]);
				
				if (index == 0) {
					contentPanes[1].remove(labelErrorMessage);
					contentPanes[0].add(labelErrorMessage);
					
					contentPanes[1].remove(labelSignInInstead);
					contentPanes[0].add(labelSignInInstead);
				} else if (index == 1) {
					contentPanes[0].remove(labelErrorMessage);
					contentPanes[1].add(labelErrorMessage);
					
					contentPanes[0].remove(labelSignInInstead);
					contentPanes[1].add(labelSignInInstead);
				}
				
				revalidate();
				repaint();
				componentResized(null);
			}
		});
	}
	
	public void setSecurityQuestions(String[] securityQuestions) {
		for (int i = 0; i < securityQuestions.length; i++) {
			if (StringUtilities.isNullOrEmpty(securityQuestions[i])) {
				textFieldAnswers[i].setEditable(false);
				
				continue;
			}
			
			textFieldSecurityQuestions[i].setText(securityQuestions[i]);
			textFieldAnswers[i].setEditable(true);
		}
	}
	
	public String[] getAnswers() {
		return new String[] {
			textFieldAnswers[0].getText(),
			textFieldAnswers[1].getText(),
			textFieldAnswers[2].getText()
		};
	}
	
	public void clearAnswers() {
		for (int i = 0; i < textFieldAnswers.length; i++) {
			textFieldAnswers[i].setText("");
		}
	}
	
	public void setErrorMessage(String errorMessage) {
		if (errorMessage == null || errorMessage.isEmpty()) {
			labelErrorMessage.setVisible(false);
			
			return;
		}
		
		labelErrorMessage.setText(errorMessage);
		labelErrorMessage.setVisible(true);
	}
	
	public String getEmail() {
		return textFieldEmail.getText().trim();
	}
	
	public void setEmail(String email) {
		textFieldEmail.setText(email);
	}
	
	public char[] getNewPassphrase() {
		return newPassphraseField.getPassword();
	}
	
	public char[] getRePassphrase() {
		return rePassphraseField.getPassword();
	}
	
	public void setActionListener(ActionListener actionListener) {
		if (actionListener == null) {
			return;
		}
		
		this.actionListener = actionListener;
		
		buttonNext.addActionListener(actionListener);
		buttonFinish.addActionListener(actionListener);
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
		JPanel contentPane = contentPanes[0];
		
		if (contentPanes[0].getParent() == null) {
			contentPane = contentPanes[1];
		}
		
		if (contentPane.getParent() == null) {
			return;
		}
		
		int x = (int) ((getWidth() / 2.0) - (contentPane.getWidth() / 2.0));
		int y = (int) ((getHeight() / 2.0) - (contentPane.getHeight() / 2.0));
		
		final JPanel temporaryContentPane = contentPane;
		
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				temporaryContentPane.setLocation(x, y);
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