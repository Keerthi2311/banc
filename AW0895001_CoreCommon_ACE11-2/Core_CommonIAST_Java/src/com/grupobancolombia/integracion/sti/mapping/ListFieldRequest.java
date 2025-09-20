package com.grupobancolombia.integracion.sti.mapping;

public class ListFieldRequest {
	private String nameField;
	private boolean homologacionPEQ;
	private String tipology;
	public ListFieldRequest(String nameField, boolean homologacionPEQ, String tipology){
		this.nameField = nameField;
		this.homologacionPEQ = homologacionPEQ;
		this.tipology = tipology;
	}

	public String getNameField() {
		return nameField;
	}

	public void setNameField(String nameField) {
		this.nameField = nameField;
	}

	public boolean isHomologacionPEQ() {
		return homologacionPEQ;
	}

	public void setHomologacionPEQ(boolean homologacionPEQ) {
		this.homologacionPEQ = homologacionPEQ;
	}

	public String getTipology() {
		return tipology;
	}

	public void setTipology(String tipology) {
		this.tipology = tipology;
	}
	
	
}
