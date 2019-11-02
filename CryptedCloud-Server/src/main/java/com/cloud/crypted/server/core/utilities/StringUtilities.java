package com.cloud.crypted.server.core.utilities;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Locale;

import com.cloud.crypted.server.core.DynamicResources;

public final class StringUtilities {
	
	public static String getFormattedDate(long date) {
		DateFormatSymbols dateFormatSymbols = new DateFormatSymbols(Locale.US);
		dateFormatSymbols.setAmPmStrings(new String[] {
			(String) DynamicResources.getConfiguration("format.date.am"),
			(String) DynamicResources.getConfiguration("format.date.pm")
		});
		
		SimpleDateFormat dateFormat = new SimpleDateFormat((String) DynamicResources.getConfiguration("format.date"));
		dateFormat.setDateFormatSymbols(dateFormatSymbols);
		
		return dateFormat.format(date);
	}
	
	public static boolean isNullOrEmpty(String text) {
		return text == null || text.isEmpty();
	}
	
}