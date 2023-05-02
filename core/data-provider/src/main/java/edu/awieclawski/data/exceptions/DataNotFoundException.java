package edu.awieclawski.data.exceptions;

public class DataNotFoundException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 875758401027370822L;

	public DataNotFoundException(String errorMessage, Throwable err) {
		super(errorMessage, err);
	}

	public DataNotFoundException(String errorMessage) {
		super(errorMessage);
	}
}
