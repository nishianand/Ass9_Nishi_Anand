package com.assign;

public class MovieEnum {
	public enum Category {
		 COMEDY, HORROR, DRAMA,THRILLER, ACTION, ROMANCE;
	}

	public enum Language {
		ENGLISH("English"), 
		HINDI("Hindi"), 
		PUNJABI("punjabi");
		
		private String language;
		
		Language(String language) {
			this.language = language;
		}
		
		public String getLanguage() {
			return this.language;
		}
	}

}
