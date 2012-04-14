package com.android.Smart.poster;

import java.io.Serializable;

/**
 * Basically a tuple (text, value for POST) for poll options
 * @author jaswu
 */
public class PollOption implements Serializable {
	
	private static final long serialVersionUID = 4430939148677903860L;
	private String text;
	private String postValue;
	
	public PollOption(String text, String postValue) {
		this.text = text;
		this.postValue = postValue;
	}
	
	public String getText() {
		return text;
	}

	public String getPOSTValue() {
		return postValue;
	}
}
