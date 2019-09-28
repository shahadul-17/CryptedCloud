package com.cloud.crypted.client.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.IOException;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

import com.cloud.crypted.client.core.Configuration;
import com.cloud.crypted.client.core.utilities.StringUtilities;
import com.cloud.crypted.client.ui.events.WebViewListener;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp.Browser;
import com.sun.javafx.application.PlatformImpl;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;

@SuppressWarnings("restriction")
public class WebView extends JPanel implements Runnable, Browser, ChangeListener<Object> {
	
	private static final long serialVersionUID = 1293677892779081218L;
	
	private static enum RunnableFlag {
		SWING, JAVAFX, JAVAFX_EXIT, NONE
	}
	
	private RunnableFlag runnableFlag = RunnableFlag.NONE;
	private String url = null;
	
	private List<WebViewListener> webViewListeners = null;
	
	public WebView() {
		initialize();
	}
	
	private void initialize() {
		setBackground(Color.WHITE);
		setLayout(new BorderLayout());
	}
	
	public void addWebViewListener(WebViewListener webViewListener) {
		if (webViewListener == null) {
			return;
		}
		
		if (webViewListeners == null) {
			webViewListeners = new LinkedList<WebViewListener>();
		}
		
		webViewListeners.add(webViewListener);
	}
	
	public void load(String url) {
		if (StringUtilities.isNullOrEmpty(url)) {
			return;
		}
		
		this.url = url;
		runnableFlag = RunnableFlag.JAVAFX;
		
		PlatformImpl.setImplicitExit(false);
		
		try {
			PlatformImpl.runLater(this);
		} catch (Exception exception) {
			PlatformImpl.startup(this);
		}
	}
	
	@Override
	public void run() {
		switch (runnableFlag) {
		case SWING:
			runnableFlag = RunnableFlag.NONE;
			
			revalidate();
			repaint();
			
			return;
		case JAVAFX:
			javafx.scene.web.WebView webView = new javafx.scene.web.WebView();
			webView.setContextMenuEnabled(false);
			
			WebEngine webEngine = webView.getEngine();
			webEngine.setUserAgent(Configuration.get("userAgent"));
			// webEngine.getLoadWorker().stateProperty().addListener(this);
			webEngine.documentProperty().addListener(this);
			webEngine.load(url);
			
			JFXPanel jfxPanel = new JFXPanel();
			jfxPanel.setScene(new Scene(webView));
			
			removeAll();
			add(jfxPanel, BorderLayout.CENTER);
			
			runnableFlag = RunnableFlag.SWING;
			
			SwingUtilities.invokeLater(this);
			
			return;
		case NONE:
		default:
			return;
		}
	}
	
	@Override
	public void browse(String url) throws IOException {
		for (WebViewListener webViewListener : webViewListeners) {
			webViewListener.urlRequested(url);
		}
		
		load(url);
	}
	
	@Override
	public void changed(ObservableValue<? extends Object> observable, Object oldValue, Object newValue) {
		Object observableValue = observable.getValue();
		
		/*if (observableValue instanceof State) {
			State newState = (State) newValue;
			
			switch (newState) {
			case SUCCEEDED:
				for (WebViewListener webViewListener : webViewListeners) {
					webViewListener.locationChanged(webEngine.getLocation());
				}
				
				break;
			case FAILED:
				try {
					Thread.sleep(1000);
				} catch (Exception exception) { }
				
				webEngine.reload();
				
				break;
			default:
				break;
			}
		}*/
		if (observableValue instanceof Document) {
			if (newValue == null) {
				return;
			}
			
			Document document = (Document) newValue;
			String documentContent = getTextContent(document);
			
			if (StringUtilities.isNullOrEmpty(documentContent)) {
				return;
			}
			
			for (WebViewListener webViewListener : webViewListeners) {
				webViewListener.documentContentChanged(document.getDocumentURI(), documentContent);	
			}
		}
	}
	
	private static String getTextContent(Document document) {
		Transformer transformer = null;
		
		try {
			transformer = TransformerFactory.newInstance().newTransformer();
		} catch (Exception exception) {
			exception.printStackTrace();
			
			return "";
		}
		
		DOMSource domSource = new DOMSource(document);
		StreamResult streamResult = new StreamResult(new StringWriter());
		
		try {
			transformer.transform(domSource, streamResult);
			
			return transformer.toString();
		} catch (Exception exception) {
			return "";
		}
	}
	
}