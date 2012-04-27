package com.android.Smart.poster;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;

public class LinkPoster extends Poster {
	
	private static final long serialVersionUID = 7592179133566488663L;
	private URL url;
	private String description;
	
	public LinkPoster(String tagID) {
		super(tagID);
	}
	
	public void setURL(String url) {
		try {
			String decoded = URLDecoder.decode(url);
			this.url = new URL(decoded);
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
