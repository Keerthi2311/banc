package com.grupobancolombia.integracion.sti.rule;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Define transformation rules to IL-IAST and IAST-IL commons data types.
 * 
 * @author Jorge Alberto Tchira Salazar
 * @version 1.0
 * @since 2017-03-02
 */
public class IASTtoILTranformationRules {

	/**
	 * XSD max value integer constant
	 */
	public static final long XSD_INTEGER_MAX_VALUE = 2147483647;

	/**
	 * XSD max value long constant
	 */
	public static final String XSD_LONG_MAX_VALUE = "9223372036854775807";

	/**
	 * IAST to IL String rule
	 * 
	 * @param value
	 *            with the string to be transformed
	 * @return String value with the rule applied
	 */
	public static String transformIASTtoILStringRule(String value) {
		if (value == null || value.trim().isEmpty()) {
			value = CommonConstant.EMPTY.getValue();
		}
		return value;
	}

	/**
	 * IAST to IL Integer rule
	 * 
	 * @param value
	 *            with the integer to be transformed
	 * @return String value with the rule applied
	 */
	public static String transformIASTtoILIntegerRule(String value, ArrayList<String> fieldError) {
		long longValue = 0;
		
		if (value != null && !value.trim().isEmpty()) {
			value = processNumSign(value);
			Pattern patter = Pattern
					.compile(RegexPatterns.INTEGER_FORMAT_VALIDATION
							.getPattern());
			Matcher matcher = patter.matcher(value);
			if (!matcher.matches()) {
				String errorField = ILtoIASTTranformationRules.getFieldError(fieldError);
				throw new IllegalArgumentException(
						ExceptionMessages.INTEGER_EXCEPTION.getMessage()+ errorField != null ? errorField : "" + " Value: { " + value +" }");
			}
			longValue = Long.parseLong(value);
			if (longValue > XSD_INTEGER_MAX_VALUE) {
				String errorField = ILtoIASTTranformationRules.getFieldError(fieldError);
				throw new IllegalArgumentException(
						ExceptionMessages.INTEGER_EXCEPTION.getMessage()+ errorField != null ? errorField : "" + " Value: { " + value +" }");
			}
		} else {
			String errorField = ILtoIASTTranformationRules.getFieldError(fieldError);
			throw new IllegalArgumentException(
					ExceptionMessages.INTEGER_EXCEPTION.getMessage()+ errorField != null ? errorField : "" + " Value: { " + value +" }");
		}
		return CommonConstant.EMPTY.getValue() + longValue;
	}

	/**
	 * IAST to IL Float rule
	 * 
	 * @param value
	 *            with the float to be transformed
	 * @return String value with the rule applied
	 */
	public static String transformIASTtoILLongRule(String value, ArrayList<String> fieldError) {
		BigInteger integer = null;
		
		if (value != null && !value.trim().isEmpty()) {
			value = processNumSign(value);
			Pattern patter = Pattern
					.compile(RegexPatterns.LONG_FORMAT_VALIDATION.getPattern());
			Matcher matcher = patter.matcher(value);
			if (!matcher.matches()) {
				String errorField = ILtoIASTTranformationRules.getFieldError(fieldError);
				throw new IllegalArgumentException(
						ExceptionMessages.INTEGER_EXCEPTION.getMessage()+ errorField != null ? errorField : "" + " Value: { " + value +" }");
			}
			integer = new BigInteger(value);
			if (integer.compareTo(new BigInteger(XSD_LONG_MAX_VALUE)) == 1) {
				String errorField = ILtoIASTTranformationRules.getFieldError(fieldError);
				throw new IllegalArgumentException(
						ExceptionMessages.INTEGER_EXCEPTION.getMessage()+ errorField != null ? errorField : "" + " Value: { " + value +" }");
			}
		} else {
			String errorField = ILtoIASTTranformationRules.getFieldError(fieldError);
			throw new IllegalArgumentException(
					ExceptionMessages.INTEGER_EXCEPTION.getMessage()+ errorField != null ? errorField : "" + " Value: { " + value +" }");
		}

		return integer.toString();
	}

