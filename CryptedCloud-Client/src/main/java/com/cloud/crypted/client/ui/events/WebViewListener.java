package com.cloud.crypted.client.ui.events;

import java.util.EventListener;

public interface WebViewListener extends EventListener {
	
	void urlRequested(String url);
	/*void locationChanged(String newLocation);*/
	void documentContentChanged(String documentURL, String newDocumentContent);
	
}