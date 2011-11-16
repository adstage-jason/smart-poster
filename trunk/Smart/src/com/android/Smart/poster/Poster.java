package com.android.Smart.poster;

public abstract class Poster {
	
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
