package com.grupobancolombia.integracion.sti.rule;

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
public class ILtoIASTTranformationRules {

	/**
	 * Replace DateTime characters expression
	 */
	public static final String REPLACE_DATETIME_EXPRESSION = "[-T:]";

	/**
	 * IAST DateTime length
	 */
	public static final int IAST_DATETIME_LENGTH = 14;

	/**
	 * IAST Date length
	 */
	public static final int IAST_DATE_LENGTH = 8;

	/**
	 * IAST Time length
	 */
	public static final int IAST_TIME_LENGTH = 6;

	/**
	 * IAST Boolean length
	 */
	public static final int IAST_BOOLEAN_LENGTH = 6;

	/**
	 * Define the refill options
	 * 
	 * @author Jorge Alberto Tchira Salazar
	 * @version 1.0
	 * @since 2017-03-06
	 */
	public enum RefillOption {
		BLANK_SPACE(" "), ZERO("0");

		/**
		 * Refill character
		 */
		private String refillCharacter;

		private RefillOption(String refillCharacter) {
			this.refillCharacter = refillCharacter;
		}

		/**
		 * @return the falseValue
		 */
		public String getRefillCharacter() {
			return refillCharacter;
		}
	}

	/**
	 * Define the align options
	 * 
	 * @author Jorge Alberto Tchira Salazar
	 * @version 1.0
	 * @since 2017-03-06
	 */
	public enum AlignOption {
		LEFT, RIGHT;
	}

	/**
	 * IL to IAST String data type rule
	 * 
	 * @param value
	 *            that will be transformed
	 * @param length
	 *            that will have the transformed data result
	 * @return String value with the rule applied
	 */
	public static String transformILtoIASTRightStringRule(String value, int length, ArrayList<String> fieldError) {
		return refillValue(value, RefillOption.BLANK_SPACE, AlignOption.RIGHT,
				length, fieldError);
	}
	
	/**
	 * IL to IAST String data type rule
	 * 
	 * @param value
	 *            that will be transformed
	 * @param length
	 *            that will have the transformed data result
	 * @return String value with the rule applied
	 */
	public static String transformILtoIASTStringRule(String value, int length, ArrayList<String> fieldError) {
		return refillValue(value, RefillOption.BLANK_SPACE, AlignOption.LEFT,
				length, fieldError);
	}

	/**
	 * IL to IAST Integer data types rules
	 * 
	 * @param value
	 *            that will be transformed
	 * @param length
	 *            that will have the transformed data result
	 * @return String value with the rule applied
	 */
	public static String transformILtoIASTIntegerRule(String value, int length, ArrayList<String> fieldError) {

		Pattern patter = Pattern
				.compile(RegexPatterns.INTEGER_FORMAT_VALIDATION.getPattern());
		if (value != null && !value.isEmpty()) {
				Matcher matcher = patter.matcher(value);
				if (!matcher.matches()) {
					String errorField = getFieldError(fieldError);
					throw new IllegalArgumentException(
							ExceptionMessages.INTEGER_EXCEPTION.getMessage()+ errorField != null ? errorField : "" + " Value: { " + value +" }");
				}
		}
		return refillValue(value, RefillOption.ZERO, AlignOption.RIGHT, length, fieldError);
	}

	/**
	 * IL to IAST Long data types rules
	 * 
	 * @param value
	 *            that will be transformed
	 * @param length
	 *            that will have the transformed data result
	 * @return String value with the rule applied
	 */
	public static String transformILtoIASTLongRule(String value, int length, ArrayList<String> fieldError) {

		Pattern patter = Pattern.compile(RegexPatterns.LONG_FORMAT_VALIDATION
				.getPattern());
		if (value != null && !value.isEmpty()) {
				Matcher matcher = patter.matcher(value);
				if (!matcher.matches()) {
					String errorField = getFieldError(fieldError);
					throw new IllegalArgumentException(
							ExceptionMessages.INTEGER_EXCEPTION.getMessage() + errorField != null ? errorField : "" + " Value: { " + value +" }");
			}
		}
		return refillValue(value, RefillOption.ZERO, AlignOption.RIGHT, length, fieldError);
	}

