package edu.awieclawski.data.exceptions;

public class DataExistsException extends EntityExistsException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4885808816996821818L;

	public DataExistsException(String errorMessage, Throwable err) {
		super(errorMessage, err);
	}

	public DataExistsException(String errorMessage) {
		super(errorMessage);
	}
}
