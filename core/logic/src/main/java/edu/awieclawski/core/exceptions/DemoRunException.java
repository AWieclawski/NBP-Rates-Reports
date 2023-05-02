package edu.awieclawski.core.exceptions;

import java.util.List;

public class DemoRunException extends RuntimeException {

	private final static String DELIMITER = " | ";

	/**
	 * 
	 */
	private static final long serialVersionUID = 5312007167100399270L;

	public DemoRunException(String message) {
		super(message);
	}

	public DemoRunException(List<String> messages) {
		super(listToMessage(messages));
	}

	private static String listToMessage(List<String> messages) {
		return String.join(DELIMITER, messages);
	}

}
