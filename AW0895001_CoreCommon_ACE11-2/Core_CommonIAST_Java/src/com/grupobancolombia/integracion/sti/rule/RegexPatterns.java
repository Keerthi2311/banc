package com.grupobancolombia.integracion.sti.rule;

/**
 * Define the rules validations regex patterns 
 * 
 * @author Jorge Alberto Tchira Salazar
 * @version 1.0
 * @since 2017-03-16
 */
public enum RegexPatterns {
	
	INTEGER_FORMAT_VALIDATION("\\-?\\d{0,10}"),
	ONLYNUMBERS_FORMAT_VALIDATION("\\-?\\d+"),
	DATETIMEIAST_FORMAT_VALIDATION("\\d{14,14}"),
	DATEIAST_FORMAT_VALIDATION("\\d{8,8}"),
	TIMEIAST_FORMAT_VALIDATION("\\d{6,6}"),
	LONG_FORMAT_VALIDATION("\\-?\\d{0,19}"),
	DECIMAL_FORMAT_VALIDATION("\\-?\\d+[.]\\d{2}"),
	DECIMAL_FORMAT_VALIDATION_MORE_TOW_DECIMAL("^(\\d{1}\\.)?(\\d+\\.?)+(,\\d{2})?$"),
	DATETIME_FORMAT_VALIDATION("\\d{4}[-]\\d{2}[-]\\d{2}[T]\\d{2}[:]\\d{2}[:]\\d{2}"),
	DATE_FORMAT_VALIDATION("\\d{4}-\\d{2}-\\d{2}"),
	TIME_FORMAT_VALIDATION("\\d{2}:\\d{2}:\\d{2}");
	
	/**
	 * The pattern
	 */
	private String pattern;

	private RegexPatterns(String pattern) {
		this.pattern = pattern;
	}

	/**
	 * @return the pattern
	 */
	public String getPattern() {
		return pattern;
	}

}
