package edu.awieclawski.htmltopdf.exceptions;

public class NotFoundException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8932397741364178592L;

	public NotFoundException(String errorMessage) {
		super(errorMessage);
	}

}
