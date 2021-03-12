package com.assign.exceptions;

public class IncorrectValueCountException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public IncorrectValueCountException() {
		
	}

	public IncorrectValueCountException(int value) {
		System.err.println("The number of values is " + value );
		
	}

}
