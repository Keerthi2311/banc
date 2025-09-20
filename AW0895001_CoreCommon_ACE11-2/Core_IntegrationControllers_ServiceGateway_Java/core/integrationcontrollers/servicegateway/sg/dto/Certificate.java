package core.integrationcontrollers.servicegateway.sg.dto;

import java.io.Serializable;
import java.util.Date;

public class Certificate implements Serializable {

	private static final long serialVersionUID = 1744555411137689632L;
	private String issuer;
	private String subject;
	private String serialNumber;
	private Date expiryDate;
	private String publicKey;
	private String publicKeyAlgorithm;
	private String url;
	private String certificateType;

	public String getIssuer() {
		return issuer;
	}

	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	public String getPublicKeyAlgorithm() {
		return publicKeyAlgorithm;
	}

	public void setPublicKeyAlgorithm(String publicKeyAlgorithm) {
		this.publicKeyAlgorithm = publicKeyAlgorithm;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getCertificateType() {
		return certificateType;
	}

	public void setCertificateType(String certificateType) {
		this.certificateType = certificateType;
	}

}
