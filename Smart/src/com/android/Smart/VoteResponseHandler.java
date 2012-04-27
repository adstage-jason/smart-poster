package com.android.Smart;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Handler for the XML document returned by vote.php
 */
public class VoteResponseHandler extends DefaultHandler {
	
	private	int errorCode = 0;
	private int successCode = 0;
	
	@Override
	public void startElement(String namespaceURI, String localName,
			String qName, Attributes atts) throws SAXException {
		// localName or qName?
		System.out.println(qName);
		if (qName.equals("error")) {
			errorCode = Integer.valueOf(atts.getValue("code"));
		} else if (qName.equals("success")) {
			successCode = Integer.valueOf(atts.getValue("code"));
		}
	}
	
	public int getErrorCode() {
		return errorCode;
	}
	
	public int getSuccessCode() {
		return successCode;
	}
}
