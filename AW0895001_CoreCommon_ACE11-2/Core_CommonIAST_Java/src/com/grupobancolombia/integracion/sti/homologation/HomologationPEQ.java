package com.grupobancolombia.integracion.sti.homologation;

public class HomologationPEQ {

	/**
	 * appDest, Constante Aplicacion destino
	 */
	private String appDestPEQ;
	
	/**
	 * sociedadDestPEQ, Constante Sociedad destino
	 */
	private String sociedadDestPEQ;

	public HomologationPEQ() {
		super();
		this.appDestPEQ = "";
		this.sociedadDestPEQ = "";
	}
	
	public HomologationPEQ(String appDestPEQ, String sociedadDestPEQ) {
		super();
		this.appDestPEQ = appDestPEQ;
		this.sociedadDestPEQ = sociedadDestPEQ;
	}
	
	public String getAppDestPEQ() {
		return appDestPEQ;
	}

	public void setAppDestPEQ(String appDestPEQ) {
		this.appDestPEQ = appDestPEQ;
	}

	public String getSociedadDestPEQ() {
		return sociedadDestPEQ;
	}

	public void setSociedadDestPEQ(String sociedadDestPEQ) {
		this.sociedadDestPEQ = sociedadDestPEQ;
	}

}
