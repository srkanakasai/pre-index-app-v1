/**
 * 
 */
package com.smarsh.preindex.exception;

/**
 * @author sridhar.kanakasai
 *
 */
public class PreIndexRunTimeException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PreIndexRunTimeException(String message, Throwable cause) {
		super(message, cause);
	}

	public PreIndexRunTimeException(String message) {
		super(message);
	}

	public PreIndexRunTimeException(Throwable cause) {
		super(cause);
	}
}
