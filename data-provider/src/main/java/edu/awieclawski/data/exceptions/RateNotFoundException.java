package edu.awieclawski.data.exceptions;

public class RateNotFoundException extends DataNotFoundException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -862758407327370468L;

	public RateNotFoundException(String errorMessage, Throwable err) {
		super(errorMessage, err);
	}

	public RateNotFoundException(String errorMessage) {
		super(errorMessage);
	}
}
