package com.android.Smart.poster;

import java.io.Serializable;

public abstract class Poster implements Serializable {
	
	private static final long serialVersionUID = 9079110342394305206L;
	private String tagID;
	
	public Poster(String tagID) {
		this.tagID = tagID;
	}
	
	public String getTagID() {
		return tagID;
	}
	
	public static class NoSuchPosterException extends Exception {
		private static final long serialVersionUID = -7392711870005171312L; 
	}
	
	public static class RevokedPosterException extends Exception {
		private static final long serialVersionUID = 5859334351007143331L;
	}

}
