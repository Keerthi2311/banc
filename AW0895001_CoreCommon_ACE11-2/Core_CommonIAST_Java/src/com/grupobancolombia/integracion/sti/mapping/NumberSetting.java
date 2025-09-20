package com.grupobancolombia.integracion.sti.mapping;
/**
 * Represent the parameters of enter for the implementation of field value Decimal or Integer
 * 
 * @author Yeiner Palomeque Moreno
 * @version 1.0
 * @since 2017-10-09
 */
public class NumberSetting {
	private int numCifraDecimal;
	private int positionInitialSing;
	private int positionFinalSin;
	
	public NumberSetting() {
		super();
	}

	public NumberSetting(int numCifraDecimal,
			int positionInitialSing, int positionFinalSin) {
		super();
		this.numCifraDecimal = numCifraDecimal;
		this.positionInitialSing = positionInitialSing;
		this.positionFinalSin = positionFinalSin;
	}
	
	public int getNumCifraDecimal() {
		return numCifraDecimal;
	}
	public void setNumCifraDecimal(int numCifraDecimal) {
		this.numCifraDecimal = numCifraDecimal;
	}
	public int getPositionInitialSing() {
		return positionInitialSing;
	}
	public void setPositionInitialSing(int positionInitialSing) {
		this.positionInitialSing = positionInitialSing;
	}
	public int getPositionFinalSin() {
		return positionFinalSin;
	}
	public void setPositionFinalSin(int positionFinalSin) {
		this.positionFinalSin = positionFinalSin;
	}
	
	
	
}
