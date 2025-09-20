package core.integrationcontrollers.servicegateway.sg.dto;

import java.io.Serializable;
import java.util.List;

public class SLA implements Serializable {

	private static final long serialVersionUID = 6399679760492943916L;
	private String applicationId;
	private String sourceDataDomain;
	private String sourceSystem;
	private String environment;
	private List<ConsumerIdentifier> consumerIdentifier;
	private List<Certificate> certificate;
	private List<Authorization> authorization;

	private SLD sld;

	public String getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	public String getSourceDataDomain() {
		return sourceDataDomain;
	}

	public void setSourceDataDomain(String sourceDataDomain) {
		this.sourceDataDomain = sourceDataDomain;
	}

	public String getSourceSystem() {
		return sourceSystem;
	}

	public void setSourceSystem(String sourceSystem) {
		this.sourceSystem = sourceSystem;
	}

	public String getEnvironment() {
		return environment;
	}

	public void setEnvironment(String environment) {
		this.environment = environment;
	}

	public SLD getSld() {
		return sld;
	}

	public void setSld(SLD sld) {
		this.sld = sld;
	}

	public List<ConsumerIdentifier> getConsumerIdentifier() {
		return consumerIdentifier;
	}

	public void setConsumerIdentifier(List<ConsumerIdentifier> consumerIdentifier) {
		this.consumerIdentifier = consumerIdentifier;
	}

	public List<Certificate> getCertificate() {
		return certificate;
	}

	public void setCertificate(List<Certificate> certificate) {
		this.certificate = certificate;
	}

	public List<Authorization> getAuthorization() {
		return authorization;
	}

	public void setAuthorization(List<Authorization> authorization) {
		this.authorization = authorization;
	}

}
