package core.integrationcontrollers.servicegateway.sg.dto;

import java.io.Serializable;

public class SLDValues implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8667612969668829000L;
	/**
	 * 
	 */

	private String startDayAvailability;
	private String endDayAvailability;
	private String startHourAvailability;
	private String endHourAvailability;
	private Double averageResponseTime;
	private Double averageMessages;

	public String getStartDayAvailability() {
		return startDayAvailability;
	}

	public void setStartDayAvailability(String startDayAvailability) {
		this.startDayAvailability = startDayAvailability;
	}

	public String getEndDayAvailability() {
		return endDayAvailability;
	}

	public void setEndDayAvailability(String endDayAvailability) {
		this.endDayAvailability = endDayAvailability;
	}

	public String getStartHourAvailability() {
		return startHourAvailability;
	}

	public void setStartHourAvailability(String startHourAvailability) {
		this.startHourAvailability = startHourAvailability;
	}

	public String getEndHourAvailability() {
		return endHourAvailability;
	}

	public void setEndHourAvailability(String endHourAvailability) {
		this.endHourAvailability = endHourAvailability;
	}

	public Double getAverageResponseTime() {
		return averageResponseTime;
	}

	public void setAverageResponseTime(Double averageResponseTime) {
		this.averageResponseTime = averageResponseTime;
	}

	public Double getAverageMessages() {
		return averageMessages;
	}

	public void setAverageMessages(Double averageMessages) {
		this.averageMessages = averageMessages;
	}

}
