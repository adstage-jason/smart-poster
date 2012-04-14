package com.android.Smart;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.android.Smart.poster.LinkPoster;
import com.android.Smart.poster.PollOption;
import com.android.Smart.poster.PollPoster;
import com.android.Smart.poster.Poster;

/**
 * Handler for the XML document returned by get_poster.php
 */
public class SPANHandler extends DefaultHandler {
	
	private	int errorCode = 0;
	private Poster poster;
	private ArrayList<PollOption> pollOptions;
	
	@Override
	public void startElement(String namespaceURI, String localName,
			String qName, Attributes atts) throws SAXException {
		// localName or qName?
		if (qName.equals("error")) {
			errorCode = Integer.valueOf(atts.getValue("code"));
		} else if (qName.equals("poster")) {
			int posterType = Integer.valueOf(atts.getValue("type"));
			String tagID = atts.getValue("tag_id");
			if (posterType == 1) {
				poster = new LinkPoster(tagID);
			} else if (posterType == 3) {
				poster = new PollPoster(tagID);
			}
		} else {
			if (poster instanceof LinkPoster) {
				handleLinkPoster(namespaceURI, localName, qName, atts);
			} else if (poster instanceof PollPoster) {
				handlePollPoster(namespaceURI, localName, qName, atts);
			}
		}
	}
	
	private void handleLinkPoster(String namespaceURI, String localName,
			String qName, Attributes atts) {
		LinkPoster thePoster = (LinkPoster) poster;
		if (qName.equals("link")) {
			thePoster.setURL(atts.getValue("url"));
			thePoster.setDescription(atts.getValue("description"));
		}
	}
	
	private void handlePollPoster(String namespaceURI, String localName,
			String qName, Attributes atts) {
		PollPoster thePoster = (PollPoster) poster;
		if (qName.equals("question")) {
			thePoster.setQuestion(atts.getValue("q"));
		} else if (qName.equals("link")) {
			thePoster.setURL(atts.getValue("url"));
			thePoster.setPOSTKey(atts.getValue("post_key"));
		} else if (qName.equals("options")) {
			pollOptions = new ArrayList<PollOption>();
		} else if (qName.equals("option")) {
			String text = atts.getValue("text");
			String postValue = atts.getValue("post_value");
			PollOption newOption = new PollOption(text, postValue);
			pollOptions.add(newOption);
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName) {
		if (qName.equals("options") && poster instanceof PollPoster) {
			PollPoster thePoster = (PollPoster) poster;
			thePoster.setOptions(pollOptions);
		}
	}
	
	public int getErrorCode() {
		return errorCode;
	}
	
	public Poster getPoster() {
		return poster;
	}
}
