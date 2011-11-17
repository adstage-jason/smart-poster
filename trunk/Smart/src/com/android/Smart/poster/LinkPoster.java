package com.android.Smart.poster;

import java.net.MalformedURLException;
import java.net.URL;

public class LinkPoster extends Poster {
	
	private URL url;
	private String description;
	
	public LinkPoster(String tagID) {
		super(tagID);
	}
	
	public void setURL(String url) {
		try {
			this.url = new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public URL getURL() {
		return url;
	}
	
	public String getDescription() {
		return description;
	}

}