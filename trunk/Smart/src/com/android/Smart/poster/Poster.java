package com.android.Smart.poster;

import java.io.Serializable;

public abstract class Poster implements Serializable {
	
	private static final long serialVersionUID = 9079110342394305206L;
	protected String tagID;
	
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
	
	public static class AlreadyVotedException extends Exception {
		private static final long serialVersionUID = -7335482597453351924L;
	}
	
	public static class AlreadyCommentedException extends Exception {
		private static final long serialVersionUID = 7890010643543366136L;
	}
	
	public static class AccessRevokedException extends Exception {
		private static final long serialVersionUID = 2622749031426062640L;
	}

}
