package Core_Common_Channel.IAST;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CA_IAST_Util {

	private CA_IAST_Util() {
	}

	/**
	 * Retorna la fecha actual en un formato dado en un String
	 * 
	 * @param format
	 *            Formato de la fecha, debe ser un formato SimpleDateFormat
	 *            valido
	 * @return Retorna un String con la fecha actual de acuerdo al formato dado.
	 */
	public static String currentDate(String format) {

		DateFormat df = new SimpleDateFormat(format);
		Date dateObj = new Date();

		return df.format(dateObj);
	}

	/**
	 * Valida que un String tenga un valor valido, es decir no nulo ni vacio
	 * 
	 * @param value
	 *            Representa la cadena de string dada
	 * @return Retorna un valor booleano el cual es true si el String dado no es
	 *         nulo ni vacio
	 */
	public static boolean isValidString(String value) {

		if (value != null && !"".equals(value.trim())) {
			return true;
		}

		return false;
	}

	/**
	 * Valida que un String dado no supere la longitud especificada.
	 * 
	 * @param value
	 *            Representa la cadena de string dada
	 * @param length
	 *            Representa la longitud maxima que deberia tener el string dado
	 * @return Retorna un valor booleano el cual es true si el String dado se
	 *         encuentra en la longitud permitida.
	 */
	public static boolean isValidLength(String value, int length) {

		if (value != null && value.length() <= length) {

			return true;
		}

		return false;
	}
}
