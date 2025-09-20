package core.integrationcontrollers.servicegateway.sg.dto;

import java.io.Serializable;
import java.util.List;

/**
 * @author jrtorre
 * 
 */
public class SLD implements Serializable {

	private static final long serialVersionUID = 4482457502087032791L;
	private String name;
	private String namespace;
	private List<TypeImplementation> implementation;
	private List<SLA> sla;
	private List<Policy> policy;
	private SLDValues sldValue;
	private List<InternalRoute> internalRoutes;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public List<TypeImplementation> getImplementation() {
		return implementation;
	}

	public void setImplementation(List<TypeImplementation> implementation) {
		this.implementation = implementation;
	}

	public List<SLA> getSla() {
		return sla;
	}

	public void setSla(List<SLA> sla) {
		this.sla = sla;
	}

	public List<Policy> getSecurityPoliciy() {
		return policy;
	}

	public void setSecurityPoliciy(List<Policy> policy) {
		this.policy = policy;
	}

	public SLDValues getSldValue() {
		return sldValue;
	}

	public void setSldValue(SLDValues sldValue) {
		this.sldValue = sldValue;
	}

	public List<InternalRoute> getInternalRoutes() {
		return internalRoutes;
	}

	public void setInternalRoutes(List<InternalRoute> internalRoutes) {
		this.internalRoutes = internalRoutes;
	}

}