	/**
	 * IAST to IL Decimal rule
	 * 
	 * @param value
	 *            with the decimal to be transformed
	 * @return String value with the rule applied
	 */
	public static String transformIASTtoILDecimalRule(String value,  ArrayList<String> fieldError) {
		StringBuilder sb = new StringBuilder();
		BigInteger integer;
		if (value != null && !value.trim().isEmpty()) {
			value = processNumSign(value);
			Pattern patter = Pattern
					.compile(RegexPatterns.ONLYNUMBERS_FORMAT_VALIDATION
							.getPattern());
			Matcher matcher = patter.matcher(value);
			if (!matcher.matches()) {
				String errorField = ILtoIASTTranformationRules.getFieldError(fieldError);
				throw new IllegalArgumentException(
						ExceptionMessages.DECIMAL_EXCEPTION.getMessage()+ errorField != null ? errorField : "" + " Value: { " + value +" }");
			}

			int pointIndex = value.length() - 2;
			if (pointIndex > 0) {
				String firsValue = value.substring(0,1);
				if(firsValue.equals("-")){
					sb.append(firsValue.toString());
					integer = new BigInteger(value.substring(1, pointIndex));
				}else{
					integer = new BigInteger(value.substring(0, pointIndex));
				}
				String decimalValue = value.substring(pointIndex);
				sb.append(integer.toString());
				sb.append(CommonConstant.DECIMAL_SEPARATOR.getValue());
				sb.append(decimalValue);
			} else {
				String errorField = ILtoIASTTranformationRules.getFieldError(fieldError);
				throw new IllegalArgumentException(
						ExceptionMessages.DECIMAL_EXCEPTION.getMessage() + errorField != null ? errorField : "" + " Value: { " + value +" }");
			}
		} else {
			String errorField = ILtoIASTTranformationRules.getFieldError(fieldError);
			throw new IllegalArgumentException(
					ExceptionMessages.DECIMAL_EXCEPTION.getMessage()+ errorField != null ? errorField : "" + " Value: { " + value +" }");
		}
		return sb.toString();
	}

	/**
	 * IAST to IL DateTime rule
	 * 
	 * @param value
	 *            with the DateTime to be transformed
	 * @return String value with the rule applied
	 */
	public static String transformIASTtoILDateTimeRule(String value, ArrayList<String> fieldError) {
		StringBuilder sb = new StringBuilder();
		if (value != null && !value.isEmpty()) {

			Pattern patter = Pattern
					.compile(RegexPatterns.DATETIMEIAST_FORMAT_VALIDATION
							.getPattern());
			Matcher matcher = patter.matcher(value);
			if (!matcher.matches()) {
				String errorField = ILtoIASTTranformationRules.getFieldError(fieldError);
				throw new IllegalArgumentException(
						ExceptionMessages.DATETIME_EXCEPTION.getMessage()+ errorField != null ? errorField : "" + " Value: { " + value +" }");
			}
			if (!CommonConstant.IAST_DATETIME_DEFAULT_VALUE.getValue().equals(
					value)) {
				int pointIndex = value.length();
				if (pointIndex == 14) {
					String date = transformIASTtoILDateRule(value.substring(0,
							8), fieldError);
					String time = transformIASTtoILTimeRule(value.substring(8,
							14), fieldError);
					sb.append(date);
					sb.append(CommonConstant.DATETIME_CHARACTER.getValue());
					sb.append(time);
				} else {
					String errorField = ILtoIASTTranformationRules.getFieldError(fieldError);
					throw new IllegalArgumentException(
							ExceptionMessages.DATETIME_EXCEPTION.getMessage()+ errorField != null ? errorField : "" + " Value: { " + value +" }");
				}
			} else {
				sb.append(CommonConstant.XSD_DATETIME_DEFAULT_VALUE.getValue());
			}
		} else {
			String errorField = ILtoIASTTranformationRules.getFieldError(fieldError);
			throw new IllegalArgumentException(
					ExceptionMessages.DATETIME_EXCEPTION.getMessage()+ errorField != null ? errorField : "" + " Value: { " + value +" }");
		}
		return sb.toString();
	}

