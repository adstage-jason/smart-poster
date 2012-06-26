package com.android.Smart.poster;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class PollPoster extends Poster {

	private static final long serialVersionUID = -7796035100178703468L;
	//private URL url;
	private String question;
	//private String postKey;
	private ArrayList<PollOption> options;
	
	public PollPoster(String tagID) {
		super(tagID);
	}
	
	/*public void setURL(String url) {
		try {
			this.url = new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}*/
	
	public void setQuestion(String question) {
		this.question = question;
	}
	
	/*public void setPOSTKey(String postKey) {
		this.postKey = postKey;
	}*/
	
	public void setOptions(ArrayList<PollOption> options) {
		this.options = options;
	}
	
	/*public URL getURL() {
		return url;
	}*/
	
	public String getQuestion() {
		return question;
	}
	
	/*public String getPOSTKey() {
		return postKey;
	}*/
	
	public ArrayList<PollOption> getOptions() {
		return options;
	}

}
