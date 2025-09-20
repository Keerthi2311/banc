package core.common.util2;

import java.text.Normalizer;

public class StringUtil {
	private StringUtil() {
	}

	public static String removeSpecialChar(String value) {
    	String cadenaNormalize = Normalizer.normalize(value, Normalizer.Form.NFD);   
    	value = cadenaNormalize.replaceAll("[^\\p{ASCII}]", "").trim();		
		return value;
	}
}
