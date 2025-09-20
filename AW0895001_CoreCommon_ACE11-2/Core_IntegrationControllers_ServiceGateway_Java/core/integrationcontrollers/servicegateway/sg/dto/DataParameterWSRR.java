package core.integrationcontrollers.servicegateway.sg.dto;

public class DataParameterWSRR {

	private boolean consumerAuthorized;
	private boolean operationAuthorized;
	private boolean autorizationConsumerTLS;
	private boolean hasTLSPolicy;
	private String qmanager;
	private String queueRequest;
	private String queueResponse;
	private String qmanagerVersion;
	private String dataDomainOrigin;
	private String systemOrigin;
	private String queueCMSTI;

	public boolean isAutorizationConsumerTLS() {
		return autorizationConsumerTLS;
	}

	public void setAutorizationConsumerTLS(boolean autorizationConsumerTLS) {
		this.autorizationConsumerTLS = autorizationConsumerTLS;
	}

	public boolean isOperationAuthorized() {
		return operationAuthorized;
	}

	public void setOperationAuthorized(boolean operationAuthorized) {
		this.operationAuthorized = operationAuthorized;
	}

	public String getQueueCMSTI() {
		return queueCMSTI;
	}

	public void setQueueCMSTI(String queueCMSTI) {
		this.queueCMSTI = queueCMSTI;
	}

	public boolean isConsumerAuthorized() {
		return consumerAuthorized;
	}

	public void setConsumerAuthorization(boolean autorization) {
		this.consumerAuthorized = autorization;
	}

	public String getQmanager() {
		return qmanager;
	}

	public void setQmanager(String qmanager) {
		this.qmanager = qmanager;
	}

	public String getQueueRequest() {
		return queueRequest;
	}

	public void setQueueRequest(String queueRequest) {
		this.queueRequest = queueRequest;
	}

	public String getQueueResponse() {
		return queueResponse;
	}

	public void setQueueResponse(String queueResponse) {
		this.queueResponse = queueResponse;
	}

	public String getQmanagerVersion() {
		return qmanagerVersion;
	}

	public void setQmanagerVersion(String qmanagerVersion) {
		this.qmanagerVersion = qmanagerVersion;
	}

	public String getDataDomainOrigin() {
		return dataDomainOrigin;
	}

	public void setDataDomainOrigin(String dataDomainOrigin) {
		this.dataDomainOrigin = dataDomainOrigin;
	}

	public String getSystemOrigin() {
		return systemOrigin;
	}

	public void setSystemOrigin(String systemOrigin) {
		this.systemOrigin = systemOrigin;
	}

	public boolean isHasTLSPolicy() {
		return hasTLSPolicy;
	}

	public void setHasTLSPolicy(boolean hasTLSPolicy) {
		this.hasTLSPolicy = hasTLSPolicy;
	}
}
