package edu.awieclawski.data.exceptions;

public class CurrencyNotFoundException extends DataNotFoundException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -321758401027370432L;

	public CurrencyNotFoundException(String errorMessage, Throwable err) {
		super(errorMessage, err);
	}

	public CurrencyNotFoundException(String errorMessage) {
		super(errorMessage);
	}
}
