package com.grupobancolombia.integracion.sti.mapping;

import com.grupobancolombia.integracion.sti.mapping.operation.RuleOption;

public class ListElement {
	private String nameField;
	private int positionInitial;
	private int positionFinal;
	private RuleOption ruleOption;
	private boolean isHomologation;
	private TypeOptionHomologation typeHomologation;
	private String tipologyOrServiceConfiguration;
	private int lenghtFiled;
	private String prefixHomologation;
	private NumberSetting listNumericProcces;
	private String defaultValue;

	public ListElement() {
	}

	public ListElement(String nameField, int positionInitial,
			int positionFinal, RuleOption ruleOption,
			boolean isHomologation, TypeOptionHomologation typeHomologation,
			String tipologyOrServiceConfiguration, String prefixHomologation) {
		this.ruleOption = ruleOption;
		this.positionInitial = positionInitial;
		this.positionFinal = positionFinal;
		this.ruleOption = ruleOption;
		this.isHomologation = isHomologation;
		this.typeHomologation = typeHomologation;
		this.tipologyOrServiceConfiguration = tipologyOrServiceConfiguration;
		this.nameField = nameField;
		this.prefixHomologation = prefixHomologation;

	}

	public ListElement(String nameField, int positionInitial,
			int positionFinal, RuleOption ruleOption,
			NumberSetting listNumericProcces) {
		this.ruleOption = ruleOption;
		this.positionInitial = positionInitial;
		this.positionFinal = positionFinal;
		this.nameField = nameField;
		this.listNumericProcces = listNumericProcces;
	}
	
	public ListElement(String nameField, int positionInitial,
			int positionFinal, RuleOption ruleOption,
			NumberSetting listNumericProcces, String defaultValue) {
		this.ruleOption = ruleOption;
		this.positionInitial = positionInitial;
		this.positionFinal = positionFinal;
		this.nameField = nameField;
		this.listNumericProcces = listNumericProcces;
		this.defaultValue = defaultValue;
	}
	
	public ListElement(String nameField,
			boolean isHomologation,String tipologyOrServiceConfiguration, RuleOption ruleOption, int lenghtFiled) {
		
		this.isHomologation = isHomologation;
		this.tipologyOrServiceConfiguration = tipologyOrServiceConfiguration;
		this.nameField = nameField;
		this.ruleOption = ruleOption;
		this.lenghtFiled = lenghtFiled;
	}
	
	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getPrefixHomologation() {
		return prefixHomologation;
	}

	public void setPrefixHomologation(String prefixHomologation) {
		this.prefixHomologation = prefixHomologation;
	}

	public String getNameField() {
		return nameField;
	}

	public void setNameField(String nameField) {
		this.nameField = nameField;
	}

	public int getPositionInitial() {
		return positionInitial;
	}

	public void setPositionInitial(int positionInitial) {
		this.positionInitial = positionInitial;
	}

	public int getPositionFinal() {
		return positionFinal;
	}

	public void setPositionFinal(int positionFinal) {
		this.positionFinal = positionFinal;
	}

	public RuleOption getRuleOption() {
		return ruleOption;
	}

	public void setRuleOption(RuleOption ruleOption) {
		this.ruleOption = ruleOption;
	}

	public boolean isHomologation() {
		return isHomologation;
	}

	public void setHomologation(boolean isHomologation) {
		this.isHomologation = isHomologation;
	}

	public TypeOptionHomologation getTypeHomologation() {
		return typeHomologation;
	}

	public void setTypeHomologation(TypeOptionHomologation typeHomologation) {
		this.typeHomologation = typeHomologation;
	}

	public String getTipologyOrServiceConfiguration() {
		return tipologyOrServiceConfiguration;
	}

	public void setTipologyOrServiceConfiguration(
			String tipologyOrServiceConfiguration) {
		this.tipologyOrServiceConfiguration = tipologyOrServiceConfiguration;
	}

	public int getLenghtFiled() {
		return lenghtFiled;
	}

	public void setLenghtFiled(int lenghtFiled) {
		this.lenghtFiled = lenghtFiled;
	}

	public NumberSetting getListNumericProcces() {
		return listNumericProcces;
	}

	public void setListNumericProcces(NumberSetting listNumericProcces) {
		this.listNumericProcces = listNumericProcces;
	}

	public enum TypeOptionHomologation {
		PEQ, SERVICIECONFIGURATION
	}
}
