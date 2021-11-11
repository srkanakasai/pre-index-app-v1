package com.smarsh.preindex.exception;

import org.springframework.beans.factory.BeanCreationException;

public class PreIndexInitException extends BeanCreationException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PreIndexInitException(String message, Throwable cause) {
		super(message, cause);
	}

	public PreIndexInitException(String message) {
		super(message);
	}

}
