package com.grupobancolombia.integracion.sti.mapping;

/**
 * 
 * Class that implements the following IMessageMapping interface
 * 
 * @author Yeiner Palomeque Moreno
 * @version 1.0
 * @since 2017-09-27
 */

public class ListSetting {
	/*
	 * parameters.add(String.valueOf(numReg)); numero de registros
	 * parameters.add("111"); posicion inicial de la lista
	 * parameters.add("61"); logintud de la lista parameters.add(path +
	 * "informacionTarjetas/informacionTarjeta"); path de la lista
	 * parameters.add("9"); numero de campos
	 */
	
	
	private int numberRegister;
	private int positionInitialList;
	private int lenghtList;
	private int numberField;
	private String path;
	
	public ListSetting(int numberRegister, int positionInitialList, int lenghtList, int numberFlied, String path){
		this.numberRegister = numberRegister;
		this.positionInitialList = positionInitialList;
		this.lenghtList = lenghtList;
		this.numberField = numberFlied;
		this.path = path;
	}
	public ListSetting(){

	}

	public int getNumberRegister() {
		return numberRegister;
	}

	public void setNumberRegister(int numberRegister) {
		this.numberRegister = numberRegister;
	}

	public int getPositionInitiaList() {
		return positionInitialList;
	}

	public void setPositionInitiaList(int positionInitial) {
		this.positionInitialList = positionInitial;
	}

	public int getLenghtList() {
		return lenghtList;
	}

	public void setLenghtList(int lenghtList) {
		this.lenghtList = lenghtList;
	}

	public int getNumberFlied() {
		return numberField;
	}

	public void setNumberFlied(int numberFlied) {
		this.numberField = numberFlied;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	
	
	
}
