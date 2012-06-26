package com.android.Smart.poster;

import java.io.Serializable;

/**
 * Basically a tuple (text, value for POST) for poll options
 * @author jaswu
 */
public class PollOption implements Serializable {
	
	private static final long serialVersionUID = 4430939148677903860L;
	private String text;
	private int optionId;
	//private String postValue;
	
	public PollOption(String text, int optionId) {
		this.text = text;
		this.optionId = optionId;
		//this.postValue = postValue;
	}
	
	public String getText() {
		return text;
	}
	
	public int getOptionId() {
		return optionId;
	}

	/*public String getPOSTValue() {
		return postValue;
	}*/	
	
	public static class InvalidOptionException extends Exception {
		private static final long serialVersionUID = -708031545711296888L;
	}
}
