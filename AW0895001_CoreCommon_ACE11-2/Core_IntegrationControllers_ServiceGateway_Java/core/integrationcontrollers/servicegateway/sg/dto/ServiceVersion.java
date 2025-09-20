package core.integrationcontrollers.servicegateway.sg.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ServiceVersion implements Serializable {

	private static final long serialVersionUID = -2264088801182597526L;
	private String name;
	private String version;
	private List<SLD> sld;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public List<SLD> getSld() {

		if (sld != null) {
			return sld;
		} else {
			sld = new ArrayList<>();
			return sld;
		}
	}

	public void setSld(List<SLD> sld) {
		this.sld = sld;
	}
}
