package core.integrationcontrollers.servicegateway.sg.dto;

import java.io.Serializable;

public class SecurityPolicy implements Serializable {

	private static final long serialVersionUID = -5163352790354191858L;
	private String name;
	private Boolean active;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}
}
