package core.integrationcontrollers.servicegateway.sg.dto;

import java.io.Serializable;

public class InternalRoute implements Serializable {

	private static final long serialVersionUID = 1590756530650818063L;
	private String name;
	private String operationId;
	private String consumerId;
	private InternalComponentEndpoint internalComponentEndpoint;
	private InternalComponentEndpoint sourceComponent;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOperationId() {
		return operationId;
	}

	public void setOperationId(String operationId) {
		this.operationId = operationId;
	}

	public String getConsumerId() {
		return consumerId;
	}

	public void setConsumerId(String consumerId) {
		this.consumerId = consumerId;
	}

	public InternalComponentEndpoint getInternalComponentEndpoint() {
		return internalComponentEndpoint;
	}

	public void setInternalComponentEndpoint(InternalComponentEndpoint internalComponentEndpoint) {
		this.internalComponentEndpoint = internalComponentEndpoint;
	}

	public InternalComponentEndpoint getSourceComponent() {
		return sourceComponent;
	}

	public void setSourceComponent(InternalComponentEndpoint sourceComponent) {
		this.sourceComponent = sourceComponent;
	}
}
