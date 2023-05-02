package edu.awieclawski.data.exceptions;

public class EntityExistsException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4885808816996821818L;

	public EntityExistsException(String errorMessage, Throwable err) {
		super(errorMessage, err);
	}

	public EntityExistsException(String errorMessage) {
		super(errorMessage);
	}
}
