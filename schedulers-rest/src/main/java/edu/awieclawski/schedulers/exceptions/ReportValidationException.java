package edu.awieclawski.schedulers.exceptions;

import java.util.List;

public class ReportValidationException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4242007167100399020L;

	public ReportValidationException(String message) {
		super(message);
	}

	public ReportValidationException(List<String> messages) {
		super(listToMessage(messages));
	}

	private static String listToMessage(List<String> messages) {
		return String.join(" | ", messages);
	}

}
