package com.android.Smart.poster;

public abstract class Poster {
	
	private String tagID;
	
	public Poster(String tagID) {
		this.tagID = tagID;
	}
	
	public String getTagID() {
		return tagID;
	}

}
