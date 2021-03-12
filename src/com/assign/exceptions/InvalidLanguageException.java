package com.assign.exceptions;

import java.util.Arrays;

import com.assign.MovieEnum.Language;

public class InvalidLanguageException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidLanguageException() {
		// TODO Auto-generated constructor stub
	}

	public InvalidLanguageException(String message) {
		System.err.println(message + " is an invalid language. Language should be either " + Arrays.asList(Language.values()));
	}

}
