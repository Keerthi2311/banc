package core.common.peq.dto;

import java.util.List;

import com.ibm.broker.plugin.MbException;

import core.common.peq.Constants;

public class PEQDTO {
	
	private String originSociety;
	private String originApp;
	private String destinationSociety;
	private String destinationApp;
	private List<Criteria> criterias;
	
	public String getOriginSociety() {
		return originSociety;
	}
	public void setOriginSociety(String originSociety) {
		this.originSociety = originSociety;
	}
	public String getOriginApp() {
		return originApp;
	}
	public void setOriginApp(String originApp) {
		this.originApp = originApp;
	}
	public String getDestinationSociety() {
		return destinationSociety;
	}
	public void setDestinationSociety(String destinationSociety) {
		this.destinationSociety = destinationSociety;
	}
	public String getDestinationApp() {
		return destinationApp;
	}
	public void setDestinationApp(String destinationApp) {
		this.destinationApp = destinationApp;
	}
	public List<Criteria> getCriterias() {
		return criterias;
	}
	public void setCriterias(List<Criteria> criterias) {
		this.criterias = criterias;
	}
	
	/**
	 * Convierte un arreglo de duplas codigo-valor en un String para el campo
	 * ListaCampos del Stored Procedure
	 * 
	 * @param parameters
	 *            Elemento de mensaje
	 * @return Cadena concatenada
	 * @author Oscar Bustos
	 * @throws MbException
	 */
	public String buildCriteriaRequest() {

		StringBuffer camposConcatenados = new StringBuffer();

		for (Criteria criteria : criterias) {
			//TODO Verificar que significa la condicion igual a -4
			//if (parameter[4].equals("-1")) {
				camposConcatenados.append(criteria.getTipology()
						+ Constants.GAP + criteria.getOriginValue()
						+ Constants.SPLIT);
			//}
		}
		if (camposConcatenados.length() > 0) {
			camposConcatenados.deleteCharAt(camposConcatenados.length() - 1);
		}

		return camposConcatenados.toString();
	}

}