	/**
	 * IL to IAST Decimal data types rules
	 * 
	 * @param value
	 *            that will be transformed
	 * @param length
	 *            that will have the transformed data result
	 * @return String value with the rule applied
	 */
	public static String transformILtoIASTDecimalRule(String value, int length, ArrayList<String> fieldError) {
		if (value != null && !value.isEmpty()) {
			Pattern patter = Pattern
					.compile(RegexPatterns.DECIMAL_FORMAT_VALIDATION
							.getPattern());
			Pattern pattern2 = Pattern.compile(RegexPatterns.DECIMAL_FORMAT_VALIDATION_MORE_TOW_DECIMAL.getPattern());
				Matcher matcher2 = pattern2.matcher(value);
				Matcher matcher = patter.matcher(value);
				if (!matcher.matches() || !matcher2.matches()) {
					String errorField = getFieldError(fieldError);
					throw new IllegalArgumentException(
							ExceptionMessages.DECIMAL_EXCEPTION.getMessage() + errorField != null ? errorField : "" + " Value: { " + value +" }");
				}
				value = value.replace(CommonConstant.DECIMAL_SEPARATOR
						.getValue(), CommonConstant.EMPTY.getValue());
		}
		return refillValue(value, RefillOption.ZERO, AlignOption.RIGHT, length, fieldError);
	}

	/**
	 * IL to IAST DateTime data types rules
	 * 
	 * @param value
	 *            that will be transformed
	 * 
	 * @return String value with the rule applied
	 */
	public static String transformILtoIASTDateTimeRule(String value, ArrayList<String> fieldError) {
		String iastDateTime = null;
		if (value != null && !value.isEmpty()) {
				Pattern patter = Pattern
						.compile(RegexPatterns.DATETIME_FORMAT_VALIDATION
								.getPattern());
				Matcher matcher = patter.matcher(value);
				if (matcher.matches()) {
					iastDateTime = value.replaceAll(
							REPLACE_DATETIME_EXPRESSION, CommonConstant.EMPTY
									.getValue());
				} else {
					String errorField = getFieldError(fieldError);
					throw new IllegalArgumentException(
							ExceptionMessages.DATETIME_EXCEPTION.getMessage()+ errorField != null ? errorField : "" + " Value: { " + value +" }");
				}

		} else {
			iastDateTime = CommonConstant.IAST_DATETIME_DEFAULT_VALUE
					.getValue();
		}
		return iastDateTime;
	}

	/**
	 * IL to IAST Date data types rules
	 * 
	 * @param value
	 *            that will be transformed
	 * 
	 * @return String value with the rule applied
	 */
	public static String transformILtoIASTDateRule(String value, ArrayList<String> fieldError) {
		String iastDate = null;
		if (value != null && !value.trim().isEmpty()) {
			Pattern patter = Pattern
					.compile(RegexPatterns.DATE_FORMAT_VALIDATION.getPattern());
			Matcher matcher = patter.matcher(value);
			if (matcher.matches()) {
				iastDate = value.replaceAll(REPLACE_DATETIME_EXPRESSION,
						CommonConstant.EMPTY.getValue());
			} else {
				String errorField = getFieldError(fieldError);
				throw new IllegalArgumentException(
						ExceptionMessages.DATE_EXCEPTION.getMessage()+ errorField != null ? errorField : "" + " Value: { " + value +" }");
			}
		} else {
			iastDate = CommonConstant.IAST_DATE_DEFAULT_VALUE.getValue();
		}
		return iastDate;
	}

	/**
	 * IL to IAST Time data types rules
	 * 
	 * @param value
	 *            that will be transformed
	 * 
	 * @return String value with the rule applied
	 */
	public static String transformILtoIASTTimeRule(String value, ArrayList<String> fieldError) {
		String iastTime = null;
		if (value != null && !value.isEmpty()) {
			Pattern patter = Pattern
					.compile(RegexPatterns.TIME_FORMAT_VALIDATION.getPattern());
			Matcher matcher = patter.matcher(value);
			if (matcher.matches()) {
				iastTime = value.replaceAll(REPLACE_DATETIME_EXPRESSION,
						CommonConstant.EMPTY.getValue());
			} else {
				String errorField = getFieldError(fieldError);
				throw new IllegalArgumentException(
						ExceptionMessages.TIME_EXCEPTION.getMessage()+ errorField != null ? errorField : "" + " Value: { " + value +" }");
			}
		} else {
			iastTime = CommonConstant.IAST_TIME_DEFAULT_VALUE.getValue();
		}
		return iastTime;
	}

