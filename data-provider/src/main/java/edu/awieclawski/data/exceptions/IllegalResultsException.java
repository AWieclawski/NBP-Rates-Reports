package edu.awieclawski.data.exceptions;

public class IllegalResultsException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5211068895466503708L;

	public IllegalResultsException(String errorMessage, Throwable err) {
		super(errorMessage, err);
	}

	public IllegalResultsException(String errorMessage) {
		super(errorMessage);
	}

}
