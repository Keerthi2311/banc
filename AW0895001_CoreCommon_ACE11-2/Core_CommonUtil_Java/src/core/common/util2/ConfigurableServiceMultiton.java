package core.common.util2;

import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbPolicy;

public class ConfigurableServiceMultiton {
	/**
	 * Constructor privado para impedir que se instancie esta clase
	 */
	private ConfigurableServiceMultiton() {
	}

	public static MbPolicy getPolicy(String policyType, String policyProject,
			String policyName) {
		MbPolicy myPol;
		try {
			if (policyProject == null || "".equals(policyProject))
				myPol = MbPolicy.getPolicy(policyType, policyName);
			else
				myPol = MbPolicy.getPolicy(policyType, String.format("{%s}:%s", policyProject, policyName));
			if (myPol == null)
				throw new RuntimeException(
						"No existe la politica o proyecto especificado");
		} catch (MbException e) {
			throw new RuntimeException(e);
		}
		return myPol;
	}

	public static String getValue(String policyType, String policyProject,
			String policyName, String propertyKey) {
		String resultValue = "";
		if (propertyKey.equals("")) return null;
		try {
			MbPolicy myPol = getPolicy(policyType, policyProject, policyName);
			resultValue = myPol.getPropertyValueAsString(propertyKey);
			if (resultValue == null) {
				return "";
				//no se debe enregar un null sino vacio ya que la funcion que espera puede continuear con el codigo de error
				//throw new RuntimeException(String.format("No existe la propiedad: %s especificada", propertyKey));
			}
		} catch (MbException e) {
			throw new RuntimeException(e);
		}
		return resultValue;
	}
}
