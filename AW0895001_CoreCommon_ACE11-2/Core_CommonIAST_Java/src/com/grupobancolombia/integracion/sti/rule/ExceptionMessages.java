package com.grupobancolombia.integracion.sti.rule;

/**
 * Define the exception messages 
 * 
 * @author Jorge Alberto Tchira Salazar
 * @version 1.0
 * @since 2017-03-16
 */
public enum ExceptionMessages {
	
	INTEGER_EXCEPTION("The input value must be a integer"),
	DECIMAL_EXCEPTION("The input value must be a number with maximum of 19 digits integers and 2 decimals"),
	DATETIME_EXCEPTION("The input value must be a DateTime with format yyyy-mm-ddT00:00:00"),
	DATE_EXCEPTION("The input value must be a Date with format yyyy-mm-dd"),
	TIME_EXCEPTION("The input value must be a Time with format 00:00:00"),
	BOOLEAN_EXCEPTION("Error to transform a boolean value"),
	INVALID_LENGTH_CHARACTER("Invalid length character"),
	BOOLEAN_FORMAT_NOT_SUPPORTED("Boolean format not supported");

	/**
	 * The exception message
	 */
	private String message;

	private ExceptionMessages(String message) {
		this.message = message;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

}
