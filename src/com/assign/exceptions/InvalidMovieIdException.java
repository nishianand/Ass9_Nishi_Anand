package com.assign.exceptions;

public class InvalidMovieIdException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidMovieIdException() {
		// TODO Auto-generated constructor stub
	}

	public InvalidMovieIdException(String message) {
		System.err.println(message + " is an invalid movieId. It should be an Integer.");
	}

}
