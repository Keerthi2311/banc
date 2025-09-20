package core.integrationcontrollers.servicegateway.sg.dto;

import java.util.Map;

public class Policy {

	private String name;
	private Map<String, String> securityPolicies;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, String> getSecurityPolicies() {
		return securityPolicies;
	}

	public void setSecurityPolicies(Map<String, String> securityPolicies) {
		this.securityPolicies = securityPolicies;
	}
}
