package edu.awieclawski.core.exceptions;

import java.util.List;

public class NbpRequestException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4792007167100399741L;

	public NbpRequestException(String message) {
		super(message);
	}

	public NbpRequestException(List<String> messages) {
		super(listToMessage(messages));
	}

	private static String listToMessage(List<String> messages) {
		return String.join(" | ", messages);
	}

}