	/**
	 * IL to IAST Date and Time data types rules
	 * 
	 * @param value
	 *            that will be transformed
	 * @param BooleanFormat
	 *            with the desired boolean format output
	 * @return String value with the rule applied
	 */
	public static String transformILtoIASTBooleanRule(String value,
			BooleanFormat format, ArrayList<String> fieldError) {
		String result = null;
		if (value != null && !value.isEmpty()) {
			if (format != null && !format.equals(BooleanFormat.BOOL)) {

				if (value.equalsIgnoreCase("true")
						|| value.equalsIgnoreCase("1")) {
					result = format.getTrueValue();
				} else if (value.equalsIgnoreCase("false")
						|| value.equalsIgnoreCase("0")) {
					result = format.getFalseValue();
				} else {
					if (value.equals(CommonConstant.BLANK_SPACE.getValue())) {
						result = CommonConstant.EMPTY.getValue();
					} else {
						String errorField = getFieldError(fieldError);
						throw new IllegalArgumentException(
								ExceptionMessages.BOOLEAN_EXCEPTION
										.getMessage()+ errorField != null ? errorField : "" + " Value: { " + value +" }");
					}
				}
			} else {
				String errorField = getFieldError(fieldError);
				throw new IllegalArgumentException(
						ExceptionMessages.BOOLEAN_EXCEPTION.getMessage() + errorField != null ? errorField : "" + " Value: { " + value +" }");
			}
		} else {
			String errorField = getFieldError(fieldError);
			throw new IllegalArgumentException(
					ExceptionMessages.BOOLEAN_EXCEPTION.getMessage() + errorField != null ? errorField : "" + " Value: { " + value +" }");
		}
		return result;
	}

	/**
	 * Refill the value with the characters needed
	 * 
	 * @param value
	 *            that will be refill
	 * @param refillOption
	 *            to choose the refill character
	 * @param refillOption
	 *            with the desired character to refill
	 * @param length
	 *            that will have the data result
	 * @return String value with the rule applied
	 */
	
	public static String refillValue(String value, RefillOption refillOption,
			AlignOption alignOption, int length, ArrayList<String> fieldError) {

		StringBuffer sbRefill = new StringBuffer();
		StringBuffer sbValue = new StringBuffer();
		if (value != null && !CommonConstant.EMPTY.getValue().equals(value)) {
			int fill = length - value.length();
			if (fill >= 0) {
				for (int cont = 0; cont < fill; cont++) {
					sbRefill.append(refillOption.refillCharacter);
				}
			} else {
				String errorField = getFieldError(fieldError);
				throw new IllegalArgumentException("Invalid length character" +  errorField != null ? errorField : "" + " Value: { " + value +" }") ;
			}
		} else {
			int cont = 0;
			do{
				if(value == null){
					value = CommonConstant.EMPTY.getValue();
				}
					if(!value.isEmpty()){
					value = refillOption.refillCharacter;
				}
				cont++;
				sbRefill.append(refillOption.refillCharacter);
				
			}while(cont < length);
		}

		if (AlignOption.LEFT.equals(alignOption)) {
			sbValue.append(value);
			sbValue.append(sbRefill);
		} else if (AlignOption.RIGHT.equals(alignOption)) {
			sbValue.append(sbRefill);
			sbValue.append(value);
		}
		return sbValue.toString();
	}

	/***
	 * 
	 * @author ypalomeq
	 * @since 2017-10-06
	 * @desc this function allows refill of fields with send the parameter characterRefill
	 * @param value
	 *            parameter that contains the value of the field
	 * @param lenValue
	 *            parameter that contains the length of field
	 * @param characterRefill
	 *            parameter containing which fill character is used in the field
	 *            
	 */

	
	public static String CharacterRefill(String value, int lenValue,
			String characterRefill, ArrayList<String> fieldError) {

		String returnValue = "";
		StringBuffer sb = new StringBuffer();
		if (value != null && !value.isEmpty()) {

			if (value.length() == lenValue) {
				 returnValue = value;
			} else {
				int len = lenValue - value.length();

				for (int i = 0; i < len; i++) {
					sb.append(characterRefill);
				}
				if (characterRefill.matches("[0-9]+")) {
					returnValue = sb.toString() + value;
				} else {
					returnValue = value + sb.toString();
				}
			}
		}
		return returnValue;
	}
	
	public static String getFieldError(ArrayList<String> fieldError){
		StringBuffer sb = new StringBuffer();
		if(null != fieldError){
			sb.append(" El campo xml esta mal formado o es un valor por defecto, {o} Las posiciones de la trama son incorrectas: ");
		for (String field : fieldError) {
			sb.append(field + ", ");
		}
		}
		return sb.toString();
	}
}
