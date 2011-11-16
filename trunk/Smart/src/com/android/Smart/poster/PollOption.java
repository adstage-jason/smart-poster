package com.android.Smart.poster;

/**
 * Basically a tuple (text, value for POST) for poll options
 * @author jaswu
 */
public class PollOption {
	
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
