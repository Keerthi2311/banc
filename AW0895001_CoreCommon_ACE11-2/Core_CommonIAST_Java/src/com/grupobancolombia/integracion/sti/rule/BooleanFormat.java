package com.grupobancolombia.integracion.sti.rule;

/**
 * Define the IAST and XSD booleans formats
 * 
 * @author Jorge Alberto Tchira Salazar
 * @version 1.0
 * @since 2017-03-06
 */
public enum BooleanFormat {

	NUMBER("0", "1"), LETTER("N", "S"), BOOL("false", "true");

	/**
	 * False value representation for a element
	 */
	private String falseValue;

	/**
	 * True value representation for a element
	 */
	private String trueValue;

	private BooleanFormat(String falseValue, String trueValue) {
		this.falseValue = falseValue;
		this.trueValue = trueValue;
	}

	/**
	 * @return the falseValue
	 */
	public String getFalseValue() {
		return falseValue;
	}

	/**
	 * @return the trueValue
	 */
	public String getTrueValue() {
		return trueValue;
	}

}
