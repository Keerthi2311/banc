package core.integrationcontrollers.servicegateway.sg.dto;

import java.io.Serializable;

public class InternalComponentEndpoint implements Serializable {

	private static final long serialVersionUID = 2597142859142774154L;
	private String name;
	private String componentType;
	private String endPointType;
	private String host;
	private String requestQName;
	private String requestQMgr;
	private String responseQName;
	private String responseQMgr;
	private String mqVersion;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getComponentType() {
		return componentType;
	}

	public void setComponentType(String componentType) {
		this.componentType = componentType;
	}

	public String getEndPointType() {
		return endPointType;
	}

	public void setEndPointType(String endPointType) {
		this.endPointType = endPointType;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getRequestQName() {
		return requestQName;
	}

	public void setRequestQName(String requestQName) {
		this.requestQName = requestQName;
	}

	public String getRequestQMgr() {
		return requestQMgr;
	}

	public void setRequestQMgr(String requestQMgr) {
		this.requestQMgr = requestQMgr;
	}

	public String getResponseQName() {
		return responseQName;
	}

	public void setResponseQName(String responseQName) {
		this.responseQName = responseQName;
	}

	public String getResponseQMgr() {
		return responseQMgr;
	}

	public void setResponseQMgr(String responseQMgr) {
		this.responseQMgr = responseQMgr;
	}

	public String getMqVersion() {
		return mqVersion;
	}

	public void setMqVersion(String mqVersion) {
		this.mqVersion = mqVersion;
	}
}
