package core.integrationcontrollers.servicegateway.sg.dto;

import java.io.Serializable;
import java.util.List;

public class Authorization implements Serializable {

	private static final long serialVersionUID = -6488399427951750683L;
	private int priority;
	private Double averageMessagesPerSecond;
	private Double averageResponseTimeMillis;
	private Boolean active;
	private List<String> operations;

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public Double getAverageMessagesPerSecond() {
		return averageMessagesPerSecond;
	}

	public void setAverageMessagesPerSecond(Double averageMessagesPerSecond) {
		this.averageMessagesPerSecond = averageMessagesPerSecond;
	}

	public Double getAverageResponseTimeMillis() {
		return averageResponseTimeMillis;
	}

	public void setAverageResponseTimeMillis(Double averageResponseTimeMillis) {
		this.averageResponseTimeMillis = averageResponseTimeMillis;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public List<String> getOperations() {
		return operations;
	}

	public void setOperations(List<String> operations) {
		this.operations = operations;
	}

}
