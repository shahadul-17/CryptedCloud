package com.cloud.crypted.client.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public final class ShareDialog extends JDialog implements ActionListener {
	
	private String email = "";
	
	private JPanel contentPane;
	private JTextField textFieldEmail;
	private JButton buttonShare;
	
	private static final long serialVersionUID = 6597459820679626571L;
	
	private ShareDialog() {
		initialize();
	}
	
	private void initialize() {
		setTitle("Share");
		setModal(true);
		setSize(435, 160);
		setResizable(false);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		contentPane = new JPanel();
		contentPane.setLayout(null);
		setContentPane(contentPane);
		
		JLabel labelEmail = new JLabel("Email");
		labelEmail.setBounds(10, 13, 150, 25);
		contentPane.add(labelEmail);
		
		textFieldEmail = new JTextField();
		textFieldEmail.setBorder(BorderFactory.createEmptyBorder());
		textFieldEmail.setBounds(10, 40, 408, 25);
		contentPane.add(textFieldEmail);
		
		buttonShare = new JButton("Share");
		buttonShare.setBounds(318, 78, 100, 30);
		buttonShare.addActionListener(this);
		contentPane.add(buttonShare);
	}
	
	public String getEmail() {
		return email;
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getSource().equals(buttonShare)) {
			email = textFieldEmail.getText().trim();
			
			dispose();
		}
	}
	
	public static ShareDialog show(Component parentComponent) {
		ShareDialog shareDialog = new ShareDialog();
		
		if (parentComponent instanceof JFrame) {
			shareDialog.setIconImage(((JFrame) parentComponent).getIconImage());
		}
		
		shareDialog.setLocationRelativeTo(parentComponent);
		shareDialog.setVisible(true);
		
		return shareDialog;
	}
	
}