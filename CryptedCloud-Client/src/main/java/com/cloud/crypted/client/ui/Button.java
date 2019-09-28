package com.cloud.crypted.client.ui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class Button extends JPanel implements ActionListener {
	
	private static final long serialVersionUID = -7958499099934516374L;
	
	private JButton button = null;
	private JLabel label = null;
	
	private List<ActionListener> actionListeners = null;
	
	public Button() {
		initialize();
	}
	
	private void initialize() {
		setOpaque(false);
		setLayout(new BorderLayout(5, 0));
		setBorder(BorderFactory.createEmptyBorder());
		
		button = new JButton();
		button.setEnabled(false);
		button.setFocusable(false);
		button.setFocusPainted(false);
		button.addActionListener(this);
		add(button, BorderLayout.WEST);
		
		label = new JLabel();
		label.setHorizontalAlignment(SwingConstants.LEFT);
		label.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		add(label, BorderLayout.CENTER);
	}
	
	public String getText() {
		return label.getText();
	}
	
	public void setText(String text) {
		label.setText(text);
	}
	
	public void setIcon(String iconName) {
		button.setIcon(new ImageIcon(getClass().getResource("/images/" + iconName)));
	}
	
	public void setEnabled(boolean enabled) {
		button.setEnabled(enabled);
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
	public void setToolTipText(String toolTipText) {
		button.setToolTipText(toolTipText);
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		event = new ActionEvent(this, event.getID(), event.getActionCommand());
		
		for (ActionListener actionListener : actionListeners) {
			actionListener.actionPerformed(event);
		}
	}
	
}