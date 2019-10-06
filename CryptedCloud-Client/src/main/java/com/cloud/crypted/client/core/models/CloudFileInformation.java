package com.cloud.crypted.client.core.models;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import com.cloud.crypted.client.core.utilities.StringUtilities;

public class CloudFileInformation {
	
	private long size;
	private long modifiedTime;
	
	private String id;
	private String name;
	private String iconURL;
	private ImageIcon icon;
	
	// private static final int ICON_SIZE = 24;
	
	private static final Map<String, ImageIcon> ICON_MAP = new HashMap<String, ImageIcon>();
	
	public long getSize() {
		return size;
	}
	
	public String getSizeString() {
		return StringUtilities.getFormattedSize(size);
	}
	
	public void setSize(long size) {
		this.size = size;
	}
	
	public long getModifiedTime() {
		return modifiedTime;
	}
	
	public String getModifiedTimeString() {
		return StringUtilities.getFormattedDate(modifiedTime);
	}
	
	public void setModifiedTime(long modifiedTime) {
		this.modifiedTime = modifiedTime;
	}
	
	public String getID() {
		return id;
	}
	
	public void setID(String id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public ImageIcon getIcon() {
		return icon;
	}
	
	public void setIcon(String iconURL) {
		this.iconURL = iconURL;
		
		try {
			icon = getImageIcon(iconURL);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
	
	public String getIconURL() {
		return iconURL;
	}
	
	private static ImageIcon loadImageIconFromURL(String iconURL) throws Exception {
		URL url = new URL(iconURL.replace("/16/", "/32/"));
		BufferedImage bufferedImage = ImageIO.read(url);
		// Image image = bufferedImage.getScaledInstance(ICON_SIZE, ICON_SIZE, Image.SCALE_SMOOTH);
		
		return new ImageIcon(bufferedImage);
	}
	
	private static ImageIcon getImageIcon(String iconURL) throws Exception {
		int indexOfType = iconURL.lastIndexOf("type");
		
		if (indexOfType != -1) {
			String iconKey = iconURL.substring(indexOfType + 5);
			ImageIcon imageIcon = ICON_MAP.get(iconKey);
			
			if (imageIcon == null) {
				imageIcon = loadImageIconFromURL(iconURL);
				
				ICON_MAP.put(iconKey, imageIcon);
			}
			
			return imageIcon;
		}
		
		return null;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
}