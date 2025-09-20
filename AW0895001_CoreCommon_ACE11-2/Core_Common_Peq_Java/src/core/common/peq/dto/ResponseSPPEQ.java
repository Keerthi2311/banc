package core.common.peq.dto;

public class ResponseSPPEQ {
	private String results;
	/**
	 * @return the results
	 */
	public String getResults() {
		return results;
	}
	/**
	 * @param results the results to set
	 */
	public void setResults(String results) {
		this.results = results;
	}
	/**
	 * @return the errors
	 */
	public String getErrors() {
		return errors;
	}
	/**
	 * @param errors the errors to set
	 */
	public void setErrors(String errors) {
		this.errors = errors;
	}
	private String errors;
}
