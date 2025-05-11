package org.example.final_project.exception;

/**
 * @author nawaz
 */
public class ApiException extends RuntimeException {

	public ApiException(String message) {
		super(message);

	}

	public ApiException() {
		super();

	}

}
