package co.com.bancolombia.common.util;

import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Map;

public class GenerateJwt {

	private static final String POLICYNAME = "DefaultPolicies";
	private static final String POLICYPROJECT = "JWTProperties";
	private static final String USERDEFINED = "UserDefined";
	private static final String SUM_CLIENTID = "SUM_CLIENT_ID";
	private static final String JWT_TIME_EXP = "JWT_TIME_EXP";
	private static final String PRIVATEKEY = "PRIVATE_KEY";
	private static final String AUDNAME = "AUD_NAME";
	private static final String ISSNAME = "ISS_NAME";
	private static String payload;
	private static int exp = Integer.MIN_VALUE;
	public GenerateJwt() {
	}

	private static String PRIVATE_KEY = ConfigurableServiceMultiton.getValue(USERDEFINED, POLICYNAME, POLICYPROJECT,PRIVATEKEY);
	private static String time = ConfigurableServiceMultiton.getValue(USERDEFINED, POLICYNAME, POLICYPROJECT,JWT_TIME_EXP);
	private static String sub = ConfigurableServiceMultiton.getValue(USERDEFINED, POLICYNAME, POLICYPROJECT,SUM_CLIENTID);
	private static String iss = ConfigurableServiceMultiton.getValue(USERDEFINED, POLICYNAME, POLICYPROJECT, ISSNAME);
	private static String aud = ConfigurableServiceMultiton.getValue(USERDEFINED, POLICYNAME, POLICYPROJECT, AUDNAME);
	
	@SuppressWarnings("rawtypes")
	private static Header header = Jwts.header().setType("JWT");

	@SuppressWarnings("unchecked")
	public static String returnJWT() {
		PrivateKey privateKey = getPrivateKey(PRIVATE_KEY);
		return Jwts.builder().setHeader((Map<String, Object>) header).setPayload(payload()).signWith(privateKey)
				.compact();
	}

	private static String payload() {
		int iat = (int) (System.currentTimeMillis() / 1000);
		if (exp <= iat) {
			exp = iat + Integer.parseInt(time);
			payload = "{\"iss\":" + "\"" + iss + "\"" + ", \"sub\":" + "\"" + sub + "\"" + ", \"aud\":" + "\"" + aud
					+ "\"" + ", \"exp\":" + exp + ", \"iat\":" + iat + "}";
		}
		return payload;
	}

	private static PrivateKey getPrivateKey(String rsaPrivateKey) {
		PrivateKey prKey = null;
		try {

			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(rsaPrivateKey));
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			prKey = keyFactory.generatePrivate(keySpec);

		} catch (Exception e) {
			System.out.println("Error generando Token" + e.getMessage());

		}
		return prKey;
	}
}
