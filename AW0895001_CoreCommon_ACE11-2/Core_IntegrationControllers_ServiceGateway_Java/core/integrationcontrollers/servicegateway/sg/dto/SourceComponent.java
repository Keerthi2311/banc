package core.integrationcontrollers.servicegateway.sg.dto;

import java.io.Serializable;

public class SourceComponent implements Serializable {

	private static final long serialVersionUID = 2597142859142774154L;
	private String name;
	private String componentType;
	private String endPointType;
	private String url;

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

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
