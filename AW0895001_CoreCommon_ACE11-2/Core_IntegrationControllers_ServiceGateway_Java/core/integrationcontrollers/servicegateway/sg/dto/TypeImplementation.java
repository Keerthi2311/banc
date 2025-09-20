package core.integrationcontrollers.servicegateway.sg.dto;

import java.io.Serializable;

public class TypeImplementation implements Serializable {

	private static final long serialVersionUID = 1016043238852270781L;

	private String implementation;

	public String getImplementation() {
		return implementation;
	}

	public void setImplementation(String implementation) {
		this.implementation = implementation;
	}
}
