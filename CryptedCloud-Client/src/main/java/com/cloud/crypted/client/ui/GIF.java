package com.cloud.crypted.client.ui;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;

public class GIF extends Component {
	
	private Image image;
	
	private static final long serialVersionUID = -3775974246748709029L;
	
	public GIF(String gifName) {
		image = new ImageIcon(getClass().getResource("/images/" + gifName + ".gif")).getImage();
	}
	
	public int getImageWidth() {
		return image.getWidth(null);
	}
	
	public int getImageHeight() {
		return image.getHeight(null);
	}
	
	@Override
	public void paint(Graphics graphics) {
		if (image == null) {
			return;
		}
		
		int x = (int) ((getWidth() / 2.0) - (getImageWidth() / 2.0));
		int y = (int) ((getHeight() / 2.0) - (getImageHeight() / 2.0));
		
		graphics.drawImage(image, x, y, this);
	}
	
}