package com.grupobancolombia.integracion.sti.mapping.operation;

public class Policies {
	private static final String USER_DEFINED = "UserDefined";
	private static final String DEFAULT_POLICIES = "DefaultPolicies";

	private String PolicyType;
	private String PolicyProject;
	
	/**
	 * @return the getPolicyType
	 */
	public String getPolicyType() {
		return PolicyType;
	}
	/**
	 * @return the policyProject
	 */
	public String getPolicyProject() {
		return PolicyProject;
	}
	
	public Policies(){
		this.PolicyType = USER_DEFINED;
		this.PolicyProject = DEFAULT_POLICIES;
	}
	
	public Policies(String policyType, String policyProject){
		this.PolicyType = policyType;
		this.PolicyProject = policyProject;
	}	
	
}