	/**
	 * IAST to IL Date rule
	 * 
	 * @param value
	 *            with the Date to be transformed
	 * @return String value with the rule applied
	 */
	public static String transformIASTtoILDateRule(String value, ArrayList<String> fieldError) {
		StringBuilder sb = new StringBuilder();
		if (value != null && !value.trim().isEmpty()) {

			if (!CommonConstant.IAST_DATE_DEFAULT_VALUE.getValue()
					.equals(value)) {
				transformIASTtoILLongRule(value, fieldError);
				int pointIndex = value.length();
				if (pointIndex == 8) {
					String year = value.substring(0, 4);
					String month = value.substring(4, 6);
					String day = value.substring(6, 8);

					sb.append(year);
					sb.append(CommonConstant.DATE_CHARACTER.getValue());
					sb.append(month);
					sb.append(CommonConstant.DATE_CHARACTER.getValue());
					sb.append(day);
				} else {
					String errorField = ILtoIASTTranformationRules.getFieldError(fieldError);
					throw new IllegalArgumentException(
							ExceptionMessages.DATE_EXCEPTION.getMessage()+ errorField != null ? errorField : "" + " Value: { " + value +" }");
				}
			} else {
				sb.append(CommonConstant.XSD_DATE_DEFAULT_VALUE.getValue());
			}
		} else {
			String errorField = ILtoIASTTranformationRules.getFieldError(fieldError);
			throw new IllegalArgumentException(ExceptionMessages.DATE_EXCEPTION
					.getMessage()+ errorField != null ? errorField : "" + " Value: { " + value +" }");
		}
		return sb.toString();
	}

	/**
	 * IAST to IL Time rule
	 * 
	 * @param value
	 *            with the Time to be transformed
	 * @return String value with the rule applied
	 */
	public static String transformIASTtoILTimeRule(String value, ArrayList<String> fieldError) {
		StringBuilder sb = new StringBuilder();
		if (value != null && !value.trim().isEmpty()) {
			transformIASTtoILLongRule(value, fieldError);
			int pointIndex = value.length();
			if (pointIndex == 6) {
				String hours = value.substring(0, 2);
				String minutes = value.substring(2, 4);
				String seconds = value.substring(4, 6);

				sb.append(hours);
				sb.append(CommonConstant.TIME_CHARACTER.getValue());
				sb.append(minutes);
				sb.append(CommonConstant.TIME_CHARACTER.getValue());
				sb.append(seconds);
			} else {
				String errorField = ILtoIASTTranformationRules.getFieldError(fieldError);
				throw new IllegalArgumentException(
						ExceptionMessages.TIME_EXCEPTION.getMessage()+ errorField != null ? errorField : "" + " Value: { " + value +" }");
			}
		} else {
			String errorField = ILtoIASTTranformationRules.getFieldError(fieldError);
			throw new IllegalArgumentException(ExceptionMessages.TIME_EXCEPTION
					.getMessage()+ errorField != null ? errorField : "" + " Value: { " + value +" }");
		}
		return sb.toString();
	}

	/**
	 * IAST to IL boolean rule
	 * 
	 * @param value
	 *            with the boolean to be transformed
	 * @return String value with the rule applied
	 */
	public static String transformIASTtoILBooleanRule(String value,
			BooleanFormat format, ArrayList<String> fieldError) {
		String result = null;
		if (value != null && !value.isEmpty()) {
			if (format != null && !format.equals(BooleanFormat.LETTER)) {

				if (value.equalsIgnoreCase("S")
						|| value.equalsIgnoreCase("1")) {
					result = format.getTrueValue();
				} else if (value.equalsIgnoreCase("N")
						|| value.equalsIgnoreCase("0")) {
					result = format.getFalseValue();
				} else {
					if (value.equals(CommonConstant.BLANK_SPACE.getValue())) {
						result = CommonConstant.EMPTY.getValue();
					} else {
						String errorField = ILtoIASTTranformationRules.getFieldError(fieldError);
						throw new IllegalArgumentException(
								ExceptionMessages.BOOLEAN_EXCEPTION
										.getMessage()+ errorField + " Value: { " + value +" }");
					}
				}
			} else {
				String errorField = ILtoIASTTranformationRules.getFieldError(fieldError);
				throw new IllegalArgumentException(
						ExceptionMessages.BOOLEAN_EXCEPTION.getMessage()+ errorField != null ? errorField : "" + " Value: { " + value +" }");
			}
		} else {
			String errorField = ILtoIASTTranformationRules.getFieldError(fieldError);
			throw new IllegalArgumentException(
					ExceptionMessages.BOOLEAN_EXCEPTION.getMessage()+ errorField != null ? errorField : "" + " Value: { " + value +" }");
		}
		return result;
	}
	
	/**
	 * IAST to IL processNumSign process
	 * 
	 * @param value
	 *            Value for assign the "Sign" positive or negative
	 * @return String value with the rule applied
	 */
	public static String processNumSign(String value){
		String firsValue = value.substring(0,1);
		if((firsValue.equals(" ")) || (firsValue.equals("+"))){
			value = value.substring(1);
		}
		return value;
	}
}
