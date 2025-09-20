package com.grupobancolombia.integracion.sti.rule;

/**
 * Define common constant values
 * 
 * @author Jorge Alberto Tchira Salazar
 * @version 1.0
 * @since 2017-03-16
 */
public enum CommonConstant {

	BLANK_SPACE(" "), EMPTY(""), ZERO("0"), DECIMAL_SEPARATOR("."), DATETIME_CHARACTER(
			"T"), DATE_CHARACTER("-"), TIME_CHARACTER(":"), XSD_DATETIME_DEFAULT_VALUE(
			"0001-01-01T00:00:00"), XSD_DATE_DEFAULT_VALUE("0001-01-01"), TIME_DEFAULT_VALUE(
			"00:00:00"), DECIMAL_DEFAULT_VALUE("0.00"), IAST_DATETIME_DEFAULT_VALUE(
			"00000000000000"), IAST_DATE_DEFAULT_VALUE("00000000"), IAST_TIME_DEFAULT_VALUE(
			"000000");

	/**
	 * The constant value
	 */
	private String value;

	private CommonConstant(String value) {
		this.value = value;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

}
